package server.dao.impl;

import common.vo.GradeVO;
import server.dao.GradeDAO;
import server.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩数据访问实现类
 */
public class GradeDAOImpl implements GradeDAO {
    
    @Override
    public Integer insert(GradeVO grade) {
        String sql = "INSERT INTO grades (enrollment_id, student_id, course_id, teacher_id, semester, " +
                    "midterm_grade, final_grade, assignment_grade, attendance_grade, total_grade, " +
                    "grade_point, grade_level, is_retake, comments, graded_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setObject(1, grade.getEnrollmentId());
            pstmt.setInt(2, grade.getStudentId());
            pstmt.setInt(3, grade.getCourseId());
            pstmt.setObject(4, grade.getTeacherId());
            pstmt.setString(5, grade.getSemester());
            pstmt.setObject(6, grade.getMidtermGrade());
            pstmt.setObject(7, grade.getFinalGrade());
            pstmt.setObject(8, grade.getAssignmentGrade());
            pstmt.setObject(9, grade.getAttendanceGrade());
            pstmt.setObject(10, grade.getTotalGrade());
            pstmt.setObject(11, grade.getGradePoint());
            pstmt.setString(12, grade.getGradeLevel());
            pstmt.setObject(13, grade.getIsRetake());
            pstmt.setString(14, grade.getComments());
            pstmt.setTimestamp(15, grade.getGradedTime());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean deleteById(Integer gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gradeId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除成绩失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean update(GradeVO grade) {
        String sql = "UPDATE grades SET midterm_grade = ?, final_grade = ?, assignment_grade = ?, " +
                    "attendance_grade = ?, total_grade = ?, grade_point = ?, grade_level = ?, " +
                    "is_retake = ?, comments = ?, graded_time = ? WHERE grade_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setObject(1, grade.getMidtermGrade());
            pstmt.setObject(2, grade.getFinalGrade());
            pstmt.setObject(3, grade.getAssignmentGrade());
            pstmt.setObject(4, grade.getAttendanceGrade());
            pstmt.setObject(5, grade.getTotalGrade());
            pstmt.setObject(6, grade.getGradePoint());
            pstmt.setString(7, grade.getGradeLevel());
            pstmt.setObject(8, grade.getIsRetake());
            pstmt.setString(9, grade.getComments());
            pstmt.setTimestamp(10, grade.getGradedTime());
            pstmt.setInt(11, grade.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新成绩失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public GradeVO findById(Integer gradeId) {
        String sql = "SELECT * FROM grades WHERE grade_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gradeId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToGradeVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<GradeVO> findAll() {
        String sql = "SELECT * FROM grades ORDER BY grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGradeVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM grades";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("统计成绩数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsById(Integer gradeId) {
        String sql = "SELECT 1 FROM grades WHERE grade_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gradeId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查成绩是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<GradeVO> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM grades WHERE student_id = ? ORDER BY grade_id";
        return findByIntField(sql, studentId);
    }
    
    @Override
    public List<GradeVO> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM grades WHERE course_id = ? ORDER BY grade_id";
        return findByIntField(sql, courseId);
    }
    
    @Override
    public GradeVO findByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToGradeVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和课程ID查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<GradeVO> findBySemester(String semester) {
        String sql = "SELECT * FROM grades WHERE semester = ? ORDER BY grade_id";
        return findByStringField(sql, semester);
    }
    
    @Override
    public List<GradeVO> findByTeacherId(Integer teacherId) {
        String sql = "SELECT * FROM grades WHERE teacher_id = ? ORDER BY grade_id";
        return findByIntField(sql, teacherId);
    }
    
    @Override
    public List<GradeVO> findByStudentIdAndSemester(Integer studentId, String semester) {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND semester = ? ORDER BY grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setString(2, semester);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGradeVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和学期查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public List<GradeVO> findByCourseIdAndSemester(Integer courseId, String semester) {
        String sql = "SELECT * FROM grades WHERE course_id = ? AND semester = ? ORDER BY grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            pstmt.setString(2, semester);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGradeVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据课程ID和学期查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public List<GradeVO> findAllWithDetails() {
        String sql = "SELECT g.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM grades g " +
                    "LEFT JOIN students s ON g.student_id = s.student_id " +
                    "LEFT JOIN courses c ON g.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON g.teacher_id = t.teacher_id " +
                    "ORDER BY g.grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GradeVO grade = mapResultSetToGradeVO(rs);
                // 设置关联信息
                grade.setStudentName(rs.getString("student_name"));
                grade.setStudentNo(rs.getString("student_no"));
                grade.setCourseName(rs.getString("course_name"));
                grade.setCourseCode(rs.getString("course_code"));
                grade.setCredits(rs.getInt("credits"));
                grade.setTeacherName(rs.getString("teacher_name"));
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.err.println("查询所有成绩（含详细信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public List<GradeVO> findByStudentIdWithDetails(Integer studentId) {
        String sql = "SELECT g.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM grades g " +
                    "LEFT JOIN students s ON g.student_id = s.student_id " +
                    "LEFT JOIN courses c ON g.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON g.teacher_id = t.teacher_id " +
                    "WHERE g.student_id = ? ORDER BY g.grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GradeVO grade = mapResultSetToGradeVO(rs);
                // 设置关联信息
                grade.setStudentName(rs.getString("student_name"));
                grade.setStudentNo(rs.getString("student_no"));
                grade.setCourseName(rs.getString("course_name"));
                grade.setCourseCode(rs.getString("course_code"));
                grade.setCredits(rs.getInt("credits"));
                grade.setTeacherName(rs.getString("teacher_name"));
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID查询成绩（含详细信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public List<GradeVO> findByCourseIdWithDetails(Integer courseId) {
        String sql = "SELECT g.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM grades g " +
                    "LEFT JOIN students s ON g.student_id = s.student_id " +
                    "LEFT JOIN courses c ON g.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON g.teacher_id = t.teacher_id " +
                    "WHERE g.course_id = ? ORDER BY g.grade_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GradeVO grade = mapResultSetToGradeVO(rs);
                // 设置关联信息
                grade.setStudentName(rs.getString("student_name"));
                grade.setStudentNo(rs.getString("student_no"));
                grade.setCourseName(rs.getString("course_name"));
                grade.setCourseCode(rs.getString("course_code"));
                grade.setCredits(rs.getInt("credits"));
                grade.setTeacherName(rs.getString("teacher_name"));
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.err.println("根据课程ID查询成绩（含详细信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    @Override
    public GradeVO findByStudentIdAndCourseIdWithDetails(Integer studentId, Integer courseId) {
        String sql = "SELECT g.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM grades g " +
                    "LEFT JOIN students s ON g.student_id = s.student_id " +
                    "LEFT JOIN courses c ON g.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON g.teacher_id = t.teacher_id " +
                    "WHERE g.student_id = ? AND g.course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                GradeVO grade = mapResultSetToGradeVO(rs);
                // 设置关联信息
                grade.setStudentName(rs.getString("student_name"));
                grade.setStudentNo(rs.getString("student_no"));
                grade.setCourseName(rs.getString("course_name"));
                grade.setCourseCode(rs.getString("course_code"));
                grade.setCredits(rs.getInt("credits"));
                grade.setTeacherName(rs.getString("teacher_name"));
                return grade;
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和课程ID查询成绩（含详细信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public int countByStudentId(Integer studentId) {
        String sql = "SELECT COUNT(*) FROM grades WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("统计学生成绩数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public int countByCourseId(Integer courseId) {
        String sql = "SELECT COUNT(*) FROM grades WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("统计课程成绩数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        String sql = "SELECT 1 FROM grades WHERE student_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查成绩是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    /**
     * 根据整数字段查询成绩列表的通用方法
     */
    private List<GradeVO> findByIntField(String sql, Integer value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGradeVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    /**
     * 根据字符串字段查询成绩列表的通用方法
     */
    private List<GradeVO> findByStringField(String sql, String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GradeVO> grades = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGradeVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询成绩失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return grades;
    }
    
    /**
     * 将ResultSet映射为GradeVO对象
     */
    private GradeVO mapResultSetToGradeVO(ResultSet rs) throws SQLException {
        GradeVO grade = new GradeVO();
        grade.setId(rs.getInt("grade_id"));
        grade.setEnrollmentId((Integer) rs.getObject("enrollment_id"));
        grade.setStudentId(rs.getInt("student_id"));
        grade.setCourseId(rs.getInt("course_id"));
        grade.setTeacherId((Integer) rs.getObject("teacher_id"));
        grade.setSemester(rs.getString("semester"));
        grade.setMidtermGrade((BigDecimal) rs.getObject("midterm_grade"));
        grade.setFinalGrade((BigDecimal) rs.getObject("final_grade"));
        grade.setAssignmentGrade((BigDecimal) rs.getObject("assignment_grade"));
        grade.setAttendanceGrade((BigDecimal) rs.getObject("attendance_grade"));
        grade.setTotalGrade((BigDecimal) rs.getObject("total_grade"));
        grade.setGradePoint((BigDecimal) rs.getObject("grade_point"));
        grade.setGradeLevel(rs.getString("grade_level"));
        grade.setIsRetake(rs.getBoolean("is_retake"));
        grade.setComments(rs.getString("comments"));
        grade.setGradedTime(rs.getTimestamp("graded_time"));
        grade.setCreatedTime(rs.getTimestamp("created_time"));
        grade.setUpdatedTime(rs.getTimestamp("updated_time"));
        return grade;
    }
}
