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
 * 管理员成绩管理模块
 * 从原AdminArchiveModule的成绩管理tab分离出来
 */
public class AdminGradeManagementModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    
    // UI组件
    private JTable gradeTable;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton updateGradeButton;
    private JButton refreshButton;
    
    public AdminGradeManagementModule() {
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题和筛选条件
        JPanel headerPanel = createHeaderPanel();
        root.add(headerPanel, BorderLayout.NORTH);
        
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
        root.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createButtonPanel();
        root.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建头部面板
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 标题
        JLabel titleLabel = new JLabel("成绩管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
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
        
        updateGradeButton = createRoundedButton("更新成绩", new Color(0, 120, 0), e -> updateGrades());
        controlPanel.add(updateGradeButton);
        
        refreshButton = createRoundedButton("刷新", new Color(100, 100, 100), e -> refreshGrades());
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建操作按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // 移除了导出成绩、导入成绩、批量更新按钮
        
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
        return "admin_grade_management";
    }
    
    @Override
    public String getDisplayName() {
        return "成绩管理";
    }
    
    @Override
    public String getIconPath() {
        return "icons/成绩管理.png"; // 成绩管理图标
    }
    
    @Override
    public JComponent getComponent() {
        return root;
    }
    
    @Override
    public void initContext(UserVO currentUser, ServerConnection connection) {
        this.currentUser = currentUser;
        this.connection = connection;
        
        // 初始化下拉框
        courseComboBox.addItem("程序设计基础");
        courseComboBox.addItem("数据结构");
        courseComboBox.addItem("算法设计");
        courseComboBox.addItem("数据库原理");
        
        semesterComboBox.addItem("2023-1");
        semesterComboBox.addItem("2023-2");
        semesterComboBox.addItem("2024-1");
        semesterComboBox.addItem("2024-2");
        
        // 初始化时加载数据
        refreshGrades();
    }
}
