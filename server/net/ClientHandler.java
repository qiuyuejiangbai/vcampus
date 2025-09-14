package server.net;

import common.protocol.Message;
import common.protocol.MessageType;
import common.protocol.StatusCode;
import common.vo.*;
import server.service.UserService;
import server.service.CourseService;
import server.service.EnrollmentService;
import server.service.GradeService;
import server.dao.TeacherDAO;
import server.dao.impl.TeacherDAOImpl;

import java.util.List;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * 客户端处理器
 * 每个连接的客户端对应一个ClientHandler实例
 * 负责处理客户端请求并返回响应
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final VCampusServer server;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private Integer currentUserId; // 当前登录用户ID
    private UserVO currentUser;    // 当前登录用户信息
    private boolean isConnected = true;
    
    // 业务服务
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final TeacherDAO teacherDAO;
    
    public ClientHandler(Socket clientSocket, VCampusServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.userService = new UserService();
        this.courseService = new CourseService();
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        this.teacherDAO = new TeacherDAOImpl();
        
        try {
            // 创建输入输出流
            this.objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectIn = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("创建客户端流失败: " + e.getMessage());
            disconnect();
        }
    }
    
    @Override
    public void run() {
        String clientInfo = clientSocket.getRemoteSocketAddress().toString();
        System.out.println("客户端处理线程启动: " + clientInfo);
        
        try {
            // 主循环：处理客户端消息
            while (isConnected && !clientSocket.isClosed()) {
                try {
                    Object receivedObject = objectIn.readObject();
                    
                    if (receivedObject instanceof Message) {
                        Message request = (Message) receivedObject;
                        handleMessage(request);
                    } else {
                        System.err.println("收到无效消息类型: " + receivedObject.getClass().getName());
                        sendErrorMessage("无效的消息格式");
                    }
                    
                } catch (SocketException e) {
                    // 客户端正常断开连接
                    System.out.println("客户端断开连接: " + clientInfo);
                    break;
                } catch (EOFException e) {
                    // 客户端关闭连接
                    System.out.println("客户端关闭连接: " + clientInfo);
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("反序列化消息失败: " + e.getMessage());
                    sendErrorMessage("消息格式错误");
                } catch (IOException e) {
                    System.err.println("读取客户端消息失败: " + e.getMessage());
                    break;
                }
            }
            
        } finally {
            disconnect();
        }
    }
    
    /**
     * 处理客户端消息
     * @param request 请求消息
     */
    private void handleMessage(Message request) {
        if (request == null || request.getType() == null) {
            sendErrorMessage("无效的请求");
            return;
        }
        
        System.out.println("处理消息: " + request.getType() + " from " + 
                          (currentUser != null ? currentUser.getLoginId() : "未登录用户"));
        
        try {
            switch (request.getType()) {
                case LOGIN_REQUEST:
                    handleLogin(request);
                    break;
                    
                case REGISTER_REQUEST:
                    handleRegister(request);
                    break;
                    
                case LOGOUT_REQUEST:
                    handleLogout(request);
                    break;
                    
                case GET_USER_INFO_REQUEST:
                    handleGetUserInfo(request);
                    break;
                    
                case UPDATE_USER_REQUEST:
                    handleUpdateUser(request);
                    break;
                    
                case GET_ALL_USERS_REQUEST:
                    handleGetAllUsers(request);
                    break;
                    
                case GET_STUDENT_INFO_REQUEST:
                    handleGetStudentInfo(request);
                    break;
                    
                case GET_ALL_COURSES_REQUEST:
                    handleGetAllCourses(request);
                    break;
                    
                case GET_MY_COURSES_REQUEST:
                    handleGetMyCourses(request);
                    break;
                    
                case GET_TEACHER_COURSES_REQUEST:
                    handleGetTeacherCourses(request);
                    break;
                    
                case GET_COURSE_STUDENTS_REQUEST:
                    handleGetCourseStudents(request);
                    break;
                    
                case GET_COURSE_GRADES_REQUEST:
                    handleGetCourseGrades(request);
                    break;
                    
                case SAVE_GRADE_REQUEST:
                    handleSaveGrade(request);
                    break;
                    
                case ADD_COURSE_REQUEST:
                    handleAddCourse(request);
                    break;
                    
                case UPDATE_COURSE_REQUEST:
                    handleUpdateCourse(request);
                    break;
                    
                case DELETE_COURSE_REQUEST:
                    handleDeleteCourse(request);
                    break;
                    
                case SEARCH_COURSES_REQUEST:
                    handleSearchCourses(request);
                    break;
                    
                case GET_ALL_TEACHERS_REQUEST:
                    handleGetAllTeachers(request);
                    break;
                    
                case GET_COURSE_BY_CODE_REQUEST:
                    handleGetCourseByCode(request);
                    break;
                    
                case ENROLL_COURSE_REQUEST:
                    handleEnrollCourse(request);
                    break;
                    
                case DROP_COURSE_REQUEST:
                    handleDropCourse(request);
                    break;
                    
                case HEARTBEAT:
                    handleHeartbeat(request);
                    break;
                    
                default:
                    handleUnsupportedRequest(request);
                    break;
            }
        } catch (Exception e) {
            System.err.println("处理消息异常: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 处理登录请求
     */
    private void handleLogin(Message request) {
        System.out.println("=== 处理登录请求 ===");
        if (request.getData() instanceof UserVO) {
            UserVO loginUser = (UserVO) request.getData();
            System.out.println("收到登录请求 - ID: " + loginUser.getId());
            
            UserVO user = userService.login(loginUser.getId(), loginUser.getPassword());
            if (user != null) {
                // 登录成功
                this.currentUserId = user.getUserId(); // 使用数据库的user_id
                this.currentUser = user;
                System.out.println("登录成功，保存会话：currentUserId=" + this.currentUserId + ", loginId=" + this.currentUser.getLoginId() + ", role=" + this.currentUser.getRoleName());
                
                // 添加到在线用户列表
                server.addOnlineUser(currentUserId, this);
                
                // 清除密码信息（安全考虑）
                user.setPassword(null);
                
                Message response = new Message(MessageType.LOGIN_SUCCESS, StatusCode.SUCCESS, user, "登录成功");
                sendMessage(response);
                
                System.out.println("用户登录成功: " + user.getId() + " (" + user.getRoleName() + ")");
            } else {
                // 登录失败
                System.out.println("登录失败，发送失败响应");
                Message response = new Message(MessageType.LOGIN_FAIL, StatusCode.INVALID_PASSWORD, null, "用户名或密码错误");
                sendMessage(response);
            }
        } else {
            System.out.println("登录数据格式错误");
            sendErrorMessage("登录数据格式错误");
        }
        System.out.println("=== 登录请求处理完成 ===");
    }
    
    /**
     * 处理注册请求
     */
    private void handleRegister(Message request) {
        try {
            if (request.getData() instanceof UserVO) {
                // 简单注册（只有基础用户信息）
                UserVO newUser = (UserVO) request.getData();
                
                Integer userId = userService.register(newUser);
                if (userId != null) {
                    // 注册成功，账户自动激活
                    Message response = new Message(MessageType.REGISTER_SUCCESS, StatusCode.CREATED, userId, "注册成功，账户已激活");
                    sendMessage(response);
                    
                    System.out.println("新用户注册: " + newUser.getId());
                } else {
                    // 注册失败
                    String errorMsg = "注册失败";
                    if (userService.loginIdExists(newUser.getId())) {
                        errorMsg = "登录ID已存在";
                    }
                    Message response = new Message(MessageType.REGISTER_FAIL, StatusCode.USER_EXISTS, null, errorMsg);
                    sendMessage(response);
                }
            } else if (request.getData() instanceof java.util.Map) {
                // 详细注册（包含学生/教师信息）
                handleDetailedRegister(request);
            } else {
                sendErrorMessage("注册数据格式错误");
            }
        } catch (Exception e) {
            System.err.println("处理注册请求失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("注册处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理详细注册请求
     */
    private void handleDetailedRegister(Message request) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> details = (java.util.Map<String, Object>) request.getData();
        
        UserVO user = (UserVO) details.get("user");
        String name = (String) details.get("name");
        String phone = (String) details.get("phone");
        String email = (String) details.get("email");
        String department = (String) details.get("department");
        String major = (String) details.get("major");
        String title = (String) details.get("title");
        
        // 创建学生或教师信息对象
        StudentVO studentInfo = null;
        TeacherVO teacherInfo = null;
        
        if (user.isStudent()) {
            studentInfo = new StudentVO();
            studentInfo.setName(name);
            studentInfo.setPhone(phone);
            studentInfo.setEmail(email);
            studentInfo.setDepartment(department);
            studentInfo.setMajor(major);
            // 其他字段为空，账户余额默认为0在数据库中设置
        } else if (user.isTeacher()) {
            teacherInfo = new TeacherVO();
            teacherInfo.setName(name);
            teacherInfo.setPhone(phone);
            teacherInfo.setEmail(email);
            teacherInfo.setDepartment(department);
            teacherInfo.setTitle(title);
        }
        
        // 执行注册
        Integer userId = userService.register(user, studentInfo, teacherInfo);
        if (userId != null) {
            // 注册成功，账户自动激活
            Message response = new Message(MessageType.REGISTER_SUCCESS, StatusCode.CREATED, userId, "注册成功，账户已激活");
            sendMessage(response);
            
            System.out.println("新用户详细注册: " + user.getId() + " (" + user.getRoleName() + ")");
        } else {
            // 注册失败
            String errorMsg = "注册失败";
            if (userService.loginIdExists(user.getId())) {
                errorMsg = "登录ID已存在";
            }
            Message response = new Message(MessageType.REGISTER_FAIL, StatusCode.USER_EXISTS, null, errorMsg);
            sendMessage(response);
        }
    }
    
    /**
     * 处理登出请求
     */
    private void handleLogout(Message request) {
        if (currentUserId != null) {
            server.removeOnlineUser(currentUserId);
            System.out.println("用户登出: " + (currentUser != null ? currentUser.getId() : currentUserId));
        }
        
        currentUserId = null;
        currentUser = null;
        
        Message response = new Message(MessageType.LOGOUT_SUCCESS, StatusCode.SUCCESS, null, "登出成功");
        sendMessage(response);
    }
    
    /**
     * 处理获取用户信息请求
     */
    private void handleGetUserInfo(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        UserVO user = userService.getUserById(currentUserId);
        if (user != null) {
            // 清除密码信息
            user.setPassword(null);
            Message response = new Message(MessageType.GET_USER_INFO_SUCCESS, StatusCode.SUCCESS, user);
            sendMessage(response);
        } else {
            sendErrorMessage("获取用户信息失败");
        }
    }
    
    /**
     * 处理获取学生信息请求
     */
    private void handleGetStudentInfo(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为学生
        if (!currentUser.isStudent()) {
            sendErrorMessage("只有学生用户才能获取学生信息");
            return;
        }
        
        // 获取学生详细信息
        System.out.println("准备查询学生信息，userId=" + currentUserId + ", loginId=" + currentUser.getLoginId());
        server.service.StudentService studentService = new server.service.StudentService();
        StudentVO student = studentService.getStudentByUserId(currentUserId);
        
        if (student != null) {
            // 设置用户信息
            student.setUserInfo(currentUser);
            System.out.println("学生信息查询成功：姓名=" + student.getName() + ", 专业=" + student.getMajor());
            Message response = new Message(MessageType.GET_STUDENT_INFO_SUCCESS, StatusCode.SUCCESS, student, "获取学生信息成功");
            sendMessage(response);
        } else {
            System.out.println("学生信息查询结果为空，userId=" + currentUserId);
            Message response = new Message(MessageType.GET_STUDENT_INFO_SUCCESS, StatusCode.NOT_FOUND, null, "学生信息不存在");
            sendMessage(response);
        }
    }
    
    /**
     * 处理更新用户信息请求
     */
    private void handleUpdateUser(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        if (request.getData() instanceof UserVO) {
            UserVO updateUser = (UserVO) request.getData();
            
            // 安全检查：只能更新自己的信息（除非是管理员）
            if (!currentUser.isAdmin() && !currentUserId.equals(updateUser.getUserId())) {
                Message response = new Message(MessageType.UPDATE_USER_FAIL, StatusCode.FORBIDDEN, null, "无权限修改他人信息");
                sendMessage(response);
                return;
            }
            
            // 如果没有设置userId，则设置为当前用户的ID
            if (updateUser.getUserId() == null) {
                updateUser.setUserId(currentUserId);
            }
            
            boolean success = userService.updateUser(updateUser);
            if (success) {
                // 如果更新的是当前用户，刷新当前用户信息
                if (currentUserId.equals(updateUser.getUserId())) {
                    currentUser = userService.getUserById(currentUserId);
                }
                
                Message response = new Message(MessageType.UPDATE_USER_SUCCESS, StatusCode.SUCCESS, null, "更新成功");
                sendMessage(response);
            } else {
                Message response = new Message(MessageType.UPDATE_USER_FAIL, StatusCode.INTERNAL_ERROR, null, "更新失败");
                sendMessage(response);
            }
        } else {
            sendErrorMessage("更新数据格式错误");
        }
    }
    
    /**
     * 处理获取所有用户请求（管理员功能）
     */
    private void handleGetAllUsers(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查管理员权限
        if (!currentUser.isAdmin()) {
            Message response = new Message(MessageType.GET_ALL_USERS_SUCCESS, StatusCode.FORBIDDEN, null, "需要管理员权限");
            sendMessage(response);
            return;
        }
        
        List<UserVO> users = userService.getAllUsers();
        // 清除所有用户的密码信息
        users.forEach(user -> user.setPassword(null));
        
        Message response = new Message(MessageType.GET_ALL_USERS_SUCCESS, StatusCode.SUCCESS, users);
        sendMessage(response);
    }
    
    /**
     * 处理获取所有课程请求
     */
    private void handleGetAllCourses(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        try {
            List<CourseVO> courses = courseService.getAllCourses();
            Message response = new Message(MessageType.GET_ALL_COURSES_SUCCESS, StatusCode.SUCCESS, courses, "获取课程列表成功");
            sendMessage(response);
            System.out.println("返回课程列表，共 " + (courses != null ? courses.size() : 0) + " 门课程");
        } catch (Exception e) {
            System.err.println("获取课程列表失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取课程列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取我的课程请求
     */
    private void handleGetMyCourses(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        try {
            // 获取学生信息以获取studentId
            server.service.StudentService studentService = new server.service.StudentService();
            common.vo.StudentVO student = studentService.getStudentByUserId(currentUserId);
            if (student == null) {
                sendErrorMessage("学生信息不存在");
                return;
            }
            
            List<EnrollmentVO> enrollments = enrollmentService.getStudentEnrollments(student.getStudentId());
            Message response = new Message(MessageType.GET_MY_COURSES_SUCCESS, StatusCode.SUCCESS, enrollments, "获取我的课程成功");
            sendMessage(response);
            System.out.println("返回我的课程列表，共 " + (enrollments != null ? enrollments.size() : 0) + " 门课程");
        } catch (Exception e) {
            System.err.println("获取我的课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取我的课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取教师课程请求
     */
    private void handleGetTeacherCourses(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为教师
        if (!currentUser.isTeacher()) {
            sendErrorMessage("只有教师用户才能获取教师课程");
            return;
        }
        
        try {
            // 获取教师信息以获取teacherId
            TeacherVO teacher = teacherDAO.findByUserId(currentUserId);
            if (teacher == null) {
                sendErrorMessage("教师信息不存在");
                return;
            }
            
            List<CourseVO> courses = courseService.getCoursesByTeacher(teacher.getId());
            Message response = new Message(MessageType.GET_TEACHER_COURSES_SUCCESS, StatusCode.SUCCESS, courses, "获取教师课程成功");
            sendMessage(response);
            System.out.println("返回教师课程列表，共 " + (courses != null ? courses.size() : 0) + " 门课程");
        } catch (Exception e) {
            System.err.println("获取教师课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取教师课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取课程学生名单请求
     */
    private void handleGetCourseStudents(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为教师
        if (!currentUser.isTeacher()) {
            sendErrorMessage("只有教师用户才能获取课程学生名单");
            return;
        }
        
        try {
            String courseCode = (String) request.getData();
            if (courseCode == null || courseCode.trim().isEmpty()) {
                sendErrorMessage("课程代码不能为空");
                return;
            }
            
            List<EnrollmentVO> students = enrollmentService.getCourseStudents(courseCode);
            Message response = new Message(MessageType.GET_COURSE_STUDENTS_SUCCESS, StatusCode.SUCCESS, students, "获取课程学生名单成功");
            sendMessage(response);
            System.out.println("返回课程 " + courseCode + " 的学生名单，共 " + (students != null ? students.size() : 0) + " 名学生");
        } catch (Exception e) {
            System.err.println("获取课程学生名单失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取课程学生名单失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取课程成绩请求
     */
    private void handleGetCourseGrades(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为教师
        if (!currentUser.isTeacher()) {
            sendErrorMessage("只有教师用户才能获取课程成绩");
            return;
        }
        
        try {
            String courseCode = (String) request.getData();
            if (courseCode == null || courseCode.trim().isEmpty()) {
                sendErrorMessage("课程代码不能为空");
                return;
            }
            
            List<GradeVO> grades = gradeService.getCourseGrades(courseCode);
            Message response = new Message(MessageType.GET_COURSE_GRADES_SUCCESS, StatusCode.SUCCESS, grades, "获取课程成绩成功");
            sendMessage(response);
            System.out.println("返回课程 " + courseCode + " 的成绩列表，共 " + (grades != null ? grades.size() : 0) + " 条记录");
        } catch (Exception e) {
            System.err.println("获取课程成绩失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取课程成绩失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理保存成绩请求
     */
    private void handleSaveGrade(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为教师
        if (!currentUser.isTeacher()) {
            sendErrorMessage("只有教师用户才能保存成绩");
            return;
        }
        
        try {
            GradeVO grade = (GradeVO) request.getData();
            if (grade == null) {
                sendErrorMessage("成绩信息不能为空");
                return;
            }
            
            boolean success = gradeService.saveGrade(grade);
            if (success) {
                Message response = new Message(MessageType.SAVE_GRADE_SUCCESS, StatusCode.SUCCESS, null, "保存成绩成功");
                sendMessage(response);
                System.out.println("成功保存成绩: " + grade.getStudentNo() + " - " + grade.getCourseCode());
            } else {
                sendErrorMessage("保存成绩失败");
            }
        } catch (Exception e) {
            System.err.println("保存成绩失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("保存成绩失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理添加课程请求
     */
    private void handleAddCourse(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为管理员
        if (!currentUser.isAdmin()) {
            sendErrorMessage("只有管理员用户才能添加课程");
            return;
        }
        
        try {
            CourseVO course = (CourseVO) request.getData();
            if (course == null) {
                sendErrorMessage("课程信息不能为空");
                return;
            }
            
            Integer courseId = courseService.addCourse(course);
            if (courseId != null) {
                course.setCourseId(courseId);
                Message response = new Message(MessageType.ADD_COURSE_SUCCESS, StatusCode.SUCCESS, course, "添加课程成功");
                sendMessage(response);
                System.out.println("成功添加课程: " + course.getCourseCode() + " - " + course.getCourseName());
            } else {
                sendErrorMessage("添加课程失败");
            }
        } catch (Exception e) {
            System.err.println("添加课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("添加课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理更新课程请求
     */
    private void handleUpdateCourse(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为管理员
        if (!currentUser.isAdmin()) {
            sendErrorMessage("只有管理员用户才能更新课程");
            return;
        }
        
        try {
            CourseVO course = (CourseVO) request.getData();
            if (course == null || course.getCourseId() == null) {
                sendErrorMessage("课程信息不能为空");
                return;
            }
            
            boolean success = courseService.updateCourse(course);
            if (success) {
                Message response = new Message(MessageType.UPDATE_COURSE_SUCCESS, StatusCode.SUCCESS, course, "更新课程成功");
                sendMessage(response);
                System.out.println("成功更新课程: " + course.getCourseCode() + " - " + course.getCourseName());
            } else {
                sendErrorMessage("更新课程失败");
            }
        } catch (Exception e) {
            System.err.println("更新课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("更新课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理删除课程请求
     */
    private void handleDeleteCourse(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为管理员
        if (!currentUser.isAdmin()) {
            sendErrorMessage("只有管理员用户才能删除课程");
            return;
        }
        
        try {
            Object data = request.getData();
            boolean success = false;
            String courseInfo = "";
            
            if (data instanceof Integer) {
                // 通过课程ID删除
                Integer courseId = (Integer) data;
                success = courseService.deleteCourse(courseId);
                courseInfo = "课程ID: " + courseId;
            } else if (data instanceof String) {
                // 通过课程代码删除
                String courseCode = (String) data;
                success = courseService.deleteCourseByCode(courseCode);
                courseInfo = "课程代码: " + courseCode;
            } else {
                sendErrorMessage("无效的删除参数");
                return;
            }
            
            if (success) {
                Message response = new Message(MessageType.DELETE_COURSE_SUCCESS, StatusCode.SUCCESS, null, "删除课程成功");
                sendMessage(response);
                System.out.println("成功删除课程 " + courseInfo);
            } else {
                sendErrorMessage("删除课程失败");
            }
        } catch (Exception e) {
            System.err.println("删除课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("删除课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理搜索课程请求
     */
    private void handleSearchCourses(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        try {
            @SuppressWarnings("unchecked")
            List<String> searchParams = (List<String>) request.getData();
            if (searchParams == null || searchParams.size() < 3) {
                sendErrorMessage("搜索参数不完整");
                return;
            }
            
            String keyword = searchParams.get(0);
            String department = searchParams.get(1);
            String semester = searchParams.get(2);
            
            List<CourseVO> courses = courseService.searchCourses(keyword, department, semester);
            Message response = new Message(MessageType.SEARCH_COURSES_SUCCESS, StatusCode.SUCCESS, courses, "搜索课程成功");
            sendMessage(response);
            System.out.println("搜索课程成功，返回 " + (courses != null ? courses.size() : 0) + " 门课程");
        } catch (Exception e) {
            System.err.println("搜索课程失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("搜索课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取所有教师请求
     */
    private void handleGetAllTeachers(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        try {
            List<TeacherVO> teachers = teacherDAO.findAllWithUserInfo();
            Message response = new Message(MessageType.GET_ALL_TEACHERS_SUCCESS, StatusCode.SUCCESS, teachers, "获取教师列表成功");
            sendMessage(response);
            System.out.println("返回教师列表，共 " + (teachers != null ? teachers.size() : 0) + " 名教师");
        } catch (Exception e) {
            System.err.println("获取教师列表失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取教师列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理通过课程代码获取课程请求
     */
    private void handleGetCourseByCode(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        try {
            String courseCode = (String) request.getData();
            if (courseCode == null || courseCode.trim().isEmpty()) {
                sendErrorMessage("课程代码不能为空");
                return;
            }
            
            CourseVO course = courseService.getCourseByCode(courseCode);
            if (course != null) {
                Message response = new Message(MessageType.GET_COURSE_BY_CODE_SUCCESS, StatusCode.SUCCESS, course, "获取课程信息成功");
                sendMessage(response);
                System.out.println("成功获取课程信息: " + courseCode);
            } else {
                sendErrorMessage("课程不存在");
            }
        } catch (Exception e) {
            System.err.println("获取课程信息失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("获取课程信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理选课请求
     */
    private void handleEnrollCourse(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为学生
        if (!currentUser.isStudent()) {
            sendErrorMessage("只有学生用户才能选课");
            return;
        }
        
        try {
            String courseCode = (String) request.getData();
            if (courseCode == null || courseCode.trim().isEmpty()) {
                sendErrorMessage("课程代码不能为空");
                return;
            }
            
            // 获取学生信息以获取studentId
            server.service.StudentService studentService = new server.service.StudentService();
            common.vo.StudentVO student = studentService.getStudentByUserId(currentUserId);
            if (student == null) {
                sendErrorMessage("学生信息不存在");
                return;
            }
            
            boolean success = enrollmentService.enrollCourse(student.getStudentId(), courseCode);
            if (success) {
                Message response = new Message(MessageType.ENROLL_COURSE_SUCCESS, StatusCode.SUCCESS, null, "选课成功");
                sendMessage(response);
                System.out.println("学生 " + currentUser.getLoginId() + " (studentId=" + student.getStudentId() + ") 选课成功: " + courseCode);
            } else {
                Message response = new Message(MessageType.ENROLL_COURSE_FAIL, StatusCode.BAD_REQUEST, null, "选课失败，可能已经选过该课程或课程已满");
                sendMessage(response);
                System.out.println("学生 " + currentUser.getLoginId() + " (studentId=" + student.getStudentId() + ") 选课失败: " + courseCode);
            }
        } catch (Exception e) {
            System.err.println("处理选课请求失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("选课处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理退课请求
     */
    private void handleDropCourse(Message request) {
        if (!isLoggedIn()) {
            sendUnauthorizedMessage();
            return;
        }
        
        // 检查当前用户是否为学生
        if (!currentUser.isStudent()) {
            sendErrorMessage("只有学生用户才能退课");
            return;
        }
        
        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> data = (java.util.Map<String, String>) request.getData();
            String courseCode = data.get("courseCode");
            String reason = data.get("reason");
            
            if (courseCode == null || courseCode.trim().isEmpty()) {
                sendErrorMessage("课程代码不能为空");
                return;
            }
            
            // 获取学生信息以获取studentId
            server.service.StudentService studentService = new server.service.StudentService();
            common.vo.StudentVO student = studentService.getStudentByUserId(currentUserId);
            if (student == null) {
                sendErrorMessage("学生信息不存在");
                return;
            }
            
            boolean success = enrollmentService.dropCourse(student.getStudentId(), courseCode, reason);
            if (success) {
                Message response = new Message(MessageType.DROP_COURSE_SUCCESS, StatusCode.SUCCESS, null, "退课成功");
                sendMessage(response);
                System.out.println("学生 " + currentUser.getLoginId() + " (studentId=" + student.getStudentId() + ") 退课成功: " + courseCode);
            } else {
                Message response = new Message(MessageType.DROP_COURSE_FAIL, StatusCode.BAD_REQUEST, null, "退课失败，请检查课程状态");
                sendMessage(response);
                System.out.println("学生 " + currentUser.getLoginId() + " (studentId=" + student.getStudentId() + ") 退课失败: " + courseCode);
            }
        } catch (Exception e) {
            System.err.println("处理退课请求失败: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage("退课处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理心跳请求
     */
    private void handleHeartbeat(Message request) {
        Message response = new Message(MessageType.HEARTBEAT, StatusCode.SUCCESS, System.currentTimeMillis());
        sendMessage(response);
    }
    
    /**
     * 处理不支持的请求
     */
    private void handleUnsupportedRequest(Message request) {
        System.err.println("不支持的请求类型: " + request.getType());
        Message response = new Message(MessageType.INVALID_REQUEST, StatusCode.BAD_REQUEST, null, "不支持的请求类型");
        sendMessage(response);
    }
    
    /**
     * 发送消息到客户端
     * @param message 消息对象
     */
    public void sendMessage(Object message) {
        if (!isConnected || objectOut == null) {
            return;
        }
        
        try {
            objectOut.writeObject(message);
            objectOut.flush();
        } catch (IOException e) {
            System.err.println("发送消息失败: " + e.getMessage());
            disconnect();
        }
    }
    
    /**
     * 发送错误消息
     * @param errorMsg 错误信息
     */
    private void sendErrorMessage(String errorMsg) {
        Message response = new Message(MessageType.ERROR, StatusCode.BAD_REQUEST, null, errorMsg);
        sendMessage(response);
    }
    
    /**
     * 发送未授权消息
     */
    private void sendUnauthorizedMessage() {
        Message response = new Message(MessageType.ERROR, StatusCode.UNAUTHORIZED, null, "请先登录");
        sendMessage(response);
    }
    
    /**
     * 检查用户是否已登录
     * @return 已登录返回true，未登录返回false
     */
    private boolean isLoggedIn() {
        return currentUserId != null && currentUser != null;
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        if (!isConnected) {
            return;
        }
        
        isConnected = false;
        
        // 从服务器移除
        server.removeClientHandler(clientSocket);
        
        // 关闭流
        try {
            if (objectIn != null) {
                objectIn.close();
            }
            if (objectOut != null) {
                objectOut.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回null
     */
    public Integer getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息，未登录返回null
     */
    public UserVO getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 检查连接是否活跃
     * @return 连接活跃返回true，已断开返回false
     */
    public boolean isConnected() {
        return isConnected && clientSocket != null && !clientSocket.isClosed();
    }
}
