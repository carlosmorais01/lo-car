package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.LocacaoController;
import controller.VeiculoController;
import entities.*;
import enums.*;
import util.ImageScaler;
import view.components.HeaderPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.List; // Adicionado para simular a busca de veículos similares

public class VehicleDetailScreen extends JFrame {

    private Veiculo selectedVeiculo;
    private Pessoa loggedInUser;
    private VeiculoController veiculoController;
    private LocacaoController locacaoController;

    private JLabel vehicleImageLabel;
    private JLabel statusLabel; // Alterado para corresponder ao estilo do VeiculoInfoScreen
    private JButton actionButton;
    private JButton deleteButton;
    private JTextField rentalDaysField;
    private JLabel totalRentalValueLabel;

    public VehicleDetailScreen(Veiculo veiculo, Pessoa user) {
        this.selectedVeiculo = veiculo;
        this.loggedInUser = user;
        this.veiculoController = new VeiculoController();
        this.locacaoController = new LocacaoController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle(selectedVeiculo.getNome() + " " + selectedVeiculo.getModelo() + " - LoCar!"); // Título dinâmico
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700)); // Adicionado tamanho mínimo
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        String userName = (loggedInUser != null) ? loggedInUser.getNome() : "Visitante";
        String userProfilePic = (loggedInUser != null) ? loggedInUser.getCaminhoFoto() : null;
        HeaderPanel headerPanel = new HeaderPanel(userName, userProfilePic);

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

        // Painel de Conteúdo Principal (scrollable)
        JPanel scrollableContentPanel = new JPanel();
        scrollableContentPanel.setLayout(new BoxLayout(scrollableContentPanel, BoxLayout.Y_AXIS));
        scrollableContentPanel.setBackground(UIManager.getColor("Panel.background"));
        scrollableContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Seção Superior: Imagem, Info Principal, Botão
        JPanel topDetailsPanel = new JPanel(new BorderLayout(30, 0)); // Usando BorderLayout
        topDetailsPanel.setBackground(UIManager.getColor("Panel.background"));
        topDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        vehicleImageLabel = new JLabel();
        vehicleImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vehicleImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        vehicleImageLabel.setPreferredSize(new Dimension(500, 350)); // Aumentado para corresponder
        vehicleImageLabel.setOpaque(true);
        vehicleImageLabel.setBackground(new Color(230, 230, 230));
        vehicleImageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        loadAndSetVehicleImage(selectedVeiculo.getCaminhoFoto()); // Carrega a imagem

        JPanel imageWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageWrapperPanel.setBackground(UIManager.getColor("Panel.background"));
        imageWrapperPanel.add(vehicleImageLabel);
        topDetailsPanel.add(imageWrapperPanel, BorderLayout.WEST);

        // Informações e Botão de Ação (lado direito)
        JPanel infoAndRentPanel = new JPanel();
        infoAndRentPanel.setLayout(new BoxLayout(infoAndRentPanel, BoxLayout.Y_AXIS));
        infoAndRentPanel.setBackground(UIManager.getColor("Panel.background"));
        infoAndRentPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel vehicleNameLabel = new JLabel(selectedVeiculo.getNome() + " " + selectedVeiculo.getModelo());
        vehicleNameLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 30f));
        vehicleNameLabel.setForeground(UIManager.getColor("Label.foreground"));
        vehicleNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(vehicleNameLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(10));

        JLabel priceLabel = new JLabel(String.format("R$ %.2f / dia", selectedVeiculo.getValorDiario()));
        priceLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 24f));
        priceLabel.setForeground(new Color(0, 153, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(priceLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(5));

        JLabel paymentOptionsLabel = new JLabel("<html><u>Ver os meios de pagamento</u></html>");
        paymentOptionsLabel.setForeground(Color.GRAY.darker());
        paymentOptionsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        paymentOptionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentOptionsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(VehicleDetailScreen.this, "Meio de pagamento: PIX.", "Meio de Pagamento", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        infoAndRentPanel.add(paymentOptionsLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(15));

        JLabel vehicleLocationLabel = new JLabel("Setor Marista - Goiânia"); // Mantido fixo
        vehicleLocationLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 16f));
        vehicleLocationLabel.setForeground(Color.GRAY.darker());
        vehicleLocationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(vehicleLocationLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(15));

        statusLabel = new JLabel(); // Usando o novo statusLabel
        statusLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 20f));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(statusLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(25));

        // Campo de dias de aluguel e valor total (APENAS PARA CLIENTES E SE DISPONÍVEL)
        // Isso será controlado pela lógica de habilitação do botão e updateStatusLabel
        if (loggedInUser instanceof Cliente) { // Simplificado, a visibilidade será gerenciada pelo status
            JPanel rentalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            rentalPanel.setOpaque(false);
            rentalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel daysLabel = new JLabel("Alugar por (dias):");
            daysLabel.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            rentalPanel.add(daysLabel);

            rentalDaysField = new JTextField("1", 5);
            rentalDaysField.setFont(UIManager.getFont("TextField.font").deriveFont(16f));
            rentalDaysField.setPreferredSize(new Dimension(80, 30));
            rentalDaysField.setMaximumSize(new Dimension(80, 30));
            rentalDaysField.setBackground(Color.WHITE);
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

            infoAndRentPanel.add(rentalPanel);
            infoAndRentPanel.add(Box.createVerticalStrut(10));
        }

        actionButton = new JButton();
        actionButton.setPreferredSize(new Dimension(250, 60)); // Aumentado para corresponder
        actionButton.setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 20f)); // Adicionado fonte
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (loggedInUser instanceof Cliente) {
            actionButton.setText("Alugar");
            actionButton.setBackground(new Color(0, 128, 0)); // Verde
            actionButton.setForeground(Color.WHITE);
            actionButton.addActionListener(e -> handleRentVehicle());
        } else if (loggedInUser instanceof Funcionario) {
            actionButton.setText("Editar Veículo");
            actionButton.setBackground(new Color(10, 40, 61)); // Azul principal
            actionButton.setForeground(Color.WHITE);
            actionButton.addActionListener(e -> handleEditVehicle());

            deleteButton = new JButton("Excluir Veículo");
            deleteButton.setPreferredSize(new Dimension(250, 60)); // Aumentado
            deleteButton.setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 20f)); // Adicionado fonte
            deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            deleteButton.setBackground(new Color(200, 0, 0));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.addActionListener(e -> handleDeleteVehicle());
        }

        infoAndRentPanel.add(actionButton);
        if (deleteButton != null) {
            infoAndRentPanel.add(Box.createVerticalStrut(10));
            infoAndRentPanel.add(deleteButton);
        }

        infoAndRentPanel.add(Box.createVerticalGlue()); // Para empurrar o conteúdo para cima

        topDetailsPanel.add(infoAndRentPanel, BorderLayout.CENTER);

        scrollableContentPanel.add(topDetailsPanel);
        scrollableContentPanel.add(Box.createVerticalStrut(30));

        // Seção Inferior: Ficha Técnica (usando createFichaTecnicaPanel)
        JPanel fichaTecnicaPanel = createFichaTecnicaPanel();
        fichaTecnicaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fichaTecnicaPanel.setPreferredSize(new Dimension(800, 450)); // Altura fixa, largura flexível
        fichaTecnicaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600)); // Altura máxima

        JScrollPane fichaTecnicaScrollPane = new JScrollPane(fichaTecnicaPanel);
        fichaTecnicaScrollPane.setBorder(BorderFactory.createEmptyBorder());
        fichaTecnicaScrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        fichaTecnicaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fichaTecnicaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fichaTecnicaScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        fichaTecnicaScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollableContentPanel.add(fichaTecnicaScrollPane);
        scrollableContentPanel.add(Box.createVerticalGlue()); // Para empurrar o conteúdo para cima

        JScrollPane mainScrollPane = new JScrollPane(scrollableContentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);

        updateStatusLabel(); // Inicializa o status e o botão de aluguel
        calculateRentalValue(); // Calcula o valor inicial
    }

    private void loadAndSetVehicleImage(String imagePath) {
        Image veiculoImage = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File savedImageFile = new File(imagePath);
                if (savedImageFile.exists()) {
                    veiculoImage = ImageIO.read(savedImageFile);
                } else {
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
            vehicleImageLabel.setForeground(Color.RED);
            vehicleImageLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16f));
        }
    }

    // Adaptado de createFichaTecnicaPanel do VeiculoInfoScreen
    private JPanel createFichaTecnicaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIManager.getColor("Panel.background"));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                "Ficha Técnica",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                UIManager.getFont("Label.font").deriveFont(Font.BOLD, 20f),
                UIManager.getColor("Label.foreground")
        ));

        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 40, 5));
        columnsPanel.setBackground(UIManager.getColor("Panel.background"));
        columnsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel colEsquerda = new JPanel(new GridBagLayout());
        colEsquerda.setBackground(UIManager.getColor("Panel.background"));
        JPanel colDireita = new JPanel(new GridBagLayout());
        colDireita.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbcEsquerda = new GridBagConstraints();
        gbcEsquerda.insets = new Insets(4, 0, 4, 0);
        gbcEsquerda.fill = GridBagConstraints.HORIZONTAL;
        gbcEsquerda.anchor = GridBagConstraints.WEST;
        gbcEsquerda.weightx = 0.5;

        GridBagConstraints gbcDireita = new GridBagConstraints();
        gbcDireita.insets = new Insets(4, 0, 4, 0);
        gbcDireita.fill = GridBagConstraints.HORIZONTAL;
        gbcDireita.anchor = GridBagConstraints.WEST;
        gbcDireita.weightx = 0.5;

        int rowEsquerda = 0;
        int rowDireita = 0;

        Font labelFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 15f);
        Font valueFont = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 15f);

        addRowToGridBagPanel(colDireita, gbcDireita, "Marca:", selectedVeiculo.getMarca(), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Modelo:", selectedVeiculo.getModelo(), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Nome Comercial:", selectedVeiculo.getNome(), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Ano:", String.valueOf(selectedVeiculo.getAno()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Cor:", selectedVeiculo.getCor().name(), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Função:", selectedVeiculo.getFuncao().name(), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Quilometragem:", String.format("%.1f km", selectedVeiculo.getQuilometragem()), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Nº Passageiros:", String.valueOf(selectedVeiculo.getNumeroPassageiros()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Consumo:", String.format("%.1f Km/L", selectedVeiculo.getConsumoCombustivelPLitro()), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Velocidade Máxima:", String.format("%.1f km/h", selectedVeiculo.getVelocidadeMax()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Transmissão:", (selectedVeiculo.isAutomatico() ? "Automática" : "Manual"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Combustível:", selectedVeiculo.getCombustivel().name(), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Tração:", selectedVeiculo.getTracao().name(), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Assentos:", String.valueOf(selectedVeiculo.getQuantAssento()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Airbag:", (selectedVeiculo.isAirBag() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Potência:", String.format("%.1f cv", selectedVeiculo.getPotencia()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Vidros elétricos:", (selectedVeiculo.isVidroEletrico() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Ar-condicionado:", (selectedVeiculo.isArCondicionado() ? "Sim" : "Não"), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Multimídia:", (selectedVeiculo.isMultimidia() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Entrada USB:", (selectedVeiculo.isEntradaUSB() ? "Sim" : "Não"), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Vidro fumê:", (selectedVeiculo.isVidroFume() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Peso:", String.format("%.1f kg", selectedVeiculo.getPeso()), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Engate:", (selectedVeiculo.isEngate() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Direção Hidráulica:", (selectedVeiculo.isDirecaoHidraulica() ? "Sim" : "Não"), labelFont, valueFont, rowEsquerda++);
        addRowToGridBagPanel(colDireita, gbcDireita, "Valor Diário:", String.format("R$ %.2f", selectedVeiculo.getValorDiario()), labelFont, valueFont, rowDireita++);
        addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Nº Locações:", String.valueOf(selectedVeiculo.getLocacoes()), labelFont, valueFont, rowEsquerda++);


        if (selectedVeiculo instanceof Carro carro) {
            addRowToGridBagPanel(colDireita, gbcDireita, "Portas:", String.valueOf(carro.getPortas()), labelFont, valueFont, rowDireita++);
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Aerofólio:", (carro.isAerofolio() ? "Sim" : "Não"), labelFont, valueFont, rowEsquerda++);
        } else if (selectedVeiculo instanceof Moto moto) {
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Cilindradas:", String.valueOf(moto.getCilindradas()) + " cc", labelFont, valueFont, rowEsquerda++);
            addRowToGridBagPanel(colDireita, gbcDireita, "Porta Carga:", (moto.isPortaCarga() ? "Sim" : "Não"), labelFont, valueFont, rowDireita++);
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Raio Pneu:", String.valueOf(moto.getRaioPneu()) + "\"", labelFont, valueFont, rowEsquerda++);
        } else if (selectedVeiculo instanceof Caminhao caminhao) {
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Carga Máxima:", String.format("%.1f kg", caminhao.getCargaMaxima()), labelFont, valueFont, rowEsquerda++);
            addRowToGridBagPanel(colDireita, gbcDireita, "Altura:", String.format("%.1f m", caminhao.getAltura()), labelFont, valueFont, rowDireita++);
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Largura:", String.format("%.1f m", caminhao.getLargura()), labelFont, valueFont, rowEsquerda++);
            addRowToGridBagPanel(colDireita, gbcDireita, "Comprimento:", String.format("%.1f m", caminhao.getComprimento()), labelFont, valueFont, rowDireita++);
            addRowToGridBagPanel(colEsquerda, gbcEsquerda, "Tipo de Vagão:", caminhao.getTipoVagao().name(), labelFont, valueFont, rowEsquerda++);
        }

        // Add glue to push content to the top
        gbcEsquerda.gridx = 0; gbcEsquerda.gridy = rowEsquerda;
        gbcEsquerda.weighty = 1.0; gbcEsquerda.gridwidth = 2; gbcEsquerda.fill = GridBagConstraints.BOTH;
        colEsquerda.add(Box.createGlue(), gbcEsquerda);

        gbcDireita.gridx = 0; gbcDireita.gridy = rowDireita;
        gbcDireita.weighty = 1.0; gbcDireita.gridwidth = 2; gbcDireita.fill = GridBagConstraints.BOTH;
        colDireita.add(Box.createGlue(), gbcDireita);


        columnsPanel.add(colEsquerda);
        columnsPanel.add(colDireita);

        panel.add(columnsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addRowToGridBagPanel(JPanel panel, GridBagConstraints gbc, String labelText, String valueText, Font labelFont, Font valueFont, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(UIManager.getColor("Label.foreground"));
        panel.add(label, gbc);

        gbc.gridx = 1;
        JLabel value = new JLabel(valueText);
        value.setFont(valueFont);
        value.setForeground(UIManager.getColor("TextField.foreground"));
        panel.add(value, gbc);
    }

    private void calculateRentalValue() {
        String daysText = rentalDaysField != null ? rentalDaysField.getText() : "";
        if (daysText.isEmpty()) {
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setText("Total: R$ 0.00");
            }
            return;
        }
        try {
            int days = Integer.parseInt(daysText);
            if (days <= 0) {
                if (totalRentalValueLabel != null) {
                    totalRentalValueLabel.setText("Total: R$ 0.00");
                }
                return;
            }
            double total = selectedVeiculo.getValorDiario() * days;
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setText("Total: R$ " + String.format("%.2f", total));
            }
        } catch (NumberFormatException ex) {
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setText("Total: Inválido");
            }
        }
    }

    private void updateStatusLabel() {
        boolean isAvailable = !veiculoController.estaLocado(selectedVeiculo);
        statusLabel.setText(isAvailable ? "Disponível" : "Indisponível");
        statusLabel.setForeground(isAvailable ? new Color(0, 150, 0) : Color.RED.darker());

        if (loggedInUser instanceof Cliente) {
            actionButton.setEnabled(isAvailable);
            actionButton.setText(isAvailable ? "Alugar" : "Indisponível");
            actionButton.setBackground(isAvailable ? new Color(0, 128, 0) : Color.GRAY);

            // Controla a visibilidade do campo de dias e valor total
            if (rentalDaysField != null) {
                rentalDaysField.setEnabled(isAvailable);
                rentalDaysField.setVisible(isAvailable); // Esconde se indisponível
            }
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setVisible(isAvailable); // Esconde se indisponível
            }
            // Garante que o painel pai se reajuste
            if (rentalDaysField != null && rentalDaysField.getParent() != null) {
                rentalDaysField.getParent().setVisible(isAvailable);
                rentalDaysField.getParent().revalidate();
                rentalDaysField.getParent().repaint();
            }

        } else if (loggedInUser instanceof Funcionario) {
            actionButton.setText("Editar Veículo"); // Botão sempre ativo para funcionário
            actionButton.setEnabled(true);
            actionButton.setBackground(new Color(10, 40, 61));
            // Garante que os campos de aluguel não apareçam para funcionário
            if (rentalDaysField != null) {
                rentalDaysField.setVisible(false);
            }
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setVisible(false);
            }
            if (rentalDaysField != null && rentalDaysField.getParent() != null) {
                rentalDaysField.getParent().setVisible(false);
            }
        }
        // Force repaint no pai se necessário, para garantir que as cores atualizem
        statusLabel.getParent().revalidate();
        statusLabel.getParent().repaint();
    }

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

        LocalDateTime dataPrevistaDevolucao = LocalDateTime.now().plusDays(days);
        // Criamos uma Locacao temporária APENAS para calcular o valor previsto.
        // Ela não é persistida ainda.
        Locacao locacaoTemporaria = new Locacao(LocalDateTime.now(), dataPrevistaDevolucao, selectedVeiculo, (Cliente) loggedInUser); // Passa loggedInUser
        double valorTotal = locacaoTemporaria.calcularValorPrevisto();


        // Verifica disponibilidade novamente
        if (veiculoController.estaLocado(selectedVeiculo)) {
            JOptionPane.showMessageDialog(this, "Este veículo já está locado no momento.", "Veículo Indisponível", JOptionPane.WARNING_MESSAGE);
            updateStatusLabel(); // Atualiza o status caso tenha mudado
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirmar aluguel de %s por %d dias?\nValor Total: R$ %.2f\nSeu saldo atual: R$ %.2f",
                        selectedVeiculo.getNome(), days, valorTotal, cliente.getSaldo()), // Usar cliente.getSaldo()
                "Confirmar Aluguel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (cliente.getSaldo() >= valorTotal) {
                boolean aluguelSucesso = locacaoController.realizarLocacao(cliente, selectedVeiculo, days, valorTotal);

                if (aluguelSucesso) {
                    JOptionPane.showMessageDialog(this, "Veículo alugado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    updateStatusLabel(); // Atualiza o status e o botão
                    // Não fecha a tela, apenas atualiza o status
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao registrar aluguel. Saldo, disponibilidade ou erro interno.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente. Seu saldo é R$ " + String.format("%.2f", cliente.getSaldo()) + ".", "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void handleEditVehicle() {
        if (loggedInUser instanceof Funcionario) {
            dispose();
            new VehicleRegistrationScreen((Funcionario) loggedInUser, selectedVeiculo).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Apenas funcionários podem editar veículos.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteVehicle() {
        if (!(loggedInUser instanceof Funcionario)) {
            JOptionPane.showMessageDialog(this, "Apenas funcionários podem excluir veículos.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o veículo " + selectedVeiculo.getNome() + " (" + selectedVeiculo.getPlaca() + ")?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exclusaoSucesso = veiculoController.excluirVeiculo(selectedVeiculo);
            if (exclusaoSucesso) {
                JOptionPane.showMessageDialog(this, "Veículo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainScreen((Funcionario) loggedInUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir veículo. Verifique se não há locações ativas para este veículo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}