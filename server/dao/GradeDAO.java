package server.dao;

import common.vo.GradeVO;
import java.util.List;

/**
 * 成绩数据访问接口
 * 定义成绩相关的数据库操作方法
 */
public interface GradeDAO extends BaseDAO<GradeVO, Integer> {
    
    /**
     * 根据学生ID查询成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<GradeVO> findByStudentId(Integer studentId);
    
    /**
     * 根据课程ID查询成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    List<GradeVO> findByCourseId(Integer courseId);
    
    /**
     * 根据学生ID和课程ID查询成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息，不存在返回null
     */
    GradeVO findByStudentIdAndCourseId(Integer studentId, Integer courseId);
    
    /**
     * 根据学期查询成绩
     * @param semester 学期
     * @return 成绩列表
     */
    List<GradeVO> findBySemester(String semester);
    
    /**
     * 根据教师ID查询成绩
     * @param teacherId 教师ID
     * @return 成绩列表
     */
    List<GradeVO> findByTeacherId(Integer teacherId);
    
    /**
     * 根据学生ID和学期查询成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<GradeVO> findByStudentIdAndSemester(Integer studentId, String semester);
    
    /**
     * 根据课程ID和学期查询成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<GradeVO> findByCourseIdAndSemester(Integer courseId, String semester);
    
    /**
     * 查询所有成绩（包含学生和课程信息）
     * @return 成绩列表
     */
    List<GradeVO> findAllWithDetails();
    
    /**
     * 根据学生ID查询成绩（包含课程信息）
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<GradeVO> findByStudentIdWithDetails(Integer studentId);
    
    /**
     * 根据课程ID查询成绩（包含学生信息）
     * @param courseId 课程ID
     * @return 成绩列表
     */
    List<GradeVO> findByCourseIdWithDetails(Integer courseId);
    
    /**
     * 根据学生ID和课程ID查询成绩（包含详细信息）
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息，不存在返回null
     */
    GradeVO findByStudentIdAndCourseIdWithDetails(Integer studentId, Integer courseId);
    
    /**
     * 统计学生成绩数量
     * @param studentId 学生ID
     * @return 成绩数量
     */
    int countByStudentId(Integer studentId);
    
    /**
     * 统计课程成绩数量
     * @param courseId 课程ID
     * @return 成绩数量
     */
    int countByCourseId(Integer courseId);
    
    /**
     * 检查成绩是否存在
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 存在返回true，不存在返回false
     */
    boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
