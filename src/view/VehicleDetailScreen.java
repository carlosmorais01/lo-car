package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.LocacaoController; // NOVO: Controlador para Locações
import controller.VeiculoController;
import entities.*;
import enums.*;
import util.ImageScaler;
import view.components.HeaderPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit; // Para cálculo de dias
import java.util.Objects;

public class VehicleDetailScreen extends JFrame {

    private Veiculo selectedVeiculo;
    private Pessoa loggedInUser;
    private VeiculoController veiculoController;
    private LocacaoController locacaoController; // Para gerenciar locações

    private JLabel vehicleImageLabel;
    private JLabel vehicleNameLabel;
    private JLabel vehiclePriceLabel;
    private JLabel vehicleLocationLabel; // Localização (Setor Marista - Goiânia)
    private JLabel vehicleStatusLabel;
    private JButton actionButton; // Botão Alugar ou Editar
    private JButton deleteButton; // Botão Excluir (apenas para funcionário)
    private JTextField rentalDaysField; // Campo para número de dias de aluguel
    private JLabel totalRentalValueLabel; // Label para mostrar o valor total

    public VehicleDetailScreen(Veiculo veiculo, Pessoa user) {
        this.selectedVeiculo = veiculo;
        this.loggedInUser = user;
        this.veiculoController = new VeiculoController();
        this.locacaoController = new LocacaoController(); // Inicializa o controlador de locação
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Detalhes do Veículo - LoCar!");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Apenas fecha esta janela
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Header Panel
        String userName = (loggedInUser != null) ? loggedInUser.getNome() : "Visitante";
        String userProfilePic = (loggedInUser != null) ? loggedInUser.getCaminhoFoto() : null;
        HeaderPanel headerPanel = new HeaderPanel(userName, userProfilePic);
        // Configurar ações do header aqui (busca, logo, configurações)
        headerPanel.setSearchAction(e -> {
            String searchText = headerPanel.getSearchText();
            dispose();
            VehicleListScreen searchResultsScreen = new VehicleListScreen(loggedInUser, searchText);
            searchResultsScreen.setVisible(true);
        });
        headerPanel.setLogoClickListener(e -> {
            dispose();
            if (loggedInUser instanceof Cliente) {
                new MainScreen((Cliente) loggedInUser).setVisible(true);
            } else if (loggedInUser instanceof Funcionario) {
                new MainScreen((Funcionario) loggedInUser).setVisible(true);
            }
        });
        if (loggedInUser instanceof Funcionario) {
            headerPanel.showSettingsButton(true);
            headerPanel.setSettingsAction(e -> {
                dispose();
                new VehicleRegistrationScreen((Funcionario) loggedInUser).setVisible(true);
            });
        } else {
            headerPanel.showSettingsButton(false);
        }
        add(headerPanel, BorderLayout.NORTH);

        // Painel de Conteúdo Principal
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(UIManager.getColor("Panel.background"));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Seção Superior: Imagem, Info Principal, Botão
        JPanel topSectionPanel = new JPanel(new GridBagLayout());
        topSectionPanel.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Imagem do Veículo (lado esquerdo)
        vehicleImageLabel = new JLabel();
        vehicleImageLabel.setPreferredSize(new Dimension(500, 300)); // Tamanho fixo para a imagem
        vehicleImageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        loadAndSetVehicleImage(selectedVeiculo.getCaminhoFoto()); // Carrega a imagem

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4; // Ocupa 4 linhas
        gbc.weightx = 0.5; // Ocupa metade da largura disponível
        topSectionPanel.add(vehicleImageLabel, gbc);

        // Informações Principais do Veículo (lado direito)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false); // Transparente para ver o fundo do topSectionPanel
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha o conteúdo à esquerda

        vehicleNameLabel = new JLabel(selectedVeiculo.getMarca() + " " + selectedVeiculo.getNome() + " " + selectedVeiculo.getAno());
        vehicleNameLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 28f));
        vehicleNameLabel.setForeground(new Color(10, 40, 61));
        vehicleNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(vehicleNameLabel);

        vehiclePriceLabel = new JLabel("R$ " + String.format("%.2f", selectedVeiculo.getValorDiario()) + " / dia");
        vehiclePriceLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 22f));
        vehiclePriceLabel.setForeground(new Color(0, 128, 0)); // Verde para preço
        vehiclePriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(vehiclePriceLabel);

        // Exemplo de localização fixa no dump
        vehicleLocationLabel = new JLabel("Setor Marista - Goiânia");
        vehicleLocationLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 16f));
        vehicleLocationLabel.setForeground(Color.GRAY);
        vehicleLocationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(vehicleLocationLabel);

        boolean isAvailable = !veiculoController.estaLocado(selectedVeiculo);
        vehicleStatusLabel = new JLabel(isAvailable ? "Disponível" : "Indisponível");
        vehicleStatusLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 18f));
        vehicleStatusLabel.setForeground(isAvailable ? new Color(0, 150, 0) : Color.RED); // Verde para disponível, vermelho para indisponível
        vehicleStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(vehicleStatusLabel);

        infoPanel.add(Box.createVerticalStrut(20)); // Espaço antes do botão

        // Campo de dias de aluguel e valor total (APENAS PARA CLIENTES E SE DISPONÍVEL)
        if (loggedInUser instanceof Cliente && isAvailable) {
            JPanel rentalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            rentalPanel.setOpaque(false);
            rentalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel daysLabel = new JLabel("Alugar por (dias):");
            daysLabel.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            rentalPanel.add(daysLabel);

            rentalDaysField = new JTextField("1", 5); // Default 1 dia
            rentalDaysField.setFont(UIManager.getFont("TextField.font").deriveFont(16f));
            rentalDaysField.setPreferredSize(new Dimension(80, 30));
            rentalDaysField.setMaximumSize(new Dimension(80, 30));
            rentalDaysField.addActionListener(e -> calculateRentalValue());
            rentalDaysField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    calculateRentalValue();
                }
            });
            rentalPanel.add(rentalDaysField);

            totalRentalValueLabel = new JLabel("Total: R$ 0.00");
            totalRentalValueLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 18f));
            totalRentalValueLabel.setForeground(new Color(10, 40, 61));
            rentalPanel.add(totalRentalValueLabel);

            infoPanel.add(rentalPanel);
            infoPanel.add(Box.createVerticalStrut(10)); // Espaço
        }

        // Botão de Ação (Alugar / Editar)
        actionButton = new JButton();
        actionButton.setPreferredSize(new Dimension(180, 50));
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinhar à esquerda com o infoPanel

        if (loggedInUser instanceof Cliente) {
            if (isAvailable) {
                actionButton.setText("Alugar");
                actionButton.setBackground(new Color(0, 128, 0)); // Verde
                actionButton.setForeground(Color.WHITE);
                actionButton.addActionListener(e -> handleRentVehicle());
            } else {
                actionButton.setText("Indisponível");
                actionButton.setBackground(Color.GRAY);
                actionButton.setEnabled(false);
            }
        } else if (loggedInUser instanceof Funcionario) {
            actionButton.setText("Editar Veículo");
            actionButton.setBackground(new Color(10, 40, 61)); // Azul principal
            actionButton.setForeground(Color.WHITE);
            actionButton.addActionListener(e -> handleEditVehicle());

            // Botão de Excluir (apenas para funcionário)
            deleteButton = new JButton("Excluir Veículo");
            deleteButton.setPreferredSize(new Dimension(180, 50));
            deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            deleteButton.setBackground(new Color(200, 0, 0)); // Vermelho para exclusão
            deleteButton.setForeground(Color.WHITE);
            deleteButton.addActionListener(e -> handleDeleteVehicle());
        }

        infoPanel.add(actionButton);
        if (deleteButton != null) { // Adiciona o botão de exclusão se existir
            infoPanel.add(Box.createVerticalStrut(10)); // Espaço
            infoPanel.add(deleteButton);
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1; // Ocupa uma linha
        gbc.weightx = 0.5; // Ocupa metade da largura disponível
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche a largura
        gbc.anchor = GridBagConstraints.NORTHWEST; // Alinha ao topo-esquerda
        topSectionPanel.add(infoPanel, gbc);

        mainContentPanel.add(topSectionPanel);
        mainContentPanel.add(Box.createVerticalStrut(30)); // Espaçamento

        // Seção Inferior: Ficha Técnica
        JLabel fichaTecnicaTitle = new JLabel("Ficha Técnica:");
        fichaTecnicaTitle.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 24f));
        fichaTecnicaTitle.setForeground(new Color(10, 40, 61));
        fichaTecnicaTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContentPanel.add(fichaTecnicaTitle);
        mainContentPanel.add(Box.createVerticalStrut(15));

        JTextArea fichaTecnicaArea = new JTextArea();
        fichaTecnicaArea.setEditable(false);
        fichaTecnicaArea.setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 14f));
        fichaTecnicaArea.setForeground(new Color(43, 43, 43));
        fichaTecnicaArea.setBackground(UIManager.getColor("Panel.background"));
        fichaTecnicaArea.setLineWrap(true);
        fichaTecnicaArea.setWrapStyleWord(true);
        fichaTecnicaArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        buildFichaTecnica(fichaTecnicaArea); // Preenche a ficha técnica

        JScrollPane fichaTecnicaScrollPane = new JScrollPane(fichaTecnicaArea);
        fichaTecnicaScrollPane.setPreferredSize(new Dimension(mainContentPanel.getWidth(), 300)); // Altura fixa, largura flexível
        fichaTecnicaScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        fichaTecnicaScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Borda sutil
        mainContentPanel.add(fichaTecnicaScrollPane);

        JScrollPane mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void loadAndSetVehicleImage(String imagePath) {
        Image veiculoImage = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                // Tenta carregar do caminho absoluto (dump/profile_pics ou dump/vehicle_pics)
                File savedImageFile = new File(imagePath);
                if (savedImageFile.exists()) {
                    veiculoImage = ImageIO.read(savedImageFile);
                } else {
                    // Fallback para recurso do classpath (para imagens do dump original como /images/carrossel/...)
                    URL imageUrl = getClass().getResource(imagePath);
                    if (imageUrl != null) {
                        veiculoImage = ImageIO.read(imageUrl);
                    } else {
                        System.err.println("Imagem do veículo não encontrada: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar imagem do veículo: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar/escalar imagem do veículo: " + e.getMessage());
            }
        }

        if (veiculoImage != null) {
            Image scaledImage = ImageScaler.getScaledImage(veiculoImage, vehicleImageLabel.getPreferredSize().width, vehicleImageLabel.getPreferredSize().height);
            vehicleImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            vehicleImageLabel.setIcon(null);
            vehicleImageLabel.setText("Imagem Indisponível");
        }
    }

    private void buildFichaTecnica(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        sb.append("Placa: ").append(selectedVeiculo.getPlaca()).append("\n");
        sb.append("Marca: ").append(selectedVeiculo.getMarca()).append("\n");
        sb.append("Modelo: ").append(selectedVeiculo.getModelo()).append("\n");
        sb.append("Nome Comercial: ").append(selectedVeiculo.getNome()).append("\n");
        sb.append("Ano: ").append(selectedVeiculo.getAno()).append("\n");
        sb.append("Cor: ").append(selectedVeiculo.getCor()).append("\n");
        sb.append("Função: ").append(selectedVeiculo.getFuncao()).append("\n");
        sb.append("Quilometragem: ").append(String.format("%.1f", selectedVeiculo.getQuilometragem())).append(" km\n");
        sb.append("Número de Passageiros: ").append(selectedVeiculo.getNumeroPassageiros()).append("\n");
        sb.append("Consumo (Km/L): ").append(String.format("%.1f", selectedVeiculo.getConsumoCombustivelPLitro())).append("\n");
        sb.append("Velocidade Máxima: ").append(String.format("%.1f", selectedVeiculo.getVelocidadeMax())).append(" km/h\n");
        sb.append("Automático: ").append(selectedVeiculo.isAutomatico() ? "Sim" : "Não").append("\n");
        sb.append("Combustível: ").append(selectedVeiculo.getCombustivel()).append("\n");
        sb.append("Tração: ").append(selectedVeiculo.getTracao()).append("\n");
        sb.append("Quantidade de Assentos: ").append(selectedVeiculo.getQuantAssento()).append("\n");
        sb.append("AirBag: ").append(selectedVeiculo.isAirBag() ? "Sim" : "Não").append("\n");
        sb.append("Potência: ").append(String.format("%.1f", selectedVeiculo.getPotencia())).append(" cv\n");
        sb.append("Vidro Elétrico: ").append(selectedVeiculo.isVidroEletrico() ? "Sim" : "Não").append("\n");
        sb.append("Ar Condicionado: ").append(selectedVeiculo.isArCondicionado() ? "Sim" : "Não").append("\n");
        sb.append("Multimídia: ").append(selectedVeiculo.isMultimidia() ? "Sim" : "Não").append("\n");
        sb.append("Entrada USB: ").append(selectedVeiculo.isEntradaUSB() ? "Sim" : "Não").append("\n");
        sb.append("Vidro Fumê: ").append(selectedVeiculo.isVidroFume() ? "Sim" : "Não").append("\n");
        sb.append("Peso: ").append(String.format("%.1f", selectedVeiculo.getPeso())).append(" kg\n");
        sb.append("Engate: ").append(selectedVeiculo.isEngate() ? "Sim" : "Não").append("\n");
        sb.append("Direção Hidráulica: ").append(selectedVeiculo.isDirecaoHidraulica() ? "Sim" : "Não").append("\n");
        sb.append("Valor Diário: R$ ").append(String.format("%.2f", selectedVeiculo.getValorDiario())).append("\n");
        sb.append("Número de Locações: ").append(selectedVeiculo.getLocacoes()).append("\n");

        if (selectedVeiculo instanceof Carro carro) {
            sb.append("\n--- Detalhes do Carro ---\n");
            sb.append("Portas: ").append(carro.getPortas()).append("\n");
            sb.append("Aerofólio: ").append(carro.isAerofolio() ? "Sim" : "Não").append("\n");
        } else if (selectedVeiculo instanceof Moto moto) {
            sb.append("\n--- Detalhes da Moto ---\n");
            sb.append("Cilindradas: ").append(moto.getCilindradas()).append("\n");
            sb.append("Porta Carga: ").append(moto.isPortaCarga() ? "Sim" : "Não").append("\n");
            sb.append("Raio do Pneu: ").append(moto.getRaioPneu()).append("\n");
        } else if (selectedVeiculo instanceof Caminhao caminhao) {
            sb.append("\n--- Detalhes do Caminhão ---\n");
            sb.append("Carga Máxima: ").append(String.format("%.1f", caminhao.getCargaMaxima())).append(" kg\n");
            sb.append("Altura: ").append(String.format("%.1f", caminhao.getAltura())).append(" m\n");
            sb.append("Largura: ").append(String.format("%.1f", caminhao.getLargura())).append(" m\n");
            sb.append("Comprimento: ").append(String.format("%.1f", caminhao.getComprimento())).append(" m\n");
            sb.append("Tipo de Vagão: ").append(caminhao.getTipoVagao()).append("\n");
        }

        area.setText(sb.toString());
        area.setCaretPosition(0); // Rola para o topo
    }

    private void calculateRentalValue() {
        String daysText = rentalDaysField.getText();
        if (daysText.isEmpty()) {
            totalRentalValueLabel.setText("Total: R$ 0.00");
            return;
        }
        try {
            int days = Integer.parseInt(daysText);
            if (days <= 0) {
                totalRentalValueLabel.setText("Total: R$ 0.00");
                return;
            }
            double total = selectedVeiculo.getValorDiario() * days;
            totalRentalValueLabel.setText("Total: R$ " + String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            totalRentalValueLabel.setText("Total: Inválido");
        }
    }

    // TODO: Implementar lógica de aluguel real
    private void handleRentVehicle() {
        if (!(loggedInUser instanceof Cliente)) {
            JOptionPane.showMessageDialog(this, "Apenas clientes podem alugar veículos.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = (Cliente) loggedInUser;

        String daysText = rentalDaysField.getText();
        if (daysText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o número de dias para alugar.", "Erro de Aluguel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysText);
            if (days <= 0) {
                JOptionPane.showMessageDialog(this, "O número de dias deve ser maior que zero.", "Erro de Aluguel", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de dias inválido. Por favor, digite um número inteiro.", "Erro de Aluguel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double valorTotal = selectedVeiculo.getValorDiario() * days;

        // Verifica disponibilidade novamente
        if (veiculoController.estaLocado(selectedVeiculo)) {
            JOptionPane.showMessageDialog(this, "Este veículo já está locado no momento.", "Veículo Indisponível", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulação de pagamento com saldo
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirmar aluguel de %s por %d dias?\nValor Total: R$ %.2f\nSeu saldo atual: R$ %.2f",
                        selectedVeiculo.getNome(), days, valorTotal, ((Cliente) loggedInUser).getSaldo()),
                "Confirmar Aluguel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (cliente.getSaldo() >= valorTotal) {
                // CHAMA O LOCACAOCONTROLLER PARA REALIZAR A OPERAÇÃO COMPLETA
                boolean aluguelSucesso = locacaoController.realizarLocacao(cliente, selectedVeiculo, days, valorTotal);

                if (aluguelSucesso) {
                    JOptionPane.showMessageDialog(this, "Veículo alugado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new MainScreen(cliente).setVisible(true); // Retorna à tela principal (e recarrega dados)
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao registrar aluguel. Saldo, disponibilidade ou erro interno.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente. Seu saldo é R$ " + String.format("%.2f", cliente.getSaldo()) + ".", "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // TODO: Implementar lógica de edição e exclusão
    private void handleEditVehicle() {
        JOptionPane.showMessageDialog(this, "Funcionalidade de edição de veículo a ser implementada.", "Em Desenvolvimento", JOptionPane.INFORMATION_MESSAGE);
        // Aqui você abriria a VehicleRegistrationScreen, passando o veículo para edição
        // dispose();
        // VehicleRegistrationScreen editScreen = new VehicleRegistrationScreen((Funcionario) loggedInUser, selectedVeiculo);
        // editScreen.setVisible(true);
    }

    private void handleDeleteVehicle() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o veículo " + selectedVeiculo.getNome() + " (" + selectedVeiculo.getPlaca() + ")?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar o VeiculoController para excluir o veículo
            // boolean exclusaoSucesso = veiculoController.excluirVeiculo(selectedVeiculo);
            // if (exclusaoSucesso) {
            //      JOptionPane.showMessageDialog(this, "Veículo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            //      dispose();
            //      new MainScreen((Funcionario) loggedInUser).setVisible(true); // Volta à tela principal
            // } else {
            //      JOptionPane.showMessageDialog(this, "Falha ao excluir veículo.", "Erro", JOptionPane.ERROR_MESSAGE);
            // }
            JOptionPane.showMessageDialog(this, "Simulação de exclusão concluída! (Veículo removido)", "Sucesso (Simulado)", JOptionPane.INFORMATION_MESSAGE);
            // Para a simulação, apenas volta à tela principal
            dispose();
            new MainScreen((Funcionario) loggedInUser).setVisible(true); // Retorna à tela principal
        }
    }
}