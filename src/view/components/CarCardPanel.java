package view.components;

import entities.Veiculo;
import controller.VeiculoController; // Para verificar o status de locação

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class CarCardPanel extends JPanel {

    private Veiculo veiculo;
    private VeiculoController veiculoController; // Injetar o controller para verificar status

    public CarCardPanel(Veiculo veiculo, VeiculoController veiculoController) {
        this.veiculo = veiculo;
        this.veiculoController = veiculoController;
        initializeCard();
    }

    private void initializeCard() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ImageIcon icon = null;
        try {
            // Tenta carregar como recurso do classpath (para imagens dentro do JAR)
            URL imageUrl = getClass().getResource(veiculo.getCaminhoFoto());
            if (imageUrl != null) {
                icon = new ImageIcon(imageUrl);
            } else {
                // Se não for um recurso, tenta carregar como arquivo absoluto
                icon = new ImageIcon(veiculo.getCaminhoFoto());
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem para " + veiculo.getNome() + ": " + e.getMessage());
            icon = new ImageIcon(new byte[0]); // ImageIcon vazio em caso de erro
        }

        if (icon != null && icon.getImage() != null) {
            Image img = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH); // Ajuste o tamanho da imagem no card
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