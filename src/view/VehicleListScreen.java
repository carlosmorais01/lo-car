package view;

import controller.VeiculoController;
import entities.Cliente;
import entities.Funcionario;
import entities.Pessoa;
import entities.Veiculo;
import enums.Cor;
import view.components.CarCardPanel;
import view.components.HeaderPanel;
import view.components.RoundedImageLabel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A classe `VehicleListScreen` representa a tela de listagem e filtragem de veículos na aplicação LoCar!.
 * Ela permite que os usuários busquem veículos por texto, filtrem por preço, cor, status de disponibilidade e tipo de veículo.
 * Os resultados são exibidos em um formato de cartão.
 */
public class VehicleListScreen extends JFrame {
    private JPanel cardPanel;
    private JPanel cardContainerPanel;
    private VeiculoController veiculoController;
    private HeaderPanel headerPanel;
    private Pessoa loggedInUser;

    private JTextField precoMaxField;
    private JComboBox<Cor> coresComboBox;
    private JRadioButton disponiveisRadio;
    private JRadioButton proximosDevolucaoRadio;
    private JRadioButton todosStatusRadio;
    private ButtonGroup statusButtonGroup;
    private JPanel filterPanel;

    private JRadioButton carroRadio;
    private JRadioButton motoRadio;
    private JRadioButton caminhaoRadio;
    private JRadioButton todosModelosRadio;
    private ButtonGroup modeloButtonGroup;

