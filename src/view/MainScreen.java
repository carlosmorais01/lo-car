package view;

import controller.VeiculoController;
import entities.Cliente;
import entities.Veiculo;
import view.components.CarCardPanel;
import view.components.CarrosselPanel;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainScreen extends JFrame {
    private VeiculoController veiculoController;
    private Cliente loggedInClient;
    private HeaderPanel headerPanel;
    private CarrosselPanel carrosselPanel;
    private JPanel mostRentedCarsPanel; // Painel para os carros mais alugados

    public MainScreen(Cliente client) {
        this.loggedInClient = client;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LoCar! - Página Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Header (Top Bar)
        String userName = (loggedInClient != null) ? loggedInClient.getNome() : "Visitante";
        String userProfilePic = (loggedInClient != null) ? loggedInClient.getCaminhoFoto() : null;

        headerPanel = new HeaderPanel(userName, userProfilePic);
        headerPanel.setSearchAction(e -> {
            String searchText = headerPanel.getSearchText();
            // Ação do botão de busca no header: Abre VehicleListScreen com o filtro
            dispose(); // Fecha a MainScreen
            VehicleListScreen searchResultsScreen = new VehicleListScreen(loggedInClient, searchText);
            searchResultsScreen.setVisible(true);
        });
        add(headerPanel, BorderLayout.NORTH);

        // Painel de Conteúdo Principal (Carrossel + Carros Mais Alugados)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Layout vertical para as seções
        contentPanel.setBackground(UIManager.getColor("Panel.background"));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Padding geral

        // Carrossel de Veículos
        List<Veiculo> veiculosCarrossel = veiculoController.listarTodos();
        carrosselPanel = new CarrosselPanel(veiculosCarrossel);
        carrosselPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Já está aqui, bom
        contentPanel.add(carrosselPanel);
        contentPanel.add(Box.createVerticalStrut(40));

        // Título "Carros mais alugados"
        JLabel mostRentedTitle = new JLabel("Carros mais alugados");
        mostRentedTitle.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 24f));
        mostRentedTitle.setForeground(new Color(10, 40, 61));
        mostRentedTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // ALINHAR AO CENTRO
        contentPanel.add(mostRentedTitle);
        contentPanel.add(Box.createVerticalStrut(20));

        // Painel para os cards de carros mais alugados
        mostRentedCarsPanel = new JPanel();
        mostRentedCarsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // FlowLayout já centraliza os items dentro dele
        mostRentedCarsPanel.setBackground(UIManager.getColor("Panel.background"));
        mostRentedCarsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // ALINHAR AO CENTRO

        // Carrega e exibe os carros mais alugados
        loadMostRentedCars();

        JScrollPane mostRentedScrollPane = new JScrollPane(mostRentedCarsPanel);
        mostRentedScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mostRentedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mostRentedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mostRentedScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mostRentedScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT); // ALINHAR AO CENTRO (o scroll pane)

        contentPanel.add(mostRentedScrollPane);
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void loadMostRentedCars() {
        mostRentedCarsPanel.removeAll();
        List<Veiculo> carrosMaisAlugados = veiculoController.getVeiculosMaisAlugados(4); // Exibe os 4 mais alugados

        if (carrosMaisAlugados.isEmpty()) {
            JLabel noCarsLabel = new JLabel("Nenhum carro popular encontrado.");
            noCarsLabel.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            mostRentedCarsPanel.add(noCarsLabel);
        } else {
            for (Veiculo veiculo : carrosMaisAlugados) {
                CarCardPanel card = new CarCardPanel(veiculo, veiculoController); // Usa o novo componente
                card.setPreferredSize(new Dimension(280, 280)); // Ajuste o tamanho do card aqui
                mostRentedCarsPanel.add(card);
            }
        }
        mostRentedCarsPanel.revalidate();
        mostRentedCarsPanel.repaint();
    }
}