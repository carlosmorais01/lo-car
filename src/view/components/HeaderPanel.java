package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {
    private final JTextField searchField;
    private JButton searchButton;
    private JLabel userLabel;
    private JLabel profileIcon;

    public HeaderPanel(String userName) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 38, 64));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Esquerda: ícone e nome do sistema
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JLabel logoLabel = new JLabel(new ImageIcon("assets/logo.png")); // Coloque seu caminho correto
        leftPanel.add(new JLabel(new ImageIcon("assets/menu.png"))); // Ícone de menu (opcional)
        leftPanel.add(logoLabel);

        // Centro: campo de busca
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setOpaque(false);
        searchField = new JTextField("Veiculo XXXX", 20);
        searchField.setMaximumSize(new Dimension(250, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setCaretColor(Color.GRAY);

        searchButton = new JButton(new ImageIcon("assets/search-icon.png"));
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);


        centerPanel.add(searchField);
        centerPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        centerPanel.add(searchButton);

        // Direita: usuário e perfil
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        userLabel = new JLabel(userName);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        profileIcon = new JLabel(new ImageIcon("assets/user-icon.png"));
        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIcon);

        // Adiciona tudo ao painel principal
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