    /**
     * Construtor para `VehicleListScreen`.
     *
     * @param user              O objeto {@link Pessoa} representando o usuário atualmente logado.
     * @param initialSearchText Um texto de busca inicial a ser aplicado automaticamente ao carregar a tela. Pode ser nulo.
     */
    public VehicleListScreen(Pessoa user, String initialSearchText) {
        this.loggedInUser = user;
        setTitle("Tela de Pesquisa - LoCar!");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        veiculoController = new VeiculoController();

        String userName = (loggedInUser != null) ? loggedInUser.getNome() : "Visitante";
        String userProfilePic = (loggedInUser != null) ? loggedInUser.getCaminhoFoto() : null;

        headerPanel = new HeaderPanel(userName, userProfilePic);
        headerPanel.setSearchAction(e -> aplicarFiltros());

        headerPanel.setLogoClickListener(e -> {
            dispose();
            if (loggedInUser instanceof Cliente) {
                MainScreen mainScreen = new MainScreen((Cliente) loggedInUser);
                mainScreen.setVisible(true);
            } else if (loggedInUser instanceof Funcionario) {
                MainScreen mainScreen = new MainScreen((Funcionario) loggedInUser);
                mainScreen.setVisible(true);
            }
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

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        filterPanel.setPreferredSize(new Dimension(200, getHeight()));
        filterPanel.setBackground(new Color(240, 240, 240));

        addFiltersToPanel();
        mainContentPanel.add(filterPanel, BorderLayout.WEST);

        cardContainerPanel = new JPanel();
        cardContainerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 3, 20, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cardContainerPanel.add(cardPanel);

        JScrollPane scrollPane = new JScrollPane(cardContainerPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        if (initialSearchText != null && !initialSearchText.isEmpty()) {
            headerPanel.setSearchText(initialSearchText);
            aplicarFiltros();
        } else {
            atualizarCards(veiculoController.listarTodos());
        }
        SwingUtilities.invokeLater(() -> aplicarFiltros());
        setVisible(true);
    }

    /**
     * Adiciona os componentes de filtro (preço máximo, cor, disponibilidade e tipo de veículo)
     * ao painel lateral de filtros.
     */
    private void addFiltersToPanel() {
        JLabel titleLabel = new JLabel("Filtros");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(titleLabel);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel precoLabel = new JLabel("Preço máximo por dia:");
        precoLabel.setFont(precoLabel.getFont().deriveFont(Font.BOLD, 14f));
        precoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(precoLabel);

        precoMaxField = new JTextField(10);
        precoMaxField.setBackground(Color.WHITE);
        precoMaxField.setPreferredSize(new Dimension(150, 30));
        precoMaxField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        precoMaxField.setAlignmentX(Component.LEFT_ALIGNMENT);
        precoMaxField.addActionListener(e -> aplicarFiltros());
        filterPanel.add(precoMaxField);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel coresLabel = new JLabel("Cor:");
        coresLabel.setFont(coresLabel.getFont().deriveFont(Font.BOLD, 14f));
        coresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(coresLabel);

        coresComboBox = new JComboBox<>(Cor.values());
        coresComboBox.setSelectedItem(null);
        coresComboBox.setBackground(Color.WHITE);
        coresComboBox.setPreferredSize(new Dimension(150, 30));
        coresComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        coresComboBox.addActionListener(e -> aplicarFiltros());
        coresComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        coresComboBox.setFont(coresComboBox.getFont().deriveFont(Font.PLAIN, 14));
        filterPanel.add(coresComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel statusLabel = new JLabel("Disponibilidade:");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(statusLabel);

        statusButtonGroup = new ButtonGroup();
        disponiveisRadio = new JRadioButton("Somente Disponíveis");
        disponiveisRadio.setForeground(new Color(0, 0, 0));

        proximosDevolucaoRadio = new JRadioButton("Próximos de Devolução");
        proximosDevolucaoRadio.setForeground(new Color(0, 0, 0));
        todosStatusRadio = new JRadioButton("Todos");
        todosStatusRadio.setForeground(new Color(0, 0, 0));
        todosStatusRadio.setSelected(true);

        statusButtonGroup.add(disponiveisRadio);
        statusButtonGroup.add(proximosDevolucaoRadio);
        statusButtonGroup.add(todosStatusRadio);

        disponiveisRadio.setFont(disponiveisRadio.getFont().deriveFont(12f));
        proximosDevolucaoRadio.setFont(proximosDevolucaoRadio.getFont().deriveFont(12f));
        todosStatusRadio.setFont(todosStatusRadio.getFont().deriveFont(12f));

        disponiveisRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        proximosDevolucaoRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        todosStatusRadio.setAlignmentX(Component.LEFT_ALIGNMENT);

        disponiveisRadio.addActionListener(e -> aplicarFiltros());
        proximosDevolucaoRadio.addActionListener(e -> aplicarFiltros());
        todosStatusRadio.addActionListener(e -> aplicarFiltros());

        filterPanel.add(disponiveisRadio);
        filterPanel.add(proximosDevolucaoRadio);
        filterPanel.add(todosStatusRadio);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel modeloLabel = new JLabel("Tipo de Veículo:");
        modeloLabel.setFont(modeloLabel.getFont().deriveFont(Font.BOLD, 14f));
        modeloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(modeloLabel);

        modeloButtonGroup = new ButtonGroup();
        carroRadio = new JRadioButton("Carro");
        carroRadio.setForeground(new Color(0, 0, 0));
        motoRadio = new JRadioButton("Moto");
        motoRadio.setForeground(new Color(0, 0, 0));
        caminhaoRadio = new JRadioButton("Caminhão");
        caminhaoRadio.setForeground(new Color(0, 0, 0));
        todosModelosRadio = new JRadioButton("Todos os tipos");
        todosModelosRadio.setForeground(new Color(0, 0, 0));
        todosModelosRadio.setSelected(true);

        modeloButtonGroup.add(carroRadio);
        modeloButtonGroup.add(motoRadio);
        modeloButtonGroup.add(caminhaoRadio);
        modeloButtonGroup.add(todosModelosRadio);

        carroRadio.setFont(carroRadio.getFont().deriveFont(12f));
        motoRadio.setFont(motoRadio.getFont().deriveFont(12f));
        caminhaoRadio.setFont(caminhaoRadio.getFont().deriveFont(12f));
        todosModelosRadio.setFont(todosModelosRadio.getFont().deriveFont(12f));

        carroRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        motoRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        caminhaoRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        todosModelosRadio.setAlignmentX(Component.LEFT_ALIGNMENT);

        carroRadio.addActionListener(e -> aplicarFiltros());
        motoRadio.addActionListener(e -> aplicarFiltros());
        caminhaoRadio.addActionListener(e -> aplicarFiltros());
        todosModelosRadio.addActionListener(e -> aplicarFiltros());

        filterPanel.add(carroRadio);
        filterPanel.add(motoRadio);
        filterPanel.add(caminhaoRadio);
        filterPanel.add(todosModelosRadio);
        filterPanel.add(Box.createVerticalGlue());
    }

    /**
     * Aplica os filtros selecionados na interface e atualiza a lista de veículos exibida.
     * Coleta os valores dos campos de busca e filtros (preço, cor, status, tipo),
     * e então chama o `VeiculoController` para obter a lista filtrada de veículos.
     */
    private void aplicarFiltros() {
        String termoBuscaGeral = headerPanel.getSearchText().toLowerCase();

        Double precoMax = null;
        try {
            if (!precoMaxField.getText().trim().isEmpty()) {
                precoMax = Double.parseDouble(precoMaxField.getText().replace(",", "."));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para o preço.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            precoMaxField.setText("");
            return;
        }

        Cor corSelecionada = (Cor) coresComboBox.getSelectedItem();

        String statusSelecionado = null;
        if (disponiveisRadio.isSelected()) {
            statusSelecionado = "Disponíveis";
        } else if (proximosDevolucaoRadio.isSelected()) {
            statusSelecionado = "Próximos de Devolução";
        } else if (todosStatusRadio.isSelected()) {
            statusSelecionado = "Todos";
        }
        Integer anoMin = null;
        Integer anoMax = null;

        String tipoVeiculoSelecionado = null;
        if (carroRadio.isSelected()) {
            tipoVeiculoSelecionado = "Carro";
        } else if (motoRadio.isSelected()) {
            tipoVeiculoSelecionado = "Moto";
        } else if (caminhaoRadio.isSelected()) {
            tipoVeiculoSelecionado = "Caminhão";
        } else if (todosModelosRadio.isSelected()) {
            tipoVeiculoSelecionado = "Todos os Modelos";
        }

        List<Veiculo> veiculosFiltrados = veiculoController.filtrarVeiculos(termoBuscaGeral, precoMax, corSelecionada, statusSelecionado, anoMin, anoMax, tipoVeiculoSelecionado);
        atualizarCards(veiculosFiltrados);
    }

    /**
     * Atualiza o painel de exibição de veículos com a lista de veículos fornecida.
     * Remove todos os cartões existentes e adiciona novos {@link CarCardPanel} para cada veículo na lista.
     * Se a lista estiver vazia, uma mensagem indicando nenhum resultado é exibida.
     *
     * @param veiculos A lista de objetos {@link Veiculo} a serem exibidos.
     */
    private void atualizarCards(List<Veiculo> veiculos) {
        cardPanel.removeAll();
        int fixedCardWidth = 300;
        int fixedCardHeight = 300;
        Dimension fixedCardSize = new Dimension(fixedCardWidth, fixedCardHeight);

        if (veiculos.isEmpty()) {
            cardPanel.setLayout(new GridBagLayout());
            JLabel noResultsLabel = new JLabel("Nenhum veículo encontrado com os filtros aplicados.", SwingConstants.CENTER);
            noResultsLabel.setFont(noResultsLabel.getFont().deriveFont(16f));
            cardPanel.add(noResultsLabel);
        } else {
            cardPanel.setLayout(new GridLayout(0, 3, 20, 20));
            for (Veiculo veiculo : veiculos) {
                CarCardPanel card = new CarCardPanel(veiculo, veiculoController, loggedInUser);
                card.setPreferredSize(fixedCardSize);
                card.setMaximumSize(fixedCardSize);
                card.setMinimumSize(fixedCardSize);
                cardPanel.add(card);
            }
        }
        cardContainerPanel.revalidate();
        cardContainerPanel.repaint();
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}