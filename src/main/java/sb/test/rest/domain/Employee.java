package sb.test.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "employee", indexes = {@Index(name = "idx_login", columnList = "login", unique = true)})
public class Employee implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;
    private String name;
    private String position;
    private String login;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Date born;
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Employee)) return false;
        final Employee other = (Employee) o;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name)) return false;
        final Object this$position = this.getPosition();
        final Object other$position = other.getPosition();
        if (!Objects.equals(this$position, other$position)) return false;
        final Object this$login = this.getLogin();
        final Object other$login = other.getLogin();
        if (!Objects.equals(this$login, other$login)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (!Objects.equals(this$password, other$password)) return false;
        final Object this$role = this.getRole();
        final Object other$role = other.getRole();
        if (!Objects.equals(this$role, other$role)) return false;

        /*
         * Avoid H2 milliseconds conversion
         */
        final Date this$born = this.getBorn();
        final Date other$born = other.getBorn();
        if ((other$born == null || this$born == null) && this$born != other$born) return false;
        if ((this$born.getTime() / 1000) != (other$born.getTime() / 1000)) return false;

        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $position = this.getPosition();
        result = result * PRIME + ($position == null ? 43 : $position.hashCode());
        final Object $login = this.getLogin();
        result = result * PRIME + ($login == null ? 43 : $login.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $born = this.getBorn();
        result = result * PRIME + ($born == null ? 43 : $born.hashCode());
        final Object $role = this.getRole();
        result = result * PRIME + ($role == null ? 43 : $role.hashCode());
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
