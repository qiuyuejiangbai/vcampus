package server.dao.impl;

import common.vo.CourseVO;
import server.dao.CourseDAO;
import server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问实现类
 */
public class CourseDAOImpl implements CourseDAO {
    
    @Override
    public Integer insert(CourseVO course) {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department, teacher_id, " +
                    "semester, academic_year, class_time, location, description, prerequisites, " +
                    "syllabus, capacity, enrolled_count, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getDepartment());
            pstmt.setObject(5, course.getTeacherId());
            System.out.println("插入课程 - 教师ID: " + course.getTeacherId() + ", 教师姓名: " + course.getTeacherName());
            pstmt.setString(6, course.getSemester());
            pstmt.setString(7, course.getAcademicYear());
            pstmt.setString(8, course.getClassTime());
            pstmt.setString(9, course.getLocation());
            pstmt.setString(10, course.getDescription());
            pstmt.setString(11, course.getPrerequisites());
            pstmt.setString(12, course.getSyllabus());
            pstmt.setInt(13, course.getCapacity());
            pstmt.setInt(14, course.getEnrolledCount() != null ? course.getEnrolledCount() : 0);
            pstmt.setString(15, course.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean deleteById(Integer courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除课程失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean update(CourseVO course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, department = ?, " +
                    "teacher_id = ?, semester = ?, academic_year = ?, class_time = ?, location = ?, " +
                    "description = ?, prerequisites = ?, syllabus = ?, capacity = ?, enrolled_count = ?, " +
                    "status = ? WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getDepartment());
            pstmt.setObject(5, course.getTeacherId());
            System.out.println("更新课程 - 教师ID: " + course.getTeacherId() + ", 教师姓名: " + course.getTeacherName());
            pstmt.setString(6, course.getSemester());
            pstmt.setString(7, course.getAcademicYear());
            pstmt.setString(8, course.getClassTime());
            pstmt.setString(9, course.getLocation());
            pstmt.setString(10, course.getDescription());
            pstmt.setString(11, course.getPrerequisites());
            pstmt.setString(12, course.getSyllabus());
            pstmt.setInt(13, course.getCapacity());
            pstmt.setInt(14, course.getEnrolledCount() != null ? course.getEnrolledCount() : 0);
            pstmt.setString(15, course.getStatus());
            pstmt.setInt(16, course.getCourseId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新课程失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public CourseVO findById(Integer courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourseVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<CourseVO> findAll() {
        String sql = "SELECT * FROM courses ORDER BY course_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourseVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM courses";
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
            System.err.println("统计课程数量失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return 0;
    }
    
    @Override
    public boolean existsById(Integer courseId) {
        String sql = "SELECT 1 FROM courses WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查课程是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public CourseVO findByCourseCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseCode);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourseVO(rs);
            }
        } catch (SQLException e) {
            System.err.println("根据课程代码查询课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean existsByCourseCode(String courseCode) {
        String sql = "SELECT 1 FROM courses WHERE course_code = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseCode);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查课程代码是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
    
    @Override
    public List<CourseVO> findByTeacherId(Integer teacherId) {
        String sql = "SELECT * FROM courses WHERE teacher_id = ? ORDER BY course_id";
        return findByIntField(sql, teacherId);
    }
    
    @Override
    public List<CourseVO> findByDepartment(String department) {
        String sql = "SELECT * FROM courses WHERE department = ? ORDER BY course_id";
        return findByStringField(sql, department);
    }
    
    @Override
    public List<CourseVO> findBySemester(String semester) {
        String sql = "SELECT * FROM courses WHERE semester = ? ORDER BY course_id";
        return findByStringField(sql, semester);
    }
    
    @Override
    public List<CourseVO> findByStatus(Integer status) {
        String sql = "SELECT * FROM courses WHERE status = ? ORDER BY course_id";
        return findByIntField(sql, status);
    }
    
    @Override
    public List<CourseVO> findByNameLike(String courseName) {
        String sql = "SELECT * FROM courses WHERE course_name LIKE ? ORDER BY course_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + courseName + "%");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourseVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("根据课程名称模糊查询失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    @Override
    public List<CourseVO> findAllEnabled() {
        String sql = "SELECT * FROM courses WHERE status = 'active' ORDER BY course_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourseVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有启用课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    @Override
    public List<CourseVO> findAllWithTeacherName() {
        String sql = "SELECT c.*, t.name as teacher_name " +
                    "FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "ORDER BY c.course_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 先查询teachers表，看看有哪些教师
            System.out.println("=== 查询teachers表 ===");
            PreparedStatement teacherStmt = conn.prepareStatement("SELECT teacher_id, name, teacher_no FROM teachers");
            ResultSet teacherRs = teacherStmt.executeQuery();
            while (teacherRs.next()) {
                System.out.println("教师: ID=" + teacherRs.getInt("teacher_id") + 
                                 ", 姓名=" + teacherRs.getString("name") + 
                                 ", 工号=" + teacherRs.getString("teacher_no"));
            }
            teacherRs.close();
            teacherStmt.close();
            System.out.println("=== teachers表查询结束 ===");
            
            // 查询courses表中的teacher_id值
            System.out.println("=== 查询courses表teacher_id ===");
            PreparedStatement courseStmt = conn.prepareStatement("SELECT course_id, course_name, teacher_id FROM courses ORDER BY course_id");
            ResultSet courseRs = courseStmt.executeQuery();
            while (courseRs.next()) {
                System.out.println("课程: ID=" + courseRs.getInt("course_id") + 
                                 ", 名称=" + courseRs.getString("course_name") + 
                                 ", 教师ID=" + courseRs.getObject("teacher_id"));
            }
            courseRs.close();
            courseStmt.close();
            System.out.println("=== courses表查询结束 ===");
            
            // 测试LEFT JOIN查询
            System.out.println("=== 测试LEFT JOIN查询 ===");
            PreparedStatement testStmt = conn.prepareStatement("SELECT c.course_id, c.course_name, c.teacher_id, t.teacher_id as t_teacher_id, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id ORDER BY c.course_id");
            ResultSet testRs = testStmt.executeQuery();
            while (testRs.next()) {
                System.out.println("JOIN测试: 课程ID=" + testRs.getInt("course_id") + 
                                 ", 课程名=" + testRs.getString("course_name") + 
                                 ", 课程teacher_id=" + testRs.getObject("teacher_id") + 
                                 ", 教师表teacher_id=" + testRs.getObject("t_teacher_id") + 
                                 ", 教师姓名=" + testRs.getString("teacher_name"));
            }
            testRs.close();
            testStmt.close();
            System.out.println("=== LEFT JOIN测试结束 ===");
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CourseVO course = mapResultSetToCourseVO(rs);
                String teacherName = rs.getString("teacher_name");
                course.setTeacherName(teacherName);
                System.out.println("课程: " + course.getCourseName() + 
                                 ", 教师ID: " + course.getTeacherId() + 
                                 ", 教师姓名: " + teacherName + 
                                 ", 原始teacher_name: " + teacherName);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("查询所有课程（含教师姓名）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    @Override
    public CourseVO findByIdWithTeacherName(Integer courseId) {
        String sql = "SELECT c.*, t.name as teacher_name " +
                    "FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE c.course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                CourseVO course = mapResultSetToCourseVO(rs);
                course.setTeacherName(rs.getString("teacher_name"));
                return course;
            }
        } catch (SQLException e) {
            System.err.println("查询课程（含教师姓名）失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public boolean updateEnrolledCount(Integer courseId, Integer enrolledCount) {
        String sql = "UPDATE courses SET enrolled_count = ? WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrolledCount);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新课程选课人数失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean incrementEnrolledCount(Integer courseId, Integer increment) {
        String sql = "UPDATE courses SET enrolled_count = enrolled_count + ? WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, increment);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("增加课程选课人数失败: " + e.getMessage());
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 根据整数字段查询课程列表的通用方法
     */
    private List<CourseVO> findByIntField(String sql, Integer value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourseVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    /**
     * 根据字符串字段查询课程列表的通用方法
     */
    private List<CourseVO> findByStringField(String sql, String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CourseVO> courses = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourseVO(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        return courses;
    }
    
    /**
     * 将ResultSet映射为CourseVO对象
     */
    private CourseVO mapResultSetToCourseVO(ResultSet rs) throws SQLException {
        CourseVO course = new CourseVO();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setDepartment(rs.getString("department"));
        course.setTeacherId((Integer) rs.getObject("teacher_id"));
        course.setSemester(rs.getString("semester"));
        course.setAcademicYear(rs.getString("academic_year"));
        course.setClassTime(rs.getString("class_time"));
        course.setLocation(rs.getString("location"));
        course.setDescription(rs.getString("description"));
        course.setPrerequisites(rs.getString("prerequisites"));
        course.setSyllabus(rs.getString("syllabus"));
        course.setCapacity(rs.getInt("capacity"));
        course.setEnrolledCount(rs.getInt("enrolled_count"));
        course.setStatus(rs.getString("status"));
        course.setCreatedTime(rs.getTimestamp("created_time"));
        course.setUpdatedTime(rs.getTimestamp("updated_time"));
        return course;
    }
}
