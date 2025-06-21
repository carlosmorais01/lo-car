package view;

import controller.VeiculoController;
import entities.*;
import enums.Cor;
import view.components.HeaderPanel;
import view.components.RoundedImageLabel; // Ajustado para o seu pacote 'view'

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VehicleListScreen extends JFrame {
    private JPanel cardPanel;
    private VeiculoController veiculoController;
    private HeaderPanel headerPanel;
    private Cliente loggedInClient;

    // Componentes para os filtros
    private JTextField precoMaxField;
    private JComboBox<Cor> coresComboBox;
    private JRadioButton disponiveisRadio;
    private JRadioButton proximosDevolucaoRadio;
    private JRadioButton todosStatusRadio;
    private JTextField anoMinField;
    private JTextField anoMaxField;
    private ButtonGroup statusButtonGroup;
    private JPanel filterPanel;

    // Componentes para o filtro de modelo/tipo
    private JRadioButton carroRadio;
    private JRadioButton motoRadio;
    private JRadioButton caminhaoRadio;
    private JRadioButton todosModelosRadio;
    private ButtonGroup modeloButtonGroup;

    public VehicleListScreen(Cliente client) {
        this.loggedInClient = client;
        setTitle("Tela de Pesquisa - LoCar!");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        veiculoController = new VeiculoController();

        headerPanel = new HeaderPanel(loggedInClient.getNome(), loggedInClient.getCaminhoFoto());
        headerPanel.setSearchAction(e -> aplicarFiltros());
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        filterPanel.setPreferredSize(new Dimension(200, getHeight()));
        filterPanel.setBackground(new Color(240, 240, 240));

        addFiltersToPanel();
        mainContentPanel.add(filterPanel, BorderLayout.WEST);

        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 3, 20, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        atualizarCards(veiculoController.listarTodos());

        setVisible(true);
    }

    private void addFiltersToPanel() {
        JLabel titleLabel = new JLabel("Veículo XXXX");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(titleLabel);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Preço por dia
        JLabel precoLabel = new JLabel("Preço por dia:");
        precoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(precoLabel);
        precoMaxField = new JTextField(10);
        precoMaxField.setMaximumSize(new Dimension(Integer.MAX_VALUE, precoMaxField.getPreferredSize().height));
        precoMaxField.setAlignmentX(Component.LEFT_ALIGNMENT);
        precoMaxField.addActionListener(e -> aplicarFiltros());
        filterPanel.add(precoMaxField);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Cores
        JLabel coresLabel = new JLabel("Cores:");
        coresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(coresLabel);
        coresComboBox = new JComboBox<>(Cor.values());
        coresComboBox.setSelectedItem(null);
        coresComboBox.addActionListener(e -> aplicarFiltros());
        coresComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(coresComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(statusLabel);

        statusButtonGroup = new ButtonGroup();
        disponiveisRadio = new JRadioButton("Somente Disponíveis");
        proximosDevolucaoRadio = new JRadioButton("Próximos de Devolução");
        todosStatusRadio = new JRadioButton("Todos");
        todosStatusRadio.setSelected(true);

        statusButtonGroup.add(disponiveisRadio);
        statusButtonGroup.add(proximosDevolucaoRadio);
        statusButtonGroup.add(todosStatusRadio);

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

        // Ano
        JLabel anoLabel = new JLabel("Ano:");
        anoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(anoLabel);

        JPanel anoPanel = new JPanel();
        anoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        anoMinField = new JTextField(4);
        anoMaxField = new JTextField(4);
        anoMinField.addActionListener(e -> aplicarFiltros());
        anoMaxField.addActionListener(e -> aplicarFiltros());

        anoPanel.add(anoMinField);
        anoPanel.add(new JLabel(" - "));
        anoPanel.add(anoMaxField);
        anoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(anoPanel);
        filterPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Modelo (Carro, Moto, Caminhão)
        JLabel modeloLabel = new JLabel("Modelo:");
        modeloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(modeloLabel);

        modeloButtonGroup = new ButtonGroup();
        carroRadio = new JRadioButton("Carro");
        motoRadio = new JRadioButton("Moto");
        caminhaoRadio = new JRadioButton("Caminhão");
        todosModelosRadio = new JRadioButton("Todos os Modelos");
        todosModelosRadio.setSelected(true);

        modeloButtonGroup.add(carroRadio);
        modeloButtonGroup.add(motoRadio);
        modeloButtonGroup.add(caminhaoRadio);
        modeloButtonGroup.add(todosModelosRadio);

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
        String nome = headerPanel.getSearchText().toLowerCase();

        Double precoMax = null;
        try {
            if (!precoMaxField.getText().trim().isEmpty()) {
                precoMax = Double.parseDouble(precoMaxField.getText().replace(",", "."));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para o preço.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
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
        try {
            if (!anoMinField.getText().trim().isEmpty()) {
                anoMin = Integer.parseInt(anoMinField.getText());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ano inicial válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer anoMax = null;
        try {
            if (!anoMaxField.getText().trim().isEmpty()) {
                anoMax = Integer.parseInt(anoMaxField.getText());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ano final válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        List<Veiculo> veiculosFiltrados = veiculoController.filtrarVeiculos(nome, precoMax, corSelecionada, statusSelecionado, anoMin, anoMax, tipoVeiculoSelecionado);
        atualizarCards(veiculosFiltrados);
    }

    private void atualizarCards(List<Veiculo> veiculos) {
        cardPanel.removeAll();

        if (veiculos.isEmpty()) {
            JLabel noResultsLabel = new JLabel("Nenhum veículo encontrado com os filtros aplicados.", SwingConstants.CENTER);
            noResultsLabel.setFont(noResultsLabel.getFont().deriveFont(16f));
            cardPanel.add(noResultsLabel);
        } else {
            for (Veiculo veiculo : veiculos) {
                JPanel card = new JPanel();
                card.setLayout(new BorderLayout());
                card.setBackground(Color.WHITE);

                // Imagem
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
                    icon = new ImageIcon(new byte[0]); // ImageIcon vazio
                }

                if (icon.getImage() != null) {
                    Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new RoundedImageLabel(new ImageIcon(img), 15);
                    card.add(imgLabel, BorderLayout.NORTH);
                } else {
                    JLabel placeholder = new JLabel("Imagem Indisponível", SwingConstants.CENTER);
                    placeholder.setPreferredSize(new Dimension(180, 120));
                    card.add(placeholder, BorderLayout.NORTH);
                }

                // Informações do veículo
                JLabel nome = new JLabel(veiculo.getMarca() + " " + veiculo.getNome(), SwingConstants.CENTER);
                JLabel local = new JLabel("Setor Marista - Goiânia", SwingConstants.CENTER);
                JLabel preco = new JLabel("R$ " + String.format("%.2f", veiculo.getValorDiario()) + " / dia", SwingConstants.CENTER);

                nome.setFont(nome.getFont().deriveFont(Font.BOLD, 20f));
                local.setFont(local.getFont().deriveFont(Font.PLAIN, 18f));
                local.setForeground(Color.GRAY);
                preco.setFont(preco.getFont().deriveFont(Font.BOLD, 16f));
                preco.setForeground(new Color(0, 100, 0));

                JPanel info = new JPanel(new GridLayout(3, 1));
                info.setBackground(Color.WHITE);
                info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                info.add(nome);
                info.add(local);
                info.add(preco);
                card.add(info, BorderLayout.CENTER);

                // Status de disponibilidade - Agora usando o método do controller
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
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}