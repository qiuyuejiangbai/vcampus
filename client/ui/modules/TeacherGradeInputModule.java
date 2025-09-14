package client.ui.modules;

import client.ui.api.IModuleView;
import common.vo.CourseVO;
import common.vo.EnrollmentVO;
import common.vo.GradeVO;
import common.vo.UserVO;
import client.net.ServerConnection;
import client.controller.CourseController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 教师成绩录入模块
 * 教师可以查看所教课程的学生名单，录入和修改学生成绩
 */
public class TeacherGradeInputModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private CourseController courseController;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel courseListPanel;
    private JPanel gradeInputPanel;
    
    // 课程列表组件
    private JTable courseTable;
    private JButton viewStudentsButton;
    private JButton refreshCoursesButton;
    
    // 成绩录入组件
    private JTable studentTable;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton refreshStudentsButton;
    private JButton saveGradesButton;
    private JButton calculateGradesButton;
    
    public TeacherGradeInputModule() {
        this.courseController = new CourseController();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 我的课程选项卡
        courseListPanel = createCourseListPanel();
        tabbedPane.addTab("我的课程", courseListPanel);
        
        // 成绩录入选项卡
        gradeInputPanel = createGradeInputPanel();
        tabbedPane.addTab("成绩录入", gradeInputPanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
        
        // 初始化数据
        refreshMyCourses();
    }
    
    /**
     * 创建我的课程面板
     */
    private JPanel createCourseListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 操作面板
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        operationPanel.setBorder(BorderFactory.createTitledBorder("操作"));
        
        refreshCoursesButton = new JButton("刷新");
        refreshCoursesButton.setBackground(new Color(0, 100, 200));
        refreshCoursesButton.setForeground(Color.WHITE);
        refreshCoursesButton.addActionListener(e -> refreshMyCourses());
        operationPanel.add(refreshCoursesButton);
        
        viewStudentsButton = new JButton("查看学生名单");
        viewStudentsButton.setBackground(new Color(0, 120, 0));
        viewStudentsButton.setForeground(Color.WHITE);
        viewStudentsButton.addActionListener(e -> viewStudents());
        operationPanel.add(viewStudentsButton);
        
        panel.add(operationPanel, BorderLayout.NORTH);
        
        // 课程表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "院系", "学期", "容量", "已选人数", "状态"};
        courseTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        courseTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        courseTable.setRowHeight(25);
        courseTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建成绩录入面板
     */
    private JPanel createGradeInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选条件"));
        
        filterPanel.add(new JLabel("课程:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 30));
        courseComboBox.addItem("请选择课程");
        filterPanel.add(courseComboBox);
        
        filterPanel.add(new JLabel("学期:"));
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(100, 30));
        semesterComboBox.addItem("全部");
        semesterComboBox.addItem("2024春季");
        semesterComboBox.addItem("2024秋季");
        semesterComboBox.addItem("2023春季");
        semesterComboBox.addItem("2023秋季");
        filterPanel.add(semesterComboBox);
        
        refreshStudentsButton = new JButton("刷新学生");
        refreshStudentsButton.setBackground(new Color(0, 100, 200));
        refreshStudentsButton.setForeground(Color.WHITE);
        refreshStudentsButton.addActionListener(e -> refreshStudents());
        filterPanel.add(refreshStudentsButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // 学生成绩表格
        String[] columnNames = {"学号", "姓名", "期中成绩", "期末成绩", "作业成绩", "考勤成绩", "总成绩", "等级", "状态"};
        studentTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有成绩列可编辑
                return column >= 2 && column <= 5;
            }
        });
        
        // 设置表格样式
        studentTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加表格监听器，自动计算总成绩
        studentTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() >= 2 && e.getColumn() <= 5) {
                calculateTotalGrade(e.getFirstRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createGradeButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 创建成绩操作按钮面板
     */
    private JPanel createGradeButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        calculateGradesButton = new JButton("计算总成绩");
        calculateGradesButton.setBackground(new Color(100, 100, 100));
        calculateGradesButton.setForeground(Color.WHITE);
        calculateGradesButton.addActionListener(e -> calculateAllGrades());
        panel.add(calculateGradesButton);
        
        saveGradesButton = new JButton("保存成绩");
        saveGradesButton.setBackground(new Color(0, 120, 0));
        saveGradesButton.setForeground(Color.WHITE);
        saveGradesButton.addActionListener(e -> saveGrades());
        panel.add(saveGradesButton);
        
        return panel;
    }
    
    /**
     * 刷新我的课程列表
     */
    private void refreshMyCourses() {
        courseController.getTeacherCourses(new CourseController.GetTeacherCoursesCallback() {
            @Override
            public void onSuccess(List<CourseVO> courses) {
                SwingUtilities.invokeLater(() -> {
                    updateCourseTable(courses);
                    // 更新成绩录入页面的课程下拉框
                    updateCourseComboBox(courses);
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取课程列表时发生错误：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 更新课程表格
     */
    private void updateCourseTable(List<CourseVO> courses) {
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        model.setRowCount(0); // 清空表格
        
        if (courses != null) {
            for (CourseVO course : courses) {
                Object[] row = {
                    course.getCourseCode() != null ? course.getCourseCode() : "",
                    course.getCourseName() != null ? course.getCourseName() : "",
                    course.getCredits() != null ? course.getCredits().toString() : "",
                    course.getDepartment() != null ? course.getDepartment() : "",
                    course.getSemester() != null ? course.getSemester() : "",
                    course.getCapacity() != null ? course.getCapacity().toString() : "",
                    course.getEnrolledCount() != null ? course.getEnrolledCount().toString() : "0",
                    course.getStatusName() != null ? course.getStatusName() : ""
                };
                model.addRow(row);
            }
        }
    }
    
    /**
     * 更新课程下拉框
     */
    private void updateCourseComboBox(List<CourseVO> courses) {
        courseComboBox.removeAllItems();
        courseComboBox.addItem("请选择课程");
        
        if (courses != null) {
            for (CourseVO course : courses) {
                courseComboBox.addItem(course.getCourseCode() + " - " + course.getCourseName());
            }
        }
    }
    
    /**
     * 查看学生名单
     */
    private void viewStudents() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要查看的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 1);
        
        // 使用网络请求获取课程学生名单
        courseController.getCourseStudents(courseCode, new CourseController.GetCourseStudentsCallback() {
            @Override
            public void onSuccess(List<EnrollmentVO> enrollments) {
                SwingUtilities.invokeLater(() -> {
                    if (enrollments == null || enrollments.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            root,
                            "课程 " + courseCode + " - " + courseName + " 暂无学生选课",
                            "学生名单",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    
                    // 显示学生名单
                    StringBuilder message = new StringBuilder();
                    message.append("课程：").append(courseCode).append(" - ").append(courseName).append("\n\n");
                    message.append("学生名单：\n");
                    
                    for (int i = 0; i < enrollments.size(); i++) {
                        EnrollmentVO enrollment = enrollments.get(i);
                        message.append(i + 1).append(". ")
                               .append(enrollment.getStudentNo()).append(" - ")
                               .append(enrollment.getStudentName()).append(" (")
                               .append(enrollment.getStatusName()).append(")\n");
                    }
                    
                    JOptionPane.showMessageDialog(
                        root,
                        message.toString(),
                        "学生名单",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取学生名单时发生错误：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 刷新学生列表
     */
    private void refreshStudents() {
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        if (selectedCourse == null || "请选择课程".equals(selectedCourse)) {
            JOptionPane.showMessageDialog(root, "请选择要查看的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = selectedCourse.split(" - ")[0];
        
        // 使用网络请求获取课程成绩
        courseController.getCourseGrades(courseCode, new CourseController.GetCourseGradesCallback() {
            @Override
            public void onSuccess(List<GradeVO> grades) {
                SwingUtilities.invokeLater(() -> {
                    updateStudentTable(grades);
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取学生成绩时发生错误：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 更新学生成绩表格
     */
    private void updateStudentTable(List<GradeVO> grades) {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0); // 清空表格
        
        if (grades != null) {
            for (GradeVO grade : grades) {
                Object[] row = {
                    grade.getStudentNo() != null ? grade.getStudentNo() : "",
                    grade.getStudentName() != null ? grade.getStudentName() : "",
                    grade.getMidtermGrade() != null ? grade.getMidtermGrade().toString() : "",
                    grade.getFinalGrade() != null ? grade.getFinalGrade().toString() : "",
                    grade.getAssignmentGrade() != null ? grade.getAssignmentGrade().toString() : "",
                    grade.getAttendanceGrade() != null ? grade.getAttendanceGrade().toString() : "",
                    grade.getTotalGrade() != null ? grade.getTotalGrade().toString() : "",
                    grade.getGradeLevel() != null ? grade.getGradeLevel() : "",
                    grade.getGradeStatus() != null ? grade.getGradeStatus() : "未评分"
                };
                model.addRow(row);
            }
        }
    }
    
    /**
     * 计算单行总成绩
     */
    private void calculateTotalGrade(int row) {
        try {
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            
            // 获取各项成绩
            String midtermStr = (String) model.getValueAt(row, 2);
            String finalStr = (String) model.getValueAt(row, 3);
            String assignmentStr = (String) model.getValueAt(row, 4);
            String attendanceStr = (String) model.getValueAt(row, 5);
            
            // 转换为数值
            BigDecimal midterm = parseGrade(midtermStr);
            BigDecimal finalGrade = parseGrade(finalStr);
            BigDecimal assignment = parseGrade(assignmentStr);
            BigDecimal attendance = parseGrade(attendanceStr);
            
            // 计算总成绩（权重：期中30%，期末50%，作业15%，考勤5%）
            BigDecimal total = BigDecimal.ZERO;
            if (midterm != null) total = total.add(midterm.multiply(new BigDecimal("0.3")));
            if (finalGrade != null) total = total.add(finalGrade.multiply(new BigDecimal("0.5")));
            if (assignment != null) total = total.add(assignment.multiply(new BigDecimal("0.15")));
            if (attendance != null) total = total.add(attendance.multiply(new BigDecimal("0.05")));
            
            // 更新总成绩
            model.setValueAt(total.toString(), row, 6);
            
            // 计算等级
            String gradeLevel = calculateGradeLevel(total);
            model.setValueAt(gradeLevel, row, 7);
            
            // 更新状态
            String status = calculateGradeStatus(total);
            model.setValueAt(status, row, 8);
            
        } catch (Exception e) {
            // 忽略计算错误
        }
    }
    
    /**
     * 计算所有学生的总成绩
     */
    private void calculateAllGrades() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int rowCount = model.getRowCount();
        
        for (int i = 0; i < rowCount; i++) {
            calculateTotalGrade(i);
        }
        
        JOptionPane.showMessageDialog(
            root,
            "已计算所有学生的总成绩！",
            "计算完成",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * 保存成绩
     */
    private void saveGrades() {
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        if (selectedCourse == null || "请选择课程".equals(selectedCourse)) {
            JOptionPane.showMessageDialog(root, "请选择要保存成绩的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = selectedCourse.split(" - ")[0];
        
        int option = JOptionPane.showConfirmDialog(
            root,
            "确定要保存课程 " + courseCode + " 的所有成绩吗？",
            "确认保存",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            int rowCount = model.getRowCount();
            
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(root, "没有成绩数据需要保存", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 使用计数器跟踪保存进度
            final int[] savedCount = {0};
            final int[] totalCount = {rowCount};
            
            for (int i = 0; i < rowCount; i++) {
                String studentNo = (String) model.getValueAt(i, 0);
                String midtermStr = (String) model.getValueAt(i, 2);
                String finalStr = (String) model.getValueAt(i, 3);
                String assignmentStr = (String) model.getValueAt(i, 4);
                String attendanceStr = (String) model.getValueAt(i, 5);
                String totalStr = (String) model.getValueAt(i, 6);
                
                // 创建成绩对象
                GradeVO grade = new GradeVO();
                grade.setStudentNo(studentNo);
                grade.setCourseCode(courseCode);
                grade.setMidtermGrade(parseGrade(midtermStr));
                grade.setFinalGrade(parseGrade(finalStr));
                grade.setAssignmentGrade(parseGrade(assignmentStr));
                grade.setAttendanceGrade(parseGrade(attendanceStr));
                grade.setTotalGrade(parseGrade(totalStr));
                grade.setGradeLevel(calculateGradeLevel(parseGrade(totalStr)));
                
                // 使用网络请求保存成绩
                courseController.saveGrade(grade, new CourseController.SaveGradeCallback() {
                    @Override
                    public void onSuccess() {
                        savedCount[0]++;
                        // 检查是否所有成绩都已保存完成
                        if (savedCount[0] + (totalCount[0] - savedCount[0]) == totalCount[0]) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                    root,
                                    "成绩保存完成！\n成功保存 " + savedCount[0] + " 条记录",
                                    "保存成功",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            });
                        }
                    }
                    
                    @Override
                    public void onFailure(String errorMessage) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                root,
                                "保存成绩时发生错误：" + errorMessage,
                                "保存失败",
                                JOptionPane.ERROR_MESSAGE
                            );
                        });
                    }
                });
            }
        }
    }
    
    /**
     * 解析成绩字符串为BigDecimal
     */
    private BigDecimal parseGrade(String gradeStr) {
        if (gradeStr == null || gradeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(gradeStr.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 根据总成绩计算等级
     */
    private String calculateGradeLevel(BigDecimal totalGrade) {
        if (totalGrade == null) return "";
        
        double score = totalGrade.doubleValue();
        if (score >= 95) return "A+";
        else if (score >= 90) return "A";
        else if (score >= 85) return "B+";
        else if (score >= 80) return "B";
        else if (score >= 75) return "C+";
        else if (score >= 70) return "C";
        else if (score >= 65) return "D+";
        else if (score >= 60) return "D";
        else return "F";
    }
    
    /**
     * 根据总成绩计算状态
     */
    private String calculateGradeStatus(BigDecimal totalGrade) {
        if (totalGrade == null) return "未评分";
        
        double score = totalGrade.doubleValue();
        if (score >= 90) return "优秀";
        else if (score >= 60) return "及格";
        else return "不及格";
    }
    
    @Override
    public String getKey() {
        return "teacher_grade_input";
    }
    
    @Override
    public String getDisplayName() {
        return "成绩录入";
    }
    
    @Override
    public String getIconPath() {
        return "resources/icons/成绩录入.png";
    }
    
    @Override
    public JComponent getComponent() {
        return root;
    }
    
    @Override
    public void initContext(UserVO user, ServerConnection connection) {
        this.currentUser = user;
        this.connection = connection;
    }
}
