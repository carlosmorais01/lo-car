package view.components;

import com.formdev.flatlaf.FlatClientProperties;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {
    private final JTextField searchField;
    private JButton searchButton;
    private JLabel userLabel;
    private JLabel profileIcon;

    public HeaderPanel(String userName) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 38, 64));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Esquerda: ícone e nome do sistema
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JLabel logoLabel = new JLabel(new ImageIcon("assets/logo.png"));
        leftPanel.add(new JLabel(new ImageIcon("assets/menu.png")));
        leftPanel.add(logoLabel);

        // Centro: campo de busca
        JPanel centerPanel = new JPanel(new GridBagLayout()); // <-- Altera para GridBagLayout
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

        searchField.addActionListener(e -> { //
            if (searchButton.getActionListeners().length > 0) {
                searchButton.getActionListeners()[0].actionPerformed(new ActionEvent(searchButton, ActionEvent.ACTION_PERFORMED, "search"));
            }
        });

        searchButton = new JButton(new ImageIcon("src/images/lupa-icon.png"));
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        userLabel = new JLabel(userName);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD, 18f));
        profileIcon = new JLabel(new ImageIcon(ImageScaler.scaleImage("src/images/pfp.png", 50, 50))) ;

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIcon);

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
}