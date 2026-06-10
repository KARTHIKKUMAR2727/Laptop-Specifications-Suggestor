package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import db.DBConnection;

public class UserDashboard extends JFrame {
    private JPanel checkboxPanel;
    private JScrollPane scrollPane;
    private Map<String, JCheckBox> softwareCheckboxes;
    private JTextField searchField;
    private JButton findBtn, findSoftwareBtn, logoutBtn;
    private List<String> allSoftwareNames;

    public UserDashboard(String username) {
        setTitle("Welcome " + username);
        setSize(750, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(new Color(245, 245, 250));

        // Title Panel with gradient
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                Color color1 = new Color(52, 168, 83);
                Color color2 = new Color(66, 133, 244);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, getHeight());
            }
        };
        titlePanel.setBounds(0, 0, 700, 80);
        titlePanel.setLayout(null);
        add(titlePanel);

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setBounds(30, 15, 500, 50);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        titlePanel.add(welcomeLabel);

        // Search Field
        JLabel searchLabel = new JLabel("Find Software:");
        searchLabel.setBounds(40, 100, 150, 25);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(40, 125, 350, 38);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        searchField.setBackground(Color.WHITE);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterSoftware(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterSoftware(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterSoftware(); }
        });
        add(searchField);

        // Software Checkbox Panel
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBounds(40, 175, 350, 30);
        labelPanel.setOpaque(false);
        
        JLabel label = new JLabel("Available Software:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelPanel.add(label, BorderLayout.WEST);
        
        // Select All / Deselect All buttons
        JPanel selectButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        selectButtonsPanel.setOpaque(false);
        
        JButton selectAllBtn = createSmallButton("Select All", new Color(66, 133, 244));
        selectAllBtn.addActionListener(_ -> selectAllCheckboxes(true));
        
        JButton deselectAllBtn = createSmallButton("Clear", new Color(120, 120, 120));
        deselectAllBtn.addActionListener(_ -> selectAllCheckboxes(false));
        
        selectButtonsPanel.add(selectAllBtn);
        selectButtonsPanel.add(deselectAllBtn);
        labelPanel.add(selectButtonsPanel, BorderLayout.EAST);
        add(labelPanel);

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setBounds(40, 205, 350, 310);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        softwareCheckboxes = new HashMap<>();
        allSoftwareNames = new ArrayList<>();
        loadSoftware();

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(420, 125, 290, 390);
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));
        buttonPanel.setBackground(new Color(245, 245, 250));
        add(buttonPanel);

        // Find Specs Button (for selected software)
        findBtn = new JButton("Find Recommended Specs") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        findBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        findBtn.setBackground(new Color(66, 133, 244));
        findBtn.setForeground(Color.WHITE);
        findBtn.setFocusPainted(false);
        findBtn.setBorderPainted(false);
        findBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        findBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                findBtn.setBackground(new Color(58, 118, 230));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                findBtn.setBackground(new Color(66, 133, 244));
            }
        });
        buttonPanel.add(findBtn);

        // Find Software Button (for user specs)
        findSoftwareBtn = new JButton("Find Compatible Software") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        findSoftwareBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        findSoftwareBtn.setBackground(new Color(156, 39, 176));
        findSoftwareBtn.setForeground(Color.WHITE);
        findSoftwareBtn.setFocusPainted(false);
        findSoftwareBtn.setBorderPainted(false);
        findSoftwareBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        findSoftwareBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                findSoftwareBtn.setBackground(new Color(142, 36, 170));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                findSoftwareBtn.setBackground(new Color(156, 39, 176));
            }
        });
        buttonPanel.add(findSoftwareBtn);

        // Logout Button
        logoutBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(234, 67, 53));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(211, 55, 43));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(234, 67, 53));
            }
        });
        buttonPanel.add(logoutBtn);

        // Action Listeners
        findBtn.addActionListener(_ -> showSpecs());
        findSoftwareBtn.addActionListener(_ -> {
            new SpecInputFrame().setVisible(true);
        });
        logoutBtn.addActionListener(_ -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 25));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(brightenColor(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private Color brightenColor(Color color) {
        int r = Math.min(255, color.getRed() + 20);
        int g = Math.min(255, color.getGreen() + 20);
        int b = Math.min(255, color.getBlue() + 20);
        return new Color(r, g, b);
    }

    private void loadSoftware() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT software_name FROM software_specs ORDER BY software_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String softwareName = rs.getString(1);
                allSoftwareNames.add(softwareName);
                createCheckbox(softwareName);
            }
            checkboxPanel.revalidate();
            checkboxPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading software: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCheckbox(String softwareName) {
        JCheckBox checkbox = new JCheckBox(softwareName);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkbox.setBackground(Color.WHITE);
        checkbox.setForeground(new Color(60, 60, 60));
        checkbox.setBorder(new EmptyBorder(6, 8, 6, 8));
        checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkbox.setMaximumSize(new Dimension(Integer.MAX_VALUE, checkbox.getPreferredSize().height));
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        softwareCheckboxes.put(softwareName, checkbox);
        checkboxPanel.add(checkbox);
    }

    private void filterSoftware() {
        String searchText = searchField.getText().toLowerCase().trim();
        checkboxPanel.removeAll();
        
        for (String softwareName : allSoftwareNames) {
            if (softwareName.toLowerCase().contains(searchText)) {
                checkboxPanel.add(softwareCheckboxes.get(softwareName));
            }
        }
        
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void selectAllCheckboxes(boolean select) {
        Component[] components = checkboxPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected(select);
            }
        }
    }

    private void showSpecs() {
        List<String> selected = new ArrayList<>();
        // Get all checkboxes from the panel (visible ones after filtering)
        Component[] components = checkboxPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) component;
                if (checkbox.isSelected()) {
                    selected.add(checkbox.getText());
                }
            }
        }
        
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one software!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new SpecResultFrame(selected).setVisible(true);
    }
}