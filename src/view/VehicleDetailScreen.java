package view;

import controller.LocacaoController;
import controller.VeiculoController;
import entities.*;
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
import java.time.LocalDateTime;

/**
 * A classe `VehicleDetailScreen` representa a tela de detalhes de um veículo na aplicação LoCar!.
 * Ela exibe informações detalhadas sobre um veículo e oferece ações diferentes dependendo do tipo de usuário logado
 * (Cliente ou Funcionário) e da disponibilidade do veículo.
 */
public class VehicleDetailScreen extends JFrame {

    private Veiculo selectedVeiculo;
    private Pessoa loggedInUser;
    private VeiculoController veiculoController;
    private LocacaoController locacaoController;

    private JLabel vehicleImageLabel;
    private JLabel statusLabel;
    private JButton actionButton;
    private JButton deleteButton;
    private JTextField rentalDaysField;
    private JLabel totalRentalValueLabel;

    /**
     * Construtor para `VehicleDetailScreen`.
     *
     * @param veiculo O objeto {@link Veiculo} cujos detalhes serão exibidos.
     * @param user    O objeto {@link Pessoa} representando o usuário atualmente logado.
     */
    public VehicleDetailScreen(Veiculo veiculo, Pessoa user) {
        this.selectedVeiculo = veiculo;
        this.loggedInUser = user;
        this.veiculoController = new VeiculoController();
        this.locacaoController = new LocacaoController();
        initializeUI();
    }

