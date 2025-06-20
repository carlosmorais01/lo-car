package view;

import controller.VeiculoController;
import entities.Veiculo;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VehicleListScreen extends JFrame {
    private JPanel cardPanel;
    private VeiculoController veiculoController;
    private HeaderPanel headerPanel;

    public VehicleListScreen() {
        setTitle("Tela de Pesquisa");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        veiculoController = new VeiculoController();

        // 1. Instancia o Header
        headerPanel = new HeaderPanel("José Borges");

        // 2. Define a ação de busca depois da instanciação
        headerPanel.setSearchAction(e -> {
            String searchText = headerPanel.getSearchText();
            atualizarCards(veiculoController.filtrarPorNome(searchText));
        });

        // 3. Adiciona o header ao topo
        add(headerPanel, BorderLayout.NORTH);

        // Painel para os cards dos veículos
        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 3, 20, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(new JScrollPane(cardPanel), BorderLayout.CENTER);

        atualizarCards(veiculoController.listarTodos());

        setVisible(true);
    }

    private void atualizarCards(List<Veiculo> veiculos) {
        cardPanel.removeAll();

        for (Veiculo veiculo : veiculos) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            ImageIcon icon = new ImageIcon(veiculo.getCaminhoFoto());
            Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            JLabel imgLabel = new RoundedImageLabel(new ImageIcon(img), 15);
            card.add(imgLabel, BorderLayout.NORTH);

            JLabel nome = new JLabel(veiculo.getNome(), SwingConstants.CENTER);
            JLabel local = new JLabel("Setor Marista - Goiânia", SwingConstants.CENTER);
            JLabel preco = new JLabel("R$ " + String.format("%.2f", veiculo.getValorDiario()) + " / dia", SwingConstants.CENTER);

            nome.setFont(new Font("SansSerif", Font.BOLD, 14));
            preco.setForeground(Color.DARK_GRAY);

            JPanel info = new JPanel(new GridLayout(3, 1));
            info.add(nome);
            info.add(local);
            info.add(preco);
            card.add(info, BorderLayout.CENTER);

            JLabel status = new JLabel(veiculo.isLocado() ? "Indisponível" : "Disponível", SwingConstants.CENTER);
            status.setForeground(veiculo.isLocado() ? Color.RED : Color.GREEN);
            card.add(status, BorderLayout.SOUTH);

            cardPanel.add(card);
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }
}
