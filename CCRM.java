import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
  enum StudentStatus { SUSPENDED, ACTIVE, UNDERGRAD, INACTIVE, GRADUATED }
   enum Grade { A, B, C, D, F }

class Student {
     private final String ID;
  private String FirstName;
  private String LastName;
      private LocalDate DateOfBirth;
private StudentStatus status;
    public Student(String id, String FirstName, String LastName, LocalDate DateOfBirth) {
      this.ID = id;
      this.FirstName = FirstName;
        this.LastName = LastName;
    this.DateOfBirth = DateOfBirth;
        this.status = StudentStatus.UNDERGRAD;}
  public String getId() { return ID; }
    public String getFullName() { return FirstName + " " + LastName; }
       public StudentStatus getStatus() { return status; }
  public void setStatus(StudentStatus status) { this.status = status; }
    @Override
    public String toString() {
        return ID + " - " + getFullName() + " (" + status + ")"; }}
class Course {
    private final String code;
    private String name;
    private int credits;
    public Course(String code, String name, int credits) {
        this.code = code; this.name = name; this.credits = credits;}
    public String getCode() { return code; }
    public int getCredits() { return credits; }
    @Override
    public String toString() { return code + " - " + name + " (" + credits + " credits)"; }}

class Enrollment {
    private final Student student;
    private final Course course;
    private Grade grade;
    public Enrollment(Student student, Course course) {
        this.student = student; this.course = course;
    }
public Student getStudent() { return student; }
    public Course getCourse() { return course; }
     public Grade getGrade() { return grade; }
  public void setGrade(Grade grade) { this.grade = grade; }
    @Override
    public String toString() {
    return student.getFullName() + " in " + course.getCode() +
                (grade != null ? " Grade: " + grade : "");
                         }}
class StudentHandler {
    private final Map<String, Student> students = new HashMap<>();
 public void createStudent(Student s) { students.put(s.getId(), s); }
      public Student findById(String id) { return students.get(id); }
   public List<Student> findAll() { return new ArrayList<>(students.values()); }
        public List<Student> searchByName(String name) {
    List<Student> result = new ArrayList<>();
    for (Student s : students.values()) {
        if (s.getFullName().toLowerCase().contains(name.toLowerCase())) {
            result.add(s);
                }}
    return result;}
    public void bulkImport(List<Student> list) { for (Student s : list) createStudent(s); }}

class CourseHandler {
    private final Map<String, Course> courses = new HashMap<>();
  public void createCourse(Course c) { courses.put(c.getCode(), c); }
public Course findByCode(String code) { return courses.get(code); }
      public List<Course> findAll() { return new ArrayList<>(courses.values()); }
       public void bulkImport(List<Course> list) { for (Course c : list) createCourse(c); }}
class EnrollmentManager{
    private final List<Enrollment> enrollments = new ArrayList<>();
      private final StudentHandler studentHandler;
private final CourseHandler courseHandler;
    public EnrollmentManager(StudentHandler s, CourseHandler c) {
        this.studentHandler = s; this.courseHandler = c;
    }
    public void enrollStudent(String stid, String coid) {
        Student s = studentHandler.findById(stid);
        Course c = courseHandler.findByCode(coid);
        if (s == null || c == null) throw new RuntimeException("Invalid student or course");
        enrollments.add(new Enrollment(s, c));
            }
    public void removeEnrollment(String stid, String coid) {
        enrollments.removeIf(e -> e.getStudent().getId().equals(stid)
                && e.getCourse().getCode().equals(coid));
                        }
       public List<Enrollment> findEnrollmentsByStudent(String stid) {
        List<Enrollment> res = new ArrayList<>();
        for (Enrollment e : enrollments) if (e.getStudent().getId().equals(stid)) res.add(e);
        return res; }
     public List<Enrollment> findEnrollmentsByCourse(String coid) {
        List<Enrollment> res = new ArrayList<>();
        for (Enrollment e : enrollments) if (e.getCourse().getCode().equals(coid)) res.add(e);
        return res;}
public void recordGrade(String stid, String coid, Grade g) {
        for (Enrollment e : enrollments) {
            if (e.getStudent().getId().equals(stid) && e.getCourse().getCode().equals(coid)) {
                e.setGrade(g); return;      } }
        throw new RuntimeException("Enrollment not found"); }
 public double calculateStudentGPA(String stid) {
        List<Enrollment> list = findEnrollmentsByStudent(stid);
        if (list.isEmpty()) return 0.0;
        double points = 0; int totalCredits = 0;
        for (Enrollment e : list) {
            if (e.getGrade() != null) {
                int gp = switch (e.getGrade()) {
                    case A -> 4; case B -> 3; case C -> 2; case D -> 1; case F -> 0;
                };
                points += gp * e.getCourse().getCredits();
                totalCredits += e.getCourse().getCredits();       } }
        return totalCredits == 0 ? 0.0 : points / totalCredits;   }}
