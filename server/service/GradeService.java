package server.service;

import common.vo.CourseVO;
import common.vo.GradeVO;
import common.vo.StudentVO;
import server.dao.GradeDAO;
import server.dao.impl.GradeDAOImpl;
import server.dao.CourseDAO;
import server.dao.impl.CourseDAOImpl;
import server.dao.StudentDAO;
import server.dao.impl.StudentDAOImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 成绩服务类
 * 处理成绩相关的业务逻辑
 */
public class GradeService {
    private final GradeDAO gradeDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    
    public GradeService() {
        this.gradeDAO = new GradeDAOImpl();
        this.courseDAO = new CourseDAOImpl();
        this.studentDAO = new StudentDAOImpl();
    }
    
    /**
     * 保存成绩
     * @param grade 成绩信息
     * @return 保存成功返回true，失败返回false
     */
    public boolean saveGrade(GradeVO grade) {
        if (grade == null) {
            return false;
        }
        
        try {
            // 获取学生ID
            Integer studentId = getStudentIdByStudentNo(grade.getStudentNo());
            if (studentId == null) {
                return false; // 学生不存在
            }
            
            // 获取课程ID
            Integer courseId = getCourseIdByCourseCode(grade.getCourseCode());
            if (courseId == null) {
                return false; // 课程不存在
            }
            
            // 设置基本信息
            grade.setStudentId(studentId);
            grade.setCourseId(courseId);
            grade.setGradedTime(new Timestamp(System.currentTimeMillis()));
            
            // 自动计算总成绩、等级和绩点
            grade.calculateTotalGrade();
            
            // 检查是否已存在成绩记录
            GradeVO existingGrade = gradeDAO.findByStudentIdAndCourseId(studentId, courseId);
            
            if (existingGrade != null) {
                // 更新现有成绩
                grade.setId(existingGrade.getId());
                return gradeDAO.update(grade);
            } else {
                // 插入新成绩
                Integer gradeId = gradeDAO.insert(grade);
                return gradeId != null;
            }
            
        } catch (Exception e) {
            System.err.println("保存成绩失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取课程的所有学生成绩
     * @param courseCode 课程代码
     * @return 成绩列表
     */
    public List<GradeVO> getCourseGrades(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 获取课程ID
            Integer courseId = getCourseIdByCourseCode(courseCode);
            if (courseId == null) {
                return null;
            }
            
            return gradeDAO.findByCourseId(courseId);
        } catch (Exception e) {
            System.err.println("获取课程成绩失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取学生的所有成绩
     * @param studentNo 学号
     * @return 成绩列表
     */
    public List<GradeVO> getStudentGrades(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 获取学生ID
            Integer studentId = getStudentIdByStudentNo(studentNo);
            if (studentId == null) {
                return null;
            }
            
            return gradeDAO.findByStudentId(studentId);
        } catch (Exception e) {
            System.err.println("获取学生成绩失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据学生ID获取成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    public List<GradeVO> getStudentGradesById(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        return gradeDAO.findByStudentId(studentId);
    }
    
    /**
     * 根据课程ID获取成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    public List<GradeVO> getCourseGradesById(Integer courseId) {
        if (courseId == null) {
            return null;
        }
        return gradeDAO.findByCourseId(courseId);
    }
    
    /**
     * 根据学生ID和课程ID获取成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息，不存在返回null
     */
    public GradeVO getGradeByStudentAndCourse(Integer studentId, Integer courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        return gradeDAO.findByStudentIdAndCourseId(studentId, courseId);
    }
    
    /**
     * 根据学号和课程代码获取成绩
     * @param studentNo 学号
     * @param courseCode 课程代码
     * @return 成绩信息，不存在返回null
     */
    public GradeVO getGradeByStudentNoAndCourseCode(String studentNo, String courseCode) {
        if (studentNo == null || studentNo.trim().isEmpty() || 
            courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 获取学生ID
            Integer studentId = getStudentIdByStudentNo(studentNo);
            if (studentId == null) {
                return null;
            }
            
            // 获取课程ID
            Integer courseId = getCourseIdByCourseCode(courseCode);
            if (courseId == null) {
                return null;
            }
            
            return gradeDAO.findByStudentIdAndCourseId(studentId, courseId);
        } catch (Exception e) {
            System.err.println("获取成绩失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 更新成绩
     * @param grade 成绩信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateGrade(GradeVO grade) {
        if (grade == null || grade.getId() == null) {
            return false;
        }
        
        try {
            // 自动计算总成绩、等级和绩点
            grade.calculateTotalGrade();
            grade.setGradedTime(new Timestamp(System.currentTimeMillis()));
            
            return gradeDAO.update(grade);
        } catch (Exception e) {
            System.err.println("更新成绩失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除成绩
     * @param gradeId 成绩ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteGrade(Integer gradeId) {
        if (gradeId == null) {
            return false;
        }
        return gradeDAO.deleteById(gradeId);
    }
    
    /**
     * 根据学生ID和课程ID删除成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteGradeByStudentAndCourse(Integer studentId, Integer courseId) {
        if (studentId == null || courseId == null) {
            return false;
        }
        
        try {
            GradeVO grade = gradeDAO.findByStudentIdAndCourseId(studentId, courseId);
            if (grade != null) {
                return gradeDAO.deleteById(grade.getId());
            }
            return false;
        } catch (Exception e) {
            System.err.println("删除成绩失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 批量保存成绩
     * @param grades 成绩列表
     * @return 成功保存的数量
     */
    public int batchSaveGrades(List<GradeVO> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (GradeVO grade : grades) {
            if (saveGrade(grade)) {
                successCount++;
            }
        }
        
        return successCount;
    }
    
    /**
     * 计算课程平均分
     * @param courseCode 课程代码
     * @return 平均分，无成绩返回null
     */
    public BigDecimal calculateCourseAverage(String courseCode) {
        List<GradeVO> grades = getCourseGrades(courseCode);
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        
        for (GradeVO grade : grades) {
            if (grade.getTotalGrade() != null) {
                total = total.add(grade.getTotalGrade());
                count++;
            }
        }
        
        if (count == 0) {
            return null;
        }
        
        return total.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算学生平均分
     * @param studentNo 学号
     * @return 平均分，无成绩返回null
     */
    public BigDecimal calculateStudentAverage(String studentNo) {
        List<GradeVO> grades = getStudentGrades(studentNo);
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        
        for (GradeVO grade : grades) {
            if (grade.getTotalGrade() != null) {
                total = total.add(grade.getTotalGrade());
                count++;
            }
        }
        
        if (count == 0) {
            return null;
        }
        
        return total.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 根据学号获取学生ID
     */
    private Integer getStudentIdByStudentNo(String studentNo) {
        try {
            StudentVO student = studentDAO.findByStudentNo(studentNo);
            return student != null ? student.getStudentId() : null;
        } catch (Exception e) {
            System.err.println("根据学号获取学生ID失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据课程代码获取课程ID
     */
    private Integer getCourseIdByCourseCode(String courseCode) {
        try {
            CourseVO course = courseDAO.findByCourseCode(courseCode);
            return course != null ? course.getCourseId() : null;
        } catch (Exception e) {
            System.err.println("根据课程代码获取课程ID失败: " + e.getMessage());
            return null;
        }
    }
}
