package client.ui.modules;

import client.ui.api.IModuleView;
import client.ui.integration.ModuleKeys;
import common.vo.StudentVO;
import common.vo.UserVO;
import client.net.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学生学籍档案自查模块
 * 学生可以查看自己的学籍档案、成绩单等个人信息
 */
public class StudentArchiveModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel personalInfoPanel;
    private JPanel transcriptPanel;
    private JPanel enrollmentPanel;
    
    // 个人信息组件
    private JLabel nameLabel, studentNoLabel, majorLabel, classNameLabel;
    private JLabel genderLabel, phoneLabel, emailLabel, balanceLabel;
    private JLabel enrollmentYearLabel, departmentLabel, gradeLabel;
    
    // 成绩单组件
    private JTable transcriptTable;
    private JLabel gpaLabel, totalCreditsLabel, averageScoreLabel;
    
    // 选课记录组件
    private JTable enrollmentTable;
    private JLabel enrollmentCountLabel, completedCountLabel;
    
    public StudentArchiveModule() {
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 学籍档案选项卡
        personalInfoPanel = createPersonalInfoPanel();
        tabbedPane.addTab("学籍档案", personalInfoPanel);
        
        // 成绩单选项卡
        transcriptPanel = createTranscriptPanel();
        tabbedPane.addTab("成绩单", transcriptPanel);
        
        // 选课记录选项卡
        enrollmentPanel = createEnrollmentPanel();
        tabbedPane.addTab("选课记录", enrollmentPanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * 创建学籍档案面板
     */
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("学籍档案", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
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
        phoneLabel = createInfoLabel("联系电话", "");
        emailLabel = createInfoLabel("邮箱", "");
        balanceLabel = createInfoLabel("账户余额", "");
        enrollmentYearLabel = createInfoLabel("入学年份", "");
        departmentLabel = createInfoLabel("院系", "");
        gradeLabel = createInfoLabel("年级", "");
        
        // 添加标签到面板
        addInfoRow(infoPanel, gbc, 0, nameLabel, studentNoLabel);
        addInfoRow(infoPanel, gbc, 1, majorLabel, classNameLabel);
        addInfoRow(infoPanel, gbc, 2, genderLabel, phoneLabel);
        addInfoRow(infoPanel, gbc, 3, emailLabel, balanceLabel);
        addInfoRow(infoPanel, gbc, 4, enrollmentYearLabel, departmentLabel);
        addInfoRow(infoPanel, gbc, 5, gradeLabel, new JLabel());
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // 刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新档案");
        refreshButton.addActionListener(e -> refreshPersonalInfo());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
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
        
        // 刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新成绩");
        refreshButton.addActionListener(e -> refreshTranscript());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
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
        
        // 刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新记录");
        refreshButton.addActionListener(e -> refreshEnrollments());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
     * 刷新个人信息
     */
    private void refreshPersonalInfo() {
        if (currentUser == null) return;
        
        // 模拟数据 - 实际应该从服务器获取
        nameLabel.setText("<html><b>姓名:</b> " + (currentUser.getName() != null ? currentUser.getName() : "张三") + "</html>");
        studentNoLabel.setText("<html><b>学号:</b> " + (currentUser.getId() != null ? currentUser.getId() : "2021001") + "</html>");
        majorLabel.setText("<html><b>专业:</b> 计算机科学与技术</html>");
        classNameLabel.setText("<html><b>班级:</b> 计科2021-1班</html>");
        genderLabel.setText("<html><b>性别:</b> 男</html>");
        phoneLabel.setText("<html><b>联系电话:</b> " + (currentUser.getPhone() != null ? currentUser.getPhone() : "13800138001") + "</html>");
        emailLabel.setText("<html><b>邮箱:</b> " + (currentUser.getEmail() != null ? currentUser.getEmail() : "zhangsan@example.com") + "</html>");
        balanceLabel.setText("<html><b>账户余额:</b> ¥" + (currentUser.getBalance() != null ? currentUser.getBalance() : "1000.00") + "</html>");
        enrollmentYearLabel.setText("<html><b>入学年份:</b> 2021年</html>");
        departmentLabel.setText("<html><b>院系:</b> 计算机学院</html>");
        gradeLabel.setText("<html><b>年级:</b> 2021级</html>");
    }
    
    /**
     * 刷新成绩单
     */
    private void refreshTranscript() {
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
     * 刷新选课记录
     */
    private void refreshEnrollments() {
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
    
    @Override
    public String getKey() {
        return "student_archive";
    }
    
    @Override
    public String getDisplayName() {
        return "学籍档案";
    }
    
    @Override
    public String getIconPath() {
        return "icons/学籍档案.png";
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
        refreshPersonalInfo();
        refreshTranscript();
        refreshEnrollments();
    }
}
