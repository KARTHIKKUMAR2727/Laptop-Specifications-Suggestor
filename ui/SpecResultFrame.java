package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.*;
import java.util.List;
import db.DBConnection;

public class SpecResultFrame extends JFrame {
    private CardPanel hoveredPanel;
    
    public SpecResultFrame(List<String> softwares) {
        setTitle("Recommended Laptop Specifications");
        setSize(900, 750);
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
                Color color1 = new Color(70, 130, 255);
                Color color2 = new Color(100, 149, 237);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(900, 90));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 30));
        add(titlePanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Recommended Specifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 243, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add selected software section
        JPanel softwareSection = createSectionPanel("Selected Software");
        JPanel softwareListPanel = new JPanel();
        softwareListPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        softwareListPanel.setOpaque(false);
        
        if (softwares != null && !softwares.isEmpty()) {
            for (String s : softwares) {
                JLabel softwareLabel = createSoftwareLabel(s);
                softwareListPanel.add(softwareLabel);
            }
        } else {
            softwareListPanel.add(createInfoLabel("No software selected"));
        }
        
        softwareSection.add(softwareListPanel, BorderLayout.CENTER);
        contentPanel.add(softwareSection);
        contentPanel.add(Box.createVerticalStrut(10));

        // Specifications section
        JPanel specsSection = createSectionPanel("Recommended Specifications");
        JPanel specsGrid = new JPanel();
        specsGrid.setLayout(new GridLayout(3, 2, 15, 15));
        specsGrid.setOpaque(false);
        specsGrid.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create spec cards
        Map<String, String> specs = getSpecs(softwares);
        
        specsGrid.add(createSpecCard("Operating System", specs.get("OS")));
        specsGrid.add(createSpecCard("Processor", specs.get("Processor")));
        specsGrid.add(createSpecCard("RAM", specs.get("RAM")));
        specsGrid.add(createSpecCard("Storage", specs.get("Storage")));
        specsGrid.add(createSpecCard("GPU", specs.get("GPU")));
        specsGrid.add(createSpecCard("Screen Size", specs.get("Screen")));

        specsSection.add(specsGrid, BorderLayout.CENTER);
        contentPanel.add(specsSection);

        // Scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(240, 243, 250));
        scrollPane.getViewport().setBackground(new Color(240, 243, 250));
        add(scrollPane, BorderLayout.CENTER);

        // Close Button with modern style
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 243, 250));
        buttonPanel.setPreferredSize(new Dimension(900, 90));
        add(buttonPanel, BorderLayout.SOUTH);

        JButton closeBtn = new JButton("Close") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        closeBtn.setPreferredSize(new Dimension(200, 50));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeBtn.setBackground(new Color(52, 152, 219));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to close button
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(new Color(41, 128, 185));
                closeBtn.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(new Color(52, 152, 219));
                closeBtn.setForeground(Color.WHITE);
            }
        });
        
        closeBtn.addActionListener(_ -> dispose());
        buttonPanel.add(closeBtn);
    }


    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }

    private JLabel createSoftwareLabel(String software) {
        JLabel label = new JLabel("  " + software + "  ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(52, 152, 219));
        label.setBackground(new Color(240, 248, 255));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        label.setForeground(new Color(149, 165, 166));
        return label;
    }

    private JPanel createSpecCard(String title, String value) {
        CardPanel card = new CardPanel(title, value);
        card.setPreferredSize(new Dimension(200, 120));
        card.setMinimumSize(new Dimension(200, 120));
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoveredPanel != null && hoveredPanel != card) {
                    hoveredPanel.setHovered(false);
                }
                card.setHovered(true);
                hoveredPanel = card;
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setHovered(false);
                if (hoveredPanel == card) {
                    hoveredPanel = null;
                }
            }
        });
        
        return card;
    }

    // Custom card panel with hover effect
    class CardPanel extends JPanel {
        private JLabel titleLabel;
        private JLabel valueLabel;
        
        public CardPanel(String title, String value) {
            setLayout(new BorderLayout(10, 10));
            setBackground(new Color(250, 251, 252));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(16, 16, 16, 16)
            ));
            
            titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titleLabel.setForeground(new Color(127, 140, 141));
            add(titleLabel, BorderLayout.NORTH);
            
            valueLabel = new JLabel(value != null ? value : "Not specified");
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valueLabel.setForeground(new Color(44, 62, 80));
            valueLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
            add(valueLabel, BorderLayout.CENTER);
        }
        
        public void setHovered(boolean hovered) {
            if (hovered) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 3),
                    new EmptyBorder(14, 14, 14, 14)
                ));
                setBackground(new Color(249, 252, 255));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 207, 220), 2),
                    new EmptyBorder(15, 15, 15, 15)
                ));
                setBackground(Color.WHITE);
            }
        }
        
    }

    private Map<String, String> getSpecs(List<String> softwares) {
        Map<String, String> specs = new HashMap<>();
        
        if (softwares == null || softwares.isEmpty()) {
            specs.put("OS", "Please select software");
            specs.put("Processor", "Please select software");
            specs.put("RAM", "Please select software");
            specs.put("Storage", "Please select software");
            specs.put("GPU", "Please select software");
            specs.put("Screen", "Please select software");
            return specs;
        }

        String query = "SELECT * FROM software_specs WHERE software_name IN (" +
                String.join(",", Collections.nCopies(softwares.size(), "?")) + ")";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            for (int i = 0; i < softwares.size(); i++) {
                ps.setString(i + 1, softwares.get(i));
            }
            ResultSet rs = ps.executeQuery();

            // Collect all specs to find highest priority
            List<String> allOS = new ArrayList<>();
            List<String> allProcessors = new ArrayList<>();
            List<String> allRAM = new ArrayList<>();
            List<String> allStorage = new ArrayList<>();
            List<String> allGPU = new ArrayList<>();
            List<String> allScreen = new ArrayList<>();

            while (rs.next()) {
                allOS.add(rs.getString("os_recommended"));
                allProcessors.add(rs.getString("processor_recommended"));
                allRAM.add(rs.getString("ram_recommended"));
                allStorage.add(rs.getString("storage_recommended"));
                allGPU.add(rs.getString("gpu_recommended"));
                allScreen.add(rs.getString("screen_recommended"));
            }

            // Find highest priority specifications
            specs.put("OS", getHighestPriorityOS(allOS));
            specs.put("Processor", getHighestPriorityProcessor(allProcessors));
            specs.put("RAM", getHighestPriorityRAM(allRAM));
            specs.put("Storage", getHighestPriorityStorage(allStorage));
            specs.put("GPU", getHighestPriorityGPU(allGPU));
            specs.put("Screen", getHighestPriorityScreen(allScreen));

        } catch (Exception e) {
            e.printStackTrace();
            specs.put("Error", "Unable to fetch specifications: " + e.getMessage());
        }

        return specs;
    }

    // Priority methods to determine highest specification
    private String getHighestPriorityOS(List<String> osList) {
        if (osList.isEmpty()) return "Not specified";
        
        // Priority: Windows 11 > Windows 10 > macOS > Linux
        for (String os : osList) {
            if (os.toLowerCase().contains("windows 11")) return os;
        }
        for (String os : osList) {
            if (os.toLowerCase().contains("windows 10")) return os;
        }
        for (String os : osList) {
            if (os.toLowerCase().contains("macos") || os.toLowerCase().contains("mac")) return os;
        }
        for (String os : osList) {
            if (os.toLowerCase().contains("linux")) return os;
        }
        return osList.get(0);
    }

    private String getHighestPriorityProcessor(List<String> processorList) {
        if (processorList.isEmpty()) return "Not specified";
        
        // Priority: i9 > i7 > i5 > Ryzen 9 > Ryzen 7 > Ryzen 5 > others
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("i9")) return proc;
        }
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("i7")) return proc;
        }
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("ryzen 9")) return proc;
        }
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("i5")) return proc;
        }
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("ryzen 7")) return proc;
        }
        for (String proc : processorList) {
            if (proc.toLowerCase().contains("ryzen 5")) return proc;
        }
        return processorList.get(0);
    }

    private String getHighestPriorityRAM(List<String> ramList) {
        if (ramList.isEmpty()) return "Not specified";
        
        // Priority: 32GB > 16GB > 8GB > others
        for (String ram : ramList) {
            if (ram.toLowerCase().contains("32gb") || ram.toLowerCase().contains("32 gb")) return ram;
        }
        for (String ram : ramList) {
            if (ram.toLowerCase().contains("16gb") || ram.toLowerCase().contains("16 gb")) return ram;
        }
        for (String ram : ramList) {
            if (ram.toLowerCase().contains("8gb") || ram.toLowerCase().contains("8 gb")) return ram;
        }
        return ramList.get(0);
    }

    private String getHighestPriorityStorage(List<String> storageList) {
        if (storageList.isEmpty()) return "Not specified";
        
        // Priority: 2TB > 1TB > 512GB > 256GB > others
        for (String storage : storageList) {
            if (storage.toLowerCase().contains("2tb") || storage.toLowerCase().contains("2 tb")) return storage;
        }
        for (String storage : storageList) {
            if (storage.toLowerCase().contains("1tb") || storage.toLowerCase().contains("1 tb")) return storage;
        }
        for (String storage : storageList) {
            if (storage.toLowerCase().contains("512gb") || storage.toLowerCase().contains("512 gb")) return storage;
        }
        for (String storage : storageList) {
            if (storage.toLowerCase().contains("256gb") || storage.toLowerCase().contains("256 gb")) return storage;
        }
        return storageList.get(0);
    }

    private String getHighestPriorityGPU(List<String> gpuList) {
        if (gpuList.isEmpty()) return "Not specified";
        
        // Priority: RTX 4090 > RTX 4080 > RTX 4070 > RTX 4060 > RTX 30 series > GTX series > others
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("rtx 4090")) return gpu;
        }
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("rtx 4080")) return gpu;
        }
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("rtx 4070")) return gpu;
        }
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("rtx 4060")) return gpu;
        }
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("rtx 30")) return gpu;
        }
        for (String gpu : gpuList) {
            if (gpu.toLowerCase().contains("gtx")) return gpu;
        }
        return gpuList.get(0);
    }

    private String getHighestPriorityScreen(List<String> screenList) {
        if (screenList.isEmpty()) return "Not specified";
        
        // Priority: 17" > 15.6" > 14" > 13" > others
        for (String screen : screenList) {
            if (screen.toLowerCase().contains("17")) return screen;
        }
        for (String screen : screenList) {
            if (screen.toLowerCase().contains("15.6") || screen.toLowerCase().contains("15")) return screen;
        }
        for (String screen : screenList) {
            if (screen.toLowerCase().contains("14")) return screen;
        }
        for (String screen : screenList) {
            if (screen.toLowerCase().contains("13")) return screen;
        }
        return screenList.get(0);
    }
}
