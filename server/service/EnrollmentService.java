package server.service;

import common.vo.CourseVO;
import common.vo.EnrollmentVO;
import server.dao.EnrollmentDAO;
import server.dao.impl.EnrollmentDAOImpl;
import server.dao.CourseDAO;
import server.dao.impl.CourseDAOImpl;

import java.sql.Timestamp;
import java.util.List;

/**
 * 选课服务类
 * 处理选课相关的业务逻辑
 */
public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    
    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.courseDAO = new CourseDAOImpl();
    }
    
    /**
     * 学生选课
     * @param studentId 学生ID
     * @param courseCode 课程代码
     * @return 选课成功返回true，失败返回false
     */
    public boolean enrollCourse(Integer studentId, String courseCode) {
        if (studentId == null || courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return false; // 课程不存在
            }
            
            // 检查课程是否已满
            if (course.getAvailableCapacity() <= 0) {
                return false; // 课程已满
            }
            
            // 检查学生是否已选该课程
            if (enrollmentDAO.isEnrolled(studentId, course.getCourseId())) {
                return false; // 已选该课程
            }
            
            // 创建选课记录
            EnrollmentVO enrollment = new EnrollmentVO();
            enrollment.setStudentId(studentId);
            enrollment.setCourseId(course.getCourseId());
            enrollment.setSemester(course.getSemester());
            enrollment.setAcademicYear(course.getAcademicYear());
            enrollment.setEnrollmentTime(new Timestamp(System.currentTimeMillis()));
            enrollment.setStatus("enrolled");
            
            // 插入选课记录
            Integer enrollmentId = enrollmentDAO.insert(enrollment);
            if (enrollmentId != null) {
                // 更新课程选课人数
                courseDAO.incrementEnrolledCount(course.getCourseId(), 1);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("选课失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 学生退课
     * @param studentId 学生ID
     * @param courseCode 课程代码
     * @param reason 退课原因
     * @return 退课成功返回true，失败返回false
     */
    public boolean dropCourse(Integer studentId, String courseCode, String reason) {
        if (studentId == null || courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return false; // 课程不存在
            }
            
            // 获取选课记录
            EnrollmentVO enrollment = enrollmentDAO.findByStudentIdAndCourseId(studentId, course.getCourseId());
            if (enrollment == null || !"enrolled".equals(enrollment.getStatus())) {
                return false; // 未选该课程或已退课
            }
            
            // 更新选课记录状态
            enrollment.setStatus("dropped");
            enrollment.setDropTime(new Timestamp(System.currentTimeMillis()));
            enrollment.setDropReason(reason);
            
            boolean success = enrollmentDAO.update(enrollment);
            if (success) {
                // 更新课程选课人数
                courseDAO.incrementEnrolledCount(course.getCourseId(), -1);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("退课失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 获取学生的选课记录
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    public List<EnrollmentVO> getStudentEnrollments(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        // 使用包含课程信息的方法
        return enrollmentDAO.getTranscriptByStudentId(studentId);
    }
    
    /**
     * 获取课程的学生名单
     * @param courseCode 课程代码
     * @return 选课记录列表
     */
    public List<EnrollmentVO> getCourseStudents(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return null;
            }
            
            return enrollmentDAO.getStudentListByCourseId(course.getCourseId());
        } catch (Exception e) {
            System.err.println("获取课程学生名单失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取学生的成绩单
     * @param studentId 学生ID
     * @return 选课记录列表（包含成绩信息）
     */
    public List<EnrollmentVO> getStudentTranscript(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        return enrollmentDAO.getTranscriptByStudentId(studentId);
    }
    
    /**
     * 检查学生是否已选某课程
     * @param studentId 学生ID
     * @param courseCode 课程代码
     * @return 已选返回true，未选返回false
     */
    public boolean isStudentEnrolled(Integer studentId, String courseCode) {
        if (studentId == null || courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return false;
            }
            
            return enrollmentDAO.isEnrolled(studentId, course.getCourseId());
        } catch (Exception e) {
            System.err.println("检查选课状态失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 统计学生当前选课数量
     * @param studentId 学生ID
     * @return 选课数量
     */
    public int countStudentActiveEnrollments(Integer studentId) {
        if (studentId == null) {
            return 0;
        }
        return enrollmentDAO.countActiveEnrollmentsByStudentId(studentId);
    }
    
    /**
     * 统计课程当前选课人数
     * @param courseCode 课程代码
     * @return 选课人数
     */
    public int countCourseActiveEnrollments(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return 0;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return 0;
            }
            
            return enrollmentDAO.countActiveEnrollmentsByCourseId(course.getCourseId());
        } catch (Exception e) {
            System.err.println("统计课程选课人数失败: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 根据状态获取选课记录
     * @param status 状态
     * @return 选课记录列表
     */
    public List<EnrollmentVO> getEnrollmentsByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return enrollmentDAO.findByStatus(status);
    }
    
    /**
     * 根据学生ID和状态获取选课记录
     * @param studentId 学生ID
     * @param status 状态
     * @return 选课记录列表
     */
    public List<EnrollmentVO> getStudentEnrollmentsByStatus(Integer studentId, Integer status) {
        if (studentId == null || status == null) {
            return null;
        }
        return enrollmentDAO.findByStudentIdAndStatus(studentId, status);
    }
    
    /**
     * 根据课程ID和状态获取选课记录
     * @param courseCode 课程代码
     * @param status 状态
     * @return 选课记录列表
     */
    public List<EnrollmentVO> getCourseEnrollmentsByStatus(String courseCode, Integer status) {
        if (courseCode == null || courseCode.trim().isEmpty() || status == null) {
            return null;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return null;
            }
            
            return enrollmentDAO.findByCourseIdAndStatus(course.getCourseId(), status);
        } catch (Exception e) {
            System.err.println("获取课程选课记录失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 更新选课状态
     * @param studentId 学生ID
     * @param courseCode 课程代码
     * @param status 新状态
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateEnrollmentStatus(Integer studentId, String courseCode, Integer status) {
        if (studentId == null || courseCode == null || courseCode.trim().isEmpty() || status == null) {
            return false;
        }
        
        try {
            // 获取课程信息
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            if (course == null) {
                return false;
            }
            
            return enrollmentDAO.updateStatus(studentId, course.getCourseId(), status);
        } catch (Exception e) {
            System.err.println("更新选课状态失败: " + e.getMessage());
            return false;
        }
    }
}
