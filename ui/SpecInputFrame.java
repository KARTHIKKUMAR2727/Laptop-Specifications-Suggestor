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

public class SpecInputFrame extends JFrame {
    private JComboBox<String> osComboBox;
    private JComboBox<String> processorComboBox;
    private JComboBox<String> ramComboBox;
    private JComboBox<String> storageComboBox;
    private JComboBox<String> gpuComboBox;
    private JComboBox<String> screenComboBox;
    private JPanel checkboxPanel;
    private JScrollPane softwareScrollPane;
    private JTextField searchSoftwareField;
    private Map<String, JCheckBox> softwareCheckboxes;
    private List<String> allSoftwareNames;

    public SpecInputFrame() {
        setTitle("Enter Your PC/Laptop Specifications");
        setSize(700, 850);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 243, 250));

        // Title Panel with gradient
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                Color color1 = new Color(156, 39, 176);
                Color color2 = new Color(233, 30, 99);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(650, 90));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 30));
        add(titlePanel, BorderLayout.NORTH);

        
        JLabel titleLabel = new JLabel("Enter Your Specifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 243, 250));
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Software Selection Section
        JPanel softwareSelectionPanel = new JPanel();
        softwareSelectionPanel.setLayout(new BoxLayout(softwareSelectionPanel, BoxLayout.Y_AXIS));
        softwareSelectionPanel.setBackground(Color.WHITE);
        softwareSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        softwareSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(18, 18, 18, 18)
        ));

        // Label and buttons panel
        JPanel labelButtonPanel = new JPanel(new BorderLayout());
        labelButtonPanel.setOpaque(false);
        labelButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel softwareLabel = new JLabel("Software to Check (Optional)");
        softwareLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        softwareLabel.setForeground(new Color(44, 62, 80));
        labelButtonPanel.add(softwareLabel, BorderLayout.WEST);
        
        // Select All / Deselect All buttons
        JPanel selectButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        selectButtonsPanel.setOpaque(false);
        
        JButton selectAllBtn = createSmallButton("Select All", new Color(156, 39, 176));
        selectAllBtn.addActionListener(_ -> selectAllSoftware(true));
        
        JButton deselectAllBtn = createSmallButton("Clear", new Color(120, 120, 120));
        deselectAllBtn.addActionListener(_ -> selectAllSoftware(false));
        
        selectButtonsPanel.add(selectAllBtn);
        selectButtonsPanel.add(deselectAllBtn);
        labelButtonPanel.add(selectButtonsPanel, BorderLayout.EAST);
        
        softwareSelectionPanel.add(labelButtonPanel);
        softwareSelectionPanel.add(Box.createVerticalStrut(10));

        // Search field for software
        searchSoftwareField = new JTextField();
        searchSoftwareField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchSoftwareField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchSoftwareField.setToolTipText("Search software...");
        searchSoftwareField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchSoftwareField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchSoftwareField.getPreferredSize().height));
        searchSoftwareField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterSoftware(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterSoftware(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterSoftware(); }
        });
        softwareSelectionPanel.add(searchSoftwareField);
        softwareSelectionPanel.add(Box.createVerticalStrut(12));

        // Checkbox panel for software
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        softwareScrollPane = new JScrollPane(checkboxPanel);
        softwareScrollPane.setPreferredSize(new Dimension(0, 140));
        softwareScrollPane.setMinimumSize(new Dimension(0, 140));
        softwareScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        softwareScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        softwareScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        softwareScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        softwareCheckboxes = new HashMap<>();
        allSoftwareNames = new ArrayList<>();
        loadSoftwareNames();

        softwareSelectionPanel.add(softwareScrollPane);

        contentPanel.add(softwareSelectionPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // OS Selection
        contentPanel.add(createSpecField("Operating System", createOSComboBox()));
        contentPanel.add(Box.createVerticalStrut(20));

        // Processor
        contentPanel.add(createSpecField("Processor", createProcessorComboBox()));
        contentPanel.add(Box.createVerticalStrut(20));

        // RAM
        contentPanel.add(createSpecField("RAM", createRAMComboBox()));
        contentPanel.add(Box.createVerticalStrut(20));

        // Storage
        contentPanel.add(createSpecField("Storage", createStorageComboBox()));
        contentPanel.add(Box.createVerticalStrut(20));

        // GPU
        contentPanel.add(createSpecField("GPU", createGPUComboBox()));
        contentPanel.add(Box.createVerticalStrut(20));

        // Screen Size
        contentPanel.add(createSpecField("Screen Size", createScreenComboBox()));
        contentPanel.add(Box.createVerticalStrut(30));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 243, 250));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton findBtn = createStyledButton("Find Compatible Software", new Color(156, 39, 176));
        findBtn.addActionListener(_ -> findCompatibleSoftware());
        buttonPanel.add(findBtn);

        JButton cancelBtn = createStyledButton("Cancel", new Color(120, 120, 120));
        cancelBtn.addActionListener(_ -> dispose());
        buttonPanel.add(cancelBtn);

        contentPanel.add(buttonPanel);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(240, 243, 250));
        scrollPane.getViewport().setBackground(new Color(240, 243, 250));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSpecField(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(15, 8));
        panel.setBackground(new Color(240, 243, 250));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(44, 62, 80));
        panel.add(label, BorderLayout.NORTH);

        inputComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (inputComponent instanceof JComboBox) {
            ((JComboBox<?>) inputComponent).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 14, 10, 14)
            ));
            ((JComboBox<?>) inputComponent).setBackground(Color.WHITE);
            ((JComboBox<?>) inputComponent).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        panel.add(inputComponent, BorderLayout.CENTER);

        return panel;
    }

    private JComboBox<String> createOSComboBox() {
        String[] osOptions = {
            "Windows 11",
            "Windows 10",
            "Windows 8.1",
            "Windows 7",
            "macOS",
            "Linux",
            "Other"
        };
        osComboBox = new JComboBox<>(osOptions);
        return osComboBox;
    }

    private JComboBox<String> createProcessorComboBox() {
        String[] processorOptions = {
            "Select Processor",
            "Intel i9",
            "Intel i7",
            "Intel i5",
            "Intel i3",
            "AMD Ryzen 9",
            "AMD Ryzen 7",
            "AMD Ryzen 5",
            "AMD Ryzen 3",
            "Apple M1",
            "Apple M1 Pro",
            "Apple M1 Max",
            "Apple M2",
            "Apple M2 Pro",
            "Apple M2 Max",
            "Other"
        };
        processorComboBox = new JComboBox<>(processorOptions);
        processorComboBox.setSelectedIndex(0);
        return processorComboBox;
    }

    private JComboBox<String> createRAMComboBox() {
        String[] ramOptions = {
            "4 GB",
            "8 GB",
            "16 GB",
            "32 GB",
            "64 GB"
        };
        ramComboBox = new JComboBox<>(ramOptions);
        ramComboBox.setSelectedIndex(1); // Default to 8 GB
        return ramComboBox;
    }

    private JComboBox<String> createStorageComboBox() {
        String[] storageOptions = {
            "128 GB",
            "256 GB",
            "512 GB",
            "1 TB",
            "2 TB",
            "4 TB"
        };
        storageComboBox = new JComboBox<>(storageOptions);
        storageComboBox.setSelectedIndex(2); // Default to 512 GB
        return storageComboBox;
    }

    private JComboBox<String> createGPUComboBox() {
        String[] gpuOptions = {
            "Select GPU",
            "NVIDIA RTX 4090",
            "NVIDIA RTX 4080",
            "NVIDIA RTX 4070",
            "NVIDIA RTX 4060",
            "NVIDIA RTX 3090",
            "NVIDIA RTX 3080",
            "NVIDIA RTX 3070",
            "NVIDIA RTX 3060",
            "NVIDIA RTX 3050",
            "NVIDIA RTX 2080",
            "NVIDIA RTX 2070",
            "NVIDIA RTX 2060",
            "NVIDIA GTX 1660 Ti",
            "NVIDIA GTX 1660",
            "NVIDIA GTX 1080",
            "NVIDIA GTX 1070",
            "NVIDIA GTX 1060",
            "AMD RX 6900 XT",
            "AMD RX 6800 XT",
            "AMD RX 6700 XT",
            "AMD RX 6600 XT",
            "AMD RX 6500 XT",
            "AMD RX 5700 XT",
            "AMD RX 5600 XT",
            "Integrated Graphics",
            "Other"
        };
        gpuComboBox = new JComboBox<>(gpuOptions);
        gpuComboBox.setSelectedIndex(0);
        return gpuComboBox;
    }

    private JComboBox<String> createScreenComboBox() {
        String[] screenOptions = {
            "13\"",
            "14\"",
            "15.6\"",
            "17\"",
            "Other"
        };
        screenComboBox = new JComboBox<>(screenOptions);
        screenComboBox.setSelectedIndex(2); // Default to 15.6"
        return screenComboBox;
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
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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

    private void findCompatibleSoftware() {
        String os = (String) osComboBox.getSelectedItem();
        String processor = (String) processorComboBox.getSelectedItem();
        String ram = (String) ramComboBox.getSelectedItem();
        String storage = (String) storageComboBox.getSelectedItem();
        String gpu = (String) gpuComboBox.getSelectedItem();
        String screen = (String) screenComboBox.getSelectedItem();

        // Check if processor or GPU is not selected
        if ((processor == null || processor.equals("Select Processor")) && 
            (gpu == null || gpu.equals("Select GPU"))) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least Processor or GPU!", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Handle "Select" options - set to empty string
        if (processor != null && processor.equals("Select Processor")) {
            processor = "";
        }
        if (gpu != null && gpu.equals("Select GPU")) {
            gpu = "";
        }

        // Get selected software names
        List<String> selectedSoftware = new ArrayList<>();
        Component[] components = checkboxPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) component;
                if (checkbox.isSelected()) {
                    selectedSoftware.add(checkbox.getText());
                }
            }
        }

        // Create user specs object
        UserSpecs userSpecs = new UserSpecs(os, processor, ram, storage, gpu, screen);
        
        // Open result frame with selected software
        new SoftwareResultFrame(userSpecs, selectedSoftware).setVisible(true);
        dispose();
    }

    private void loadSoftwareNames() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT software_name FROM software_specs ORDER BY software_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String softwareName = rs.getString(1);
                allSoftwareNames.add(softwareName);
                createSoftwareCheckbox(softwareName);
            }
            checkboxPanel.revalidate();
            checkboxPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSoftwareCheckbox(String softwareName) {
        JCheckBox checkbox = new JCheckBox(softwareName);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkbox.setBackground(Color.WHITE);
        checkbox.setForeground(new Color(60, 60, 60));
        checkbox.setBorder(new EmptyBorder(5, 8, 5, 8));
        checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkbox.setMaximumSize(new Dimension(Integer.MAX_VALUE, checkbox.getPreferredSize().height));
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        softwareCheckboxes.put(softwareName, checkbox);
        checkboxPanel.add(checkbox);
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 26));
        
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

    private void filterSoftware() {
        String searchText = searchSoftwareField.getText().toLowerCase().trim();
        checkboxPanel.removeAll();
        
        for (String softwareName : allSoftwareNames) {
            if (softwareName.toLowerCase().contains(searchText)) {
                checkboxPanel.add(softwareCheckboxes.get(softwareName));
            }
        }
        
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void selectAllSoftware(boolean select) {
        Component[] components = checkboxPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected(select);
            }
        }
    }

    // Inner class to hold user specifications
    public static class UserSpecs {
        public String os;
        public String processor;
        public String ram;
        public String storage;
        public String gpu;
        public String screen;

        public UserSpecs(String os, String processor, String ram, String storage, String gpu, String screen) {
            this.os = os;
            this.processor = processor;
            this.ram = ram;
            this.storage = storage;
            this.gpu = gpu;
            this.screen = screen;
        }
    }
}

