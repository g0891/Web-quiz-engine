package engine;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userId")
    private int userId;
    private String email;
    private String password;
    private boolean active = true;
    private String roles = "ROLE_USER";

    @OneToMany(
            mappedBy = "userCreated",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<QuizItem> quizItemList = new ArrayList<>();

    @OneToMany(
            mappedBy = "userCompleted"/*,
            cascade = CascadeType.ALL,
            orphanRemoval = true*/
    )
    List<QuizCompletion> quizCompletions = new ArrayList<>();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        userId = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
