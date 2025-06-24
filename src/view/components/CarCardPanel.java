package view.components;

import entities.Veiculo;
import controller.VeiculoController;
import util.ImageScaler;
import view.VehicleDetailScreen;
import entities.Pessoa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * `CarCardPanel` é um componente Swing que exibe informações resumidas de um veículo
 * em um formato de cartão. Ele mostra a imagem do veículo, nome completo, preço diário
 * e status de disponibilidade (disponível/indisponível). Clicar no cartão abre a
 * tela de detalhes do veículo (`VehicleDetailScreen`).
 */
public class CarCardPanel extends JPanel {

    private Veiculo veiculo;
    private VeiculoController veiculoController;
    private Pessoa loggedInUser;

    /**
     * Construtor para `CarCardPanel`.
     *
     * @param veiculo O objeto {@link Veiculo} cujas informações serão exibidas no cartão.
     * @param veiculoController A instância de {@link VeiculoController} para verificar o status de locação do veículo.
     * @param loggedInUser O objeto {@link Pessoa} representando o usuário atualmente logado,
     * necessário para passar para a tela de detalhes do veículo.
     */
    public CarCardPanel(Veiculo veiculo, VeiculoController veiculoController, Pessoa loggedInUser) {
        this.veiculo = veiculo;
        this.veiculoController = veiculoController;
        this.loggedInUser = loggedInUser;
        initializeCard();
    }

    /**
     * Inicializa a interface gráfica do componente de cartão do veículo.
     * Configura o layout, estilo, carrega e exibe a imagem do veículo,
     * e adiciona informações como nome, preço e status de disponibilidade.
     * Também configura um listener de clique para abrir a tela de detalhes do veículo.
     */
    private void initializeCard() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            /**
             * Manipula o evento de clique do mouse no cartão.
             * Ao clicar, a janela pai é fechada e uma nova {@link VehicleDetailScreen}
             * é aberta com os detalhes do veículo selecionado e o usuário logado.
             * @param e O evento de mouse.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CarCardPanel.this);
                parentFrame.dispose();
                VehicleDetailScreen detailScreen = new VehicleDetailScreen(veiculo, loggedInUser);
                detailScreen.setVisible(true);
            }
        });
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Image cardImage = null;
        String imagePath = veiculo.getCaminhoFoto();

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File savedImageFile = new File(imagePath);
                if (savedImageFile.exists()) {
                    cardImage = ImageIO.read(savedImageFile);
                } else {
                    URL imageUrl = getClass().getResource(imagePath);
                    if (imageUrl != null) {
                        cardImage = ImageIO.read(imageUrl);
                    } else {
                        System.err.println("Imagem do veículo não encontrada: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar imagem para " + veiculo.getPlaca() + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar/escalar imagem do veículo: " + e.getMessage());
            }
        }

        if (cardImage != null) {
            Image img = ImageScaler.getScaledImage(cardImage, 250, 150);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(imgLabel, BorderLayout.NORTH);
        } else {
            JLabel placeholder = new JLabel("Imagem Indisponível", SwingConstants.CENTER);
            placeholder.setPreferredSize(new Dimension(250, 150));
            add(placeholder, BorderLayout.NORTH);
        }

        JLabel nomeCompleto = new JLabel(veiculo.getMarca() + " " + veiculo.getNome() + " " + veiculo.getAno(), SwingConstants.CENTER);
        JLabel preco = new JLabel("R$ " + String.format("%.2f", veiculo.getValorDiario()) + " / dia", SwingConstants.CENTER);

        nomeCompleto.setFont(nomeCompleto.getFont().deriveFont(Font.BOLD, 16f));
        nomeCompleto.setForeground(new Color(10, 40, 61));
        preco.setFont(preco.getFont().deriveFont(Font.BOLD, 14f));
        preco.setForeground(new Color(0, 128, 0));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(nomeCompleto);
        infoPanel.add(preco);
        add(infoPanel, BorderLayout.CENTER);

        boolean isLocado = veiculoController.estaLocado(veiculo);
        JLabel statusLabel = new JLabel(isLocado ? "Indisponível" : "Disponível", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isLocado ? new Color(255, 230, 230) : new Color(230, 255, 230));
        statusLabel.setForeground(isLocado ? Color.RED : new Color(0, 150, 0));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }
}