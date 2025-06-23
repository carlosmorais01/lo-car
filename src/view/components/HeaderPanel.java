package view.components;

import com.formdev.flatlaf.FlatClientProperties;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import java.io.File;
import javax.imageio.ImageIO;

/**
 * `HeaderPanel` é um componente Swing que representa o cabeçalho da aplicação.
 * Ele contém o logotipo do sistema, um campo de busca com botão, e informações do usuário logado
 * (nome e foto de perfil), além de um botão de configurações que pode ser visível ou não.
 */
public class HeaderPanel extends JPanel {
    private final JTextField searchField;
    private JButton searchButton;
    private JLabel userLabel;
    private JLabel profileIconLabel;
    private JLabel systemLogoLabel;
    private JButton settingsButton;

    /**
     * Construtor de `HeaderPanel` com nome de usuário e caminho para a imagem de perfil.
     * Inicializa a interface do cabeçalho, carregando imagens e configurando componentes.
     *
     * @param userName         O nome do usuário a ser exibido no cabeçalho.
     * @param profileImagePath O caminho da imagem de perfil do usuário. Pode ser um recurso interno ou um caminho de arquivo.
     */
    public HeaderPanel(String userName, String profileImagePath) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 38, 64));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
                systemLogoIcon = new ImageIcon(ImageScaler.getScaledImage(ImageIO.read(new File("src/images/icons/logotipo.png")), 70, 70));
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
            URL gearUrl = getClass().getResource("/images/icons/gear-icon.png");
            if (gearUrl != null) {
                gearIcon = new ImageIcon(ImageScaler.getScaledImage(new ImageIcon(gearUrl).getImage(), 25, 25));
            } else {
                System.err.println("Ícone de engrenagem não encontrado como recurso. Tentando carregar de arquivo.");
                gearIcon = new ImageIcon(ImageScaler.getScaledImage(ImageIO.read(new File("src/images/icons/gear-icon.png")), 25, 25));
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

        ImageIcon lupaIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/icons/lupa-icon.png")));
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
                URL defaultPfpUrl = getClass().getResource("/images/icons/default_pfp.png");
                if (defaultPfpUrl != null) {
                    defaultPfpImage = ImageIO.read(defaultPfpUrl);
                } else {
                    System.err.println("Recurso de PFP padrão não encontrado. Tentando carregar de arquivo local.");
                    File defaultPfpFile = new File("src/images/icons/default_pfp.png");
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


        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightPanel.add(profileIconLabel);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Define um {@link ActionListener} para o ícone de perfil.
     * Qualquer listener anterior é removido para evitar a duplicação de eventos.
     *
     * @param listener O {@link ActionListener} a ser executado quando o ícone de perfil for clicado.
     */
    public void setProfileIconClickListener(ActionListener listener) {
        for (java.awt.event.MouseListener ml : profileIconLabel.getMouseListeners()) {
            profileIconLabel.removeMouseListener(ml);
        }

        profileIconLabel.addMouseListener(new MouseAdapter() {
            /**
             * Manipula o evento de clique do mouse no ícone de perfil.
             * Dispara o {@link ActionListener} fornecido.
             * @param e O evento de mouse.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.actionPerformed(new ActionEvent(profileIconLabel, ActionEvent.ACTION_PERFORMED, "profileIconClicked"));
                }
            }
        });
    }

    /**
     * Retorna o texto atual contido no campo de busca.
     * O texto é limpo de espaços em branco no início e no fim.
     *
     * @return Uma string contendo o texto do campo de busca.
     */
    public String getSearchText() {
        return searchField.getText().trim();
    }

    /**
     * Define a visibilidade do botão de configurações.
     *
     * @param visible true para tornar o botão visível, false para ocultá-lo.
     */
    public void showSettingsButton(boolean visible) {
        settingsButton.setVisible(visible);
    }

    /**
     * Define um {@link ActionListener} para o botão de configurações.
     *
     * @param actionListener O {@link ActionListener} a ser executado quando o botão de configurações for clicado.
     */
    public void setSettingsAction(ActionListener actionListener) {
        settingsButton.addActionListener(actionListener);
    }

    /**
     * Define um {@link ActionListener} para o botão de busca.
     *
     * @param actionListener O {@link ActionListener} a ser executado quando o botão de busca for clicado.
     */
    public void setSearchAction(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    /**
     * Define um {@link ActionListener} para o logotipo do sistema.
     * Remove quaisquer listeners de mouse anônimos ou internos existentes
     * para evitar a duplicação de eventos antes de adicionar o novo listener.
     *
     * @param listener O {@link ActionListener} a ser executado quando o logotipo for clicado.
     */
    public void setLogoClickListener(ActionListener listener) {
        for (java.awt.event.MouseListener ml : systemLogoLabel.getMouseListeners()) {
            if (ml.getClass().getName().contains("$")) {
                systemLogoLabel.removeMouseListener(ml);
            }
        }
        systemLogoLabel.addMouseListener(new MouseAdapter() {
            /**
             * Manipula o evento de clique do mouse no logotipo do sistema.
             * Dispara o {@link ActionListener} fornecido.
             * @param e O evento de mouse.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.actionPerformed(new ActionEvent(systemLogoLabel, ActionEvent.ACTION_PERFORMED, "logoClicked"));
            }
        });
    }

    /**
     * Define o texto no campo de busca.
     *
     * @param text O texto a ser definido no campo de busca.
     */
    public void setSearchText(String text) {
        searchField.setText(text);
    }

    /**
     * Construtor de `HeaderPanel` que permite criar o cabeçalho apenas com o nome de usuário,
     * utilizando um caminho de imagem de perfil padrão (nulo).
     *
     * @param userName O nome do usuário a ser exibido no cabeçalho.
     */
    public HeaderPanel(String userName) {
        this(userName, null);
    }
}