package view.components;

import com.formdev.flatlaf.FlatClientProperties;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class HeaderPanel extends JPanel {
    private final JTextField searchField;
    private JButton searchButton;
    private JLabel userLabel;
    private JLabel profileIconLabel;

    public HeaderPanel(String userName, String profileImagePath) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 38, 64));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(0, 0, 0, 0);
        gbcLeft.anchor = GridBagConstraints.CENTER;

        Image logo = ImageScaler.scaleImage("src/images/logotipo.png", 70, 70);
        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        leftPanel.add(logoLabel, gbcLeft);

        // Centro: campo de busca (mantido como está)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar veículo");
        searchField.setMaximumSize(new Dimension(250, 45));
        searchField.setPreferredSize(new Dimension(250, 45));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setCaretColor(Color.GRAY);

        searchField.addActionListener(e -> {
            if (searchButton.getActionListeners().length > 0) {
                searchButton.getActionListeners()[0].actionPerformed(new ActionEvent(searchButton, ActionEvent.ACTION_PERFORMED, "search"));
            }
        });

        ImageIcon lupaIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/lupa-icon.png")));
        Image scaledLupa = ImageScaler.getScaledImage(lupaIcon.getImage(), 20, 20);
        searchButton = new JButton(new ImageIcon(scaledLupa));
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(searchField, gbc);

        gbc.gridx = 1;
        centerPanel.add(Box.createRigidArea(new Dimension(5, 0)), gbc);

        gbc.gridx = 2;
        centerPanel.add(searchButton, gbc);

        // Direita: nome do usuário e foto de perfil
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        userLabel = new JLabel(userName);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD, 18f));

        ImageIcon pfpIcon = null;
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            try {
                pfpIcon = new ImageIcon(ImageScaler.getScaledImage(new ImageIcon(profileImagePath).getImage(), 50, 50));
            } catch (Exception e) {
                System.err.println("Erro ao carregar foto de perfil: " + e.getMessage());
                pfpIcon = new ImageIcon(ImageScaler.scaleImage("src/images/pfp.png", 50, 50));
            }
        } else {
            pfpIcon = new ImageIcon(ImageScaler.scaleImage("src/images/pfp.png", 50, 50));
        }

        profileIconLabel = new JLabel(pfpIcon);

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIconLabel);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }
    public void setSearchAction(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    public HeaderPanel(String userName) {
        this(userName, null);
    }
}