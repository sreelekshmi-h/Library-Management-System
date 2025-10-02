package model;

public class Librarian extends User {
    private String employeeId;

    public Librarian() {
        super();
        setRole("librarian");
    }

    public Librarian(int id, String name, String email, String password, String employeeId) {
        super(id, name, email, password, "librarian");
        this.employeeId = employeeId;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @Override
    public String toString() {
        return super.toString() + ", employeeId='" + employeeId + "'";
    }
}
