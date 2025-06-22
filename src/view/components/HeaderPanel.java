package view.components;

import com.formdev.flatlaf.FlatClientProperties;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // Importar MouseAdapter
import java.awt.event.MouseEvent; // Importar MouseEvent
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
                systemLogoIcon = new ImageIcon(ImageScaler.getScaledImage(ImageIO.read(new File("src/images/logotipo.png")), 70, 70));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar logotipo do sistema: " + e.getMessage());
            systemLogoIcon = new ImageIcon(new byte[0]);
        }

        systemLogoLabel = new JLabel(systemLogoIcon);
        systemLogoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(systemLogoLabel, gbcLeft);

        ImageIcon gearIcon = null;
        try {
            URL gearUrl = getClass().getResource("/icons/gear-icon.png");
            if (gearUrl != null) {
                gearIcon = new ImageIcon(ImageScaler.getScaledImage(new ImageIcon(gearUrl).getImage(), 25, 25));
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
        settingsButton.setVisible(false);
        gbcLeft.gridx = 1;
        gbcLeft.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(settingsButton, gbcLeft);

        // Centro: campo de busca
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
        int pfpSize = 50;

        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            try {
                Image loadedPfpImage = null;
                File filePfp = new File(profileImagePath);
                if (filePfp.exists()) {
                    loadedPfpImage = ImageIO.read(filePfp);
                }

                if (loadedPfpImage == null) {
                    URL resourcePfpUrl = getClass().getResource(profileImagePath);
                    if (resourcePfpUrl != null) {
                        loadedPfpImage = ImageIO.read(resourcePfpUrl);
                    }
                }

                if (loadedPfpImage != null) {
                    Image scaledPfpImage = ImageScaler.getScaledImage(loadedPfpImage, pfpSize, pfpSize);
                    pfpIcon = new ImageIcon(scaledPfpImage);
                } else {
                    System.err.println("PFP do usuário não encontrada em nenhum caminho: " + profileImagePath);
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar PFP: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar/escalar PFP: " + e.getMessage());
            }
        }

        if (pfpIcon == null || (pfpIcon.getImageLoadStatus() != -1 && pfpIcon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                Image defaultPfpImage = null;
                URL defaultPfpUrl = getClass().getResource("/images/default_pfp.png");
                if (defaultPfpUrl != null) {
                    defaultPfpImage = ImageIO.read(defaultPfpUrl);
                } else {
                    System.err.println("Recurso de PFP padrão não encontrado. Tentando carregar de arquivo local.");
                    File defaultPfpFile = new File("src/images/default_pfp.png");
                    if (defaultPfpFile.exists()) {
                        defaultPfpImage = ImageIO.read(defaultPfpFile);
                    } else {
                        System.err.println("Fallback de PFP padrão local também falhou.");
                    }
                }

                if (defaultPfpImage != null) {
                    pfpIcon = new ImageIcon(ImageScaler.getScaledImage(defaultPfpImage, pfpSize, pfpSize));
                } else {
                    System.err.println("Nenhuma PFP padrão carregada.");
                    pfpIcon = new ImageIcon(new byte[0]);
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar PFP padrão: " + e.getMessage());
                pfpIcon = new ImageIcon(new byte[0]);
            } catch (Exception e) {
                System.err.println("Erro inesperado ao escalar PFP padrão: " + e.getMessage());
                pfpIcon = new ImageIcon(new byte[0]);
            }
        }

        profileIconLabel = new JLabel(pfpIcon);
        profileIconLabel.setPreferredSize(new Dimension(pfpSize, pfpSize));
        profileIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // O MouseListener será adicionado externamente pelo setProfileIconClickListener

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIconLabel);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // Método refatorado para ser a única forma de definir o listener do ícone de perfil
    public void setProfileIconClickListener(ActionListener listener) {
        // Primeiro, remova TODOS os MouseListeners existentes para evitar duplicação.
        // Isso é seguro porque não estamos removendo MouseListeners que o próprio Swing pode ter adicionado.
        for (java.awt.event.MouseListener ml : profileIconLabel.getMouseListeners()) {
            profileIconLabel.removeMouseListener(ml);
        }
        // Agora, adicione um novo MouseListener anônimo que irá disparar o ActionListener
        profileIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.actionPerformed(new ActionEvent(profileIconLabel, ActionEvent.ACTION_PERFORMED, "profileIconClicked"));
                }
            }
        });
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
        // Remover MouseAdapter anônimo se já não tiver sido
        for (java.awt.event.MouseListener ml : systemLogoLabel.getMouseListeners()) {
            // Heurística para anônimo ou auto-gerado
            // (Uso de contains("$") é uma heurística, idealmente você saberia o tipo exato do seu próprio listener)
            if (ml.getClass().getName().contains("$")) {
                systemLogoLabel.removeMouseListener(ml);
            }
        }
        systemLogoLabel.addMouseListener(new MouseAdapter() { // Usar MouseAdapter aqui também
            @Override
            public void mouseClicked(MouseEvent e) {
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