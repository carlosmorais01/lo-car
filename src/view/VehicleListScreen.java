package view;

import controller.VeiculoController;
import entities.*;
import enums.Cor;
import view.components.HeaderPanel;
import view.components.RoundedImageLabel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VehicleListScreen extends JFrame {
    private JPanel cardPanel; // Este JPanel ainda conterá os cards em GridLayout
    private JPanel cardContainerPanel; // NOVO: Este painel conterá o cardPanel e usará FlowLayout
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

    public VehicleListScreen(Pessoa user, String initialSearchText) {
        this.loggedInUser = user; // Armazena o usuário (Cliente ou Funcionario)
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

        // Ação para o clique no logotipo: voltar para MainScreen
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

        // NOVO: Exibir botão de engrenagem e configurar ação APENAS se for funcionário
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

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        filterPanel.setPreferredSize(new Dimension(200, getHeight()));
        filterPanel.setBackground(new Color(240, 240, 240));

        addFiltersToPanel();
        mainContentPanel.add(filterPanel, BorderLayout.WEST);

        // **ALTERADO AQUI**: cardContainerPanel para conter o cardPanel
        cardContainerPanel = new JPanel();
        cardContainerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Centraliza o cardPanel dentro dele

        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 3, 20, 20)); // GridLayout para 3 colunas e espaçamento
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remover borda, já que o container tem padding

        cardContainerPanel.add(cardPanel); // Adiciona o cardPanel ao novo container

        JScrollPane scrollPane = new JScrollPane(cardContainerPanel); // ScrollPane agora envolve o container
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // Aplica o filtro inicial se houver um termo de busca
        if (initialSearchText != null && !initialSearchText.isEmpty()) {
            headerPanel.setSearchText(initialSearchText); // NOVO MÉTODO no HeaderPanel
            aplicarFiltros(); // Aplica os filtros com o texto inicial
        } else {
            atualizarCards(veiculoController.listarTodos());
        }

        SwingUtilities.invokeLater(() -> aplicarFiltros());
        setVisible(true);
    }

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

    private void atualizarCards(List<Veiculo> veiculos) {
        cardPanel.removeAll(); // Limpa os cards existentes

        // Definir um tamanho padrão para os cards.
        // Estes são os valores que cada card individualmente tentará ter.
        int fixedCardWidth = 300; // Largura do card (ajuste se necessário)
        int fixedCardHeight = 300; // Altura do card (fixa em 300)
        Dimension fixedCardSize = new Dimension(fixedCardWidth, fixedCardHeight);

        // Largura e altura para a imagem dentro do card
        int imageWidth = fixedCardWidth - 20; // 10px de padding interno em cada lado
        int imageHeight = 150; // Altura da imagem (ajustar se o card ficar muito apertado)

        if (veiculos.isEmpty()) {
            // Se não há veículos, centraliza a mensagem.
            // Para isso, o cardContainerPanel (que é um FlowLayout) pode ser ajustado
            // ou o cardPanel pode mudar temporariamente seu layout.
            // Aqui, vamos fazer o cardPanel mudar para GridBagLayout.
            cardPanel.setLayout(new GridBagLayout());
            JLabel noResultsLabel = new JLabel("Nenhum veículo encontrado com os filtros aplicados.", SwingConstants.CENTER);
            noResultsLabel.setFont(noResultsLabel.getFont().deriveFont(16f));
            cardPanel.add(noResultsLabel);
        } else {
            // **ALTERADO AQUI:** Garante que o layout é GridLayout quando há cards.
            // Isso fará com que os cards se organizem em 3 colunas e quebrem a linha.
            // O FlowLayout do cardContainerPanel vai centralizar/alinhar esse GridLayout.
            cardPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 0 linhas (auto), 3 colunas, 20px de espaçamento

            for (Veiculo veiculo : veiculos) {
                JPanel card = new JPanel();
                card.setLayout(new BorderLayout());
                card.setBackground(Color.WHITE);

                // Definir o tamanho preferencial e máximo para cada card.
                // O GridLayout tentará usar essas dimensões.
                card.setPreferredSize(fixedCardSize);
                card.setMaximumSize(fixedCardSize);
                card.setMinimumSize(fixedCardSize);

                // --- Carregamento e Estilização da Imagem ---
                ImageIcon icon = null;
                try {
                    java.net.URL imageUrl = getClass().getResource(veiculo.getCaminhoFoto());
                    if (imageUrl != null) {
                        icon = new ImageIcon(imageUrl);
                    } else {
                        System.err.println("Recurso de imagem não encontrado: " + veiculo.getCaminhoFoto() + ". Tentando carregar como arquivo.");
                        icon = new ImageIcon(veiculo.getCaminhoFoto());
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar imagem para " + veiculo.getNome() + ": " + e.getMessage());
                    icon = new ImageIcon(new byte[0]);
                }

                if (icon != null && icon.getImage() != null) {
                    Image img = icon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new RoundedImageLabel(new ImageIcon(img), 15);
                    imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imgLabel.setVerticalAlignment(SwingConstants.CENTER);
                    imgLabel.setPreferredSize(new Dimension(imageWidth, imageHeight));
                    imgLabel.setMaximumSize(new Dimension(imageWidth, imageHeight));
                    card.add(imgLabel, BorderLayout.NORTH);
                } else {
                    JLabel placeholder = new JLabel("Imagem Indisponível", SwingConstants.CENTER);
                    placeholder.setPreferredSize(new Dimension(imageWidth, imageHeight));
                    placeholder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    placeholder.setBackground(Color.LIGHT_GRAY);
                    placeholder.setOpaque(true);
                    card.add(placeholder, BorderLayout.NORTH);
                }

                // Informações do veículo
                JLabel nome = new JLabel(veiculo.getMarca() + " " + veiculo.getNome() + " " + veiculo.getAno(), SwingConstants.CENTER);
                JLabel preco = new JLabel("R$ " + String.format("%.2f", veiculo.getValorDiario()) + " / dia", SwingConstants.CENTER);

                nome.setFont(nome.getFont().deriveFont(Font.BOLD,16f));
                preco.setFont(preco.getFont().deriveFont(Font.BOLD, 12f));
                preco.setForeground(new Color(0, 100, 0));

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setBackground(Color.WHITE);
                info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                info.add(nome);
                info.add(preco);
                card.add(info, BorderLayout.CENTER);

                // Status de disponibilidade
                boolean isLocado = veiculoController.estaLocado(veiculo);
                JLabel status = new JLabel(isLocado ? "Indisponível" : "Disponível", SwingConstants.CENTER);
                status.setFont(status.getFont().deriveFont(Font.BOLD, 12f));
                status.setOpaque(true);
                status.setBackground(isLocado ? new Color(255, 230, 230) : new Color(230, 255, 230));
                status.setForeground(isLocado ? Color.RED : new Color(0, 150, 0));
                status.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                card.add(status, BorderLayout.SOUTH);

                cardPanel.add(card);
            }
        }
        // É importante que o cardContainerPanel revalide seu tamanho se o cardPanel dentro dele mudar
        cardContainerPanel.revalidate();
        cardContainerPanel.repaint();
        cardPanel.revalidate(); // Revalida o layout do painel (necessário após adicionar/remover componentes)
        cardPanel.repaint(); // Redesenha o painel para refletir as mudanças
    }
}