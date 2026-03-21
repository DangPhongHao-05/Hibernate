import jakarta.persistence.*;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String body;
    private String status = "PUBLISHED";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post() {}

    // Getters và Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}