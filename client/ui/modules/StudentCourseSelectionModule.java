package client.ui.modules;

import client.ui.api.IModuleView;
import common.vo.CourseVO;
import common.vo.EnrollmentVO;
import common.vo.UserVO;
import client.net.ServerConnection;
import client.controller.CourseController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 学生选课模块
 * 学生可以浏览课程、选课、退课、查看已选课程等
 */
public class StudentCourseSelectionModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private CourseController courseController;
    
    // UI组件
    private JTabbedPane tabbedPane;
    private JPanel courseBrowsePanel;
    private JPanel myCoursesPanel;
    
    // 课程浏览组件
    private JTable courseTable;
    private JTextField searchField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton searchButton;
    private JButton selectCourseButton;
    private JButton viewCourseButton;
    
    // 我的课程组件
    private JTable myCourseTable;
    private JButton dropCourseButton;
    private JButton refreshMyCoursesButton;
    
    public StudentCourseSelectionModule() {
        this.courseController = new CourseController();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建选项卡
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        
        // 课程浏览选项卡
        courseBrowsePanel = createCourseBrowsePanel();
        tabbedPane.addTab("课程浏览", courseBrowsePanel);
        
        // 我的课程选项卡
        myCoursesPanel = createMyCoursesPanel();
        tabbedPane.addTab("我的课程", myCoursesPanel);
        
        root.add(tabbedPane, BorderLayout.CENTER);
        
        // 初始化数据
        refreshCourses();
        refreshMyCourses();
    }
    
    /**
     * 创建课程浏览面板
     */
    private JPanel createCourseBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // 课程表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "院系", "任课教师", "学期", "容量", "已选人数", "剩余容量", "状态"};
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
        JPanel buttonPanel = createCourseButtonPanel();
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
     * 创建课程操作按钮面板
     */
    private JPanel createCourseButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        selectCourseButton = new JButton("选课");
        selectCourseButton.setBackground(new Color(0, 120, 0));
        selectCourseButton.setForeground(Color.WHITE);
        selectCourseButton.addActionListener(e -> selectCourse());
        panel.add(selectCourseButton);
        
        viewCourseButton = new JButton("查看详情");
        viewCourseButton.setBackground(new Color(100, 100, 100));
        viewCourseButton.setForeground(Color.WHITE);
        viewCourseButton.addActionListener(e -> viewCourseDetail());
        panel.add(viewCourseButton);
        
        return panel;
    }
    
    /**
     * 创建我的课程面板
     */
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 操作面板
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        operationPanel.setBorder(BorderFactory.createTitledBorder("操作"));
        
        refreshMyCoursesButton = new JButton("刷新");
        refreshMyCoursesButton.setBackground(new Color(0, 100, 200));
        refreshMyCoursesButton.setForeground(Color.WHITE);
        refreshMyCoursesButton.addActionListener(e -> refreshMyCourses());
        operationPanel.add(refreshMyCoursesButton);
        
        dropCourseButton = new JButton("退课");
        dropCourseButton.setBackground(new Color(200, 0, 0));
        dropCourseButton.setForeground(Color.WHITE);
        dropCourseButton.addActionListener(e -> dropCourse());
        operationPanel.add(dropCourseButton);
        
        panel.add(operationPanel, BorderLayout.NORTH);
        
        // 我的课程表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "任课教师", "学期", "选课时间", "状态"};
        myCourseTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 只读表格
            }
        });
        
        // 设置表格样式
        myCourseTable.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        myCourseTable.setRowHeight(25);
        myCourseTable.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        myCourseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(myCourseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 搜索课程
     */
    private void searchCourses() {
        // 暂时使用刷新课程列表，后续可以实现服务器端搜索
        refreshCourses();
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
                    course.getAvailableCapacity() != null ? course.getAvailableCapacity().toString() : "0",
                    course.getStatusName() != null ? course.getStatusName() : ""
                };
                model.addRow(row);
            }
        }
    }
    
    /**
     * 刷新我的课程列表
     */
    private void refreshMyCourses() {
        courseController.getMyCourses(new CourseController.GetMyCoursesCallback() {
            @Override
            public void onSuccess(List<EnrollmentVO> enrollments) {
                SwingUtilities.invokeLater(() -> {
                    updateMyCourseTable(enrollments);
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取我的课程时发生错误：" + errorMessage,
                        "获取失败",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * 更新我的课程表格
     */
    private void updateMyCourseTable(List<EnrollmentVO> enrollments) {
        DefaultTableModel model = (DefaultTableModel) myCourseTable.getModel();
        model.setRowCount(0); // 清空表格
        
        if (enrollments != null) {
            for (EnrollmentVO enrollment : enrollments) {
                Object[] row = {
                    enrollment.getCourseCode() != null ? enrollment.getCourseCode() : "",
                    enrollment.getCourseName() != null ? enrollment.getCourseName() : "",
                    enrollment.getCredits() != null ? enrollment.getCredits().toString() : "",
                    enrollment.getTeacherName() != null ? enrollment.getTeacherName() : "",
                    enrollment.getSemester() != null ? enrollment.getSemester() : "",
                    enrollment.getEnrollmentTime() != null ? enrollment.getEnrollmentTime().toString().substring(0, 10) : "",
                    enrollment.getStatusName() != null ? enrollment.getStatusName() : ""
                };
                model.addRow(row);
            }
        }
    }
    
    /**
     * 选课
     */
    private void selectCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要选课的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 1);
        String availableCapacity = (String) courseTable.getValueAt(selectedRow, 8);
        
        // 检查课程是否已满
        if ("0".equals(availableCapacity)) {
            JOptionPane.showMessageDialog(
                root,
                "课程 " + courseCode + " - " + courseName + " 已满，无法选课！",
                "选课失败",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(
            root,
            "确定要选择课程 " + courseCode + " - " + courseName + " 吗？",
            "确认选课",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            courseController.enrollCourse(courseCode, new CourseController.EnrollCourseCallback() {
                @Override
                public void onSuccess() {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "选课成功！\n课程: " + courseCode + " - " + courseName,
                            "选课成功",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // 刷新列表
                        refreshCourses();
                        refreshMyCourses();
                    });
                }
                
                @Override
                public void onFailure(String errorMessage) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "选课失败：" + errorMessage,
                            "选课失败",
                            JOptionPane.ERROR_MESSAGE
                        );
                    });
                }
            });
        }
    }
    
    /**
     * 退课
     */
    private void dropCourse() {
        int selectedRow = myCourseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(root, "请选择要退课的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) myCourseTable.getValueAt(selectedRow, 0);
        String courseName = (String) myCourseTable.getValueAt(selectedRow, 1);
        String status = (String) myCourseTable.getValueAt(selectedRow, 6);
        
        // 检查课程状态
        if ("已退课".equals(status) || "已完成".equals(status)) {
            JOptionPane.showMessageDialog(
                root,
                "该课程状态为 " + status + "，无法退课！",
                "退课失败",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // 获取退课原因
        String reason = JOptionPane.showInputDialog(
            root,
            "请输入退课原因：",
            "退课原因",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                root,
                "退课原因不能为空！",
                "退课失败",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(
            root,
            "确定要退选课程 " + courseCode + " - " + courseName + " 吗？\n退课原因：" + reason,
            "确认退课",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            courseController.dropCourse(courseCode, reason, new CourseController.DropCourseCallback() {
                @Override
                public void onSuccess() {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "退课成功！\n课程: " + courseCode + " - " + courseName,
                            "退课成功",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // 刷新列表
                        refreshCourses();
                        refreshMyCourses();
                    });
                }
                
                @Override
                public void onFailure(String errorMessage) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            root,
                            "退课失败：" + errorMessage,
                            "退课失败",
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
        
        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 1);
        String credits = (String) courseTable.getValueAt(selectedRow, 2);
        String department = (String) courseTable.getValueAt(selectedRow, 3);
        String teacher = (String) courseTable.getValueAt(selectedRow, 4);
        String semester = (String) courseTable.getValueAt(selectedRow, 5);
        String capacity = (String) courseTable.getValueAt(selectedRow, 6);
        String enrolled = (String) courseTable.getValueAt(selectedRow, 7);
        String available = (String) courseTable.getValueAt(selectedRow, 8);
        String status = (String) courseTable.getValueAt(selectedRow, 9);
        
        String message = "课程详情：\n\n" +
                        "课程代码: " + courseCode + "\n" +
                        "课程名称: " + courseName + "\n" +
                        "学分: " + credits + "\n" +
                        "院系: " + department + "\n" +
                        "任课教师: " + teacher + "\n" +
                        "学期: " + semester + "\n" +
                        "容量: " + capacity + "\n" +
                        "已选人数: " + enrolled + "\n" +
                        "剩余容量: " + available + "\n" +
                        "状态: " + status;
        
        JOptionPane.showMessageDialog(
            root,
            message,
            "课程详情",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public String getKey() {
        return "student_course_selection";
    }
    
    @Override
    public String getDisplayName() {
        return "选课管理";
    }
    
    @Override
    public String getIconPath() {
        return "resources/icons/选课管理.png";
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