class TranscriptService {
    private final EnrollmentManager enrollmentService;
       public TranscriptService(EnrollmentManager es) { this.enrollmentService = es; }
     public String generateTranscript(String stid) {
        StringBuilder sb = new StringBuilder("\n--- Transcript ---\n");
        List<Enrollment> list = enrollmentService.findEnrollmentsByStudent(stid);
        for (Enrollment e : list) sb.append(e).append("\n");
        sb.append("GPA: ").append(String.format("%.2f", enrollmentService.calculateStudentGPA(stid)));
        return sb.toString(); }}

class ImportExportService {
      public void exportStudents(List<Student> list) {
        System.out.println("Exported " + list.size() + " students to file."); }
  public void exportCourses(List<Course> list) {
        System.out.println("Exported " + list.size() + " courses to file.");}
    public List<Student> importStudents() {
        return List.of(new Student("24BCE11452", "Sidharth", "Sharma", LocalDate.of(2006,1,8)));}
public List<Course> importCourses() {
        return List.of(new Course("CSE3009", "Full WebDev", 4));
              }}
class BackupHandler {
    public void createBackup() { System.out.println("Backup created."); }
    public void restoreBackup() { System.out.println("Backup restored."); }}
class AppConfig {
    private static final AppConfig instance = new AppConfig();
    private AppConfig() {}
    public static AppConfig getInstance() { return instance; }
    public void printConfiguration() {
        System.out.println("AppConfig: Demo configuration loaded.");
      }}
public class CCRM {
    private final Scanner sc = new Scanner(System.in);
private final StudentHandler studentHandler = new StudentHandler();
    private final CourseHandler courseHandler = new CourseHandler();
    private final EnrollmentManager enrollmentService = new EnrollmentManager(studentHandler, courseHandler);
     private final TranscriptService transcriptService = new TranscriptService(enrollmentService);
  private final ImportExportService importExportService = new ImportExportService();
    private final BackupHandler backupHandler = new BackupHandler();
    public static void main(String[] args) {
        new CCRM().start(); }
  private void start() {
        System.out.println("||WELCOME TO University Manager Applicaton||\n");
        System.out.println("You are viewing the Students Informtion who are enrolled in the VIT Bhopal University");
        AppConfig.getInstance().printConfiguration();
        boolean run = true;
        while (run) {
            System.out.println("\n1.Manage Students 2.Manage Courses 3.EnrollmentsData 4.GradesData 5.Export/ImportData 6.BackupData 7.ReportsData 8.Info 9.Exit");
            int Options = getInt("Available Options: ");
            switch (Options) {
             case 1 -> studentList();
            case 2 -> courseList();
                case 3 -> enrollmentCard();
            case 4 -> gradeCard();
              case 5 -> importExportList();
              case 6 -> handleBackup();
              case 7 -> reportCard();
                case 8 -> showSystemData(); 
                case 9 ->{
                 System.out.println("\n||THANK YOU FOR VISITING University Manager Application!||");
                 System.out.println("Come back again FOR viewing the records");
                run = false;} }}}
private void studentList() {
        System.out.println("1.Create 2.List 3.Search by Name");
        int c=getInt("Choose option");
        if (c == 1) {
 String id = getStr("Student's ID: ");
String FN = getStr("First Name: ");
    String LN = getStr("Last Name: ");
   LocalDate dob = getDate("DateOfBirth(YYYY-MM-DD): ");
    studentHandler.createStudent(new Student(id, FN, LN, dob));}
  else if (c == 2) {
    studentHandler.findAll().forEach(System.out::println);
 } else if (c == 3) {
    String name = getStr("Enter part/full name: ");
    List<Student> found = studentHandler.searchByName(name);
    if (found.isEmpty()) {
        System.out.println("No students found matching: " + name);
    } else {
        found.forEach(System.out::println);} }}
 private void courseList() {
        System.out.println("1.Create 2.List");
     int c = getInt("Choose options: ");
        if (c == 1) {
           String code = getStr("Code: ");
        String name = getStr("Name: ");
              int cr = getInt("Credits: ");
            courseHandler.createCourse(new Course(code, name, cr));}
         else if (c == 2) courseHandler.findAll().forEach(System.out::println); }

