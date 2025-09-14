package client.ui.dialogs;

import common.vo.StudentVO;
import common.vo.UserVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

/**
 * 学生信息编辑对话框
 * 用于添加和编辑学生信息
 */
public class StudentEditDialog extends JDialog {
    private StudentVO student;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    // 表单组件
    private JTextField nameField;
    private JTextField studentNoField;
    private JComboBox<String> genderComboBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField majorField;
    private JTextField classNameField;
    private JTextField gradeField;
    private JTextField departmentField;
    private JTextField enrollmentYearField;
    private JTextField birthDateField;
    
    public StudentEditDialog(Frame parent, StudentVO student) {
        super(parent, student == null ? "添加学生" : "编辑学生", true);
        this.student = student;
        this.isEditMode = student != null;
        
        initComponents();
        layoutComponents();
        loadStudentData();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        // 创建表单字段
        nameField = new JTextField(20);
        studentNoField = new JTextField(20);
        genderComboBox = new JComboBox<>(new String[]{"男", "女", "其他"});
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        majorField = new JTextField(20);
        classNameField = new JTextField(20);
        gradeField = new JTextField(20);
        departmentField = new JTextField(20);
        enrollmentYearField = new JTextField(20);
        birthDateField = new JTextField(20);
        
        // 设置字体
        Font fieldFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        nameField.setFont(fieldFont);
        studentNoField.setFont(fieldFont);
        genderComboBox.setFont(fieldFont);
        phoneField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        addressField.setFont(fieldFont);
        majorField.setFont(fieldFont);
        classNameField.setFont(fieldFont);
        gradeField.setFont(fieldFont);
        departmentField.setFont(fieldFont);
        enrollmentYearField.setFont(fieldFont);
        birthDateField.setFont(fieldFont);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 标题
        JLabel titleLabel = new JLabel(isEditMode ? "编辑学生信息" : "添加学生信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 添加表单字段
        addFormField(formPanel, gbc, 0, "姓名*", nameField);
        addFormField(formPanel, gbc, 1, "学号*", studentNoField);
        addFormField(formPanel, gbc, 2, "性别", genderComboBox);
        addFormField(formPanel, gbc, 3, "联系电话", phoneField);
        addFormField(formPanel, gbc, 4, "邮箱", emailField);
        addFormField(formPanel, gbc, 5, "家庭地址", addressField);
        addFormField(formPanel, gbc, 6, "专业*", majorField);
        addFormField(formPanel, gbc, 7, "班级*", classNameField);
        addFormField(formPanel, gbc, 8, "年级", gradeField);
        addFormField(formPanel, gbc, 9, "院系*", departmentField);
        addFormField(formPanel, gbc, 10, "入学年份*", enrollmentYearField);
        addFormField(formPanel, gbc, 11, "出生日期", birthDateField);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton(isEditMode ? "保存" : "添加");
        saveButton.setBackground(new Color(0, 120, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveStudent());
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }
    
    private void loadStudentData() {
        if (student != null) {
            nameField.setText(student.getName() != null ? student.getName() : "");
            studentNoField.setText(student.getStudentNo() != null ? student.getStudentNo() : "");
            genderComboBox.setSelectedItem(student.getGenderName() != null ? student.getGenderName() : "男");
            phoneField.setText(student.getPhone() != null ? student.getPhone() : "");
            emailField.setText(student.getEmail() != null ? student.getEmail() : "");
            addressField.setText(student.getAddress() != null ? student.getAddress() : "");
            majorField.setText(student.getMajor() != null ? student.getMajor() : "");
            classNameField.setText(student.getClassName() != null ? student.getClassName() : "");
            gradeField.setText(student.getGrade() != null ? student.getGrade() : "");
            departmentField.setText(student.getDepartment() != null ? student.getDepartment() : "");
            enrollmentYearField.setText(student.getEnrollmentYear() != null ? student.getEnrollmentYear().toString() : "");
            birthDateField.setText(student.getBirthDate() != null ? student.getBirthDate().toString() : "");
        }
    }
    
    private void saveStudent() {
        // 验证必填字段
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入学生姓名", "验证错误", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (studentNoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入学号", "验证错误", JOptionPane.ERROR_MESSAGE);
            studentNoField.requestFocus();
            return;
        }
        
        if (majorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入专业", "验证错误", JOptionPane.ERROR_MESSAGE);
            majorField.requestFocus();
            return;
        }
        
        if (classNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入班级", "验证错误", JOptionPane.ERROR_MESSAGE);
            classNameField.requestFocus();
            return;
        }
        
        
        if (departmentField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入院系", "验证错误", JOptionPane.ERROR_MESSAGE);
            departmentField.requestFocus();
            return;
        }
        
        if (enrollmentYearField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入入学年份", "验证错误", JOptionPane.ERROR_MESSAGE);
            enrollmentYearField.requestFocus();
            return;
        }
        
        // 验证入学年份格式
        try {
            Integer.parseInt(enrollmentYearField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "入学年份必须是数字", "验证错误", JOptionPane.ERROR_MESSAGE);
            enrollmentYearField.requestFocus();
            return;
        }
        
        // 验证邮箱格式
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "请输入正确的邮箱格式", "验证错误", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        // 创建或更新学生对象
        if (student == null) {
            student = new StudentVO();
        }
        
        student.setName(nameField.getText().trim());
        student.setStudentNo(studentNoField.getText().trim());
        student.setGender(genderComboBox.getSelectedItem().toString().equals("男") ? "male" : 
                         genderComboBox.getSelectedItem().toString().equals("女") ? "female" : "other");
        student.setPhone(phoneField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setAddress(addressField.getText().trim());
        student.setMajor(majorField.getText().trim());
        student.setClassName(classNameField.getText().trim());
        student.setGrade(gradeField.getText().trim());
        student.setDepartment(departmentField.getText().trim());
        student.setEnrollmentYear(Integer.parseInt(enrollmentYearField.getText().trim()));
        
        // 处理出生日期
        String birthDateStr = birthDateField.getText().trim();
        if (!birthDateStr.isEmpty()) {
            try {
                student.setBirthDate(Date.valueOf(birthDateStr));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "出生日期格式错误，请使用YYYY-MM-DD格式", "验证错误", JOptionPane.ERROR_MESSAGE);
                birthDateField.requestFocus();
                return;
            }
        }
        
        confirmed = true;
        dispose();
    }
    
    public StudentVO getStudent() {
        return confirmed ? student : null;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * 显示添加学生对话框
     */
    public static StudentVO showAddStudent(Frame parent) {
        StudentEditDialog dialog = new StudentEditDialog(parent, null);
        dialog.setVisible(true);
        return dialog.getStudent();
    }
    
    /**
     * 显示编辑学生对话框
     */
    public static StudentVO showEditStudent(Frame parent, StudentVO student) {
        StudentEditDialog dialog = new StudentEditDialog(parent, student);
        dialog.setVisible(true);
        return dialog.getStudent();
    }
}
