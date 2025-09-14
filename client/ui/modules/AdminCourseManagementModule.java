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
 * 从原AdminCourseModule的课程管理tab分离出来
 */
public class AdminCourseManagementModule implements IModuleView {
    private JPanel root;
    private UserVO currentUser;
    private ServerConnection connection;
    private CourseController courseController;
    
    // UI组件
    private JTable courseTable;
    private JTextField searchField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton searchButton;
    private JButton addCourseButton;
    private JButton editCourseButton;
    private JButton deleteCourseButton;
    private JButton viewCourseButton;
    private JButton refreshButton;
    
    public AdminCourseManagementModule() {
        this.courseController = new CourseController();
        buildUI();
    }
    
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 搜索面板
        JPanel searchPanel = createSearchPanel();
        root.add(searchPanel, BorderLayout.NORTH);
        
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
        root.add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = createButtonPanel();
        root.add(buttonPanel, BorderLayout.SOUTH);
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
        searchButton = createRoundedButton("搜索", new Color(0, 100, 200), e -> searchCourses());
        panel.add(searchButton);
        
        return panel;
    }
    
    /**
     * 创建操作按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        addCourseButton = createRoundedButton("添加课程", new Color(0, 120, 0), e -> addCourse());
        panel.add(addCourseButton);
        
        editCourseButton = createRoundedButton("编辑课程", new Color(0, 100, 200), e -> editCourse());
        panel.add(editCourseButton);
        
        deleteCourseButton = createRoundedButton("删除课程", new Color(200, 0, 0), e -> deleteCourse());
        panel.add(deleteCourseButton);
        
        viewCourseButton = createRoundedButton("查看详情", new Color(100, 100, 100), e -> viewCourseDetail());
        panel.add(viewCourseButton);
        
        refreshButton = createRoundedButton("刷新", new Color(150, 150, 150), e -> refreshCourses());
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
            System.out.println("更新课程表格，课程数量: " + courses.size());
            for (CourseVO course : courses) {
                System.out.println("课程: " + course.getCourseName() + 
                                 ", 教师ID: " + course.getTeacherId() + 
                                 ", 教师姓名: " + course.getTeacherName());
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
     * 添加课程
     */
    private void addCourse() {
        // 先获取教师列表
        courseController.getAllTeachers(new CourseController.GetTeachersCallback() {
            @Override
            public void onSuccess(List<TeacherVO> teachers) {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("获取到教师列表，大小: " + (teachers != null ? teachers.size() : 0));
                    if (teachers != null) {
                        for (TeacherVO teacher : teachers) {
                            System.out.println("教师: " + teacher.getName() + " (" + teacher.getTeacherNo() + ")");
                        }
                    }
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
        
        // 先通过课程代码获取完整的课程信息
        courseController.getCourseByCode(courseCode, new CourseController.GetCourseByCodeCallback() {
            @Override
            public void onSuccess(CourseVO course) {
                SwingUtilities.invokeLater(() -> {
                    // 先获取教师列表
                    courseController.getAllTeachers(new CourseController.GetTeachersCallback() {
                        @Override
                        public void onSuccess(List<TeacherVO> teachers) {
                            SwingUtilities.invokeLater(() -> {
                                System.out.println("编辑课程 - 获取到教师列表，大小: " + (teachers != null ? teachers.size() : 0));
                                if (teachers != null) {
                                    for (TeacherVO teacher : teachers) {
                                        System.out.println("教师: " + teacher.getName() + " (" + teacher.getTeacherNo() + ")");
                                    }
                                }
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
                });
            }
            
            @Override
            public void onFailure(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        root,
                        "获取课程信息失败：" + errorMessage,
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
        return "admin_course_management";
    }
    
    @Override
    public String getDisplayName() {
        return "课程管理";
    }
    
    @Override
    public String getIconPath() {
        return "icons/课程管理.png"; // 课程管理图标
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
        refreshCourses();
    }
}
