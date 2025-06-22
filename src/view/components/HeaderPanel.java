package view.components;

import com.formdev.flatlaf.FlatClientProperties;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import java.io.File;
import javax.imageio.ImageIO;

public class HeaderPanel extends JPanel {
    private final JTextField searchField;
    private JButton searchButton;
    private JLabel userLabel;
    private JLabel profileIconLabel;
    private JLabel systemLogoLabel;
    private JButton settingsButton;

    public HeaderPanel(String userName, String profileImagePath) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 38, 64));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Esquerda: logotipo do sistema (clicável) e botão de engrenagem (se funcionário)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(0, 5, 0, 15);
        gbcLeft.anchor = GridBagConstraints.CENTER;

        ImageIcon systemLogoIcon = null;
        try {
            URL logoUrl = getClass().getResource("/icons/logotipo.png");
            if (logoUrl != null) {
                systemLogoIcon = new ImageIcon(ImageScaler.getScaledImage(new ImageIcon(logoUrl).getImage(), 70, 70));
            } else {
                // Fallback: tentar carregar como arquivo direto (para desenvolvimento no IDE)
                systemLogoIcon = new ImageIcon(ImageScaler.getScaledImage(ImageIO.read(new File("src/images/logotipo.png")), 70, 70));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar logotipo do sistema: " + e.getMessage());
            systemLogoIcon = new ImageIcon(new byte[0]); // Ícone vazio em caso de erro
        }

        systemLogoLabel = new JLabel(systemLogoIcon);
        systemLogoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(systemLogoLabel, gbcLeft);

        ImageIcon gearIcon = null;
        try {
            URL gearUrl = getClass().getResource("/icons/gear-icon.png"); // Caminho do ícone da engrenagem
            if (gearUrl != null) {
                gearIcon = new ImageIcon(ImageScaler.getScaledImage(new ImageIcon(gearUrl).getImage(), 25, 25)); // Tamanho do ícone
            } else {
                System.err.println("Ícone de engrenagem não encontrado como recurso. Tentando carregar de arquivo.");
                gearIcon = new ImageIcon(ImageScaler.getScaledImage(ImageIO.read(new File("src/images/gear-icon.png")), 25, 25));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone de engrenagem: " + e.getMessage());
            gearIcon = new ImageIcon(new byte[0]);
        }
        settingsButton = new JButton(gearIcon);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setVisible(false); // Inicia invisível
        gbcLeft.gridx = 1; // Coloca ao lado do logotipo
        gbcLeft.insets = new Insets(0, 0, 0, 0); // Ajusta margem
        leftPanel.add(settingsButton, gbcLeft);

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

        ImageIcon lupaIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/lupa-icon.png")));
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
        int pfpSize = 50; // O tamanho da imagem de perfil

        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            try {
                // Tenta carregar a imagem do caminho absoluto
                // Ajustado para obter Image e depois criar ImageIcon escalonado
                Image originalPfpImage = ImageIO.read(new File(profileImagePath));
                if (originalPfpImage != null) {
                    Image scaledPfpImage = ImageScaler.getScaledImage(originalPfpImage, pfpSize, pfpSize);
                    pfpIcon = new ImageIcon(scaledPfpImage);
                } else {
                    System.err.println("Arquivo de foto de perfil não pôde ser lido: " + profileImagePath + ". Usando padrão.");
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar foto de perfil do arquivo: " + e.getMessage());
            } catch (Exception e) { // Captura outras exceções do ImageScaler.getScaledImage
                System.err.println("Erro ao escalar foto de perfil: " + e.getMessage());
            }
        }

        // Se a imagem ainda for nula (não carregou ou erro), use a imagem padrão
        if (pfpIcon == null || pfpIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            try {
                // Tenta carregar pfp padrão como recurso
                java.net.URL defaultPfpUrl = getClass().getResource("/icons/default_pfp.png");
                if (defaultPfpUrl != null) {
                    Image defaultPfpImage = ImageIO.read(defaultPfpUrl);
                    pfpIcon = new ImageIcon(ImageScaler.getScaledImage(defaultPfpImage, pfpSize, pfpSize));
                } else {
                    // Fallback para arquivo direto em tempo de desenvolvimento, se o recurso não for encontrado
                    System.err.println("Recurso de PFP padrão não encontrado. Tentando carregar de arquivo.");
                    Image defaultPfpImage = ImageIO.read(new File("src/images/default_pfp.png"));
                    pfpIcon = new ImageIcon(ImageScaler.getScaledImage(defaultPfpImage, pfpSize, pfpSize));
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar foto de perfil padrão: " + e.getMessage());
                pfpIcon = new ImageIcon(new byte[0]); // ImageIcon vazio se tudo falhar
            } catch (Exception e) {
                System.err.println("Erro ao escalar foto de perfil padrão: " + e.getMessage());
                pfpIcon = new ImageIcon(new byte[0]);
            }
        }

        profileIconLabel = new JLabel(pfpIcon); // Voltou a ser um JLabel simples com o ImageIcon
        profileIconLabel.setPreferredSize(new Dimension(pfpSize, pfpSize)); // Define o tamanho preferencial

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIconLabel); // Adiciona o JLabel de volta

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }

    public void showSettingsButton(boolean visible) {
        settingsButton.setVisible(visible);
    }

    public void setSettingsAction(ActionListener actionListener) {
        settingsButton.addActionListener(actionListener);
    }

    public void setSearchAction(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    public void setLogoClickListener(ActionListener listener) {
        systemLogoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                listener.actionPerformed(new ActionEvent(systemLogoLabel, ActionEvent.ACTION_PERFORMED, "logoClicked"));
            }
        });
    }

    public void setSearchText(String text) {
        searchField.setText(text);
    }

    public HeaderPanel(String userName) {
        this(userName, null);
    }
}