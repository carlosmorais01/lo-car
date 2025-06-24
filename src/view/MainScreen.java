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

/**
 * A classe `MainScreen` representa a tela principal da aplicação LoCar! após o login bem-sucedido.
 * Ela exibe um carrossel de veículos e uma seção para os veículos mais alugados,
 * adaptando a interface com base no tipo de usuário logado (Cliente ou Funcionário).
 */
public class MainScreen extends JFrame {
    private VeiculoController veiculoController;
    private Pessoa loggedInUser;
    private HeaderPanel headerPanel;
    private CarrosselPanel carrosselPanel;
    private JPanel mostRentedCarsPanel;

    /**
     * Construtor para `MainScreen` quando um cliente faz login.
     *
     * @param client O objeto {@link Cliente} que representa o usuário logado.
     */
    public MainScreen(Cliente client) {
        this.loggedInUser = client;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    /**
     * Construtor para `MainScreen` quando um funcionário faz login.
     *
     * @param funcionario O objeto {@link Funcionario} que representa o usuário logado.
     */
    public MainScreen(Funcionario funcionario) {
        this.loggedInUser = funcionario;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    /**
     * Inicializa e configura os componentes da interface do usuário para a tela principal.
     * Isso inclui o cabeçalho (`HeaderPanel`), o carrossel de veículos (`CarrosselPanel`)
     * e um painel para exibir os carros mais alugados. A visibilidade de certos elementos
     * no cabeçalho é ajustada com base no tipo de usuário logado.
     */
    private void initializeUI() {
        setTitle("LoCar! - Página Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        String userName = (loggedInUser != null) ? loggedInUser.getNome() : "Visitante";
        String userProfilePic = (loggedInUser != null) ? loggedInUser.getCaminhoFoto() : null;

        headerPanel = new HeaderPanel(userName, userProfilePic);
        headerPanel.setSearchAction(e -> {
            String searchText = headerPanel.getSearchText();
            dispose();
            VehicleListScreen searchResultsScreen = new VehicleListScreen(loggedInUser, searchText);
            searchResultsScreen.setVisible(true);
        });
        if (loggedInUser instanceof Funcionario) {
            headerPanel.showSettingsButton(true);
            headerPanel.setSettingsAction(e -> {
                dispose();
                VehicleRegistrationScreen regScreen = new VehicleRegistrationScreen((Funcionario) loggedInUser);
                regScreen.setVisible(true);
            });
        } else {
            headerPanel.showSettingsButton(false);
        }
        headerPanel.setProfileIconClickListener(e -> {
            dispose();
            UserProfileScreen profileScreen = new UserProfileScreen(loggedInUser);
            profileScreen.setVisible(true);
        });

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIManager.getColor("Panel.background"));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        List<Veiculo> veiculosCarrossel = veiculoController.listarTodos();
        carrosselPanel = new CarrosselPanel(veiculosCarrossel, loggedInUser);
        carrosselPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(carrosselPanel);
        contentPanel.add(Box.createVerticalStrut(40));
        JLabel mostRentedTitle = new JLabel("Carros mais alugados");
        mostRentedTitle.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 24f));
        mostRentedTitle.setForeground(new Color(10, 40, 61));
        mostRentedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mostRentedTitle);
        contentPanel.add(Box.createVerticalStrut(20));
        mostRentedCarsPanel = new JPanel();
        mostRentedCarsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        mostRentedCarsPanel.setBackground(UIManager.getColor("Panel.background"));

        loadMostRentedCars();

        JPanel mostRentedCarsWrapper = new JPanel(new BorderLayout());
        mostRentedCarsWrapper.setBackground(UIManager.getColor("Panel.background"));
        mostRentedCarsWrapper.add(mostRentedCarsPanel, BorderLayout.CENTER);

        mostRentedCarsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mostRentedCarsWrapper);

        contentPanel.add(Box.createVerticalGlue());
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    /**
     * Carrega e exibe os carros mais alugados no `mostRentedCarsPanel`.
     * Limpa o painel existente e adiciona novos cartões de veículo ({@link CarCardPanel})
     * para os 4 veículos mais alugados obtidos do {@link VeiculoController}.
     */
    private void loadMostRentedCars() {
        mostRentedCarsPanel.removeAll();
        List<Veiculo> carrosMaisAlugados = veiculoController.getVeiculosMaisAlugados(4);

        if (carrosMaisAlugados.isEmpty()) {
            JLabel noCarsLabel = new JLabel("Nenhum carro popular encontrado.");
            noCarsLabel.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            mostRentedCarsPanel.add(noCarsLabel);
        } else {
            for (Veiculo veiculo : carrosMaisAlugados) {
                CarCardPanel card = new CarCardPanel(veiculo, veiculoController, loggedInUser);
                card.setPreferredSize(new Dimension(280, 280));
                mostRentedCarsPanel.add(card);
            }
        }
        mostRentedCarsPanel.revalidate();
        mostRentedCarsPanel.repaint();
    }
}