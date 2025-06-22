package view;

import controller.VeiculoController;
import entities.Cliente;
import entities.Funcionario;
import entities.Pessoa;
import entities.Veiculo;
import view.components.CarCardPanel;
import view.components.CarrosselPanel;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainScreen extends JFrame {
    private VeiculoController veiculoController;
    private Pessoa loggedInUser;
    private HeaderPanel headerPanel;
    private CarrosselPanel carrosselPanel;
    private JPanel mostRentedCarsPanel;

    public MainScreen(Cliente client) {
        this.loggedInUser = client;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    public MainScreen(Funcionario funcionario) {
        this.loggedInUser = funcionario;
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
        String userName = (loggedInUser != null) ? loggedInUser.getNome() : "Visitante";
        String userProfilePic = (loggedInUser != null) ? loggedInUser.getCaminhoFoto() : null;

        headerPanel = new HeaderPanel(userName, userProfilePic);

        // Ação de busca para o HeaderPanel desta tela
        headerPanel.setSearchAction(e -> {
            String searchText = headerPanel.getSearchText();
            dispose();
            // Passa o usuário logado para a VehicleListScreen
            VehicleListScreen searchResultsScreen = new VehicleListScreen(loggedInUser, searchText);
            searchResultsScreen.setVisible(true);
        });

        // NOVO: Exibir botão de engrenagem e configurar ação APENAS se for funcionário
        if (loggedInUser instanceof Funcionario) {
            headerPanel.showSettingsButton(true); // NOVO MÉTODO no HeaderPanel
            headerPanel.setSettingsAction(e -> { // NOVO MÉTODO no HeaderPanel
                // Abrir a tela de cadastro de veículos
                dispose(); // Fecha MainScreen
                VehicleRegistrationScreen regScreen = new VehicleRegistrationScreen((Funcionario) loggedInUser);
                regScreen.setVisible(true);
            });
        } else {
            headerPanel.showSettingsButton(false); // Esconde se não for funcionário
        }

        add(headerPanel, BorderLayout.NORTH);

        // Painel de Conteúdo Principal (Carrossel + Carros Mais Alugados)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIManager.getColor("Panel.background"));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Carrossel de Veículos
        List<Veiculo> veiculosCarrossel = veiculoController.listarTodos();
        carrosselPanel = new CarrosselPanel(veiculosCarrossel);
        carrosselPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(carrosselPanel);
        contentPanel.add(Box.createVerticalStrut(40));

        // Título "Carros mais alugados"
        JLabel mostRentedTitle = new JLabel("Carros mais alugados");
        mostRentedTitle.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 24f));
        mostRentedTitle.setForeground(new Color(10, 40, 61));
        mostRentedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mostRentedTitle);
        contentPanel.add(Box.createVerticalStrut(20));

        // Painel para os cards de carros mais alugados
        mostRentedCarsPanel = new JPanel();
        mostRentedCarsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        mostRentedCarsPanel.setBackground(UIManager.getColor("Panel.background"));
        // Remova mostRentedCarsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); daqui
        // Será centralizado pelo wrapper abaixo

        loadMostRentedCars();

        // NOVO: Wrapper para centralizar o FlowLayout dentro do BoxLayout
        JPanel mostRentedCarsWrapper = new JPanel(new BorderLayout()); // Usa BorderLayout
        mostRentedCarsWrapper.setBackground(UIManager.getColor("Panel.background"));
        mostRentedCarsWrapper.add(mostRentedCarsPanel, BorderLayout.CENTER); // Adiciona o FlowLayout no centro

        mostRentedCarsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o wrapper no BoxLayout
        contentPanel.add(mostRentedCarsWrapper); // Adiciona o wrapper ao contentPanel

        contentPanel.add(Box.createVerticalGlue());

        // Apenas um JScrollPane principal para todo o conteúdo rolável
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void loadMostRentedCars() {
        mostRentedCarsPanel.removeAll();
        List<Veiculo> carrosMaisAlugados = veiculoController.getVeiculosMaisAlugados(4);

        if (carrosMaisAlugados.isEmpty()) {
            JLabel noCarsLabel = new JLabel("Nenhum carro popular encontrado.");
            noCarsLabel.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            mostRentedCarsPanel.add(noCarsLabel);
        } else {
            for (Veiculo veiculo : carrosMaisAlugados) {
                CarCardPanel card = new CarCardPanel(veiculo, veiculoController);
                card.setPreferredSize(new Dimension(280, 280));
                mostRentedCarsPanel.add(card);
            }
        }
        mostRentedCarsPanel.revalidate();
        mostRentedCarsPanel.repaint();
    }
}