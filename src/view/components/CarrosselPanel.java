package view.components;

import entities.Veiculo;
import util.ImageScaler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class CarrosselPanel extends JPanel {

    private List<Veiculo> veiculos;
    private int currentIndex = 0;
    private JLabel imageLabel;
    private JLabel descriptionLabel;
    private Timer timer;
    private JPanel dotsPanel;
    private JLayeredPane imageLayeredPane; // Campo da classe

    public CarrosselPanel(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 600));
        setMaximumSize(new Dimension(1200, 600));
        setOpaque(false);

        setupCarrosselUI();
        startAutoSlide();
    }

    private void setupCarrosselUI() {
        // JLayeredPane para sobrepor a imagem e a descrição
        imageLayeredPane = new JLayeredPane();
        imageLayeredPane.setLayout(new OverlayLayout(imageLayeredPane));

        // ImageLabel como a camada base
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        imageLayeredPane.add(imageLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        descriptionPanel.setOpaque(false); // Deixa transparente para não cobrir imagem

        descriptionLabel = new JLabel();
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setBackground(new Color(0, 0, 0, 150));
        descriptionLabel.setOpaque(true);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD, 14f));

        descriptionPanel.add(descriptionLabel);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(descriptionPanel, BorderLayout.SOUTH);

        imageLayeredPane.add(container, JLayeredPane.PALETTE_LAYER);


        // Adiciona o imageLayeredPane ao centro do CarrosselPanel
        add(imageLayeredPane, BorderLayout.CENTER);

        // Botões de navegação (setas) nas laterais do CarrosselPanel
        JButton prevButton = createArrowButton("botao_esquerdo.png", -1);
        JButton nextButton = createArrowButton("botao_direito.png", 1);

        add(prevButton, BorderLayout.WEST);
        add(nextButton, BorderLayout.EAST);

        // Painel das "bolinhas" (no SOUTH do CarrosselPanel)
        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        dotsPanel.setOpaque(false);
        add(dotsPanel, BorderLayout.SOUTH);

        // ComponentListener para redimensionar a imagem do carrossel quando o painel for redimensionado
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateImageSize();
            }
        });

        // Chamada inicial para carregar a primeira imagem e configurar bolinhas
        updateCarrossel();
        // Garante que o redimensionamento inicial ocorra após o layout ter sido calculado
        SwingUtilities.invokeLater(this::updateImageSize);
    }

    private JButton createArrowButton(String iconPath, int direction) {
        ImageIcon arrowIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/" + iconPath)));
        Image scaledArrow = ImageScaler.getScaledImage(arrowIcon.getImage(), 40, 40);
        JButton button = new JButton(new ImageIcon(scaledArrow));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMargin(new Insets(0, 10, 0, 10)); // Top, Left, Bottom, Right padding

        button.addActionListener(e -> {
            stopAutoSlide();
            currentIndex = (currentIndex + direction + veiculos.size()) % veiculos.size();
            updateCarrossel();
            startAutoSlide();
        });
        return button;
    }

    private void startAutoSlide() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(8000, e -> {
            currentIndex = (currentIndex + 1) % veiculos.size();
            updateCarrossel();
        });
        timer.start();
    }

    private void stopAutoSlide() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateCarrossel() {
        if (veiculos.isEmpty()) {
            imageLabel.setIcon(null);
            descriptionLabel.setText("Nenhum veículo disponível no carrossel.");
            dotsPanel.removeAll();
            revalidate();
            repaint();
            return;
        }

        Veiculo currentVeiculo = veiculos.get(currentIndex);
        Image carImage = null;
        try {
            // Tenta carregar como recurso do classpath (preferencial)
            URL imageUrl = getClass().getResource(currentVeiculo.getCaminhoFoto());
            if (imageUrl != null) {
                carImage = ImageIO.read(imageUrl);
            } else {
                // Fallback para carregamento direto do arquivo (para desenvolvimento)
                System.err.println("Recurso de imagem não encontrado: " + currentVeiculo.getCaminhoFoto() + ". Tentando carregar como arquivo direto.");
                File projectRoot = new File(System.getProperty("user.dir"));
                File resolvedFile = new File(projectRoot, currentVeiculo.getCaminhoFoto().startsWith("/") ? currentVeiculo.getCaminhoFoto().substring(1) : currentVeiculo.getCaminhoFoto());
                if (resolvedFile.exists()) {
                    carImage = ImageIO.read(resolvedFile);
                } else {
                    System.err.println("Fallback para arquivo local também falhou: " + resolvedFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem do carrossel para " + currentVeiculo.getNome() + ": " + e.getMessage());
            e.printStackTrace();
        }

        if (carImage != null) {
            // Define o ícone com a imagem carregada. O redimensionamento será feito em updateImageSize().
            imageLabel.setIcon(new ImageIcon(carImage));
            // Atualiza o tamanho da imagem imediatamente após a mudança de imagem
            updateImageSize();
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("Imagem Indisponível");
        }
        descriptionLabel.setText(currentVeiculo.getDescricao());

        // Atualiza as bolinhas
        dotsPanel.removeAll();
        for (int i = 0; i < veiculos.size(); i++) {
            JButton dot = new JButton("•");
            dot.setFont(dot.getFont().deriveFont(Font.BOLD, 20f));
            dot.setForeground(i == currentIndex ? UIManager.getColor("Button.background") : Color.LIGHT_GRAY);
            dot.setContentAreaFilled(false);
            dot.setBorderPainted(false);
            dot.setFocusPainted(false);
            int dotIndex = i;
            dot.addActionListener(e -> {
                stopAutoSlide();
                currentIndex = dotIndex;
                updateCarrossel();
                startAutoSlide();
            });
            dotsPanel.add(dot);
        }

        revalidate();
        repaint();
    }

    private void updateImageSize() {
        if (imageLabel.getIcon() instanceof ImageIcon currentIcon) {
            if (currentIcon.getImage() != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int availableHeightForImage = panelHeight - dotsPanel.getPreferredSize().height - 20;

                // Previna redimensionamento com tamanhos inválidos
                if (panelWidth <= 1 || availableHeightForImage <= 1) {
                    return; // aguarda próxima chamada válida
                }

                Image scaledImage = ImageScaler.getScaledImage(currentIcon.getImage(), panelWidth, availableHeightForImage);
                imageLabel.setIcon(new ImageIcon(scaledImage));

                // Ajusta os bounds do imageLayeredPane para que ele preencha o painel do carrossel
                // e o OverlayLayout dentro dele posicione a imagem e a descrição.
                imageLayeredPane.setBounds(0, 0, panelWidth, panelHeight);

                // O dotsPanel está no SOUTH do BorderLayout principal, então o layout manager principal o posiciona.
                // Não é necessário setBounds() para ele aqui, a menos que ele estivesse dentro do layeredPane.
            }
        }
    }
}