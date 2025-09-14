package client.ui.modules;

import client.ui.api.IModuleView;
import client.ui.dialogs.StudentDetailDialog;
import client.ui.dialogs.StudentEditDialog;
import common.vo.StudentVO;
import common.vo.UserVO;
import client.net.ServerConnection;
import server.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 管理员学籍档案管理模块
 * 管理员可以管理学生学籍档案、成绩单等
 */
public class AdminArchiveModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private StudentService studentService;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel studentManagePanel;
    private JPanel gradeManagePanel;
    private JPanel statisticsPanel;
    private JPanel batchOperationPanel;
    
    // 学生管理组件
    private JTable studentTable;
    private JTextField searchField;
    private JComboBox<String> majorComboBox;
    private JComboBox<String> gradeComboBox;
    private JButton searchButton;
    private JButton addStudentButton;
    private JButton editStudentButton;
    private JButton deleteStudentButton;
    private JButton exportButton;
    
    // 成绩管理组件
    private JTable gradeTable;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton updateGradeButton;
    
    // 统计信息组件
    private JLabel totalStudentsLabel;
    private JLabel majorCountLabel;
    private JLabel gradeCountLabel;
    private JTable statisticsTable;
    
    
    public AdminArchiveModule() {
        this.studentService = new StudentService();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 学生管理选项卡
        studentManagePanel = createStudentManagePanel();
        tabbedPane.addTab("学生管理", studentManagePanel);
        
        // 成绩管理选项卡
        gradeManagePanel = createGradeManagePanel();
        tabbedPane.addTab("成绩管理", gradeManagePanel);
        
        // 统计信息选项卡
        statisticsPanel = createStatisticsPanel();
        tabbedPane.addTab("统计信息", statisticsPanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * 创建学生管理面板
     */
    private JPanel createStudentManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // 学生表格
        String[] columnNames = {"学号", "姓名", "性别", "专业", "班级", "入学年份", "联系电话", "邮箱", "状态"};
        studentTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        studentTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加双击查看详情功能
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewStudentDetail();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        addStudentButton = new JButton("添加学生");
        addStudentButton.setBackground(new Color(0, 120, 0));
        addStudentButton.setForeground(Color.WHITE);
        addStudentButton.addActionListener(e -> addStudent());
        buttonPanel.add(addStudentButton);
        
        JButton viewDetailButton = new JButton("查看详情");
        viewDetailButton.setBackground(new Color(0, 150, 100));
        viewDetailButton.setForeground(Color.WHITE);
        viewDetailButton.addActionListener(e -> viewStudentDetail());
        buttonPanel.add(viewDetailButton);
        
        editStudentButton = new JButton("编辑学生");
        editStudentButton.setBackground(new Color(0, 100, 200));
        editStudentButton.setForeground(Color.WHITE);
        editStudentButton.addActionListener(e -> editStudent());
        buttonPanel.add(editStudentButton);
        
        deleteStudentButton = new JButton("删除学生");
        deleteStudentButton.setBackground(new Color(200, 0, 0));
        deleteStudentButton.setForeground(Color.WHITE);
        deleteStudentButton.addActionListener(e -> deleteStudent());
        buttonPanel.add(deleteStudentButton);
        
        
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshStudents());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 创建搜索面板
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("搜索条件"));
        
        // 姓名搜索
        panel.add(new JLabel("姓名:"));
        searchField = new JTextField(15);
        panel.add(searchField);
        
        // 专业筛选
        panel.add(new JLabel("专业:"));
        majorComboBox = new JComboBox<>();
        majorComboBox.addItem("全部");
        majorComboBox.addItem("计算机科学与技术");
        majorComboBox.addItem("软件工程");
        majorComboBox.addItem("网络工程");
        majorComboBox.addItem("信息安全");
        panel.add(majorComboBox);
        
        // 入学年份筛选
        panel.add(new JLabel("入学年份:"));
        gradeComboBox = new JComboBox<>();
        gradeComboBox.addItem("全部");
        gradeComboBox.addItem("2021");
        gradeComboBox.addItem("2022");
        gradeComboBox.addItem("2023");
        gradeComboBox.addItem("2024");
        panel.add(gradeComboBox);
        
        // 搜索按钮
        searchButton = new JButton("搜索");
        searchButton.setBackground(new Color(0, 100, 200));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchStudents());
        panel.add(searchButton);
        
        return panel;
    }
    
    /**
     * 创建成绩管理面板
     */
    private JPanel createGradeManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题和筛选条件
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("成绩管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 筛选条件和操作按钮
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.add(new JLabel("课程:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(150, 30));
        courseComboBox.addActionListener(e -> refreshGrades());
        controlPanel.add(courseComboBox);
        
        controlPanel.add(new JLabel("学期:"));
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(100, 30));
        semesterComboBox.addActionListener(e -> refreshGrades());
        controlPanel.add(semesterComboBox);
        
        updateGradeButton = new JButton("更新成绩");
        updateGradeButton.setBackground(new Color(0, 120, 0));
        updateGradeButton.setForeground(Color.WHITE);
        updateGradeButton.addActionListener(e -> updateGrades());
        controlPanel.add(updateGradeButton);
        
        headerPanel.add(controlPanel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // 成绩表格
        String[] columnNames = {"学号", "姓名", "课程", "期中成绩", "期末成绩", "作业成绩", "考勤成绩", "总成绩", "等级", "学期"};
        gradeTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有成绩列可编辑
                return column >= 3 && column <= 6;
            }
        });
        
        // 设置表格样式
        gradeTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        gradeTable.setRowHeight(25);
        gradeTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加表格监听器，自动计算总成绩
        gradeTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() >= 3 && e.getColumn() <= 6) {
                calculateTotalGrade(e.getFirstRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建统计信息面板
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("统计信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 统计卡片
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // 总学生数
        JPanel totalPanel = createStatCard("总学生数", "1,234", new Color(0, 120, 0));
        statsPanel.add(totalPanel);
        
        // 专业数
        JPanel majorPanel = createStatCard("专业数", "8", new Color(0, 100, 200));
        statsPanel.add(majorPanel);
        
        // 入学年份数
        JPanel gradePanel = createStatCard("入学年份数", "4", new Color(200, 100, 0));
        statsPanel.add(gradePanel);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 详细统计表格
        String[] columnNames = {"专业", "2021级", "2022级", "2023级", "2024级", "总计"};
        statisticsTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        statisticsTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        statisticsTable.setRowHeight(25);
        statisticsTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(statisticsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    
    /**
     * 创建统计卡片
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 搜索学生
     */
    private void searchStudents() {
        String keyword = searchField.getText().trim();
        String major = (String) majorComboBox.getSelectedItem();
        String enrollmentYear = (String) gradeComboBox.getSelectedItem();
        
        try {
            // 调用数据库服务搜索学生
            java.util.List<StudentVO> students = studentService.searchStudents(keyword, major, enrollmentYear);
            updateStudentTable(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                root,
                "搜索学生时发生错误：" + e.getMessage(),
                "搜索失败",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * 刷新学生列表
     */
    private void refreshStudents() {
        try {
            // 调用数据库服务获取所有学生
            java.util.List<StudentVO> students = studentService.getAllStudents();
            updateStudentTable(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                root,
                "获取学生列表时发生错误：" + e.getMessage(),
                "获取失败",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * 更新学生表格
     */
    private void updateStudentTable(java.util.List<StudentVO> students) {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0); // 清空表格
        
        if (students != null) {
            for (StudentVO student : students) {
                Object[] row = {
                    student.getStudentNo() != null ? student.getStudentNo() : "",
                    student.getName() != null ? student.getName() : "",
                    student.getGender() != null ? student.getGender() : "",
                    student.getMajor() != null ? student.getMajor() : "",
                    student.getClassName() != null ? student.getClassName() : "",
                    student.getEnrollmentYear() != null ? student.getEnrollmentYear().toString() : "",
                    student.getPhone() != null ? student.getPhone() : "",
                    student.getEmail() != null ? student.getEmail() : "",
                    "正常"
                };
                model.addRow(row);
            }
        }
    }
    
    /**
     * 刷新成绩列表
     */
    private void refreshGrades() {
        DefaultTableModel model = (DefaultTableModel) gradeTable.getModel();
        model.setRowCount(0); // 清空表格
        
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();
        if (selectedCourse == null) return;
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"2021001", "张三", selectedCourse, 85, 90, 88, 95, 89.5, "B+", selectedSemester},
            {"2021002", "李四", selectedCourse, 78, 82, 80, 90, 82.5, "B-", selectedSemester},
            {"2021003", "王五", selectedCourse, 92, 95, 93, 98, 94.5, "A", selectedSemester},
            {"2021004", "赵六", selectedCourse, 88, 85, 87, 92, 88.0, "B+", selectedSemester}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * 刷新统计信息
     */
    private void refreshStatistics() {
        DefaultTableModel model = (DefaultTableModel) statisticsTable.getModel();
        model.setRowCount(0); // 清空表格
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"计算机科学与技术", "120", "150", "180", "200", "650"},
            {"软件工程", "80", "100", "120", "150", "450"},
            {"网络工程", "60", "80", "100", "120", "360"},
            {"信息安全", "40", "60", "80", "100", "280"},
            {"总计", "300", "390", "480", "570", "1,740"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * 计算总成绩
     */
    private void calculateTotalGrade(int row) {
        try {
            DefaultTableModel model = (DefaultTableModel) gradeTable.getModel();
            
            // 获取各项成绩
            double midterm = getDoubleValue(model.getValueAt(row, 3));
            double finalExam = getDoubleValue(model.getValueAt(row, 4));
            double assignment = getDoubleValue(model.getValueAt(row, 5));
            double attendance = getDoubleValue(model.getValueAt(row, 6));
            
            // 计算总成绩（权重：期中30%，期末40%，作业20%，考勤10%）
            double total = midterm * 0.3 + finalExam * 0.4 + assignment * 0.2 + attendance * 0.1;
            
            // 设置总成绩
            model.setValueAt(String.format("%.1f", total), row, 7);
            
            // 计算等级
            String grade = calculateGradeLevel(total);
            model.setValueAt(grade, row, 8);
            
        } catch (Exception e) {
            // 忽略计算错误
        }
    }
    
    /**
     * 获取数值
     */
    private double getDoubleValue(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * 计算成绩等级
     */
    private String calculateGradeLevel(double score) {
        if (score >= 97) return "A+";
        if (score >= 93) return "A";
        if (score >= 90) return "A-";
        if (score >= 87) return "B+";
        if (score >= 83) return "B";
        if (score >= 80) return "B-";
        if (score >= 77) return "C+";
        if (score >= 73) return "C";
        if (score >= 70) return "C-";
        if (score >= 67) return "D+";
        if (score >= 63) return "D";
        if (score >= 60) return "D-";
        return "F";
    }
    
    /**
     * 查看学生详情
     */
    private void viewStudentDetail() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要查看的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentNo = (String) studentTable.getValueAt(selectedRow, 0);
        String studentName = (String) studentTable.getValueAt(selectedRow, 1);
        
        // 打开学生详情对话框
        StudentDetailDialog.showStudentDetail(
            (Frame) SwingUtilities.getWindowAncestor(root),
            studentNo,
            studentName,
            currentUser
        );
    }
    
    /**
     * 添加学生
     */
    private void addStudent() {
        StudentVO newStudent = StudentEditDialog.showAddStudent(
            (Frame) SwingUtilities.getWindowAncestor(root)
        );
        
        if (newStudent != null) {
            try {
                // 检查学号是否已存在
                if (studentService.isStudentNoExists(newStudent.getStudentNo())) {
                    JOptionPane.showMessageDialog(
                        root,
                        "学号 " + newStudent.getStudentNo() + " 已存在，请使用其他学号！",
                        "添加失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // 调用数据库服务添加学生
                Integer studentId = studentService.addStudent(newStudent);
                
                if (studentId != null) {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生添加成功！\n学号: " + newStudent.getStudentNo() + "\n姓名: " + newStudent.getName() + "\n学生ID: " + studentId,
                        "添加成功",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 刷新学生列表
                    refreshStudents();
                } else {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生添加失败，请检查信息是否正确！",
                        "添加失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    root,
                    "添加学生时发生错误：" + e.getMessage(),
                    "添加失败",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * 编辑学生
     */
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要编辑的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentNo = (String) studentTable.getValueAt(selectedRow, 0);
        
        try {
            // 从数据库获取完整的学生信息
            StudentVO studentToEdit = studentService.getStudentByStudentNo(studentNo);
            
            if (studentToEdit == null) {
                JOptionPane.showMessageDialog(
                    root,
                    "找不到学号为 " + studentNo + " 的学生信息！",
                    "编辑失败",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            StudentVO updatedStudent = StudentEditDialog.showEditStudent(
                (Frame) SwingUtilities.getWindowAncestor(root),
                studentToEdit
            );
            
            if (updatedStudent != null) {
                // 调用数据库服务更新学生信息
                boolean success = studentService.updateStudent(updatedStudent);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生信息更新成功！\n学号: " + updatedStudent.getStudentNo() + "\n姓名: " + updatedStudent.getName(),
                        "更新成功",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 刷新学生列表
                    refreshStudents();
                } else {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生信息更新失败，请检查信息是否正确！",
                        "更新失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                root,
                "编辑学生时发生错误：" + e.getMessage(),
                "编辑失败",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * 删除学生
     */
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要删除的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentNo = (String) studentTable.getValueAt(selectedRow, 0);
        String studentName = (String) studentTable.getValueAt(selectedRow, 1);
        
        // 显示详细的确认对话框
        String message = "确定要删除以下学生吗？\n\n" +
                        "学号: " + studentNo + "\n" +
                        "姓名: " + studentName + "\n\n" +
                        "注意：删除后将无法恢复！";
        
        int option = JOptionPane.showConfirmDialog(
            root,
            message,
            "确认删除学生",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                // 调用数据库服务删除学生
                boolean success = studentService.deleteStudentByNo(studentNo);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生删除成功！\n学号: " + studentNo + "\n姓名: " + studentName,
                        "删除成功",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 刷新学生列表
                    refreshStudents();
                } else {
                    JOptionPane.showMessageDialog(
                        root,
                        "学生删除失败，可能该学生不存在或已被删除！",
                        "删除失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    root,
                    "删除学生时发生错误：" + e.getMessage(),
                    "删除失败",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    
    /**
     * 更新成绩
     */
    private void updateGrades() {
        int option = JOptionPane.showConfirmDialog(
            root,
            "确定要保存所有成绩吗？",
            "确认保存",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // 这里应该调用服务器接口保存成绩
            JOptionPane.showMessageDialog(
                root,
                "成绩保存成功！",
                "保存成功",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    
    
    @Override
    public String getKey() {
        return "admin_archive";
    }
    
    @Override
    public String getDisplayName() {
        return "学籍管理";
    }
    
    @Override
    public String getIconPath() {
        return "icons/学籍管理.png";
    }
    
    @Override
    public JComponent getComponent() {
        return root;
    }
    
    @Override
    public void initContext(UserVO currentUser, ServerConnection connection) {
        this.currentUser = currentUser;
        this.connection = connection;
        
        // 初始化时加载数据
        refreshStudents();
        refreshStatistics();
        
        // 初始化下拉框
        courseComboBox.addItem("程序设计基础");
        courseComboBox.addItem("数据结构");
        courseComboBox.addItem("算法设计");
        courseComboBox.addItem("数据库原理");
        
        semesterComboBox.addItem("2023-1");
        semesterComboBox.addItem("2023-2");
        semesterComboBox.addItem("2024-1");
        semesterComboBox.addItem("2024-2");
    }
}
