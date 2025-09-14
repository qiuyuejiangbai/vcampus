package client.controller;

import client.net.ServerConnection;
import common.protocol.Message;
import common.protocol.MessageType;
import common.protocol.StatusCode;
import common.vo.CourseVO;
import common.vo.EnrollmentVO;
import common.vo.GradeVO;
import common.vo.TeacherVO;

import java.util.List;

/**
 * 课程控制器
 * 处理课程相关的网络请求
 */
public class CourseController {
    private final ServerConnection serverConnection;
    
    public CourseController() {
        this.serverConnection = ServerConnection.getInstance();
    }
    
    /**
     * 获取所有课程回调接口
     */
    public interface GetAllCoursesCallback {
        void onSuccess(List<CourseVO> courses);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取我的课程回调接口
     */
    public interface GetMyCoursesCallback {
        void onSuccess(List<EnrollmentVO> enrollments);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取教师课程回调接口
     */
    public interface GetTeacherCoursesCallback {
        void onSuccess(List<CourseVO> courses);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取课程学生名单回调接口
     */
    public interface GetCourseStudentsCallback {
        void onSuccess(List<EnrollmentVO> students);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取课程成绩回调接口
     */
    public interface GetCourseGradesCallback {
        void onSuccess(List<GradeVO> grades);
        void onFailure(String errorMessage);
    }
    
    /**
     * 保存成绩回调接口
     */
    public interface SaveGradeCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    
    /**
     * 添加课程回调接口
     */
    public interface AddCourseCallback {
        void onSuccess(CourseVO course);
        void onFailure(String errorMessage);
    }
    
    /**
     * 更新课程回调接口
     */
    public interface UpdateCourseCallback {
        void onSuccess(CourseVO course);
        void onFailure(String errorMessage);
    }
    
    /**
     * 删除课程回调接口
     */
    public interface DeleteCourseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    
    /**
     * 搜索课程回调接口
     */
    public interface SearchCoursesCallback {
        void onSuccess(List<CourseVO> courses);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取教师列表回调接口
     */
    public interface GetTeachersCallback {
        void onSuccess(List<TeacherVO> teachers);
        void onFailure(String errorMessage);
    }
    
    /**
     * 通过课程代码获取课程回调接口
     */
    public interface GetCourseByCodeCallback {
        void onSuccess(CourseVO course);
        void onFailure(String errorMessage);
    }
    
