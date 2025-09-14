package server.dao.impl;

import common.vo.EnrollmentVO;
import server.dao.EnrollmentDAO;
import server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 选课记录数据访问实现类
 */
public class EnrollmentDAOImpl implements EnrollmentDAO {
    
    @Override
    public Integer insert(EnrollmentVO enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, semester, academic_year, " +
                    "enrollment_time, drop_time, drop_reason, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getSemester());
            pstmt.setString(4, enrollment.getAcademicYear());
            pstmt.setTimestamp(5, enrollment.getEnrollmentTime());
            pstmt.setTimestamp(6, enrollment.getDropTime());
            pstmt.setString(7, enrollment.getDropReason());
            pstmt.setString(8, enrollment.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean deleteById(Integer enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除选课记录失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean update(EnrollmentVO enrollment) {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, semester = ?, " +
                    "academic_year = ?, enrollment_time = ?, drop_time = ?, drop_reason = ?, " +
                    "status = ? WHERE enrollment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getSemester());
            pstmt.setString(4, enrollment.getAcademicYear());
            pstmt.setTimestamp(5, enrollment.getEnrollmentTime());
            pstmt.setTimestamp(6, enrollment.getDropTime());
            pstmt.setString(7, enrollment.getDropReason());
            pstmt.setString(8, enrollment.getStatus());
            pstmt.setInt(9, enrollment.getEnrollmentId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新选课记录失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public EnrollmentVO findById(Integer enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEnrollmentVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<EnrollmentVO> findAll() {
        String sql = "SELECT * FROM enrollments ORDER BY enrollment_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollmentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM enrollments";
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
            System.err.println("统计选课记录数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsById(Integer enrollmentId) {
        String sql = "SELECT 1 FROM enrollments WHERE enrollment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查选课记录是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<EnrollmentVO> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? ORDER BY enrollment_id";
        return findByIntField(sql, studentId);
    }
    
    @Override
    public List<EnrollmentVO> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? ORDER BY enrollment_id";
        return findByIntField(sql, courseId);
    }
    
    @Override
    public EnrollmentVO findByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
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
                return mapResultSetToEnrollmentVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和课程ID查询选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean isEnrolled(Integer studentId, Integer courseId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ? AND status = 'enrolled'";
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
            System.err.println("检查学生是否已选课失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<EnrollmentVO> findByStatus(Integer status) {
        String sql = "SELECT * FROM enrollments WHERE status = ? ORDER BY enrollment_id";
        return findByIntField(sql, status);
    }
    
    @Override
    public List<EnrollmentVO> findByStudentIdAndStatus(Integer studentId, Integer status) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND status = ? ORDER BY enrollment_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setString(2, getStatusString(status));
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollmentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和状态查询选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    @Override
    public List<EnrollmentVO> findByCourseIdAndStatus(Integer courseId, Integer status) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? AND status = ? ORDER BY enrollment_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            pstmt.setString(2, getStatusString(status));
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollmentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据课程ID和状态查询选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    @Override
    public List<EnrollmentVO> getTranscriptByStudentId(Integer studentId) {
        String sql = "SELECT e.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM enrollments e " +
                    "LEFT JOIN students s ON e.student_id = s.student_id " +
                    "LEFT JOIN courses c ON e.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE e.student_id = ? ORDER BY e.enrollment_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EnrollmentVO enrollment = mapResultSetToEnrollmentVO(rs);
                // 设置关联信息
                enrollment.setStudentName(rs.getString("student_name"));
                enrollment.setStudentNo(rs.getString("student_no"));
                enrollment.setCourseName(rs.getString("course_name"));
                enrollment.setCourseCode(rs.getString("course_code"));
                enrollment.setCredits(rs.getInt("credits"));
                enrollment.setTeacherName(rs.getString("teacher_name"));
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("查询学生成绩单失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    @Override
    public List<EnrollmentVO> getStudentListByCourseId(Integer courseId) {
        String sql = "SELECT e.*, s.name as student_name, s.student_no, c.course_name, c.course_code, " +
                    "c.credits, t.name as teacher_name " +
                    "FROM enrollments e " +
                    "LEFT JOIN students s ON e.student_id = s.student_id " +
                    "LEFT JOIN courses c ON e.course_id = c.course_id " +
                    "LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE e.course_id = ? ORDER BY e.enrollment_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EnrollmentVO enrollment = mapResultSetToEnrollmentVO(rs);
                // 设置关联信息
                enrollment.setStudentName(rs.getString("student_name"));
                enrollment.setStudentNo(rs.getString("student_no"));
                enrollment.setCourseName(rs.getString("course_name"));
                enrollment.setCourseCode(rs.getString("course_code"));
                enrollment.setCredits(rs.getInt("credits"));
                enrollment.setTeacherName(rs.getString("teacher_name"));
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("查询课程学生名单失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    @Override
    public boolean updateGrade(Integer studentId, Integer courseId, Double grade) {
        // 这个方法在成绩表中更新，这里暂时不实现
        return false;
    }
    
    @Override
    public boolean updateStatus(Integer studentId, Integer courseId, Integer status) {
        String sql = "UPDATE enrollments SET status = ? WHERE student_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getStatusString(status));
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新选课状态失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean dropCourse(Integer studentId, Integer courseId) {
        String sql = "UPDATE enrollments SET status = 'dropped', drop_time = NOW() WHERE student_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("退课失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public int countActiveEnrollmentsByStudentId(Integer studentId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND status IN ('enrolled', 'completed')";
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
            System.err.println("统计学生活跃选课数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public int countActiveEnrollmentsByCourseId(Integer courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND status IN ('enrolled', 'completed')";
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
            System.err.println("统计课程活跃选课人数失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    /**
     * 根据整数字段查询选课记录列表的通用方法
     */
    private List<EnrollmentVO> findByIntField(String sql, Integer value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentVO> enrollments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollmentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询选课记录失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return enrollments;
    }
    
    /**
     * 将状态数字转换为字符串
     */
    private String getStatusString(Integer status) {
        if (status == null) return "enrolled";
        switch (status) {
            case 0: return "dropped";
            case 1: return "enrolled";
            case 2: return "completed";
            default: return "enrolled";
        }
    }
    
    /**
     * 将ResultSet映射为EnrollmentVO对象
     */
    private EnrollmentVO mapResultSetToEnrollmentVO(ResultSet rs) throws SQLException {
        EnrollmentVO enrollment = new EnrollmentVO();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setSemester(rs.getString("semester"));
        enrollment.setAcademicYear(rs.getString("academic_year"));
        enrollment.setEnrollmentTime(rs.getTimestamp("enrollment_time"));
        enrollment.setDropTime(rs.getTimestamp("drop_time"));
        enrollment.setDropReason(rs.getString("drop_reason"));
        enrollment.setStatus(rs.getString("status"));
        return enrollment;
    }
}
