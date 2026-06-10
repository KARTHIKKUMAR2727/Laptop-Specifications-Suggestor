package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import db.DBConnection;

public class LoginFrame extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, registerButton;

    public LoginFrame() {
        setTitle("Laptop Spec Recommender - Login");
        setSize(480, 420);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                
                // Create softer gradient
                Color color1 = new Color(73, 144, 255);
                Color color2 = new Color(51, 204, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Login Card Panel with shadow effect
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBounds(65, 40, 350, 330);
        mainPanel.add(cardPanel);

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(73, 144, 255));
        title.setBounds(0, 25, 350, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(title);

        JLabel subtitle = new JLabel("Welcome back!");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBounds(0, 75, 350, 20);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(subtitle);

        JLabel userLbl = new JLabel("Username");
        userLbl.setBounds(40, 110, 270, 20);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(new Color(80, 80, 80));
        cardPanel.add(userLbl);

        usernameField = new JTextField();
        usernameField.setBounds(40, 135, 270, 42);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBackground(new Color(250, 250, 250));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        cardPanel.add(usernameField);


        JLabel passLbl = new JLabel("Password");
        passLbl.setBounds(40, 190, 270, 20);
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLbl.setForeground(new Color(80, 80, 80));
        cardPanel.add(passLbl);

        passwordField = new JPasswordField();
        passwordField.setBounds(40, 215, 270, 42);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBackground(new Color(250, 250, 250));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        cardPanel.add(passwordField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBounds(40, 280, 270, 45);
        buttonPanel.setOpaque(false);
        cardPanel.add(buttonPanel);

        loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        loginButton.setPreferredSize(new Dimension(120, 42));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(73, 144, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to login button
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(58, 130, 246));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(73, 144, 255));
            }
        });
        buttonPanel.add(loginButton);

        registerButton = new JButton("Sign Up") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        registerButton.setPreferredSize(new Dimension(120, 42));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setBackground(new Color(100, 181, 246));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to register button
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(85, 165, 235));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(100, 181, 246));
            }
        });
        buttonPanel.add(registerButton);

        loginButton.addActionListener(_ -> handleLogin());
        registerButton.addActionListener(_ -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        passwordField.addActionListener(_ -> handleLogin());
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (user.equals("admin") && pass.equals("admin123")) {
                new AdminDashboard().setVisible(true);
                dispose();
                return;
            }
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND passwd=?");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                new UserDashboard(user).setVisible(true);
                dispose();
            }
            else{
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
