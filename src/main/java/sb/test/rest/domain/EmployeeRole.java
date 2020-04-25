package sb.test.rest.domain;

public enum EmployeeRole {
    USER, ADMIN;

    public boolean isAdmin(){
        return this == ADMIN;
    }
}
