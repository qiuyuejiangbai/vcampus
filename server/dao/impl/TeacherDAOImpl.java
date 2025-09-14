package server.dao.impl;

import common.vo.TeacherVO;
import common.vo.UserVO;
import server.dao.TeacherDAO;
import server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师数据访问实现类
 */
public class TeacherDAOImpl implements TeacherDAO {
    
    @Override
    public Integer insert(TeacherVO teacher) {
        String sql = "INSERT INTO teachers (user_id, name, teacher_no, phone, email, department, research_area) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, teacher.getUserId());
            pstmt.setString(2, teacher.getName());
            pstmt.setString(3, teacher.getTeacherNo());
            pstmt.setString(4, teacher.getPhone());
            pstmt.setString(5, teacher.getEmail());
            pstmt.setString(6, teacher.getDepartment());
            pstmt.setString(7, teacher.getResearchArea());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean deleteById(Integer teacherId) {
        String sql = "DELETE FROM teachers WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除教师失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean update(TeacherVO teacher) {
        String sql = "UPDATE teachers SET name = ?, teacher_no = ?, phone = ?, email = ?, department = ?, research_area = ?, updated_time = CURRENT_TIMESTAMP WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getTeacherNo());
            pstmt.setString(3, teacher.getPhone());
            pstmt.setString(4, teacher.getEmail());
            pstmt.setString(5, teacher.getDepartment());
            pstmt.setString(6, teacher.getResearchArea());
            pstmt.setInt(7, teacher.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新教师失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public TeacherVO findById(Integer teacherId) {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTeacherVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<TeacherVO> findAll() {
        String sql = "SELECT * FROM teachers ORDER BY teacher_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TeacherVO> teachers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(mapResultSetToTeacherVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return teachers;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM teachers";
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
            System.err.println("统计教师数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsById(Integer teacherId) {
        String sql = "SELECT 1 FROM teachers WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查教师是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public TeacherVO findByUserId(Integer userId) {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTeacherVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据用户ID查询教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public TeacherVO findByTeacherNo(String teacherNo) {
        String sql = "SELECT * FROM teachers WHERE teacher_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherNo);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTeacherVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据工号查询教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean existsByTeacherNo(String teacherNo) {
        String sql = "SELECT 1 FROM teachers WHERE teacher_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherNo);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查工号是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<TeacherVO> findByDepartment(String department) {
        String sql = "SELECT t.*, u.name, u.phone, u.email, u.department FROM teachers t " +
                    "JOIN users u ON t.user_id = u.user_id WHERE u.department = ? ORDER BY t.teacher_id";
        return findByStringFieldWithUserInfo(sql, department);
    }
    
    @Override
    public List<TeacherVO> findByTitle(String title) {
        // 数据库中无title字段，返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public List<TeacherVO> findAllWithUserInfo() {
        String sql = "SELECT t.*, u.login_id, u.role, u.created_time, u.updated_time " +
                    "FROM teachers t JOIN users u ON t.user_id = u.user_id ORDER BY t.teacher_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TeacherVO> teachers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TeacherVO teacher = mapResultSetToTeacherVO(rs);
                UserVO user = mapResultSetToUserVO(rs);
                teacher.setUser(user);
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            System.err.println("查询所有教师（含用户信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return teachers;
    }
    
    @Override
    public TeacherVO findByIdWithUserInfo(Integer teacherId) {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTeacherVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询教师（含用户信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<TeacherVO> findByNameLike(String name) {
        String sql = "SELECT * FROM teachers WHERE name LIKE ? ORDER BY teacher_id";
        return findByStringFieldWithUserInfo(sql, "%" + name + "%");
    }
    
    @Override
    public int getCourseCount(Integer teacherId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("获取教师课程数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public int getStudentCount(Integer teacherId) {
        String sql = "SELECT SUM(c.enrolled_count) FROM courses c WHERE c.teacher_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("获取教师学生数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    /**
     * 通用的字符串字段查询方法
     */
    private List<TeacherVO> findByStringField(String sql, String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TeacherVO> teachers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(mapResultSetToTeacherVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询教师失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return teachers;
    }
    
    /**
     * 通用的字符串字段查询方法（包含用户信息）
     */
    private List<TeacherVO> findByStringFieldWithUserInfo(String sql, String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TeacherVO> teachers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(mapResultSetToTeacherVOWithUserInfo(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询教师（含用户信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return teachers;
    }
    
    /**
     * 将ResultSet映射为TeacherVO对象
     */
    private TeacherVO mapResultSetToTeacherVO(ResultSet rs) throws SQLException {
        TeacherVO teacher = new TeacherVO();
        teacher.setId(rs.getInt("teacher_id"));
        teacher.setUserId(rs.getInt("user_id"));
        teacher.setName(rs.getString("name"));
        teacher.setTeacherNo(rs.getString("teacher_no"));
        teacher.setPhone(rs.getString("phone"));
        teacher.setEmail(rs.getString("email"));
        teacher.setDepartment(rs.getString("department"));
        teacher.setResearchArea(rs.getString("research_area"));
        teacher.setCreatedTime(rs.getTimestamp("created_time"));
        teacher.setUpdatedTime(rs.getTimestamp("updated_time"));
        return teacher;
    }
    
    /**
     * 将ResultSet映射为TeacherVO对象（包含用户信息）
     */
    private TeacherVO mapResultSetToTeacherVOWithUserInfo(ResultSet rs) throws SQLException {
        // 由于teachers表已经包含了用户信息，直接使用基础映射
        return mapResultSetToTeacherVO(rs);
    }
    
    /**
     * 将ResultSet映射为UserVO对象（用于关联查询）
     */
    private UserVO mapResultSetToUserVO(ResultSet rs) throws SQLException {
        UserVO user = new UserVO();
        user.setUserId(rs.getInt("user_id"));
        user.setLoginId(rs.getString("login_id"));
        user.setRole(rs.getInt("role"));
        user.setCreatedTime(rs.getTimestamp("created_time"));
        user.setUpdatedTime(rs.getTimestamp("updated_time"));
        return user;
    }
}
