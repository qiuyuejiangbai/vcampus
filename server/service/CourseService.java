package server.service;

import common.vo.CourseVO;
import server.dao.CourseDAO;
import server.dao.impl.CourseDAOImpl;

import java.util.List;

/**
 * 课程服务类
 * 处理课程相关的业务逻辑
 */
public class CourseService {
    private final CourseDAO courseDAO;
    
    public CourseService() {
        this.courseDAO = new CourseDAOImpl();
    }
    
    /**
     * 获取所有课程
     * @return 课程列表
     */
    public List<CourseVO> getAllCourses() {
        return courseDAO.findAllWithTeacherName();
    }
    
    /**
     * 获取所有可用课程（状态为激活的课程）
     * @return 可用课程列表
     */
    public List<CourseVO> getAvailableCourses() {
        return courseDAO.findAllEnabled();
    }
    
    /**
     * 根据教师ID获取课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    public List<CourseVO> getCoursesByTeacher(Integer teacherId) {
        if (teacherId == null) {
            return null;
        }
        return courseDAO.findByTeacherId(teacherId);
    }
    
    /**
     * 根据课程代码获取课程信息
     * @param courseCode 课程代码
     * @return 课程信息，不存在返回null
     */
    public CourseVO getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        return courseDAO.findByCourseCode(courseCode);
    }
    
    /**
     * 根据课程ID获取课程信息
     * @param courseId 课程ID
     * @return 课程信息，不存在返回null
     */
    public CourseVO getCourseById(Integer courseId) {
        if (courseId == null) {
            return null;
        }
        return courseDAO.findByIdWithTeacherName(courseId);
    }
    
    /**
     * 添加课程
     * @param course 课程信息
     * @return 添加成功返回课程ID，失败返回null
     */
    public Integer addCourse(CourseVO course) {
        if (course == null || course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            return null;
        }
        
        // 检查课程代码是否已存在
        if (courseDAO.existsByCourseCode(course.getCourseCode())) {
            return null; // 课程代码已存在
        }
        
        return courseDAO.insert(course);
    }
    
    /**
     * 更新课程信息
     * @param course 课程信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateCourse(CourseVO course) {
        if (course == null || course.getCourseId() == null) {
            return false;
        }
        return courseDAO.update(course);
    }
    
    /**
     * 删除课程
     * @param courseId 课程ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteCourse(Integer courseId) {
        if (courseId == null) {
            return false;
        }
        return courseDAO.deleteById(courseId);
    }
    
    /**
     * 根据课程代码删除课程
     * @param courseCode 课程代码
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        
        CourseVO course = courseDAO.findByCourseCode(courseCode);
        if (course == null) {
            return false;
        }
        
        return courseDAO.deleteById(course.getCourseId());
    }
    
    /**
     * 检查课程代码是否存在
     * @param courseCode 课程代码
     * @return 存在返回true，不存在返回false
     */
    public boolean isCourseCodeExists(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        return courseDAO.existsByCourseCode(courseCode);
    }
    
    /**
     * 更新课程选课人数
     * @param courseId 课程ID
     * @param enrolledCount 选课人数
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateEnrolledCount(Integer courseId, Integer enrolledCount) {
        if (courseId == null || enrolledCount == null) {
            return false;
        }
        return courseDAO.updateEnrolledCount(courseId, enrolledCount);
    }
    
    /**
     * 增加课程选课人数
     * @param courseId 课程ID
     * @param increment 增加数量（可为负数表示减少）
     * @return 更新成功返回true，失败返回false
     */
    public boolean incrementEnrolledCount(Integer courseId, Integer increment) {
        if (courseId == null || increment == null) {
            return false;
        }
        return courseDAO.incrementEnrolledCount(courseId, increment);
    }
    
    /**
     * 根据条件搜索课程
     * @param name 课程名称关键词
     * @param department 院系
     * @param semester 学期
     * @return 课程列表
     */
    public List<CourseVO> searchCourses(String name, String department, String semester) {
        List<CourseVO> allCourses = courseDAO.findAllWithTeacherName();
        List<CourseVO> result = new java.util.ArrayList<>();
        
        for (CourseVO course : allCourses) {
            boolean match = true;
            
            // 按课程名称搜索
            if (name != null && !name.trim().isEmpty()) {
                if (course.getCourseName() == null || !course.getCourseName().toLowerCase().contains(name.toLowerCase())) {
                    match = false;
                }
            }
            
            // 按院系搜索
            if (department != null && !department.equals("全部") && !department.trim().isEmpty()) {
                if (course.getDepartment() == null || !course.getDepartment().equals(department)) {
                    match = false;
                }
            }
            
            // 按学期搜索
            if (semester != null && !semester.equals("全部") && !semester.trim().isEmpty()) {
                if (course.getSemester() == null || !course.getSemester().equals(semester)) {
                    match = false;
                }
            }
            
            if (match) {
                result.add(course);
            }
        }
        
        return result;
    }
    
    /**
     * 根据院系获取课程列表
     * @param department 院系
     * @return 课程列表
     */
    public List<CourseVO> getCoursesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return null;
        }
        return courseDAO.findByDepartment(department);
    }
    
    /**
     * 根据学期获取课程列表
     * @param semester 学期
     * @return 课程列表
     */
    public List<CourseVO> getCoursesBySemester(String semester) {
        if (semester == null || semester.trim().isEmpty()) {
            return null;
        }
        return courseDAO.findBySemester(semester);
    }
    
    /**
     * 根据状态获取课程列表
     * @param status 状态
     * @return 课程列表
     */
    public List<CourseVO> getCoursesByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return courseDAO.findByStatus(status);
    }
    
    /**
     * 根据课程名称模糊查询
     * @param courseName 课程名称关键词
     * @return 课程列表
     */
    public List<CourseVO> searchCoursesByName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return null;
        }
        return courseDAO.findByNameLike(courseName);
    }
}
