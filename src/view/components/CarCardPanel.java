package view.components;

import entities.Veiculo;
import controller.VeiculoController;
import util.ImageScaler;
import view.VehicleDetailScreen; // Importar VehicleDetailScreen
import entities.Pessoa; // Importar Pessoa

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; // Para MouseListener
import java.awt.event.MouseEvent; // Para MouseEvent
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class CarCardPanel extends JPanel {

    private Veiculo veiculo;
    private VeiculoController veiculoController;
    private Pessoa loggedInUser; // NOVO: Para passar para a VehicleDetailScreen

    public CarCardPanel(Veiculo veiculo, VeiculoController veiculoController, Pessoa loggedInUser) { // Construtor atualizado
        this.veiculo = veiculo;
        this.veiculoController = veiculoController;
        this.loggedInUser = loggedInUser; // Atribui o usuário logado
        initializeCard();
    }

    private void initializeCard() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão para indicar clicável

        // Adiciona MouseListener ao painel inteiro
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Abre a VehicleDetailScreen ao clicar no card
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CarCardPanel.this);
                parentFrame.dispose(); // Fecha a tela atual (MainScreen ou VehicleListScreen)
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

        // Informações do veículo
        JLabel nomeCompleto = new JLabel(veiculo.getMarca() + " " + veiculo.getNome() + " " + veiculo.getAno(), SwingConstants.CENTER);
        JLabel preco = new JLabel("R$ " + String.format("%.2f", veiculo.getValorDiario()) + " / dia", SwingConstants.CENTER);

        nomeCompleto.setFont(nomeCompleto.getFont().deriveFont(Font.BOLD, 16f));
        nomeCompleto.setForeground(new Color(10, 40, 61)); // Cor do texto principal
        preco.setFont(preco.getFont().deriveFont(Font.BOLD, 14f));
        preco.setForeground(new Color(0, 128, 0)); // Um verde para o preço

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(nomeCompleto);
        infoPanel.add(preco);
        add(infoPanel, BorderLayout.CENTER);

        // Status de disponibilidade
        boolean isLocado = veiculoController.estaLocado(veiculo);
        JLabel statusLabel = new JLabel(isLocado ? "Indisponível" : "Disponível", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isLocado ? new Color(255, 230, 230) : new Color(230, 255, 230)); // Fundo claro vermelho/verde
        statusLabel.setForeground(isLocado ? Color.RED : new Color(0, 150, 0)); // Texto vermelho/verde escuro
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }
}