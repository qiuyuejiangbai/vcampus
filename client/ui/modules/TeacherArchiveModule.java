package client.ui.modules;

import client.ui.api.IModuleView;
import client.ui.dialogs.StudentDetailDialog;
import common.vo.StudentVO;
import common.vo.UserVO;
import client.net.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 教师学籍档案查询模块
 * 教师可以查询学生学籍档案、成绩单等
 */
public class TeacherArchiveModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel studentQueryPanel;
    private JPanel gradeManagePanel;
    private JPanel statisticsPanel;
    
    // 学生查询组件
    private JTable studentTable;
    private JTextField searchField;
    private JComboBox<String> majorComboBox;
    private JComboBox<String> gradeComboBox;
    private JButton searchButton;
    private JButton viewDetailButton;
    
    // 成绩管理组件
    private JTable gradeTable;
    private JComboBox<String> courseComboBox;
    private JButton updateGradeButton;
    private JButton exportGradeButton;
    
    // 统计信息组件
    private JLabel totalStudentsLabel;
    private JLabel averageGradeLabel;
    private JTable statisticsTable;
    
    public TeacherArchiveModule() {
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 学生查询选项卡
        studentQueryPanel = createStudentQueryPanel();
        tabbedPane.addTab("学生查询", studentQueryPanel);
        
        // 成绩管理选项卡
        gradeManagePanel = createGradeManagePanel();
        tabbedPane.addTab("成绩管理", gradeManagePanel);
        
        // 统计信息选项卡
        statisticsPanel = createStatisticsPanel();
        tabbedPane.addTab("统计信息", statisticsPanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * 创建学生查询面板
     */
    private JPanel createStudentQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // 学生表格
        String[] columnNames = {"学号", "姓名", "性别", "专业", "班级", "年级", "联系电话", "邮箱", "状态"};
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
        
        viewDetailButton = new JButton("查看详情");
        viewDetailButton.setBackground(new Color(0, 100, 200));
        viewDetailButton.setForeground(Color.WHITE);
        viewDetailButton.addActionListener(e -> viewStudentDetail());
        buttonPanel.add(viewDetailButton);
        
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
        
        // 年级筛选
        panel.add(new JLabel("年级:"));
        gradeComboBox = new JComboBox<>();
        gradeComboBox.addItem("全部");
        gradeComboBox.addItem("2021级");
        gradeComboBox.addItem("2022级");
        gradeComboBox.addItem("2023级");
        gradeComboBox.addItem("2024级");
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
        
        // 标题和课程选择
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("成绩管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 课程选择和操作按钮
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.add(new JLabel("选择课程:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 30));
        courseComboBox.addActionListener(e -> refreshGrades());
        controlPanel.add(courseComboBox);
        
        updateGradeButton = new JButton("更新成绩");
        updateGradeButton.setBackground(new Color(0, 120, 0));
        updateGradeButton.setForeground(Color.WHITE);
        updateGradeButton.addActionListener(e -> updateGrades());
        controlPanel.add(updateGradeButton);
        
        exportGradeButton = new JButton("导出成绩");
        exportGradeButton.addActionListener(e -> exportGrades());
        controlPanel.add(exportGradeButton);
        
        headerPanel.add(controlPanel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // 成绩表格
        String[] columnNames = {"学号", "姓名", "期中成绩", "期末成绩", "作业成绩", "考勤成绩", "总成绩", "等级"};
        gradeTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有成绩列可编辑
                return column >= 2 && column <= 5;
            }
        });
        
        // 设置表格样式
        gradeTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        gradeTable.setRowHeight(25);
        gradeTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加表格监听器，自动计算总成绩
        gradeTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() >= 2 && e.getColumn() <= 5) {
                calculateTotalGrade(e.getFirstRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 说明文本
        JLabel helpLabel = new JLabel("<html><b>说明:</b> 请在各成绩列输入0-100的分数，系统会自动计算总成绩和等级</html>");
        helpLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        helpLabel.setForeground(Color.GRAY);
        helpLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(helpLabel, BorderLayout.SOUTH);
        
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // 总学生数
        JPanel totalPanel = createStatCard("教授学生数", "156", new Color(0, 120, 0));
        statsPanel.add(totalPanel);
        
        // 平均成绩
        JPanel gradePanel = createStatCard("平均成绩", "85.6", new Color(0, 100, 200));
        statsPanel.add(gradePanel);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 详细统计表格
        String[] columnNames = {"课程", "选课人数", "平均分", "及格率", "优秀率"};
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
        String grade = (String) gradeComboBox.getSelectedItem();
        
        // 这里应该调用服务器接口搜索学生
        refreshStudents();
    }
    
    /**
     * 刷新学生列表
     */
    private void refreshStudents() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0); // 清空表格
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"2021001", "张三", "男", "计算机科学与技术", "计科2021-1", "2021级", "13800138001", "zhangsan@example.com", "正常"},
            {"2021002", "李四", "女", "计算机科学与技术", "计科2021-1", "2021级", "13800138002", "lisi@example.com", "正常"},
            {"2021003", "王五", "男", "软件工程", "软工2021-1", "2021级", "13800138003", "wangwu@example.com", "正常"},
            {"2022001", "赵六", "女", "网络工程", "网工2022-1", "2022级", "13800138004", "zhaoliu@example.com", "正常"},
            {"2022002", "钱七", "男", "信息安全", "信安2022-1", "2022级", "13800138005", "qianqi@example.com", "正常"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * 刷新成绩列表
     */
    private void refreshGrades() {
        DefaultTableModel model = (DefaultTableModel) gradeTable.getModel();
        model.setRowCount(0); // 清空表格
        
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        if (selectedCourse == null) return;
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"2021001", "张三", 85, 90, 88, 95, 89.5, "B+"},
            {"2021002", "李四", 78, 82, 80, 90, 82.5, "B-"},
            {"2021003", "王五", 92, 95, 93, 98, 94.5, "A"},
            {"2021004", "赵六", 88, 85, 87, 92, 88.0, "B+"}
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
            {"程序设计基础", "45", "85.6", "95.6%", "22.2%"},
            {"数据结构", "38", "82.3", "92.1%", "18.4%"},
            {"算法设计", "25", "88.9", "96.0%", "28.0%"},
            {"数据库原理", "30", "86.2", "93.3%", "26.7%"}
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
            double midterm = getDoubleValue(model.getValueAt(row, 2));
            double finalExam = getDoubleValue(model.getValueAt(row, 3));
            double assignment = getDoubleValue(model.getValueAt(row, 4));
            double attendance = getDoubleValue(model.getValueAt(row, 5));
            
            // 计算总成绩（权重：期中30%，期末40%，作业20%，考勤10%）
            double total = midterm * 0.3 + finalExam * 0.4 + assignment * 0.2 + attendance * 0.1;
            
            // 设置总成绩
            model.setValueAt(String.format("%.1f", total), row, 6);
            
            // 计算等级
            String grade = calculateGradeLevel(total);
            model.setValueAt(grade, row, 7);
            
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
    
    /**
     * 导出成绩
     */
    private void exportGrades() {
        // 这里应该实现成绩导出功能
        JOptionPane.showMessageDialog(root, "导出成绩功能", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public String getKey() {
        return "teacher_archive";
    }
    
    @Override
    public String getDisplayName() {
        return "学籍查询";
    }
    
    @Override
    public String getIconPath() {
        return "icons/学籍查询.png";
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
        
        // 初始化课程下拉框
        courseComboBox.addItem("程序设计基础");
        courseComboBox.addItem("数据结构");
        courseComboBox.addItem("算法设计");
        courseComboBox.addItem("数据库原理");
    }
}
