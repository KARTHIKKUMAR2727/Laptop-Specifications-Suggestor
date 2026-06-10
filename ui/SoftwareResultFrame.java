package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import db.DBConnection;
import ui.SpecInputFrame.UserSpecs;

public class SoftwareResultFrame extends JFrame {
    private UserSpecs userSpecs;
    private SpecComparator comparator;
    private List<String> selectedSoftwareNames;

    public SoftwareResultFrame(UserSpecs userSpecs) {
        this(userSpecs, new ArrayList<>());
    }

    public SoftwareResultFrame(UserSpecs userSpecs, List<String> selectedSoftwareNames) {
        this.userSpecs = userSpecs;
        this.selectedSoftwareNames = selectedSoftwareNames != null ? selectedSoftwareNames : new ArrayList<>();
        this.comparator = new SpecComparator();
        
        setTitle("Compatible Software Recommendations");
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
                Color color1 = new Color(156, 39, 176);
                Color color2 = new Color(233, 30, 99);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(900, 90));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 30));
        add(titlePanel, BorderLayout.NORTH);

        
        JLabel titleLabel = new JLabel("Compatible Software");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 243, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // User Specs Section
        JPanel userSpecsSection = createSectionPanel("Your Specifications");
        JPanel userSpecsGrid = new JPanel();
        userSpecsGrid.setLayout(new GridLayout(3, 2, 15, 15));
        userSpecsGrid.setOpaque(false);
        userSpecsGrid.setBorder(new EmptyBorder(15, 15, 15, 15));

        userSpecsGrid.add(createSpecCard("OS", userSpecs.os));
        userSpecsGrid.add(createSpecCard("Processor", userSpecs.processor.isEmpty() ? "Not specified" : userSpecs.processor));
        userSpecsGrid.add(createSpecCard("RAM", userSpecs.ram));
        userSpecsGrid.add(createSpecCard("Storage", userSpecs.storage));
        userSpecsGrid.add(createSpecCard("GPU", userSpecs.gpu.isEmpty() ? "Not specified" : userSpecs.gpu));
        userSpecsGrid.add(createSpecCard("Screen", userSpecs.screen));

        userSpecsSection.add(userSpecsGrid, BorderLayout.CENTER);
        contentPanel.add(userSpecsSection);
        contentPanel.add(Box.createVerticalStrut(15));

        // Check selected software first
        List<SoftwareCompatibility> selectedSoftwareResults = new ArrayList<>();
        List<SoftwareCompatibility> matchedSelectedSoftware = new ArrayList<>();
        List<SoftwareCompatibility> unmatchedSelectedSoftware = new ArrayList<>();

        if (!selectedSoftwareNames.isEmpty()) {
            selectedSoftwareResults = checkSelectedSoftware();
            for (SoftwareCompatibility sw : selectedSoftwareResults) {
                if (sw.compatibilityScore >= 75) {
                    matchedSelectedSoftware.add(sw);
                } else {
                    unmatchedSelectedSoftware.add(sw);
                }
            }
        }

        // Display Selected Software Results
        if (!selectedSoftwareResults.isEmpty()) {
            JPanel selectedSoftwareSection = createSectionPanel("Selected Software Check Results");
            JPanel selectedSoftwareListPanel = new JPanel();
            selectedSoftwareListPanel.setLayout(new BoxLayout(selectedSoftwareListPanel, BoxLayout.Y_AXIS));
            selectedSoftwareListPanel.setOpaque(false);
            selectedSoftwareListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Show matched software (75%+)
            if (!matchedSelectedSoftware.isEmpty()) {
                JLabel matchedLabel = new JLabel("Software that matches your specifications (75%+ match):");
                matchedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                matchedLabel.setForeground(new Color(46, 204, 113));
                matchedLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
                selectedSoftwareListPanel.add(matchedLabel);

                for (SoftwareCompatibility sw : matchedSelectedSoftware) {
                    selectedSoftwareListPanel.add(createSoftwareCard(sw));
                    selectedSoftwareListPanel.add(Box.createVerticalStrut(10));
                }
            }

            // Show unmatched software (<75%)
            if (!unmatchedSelectedSoftware.isEmpty()) {
                if (!matchedSelectedSoftware.isEmpty()) {
                    selectedSoftwareListPanel.add(Box.createVerticalStrut(15));
                }
                JLabel unmatchedLabel = new JLabel("Software that does not match well (<75% match):");
                unmatchedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                unmatchedLabel.setForeground(new Color(231, 76, 60));
                unmatchedLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
                selectedSoftwareListPanel.add(unmatchedLabel);

                for (SoftwareCompatibility sw : unmatchedSelectedSoftware) {
                    selectedSoftwareListPanel.add(createSoftwareCard(sw));
                    selectedSoftwareListPanel.add(Box.createVerticalStrut(10));
                }
            }

            selectedSoftwareSection.add(selectedSoftwareListPanel, BorderLayout.CENTER);
            contentPanel.add(selectedSoftwareSection);
            contentPanel.add(Box.createVerticalStrut(15));
        }

        // Get compatible software (75%+ match only)
        List<SoftwareCompatibility> compatibleSoftware = getCompatibleSoftware();
        Set<String> selectedNamesSet = new HashSet<>(selectedSoftwareNames);
        
        // Filter out already selected software from compatible list
        List<SoftwareCompatibility> compatibleFiltered = new ArrayList<>();
        for (SoftwareCompatibility sw : compatibleSoftware) {
            if (!selectedNamesSet.contains(sw.softwareName)) {
                compatibleFiltered.add(sw);
            }
        }

        // Show compatible software or alternatives
        if (!matchedSelectedSoftware.isEmpty()) {
            // If selected software matches, show other compatible software
            if (!compatibleFiltered.isEmpty()) {
                JPanel softwareSection = createSectionPanel("Other Compatible Software (75%+ Match)");
                JPanel softwareListPanel = new JPanel();
                softwareListPanel.setLayout(new BoxLayout(softwareListPanel, BoxLayout.Y_AXIS));
                softwareListPanel.setOpaque(false);
                softwareListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

                for (SoftwareCompatibility sw : compatibleFiltered) {
                    softwareListPanel.add(createSoftwareCard(sw));
                    softwareListPanel.add(Box.createVerticalStrut(10));
                }

                softwareSection.add(softwareListPanel, BorderLayout.CENTER);
                contentPanel.add(softwareSection);
            }
        } else {
            // If no selected software matches or no software selected, show alternatives
            if (!unmatchedSelectedSoftware.isEmpty() || selectedSoftwareNames.isEmpty()) {
                if (!compatibleFiltered.isEmpty()) {
                    JPanel softwareSection = createSectionPanel("Recommended Compatible Software (75%+ Match)");
                    JPanel softwareListPanel = new JPanel();
                    softwareListPanel.setLayout(new BoxLayout(softwareListPanel, BoxLayout.Y_AXIS));
                    softwareListPanel.setOpaque(false);
                    softwareListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

                    JLabel infoLabel = new JLabel("These software match your specifications (75%+ compatibility):");
                    infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    infoLabel.setForeground(new Color(100, 100, 100));
                    infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
                    softwareListPanel.add(infoLabel);

                    for (SoftwareCompatibility sw : compatibleFiltered) {
                        softwareListPanel.add(createSoftwareCard(sw));
                        softwareListPanel.add(Box.createVerticalStrut(10));
                    }

                    softwareSection.add(softwareListPanel, BorderLayout.CENTER);
                    contentPanel.add(softwareSection);
                    contentPanel.add(Box.createVerticalStrut(15));
                }

                // Show alternative recommendations if selected software doesn't match
                if (!unmatchedSelectedSoftware.isEmpty()) {
                    List<SoftwareCompatibility> alternativeSoftware = getAlternativeSoftware(compatibleFiltered, unmatchedSelectedSoftware);
                    if (!alternativeSoftware.isEmpty()) {
                        JPanel alternativeSection = createSectionPanel("Alternative Recommendations");
                        JPanel alternativeListPanel = new JPanel();
                        alternativeListPanel.setLayout(new BoxLayout(alternativeListPanel, BoxLayout.Y_AXIS));
                        alternativeListPanel.setOpaque(false);
                        alternativeListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

                        JLabel altInfoLabel = new JLabel("Alternative software that match your specifications better (75%+ match):");
                        altInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        altInfoLabel.setForeground(new Color(100, 100, 100));
                        altInfoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
                        alternativeListPanel.add(altInfoLabel);

                        for (SoftwareCompatibility sw : alternativeSoftware) {
                            alternativeListPanel.add(createSoftwareCard(sw));
                            alternativeListPanel.add(Box.createVerticalStrut(10));
                        }

                        alternativeSection.add(alternativeListPanel, BorderLayout.CENTER);
                        contentPanel.add(alternativeSection);
                    } else if (compatibleFiltered.isEmpty()) {
                        // No alternatives found
                        JPanel noResultsPanel = createSectionPanel("No Recommendations Found");
                        JLabel noResultsLabel = new JLabel("No software found with 75%+ compatibility for your specifications.");
                        noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        noResultsLabel.setForeground(new Color(149, 165, 166));
                        noResultsLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
                        noResultsPanel.add(noResultsLabel, BorderLayout.CENTER);
                        contentPanel.add(noResultsPanel);
                    }
                } else if (compatibleFiltered.isEmpty() && selectedSoftwareNames.isEmpty()) {
                    // No software selected and no compatible software found
                    JPanel noResultsPanel = createSectionPanel("No Compatible Software Found");
                    JLabel noResultsLabel = new JLabel("No software found with 75%+ compatibility for your specifications.");
                    noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    noResultsLabel.setForeground(new Color(149, 165, 166));
                    noResultsLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
                    noResultsPanel.add(noResultsLabel, BorderLayout.CENTER);
                    contentPanel.add(noResultsPanel);
                }
            }
        }

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(240, 243, 250));
        scrollPane.getViewport().setBackground(new Color(240, 243, 250));
        add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 243, 250));
        buttonPanel.setPreferredSize(new Dimension(900, 90));
        add(buttonPanel, BorderLayout.SOUTH);

        JButton closeBtn = createStyledButton("Close", new Color(156, 39, 176));
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

    private JPanel createSpecCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(250, 251, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value != null ? value : "Not specified");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(new Color(44, 62, 80));
        valueLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createSoftwareCard(SoftwareCompatibility sw) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(new Color(252, 253, 254));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        // Software name and compatibility score
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(sw.softwareName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(nameLabel, BorderLayout.WEST);

        // Compatibility badge
        Color badgeColor = sw.compatibilityScore >= 80 ? new Color(46, 204, 113) :
                          sw.compatibilityScore >= 60 ? new Color(241, 196, 15) :
                          new Color(231, 76, 60);
        JLabel scoreLabel = new JLabel(sw.compatibilityScore + "% Match");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBackground(badgeColor);
        scoreLabel.setOpaque(true);
        scoreLabel.setBorder(new EmptyBorder(6, 14, 6, 14));
        headerPanel.add(scoreLabel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Compatibility details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        if (sw.osMatch != null) {
            detailsPanel.add(createDetailRow("OS", sw.osMatch, sw.osCompatible));
        }
        if (sw.processorMatch != null) {
            detailsPanel.add(createDetailRow("Processor", sw.processorMatch, sw.processorCompatible));
        }
        if (sw.ramMatch != null) {
            detailsPanel.add(createDetailRow("RAM", sw.ramMatch, sw.ramCompatible));
        }
        if (sw.storageMatch != null) {
            detailsPanel.add(createDetailRow("Storage", sw.storageMatch, sw.storageCompatible));
        }
        if (sw.gpuMatch != null) {
            detailsPanel.add(createDetailRow("GPU", sw.gpuMatch, sw.gpuCompatible));
        }
        if (sw.screenMatch != null) {
            detailsPanel.add(createDetailRow("Screen", sw.screenMatch, sw.screenCompatible));
        }

        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetailRow(String label, String value, boolean compatible) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComponent.setForeground(new Color(127, 140, 141));
        labelComponent.setPreferredSize(new Dimension(80, 20));
        row.add(labelComponent, BorderLayout.WEST);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueComponent.setForeground(compatible ? new Color(46, 204, 113) : new Color(231, 76, 60));
        row.add(valueComponent, BorderLayout.CENTER);

        JLabel statusIcon = new JLabel(compatible ? "[OK]" : "[FAIL]");
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusIcon.setForeground(compatible ? new Color(46, 204, 113) : new Color(231, 76, 60));
        statusIcon.setPreferredSize(new Dimension(50, 20));
        row.add(statusIcon, BorderLayout.EAST);

        return row;
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
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

    private List<SoftwareCompatibility> checkSelectedSoftware() {
        List<SoftwareCompatibility> results = new ArrayList<>();
        
        if (selectedSoftwareNames == null || selectedSoftwareNames.isEmpty()) {
            return results;
        }

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM software_specs WHERE software_name IN (" +
                    String.join(",", Collections.nCopies(selectedSoftwareNames.size(), "?")) + ")";
            PreparedStatement ps = con.prepareStatement(query);
            
            for (int i = 0; i < selectedSoftwareNames.size(); i++) {
                ps.setString(i + 1, selectedSoftwareNames.get(i));
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String softwareName = rs.getString("software_name");
                String reqOS = rs.getString("os_recommended");
                String reqProcessor = rs.getString("processor_recommended");
                String reqRAM = rs.getString("ram_recommended");
                String reqStorage = rs.getString("storage_recommended");
                String reqGPU = rs.getString("gpu_recommended");
                String reqScreen = rs.getString("screen_recommended");

                SoftwareCompatibility comp = comparator.checkCompatibility(
                    userSpecs, softwareName, reqOS, reqProcessor, reqRAM, reqStorage, reqGPU, reqScreen
                );

                results.add(comp);
            }

            // Sort by compatibility score (highest first)
            results.sort((a, b) -> Integer.compare(b.compatibilityScore, a.compatibilityScore));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error checking selected software: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        return results;
    }

    private List<SoftwareCompatibility> getCompatibleSoftware() {
        List<SoftwareCompatibility> compatible = new ArrayList<>();
        
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM software_specs");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String softwareName = rs.getString("software_name");
                String reqOS = rs.getString("os_recommended");
                String reqProcessor = rs.getString("processor_recommended");
                String reqRAM = rs.getString("ram_recommended");
                String reqStorage = rs.getString("storage_recommended");
                String reqGPU = rs.getString("gpu_recommended");
                String reqScreen = rs.getString("screen_recommended");

                SoftwareCompatibility comp = comparator.checkCompatibility(
                    userSpecs, softwareName, reqOS, reqProcessor, reqRAM, reqStorage, reqGPU, reqScreen
                );

                // Only add software with 75% or higher compatibility
                if (comp.compatibilityScore >= 75) {
                    compatible.add(comp);
                }
            }

            // Sort by compatibility score (highest first)
            compatible.sort((a, b) -> Integer.compare(b.compatibilityScore, a.compatibilityScore));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading software: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        return compatible;
    }

    private List<SoftwareCompatibility> getAlternativeSoftware(List<SoftwareCompatibility> compatibleSoftware, List<SoftwareCompatibility> unmatchedSelected) {
        List<SoftwareCompatibility> alternatives = new ArrayList<>();
        Set<String> compatibleNames = new HashSet<>();
        Set<String> selectedNames = new HashSet<>();
        
        for (SoftwareCompatibility sw : compatibleSoftware) {
            compatibleNames.add(sw.softwareName);
        }
        
        for (SoftwareCompatibility sw : unmatchedSelected) {
            selectedNames.add(sw.softwareName);
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM software_specs");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String softwareName = rs.getString("software_name");
                
                // Skip if already in compatible list or in selected software
                if (compatibleNames.contains(softwareName) || selectedNames.contains(softwareName)) {
                    continue;
                }

                String reqOS = rs.getString("os_recommended");
                String reqProcessor = rs.getString("processor_recommended");
                String reqRAM = rs.getString("ram_recommended");
                String reqStorage = rs.getString("storage_recommended");
                String reqGPU = rs.getString("gpu_recommended");
                String reqScreen = rs.getString("screen_recommended");

                SoftwareCompatibility comp = comparator.checkCompatibility(
                    userSpecs, softwareName, reqOS, reqProcessor, reqRAM, reqStorage, reqGPU, reqScreen
                );

                // Add as alternative if it has 75% or higher compatibility
                if (comp.compatibilityScore >= 75 && shouldSuggestAsAlternative(comp, compatibleSoftware)) {
                    alternatives.add(comp);
                }
            }

            // Sort alternatives by compatibility score (highest first)
            alternatives.sort((a, b) -> Integer.compare(b.compatibilityScore, a.compatibilityScore));

            // Limit to top 5 alternatives
            if (alternatives.size() > 5) {
                alternatives = alternatives.subList(0, 5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return alternatives;
    }

    private boolean shouldSuggestAsAlternative(SoftwareCompatibility candidate, List<SoftwareCompatibility> compatibleSoftware) {
        // Suggest if OS is compatible (good base requirement)
        if (candidate.osCompatible) {
            // If we have compatible software, only suggest alternatives that are better in some way
            // or have significantly lower requirements
            if (!compatibleSoftware.isEmpty()) {
                // Check if candidate has lower requirements in key areas
                boolean hasLowerRequirements = false;
                
                // Check RAM
                if (candidate.ramCompatible && candidate.ramMatch != null) {
                    int candidateRAM = extractRAMValue(candidate.ramMatch);
                    for (SoftwareCompatibility sw : compatibleSoftware) {
                        if (sw.ramMatch != null) {
                            int swRAM = extractRAMValue(sw.ramMatch);
                            if (candidateRAM < swRAM) {
                                hasLowerRequirements = true;
                                break;
                            }
                        }
                    }
                }
                
                // Check Storage
                if (candidate.storageCompatible && candidate.storageMatch != null) {
                    int candidateStorage = extractStorageValue(candidate.storageMatch);
                    for (SoftwareCompatibility sw : compatibleSoftware) {
                        if (sw.storageMatch != null) {
                            int swStorage = extractStorageValue(sw.storageMatch);
                            if (candidateStorage < swStorage) {
                                hasLowerRequirements = true;
                                break;
                            }
                        }
                    }
                }

                // Suggest if it has lower requirements or good compatibility score (75%+)
                return hasLowerRequirements || candidate.compatibilityScore >= 75;
            } else {
                // No compatible software found, suggest any with 75%+ compatibility
                return candidate.compatibilityScore >= 75;
            }
        }
        
        return false;
    }

    private int extractRAMValue(String ram) {
        if (ram == null) return 0;
        String ramLower = ram.toLowerCase().replaceAll("\\s+", "");
        if (ramLower.contains("64gb") || ramLower.contains("64")) return 64;
        if (ramLower.contains("32gb") || ramLower.contains("32")) return 32;
        if (ramLower.contains("16gb") || ramLower.contains("16")) return 16;
        if (ramLower.contains("8gb") || ramLower.contains("8")) return 8;
        if (ramLower.contains("4gb") || ramLower.contains("4")) return 4;
        return 0;
    }

    private int extractStorageValue(String storage) {
        if (storage == null) return 0;
        String storageLower = storage.toLowerCase().replaceAll("\\s+", "");
        if (storageLower.contains("4tb") || storageLower.contains("4000gb")) return 4000;
        if (storageLower.contains("2tb") || storageLower.contains("2000gb")) return 2000;
        if (storageLower.contains("1tb") || storageLower.contains("1000gb")) return 1000;
        if (storageLower.contains("512gb") || storageLower.contains("512")) return 512;
        if (storageLower.contains("256gb") || storageLower.contains("256")) return 256;
        if (storageLower.contains("128gb") || storageLower.contains("128")) return 128;
        return 0;
    }

    // Inner class to hold software compatibility information
    public static class SoftwareCompatibility {
        String softwareName;
        int compatibilityScore;
        String osMatch;
        boolean osCompatible;
        String processorMatch;
        boolean processorCompatible;
        String ramMatch;
        boolean ramCompatible;
        String storageMatch;
        boolean storageCompatible;
        String gpuMatch;
        boolean gpuCompatible;
        String screenMatch;
        boolean screenCompatible;
    }
}

