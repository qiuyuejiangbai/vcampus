package client.ui.dialogs;

import common.vo.StudentVO;
import common.vo.UserVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学生详情查看对话框
 * 显示学生的完整信息，包括学籍档案、成绩单、选课记录等
 */
public class StudentDetailDialog extends JDialog {
    private StudentVO student;
    private UserVO currentUser;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel personalInfoPanel;
    private JPanel transcriptPanel;
    private JPanel enrollmentPanel;
    private JPanel contactInfoPanel;
    
    // 个人信息组件
    private JLabel nameLabel, studentNoLabel, majorLabel, classNameLabel;
    private JLabel genderLabel, birthDateLabel, enrollmentYearLabel, departmentLabel;
    private JLabel gradeLabel, statusLabel, balanceLabel;
    
    // 联系信息组件
    private JLabel phoneLabel, emailLabel, addressLabel;
    
    // 成绩单组件
    private JTable transcriptTable;
    private JLabel gpaLabel, totalCreditsLabel, averageScoreLabel;
    
    // 选课记录组件
    private JTable enrollmentTable;
    private JLabel enrollmentCountLabel, completedCountLabel;
    
    public StudentDetailDialog(Frame parent, StudentVO student, UserVO currentUser) {
        super(parent, "学生详情 - " + (student != null ? student.getName() : ""), true);
        this.student = student;
        this.currentUser = currentUser;
        
        initComponents();
        layoutComponents();
        loadStudentData();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 个人信息选项卡
        personalInfoPanel = createPersonalInfoPanel();
        tabbedPane.addTab("个人信息", personalInfoPanel);
        
        // 联系信息选项卡
        contactInfoPanel = createContactInfoPanel();
        tabbedPane.addTab("联系信息", contactInfoPanel);
        
        // 成绩单选项卡
        transcriptPanel = createTranscriptPanel();
        tabbedPane.addTab("成绩单", transcriptPanel);
        
        // 选课记录选项卡
        enrollmentPanel = createEnrollmentPanel();
        tabbedPane.addTab("选课记录", enrollmentPanel);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 添加选项卡面板
        add(tabbedPane, BorderLayout.CENTER);
        
        // 添加按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadStudentData());
        buttonPanel.add(refreshButton);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建个人信息面板
     */
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("个人信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 信息显示区域
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 创建信息标签
        nameLabel = createInfoLabel("姓名", "");
        studentNoLabel = createInfoLabel("学号", "");
        majorLabel = createInfoLabel("专业", "");
        classNameLabel = createInfoLabel("班级", "");
        genderLabel = createInfoLabel("性别", "");
        birthDateLabel = createInfoLabel("出生日期", "");
        enrollmentYearLabel = createInfoLabel("入学年份", "");
        departmentLabel = createInfoLabel("院系", "");
        gradeLabel = createInfoLabel("年级", "");
        statusLabel = createInfoLabel("状态", "");
        balanceLabel = createInfoLabel("账户余额", "");
        
        // 添加标签到面板
        addInfoRow(infoPanel, gbc, 0, nameLabel, studentNoLabel);
        addInfoRow(infoPanel, gbc, 1, majorLabel, classNameLabel);
        addInfoRow(infoPanel, gbc, 2, genderLabel, birthDateLabel);
        addInfoRow(infoPanel, gbc, 3, enrollmentYearLabel, departmentLabel);
        addInfoRow(infoPanel, gbc, 4, gradeLabel, statusLabel);
        addInfoRow(infoPanel, gbc, 5, balanceLabel, new JLabel());
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建联系信息面板
     */
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("联系信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 信息显示区域
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 创建联系信息标签
        phoneLabel = createInfoLabel("联系电话", "");
        emailLabel = createInfoLabel("邮箱地址", "");
        addressLabel = createInfoLabel("家庭地址", "");
        
        // 添加标签到面板
        addInfoRow(infoPanel, gbc, 0, phoneLabel, emailLabel);
        addInfoRow(infoPanel, gbc, 1, addressLabel, new JLabel());
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建成绩单面板
     */
    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题和统计信息
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("成绩单", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 统计信息
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gpaLabel = new JLabel("GPA: --");
        totalCreditsLabel = new JLabel("总学分: --");
        averageScoreLabel = new JLabel("平均分: --");
        gpaLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        totalCreditsLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        averageScoreLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        gpaLabel.setForeground(new Color(0, 120, 0));
        totalCreditsLabel.setForeground(new Color(0, 120, 0));
        averageScoreLabel.setForeground(new Color(0, 120, 0));
        statsPanel.add(gpaLabel);
        statsPanel.add(totalCreditsLabel);
        statsPanel.add(averageScoreLabel);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // 成绩表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "成绩", "等级", "学期", "教师", "状态"};
        transcriptTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        transcriptTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        transcriptTable.setRowHeight(25);
        transcriptTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        transcriptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建选课记录面板
     */
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题和统计信息
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("选课记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 统计信息
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        enrollmentCountLabel = new JLabel("已选课程: --");
        completedCountLabel = new JLabel("已完成: --");
        enrollmentCountLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        completedCountLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        enrollmentCountLabel.setForeground(new Color(0, 120, 0));
        completedCountLabel.setForeground(new Color(0, 120, 0));
        statsPanel.add(enrollmentCountLabel);
        statsPanel.add(completedCountLabel);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // 选课记录表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "教师", "状态", "选课时间", "学期", "成绩"};
        enrollmentTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        enrollmentTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        enrollmentTable.setRowHeight(25);
        enrollmentTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建信息标签
     */
    private JLabel createInfoLabel(String label, String value) {
        JLabel labelComponent = new JLabel("<html><b>" + label + ":</b> " + value + "</html>");
        labelComponent.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        labelComponent.setPreferredSize(new Dimension(200, 30));
        return labelComponent;
    }
    
    /**
     * 添加信息行
     */
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label1, JLabel label2) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label1, gbc);
        
        gbc.gridx = 1;
        panel.add(label2, gbc);
    }
    
    /**
     * 加载学生数据
     */
    private void loadStudentData() {
        if (student == null) {
            // 如果没有传入学生对象，使用模拟数据
            loadMockData();
            return;
        }
        
        // 加载个人信息
        nameLabel.setText("<html><b>姓名:</b> " + (student.getName() != null ? student.getName() : "未设置") + "</html>");
        studentNoLabel.setText("<html><b>学号:</b> " + (student.getStudentNo() != null ? student.getStudentNo() : "未设置") + "</html>");
        majorLabel.setText("<html><b>专业:</b> " + (student.getMajor() != null ? student.getMajor() : "未设置") + "</html>");
        classNameLabel.setText("<html><b>班级:</b> " + (student.getClassName() != null ? student.getClassName() : "未设置") + "</html>");
        genderLabel.setText("<html><b>性别:</b> " + (student.getGenderName() != null ? student.getGenderName() : "未设置") + "</html>");
        birthDateLabel.setText("<html><b>出生日期:</b> " + (student.getBirthDate() != null ? student.getBirthDate().toString() : "未设置") + "</html>");
        enrollmentYearLabel.setText("<html><b>入学年份:</b> " + (student.getEnrollmentYear() != null ? student.getEnrollmentYear() + "年" : "未设置") + "</html>");
        departmentLabel.setText("<html><b>院系:</b> " + (student.getDepartment() != null ? student.getDepartment() : "未设置") + "</html>");
        gradeLabel.setText("<html><b>年级:</b> " + (student.getGrade() != null ? student.getGrade() : "未设置") + "</html>");
        statusLabel.setText("<html><b>状态:</b> 正常</html>");
        balanceLabel.setText("<html><b>账户余额:</b> ¥" + (student.getBalance() != null ? student.getBalance() : "0.00") + "</html>");
        
        // 加载联系信息
        if (student.getUser() != null) {
            phoneLabel.setText("<html><b>联系电话:</b> " + (student.getUser().getPhone() != null ? student.getUser().getPhone() : "未设置") + "</html>");
            emailLabel.setText("<html><b>邮箱地址:</b> " + (student.getUser().getEmail() != null ? student.getUser().getEmail() : "未设置") + "</html>");
        } else {
            phoneLabel.setText("<html><b>联系电话:</b> 未设置</html>");
            emailLabel.setText("<html><b>邮箱地址:</b> 未设置</html>");
        }
        addressLabel.setText("<html><b>家庭地址:</b> " + (student.getAddress() != null ? student.getAddress() : "未设置") + "</html>");
        
        // 加载成绩单和选课记录
        loadTranscriptData();
        loadEnrollmentData();
    }
    
    /**
     * 加载模拟数据
     */
    private void loadMockData() {
        // 模拟个人信息
        nameLabel.setText("<html><b>姓名:</b> 张三</html>");
        studentNoLabel.setText("<html><b>学号:</b> 2021001</html>");
        majorLabel.setText("<html><b>专业:</b> 计算机科学与技术</html>");
        classNameLabel.setText("<html><b>班级:</b> 计科2021-1班</html>");
        genderLabel.setText("<html><b>性别:</b> 男</html>");
        birthDateLabel.setText("<html><b>出生日期:</b> 2003-05-15</html>");
        enrollmentYearLabel.setText("<html><b>入学年份:</b> 2021年</html>");
        departmentLabel.setText("<html><b>院系:</b> 计算机学院</html>");
        gradeLabel.setText("<html><b>年级:</b> 2021级</html>");
        statusLabel.setText("<html><b>状态:</b> 正常</html>");
        balanceLabel.setText("<html><b>账户余额:</b> ¥1000.00</html>");
        
        // 模拟联系信息
        phoneLabel.setText("<html><b>联系电话:</b> 13800138001</html>");
        emailLabel.setText("<html><b>邮箱地址:</b> zhangsan@example.com</html>");
        addressLabel.setText("<html><b>家庭地址:</b> 北京市海淀区中关村大街1号</html>");
        
        // 加载成绩单和选课记录
        loadTranscriptData();
        loadEnrollmentData();
    }
    
    /**
     * 加载成绩单数据
     */
    private void loadTranscriptData() {
        DefaultTableModel model = (DefaultTableModel) transcriptTable.getModel();
        model.setRowCount(0); // 清空表格
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"CS101", "程序设计基础", 3, 85, "B+", "2023-1", "张教授", "已完成"},
            {"CS102", "数据结构", 4, 92, "A-", "2023-1", "李教授", "已完成"},
            {"CS201", "算法设计", 3, 78, "B", "2023-2", "王教授", "已完成"},
            {"CS301", "数据库原理", 3, 88, "B+", "2023-2", "赵教授", "已完成"},
            {"SE101", "软件工程", 3, 90, "A-", "2024-1", "刘教授", "进行中"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
        
        // 更新统计信息
        gpaLabel.setText("GPA: 3.25");
        totalCreditsLabel.setText("总学分: 16");
        averageScoreLabel.setText("平均分: 86.6");
    }
    
    /**
     * 加载选课记录数据
     */
    private void loadEnrollmentData() {
        DefaultTableModel model = (DefaultTableModel) enrollmentTable.getModel();
        model.setRowCount(0); // 清空表格
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"CS101", "程序设计基础", 3, "张教授", "已完成", "2023-01-15", "2023-1", "85"},
            {"CS102", "数据结构", 4, "李教授", "已完成", "2023-01-15", "2023-1", "92"},
            {"CS201", "算法设计", 3, "王教授", "已完成", "2023-02-20", "2023-2", "78"},
            {"CS301", "数据库原理", 3, "赵教授", "已完成", "2023-02-20", "2023-2", "88"},
            {"SE101", "软件工程", 3, "刘教授", "进行中", "2024-01-10", "2024-1", "--"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
        
        // 更新统计信息
        enrollmentCountLabel.setText("已选课程: 5");
        completedCountLabel.setText("已完成: 4");
    }
    
    /**
     * 显示学生详情对话框
     */
    public static void showStudentDetail(Frame parent, StudentVO student, UserVO currentUser) {
        StudentDetailDialog dialog = new StudentDetailDialog(parent, student, currentUser);
        dialog.setVisible(true);
    }
    
    /**
     * 显示学生详情对话框（使用模拟数据）
     */
    public static void showStudentDetail(Frame parent, String studentNo, String studentName, UserVO currentUser) {
        // 创建模拟学生对象
        StudentVO mockStudent = new StudentVO();
        mockStudent.setStudentNo(studentNo);
        mockStudent.setName(studentName);
        
        StudentDetailDialog dialog = new StudentDetailDialog(parent, mockStudent, currentUser);
        dialog.setVisible(true);
    }
}
