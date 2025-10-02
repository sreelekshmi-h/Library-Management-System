package model;

public class Student extends User {
    private String course;
    private int year;

    public Student() {
        super();
        setRole("student");
    }

    public Student(int id, String name, String email, String password, String course, int year) {
        super(id, name, email, password, "student");
        this.course = course;
        this.year = year;
    }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return super.toString() + ", course='" + course + "', year=" + year;
    }
}
