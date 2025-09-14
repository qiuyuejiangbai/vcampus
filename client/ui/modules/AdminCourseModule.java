package client.ui.modules;

import client.ui.api.IModuleView;
import common.vo.CourseVO;
import common.vo.UserVO;
import common.vo.TeacherVO;
import client.net.ServerConnection;
import client.controller.CourseController;
import client.ui.dialogs.CourseEditDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 管理员课程管理模块
 * 管理员可以管理课程信息、查看选课情况等
 */
public class AdminCourseModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private CourseController courseController;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel courseManagePanel;
    private JPanel enrollmentManagePanel;
    
    // 课程管理组件
    private JTable courseTable;
    private JTextField searchField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton searchButton;
    private JButton addCourseButton;
    private JButton editCourseButton;
    private JButton deleteCourseButton;
    private JButton viewCourseButton;
    
    // 选课管理组件
    private JTable enrollmentTable;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> statusComboBox;
    private JButton refreshEnrollmentButton;
    
    public AdminCourseModule() {
        this.courseController = new CourseController();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 课程管理选项卡
        courseManagePanel = createCourseManagePanel();
        tabbedPane.addTab("课程管理", courseManagePanel);
        
        // 选课管理选项卡
        enrollmentManagePanel = createEnrollmentManagePanel();
        tabbedPane.addTab("选课管理", enrollmentManagePanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
        
        // 初始化数据
        refreshCourses();
    }
    
    /**
     * 创建课程管理面板
     */
    private JPanel createCourseManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // 课程表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "院系", "任课教师", "学期", "容量", "已选人数", "状态"};
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
        
        // 添加双击事件
        courseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewCourseDetail();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 创建搜索面板
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("搜索条件"));
        
        // 课程名称搜索
        panel.add(new JLabel("课程名称:"));
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        panel.add(searchField);
        
        // 院系筛选
        panel.add(new JLabel("院系:"));
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setPreferredSize(new Dimension(120, 30));
        departmentComboBox.addItem("全部");
        departmentComboBox.addItem("计算机学院");
        departmentComboBox.addItem("软件学院");
        departmentComboBox.addItem("网络学院");
        departmentComboBox.addItem("信息学院");
        panel.add(departmentComboBox);
        
        // 学期筛选
        panel.add(new JLabel("学期:"));
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(100, 30));
        semesterComboBox.addItem("全部");
        semesterComboBox.addItem("2024春季");
        semesterComboBox.addItem("2024秋季");
        semesterComboBox.addItem("2023春季");
        semesterComboBox.addItem("2023秋季");
        panel.add(semesterComboBox);
        
        // 搜索按钮
        searchButton = new JButton("搜索");
        searchButton.setBackground(new Color(0, 100, 200));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchCourses());
        panel.add(searchButton);
        
        return panel;
    }
    
    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        addCourseButton = new JButton("添加课程");
        addCourseButton.setBackground(new Color(0, 120, 0));
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.addActionListener(e -> addCourse());
        panel.add(addCourseButton);
        
        editCourseButton = new JButton("编辑课程");
        editCourseButton.setBackground(new Color(0, 100, 200));
        editCourseButton.setForeground(Color.WHITE);
        editCourseButton.addActionListener(e -> editCourse());
        panel.add(editCourseButton);
        
        deleteCourseButton = new JButton("删除课程");
        deleteCourseButton.setBackground(new Color(200, 0, 0));
        deleteCourseButton.setForeground(Color.WHITE);
        deleteCourseButton.addActionListener(e -> deleteCourse());
        panel.add(deleteCourseButton);
        
        viewCourseButton = new JButton("查看详情");
        viewCourseButton.setBackground(new Color(100, 100, 100));
        viewCourseButton.setForeground(Color.WHITE);
        viewCourseButton.addActionListener(e -> viewCourseDetail());
        panel.add(viewCourseButton);
        
        return panel;
    }
    
    /**
     * 创建选课管理面板
     */
    private JPanel createEnrollmentManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选条件"));
        
        filterPanel.add(new JLabel("课程:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 30));
        courseComboBox.addItem("全部课程");
        filterPanel.add(courseComboBox);
        
        filterPanel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>();
        statusComboBox.setPreferredSize(new Dimension(100, 30));
        statusComboBox.addItem("全部");
        statusComboBox.addItem("已选课");
        statusComboBox.addItem("已退课");
        statusComboBox.addItem("已完成");
        filterPanel.add(statusComboBox);
        
        refreshEnrollmentButton = new JButton("刷新");
        refreshEnrollmentButton.setBackground(new Color(0, 100, 200));
        refreshEnrollmentButton.setForeground(Color.WHITE);
        refreshEnrollmentButton.addActionListener(e -> refreshEnrollments());
        filterPanel.add(refreshEnrollmentButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
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
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 搜索课程
     */
    private void searchCourses() {
        String keyword = searchField.getText().trim();
        String department = (String) departmentComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();
        
        courseController.searchCourses(keyword, department, semester, new CourseController.SearchCoursesCallback() {
            @Override
            public void onSuccess(List<CourseVO> courses) {
                SwingUtilities.invokeLater(() -> {
                    updateCourseTable(courses);
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "搜索课程时发生错误：" + errorMessage,
                        "搜索失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 刷新课程列表
     */
    private void refreshCourses() {
        courseController.getAllCourses(new CourseController.GetAllCoursesCallback() {
            @Override
            public void onSuccess(List<CourseVO> courses) {
                SwingUtilities.invokeLater(() -> {
                    updateCourseTable(courses);
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
                    course.getTeacherName() != null ? course.getTeacherName() : "",
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
     * 刷新选课列表
     */
    private void refreshEnrollments() {
        // 这里应该调用选课服务获取选课数据
        // 暂时显示模拟数据
        DefaultTableModel model = (DefaultTableModel) enrollmentTable.getModel();
        model.setRowCount(0);
        
        Object[][] data = {
            {"2021001", "张三", "CS101", "数据结构", "3", "李老师", "2024春季", "2024-02-15", "已选课"},
            {"2021002", "李四", "CS102", "算法设计", "3", "王老师", "2024春季", "2024-02-16", "已选课"},
            {"2021003", "王五", "CS101", "数据结构", "3", "李老师", "2024春季", "2024-02-17", "已退课"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * 添加课程
     */
    private void addCourse() {
        // 先获取教师列表
        courseController.getAllTeachers(new CourseController.GetTeachersCallback() {
            @Override
            public void onSuccess(List<TeacherVO> teachers) {
                SwingUtilities.invokeLater(() -> {
                    // 创建课程编辑对话框
                    CourseEditDialog dialog = new CourseEditDialog((Frame) SwingUtilities.getWindowAncestor(root), null, teachers);
                    dialog.setVisible(true);
                    
                    if (dialog.isConfirmed()) {
                        CourseVO newCourse = dialog.getCourse();
                        courseController.addCourse(newCourse, new CourseController.AddCourseCallback() {
                            @Override
                            public void onSuccess(CourseVO course) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(
                                        root,
                                        "课程添加成功！",
                                        "添加成功",
                                        JOptionPane.INFORMATION_MESSAGE
                                    );
                                    refreshCourses(); // 刷新课程列表
                                });
                            }
                            
                            @Override
                            public void onFailure(String errorMessage) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(
                                        root,
                                        "添加课程失败：" + errorMessage,
                                        "添加失败",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                });
                            }
                        });
                    }
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取教师列表失败：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 编辑课程
     */
    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要编辑的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 获取选中的课程信息
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        
        // 这里需要先获取完整的课程信息，暂时使用表格中的数据创建CourseVO
        CourseVO course = new CourseVO();
        course.setCourseCode(courseCode);
        course.setCourseName((String) courseTable.getValueAt(selectedRow, 1));
        
        // 安全地解析学分
        String creditsStr = (String) courseTable.getValueAt(selectedRow, 2);
        if (creditsStr != null && !creditsStr.trim().isEmpty()) {
            try {
                course.setCredits(Integer.parseInt(creditsStr));
            } catch (NumberFormatException e) {
                course.setCredits(null);
            }
        } else {
            course.setCredits(null);
        }
        
        course.setDepartment((String) courseTable.getValueAt(selectedRow, 3));
        course.setSemester((String) courseTable.getValueAt(selectedRow, 5));
        
        // 安全地解析容量
        String capacityStr = (String) courseTable.getValueAt(selectedRow, 6);
        if (capacityStr != null && !capacityStr.trim().isEmpty()) {
            try {
                course.setCapacity(Integer.parseInt(capacityStr));
            } catch (NumberFormatException e) {
                course.setCapacity(null);
            }
        } else {
            course.setCapacity(null);
        }
        
        // 先获取教师列表
        courseController.getAllTeachers(new CourseController.GetTeachersCallback() {
            @Override
            public void onSuccess(List<TeacherVO> teachers) {
                SwingUtilities.invokeLater(() -> {
                    // 创建课程编辑对话框
                    CourseEditDialog dialog = new CourseEditDialog((Frame) SwingUtilities.getWindowAncestor(root), course, teachers);
                    dialog.setVisible(true);
                    
                    if (dialog.isConfirmed()) {
                        CourseVO updatedCourse = dialog.getCourse();
                        courseController.updateCourse(updatedCourse, new CourseController.UpdateCourseCallback() {
                            @Override
                            public void onSuccess(CourseVO course) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(
                                        root,
                                        "课程更新成功！",
                                        "更新成功",
                                        JOptionPane.INFORMATION_MESSAGE
                                    );
                                    refreshCourses(); // 刷新课程列表
                                });
                            }
                            
                            @Override
                            public void onFailure(String errorMessage) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(
                                        root,
                                        "更新课程失败：" + errorMessage,
                                        "更新失败",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                });
                            }
                        });
                    }
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取教师列表失败：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 删除课程
     */
    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要删除的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 1);
        
        int option = JOptionPane.showConfirmDialog(
            root,
            "确定要删除课程 " + courseCode + " - " + courseName + " 吗？\n删除后无法恢复！",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // 使用课程代码删除课程
            courseController.deleteCourseByCode(courseCode, new CourseController.DeleteCourseCallback() {
                @Override
                public void onSuccess() {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "课程删除成功！",
                            "删除成功",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshCourses(); // 刷新课程列表
                    });
                }
                
                @Override
                public void onFailure(String errorMessage) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "删除课程失败：" + errorMessage,
                            "删除失败",
                            JOptionPane.ERROR_MESSAGE
                        );
                    });
                }
            });
        }
    }
    
    /**
     * 查看课程详情
     */
    private void viewCourseDetail() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要查看的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 获取选中的课程信息
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 1);
        String credits = (String) courseTable.getValueAt(selectedRow, 2);
        String department = (String) courseTable.getValueAt(selectedRow, 3);
        String teacherName = (String) courseTable.getValueAt(selectedRow, 4);
        String semester = (String) courseTable.getValueAt(selectedRow, 5);
        String capacity = (String) courseTable.getValueAt(selectedRow, 6);
        String enrolledCount = (String) courseTable.getValueAt(selectedRow, 7);
        String status = (String) courseTable.getValueAt(selectedRow, 8);
        
        // 构建详情信息
        StringBuilder detail = new StringBuilder();
        detail.append("课程详情信息\n");
        detail.append("==================\n\n");
        detail.append("课程代码：").append(courseCode).append("\n");
        detail.append("课程名称：").append(courseName).append("\n");
        detail.append("学分：").append(credits).append("\n");
        detail.append("院系：").append(department).append("\n");
        detail.append("任课教师：").append(teacherName).append("\n");
        detail.append("学期：").append(semester).append("\n");
        detail.append("容量：").append(capacity).append("\n");
        detail.append("已选人数：").append(enrolledCount).append("\n");
        detail.append("状态：").append(status).append("\n");
        
        JOptionPane.showMessageDialog(
            root,
            detail.toString(),
            "课程详情",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public String getKey() {
        return "admin_course";
    }
    
    @Override
    public String getDisplayName() {
        return "课程管理";
    }
    
    @Override
    public String getIconPath() {
        return "resources/icons/课程管理.png";
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