            private void enrollmentCard() {
        System.out.println("1.Enroll 2.View by Student 3.View by Course 4.Remove");
        int c = getInt("Choose options: ");
        if (c == 1) {
            enrollmentService.enrollStudent(getStr("Student ID: "), getStr("Course Code: "));}
         else if (c == 2) {
            enrollmentService.findEnrollmentsByStudent(getStr("Student ID: ")).forEach(System.out::println);}
         else if (c == 3) {
            enrollmentService.findEnrollmentsByCourse(getStr("Course Code: ")).forEach(System.out::println); }
             else if (c == 4) {
            enrollmentService.removeEnrollment(getStr("Student ID: "), getStr("Course Code: "));  }}

      private void gradeCard() {
  System.out.println("1.Record/Update Grade");
     String stid = getStr("Student ID: ");
    String coid = getStr("Course Code: ");
        System.out.println(Arrays.toString(Grade.values()));
        Grade g = Grade.valueOf(getStr("Grade: ").toUpperCase());
        enrollmentService.recordGrade(stid, coid, g);}

private void importExportList() {
        System.out.println("1.Export Students 2.Export Courses 3.Import Students 4.Import Courses");
        int c = getInt("Choose options: ");
        if (c == 1) importExportService.exportStudents(studentHandler.findAll());
    else if (c == 2) importExportService.exportCourses(courseHandler.findAll());
        else if (c == 3) studentHandler.bulkImport(importExportService.importStudents());
    else if (c == 4) courseHandler.bulkImport(importExportService.importCourses()); }
 private void handleBackup() {
        System.out.println("1.Create 2.Restore");
          int c = getInt("Choose options: ");
        if (c == 1) backupHandler.createBackup();
          else if (c == 2) backupHandler.restoreBackup();}
   private void reportCard() {
        System.out.println("1.Transcript 2.List by GPA");
        int c = getInt("Choose options: ");
        if (c == 1) System.out.println(transcriptService.generateTranscript(getStr("Student ID: ")));
        else if (c == 2) {
            Map<Student, Double> map = new HashMap<>();
            for (Student s : studentHandler
            .findAll())
                map.put(s, enrollmentService.calculateStudentGPA(s.getId()));
            map.entrySet().stream().sorted(Map.Entry.<Student,Double>comparingByValue().reversed())
                    .forEach(e -> System.out.println(e.getKey().getFullName() + " GPA: " + String.format("%.2f", e.getValue())));     }}
         private void showSystemData() {
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
    }
private String getStr(String p) { System.out.print(p); return sc.nextLine().trim(); }
  private int getInt(String p) { while (true) { try { System.out.print(p); return Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("Invalid"); } } }
    private LocalDate getDate(String p) { while (true) { try { System.out.print(p); return LocalDate.parse(sc.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE); } catch (Exception e) { System.out.println("Invalid"); } } }}