    /**
     * Inicializa e configura os componentes da interface do usuário para a tela de detalhes do veículo.
     * Isso inclui o cabeçalho, a imagem e informações principais do veículo, um painel para aluguel (para clientes),
     * botões de ação e a ficha técnica detalhada.
     */
    private void initializeUI() {
        setTitle(selectedVeiculo.getNome() + " " + selectedVeiculo.getModelo() + " - LoCar!");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

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

        headerPanel.setProfileIconClickListener(e -> {
            dispose();
            UserProfileScreen profileScreen = new UserProfileScreen(loggedInUser);
            profileScreen.setVisible(true);
        });

        add(headerPanel, BorderLayout.NORTH);

        JPanel scrollableContentPanel = new JPanel();
        scrollableContentPanel.setLayout(new BoxLayout(scrollableContentPanel, BoxLayout.Y_AXIS));
        scrollableContentPanel.setBackground(UIManager.getColor("Panel.background"));
        scrollableContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel topDetailsPanel = new JPanel(new BorderLayout(30, 0));
        topDetailsPanel.setBackground(UIManager.getColor("Panel.background"));
        topDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        vehicleImageLabel = new JLabel();
        vehicleImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vehicleImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        vehicleImageLabel.setPreferredSize(new Dimension(500, 350));
        vehicleImageLabel.setOpaque(true);
        vehicleImageLabel.setBackground(new Color(230, 230, 230));
        vehicleImageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        loadAndSetVehicleImage(selectedVeiculo.getCaminhoFoto());

        JPanel imageWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageWrapperPanel.setBackground(UIManager.getColor("Panel.background"));
        imageWrapperPanel.add(vehicleImageLabel);
        topDetailsPanel.add(imageWrapperPanel, BorderLayout.WEST);

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
            /**
             * Exibe uma mensagem informando o meio de pagamento (PIX).
             * @param e O evento de mouse.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(VehicleDetailScreen.this, "Meio de pagamento: PIX.", "Meio de Pagamento", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        infoAndRentPanel.add(paymentOptionsLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(15));

        JLabel vehicleLocationLabel = new JLabel("Setor Marista - Goiânia");
        vehicleLocationLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 16f));
        vehicleLocationLabel.setForeground(Color.GRAY.darker());
        vehicleLocationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(vehicleLocationLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(15));

        statusLabel = new JLabel();
        statusLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 20f));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndRentPanel.add(statusLabel);
        infoAndRentPanel.add(Box.createVerticalStrut(25));

        if (loggedInUser instanceof Cliente) {
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
                /**
                 * Recalcula o valor do aluguel quando uma tecla é liberada no campo de dias.
                 * @param e O evento de teclado.
                 */
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
        actionButton.setPreferredSize(new Dimension(250, 60));
        actionButton.setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 20f));
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        deleteButton = new JButton("Excluir Veículo");

        infoAndRentPanel.add(actionButton);


        infoAndRentPanel.add(Box.createVerticalGlue());

        topDetailsPanel.add(infoAndRentPanel, BorderLayout.CENTER);

        scrollableContentPanel.add(topDetailsPanel);
        scrollableContentPanel.add(Box.createVerticalStrut(30));

        JPanel fichaTecnicaPanel = createFichaTecnicaPanel();
        fichaTecnicaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fichaTecnicaPanel.setPreferredSize(new Dimension(800, 450));
        fichaTecnicaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

        JScrollPane fichaTecnicaScrollPane = new JScrollPane(fichaTecnicaPanel);
        fichaTecnicaScrollPane.setBorder(BorderFactory.createEmptyBorder());
        fichaTecnicaScrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        fichaTecnicaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fichaTecnicaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fichaTecnicaScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        fichaTecnicaScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollableContentPanel.add(fichaTecnicaScrollPane);
        scrollableContentPanel.add(Box.createVerticalGlue());

        JScrollPane mainScrollPane = new JScrollPane(scrollableContentPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);

        updateStatusLabel();
        calculateRentalValue();
    }

    /**
     * Carrega e define a imagem do veículo no `vehicleImageLabel`.
     * Tenta carregar a imagem do caminho fornecido (pode ser um arquivo local ou um recurso).
     * Se a imagem não puder ser carregada, um texto "Imagem Indisponível" é exibido.
     *
     * @param imagePath O caminho da imagem do veículo.
     */
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

    /**
     * Cria e retorna um painel contendo a ficha técnica detalhada do veículo.
     * As informações são organizadas em duas colunas com rótulos e valores formatados.
     * O conteúdo exibido varia dependendo do tipo específico de veículo (Carro, Moto, Caminhão).
     *
     * @return Um {@link JPanel} contendo a ficha técnica do veículo.
     */
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

        gbcEsquerda.gridx = 0;
        gbcEsquerda.gridy = rowEsquerda;
        gbcEsquerda.weighty = 1.0;
        gbcEsquerda.gridwidth = 2;
        gbcEsquerda.fill = GridBagConstraints.BOTH;
        colEsquerda.add(Box.createGlue(), gbcEsquerda);

        gbcDireita.gridx = 0;
        gbcDireita.gridy = rowDireita;
        gbcDireita.weighty = 1.0;
        gbcDireita.gridwidth = 2;
        gbcDireita.fill = GridBagConstraints.BOTH;
        colDireita.add(Box.createGlue(), gbcDireita);


        columnsPanel.add(colEsquerda);
        columnsPanel.add(colDireita);

        panel.add(columnsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Adiciona um par de rótulo e valor a um painel de layout `GridBagLayout`.
     *
     * @param panel     O {@link JPanel} ao qual os componentes serão adicionados.
     * @param gbc       As restrições de layout {@link GridBagConstraints} para posicionamento.
     * @param labelText O texto do rótulo (ex: "Marca:").
     * @param valueText O texto do valor (ex: "Toyota").
     * @param labelFont A fonte a ser usada para o rótulo.
     * @param valueFont A fonte a ser usada para o valor.
     * @param row       A linha na qual o par rótulo-valor será adicionado.
     */
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

    /**
     * Calcula e exibe o valor total do aluguel com base no número de dias inserido no campo `rentalDaysField`.
     * O valor é exibido no `totalRentalValueLabel`. Se o campo estiver vazio, inválido ou com dias <= 0, o total é R$ 0.00.
     */
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

    /**
     * Atualiza o rótulo de status de disponibilidade do veículo e a visibilidade/ação dos botões de ação,
     * dependendo se o veículo está locado ou não e do tipo de usuário logado.
     *
     * <p>Para Clientes:</p>
     * <ul>
     * <li>Se disponível: Botão "Alugar" ativo, campo de dias visível.</li>
     * <li>Se indisponível: Botão "Indisponível" desativado, campo de dias escondido.</li>
     * </ul>
     * <p>Para Funcionários:</p>
     * <ul>
     * <li>Se disponível: Botão "Editar Veículo" e "Excluir Veículo" ativos e visíveis.</li>
     * <li>Se indisponível: Botão "Registrar Devolução" ativo e visível; botão "Excluir Veículo" escondido.</li>
     * </ul>
     */
    private void updateStatusLabel() {
        boolean isAvailable = !veiculoController.estaLocado(selectedVeiculo);
        statusLabel.setText(isAvailable ? "Disponível" : "Indisponível");
        statusLabel.setForeground(isAvailable ? new Color(0, 150, 0) : Color.RED.darker());

        for (java.awt.event.ActionListener al : actionButton.getActionListeners()) {
            actionButton.removeActionListener(al);
        }
        if (deleteButton != null) {
            for (java.awt.event.ActionListener al : deleteButton.getActionListeners()) {
                deleteButton.removeActionListener(al);
            }

            if (deleteButton.getParent() == actionButton.getParent()) {
                actionButton.getParent().remove(deleteButton);
            }
        }


        if (loggedInUser instanceof Cliente) {
            actionButton.setText(isAvailable ? "Alugar" : "Indisponível");
            actionButton.setBackground(isAvailable ? new Color(0, 128, 0) : Color.GRAY);
            actionButton.setEnabled(isAvailable);
            actionButton.addActionListener(e -> handleRentVehicle());

            if (rentalDaysField != null) {
                rentalDaysField.setEnabled(isAvailable);
                rentalDaysField.setVisible(isAvailable);
            }
            if (totalRentalValueLabel != null) {
                totalRentalValueLabel.setVisible(isAvailable);
            }
            if (rentalDaysField != null && rentalDaysField.getParent() != null) {
                rentalDaysField.getParent().setVisible(isAvailable);
                rentalDaysField.getParent().revalidate();
                rentalDaysField.getParent().repaint();
            }
            if (deleteButton != null) {
                deleteButton.setVisible(false);
            }

        } else if (loggedInUser instanceof Funcionario) {
            if (isAvailable) {
                actionButton.setText("Editar Veículo");
                actionButton.setBackground(new Color(10, 40, 61));
                actionButton.setEnabled(true);
                actionButton.addActionListener(e -> handleEditVehicle());

                deleteButton.setText("Excluir Veículo");
                deleteButton.setBackground(new Color(200, 0, 0));
                deleteButton.setForeground(Color.WHITE);
                deleteButton.setEnabled(true);
                deleteButton.addActionListener(e -> handleDeleteVehicle());

                if (deleteButton.getParent() == null) {
                    actionButton.getParent().add(Box.createVerticalStrut(10));
                    actionButton.getParent().add(deleteButton);
                }
                deleteButton.setVisible(true);

            } else {
                actionButton.setText("Registrar Devolução");
                actionButton.setBackground(new Color(0, 100, 150));
                actionButton.setEnabled(true);
                actionButton.addActionListener(e -> handleReturnVehicle());

                if (deleteButton != null) {
                    deleteButton.setVisible(false);
                }
            }

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

        statusLabel.getParent().revalidate();
        statusLabel.getParent().repaint();
    }

    /**
     * Lida com a tentativa de alugar um veículo.
     * <p>
     * Este método valida se o usuário é um cliente, verifica a entrada de dias de aluguel,
     * calcula o valor total, verifica a disponibilidade do veículo e o saldo do cliente.
     * Se todas as validações passarem e o cliente confirmar, a locação é registrada
     * através do {@link LocacaoController}. Exibe mensagens de sucesso ou erro.
     * </p>
     */
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
        Locacao locacaoTemporaria = new Locacao(LocalDateTime.now(), dataPrevistaDevolucao, selectedVeiculo, (Cliente) loggedInUser);
        double valorTotal = locacaoTemporaria.calcularValorPrevisto();

        if (veiculoController.estaLocado(selectedVeiculo)) {
            JOptionPane.showMessageDialog(this, "Este veículo já está locado no momento.", "Veículo Indisponível", JOptionPane.WARNING_MESSAGE);
            updateStatusLabel();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirmar aluguel de %s por %d dias?\nValor Total: R$ %.2f\nSeu saldo atual: R$ %.2f",
                        selectedVeiculo.getNome(), days, valorTotal, cliente.getSaldo()),
                "Confirmar Aluguel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (cliente.getSaldo() >= valorTotal) {
                boolean aluguelSucesso = locacaoController.realizarLocacao(cliente, selectedVeiculo, days, valorTotal);

                if (aluguelSucesso) {
                    JOptionPane.showMessageDialog(this, "Veículo alugado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    updateStatusLabel();

                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao registrar aluguel. Saldo, disponibilidade ou erro interno.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente. Seu saldo é R$ " + String.format("%.2f", cliente.getSaldo()) + ".", "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Lida com a ação de editar um veículo.
     * Permite que um funcionário abra a tela de registro de veículo no modo de edição
     * para o veículo atualmente selecionado.
     */
    private void handleEditVehicle() {
        if (loggedInUser instanceof Funcionario) {
            dispose();
            new VehicleRegistrationScreen((Funcionario) loggedInUser, selectedVeiculo).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Apenas funcionários podem editar veículos.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lida com a ação de excluir um veículo.
     * Permite que um funcionário exclua o veículo atualmente selecionado após uma confirmação.
     * Se a exclusão for bem-sucedida, a tela principal é reaberta.
     */
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

    /**
     * Lida com a ação de registrar a devolução de um veículo.
     * <p>
     * Este método é acessível apenas por funcionários quando o veículo está indisponível (locado).
     * Ele encontra a locação ativa para o veículo, confirma a devolução com o usuário,
     * e chama o {@link LocacaoController#registrarDevolucao(Locacao)} para finalizar a locação,
     * calcular multas (se houver) e debitar o saldo do cliente.
     * </p>
     */
    private void handleReturnVehicle() {
        if (!(loggedInUser instanceof Funcionario)) {
            JOptionPane.showMessageDialog(this, "Apenas funcionários podem registrar devoluções.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Locacao locacaoAtiva = locacaoController.encontrarLocacaoAtiva(selectedVeiculo);
        if (locacaoAtiva == null) {
            JOptionPane.showMessageDialog(this, "Não há locação ativa para este veículo.", "Erro de Devolução", JOptionPane.ERROR_MESSAGE);
            updateStatusLabel();
            return;
        }

        Cliente clienteDaLocacao = locacaoAtiva.getCliente();
        if (clienteDaLocacao == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível identificar o cliente da locação.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirmar devolução do veículo %s (Cliente: %s)?\nUma possível multa será calculada.",
                        selectedVeiculo.getNome(), clienteDaLocacao.getNome()),
                "Confirmar Devolução", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean devolucaoSucesso = locacaoController.registrarDevolucao(locacaoAtiva);

            if (devolucaoSucesso) {

                double multaCalculada = locacaoAtiva.calcularMulta();
                double valorFinalLocacao = locacaoAtiva.calcularValorTotal();
                String mensagem = String.format("Devolução registrada com sucesso!\nValor final da locação: R$ %.2f", valorFinalLocacao);
                if (multaCalculada > 0) {
                    mensagem += String.format("\nMulta aplicada: R$ %.2f", multaCalculada);
                } else {
                    mensagem += String.format("\nNenhuma multa aplicada.");
                }
                JOptionPane.showMessageDialog(this, mensagem, "Devolução Sucesso", JOptionPane.INFORMATION_MESSAGE);

                updateStatusLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar devolução.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}