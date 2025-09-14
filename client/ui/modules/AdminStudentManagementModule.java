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
 * 管理员学生管理模块
 * 从原AdminArchiveModule的学生管理tab分离出来
 */
public class AdminStudentManagementModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private StudentService studentService;
    
    // UI组件
    private JTable studentTable;
    private JTextField searchField;
    private JComboBox<String> majorComboBox;
    private JComboBox<String> gradeComboBox;
    private JButton searchButton;
    private JButton addStudentButton;
    private JButton editStudentButton;
    private JButton deleteStudentButton;
    private JButton refreshButton;
    
    public AdminStudentManagementModule() {
        this.studentService = new StudentService();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        root.add(searchPanel, BorderLayout.NORTH);
        
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
        root.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createButtonPanel();
        root.add(buttonPanel, BorderLayout.SOUTH);
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
        searchButton = createRoundedButton("搜索", new Color(0, 100, 200), e -> searchStudents());
        panel.add(searchButton);
        
        return panel;
    }
    
    /**
     * 创建操作按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        addStudentButton = createRoundedButton("添加学生", new Color(0, 120, 0), e -> addStudent());
        panel.add(addStudentButton);
        
        JButton viewDetailButton = createRoundedButton("查看详情", new Color(0, 150, 100), e -> viewStudentDetail());
        panel.add(viewDetailButton);
        
        editStudentButton = createRoundedButton("编辑学生", new Color(0, 100, 200), e -> editStudent());
        panel.add(editStudentButton);
        
        deleteStudentButton = createRoundedButton("删除学生", new Color(200, 0, 0), e -> deleteStudent());
        panel.add(deleteStudentButton);
        
        refreshButton = createRoundedButton("刷新", new Color(100, 100, 100), e -> refreshStudents());
        panel.add(refreshButton);
        
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
     * 搜索学生
     */
    private void searchStudents() {
        String keyword = searchField.getText().trim();
        String major = (String) majorComboBox.getSelectedItem();
        String enrollmentYear = (String) gradeComboBox.getSelectedItem();
        
        try {
            // 调用数据库服务搜索学生
            List<StudentVO> students = studentService.searchStudents(keyword, major, enrollmentYear);
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
            List<StudentVO> students = studentService.getAllStudents();
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
    private void updateStudentTable(List<StudentVO> students) {
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
                
                if (studentId != null && studentId > 0) {
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
                e.printStackTrace(); // 打印详细错误信息
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
    
    @Override
    public String getKey() {
        return "admin_student_management";
    }
    
    @Override
    public String getDisplayName() {
        return "学生管理";
    }
    
    @Override
    public String getIconPath() {
        return "icons/学生管理.png"; // 学生管理图标
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
    }
}
