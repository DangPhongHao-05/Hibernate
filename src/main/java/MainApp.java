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

    public static void main(String[] args) {
        System.out.println("Đang khởi tạo cấu hình Hibernate...");
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        SwingUtilities.invokeLater(() -> {
            showLoginScreen();
        });
    }

    // MÀN HÌNH ĐĂNG NHẬP
    private static void showLoginScreen() {
        JFrame loginFrame = new JFrame("Đăng nhập - Mạng Xã Hội");
        loginFrame.setSize(400, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // TIÊU ĐỀ
        JPanel panelTitle = new JPanel();
        JLabel labelDangNhap = new JLabel("ĐĂNG NHẬP");
        labelDangNhap.setFont(new Font("Arial", Font.BOLD, 24));
        labelDangNhap.setForeground(new Color(0, 102, 204));
        panelTitle.add(labelDangNhap);

        // TÀI KHOẢN & MẬT KHẨU (SỬ DỤNG PLACEHOLDER)
        JPanel panelInputs = new JPanel(new GridLayout(2, 1, 0, 8));

        // 1. Khởi tạo ô Tài khoản có sẵn chữ mờ
        JTextField txtUsername = new JTextField("Tài khoản");
        txtUsername.setForeground(Color.GRAY); // Chữ màu xám mờ
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        // Thêm sự kiện click chuột cho ô Tài khoản
        txtUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Khi click vào, nếu đang là chữ "Tài khoản" thì xóa trắng đi để gõ
                if (txtUsername.getText().equals("Tài khoản")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK); // Chữ đen khi gõ
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Khi click ra chỗ khác, nếu ô trống thì hiện lại chữ mờ
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setForeground(Color.GRAY);
                    txtUsername.setText("Tài khoản");
                }
            }
        });

        // 2. Khởi tạo ô Mật khẩu
        JPasswordField txtPassword = new JPasswordField("Mật khẩu");
        txtPassword.setForeground(Color.GRAY);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setEchoChar((char) 0); // Hiện chữ "Mật khẩu" rõ ràng (không bị mã hóa thành dấu *)

        // Thêm sự kiện click chuột cho ô Mật khẩu
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                String pass = new String(txtPassword.getPassword());
                if (pass.equals("Mật khẩu")) {
                    txtPassword.setText("");
                    txtPassword.setForeground(Color.BLACK);
                    txtPassword.setEchoChar('•'); // Bật lại chế độ mã hóa bằng dấu chấm tròn
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                String pass = new String(txtPassword.getPassword());
                if (pass.isEmpty()) {
                    txtPassword.setForeground(Color.GRAY);
                    txtPassword.setText("Mật khẩu");
                    txtPassword.setEchoChar((char) 0); // Tắt mã hóa để hiện lại chữ "Mật khẩu"
                }
            }
        });

        panelInputs.add(txtUsername);
        panelInputs.add(txtPassword);

        // NÚT ĐĂNG NHẬP
        JPanel panelBtn = new JPanel(new BorderLayout());
        JButton btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        panelBtn.add(btnLogin, BorderLayout.CENTER);

        // LINK ĐĂNG KÝ
        JPanel panelRegister = new JPanel();
        JLabel lblRegister = new JLabel("<html>Bạn chưa có tài khoản? <font color='blue'><u>Đăng ký ngay</u></font></html>");
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginFrame.dispose();
                showRegisterScreen();
            }
        });
        panelRegister.add(lblRegister);

        mainPanel.add(panelTitle);
        mainPanel.add(panelInputs);
        mainPanel.add(panelBtn);
        mainPanel.add(panelRegister);

        loginFrame.add(mainPanel, BorderLayout.CENTER);

        // Xử lý chặn lỗi khi người dùng bấm Đăng nhập nhưng chưa nhập gì
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (username.equals("Tài khoản") || password.equals("Mật khẩu") || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Vui lòng nhập đầy đủ Tài khoản và Mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authenticateUser(username, password)) {
                loginFrame.dispose();
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Đưa con trỏ chuột ra khỏi ô text lúc vừa mở phần mềm để tránh mất chữ mờ
        mainPanel.requestFocusInWindow();
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

    // MÀN HÌNH ĐĂNG KÝ
    private static void showRegisterScreen() {
        JFrame regFrame = new JFrame("Đăng ký - Mạng Xã Hội");
        regFrame.setSize(400, 300); // Kích thước y hệt form đăng nhập
        regFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regFrame.setLocationRelativeTo(null);
        regFrame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // TIÊU ĐỀ
        JPanel panelTitle = new JPanel();
        JLabel labelDangKy = new JLabel("ĐĂNG KÝ");
        labelDangKy.setFont(new Font("Arial", Font.BOLD, 24));
        labelDangKy.setForeground(new Color(40, 167, 69)); // Màu xanh lá cây cho Đăng ký
        panelTitle.add(labelDangKy);

        // TÀI KHOẢN & MẬT KHẨU (PLACEHOLDER)
        JPanel panelInputs = new JPanel(new GridLayout(2, 1, 0, 8));

        // 1. Khởi tạo ô Tài khoản mới
        JTextField txtUsername = new JTextField("Tài khoản mới");
        txtUsername.setForeground(Color.GRAY);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        txtUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtUsername.getText().equals("Tài khoản mới")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setForeground(Color.GRAY);
                    txtUsername.setText("Tài khoản mới");
                }
            }
        });

        // 2. Khởi tạo ô Mật khẩu
        JPasswordField txtPassword = new JPasswordField("Mật khẩu");
        txtPassword.setForeground(Color.GRAY);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setEchoChar((char) 0);

        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                String pass = new String(txtPassword.getPassword());
                if (pass.equals("Mật khẩu")) {
                    txtPassword.setText("");
                    txtPassword.setForeground(Color.BLACK);
                    txtPassword.setEchoChar('•');
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                String pass = new String(txtPassword.getPassword());
                if (pass.isEmpty()) {
                    txtPassword.setForeground(Color.GRAY);
                    txtPassword.setText("Mật khẩu");
                    txtPassword.setEchoChar((char) 0);
                }
            }
        });

        panelInputs.add(txtUsername);
        panelInputs.add(txtPassword);

        // NÚT ĐĂNG KÝ
        JPanel panelBtn = new JPanel(new BorderLayout());
        JButton btnRegister = new JButton("Đăng Ký");
        btnRegister.setBackground(new Color(40, 167, 69)); // Màu xanh lá cây
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        panelBtn.add(btnRegister, BorderLayout.CENTER);

        // LINK ĐĂNG NHẬP
        JPanel panelLogin = new JPanel();
        JLabel lblLogin = new JLabel("<html>Bạn đã có tài khoản? <font color='blue'><u>Đăng nhập ngay</u></font></html>");
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regFrame.dispose();
                showLoginScreen(); // Trở về màn hình đăng nhập
            }
        });
        panelLogin.add(lblLogin);

        mainPanel.add(panelTitle);
        mainPanel.add(panelInputs);
        mainPanel.add(panelBtn);
        mainPanel.add(panelLogin);

        regFrame.add(mainPanel, BorderLayout.CENTER);

        // --- LOGIC XỬ LÝ ĐĂNG KÝ ---
        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            // Kiểm tra xem người dùng đã gõ thật chưa, hay vẫn để nguyên chữ mờ
            if (username.equals("Tài khoản mới") || password.equals("Mật khẩu") || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(regFrame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (registerNewUser(username, password)) {
                JOptionPane.showMessageDialog(regFrame, "Đăng ký thành công! Hãy đăng nhập lại.");
                regFrame.dispose();
                showLoginScreen(); // Tạo xong thì tự động đẩy về form Đăng nhập
            } else {
                JOptionPane.showMessageDialog(regFrame, "Tên tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.requestFocusInWindow();
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
        mainFrame = new JFrame("Mạng Xã Hội Demo");
        mainFrame.setSize(550, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout(10, 10));

        mainFrame.getContentPane().setBackground(new Color(240, 242, 245));

        // --- 1. Thanh Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Session session = sessionFactory.openSession();
        String currentUsername = session.get(User.class, currentUserId).getUsername();
        session.close();

        JLabel lblWelcome = new JLabel("👋 Chào mừng, " + currentUsername + "!");
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
}