package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import db.DBConnection;

public class RegisterFrame extends JFrame {
    JTextField username, name, email;
    JPasswordField password;
    JButton registerBtn, backBtn;

    public RegisterFrame() {
        setTitle("Register New User");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main Panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(52, 168, 83);
                Color color2 = new Color(66, 133, 244);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Card Panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(null);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBounds(50, 20, 350, 450);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        mainPanel.add(cardPanel);

        // Title
        JLabel title = new JLabel("Register");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(51, 204, 102));
        title.setBounds(0, 15, 310, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Create your account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBounds(0, 60, 310, 20);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(subtitle);

        // Username
        addLabel(cardPanel, "Username:", 30, 70);
        username = addTextField(cardPanel, 30, 100);

        // Full Name
        addLabel(cardPanel, "Full Name:", 30, 145);
        name = addTextField(cardPanel, 30, 175);

        // Password
        addLabel(cardPanel, "Password:", 30, 220);
        password = new JPasswordField();
        password.setBounds(30, 250, 250, 35);
        password.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(password);

        // Email
        addLabel(cardPanel, "Email:", 30, 295);
        email = addTextField(cardPanel, 30, 325);

        // Register Button
        registerBtn = new JButton("Register") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        registerBtn.setBounds(165, 380, 115, 40);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setBackground(new Color(52, 168, 83));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerBtn.setBackground(new Color(46, 204, 113));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerBtn.setBackground(new Color(52, 168, 83));
            }
        });
        cardPanel.add(registerBtn);

        // Back Button
        backBtn = new JButton("← Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        backBtn.setBounds(30, 380, 115, 40);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(100, 100, 100));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(120, 120, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(new Color(100, 100, 100));
            }
        });
        cardPanel.add(backBtn);

        // Action Listeners
        registerBtn.addActionListener(_ -> registerUser());
        backBtn.addActionListener(_ -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 200, 25);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lbl);
    }

    private JTextField addTextField(JPanel panel, int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 250, 35);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(field);
        return field;
    }

    private void registerUser() {
        String uname = username.getText().trim();
        String fname = name.getText().trim();
        String pass = new String(password.getPassword()).trim();
        String mail = email.getText().trim();

        if (uname.isEmpty() || fname.isEmpty() || pass.isEmpty() || mail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(username, uname, passwd, email) VALUES(?,?,?,?)");
            ps.setString(1, uname);
            ps.setString(2, fname);
            ps.setString(3, pass);
            ps.setString(4, mail);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
