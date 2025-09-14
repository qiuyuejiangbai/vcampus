package server.dao.impl;

import common.vo.StudentVO;
import common.vo.UserVO;
import server.dao.StudentDAO;
import server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问实现类
 */
public class StudentDAOImpl implements StudentDAO {
    
    @Override
    public Integer insert(StudentVO student) {
        // 检查数据库是否有grade字段
        boolean hasGradeField = checkGradeFieldExists();
        
        String sql;
        if (hasGradeField) {
            sql = "INSERT INTO students (user_id, name, student_no, gender, birth_date, phone, email, address, department, class_name, major, grade, enrollment_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO students (user_id, name, student_no, gender, birth_date, phone, email, address, department, class_name, major, enrollment_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getStudentNo());
            pstmt.setString(4, student.getGender());
            pstmt.setDate(5, student.getBirthDate());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getAddress());
            pstmt.setString(9, student.getDepartment());
            pstmt.setString(10, student.getClassName());
            pstmt.setString(11, student.getMajor());
            
            if (hasGradeField) {
                pstmt.setString(12, student.getGrade());
                pstmt.setObject(13, student.getEnrollmentYear());
            } else {
                pstmt.setObject(12, student.getEnrollmentYear());
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean deleteById(Integer studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除学生失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean update(StudentVO student) {
        // 检查数据库是否有grade字段
        boolean hasGradeField = checkGradeFieldExists();
        
        String sql;
        if (hasGradeField) {
            sql = "UPDATE students SET name = ?, student_no = ?, gender = ?, birth_date = ?, phone = ?, email = ?, address = ?, department = ?, class_name = ?, major = ?, grade = ?, enrollment_year = ? WHERE student_id = ?";
        } else {
            sql = "UPDATE students SET name = ?, student_no = ?, gender = ?, birth_date = ?, phone = ?, email = ?, address = ?, department = ?, class_name = ?, major = ?, enrollment_year = ? WHERE student_id = ?";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentNo());
            pstmt.setString(3, student.getGender());
            pstmt.setDate(4, student.getBirthDate());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getDepartment());
            pstmt.setString(9, student.getClassName());
            pstmt.setString(10, student.getMajor());
            
            if (hasGradeField) {
                pstmt.setString(11, student.getGrade());
                pstmt.setObject(12, student.getEnrollmentYear());
                pstmt.setInt(13, student.getStudentId());
            } else {
                pstmt.setObject(11, student.getEnrollmentYear());
                pstmt.setInt(12, student.getStudentId());
            }
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新学生失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public StudentVO findById(Integer studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudentVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<StudentVO> findAll() {
        String sql = "SELECT * FROM students ORDER BY student_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<StudentVO> students = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return students;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM students";
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
            System.err.println("统计学生数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsById(Integer studentId) {
        String sql = "SELECT 1 FROM students WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查学生是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public StudentVO findByUserId(Integer userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudentVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据用户ID查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public StudentVO findByStudentNo(String studentNo) {
        String sql = "SELECT * FROM students WHERE student_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentNo);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudentVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据学号查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean existsByStudentNo(String studentNo) {
        String sql = "SELECT 1 FROM students WHERE student_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentNo);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查学号是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<StudentVO> findByMajor(String major) {
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY student_id";
        return findByStringField(sql, major);
    }
    
    @Override
    public List<StudentVO> findByClassName(String className) {
        String sql = "SELECT * FROM students WHERE class_name = ? ORDER BY student_id";
        return findByStringField(sql, className);
    }
    
    
    @Override
    public List<StudentVO> findByEnrollmentYear(Integer enrollmentYear) {
        String sql = "SELECT * FROM students WHERE enrollment_year = ? ORDER BY student_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<StudentVO> students = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentYear);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据入学年份查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return students;
    }
    
    @Override
    public List<StudentVO> findAllWithUserInfo() {
        String sql = "SELECT s.*, u.login_id, u.role, u.created_time, u.updated_time " +
                    "FROM students s JOIN users u ON s.user_id = u.user_id ORDER BY s.student_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<StudentVO> students = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StudentVO student = mapResultSetToStudentVO(rs);
                UserVO user = mapResultSetToUserVO(rs);
                student.setUserInfo(user);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("查询所有学生（含用户信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return students;
    }
    
    @Override
    public StudentVO findByIdWithUserInfo(Integer studentId) {
        String sql = "SELECT s.*, u.login_id, u.role, u.created_time, u.updated_time " +
                    "FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StudentVO student = mapResultSetToStudentVO(rs);
                UserVO user = mapResultSetToUserVO(rs);
                student.setUserInfo(user);
                return student;
            }
        } catch (SQLException e) {
            System.err.println("查询学生（含用户信息）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<StudentVO> findByNameLike(String name) {
        String sql = "SELECT s.*, u.login_id, u.role, u.created_time, u.updated_time " +
                    "FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.name LIKE ? ORDER BY s.student_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<StudentVO> students = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StudentVO student = mapResultSetToStudentVO(rs);
                UserVO user = mapResultSetToUserVO(rs);
                student.setUserInfo(user);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("根据姓名模糊查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return students;
    }
    
    /**
     * 根据字符串字段查询学生列表的通用方法
     */
    private List<StudentVO> findByStringField(String sql, String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<StudentVO> students = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudentVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询学生失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return students;
    }
    
    /**
     * 将ResultSet映射为StudentVO对象
     */
    private StudentVO mapResultSetToStudentVO(ResultSet rs) throws SQLException {
        StudentVO student = new StudentVO();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getInt("user_id"));
        student.setName(rs.getString("name"));
        student.setStudentNo(rs.getString("student_no"));
        student.setGender(rs.getString("gender"));
        student.setBirthDate(rs.getDate("birth_date"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setAddress(rs.getString("address"));
        student.setDepartment(rs.getString("department"));
        student.setClassName(rs.getString("class_name"));
        student.setMajor(rs.getString("major"));
        // 安全地获取grade字段，如果不存在则设为null
        try {
            student.setGrade(rs.getString("grade"));
        } catch (SQLException e) {
            // 如果grade字段不存在，设置为null
            student.setGrade(null);
        }
        student.setGradeTableKey(rs.getString("grade_table_key"));
        student.setBalance(rs.getBigDecimal("balance"));
        student.setEnrollmentYear((Integer) rs.getObject("enrollment_year"));
        student.setCreatedTime(rs.getTimestamp("created_time"));
        student.setUpdatedTime(rs.getTimestamp("updated_time"));
        return student;
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
    
    /**
     * 检查数据库表中是否存在grade字段
     */
    private boolean checkGradeFieldExists() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            // 查询表结构，检查是否有grade字段
            String sql = "SHOW COLUMNS FROM students LIKE 'grade'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            return rs.next(); // 如果找到记录，说明字段存在
        } catch (SQLException e) {
            System.err.println("检查grade字段是否存在失败: " + e.getMessage());
            return false; // 出错时假设字段不存在
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
}
