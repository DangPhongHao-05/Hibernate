import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String role;

    // Hibernate sẽ tự động map với bảng 'follows'
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "following_user_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id")
    )
    private Set<User> following; // Danh sách những người user này đang theo dõi

    public User() {}

    // Getters và Setters đầy đủ cho các thuộc tính

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; } // Đã bổ sung

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; } // Đã bổ sung

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; } // Đã bổ sung

    public Set<User> getFollowing() { return following; }
    public void setFollowing(Set<User> following) { this.following = following; }
}