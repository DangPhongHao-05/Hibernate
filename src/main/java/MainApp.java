import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp {

    private static SessionFactory sessionFactory;
    private static Integer currentUserId;

    private static JFrame mainFrame;
    private static JTextArea feedArea;
    private static JTextField titleInput;
    private static JTextArea bodyInput;
    private static JPanel feedPanel; // Thay JTextArea bằng JPanel để chứa các khối bài viết
    private static JPanel followPanel;

    public static void main(String[] args) {
        System.out.println("Đang khởi tạo cấu hình Hibernate...");
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        SwingUtilities.invokeLater(() -> {
            showLoginScreen();
        });
    }

    private static void showLoginScreen() {

        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(800, 450);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(1,2));

        // ===== BÊN TRÁI (ẢNH / LOGO) =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(24,119,242));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("SOCIAL");
        icon.setFont(new Font("Arial", Font.BOLD, 40));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Connect with everyone");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.WHITE);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(icon);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(sub);
        leftPanel.add(Box.createVerticalGlue());


        // ===== BÊN PHẢI (FORM) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(60,60,60,60));
        rightPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Đăng nhập");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(300,35));
        txtUsername.setBorder(BorderFactory.createTitledBorder("Username"));
        txtUsername.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,35)
        );

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(300,35));
        txtPassword.setBorder(BorderFactory.createTitledBorder("Password"));
        txtPassword.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,35)
        );

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(24,119,242));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,35)
        );
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel goRegister =
                new JLabel("Chưa có tài khoản? Đăng ký");
        goRegister.setForeground(Color.GRAY);
        goRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        goRegister.addMouseListener(
                new java.awt.event.MouseAdapter(){
                    public void mouseClicked(
                            java.awt.event.MouseEvent evt){

                        loginFrame.dispose();
                        showRegisterScreen();

                    }
                });

        btnLogin.addActionListener(e -> {

            String username = txtUsername.getText();
            String password =
                    new String(txtPassword.getPassword());

            if(authenticateUser(username,password)){

                loginFrame.dispose();
                showMainScreen();

            }
            else{

                JOptionPane.showMessageDialog(
                        loginFrame,
                        "Sai tài khoản"
                );

            }

        });

        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(txtUsername);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(txtPassword);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(btnLogin);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(goRegister);

        loginFrame.add(leftPanel);
        loginFrame.add(rightPanel);

        loginFrame.setVisible(true);
    }

    private static boolean authenticateUser(String username, String password) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM User u WHERE u.username = :username AND u.password = :password";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            User user = query.uniqueResult();
            if (user != null) {
                currentUserId = user.getId();
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    private static void showRegisterScreen(){

        JFrame regFrame = new JFrame("Register");
        regFrame.setSize(800,450);
        regFrame.setLocationRelativeTo(null);
        regFrame.setLayout(new GridLayout(1,2));

        // trái
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(40,167,69));
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("JOIN US");
        icon.setFont(new Font("Arial",Font.BOLD,40));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Create account now");
        sub.setForeground(Color.WHITE);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(icon);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(sub);
        leftPanel.add(Box.createVerticalGlue());

        // phải
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBorder(
                BorderFactory.createEmptyBorder(60,60,60,60)
        );
        rightPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Đăng ký");
        title.setFont(new Font("Arial",Font.BOLD,26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(300,35));
        txtUsername.setBorder(
                BorderFactory.createTitledBorder("Username"));
        txtUsername.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,35));

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(300,35));
        txtPassword.setBorder(
                BorderFactory.createTitledBorder("Password"));
        txtPassword.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,35));

        JButton btnRegister = new JButton("Tạo tài khoản");
        btnRegister.setBackground(new Color(40,167,69));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel goLogin =
                new JLabel("Đã có tài khoản? Đăng nhập");
        goLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        goLogin.addMouseListener(
                new java.awt.event.MouseAdapter(){

                    public void mouseClicked(
                            java.awt.event.MouseEvent evt){

                        regFrame.dispose();
                        showLoginScreen();

                    }
                });

        btnRegister.addActionListener(e -> {

            if(registerNewUser(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword()))){

                JOptionPane.showMessageDialog(
                        regFrame,
                        "Tạo tài khoản thành công"
                );

                regFrame.dispose();
                showLoginScreen();
            }

        });

        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(txtUsername);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(txtPassword);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(btnRegister);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(goLogin);

        regFrame.add(leftPanel);
        regFrame.add(rightPanel);

        regFrame.setVisible(true);
    }

    private static boolean registerNewUser(String username, String password) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            // 1. Kiểm tra xem tên tài khoản đã tồn tại chưa
            String countHql = "SELECT count(u) FROM User u WHERE u.username = :username";
            Query<Long> countQuery = session.createQuery(countHql, Long.class);
            countQuery.setParameter("username", username);
            Long count = countQuery.uniqueResult();

            if (count > 0) {
                return false; // Đã tồn tại user trùng tên
            }

            // 2. Nếu chưa tồn tại, tiến hành tạo mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole("USER"); // Phân quyền mặc định

            session.persist(newUser);
            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    // MÀN HÌNH CHÍNH (POST & FEED)
    private static void showMainScreen() {
        mainFrame = new JFrame("Mạng Xã Hội");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLayout(new BorderLayout(10, 10));
        mainFrame.getContentPane().setBackground(new Color(240, 242, 245));

        // --- 1. Thanh Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel followPanel = createFollowPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Session session = sessionFactory.openSession();
        String currentUsername = session.get(User.class, currentUserId).getUsername();
        session.close();

        JLabel lblWelcome = new JLabel("👋 Xin chào, " + currentUsername + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setBackground(new Color(228, 230, 235));
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentUserId = null;
                mainFrame.dispose();
                showLoginScreen();
            }
        });

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // --- 2. Khung Đăng Bài ---
        JPanel postPanel = new JPanel(new BorderLayout(5, 10));
        postPanel.setBackground(Color.WHITE);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createTitledBorder("Tạo bài viết mới")
        ));

        titleInput = new JTextField();
        titleInput.setBorder(BorderFactory.createTitledBorder("Tiêu đề"));

        bodyInput = new JTextArea(3, 20);
        bodyInput.setBorder(BorderFactory.createTitledBorder("Bạn đang nghĩ gì?"));
        bodyInput.setLineWrap(true);
        bodyInput.setWrapStyleWord(true);

        JButton btnPost = new JButton("Đăng Bài");
        btnPost.setBackground(new Color(24, 119, 242));
        btnPost.setForeground(Color.WHITE);
        btnPost.setFont(new Font("Arial", Font.BOLD, 14));
        btnPost.setPreferredSize(new Dimension(0, 35));
        btnPost.addActionListener(e -> createPost());

        postPanel.add(titleInput, BorderLayout.NORTH);
        postPanel.add(new JScrollPane(bodyInput), BorderLayout.CENTER);
        postPanel.add(btnPost, BorderLayout.SOUTH);

        // --- 3. Khung Bảng Tin (Feed Container) ---
        feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedPanel.setBackground(new Color(240, 242, 245));

        JScrollPane feedScroll = new JScrollPane(feedPanel);
        feedScroll.setBorder(null);
        feedScroll.getVerticalScrollBar().setUnitIncrement(16);

        feedScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(new Color(240, 242, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        centerPanel.add(postPanel, BorderLayout.NORTH);
        centerPanel.add(feedScroll, BorderLayout.CENTER);

        mainFrame.add(headerPanel, BorderLayout.NORTH);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.add(followPanel, BorderLayout.EAST);

        loadFeed();
        mainFrame.setVisible(true);
    }

    // TẠO GIAO DIỆN CHO 1 BÀI ĐĂNG
    private static JPanel createPostCard(Post p, String currentUsername) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        // 1. PHẦN HEADER CỦA BÀI VIẾT (Avatar + Tên)
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);

        String authorName = p.getUser().getId().equals(currentUserId) ? currentUsername + " (Tôi)" : p.getUser().getUsername();
        String firstLetter = p.getUser().getUsername().substring(0, 1).toUpperCase();

        JLabel avatar = new JLabel(firstLetter, SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(24, 119, 242));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Arial", Font.BOLD, 18));
        avatar.setPreferredSize(new Dimension(40, 40));

        JLabel nameLabel = new JLabel(authorName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        header.add(avatar, BorderLayout.WEST);
        header.add(nameLabel, BorderLayout.CENTER);

        // 2. PHẦN NỘI DUNG (Tiêu đề + Thân bài)
        JPanel content = new JPanel(new BorderLayout(0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("<html><b>" + p.getTitle() + "</b></html>");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextArea txtBody = new JTextArea(p.getBody());
        txtBody.setWrapStyleWord(true); // Tự động bẻ chữ
        txtBody.setLineWrap(true);      // Ngắt dòng khi đụng viền
        txtBody.setEditable(false);
        txtBody.setFont(new Font("Arial", Font.PLAIN, 15));
        txtBody.setBorder(null);
        txtBody.setBackground(Color.WHITE);

        content.add(lblTitle, BorderLayout.NORTH);
        content.add(txtBody, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        // nếu bài viết không phải của mình thì hiện nút follow
        if (!p.getUser().getId().equals(currentUserId)) {

            Session session = sessionFactory.openSession();

            User me = session.get(User.class, currentUserId);

            boolean isFollowing =
                    me.getFollowing()
                            .stream()
                            .anyMatch(u -> u.getId().equals(p.getUser().getId()));

            session.close();

            JButton btnFollow = new JButton(
                    isFollowing ? "Following" : "Follow"
            );

            btnFollow.setBackground(
                    isFollowing ? new Color(230, 230, 230)
                            : new Color(24, 119, 242)
            );

            btnFollow.setForeground(
                    isFollowing ? Color.BLACK : Color.WHITE
            );

            btnFollow.addActionListener(e -> {

                if (isFollowing) {

                    unfollowUser(p.getUser().getId());

                } else {

                    followUser(p.getUser().getId());

                }

                loadFeed();
            });

            JPanel bottomPanel =
                    new JPanel(new FlowLayout(FlowLayout.RIGHT));

            bottomPanel.setBackground(Color.WHITE);

            bottomPanel.add(btnFollow);

            card.add(bottomPanel, BorderLayout.SOUTH);
        }

        return card;
    }

    private static void createPost() {
        String title = titleInput.getText();
        String body = bodyInput.getText();

        if (title.isEmpty() || body.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Vui lòng nhập đủ tiêu đề và nội dung!");
            return;
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            User currentUser = session.get(User.class, currentUserId);

            Post newPost = new Post();
            newPost.setTitle(title);
            newPost.setBody(body);
            newPost.setUser(currentUser);

            session.persist(newPost);
            tx.commit();

            titleInput.setText("");
            bodyInput.setText("");
            loadFeed();
            JOptionPane.showMessageDialog(mainFrame, "Đăng bài thành công!");

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Lỗi khi đăng bài!");
        } finally {
            session.close();
        }
    }

    private static void loadFeed() {
        Session session = sessionFactory.openSession();
        try {
            User currentUser = session.get(User.class, currentUserId);
            String currentUsername = currentUser.getUsername();

            // Dùng câu HQL đơn giản nhất lấy toàn bộ bài viết từ mới tới cũ
            String hql = "FROM Post p ORDER BY p.id DESC";
            Query<Post> query = session.createQuery(hql, Post.class);
            List<Post> posts = query.getResultList();

            feedPanel.removeAll();

            if (posts.isEmpty()) {
                JLabel emptyLabel = new JLabel("Hệ thống chưa có bài viết nào.");
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                feedPanel.add(emptyLabel);
            } else {
                for (Post p : posts) {
                    JPanel postCard = createPostCard(p, currentUsername);
                    feedPanel.add(postCard);
                    feedPanel.add(Box.createVerticalStrut(15));
                }
            }

            feedPanel.revalidate();
            feedPanel.repaint();

        } finally {
            session.close();
        }
    }

    private static void unfollowUser(Integer userId) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {

            User me = session.get(User.class, currentUserId);
            User target = session.get(User.class, userId);

            me.getFollowing().remove(target);

            session.merge(me);

            tx.commit();

            loadFeed();
            loadFollows(followPanel); // reload danh sách follow

        } catch (Exception e) {

            tx.rollback();
            e.printStackTrace();

        } finally {

            session.close();

        }
    }

    private static void followUser(Integer userId) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {

            User me = session.get(User.class, currentUserId);
            User target = session.get(User.class, userId);

            if (!me.getFollowing().contains(target)) {

                me.getFollowing().add(target);

                session.merge(me);

                tx.commit();

                loadFeed();
                loadFollows(followPanel); // reload danh sách follow

            }

        } catch (Exception e) {

            tx.rollback();
            e.printStackTrace();

        } finally {

            session.close();

        }
    }

    private static void loadFollows(JPanel followPanel) {

        Session session = sessionFactory.openSession();

        try {

            User me = session.get(User.class, currentUserId);

            followPanel.removeAll();

            if (me.getFollowing() == null || me.getFollowing().isEmpty()) {

                JLabel lbl = new JLabel("Chưa follow ai");
                lbl.setFont(new Font("Arial", Font.ITALIC, 13));
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                followPanel.add(lbl);

            } else {

                for (User u : me.getFollowing()) {

                    JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
                    item.setMaximumSize(new Dimension(160, 35));
                    item.setBackground(Color.WHITE);

                    // avatar
                    String firstLetter =
                            u.getUsername().substring(0, 1).toUpperCase();

                    JLabel avatar = new JLabel(firstLetter);
                    avatar.setOpaque(true);
                    avatar.setHorizontalAlignment(SwingConstants.CENTER);
                    avatar.setPreferredSize(new Dimension(28, 28));

                    avatar.setBackground(new Color(24, 119, 242));
                    avatar.setForeground(Color.WHITE);

                    avatar.setFont(new Font("Arial", Font.BOLD, 13));

                    // tên
                    JLabel name =
                            new JLabel(u.getUsername());

                    name.setFont(new Font("Arial", Font.PLAIN, 13));

                    // nút unfollow
                    JButton btnUnfollow =
                            new JButton("Unfollow");

                    btnUnfollow.setFont(
                            new Font("Arial", Font.PLAIN, 11));

                    btnUnfollow.setMargin(
                            new Insets(2, 6, 2, 6));

                    btnUnfollow.addActionListener(e -> {

                        unfollowUser(u.getId());

                        loadFollows(followPanel);

                    });

                    item.add(avatar);
                    item.add(name);
                    item.add(btnUnfollow);

                    followPanel.add(item);
                }
            }

            followPanel.revalidate();
            followPanel.repaint();

        } finally {

            session.close();

        }
    }

    private static JPanel createFollowPanel() {

        followPanel = new JPanel();

        followPanel.setLayout(
                new BoxLayout(followPanel, BoxLayout.Y_AXIS)
        );

        followPanel.setBackground(Color.WHITE);

        followPanel.setBorder(
                BorderFactory.createTitledBorder("Follows")
        );

        followPanel.setPreferredSize(
                new Dimension(180, 0)
        );

        loadFollows(followPanel);

        return followPanel;
    }

}