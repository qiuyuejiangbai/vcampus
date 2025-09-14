package client.ui.dialogs;

import common.vo.CourseVO;
import common.vo.TeacherVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程编辑对话框
 * 用于添加和编辑课程信息
 */
public class CourseEditDialog extends JDialog {
    private CourseVO course;
    private boolean confirmed = false;
    private List<TeacherVO> teachers; // 存储教师列表
    
    // UI组件
    private JTextField courseCodeField;
    private JTextField courseNameField;
    private JTextField creditsField;
    private JTextField departmentField;
    private JComboBox<String> teacherComboBox;
    private JTextField semesterField;
    private JTextField capacityField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusComboBox;
    
    // 按钮
    private JButton confirmButton;
    private JButton cancelButton;
    
    public CourseEditDialog(Frame parent, CourseVO course, List<TeacherVO> teachers) {
        super(parent, course == null ? "添加课程" : "编辑课程", true);
        this.course = course;
        this.teachers = teachers; // 存储教师列表
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        buildUI(teachers);
        if (course != null) {
            loadCourseData();
        }
    }
    
    private void buildUI(List<TeacherVO> teachers) {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 课程代码
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("课程代码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        courseCodeField = new JTextField(20);
        courseCodeField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(courseCodeField, gbc);
        
        // 课程名称
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("课程名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        courseNameField = new JTextField(20);
        courseNameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(courseNameField, gbc);
        
        // 学分
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("学分:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        creditsField = new JTextField(20);
        creditsField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(creditsField, gbc);
        
        // 院系
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("院系:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        departmentField = new JTextField(20);
        departmentField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(departmentField, gbc);
        
        // 任课教师
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("任课教师:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        teacherComboBox = new JComboBox<>();
        teacherComboBox.setPreferredSize(new Dimension(200, 30));
        teacherComboBox.addItem("请选择教师");
        if (teachers != null) {
            System.out.println("教师列表大小: " + teachers.size());
            for (TeacherVO teacher : teachers) {
                System.out.println("教师: " + teacher.getName() + " (" + teacher.getTeacherNo() + ")");
                teacherComboBox.addItem(teacher.getName() + " (" + teacher.getTeacherNo() + ")");
            }
        } else {
            System.out.println("教师列表为null");
        }
        mainPanel.add(teacherComboBox, gbc);
        
        // 学期
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("学期:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        semesterField = new JTextField(20);
        semesterField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(semesterField, gbc);
        
        // 容量
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("容量:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        capacityField = new JTextField(20);
        capacityField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(capacityField, gbc);
        
        // 状态
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        statusComboBox = new JComboBox<>();
        statusComboBox.setPreferredSize(new Dimension(200, 30));
        statusComboBox.addItem("启用");
        statusComboBox.addItem("禁用");
        mainPanel.add(statusComboBox, gbc);
        
        // 课程描述
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("课程描述:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        mainPanel.add(scrollPane, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        confirmButton = new JButton("确定");
        confirmButton.setBackground(new Color(0, 120, 0));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        confirmButton.setBorderPainted(false);
        confirmButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> confirm());
        buttonPanel.add(confirmButton);
        
        cancelButton = new JButton("取消");
        cancelButton.setBackground(new Color(150, 150, 150));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> cancel());
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCourseData() {
        if (course != null) {
            courseCodeField.setText(course.getCourseCode());
            courseNameField.setText(course.getCourseName());
            creditsField.setText(course.getCredits() != null ? course.getCredits().toString() : "");
            departmentField.setText(course.getDepartment());
            semesterField.setText(course.getSemester());
            capacityField.setText(course.getCapacity() != null ? course.getCapacity().toString() : "");
            descriptionArea.setText(course.getDescription());
            
            // 设置教师选择
            if (course.getTeacherId() != null && teachers != null) {
                // 通过teacherId找到对应的教师，然后设置正确的显示格式
                for (TeacherVO teacher : teachers) {
                    if (teacher.getId().equals(course.getTeacherId())) {
                        String teacherDisplay = teacher.getName() + " (" + teacher.getTeacherNo() + ")";
                        teacherComboBox.setSelectedItem(teacherDisplay);
                        break;
                    }
                }
            } else if (course.getTeacherName() != null) {
                // 如果没有teacherId，尝试通过姓名匹配
                for (TeacherVO teacher : teachers) {
                    if (teacher.getName().equals(course.getTeacherName())) {
                        String teacherDisplay = teacher.getName() + " (" + teacher.getTeacherNo() + ")";
                        teacherComboBox.setSelectedItem(teacherDisplay);
                        break;
                    }
                }
            }
            
            // 设置状态
            if (course.getStatus() != null) {
                if ("active".equals(course.getStatus())) {
                    statusComboBox.setSelectedIndex(0); // 启用
                } else {
                    statusComboBox.setSelectedIndex(1); // 禁用
                }
            }
        }
    }
    
    private void confirm() {
        System.out.println("=== 课程保存调试信息 ===");
        System.out.println("选择的教师: " + teacherComboBox.getSelectedItem());
        System.out.println("教师列表大小: " + (teachers != null ? teachers.size() : 0));
        
        // 验证输入
        if (courseCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入课程代码", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (courseNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入课程名称", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (!creditsField.getText().trim().isEmpty()) {
                Integer.parseInt(creditsField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "学分必须是整数", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (!capacityField.getText().trim().isEmpty()) {
                Integer.parseInt(capacityField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "容量必须是整数", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建或更新课程对象
        if (course == null) {
            course = new CourseVO();
        }
        
        course.setCourseCode(courseCodeField.getText().trim());
        course.setCourseName(courseNameField.getText().trim());
        course.setCredits(creditsField.getText().trim().isEmpty() ? null : Integer.parseInt(creditsField.getText().trim()));
        course.setDepartment(departmentField.getText().trim());
        course.setSemester(semesterField.getText().trim());
        course.setCapacity(capacityField.getText().trim().isEmpty() ? null : Integer.parseInt(capacityField.getText().trim()));
        course.setDescription(descriptionArea.getText().trim());
        course.setStatus(statusComboBox.getSelectedIndex() == 0 ? "active" : "inactive");
        
        // 设置教师ID（根据选择的教师来设置）
        String selectedTeacher = (String) teacherComboBox.getSelectedItem();
        if (selectedTeacher != null && !selectedTeacher.equals("请选择教师") && teachers != null) {
            // 从选择的教师字符串中提取工号
            String teacherNo = selectedTeacher.substring(selectedTeacher.lastIndexOf("(") + 1, selectedTeacher.lastIndexOf(")"));
            for (TeacherVO teacher : teachers) {
                if (teacher.getTeacherNo().equals(teacherNo)) {
                    course.setTeacherId(teacher.getId());
                    course.setTeacherName(teacher.getName());
                    System.out.println("设置教师信息 - ID: " + teacher.getId() + ", 姓名: " + teacher.getName() + ", 工号: " + teacher.getTeacherNo());
                    break;
                }
            }
        } else {
            course.setTeacherId(null);
            course.setTeacherName(null);
            System.out.println("未选择教师，设置为null");
        }
        
        System.out.println("最终课程信息 - 教师ID: " + course.getTeacherId() + ", 教师姓名: " + course.getTeacherName());
        System.out.println("=== 课程保存调试信息结束 ===");
        
        confirmed = true;
        dispose();
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public CourseVO getCourse() {
        return confirmed ? course : null;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