    /**
     * 选课回调接口
     */
    public interface EnrollCourseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    
    /**
     * 退课回调接口
     */
    public interface DropCourseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取所有课程
     * @param callback 回调接口
     */
    public void getAllCourses(GetAllCoursesCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_ALL_COURSES_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<CourseVO> courses = (List<CourseVO>) message.getData();
                callback.onSuccess(courses);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_ALL_COURSES_REQUEST);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 获取我的课程
     * @param callback 回调接口
     */
    public void getMyCourses(GetMyCoursesCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_MY_COURSES_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<EnrollmentVO> enrollments = (List<EnrollmentVO>) message.getData();
                callback.onSuccess(enrollments);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_MY_COURSES_REQUEST);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 获取教师课程
     * @param callback 回调接口
     */
    public void getTeacherCourses(GetTeacherCoursesCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_TEACHER_COURSES_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<CourseVO> courses = (List<CourseVO>) message.getData();
                callback.onSuccess(courses);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_TEACHER_COURSES_REQUEST);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 获取课程学生名单
     * @param courseCode 课程代码
     * @param callback 回调接口
     */
    public void getCourseStudents(String courseCode, GetCourseStudentsCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_COURSE_STUDENTS_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<EnrollmentVO> students = (List<EnrollmentVO>) message.getData();
                callback.onSuccess(students);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_COURSE_STUDENTS_REQUEST, StatusCode.SUCCESS, courseCode);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 获取课程成绩
     * @param courseCode 课程代码
     * @param callback 回调接口
     */
    public void getCourseGrades(String courseCode, GetCourseGradesCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_COURSE_GRADES_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<GradeVO> grades = (List<GradeVO>) message.getData();
                callback.onSuccess(grades);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_COURSE_GRADES_REQUEST, StatusCode.SUCCESS, courseCode);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 保存成绩
     * @param grade 成绩信息
     * @param callback 回调接口
     */
    public void saveGrade(GradeVO grade, SaveGradeCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.SAVE_GRADE_SUCCESS, message -> {
            if (callback != null) {
                callback.onSuccess();
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.SAVE_GRADE_REQUEST, StatusCode.SUCCESS, grade);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 添加课程
     * @param course 课程信息
     * @param callback 回调接口
     */
    public void addCourse(CourseVO course, AddCourseCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.ADD_COURSE_SUCCESS, message -> {
            if (callback != null) {
                CourseVO addedCourse = (CourseVO) message.getData();
                callback.onSuccess(addedCourse);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.ADD_COURSE_REQUEST, StatusCode.SUCCESS, course);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 更新课程
     * @param course 课程信息
     * @param callback 回调接口
     */
    public void updateCourse(CourseVO course, UpdateCourseCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.UPDATE_COURSE_SUCCESS, message -> {
            if (callback != null) {
                CourseVO updatedCourse = (CourseVO) message.getData();
                callback.onSuccess(updatedCourse);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.UPDATE_COURSE_REQUEST, StatusCode.SUCCESS, course);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 删除课程
     * @param courseId 课程ID
     * @param callback 回调接口
     */
    public void deleteCourse(Integer courseId, DeleteCourseCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.DELETE_COURSE_SUCCESS, message -> {
            if (callback != null) {
                callback.onSuccess();
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.DELETE_COURSE_REQUEST, StatusCode.SUCCESS, courseId);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 通过课程代码删除课程
     * @param courseCode 课程代码
     * @param callback 回调接口
     */
    public void deleteCourseByCode(String courseCode, DeleteCourseCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.DELETE_COURSE_SUCCESS, message -> {
            if (callback != null) {
                callback.onSuccess();
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.DELETE_COURSE_REQUEST, StatusCode.SUCCESS, courseCode);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 搜索课程
     * @param keyword 关键词
     * @param department 院系
     * @param semester 学期
     * @param callback 回调接口
     */
    public void searchCourses(String keyword, String department, String semester, SearchCoursesCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.SEARCH_COURSES_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<CourseVO> courses = (List<CourseVO>) message.getData();
                callback.onSuccess(courses);
            }
        });
        
        // 创建搜索参数列表
        java.util.List<String> searchParams = new java.util.ArrayList<>();
        searchParams.add(keyword != null ? keyword : "");
        searchParams.add(department != null ? department : "");
        searchParams.add(semester != null ? semester : "");
        
        // 创建请求消息
        Message request = new Message(MessageType.SEARCH_COURSES_REQUEST, StatusCode.SUCCESS, searchParams);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 获取所有教师列表
     * @param callback 回调接口
     */
    public void getAllTeachers(GetTeachersCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_ALL_TEACHERS_SUCCESS, message -> {
            if (callback != null) {
                @SuppressWarnings("unchecked")
                List<TeacherVO> teachers = (List<TeacherVO>) message.getData();
                callback.onSuccess(teachers);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_ALL_TEACHERS_REQUEST);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 通过课程代码获取课程
     * @param courseCode 课程代码
     * @param callback 回调接口
     */
    public void getCourseByCode(String courseCode, GetCourseByCodeCallback callback) {
        // 设置消息监听器
        serverConnection.setMessageListener(MessageType.GET_COURSE_BY_CODE_SUCCESS, message -> {
            if (callback != null) {
                CourseVO course = (CourseVO) message.getData();
                callback.onSuccess(course);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.GET_COURSE_BY_CODE_REQUEST, StatusCode.SUCCESS, courseCode);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 选课
     * @param courseCode 课程代码
     * @param callback 回调接口
     */
    public void enrollCourse(String courseCode, EnrollCourseCallback callback) {
        // 设置成功消息监听器
        serverConnection.setMessageListener(MessageType.ENROLL_COURSE_SUCCESS, message -> {
            if (callback != null) {
                callback.onSuccess();
            }
        });
        
        // 设置失败消息监听器
        serverConnection.setMessageListener(MessageType.ENROLL_COURSE_FAIL, message -> {
            if (callback != null) {
                String errorMsg = message.getMessage() != null ? message.getMessage() : "选课失败";
                callback.onFailure(errorMsg);
            }
        });
        
        // 创建请求消息
        Message request = new Message(MessageType.ENROLL_COURSE_REQUEST, courseCode);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
    
    /**
     * 退课
     * @param courseCode 课程代码
     * @param reason 退课原因
     * @param callback 回调接口
     */
    public void dropCourse(String courseCode, String reason, DropCourseCallback callback) {
        // 设置成功消息监听器
        serverConnection.setMessageListener(MessageType.DROP_COURSE_SUCCESS, message -> {
            if (callback != null) {
                callback.onSuccess();
            }
        });
        
        // 设置失败消息监听器
        serverConnection.setMessageListener(MessageType.DROP_COURSE_FAIL, message -> {
            if (callback != null) {
                String errorMsg = message.getMessage() != null ? message.getMessage() : "退课失败";
                callback.onFailure(errorMsg);
            }
        });
        
        // 创建请求数据
        java.util.Map<String, String> data = new java.util.HashMap<>();
        data.put("courseCode", courseCode);
        data.put("reason", reason);
        
        // 创建请求消息
        Message request = new Message(MessageType.DROP_COURSE_REQUEST, data);
        
        // 发送请求
        boolean sent = serverConnection.sendMessage(request);
        if (!sent) {
            callback.onFailure("发送请求失败");
        }
    }
}
