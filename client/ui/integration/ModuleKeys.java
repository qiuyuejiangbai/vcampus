package client.ui.integration;

/** 统一模块 Key 常量，避免拼写不一致。 */
public final class ModuleKeys {
    private ModuleKeys() {}

    // 兼容保留：HOME 旧键（尽量不再使用）
    public static final String HOME = "home";
    // 新增区分的首页键
    public static final String STUDENT_HOME = "student_home";
    public static final String TEACHER_HOME = "teacher_home";
    public static final String ADMIN_HOME = "admin_home";
    public static final String COURSE = "course";
    public static final String TIMETABLE = "timetable";
    public static final String GRADE = "grade";
    public static final String NOTICE = "notice";
    public static final String ACTIVITY = "activity";
    public static final String PROFILE = "profile";
    public static final String SETTINGS = "settings";
    public static final String LOGOUT = "logout";

    // 论坛/资源中心按角色区分
    public static final String FORUM = "forum"; // 旧键，兼容保留
    public static final String STUDENT_FORUM = "student_forum";
    public static final String TEACHER_FORUM = "teacher_forum";
    public static final String ADMIN_FORUM = "admin_forum";

    public static final String RESOURCE_CENTER = "resource_center"; // 旧键，兼容保留
    public static final String STUDENT_RESOURCE_CENTER = "student_resource_center";
    public static final String TEACHER_RESOURCE_CENTER = "teacher_resource_center";
    public static final String ADMIN_RESOURCE_CENTER = "admin_resource_center";
    
    // 学籍档案/成绩单管理模块
    public static final String STUDENT_ARCHIVE = "student_archive";
    public static final String TEACHER_ARCHIVE = "teacher_archive";
    public static final String ADMIN_ARCHIVE = "admin_archive";
    
    // 教务管理模块
    public static final String ADMIN_COURSE = "admin_course";
    public static final String STUDENT_COURSE_SELECTION = "student_course_selection";
    public static final String TEACHER_GRADE_INPUT = "teacher_grade_input";
    
    // 管理员学籍管理分离模块
    public static final String ADMIN_STUDENT_MANAGEMENT = "admin_student_management";
    public static final String ADMIN_GRADE_MANAGEMENT = "admin_grade_management";
    public static final String ADMIN_STATISTICS = "admin_statistics";
    
    // 管理员课程管理分离模块
    public static final String ADMIN_COURSE_MANAGEMENT = "admin_course_management";
    public static final String ADMIN_ENROLLMENT_MANAGEMENT = "admin_enrollment_management";
}


