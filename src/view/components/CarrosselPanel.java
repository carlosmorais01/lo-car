package view.components;

import entities.Pessoa;
import entities.Veiculo;
import util.ImageScaler;
import view.VehicleDetailScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * `CarrosselPanel` é um componente Swing que exibe uma lista de veículos em um formato de carrossel,
 * com imagens que deslizam automaticamente e botões de navegação e indicadores de posição.
 * Cada item do carrossel pode ser clicado para abrir a tela de detalhes do veículo.
 */
public class CarrosselPanel extends JPanel {

    private List<Veiculo> veiculos;
    private int currentIndex = 0;
    private JLabel imageLabel;
    private JLabel descriptionLabel;
    private Timer timer;
    private JPanel dotsPanel;
    private JLayeredPane imageLayeredPane;
    private Pessoa loggedInUser;

    /**
     * Construtor para `CarrosselPanel`.
     *
     * @param veiculos A lista de objetos {@link Veiculo} a serem exibidos no carrossel.
     * @param loggedInUser O objeto {@link Pessoa} representando o usuário atualmente logado,
     * necessário para passar para a tela de detalhes do veículo.
     */
    public CarrosselPanel(List<Veiculo> veiculos, Pessoa loggedInUser) {
        this.veiculos = veiculos;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 600));
        setMaximumSize(new Dimension(1200, 600));
        setOpaque(false);

        setupCarrosselUI();
        startAutoSlide();
    }

    /**
     * Configura a interface do usuário do carrossel.
     * Isso inclui a inicialização do `JLayeredPane` para sobrepor a descrição sobre a imagem,
     * a criação de botões de navegação e indicadores de posição (dots),
     * e a adição de um listener de clique para a imagem do carrossel.
     */
    private void setupCarrosselUI() {
        imageLayeredPane = new JLayeredPane();
        imageLayeredPane.setLayout(new OverlayLayout(imageLayeredPane));
        imageLayeredPane.setPreferredSize(getPreferredSize());
        imageLayeredPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageLayeredPane.addMouseListener(new MouseAdapter() {
            /**
             * Manipula o evento de clique do mouse na imagem do carrossel.
             * Ao clicar, a janela pai é fechada e uma nova {@link VehicleDetailScreen}
             * é aberta com os detalhes do veículo atualmente exibido e o usuário logado.
             * @param e O evento de mouse.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!veiculos.isEmpty()) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CarrosselPanel.this);
                    parentFrame.dispose();
                    VehicleDetailScreen detailScreen = new VehicleDetailScreen(veiculos.get(currentIndex), loggedInUser);
                    detailScreen.setVisible(true);
                }
            }
        });
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        imageLayeredPane.add(imageLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        descriptionPanel.setOpaque(false);

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
        add(imageLayeredPane, BorderLayout.CENTER);
        JButton prevButton = createArrowButton("icons/botao_esquerdo.png", -1);
        JButton nextButton = createArrowButton("icons/botao_direito.png", 1);

        add(prevButton, BorderLayout.WEST);
        add(nextButton, BorderLayout.EAST);
        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        dotsPanel.setOpaque(false);
        add(dotsPanel, BorderLayout.SOUTH);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            /**
             * Manipula o evento de redimensionamento do componente.
             * Quando o carrossel é redimensionado, a imagem é atualizada para se ajustar às novas dimensões.
             * @param e O evento de componente.
             */
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateImageSize();
            }
        });
        updateCarrossel();
        SwingUtilities.invokeLater(this::updateImageSize);
    }

    /**
     * Cria um botão de seta para navegação no carrossel.
     * O botão é estilizado para ser transparente e ter um ícone de seta.
     *
     * @param iconPath O caminho do ícone da seta dentro dos recursos do projeto.
     * @param direction A direção da navegação (-1 para anterior, 1 para próximo).
     * @return Um {@link JButton} configurado como um botão de seta.
     */
    private JButton createArrowButton(String iconPath, int direction) {
        ImageIcon arrowIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/" + iconPath)));
        Image scaledArrow = ImageScaler.getScaledImage(arrowIcon.getImage(), 40, 40);
        JButton button = new JButton(new ImageIcon(scaledArrow));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMargin(new Insets(0, 10, 0, 10));

        button.addActionListener(e -> {
            stopAutoSlide();
            currentIndex = (currentIndex + direction + veiculos.size()) % veiculos.size();
            updateCarrossel();
            startAutoSlide();
        });
        return button;
    }

    /**
     * Inicia o timer para a funcionalidade de slide automático do carrossel.
     * O carrossel avança para o próximo veículo a cada 8 segundos.
     * Se um timer existente estiver ativo, ele é parado antes de iniciar um novo.
     */
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

    /**
     * Para o timer do slide automático do carrossel.
     * Este método é chamado quando o usuário interage manualmente com o carrossel.
     */
    private void stopAutoSlide() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Atualiza o conteúdo visual do carrossel para exibir o veículo atual (`currentIndex`).
     * Carrega a imagem do veículo, atualiza a descrição e os indicadores de posição (dots).
     */
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
        String imagePath = currentVeiculo.getCaminhoFoto();

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File savedImageFile = new File(imagePath);
                if (savedImageFile.exists()) {
                    carImage = ImageIO.read(savedImageFile);
                } else {
                    URL imageUrl = getClass().getResource(imagePath);
                    if (imageUrl != null) {
                        carImage = ImageIO.read(imageUrl);
                    } else {
                        System.err.println("Imagem do veículo não encontrada: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar imagem para " + currentVeiculo.getPlaca() + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar imagem para " + currentVeiculo.getPlaca() + ": " + e.getMessage());
            }
        }

        if (carImage != null) {
            imageLabel.setIcon(new ImageIcon(carImage));
            updateImageSize();
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("Imagem Indisponível");
        }

        descriptionLabel.setText(currentVeiculo.getDescricao());
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

    /**
     * Atualiza o tamanho da imagem exibida no carrossel para se ajustar às dimensões atuais do painel.
     * Isso garante que a imagem seja sempre exibida corretamente, mesmo após redimensionamentos.
     */
    private void updateImageSize() {
        if (imageLabel.getIcon() instanceof ImageIcon currentIcon) {
            if (currentIcon.getImage() != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int availableHeightForImage = panelHeight - dotsPanel.getPreferredSize().height - 20;
                if (panelWidth <= 1 || availableHeightForImage <= 1) {
                    return;
                }

                Image scaledImage = ImageScaler.getScaledImage(currentIcon.getImage(), panelWidth, availableHeightForImage);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLayeredPane.setBounds(0, 0, panelWidth, panelHeight);
            }
        }
    }
}