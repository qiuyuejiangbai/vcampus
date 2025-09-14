package client.ui.modules;

import client.ui.api.IModuleView;
import common.vo.UserVO;
import client.net.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 管理员选课管理模块
 * 从原AdminCourseModule的选课管理tab分离出来
 */
public class AdminEnrollmentManagementModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    
    // UI组件
    private JTable enrollmentTable;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> statusComboBox;
    private JButton refreshEnrollmentButton;
    private JButton exportButton;
    private JButton batchProcessButton;
    private JButton statisticsButton;
    
    public AdminEnrollmentManagementModule() {
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 筛选面板
        JPanel filterPanel = createFilterPanel();
        root.add(filterPanel, BorderLayout.NORTH);
        
        // 选课表格
        String[] columnNames = {"学号", "姓名", "课程代码", "课程名称", "学分", "任课教师", "学期", "选课时间", "状态"};
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
        root.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createButtonPanel();
        root.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建筛选面板
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("筛选条件"));
        
        panel.add(new JLabel("课程:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 30));
        courseComboBox.addItem("全部课程");
        courseComboBox.addItem("数据结构");
        courseComboBox.addItem("算法设计");
        courseComboBox.addItem("数据库原理");
        courseComboBox.addItem("计算机网络");
        panel.add(courseComboBox);
        
        panel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>();
        statusComboBox.setPreferredSize(new Dimension(100, 30));
        statusComboBox.addItem("全部");
        statusComboBox.addItem("已选课");
        statusComboBox.addItem("已退课");
        statusComboBox.addItem("已完成");
        panel.add(statusComboBox);
        
        refreshEnrollmentButton = createRoundedButton("刷新", new Color(0, 100, 200), e -> refreshEnrollments());
        panel.add(refreshEnrollmentButton);
        
        return panel;
    }
    
    /**
     * 创建操作按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // 移除了导出选课、批量处理按钮
        
        statisticsButton = createRoundedButton("选课统计", new Color(150, 0, 150), e -> showEnrollmentStatistics());
        panel.add(statisticsButton);
        
        JButton viewDetailButton = createRoundedButton("查看详情", new Color(100, 100, 100), e -> viewEnrollmentDetail());
        panel.add(viewDetailButton);
        
        return panel;
    }
    
    /**
     * 创建圆角按钮
     */
    private JButton createRoundedButton(String text, Color backgroundColor, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制圆角背景
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // 绘制文字
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
        return button;
    }
    
    /**
     * 刷新选课列表
     */
    private void refreshEnrollments() {
        // 这里应该调用选课服务获取选课数据
        // 暂时显示模拟数据
        DefaultTableModel model = (DefaultTableModel) enrollmentTable.getModel();
        model.setRowCount(0);
        
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        
        // 模拟数据 - 实际应该从服务器获取
        Object[][] data = {
            {"2021001", "张三", "CS101", "数据结构", "3", "李老师", "2024春季", "2024-02-15", "已选课"},
            {"2021002", "李四", "CS102", "算法设计", "3", "王老师", "2024春季", "2024-02-16", "已选课"},
            {"2021003", "王五", "CS101", "数据结构", "3", "李老师", "2024春季", "2024-02-17", "已退课"},
            {"2021004", "赵六", "CS103", "数据库原理", "3", "陈老师", "2024春季", "2024-02-18", "已选课"},
            {"2021005", "钱七", "CS102", "算法设计", "3", "王老师", "2024春季", "2024-02-19", "已完成"},
            {"2021006", "孙八", "CS104", "计算机网络", "3", "刘老师", "2024春季", "2024-02-20", "已选课"}
        };
        
        // 根据筛选条件过滤数据
        for (Object[] row : data) {
            boolean courseMatch = "全部课程".equals(selectedCourse) || selectedCourse.equals(row[2]);
            boolean statusMatch = "全部".equals(selectedStatus) || selectedStatus.equals(row[8]);
            
            if (courseMatch && statusMatch) {
                model.addRow(row);
            }
        }
    }
    
    
    /**
     * 显示选课统计
     */
    private void showEnrollmentStatistics() {
        // 创建统计信息对话框
        JDialog statsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(root), "选课统计信息", true);
        statsDialog.setSize(500, 400);
        statsDialog.setLocationRelativeTo(root);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 统计信息
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        
        // 总选课数
        JPanel totalPanel = createStatCard("总选课数", "1,234", new Color(0, 120, 0));
        statsPanel.add(totalPanel);
        
        // 已选课数
        JPanel enrolledPanel = createStatCard("已选课数", "1,100", new Color(0, 100, 200));
        statsPanel.add(enrolledPanel);
        
        // 已退课数
        JPanel droppedPanel = createStatCard("已退课数", "50", new Color(200, 100, 0));
        statsPanel.add(droppedPanel);
        
        // 已完成数
        JPanel completedPanel = createStatCard("已完成数", "84", new Color(150, 0, 150));
        statsPanel.add(completedPanel);
        
        // 选课率
        JPanel ratePanel = createStatCard("选课率", "89.1%", new Color(0, 150, 100));
        statsPanel.add(ratePanel);
        
        // 完成率
        JPanel completionPanel = createStatCard("完成率", "7.6%", new Color(200, 150, 0));
        statsPanel.add(completionPanel);
        
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        
        // 关闭按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = createRoundedButton("关闭", new Color(100, 100, 100), e -> statsDialog.dispose());
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        statsDialog.add(contentPanel);
        statsDialog.setVisible(true);
    }
    
    /**
     * 创建统计卡片
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 查看选课详情
     */
    private void viewEnrollmentDetail() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要查看的选课记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 获取选中的选课信息
        String studentNo = (String) enrollmentTable.getValueAt(selectedRow, 0);
        String studentName = (String) enrollmentTable.getValueAt(selectedRow, 1);
        String courseCode = (String) enrollmentTable.getValueAt(selectedRow, 2);
        String courseName = (String) enrollmentTable.getValueAt(selectedRow, 3);
        String credits = (String) enrollmentTable.getValueAt(selectedRow, 4);
        String teacherName = (String) enrollmentTable.getValueAt(selectedRow, 5);
        String semester = (String) enrollmentTable.getValueAt(selectedRow, 6);
        String enrollmentTime = (String) enrollmentTable.getValueAt(selectedRow, 7);
        String status = (String) enrollmentTable.getValueAt(selectedRow, 8);
        
        // 构建详情信息
        StringBuilder detail = new StringBuilder();
        detail.append("选课详情信息\n");
        detail.append("==================\n\n");
        detail.append("学号：").append(studentNo).append("\n");
        detail.append("姓名：").append(studentName).append("\n");
        detail.append("课程代码：").append(courseCode).append("\n");
        detail.append("课程名称：").append(courseName).append("\n");
        detail.append("学分：").append(credits).append("\n");
        detail.append("任课教师：").append(teacherName).append("\n");
        detail.append("学期：").append(semester).append("\n");
        detail.append("选课时间：").append(enrollmentTime).append("\n");
        detail.append("状态：").append(status).append("\n");
        
        JOptionPane.showMessageDialog(
            root,
            detail.toString(),
            "选课详情",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public String getKey() {
        return "admin_enrollment_management";
    }
    
    @Override
    public String getDisplayName() {
        return "选课管理";
    }
    
    @Override
    public String getIconPath() {
        return "icons/选课管理.png"; // 选课管理图标
    }
    
    @Override
    public JComponent getComponent() {
        return root;
    }
    
    @Override
    public void initContext(UserVO user, ServerConnection connection) {
        this.currentUser = user;
        this.connection = connection;
        
        // 初始化时加载数据
        refreshEnrollments();
    }
}
