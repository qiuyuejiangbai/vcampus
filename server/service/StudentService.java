package server.service;

import common.vo.StudentVO;
import common.vo.UserVO;
import server.dao.StudentDAO;
import server.dao.UserDAO;
import server.dao.impl.StudentDAOImpl;
import server.dao.impl.UserDAOImpl;
import server.util.MD5Util;

/**
 * 学生服务类
 * 处理学生相关的业务逻辑
 */
public class StudentService {
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;
    
    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
        this.userDAO = new UserDAOImpl();
    }
    
    /**
     * 根据用户ID获取学生详细信息
     * @param userId 用户ID
     * @return 学生详细信息，不存在返回null
     */
    public StudentVO getStudentByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }
        return studentDAO.findByUserId(userId);
    }
    
    /**
     * 根据学号获取学生详细信息
     * @param studentNo 学号
     * @return 学生详细信息，不存在返回null
     */
    public StudentVO getStudentByStudentNo(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return null;
        }
        return studentDAO.findByStudentNo(studentNo);
    }
    
    /**
     * 根据学生ID获取学生详细信息（包含用户信息）
     * @param studentId 学生ID
     * @return 学生详细信息，不存在返回null
     */
    public StudentVO getStudentById(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        return studentDAO.findByIdWithUserInfo(studentId);
    }
    
    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateStudent(StudentVO student) {
        if (student == null || student.getStudentId() == null) {
            return false;
        }
        return studentDAO.update(student);
    }
    
    /**
     * 添加学生信息
     * @param student 学生信息
     * @return 添加成功返回学生ID，失败返回null
     */
    public Integer addStudent(StudentVO student) {
        if (student == null || student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            return null;
        }
        
        // 检查学号是否已存在
        if (studentDAO.existsByStudentNo(student.getStudentNo())) {
            return null; // 学号已存在
        }
        
        // 检查登录ID是否已存在
        if (userDAO.existsByLoginId(student.getStudentNo())) {
            return null; // 登录ID已存在
        }
        
        try {
            // 先创建用户记录
            UserVO user = new UserVO();
            user.setLoginId(student.getStudentNo());
            user.setPassword(MD5Util.encrypt("123456")); // 默认密码
            user.setRole(0); // 学生角色
            user.setName(student.getName());
            user.setPhone(student.getPhone());
            user.setEmail(student.getEmail());
            user.setStatus(1); // 激活状态
            user.setBalance(0.0); // 初始余额
            
            Integer userId = userDAO.insert(user);
            if (userId == null) {
                return null; // 用户创建失败
            }
            
            // 设置学生记录的用户ID
            student.setUserId(userId);
            
            // 创建学生记录
            return studentDAO.insert(student);
            
        } catch (Exception e) {
            System.err.println("添加学生失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 删除学生信息
     * @param studentId 学生ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteStudent(Integer studentId) {
        if (studentId == null) {
            return false;
        }
        return studentDAO.deleteById(studentId);
    }
    
    /**
     * 根据学号删除学生
     * @param studentNo 学号
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteStudentByNo(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return false;
        }
        
        StudentVO student = studentDAO.findByStudentNo(studentNo);
        if (student == null) {
            return false;
        }
        
        try {
            // 删除学生记录（会级联删除用户记录）
            boolean studentDeleted = studentDAO.deleteById(student.getStudentId());
            
            // 如果学生删除成功，也删除用户记录
            if (studentDeleted && student.getUserId() != null) {
                userDAO.deleteById(student.getUserId());
            }
            
            return studentDeleted;
            
        } catch (Exception e) {
            System.err.println("删除学生失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查学号是否存在
     * @param studentNo 学号
     * @return 存在返回true，不存在返回false
     */
    public boolean isStudentNoExists(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return false;
        }
        return studentDAO.existsByStudentNo(studentNo);
    }
    
    /**
     * 获取所有学生信息
     * @return 学生列表
     */
    public java.util.List<StudentVO> getAllStudents() {
        return studentDAO.findAllWithUserInfo();
    }
    
    /**
     * 根据条件搜索学生
     * @param name 姓名关键词
     * @param major 专业
     * @param enrollmentYear 入学年份
     * @return 学生列表
     */
    public java.util.List<StudentVO> searchStudents(String name, String major, String enrollmentYear) {
        java.util.List<StudentVO> allStudents = studentDAO.findAllWithUserInfo();
        java.util.List<StudentVO> result = new java.util.ArrayList<>();
        
        for (StudentVO student : allStudents) {
            boolean match = true;
            
            // 按姓名搜索
            if (name != null && !name.trim().isEmpty()) {
                if (student.getName() == null || !student.getName().toLowerCase().contains(name.toLowerCase())) {
                    match = false;
                }
            }
            
            // 按专业搜索
            if (major != null && !major.equals("全部") && !major.trim().isEmpty()) {
                if (student.getMajor() == null || !student.getMajor().equals(major)) {
                    match = false;
                }
            }
            
            // 按入学年份搜索
            if (enrollmentYear != null && !enrollmentYear.equals("全部") && !enrollmentYear.trim().isEmpty()) {
                if (student.getEnrollmentYear() == null || !student.getEnrollmentYear().toString().equals(enrollmentYear)) {
                    match = false;
                }
            }
            
            if (match) {
                result.add(student);
            }
        }
        
        return result;
    }
}
