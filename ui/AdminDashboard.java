package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import db.DBConnection;

public class AdminDashboard extends JFrame {
    JTable table;
    DefaultTableModel model;
    JButton addBtn, delBtn, refreshBtn, logoutBtn;

    public AdminDashboard() {
        setTitle("Admin Panel - Software Management");
        setSize(1100, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 247, 250));

        // Title Panel with gradient
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                Color color1 = new Color(70, 130, 255);
                Color color2 = new Color(100, 149, 237);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, getHeight());
            }
        };
        titlePanel.setBounds(0, 0, 1100, 85);
        titlePanel.setLayout(null);
        add(titlePanel);

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setBounds(40, 20, 600, 45);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Table Setup with modern styling
        String[] cols = {"ID", "Software", "OS", "Processor", "RAM", "Storage", "GPU", "Screen"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(100, 149, 237, 80));
        table.setSelectionForeground(new Color(40, 40, 40));
        table.setGridColor(new Color(230, 230, 240));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 255));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 115, 1020, 400);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 2));
        sp.setBackground(Color.WHITE);
        add(sp);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(40, 540, 1020, 70);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setBackground(new Color(245, 247, 250));
        add(buttonPanel);

        // Add Button
        addBtn = createStyledButton("Add Software", new Color(46, 204, 113));
        buttonPanel.add(addBtn);

        // Delete Button
        delBtn = createStyledButton("Delete Software", new Color(234, 67, 53));
        buttonPanel.add(delBtn);

        // Refresh Button
        refreshBtn = createStyledButton("Refresh", new Color(70, 130, 255));
        buttonPanel.add(refreshBtn);

        // Logout Button
        logoutBtn = createStyledButton("Logout", new Color(120, 120, 120));
        buttonPanel.add(logoutBtn);

        // Load data
        loadTable();

        // Action Listeners
        addBtn.addActionListener(_ -> addSoftware());
        delBtn.addActionListener(_ -> deleteSoftware());
        refreshBtn.addActionListener(_ -> loadTable());
        logoutBtn.addActionListener(_ -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
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
        int r = Math.min(255, color.getRed() + 30);
        int g = Math.min(255, color.getGreen() + 30);
        int b = Math.min(255, color.getBlue() + 30);
        return new Color(r, g, b);
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, software_name, os_recommended, processor_recommended, ram_recommended, storage_recommended, gpu_recommended, screen_recommended FROM software_specs");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getString(8)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSoftware() {
        JTextField softwareField = new JTextField();
        JTextField osField = new JTextField();
        JTextField processorField = new JTextField();
        JTextField ramField = new JTextField();
        JTextField storageField = new JTextField();
        JTextField gpuField = new JTextField();
        JTextField screenField = new JTextField();

        Object[] message = {
            "Software Name:", softwareField,
            "OS Recommended:", osField,
            "Processor:", processorField,
            "RAM:", ramField,
            "Storage:", storageField,
            "GPU:", gpuField,
            "Screen Size:", screenField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Software", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO software_specs(software_name, os_recommended, processor_recommended, ram_recommended, storage_recommended, gpu_recommended, screen_recommended) VALUES(?,?,?,?,?,?,?)"
                );
                ps.setString(1, softwareField.getText().trim());
                ps.setString(2, osField.getText().trim());
                ps.setString(3, processorField.getText().trim());
                ps.setString(4, ramField.getText().trim());
                ps.setString(5, storageField.getText().trim());
                ps.setString(6, gpuField.getText().trim());
                ps.setString(7, screenField.getText().trim());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Software added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTable();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSoftware() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this software?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("DELETE FROM software_specs WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Software deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTable();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
