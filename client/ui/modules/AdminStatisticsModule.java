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
 * 管理员统计信息模块
 * 从原AdminArchiveModule的统计信息tab分离出来
 */
public class AdminStatisticsModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    
    // UI组件
    private JLabel totalStudentsLabel;
    private JLabel majorCountLabel;
    private JLabel gradeCountLabel;
    private JTable statisticsTable;
    private JButton refreshButton;
    private JButton exportButton;
    
    public AdminStatisticsModule() {
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("统计信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);
        
        // 统计卡片
        JPanel statsPanel = createStatsPanel();
        root.add(statsPanel, BorderLayout.CENTER);
        
        // 详细统计表格
        JPanel tablePanel = createTablePanel();
        root.add(tablePanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建统计卡片面板
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // 总学生数
        JPanel totalPanel = createStatCard("总学生数", "1,234", new Color(0, 120, 0));
        panel.add(totalPanel);
        
        // 专业数
        JPanel majorPanel = createStatCard("专业数", "8", new Color(0, 100, 200));
        panel.add(majorPanel);
        
        // 入学年份数
        JPanel gradePanel = createStatCard("入学年份数", "4", new Color(200, 100, 0));
        panel.add(gradePanel);
        
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
     * 创建表格面板
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 表格标题和操作按钮
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel tableTitleLabel = new JLabel("详细统计信息", SwingConstants.LEFT);
        tableTitleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        headerPanel.add(tableTitleLabel, BorderLayout.WEST);
        
        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        refreshButton = createRoundedButton("刷新", new Color(100, 100, 100), e -> refreshStatistics());
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
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
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        button.setPreferredSize(new Dimension(80, 35));
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
        
        // 更新统计卡片数据
        updateStatCards();
    }
    
    /**
     * 更新统计卡片数据
     */
    private void updateStatCards() {
        // 这里应该从实际数据计算
        // 暂时使用模拟数据
        if (totalStudentsLabel != null) {
            totalStudentsLabel.setText("1,234");
        }
        if (majorCountLabel != null) {
            majorCountLabel.setText("8");
        }
        if (gradeCountLabel != null) {
            gradeCountLabel.setText("4");
        }
    }
    
    
    @Override
    public String getKey() {
        return "admin_statistics";
    }
    
    @Override
    public String getDisplayName() {
        return "统计信息";
    }
    
    @Override
    public String getIconPath() {
        return "icons/统计信息.png"; // 统计信息图标
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
        refreshStatistics();
    }
}
