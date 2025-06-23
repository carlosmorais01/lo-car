package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.VeiculoController;
import entities.*;
import enums.*;
import util.ImageScaler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A classe `VehicleRegistrationScreen` permite que funcionários cadastrem novos veículos
 * ou editem veículos existentes no sistema LoCar!. A tela adapta seus campos
 * com base no tipo de veículo selecionado (Carro, Moto ou Caminhão).
 */
public class VehicleRegistrationScreen extends JFrame {

    private Funcionario loggedInFuncionario;
    private VeiculoController veiculoController;
    private Veiculo veiculoToEdit;

    private JTextField descricaoField;
    private JTextField placaField;
    private JTextField marcaField;
    private JTextField nomeModeloField;
    private JTextField modeloAnoField;
    private JComboBox<Cor> corComboBox;
    private JComboBox<Funcao> funcaoComboBox;
    private JTextField quilometragemField;
    private JTextField numeroPassageirosField;
    private JTextField consumoCombustivelField;
    private JTextField velocidadeMaxField;
    private JCheckBox automaticoCheckBox;
    private JComboBox<Combustivel> combustivelComboBox;
    private JComboBox<Tracao> tracaoComboBox;
    private JTextField quantAssentoField;
    private JCheckBox airBagCheckBox;
    private JTextField potenciaField;
    private JCheckBox vidroEletricoCheckBox;
    private JCheckBox arCondicionadoCheckBox;
    private JCheckBox multimidiaCheckBox;
    private JCheckBox entradaUSBCheckBox;
    private JCheckBox vidroFumeCheckBox;
    private JTextField pesoField;
    private JCheckBox engateCheckBox;
    private JCheckBox direcaoHidraulicaCheckBox;
    private JTextField valorDiarioField;

    private JRadioButton carroRadio;
    private JRadioButton motoRadio;
    private JRadioButton caminhaoRadio;
    private ButtonGroup tipoVeiculoGroup;
    private JPanel specificFieldsPanel;

    private JTextField portasField;
    private JCheckBox aerofolioCheckBox;

    private JTextField cilindradasField;
    private JCheckBox portaCargaCheckBox;
    private JTextField raioPneuField;

    private JTextField cargaMaximaField;
    private JTextField alturaField;
    private JTextField larguraField;
    private JTextField comprimentoField;
    private JComboBox<Vagao> tipoVagaoComboBox;

    private JLabel vehicleImagePreview;
    private String selectedImagePath;

    private JButton registerVehicleButton;
    private JButton backButton;

    /**
     * Construtor para a tela de cadastro de um novo veículo.
     *
     * @param funcionario O objeto {@link Funcionario} que está logado e realizando o cadastro.
     */
    public VehicleRegistrationScreen(Funcionario funcionario) {
        this.loggedInFuncionario = funcionario;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    /**
     * Construtor para a tela de edição de um veículo existente.
     *
     * @param funcionario O objeto {@link Funcionario} que está logado e realizando a edição.
     * @param veiculo     O objeto {@link Veiculo} a ser editado.
     */
    public VehicleRegistrationScreen(Funcionario funcionario, Veiculo veiculo) {
        this.loggedInFuncionario = funcionario;
        this.veiculoController = new VeiculoController();
        this.veiculoToEdit = veiculo;
        initializeUI();
        populateFieldsForEdit();
    }

    /**
     * Inicializa e configura os componentes da interface do usuário para a tela de cadastro/edição de veículo.
     * Isso inclui a seleção do tipo de veículo, campos para informações gerais, campos específicos por tipo,
     * upload de imagem e botões de ação.
     */
    private void initializeUI() {
        setTitle("LoCar! - Cadastrar Veículo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(10, 38, 64));
        JLabel title = new JLabel("Cadastro de Veículo");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formContentPanel = new JPanel();
        formContentPanel.setLayout(new BoxLayout(formContentPanel, BoxLayout.Y_AXIS));
        formContentPanel.setBackground(UIManager.getColor("Panel.background"));
        formContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel typeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        typeSelectionPanel.setBackground(UIManager.getColor("Panel.background"));
        JLabel typeLabel = new JLabel("Tipo de Veículo:");
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD, 16f));
        typeSelectionPanel.add(typeLabel);

        tipoVeiculoGroup = new ButtonGroup();
        carroRadio = new JRadioButton("Carro");
        motoRadio = new JRadioButton("Moto");
        caminhaoRadio = new JRadioButton("Caminhão");

        carroRadio.setSelected(true);
        tipoVeiculoGroup.add(carroRadio);
        tipoVeiculoGroup.add(motoRadio);
        tipoVeiculoGroup.add(caminhaoRadio);

        ActionListener typeSelectionListener = e -> updateSpecificFieldsPanel();
        carroRadio.addActionListener(typeSelectionListener);
        motoRadio.addActionListener(typeSelectionListener);
        caminhaoRadio.addActionListener(typeSelectionListener);

        typeSelectionPanel.add(carroRadio);
        typeSelectionPanel.add(motoRadio);
        typeSelectionPanel.add(caminhaoRadio);
        formContentPanel.add(typeSelectionPanel);
        formContentPanel.add(Box.createVerticalStrut(20));

        JPanel generalFieldsPanel = new JPanel(new GridLayout(0, 2, 20, 15));
        generalFieldsPanel.setBackground(UIManager.getColor("Panel.background"));
        generalFieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(10, 40, 61), 1),
                "Informações Gerais do Veículo",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16f),
                new Color(10, 40, 61)
        ));

        descricaoField = createStyledTextField("Descrição");
        generalFieldsPanel.add(descricaoField);
        placaField = createStyledTextField("Placa");
        generalFieldsPanel.add(placaField);
        marcaField = createStyledTextField("Marca");
        generalFieldsPanel.add(marcaField);
        nomeModeloField = createStyledTextField("Nome/Modelo Comercial");
        generalFieldsPanel.add(nomeModeloField);
        modeloAnoField = createStyledTextField("Ano do Modelo (YYYY)");
        generalFieldsPanel.add(modeloAnoField);

        corComboBox = createStyledComboBox(Cor.values(), "Cor");
        generalFieldsPanel.add(corComboBox);
        funcaoComboBox = createStyledComboBox(Funcao.values(), "Função");
        generalFieldsPanel.add(funcaoComboBox);

        quilometragemField = createStyledTextField("Quilometragem (km)");
        generalFieldsPanel.add(quilometragemField);
        numeroPassageirosField = createStyledTextField("Número de Passageiros");
        generalFieldsPanel.add(numeroPassageirosField);
        consumoCombustivelField = createStyledTextField("Consumo (km/L)");
        generalFieldsPanel.add(consumoCombustivelField);
        velocidadeMaxField = createStyledTextField("Velocidade Máxima (km/h)");
        generalFieldsPanel.add(velocidadeMaxField);
        potenciaField = createStyledTextField("Potência (cv)");
        generalFieldsPanel.add(potenciaField);
        pesoField = createStyledTextField("Peso (kg)");
        generalFieldsPanel.add(pesoField);
        valorDiarioField = createStyledTextField("Valor Diário (R$)");
        generalFieldsPanel.add(valorDiarioField);

        automaticoCheckBox = new JCheckBox("Automático");
        automaticoCheckBox.setBackground(UIManager.getColor("Panel.background"));
        automaticoCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(automaticoCheckBox);

        combustivelComboBox = createStyledComboBox(Combustivel.values(), "Combustível");
        generalFieldsPanel.add(combustivelComboBox);
        tracaoComboBox = createStyledComboBox(Tracao.values(), "Tração");
        generalFieldsPanel.add(tracaoComboBox);

        quantAssentoField = createStyledTextField("Quantidade de Assentos");
        generalFieldsPanel.add(quantAssentoField);

        airBagCheckBox = new JCheckBox("AirBag");
        airBagCheckBox.setBackground(UIManager.getColor("Panel.background"));
        airBagCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(airBagCheckBox);

        vidroEletricoCheckBox = new JCheckBox("Vidro Elétrico");
        vidroEletricoCheckBox.setBackground(UIManager.getColor("Panel.background"));
        vidroEletricoCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(vidroEletricoCheckBox);

        arCondicionadoCheckBox = new JCheckBox("Ar Condicionado");
        arCondicionadoCheckBox.setBackground(UIManager.getColor("Panel.background"));
        arCondicionadoCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(arCondicionadoCheckBox);

        multimidiaCheckBox = new JCheckBox("Multimídia");
        multimidiaCheckBox.setBackground(UIManager.getColor("Panel.background"));
        multimidiaCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(multimidiaCheckBox);

        entradaUSBCheckBox = new JCheckBox("Entrada USB");
        entradaUSBCheckBox.setBackground(UIManager.getColor("Panel.background"));
        entradaUSBCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(entradaUSBCheckBox);

        vidroFumeCheckBox = new JCheckBox("Vidro Fumê");
        vidroFumeCheckBox.setBackground(UIManager.getColor("Panel.background"));
        vidroFumeCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(vidroFumeCheckBox);

        engateCheckBox = new JCheckBox("Engate");
        engateCheckBox.setBackground(UIManager.getColor("Panel.background"));
        engateCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(engateCheckBox);

        direcaoHidraulicaCheckBox = new JCheckBox("Direção Hidráulica");
        direcaoHidraulicaCheckBox.setBackground(UIManager.getColor("Panel.background"));
        direcaoHidraulicaCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        generalFieldsPanel.add(direcaoHidraulicaCheckBox);

        specificFieldsPanel = new JPanel(new CardLayout());
        specificFieldsPanel.setOpaque(false);

        JPanel carFields = new JPanel(new GridLayout(0, 2, 20, 15));
        carFields.setOpaque(false);
        portasField = createStyledTextField("Número de Portas");
        carFields.add(portasField);
        aerofolioCheckBox = new JCheckBox("Aerofólio");
        aerofolioCheckBox.setBackground(UIManager.getColor("Panel.background"));
        aerofolioCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        carFields.add(aerofolioCheckBox);
        specificFieldsPanel.add(carFields, "Carro");

        JPanel motoFields = new JPanel(new GridLayout(0, 2, 20, 15));
        motoFields.setOpaque(false);
        cilindradasField = createStyledTextField("Cilindradas");
        motoFields.add(cilindradasField);
        portaCargaCheckBox = new JCheckBox("Porta Carga");
        portaCargaCheckBox.setBackground(UIManager.getColor("Panel.background"));
        portaCargaCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        motoFields.add(portaCargaCheckBox);
        raioPneuField = createStyledTextField("Raio do Pneu");
        motoFields.add(raioPneuField);
        specificFieldsPanel.add(motoFields, "Moto");

        JPanel caminhaoFields = new JPanel(new GridLayout(0, 2, 20, 15));
        caminhaoFields.setOpaque(false);
        cargaMaximaField = createStyledTextField("Carga Máxima (kg)");
        caminhaoFields.add(cargaMaximaField);
        alturaField = createStyledTextField("Altura (m)");
        caminhaoFields.add(alturaField);
        larguraField = createStyledTextField("Largura (m)");
        caminhaoFields.add(larguraField);
        comprimentoField = createStyledTextField("Comprimento (m)");
        caminhaoFields.add(comprimentoField);
        tipoVagaoComboBox = createStyledComboBox(Vagao.values(), "Tipo de Vagão");
        caminhaoFields.add(tipoVagaoComboBox);
        specificFieldsPanel.add(caminhaoFields, "Caminhão");

        JPanel imageUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        imageUploadPanel.setBackground(UIManager.getColor("Panel.background"));

        vehicleImagePreview = new JLabel();
        vehicleImagePreview.setPreferredSize(new Dimension(150, 100));
        vehicleImagePreview.setBorder(BorderFactory.createLineBorder(new Color(10, 40, 61), 1));
        vehicleImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        vehicleImagePreview.setVerticalAlignment(SwingConstants.CENTER);
        imageUploadPanel.add(vehicleImagePreview);

        JButton selectImageButton = new JButton("Selecionar Imagem do Veículo");
        selectImageButton.addActionListener(e -> selectVehicleImage());
        imageUploadPanel.add(selectImageButton);


        formContentPanel.add(generalFieldsPanel);
        formContentPanel.add(Box.createVerticalStrut(20));
        formContentPanel.add(specificFieldsPanel);
        formContentPanel.add(Box.createVerticalStrut(20));
        formContentPanel.add(imageUploadPanel);

        JScrollPane scrollPane = new JScrollPane(formContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));

        backButton = new JButton("Voltar");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> {
            dispose();
            MainScreen mainScreen = new MainScreen(loggedInFuncionario);
            mainScreen.setVisible(true);
        });
        buttonPanel.add(backButton);

        registerVehicleButton = new JButton("Cadastrar Veículo");
        registerVehicleButton.setPreferredSize(new Dimension(200, 50));
        registerVehicleButton.addActionListener(e -> handleVehicleRegistration());
        buttonPanel.add(registerVehicleButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateSpecificFieldsPanel();
    }

    /**
     * Preenche os campos do formulário com os dados de um veículo existente
     * quando a tela é utilizada no modo de edição.
     */
    private void populateFieldsForEdit() {
        if (veiculoToEdit == null) return;

        descricaoField.setText(veiculoToEdit.getDescricao());
        placaField.setText(veiculoToEdit.getPlaca());
        placaField.setEditable(false);
        marcaField.setText(veiculoToEdit.getMarca());
        nomeModeloField.setText(veiculoToEdit.getNome());
        modeloAnoField.setText(String.valueOf(veiculoToEdit.getAno()));
        corComboBox.setSelectedItem(veiculoToEdit.getCor());
        funcaoComboBox.setSelectedItem(veiculoToEdit.getFuncao());
        quilometragemField.setText(String.valueOf(veiculoToEdit.getQuilometragem()));
        numeroPassageirosField.setText(String.valueOf(veiculoToEdit.getNumeroPassageiros()));
        consumoCombustivelField.setText(String.valueOf(veiculoToEdit.getConsumoCombustivelPLitro()));
        velocidadeMaxField.setText(String.valueOf(veiculoToEdit.getVelocidadeMax()));
        automaticoCheckBox.setSelected(veiculoToEdit.isAutomatico());
        combustivelComboBox.setSelectedItem(veiculoToEdit.getCombustivel());
        tracaoComboBox.setSelectedItem(veiculoToEdit.getTracao());
        quantAssentoField.setText(String.valueOf(veiculoToEdit.getQuantAssento()));
        airBagCheckBox.setSelected(veiculoToEdit.isAirBag());
        potenciaField.setText(String.valueOf(veiculoToEdit.getPotencia()));
        vidroEletricoCheckBox.setSelected(veiculoToEdit.isVidroEletrico());
        arCondicionadoCheckBox.setSelected(veiculoToEdit.isArCondicionado());
        multimidiaCheckBox.setSelected(veiculoToEdit.isMultimidia());
        entradaUSBCheckBox.setSelected(veiculoToEdit.isEntradaUSB());
        vidroFumeCheckBox.setSelected(veiculoToEdit.isVidroFume());
        pesoField.setText(String.valueOf(veiculoToEdit.getPeso()));
        engateCheckBox.setSelected(veiculoToEdit.isEngate());
        direcaoHidraulicaCheckBox.setSelected(veiculoToEdit.isDirecaoHidraulica());
        valorDiarioField.setText(String.valueOf(veiculoToEdit.getValorDiario()));

        selectedImagePath = veiculoToEdit.getCaminhoFoto();
        loadAndSetVehicleImage(selectedImagePath);

        if (veiculoToEdit instanceof Carro carro) {
            carroRadio.setSelected(true);
            portasField.setText(String.valueOf(carro.getPortas()));
            aerofolioCheckBox.setSelected(carro.isAerofolio());
        } else if (veiculoToEdit instanceof Moto moto) {
            motoRadio.setSelected(true);
            cilindradasField.setText(String.valueOf(moto.getCilindradas()));
            portaCargaCheckBox.setSelected(moto.isPortaCarga());
            raioPneuField.setText(String.valueOf(moto.getRaioPneu()));
        } else if (veiculoToEdit instanceof Caminhao caminhao) {
            caminhaoRadio.setSelected(true);
            cargaMaximaField.setText(String.valueOf(caminhao.getCargaMaxima()));
            alturaField.setText(String.valueOf(caminhao.getAltura()));
            larguraField.setText(String.valueOf(caminhao.getLargura()));
            comprimentoField.setText(String.valueOf(caminhao.getComprimento()));
            tipoVagaoComboBox.setSelectedItem(caminhao.getTipoVagao());
        }
        updateSpecificFieldsPanel();
        registerVehicleButton.setText("Salvar Alterações");
    }

    /**
     * Carrega e define a imagem do veículo para a prévia no `vehicleImagePreview`.
     * Tenta carregar a imagem do caminho fornecido (pode ser um arquivo local ou um recurso).
     * Se a imagem não puder ser carregada, um texto "Sem Imagem" é exibido.
     *
     * @param imagePath O caminho da imagem do veículo.
     */
    private void loadAndSetVehicleImage(String imagePath) {
        Image vehicleImg = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File fileImg = new File(imagePath);
                if (fileImg.exists()) {
                    vehicleImg = ImageIO.read(fileImg);
                } else {
                    URL resourceUrl = getClass().getResource(imagePath);
                    if (resourceUrl != null) {
                        vehicleImg = ImageIO.read(resourceUrl);
                    } else {
                        System.err.println("Imagem do veículo para prévia não encontrada: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro I/O ao carregar imagem para prévia: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar/escalar imagem para prévia: " + e.getMessage());
            }
        }
        if (vehicleImg != null) {
            vehicleImagePreview.setIcon(new ImageIcon(ImageScaler.getScaledImage(vehicleImg, 150, 100)));
        } else {
            vehicleImagePreview.setIcon(null);
            vehicleImagePreview.setText("Sem Imagem");
        }
    }

    /**
     * Atualiza o painel de campos específicos do veículo com base na seleção do tipo de veículo.
     * Utiliza um `CardLayout` para alternar entre os campos de Carro, Moto ou Caminhão.
     */
    private void updateSpecificFieldsPanel() {
        CardLayout cl = (CardLayout) (specificFieldsPanel.getLayout());
        if (carroRadio.isSelected()) {
            cl.show(specificFieldsPanel, "Carro");
        } else if (motoRadio.isSelected()) {
            cl.show(specificFieldsPanel, "Moto");
        } else if (caminhaoRadio.isSelected()) {
            cl.show(specificFieldsPanel, "Caminhão");
        }
        revalidate();
        repaint();
    }

    /**
     * Cria um {@link JTextField} com um estilo padronizado para a tela de registro de veículo,
     * incluindo tamanho preferido, bordas e placeholder.
     *
     * @param placeholder O texto do placeholder para o campo.
     * @return O {@link JTextField} estilizado.
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 10));
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(Color.WHITE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(UIManager.getFont("TextField.font").deriveFont(16f));
        return field;
    }

    /**
     * Cria um {@link JComboBox} estilizado para a seleção de enums, como Cor, Função, Combustível, Tração ou Vagão.
     *
     * @param <E>         O tipo da enumeração.
     * @param values      Um array dos valores da enumeração.
     * @param placeholder O texto do placeholder para o combobox.
     * @return O {@link JComboBox} estilizado.
     */
    private <E extends Enum<E>> JComboBox<E> createStyledComboBox(E[] values, String placeholder) {
        JComboBox<E> comboBox = new JComboBox<>(values);
        comboBox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        comboBox.setFont(UIManager.getFont("TextField.font").deriveFont(16f));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createEmptyBorder());
        comboBox.setPreferredSize(new Dimension(280, 40));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
        return comboBox;
    }

    /**
     * Abre um seletor de arquivos para o funcionário escolher uma imagem para o veículo.
     * A imagem selecionada é reescalada e exibida em `vehicleImagePreview`,
     * e seu caminho absoluto é armazenado em `selectedImagePath`.
     */
    private void selectVehicleImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione a Imagem do Veículo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToLoad = fileChooser.getSelectedFile();
            try {
                ImageIcon originalIcon = new ImageIcon(fileToLoad.getAbsolutePath());
                Image scaledImage = ImageScaler.getScaledImage(originalIcon.getImage(), 150, 100);
                vehicleImagePreview.setIcon(new ImageIcon(scaledImage));
                selectedImagePath = fileToLoad.getAbsolutePath();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                selectedImagePath = null;
                vehicleImagePreview.setIcon(null);
            }
        }
    }

    /**
     * Lida com a ação de cadastrar ou atualizar um veículo.
     * <p>
     * Coleta todos os dados dos campos do formulário, realiza validações básicas de preenchimento e formato numérico.
     * Cria um objeto {@link Veiculo} do tipo correto (Carro, Moto ou Caminhão) com base na seleção do rádio button.
     * Se estiver no modo de edição ({@code veiculoToEdit} não é nulo), chama `veiculoController.atualizarVeiculo()`;
     * caso contrário, chama `veiculoController.cadastrarVeiculo()`.
     * Exibe mensagens de sucesso ou erro e, em caso de sucesso, retorna à tela principal.
     * </p>
     */
    private void handleVehicleRegistration() {

        if (placaField.getText().isEmpty() || marcaField.getText().isEmpty() || nomeModeloField.getText().isEmpty() ||
                modeloAnoField.getText().isEmpty() || quilometragemField.getText().isEmpty() ||
                numeroPassageirosField.getText().isEmpty() || consumoCombustivelField.getText().isEmpty() ||
                velocidadeMaxField.getText().isEmpty() || potenciaField.getText().isEmpty() ||
                pesoField.getText().isEmpty() || valorDiarioField.getText().isEmpty() ||
                quantAssentoField.getText().isEmpty() || selectedImagePath == null || selectedImagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios e selecione uma imagem.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            int ano = Integer.parseInt(modeloAnoField.getText());
            double quilometragem = Double.parseDouble(quilometragemField.getText().replace(",", "."));
            int numPassageiros = Integer.parseInt(numeroPassageirosField.getText());
            double consumo = Double.parseDouble(consumoCombustivelField.getText().replace(",", "."));
            double velocidadeMax = Double.parseDouble(velocidadeMaxField.getText().replace(",", "."));
            double potencia = Double.parseDouble(potenciaField.getText().replace(",", "."));
            double peso = Double.parseDouble(pesoField.getText().replace(",", "."));
            double valorDiario = Double.parseDouble(valorDiarioField.getText().replace(",", "."));
            int quantAssento = Integer.parseInt(quantAssentoField.getText());

            Veiculo novoVeiculo = null;

            if (carroRadio.isSelected()) {
                int portas = Integer.parseInt(portasField.getText());
                boolean aerofolio = aerofolioCheckBox.isSelected();
                novoVeiculo = new Carro(
                        descricaoField.getText(), placaField.getText(), marcaField.getText(),
                        nomeModeloField.getText(), modeloAnoField.getText(), ano,
                        (Cor) corComboBox.getSelectedItem(), (Funcao) funcaoComboBox.getSelectedItem(),
                        quilometragem, numPassageiros, consumo, velocidadeMax,
                        automaticoCheckBox.isSelected(), (Combustivel) combustivelComboBox.getSelectedItem(),
                        (Tracao) tracaoComboBox.getSelectedItem(), quantAssento, airBagCheckBox.isSelected(),
                        selectedImagePath, potencia, vidroEletricoCheckBox.isSelected(), arCondicionadoCheckBox.isSelected(),
                        multimidiaCheckBox.isSelected(), entradaUSBCheckBox.isSelected(), vidroFumeCheckBox.isSelected(),
                        peso, engateCheckBox.isSelected(), direcaoHidraulicaCheckBox.isSelected(),
                        valorDiario, portas, aerofolio);
            } else if (motoRadio.isSelected()) {
                int cilindradas = Integer.parseInt(cilindradasField.getText());
                boolean portaCarga = portaCargaCheckBox.isSelected();
                int raioPneu = Integer.parseInt(raioPneuField.getText());
                novoVeiculo = new Moto(
                        descricaoField.getText(), placaField.getText(), marcaField.getText(),
                        nomeModeloField.getText(), modeloAnoField.getText(), ano,
                        (Cor) corComboBox.getSelectedItem(), (Funcao) funcaoComboBox.getSelectedItem(),
                        quilometragem, numPassageiros, consumo, velocidadeMax,
                        automaticoCheckBox.isSelected(), (Combustivel) combustivelComboBox.getSelectedItem(),
                        (Tracao) tracaoComboBox.getSelectedItem(), quantAssento, airBagCheckBox.isSelected(),
                        selectedImagePath, potencia, vidroEletricoCheckBox.isSelected(), arCondicionadoCheckBox.isSelected(),
                        multimidiaCheckBox.isSelected(), entradaUSBCheckBox.isSelected(), vidroFumeCheckBox.isSelected(),
                        peso, engateCheckBox.isSelected(), direcaoHidraulicaCheckBox.isSelected(),
                        valorDiario, cilindradas, portaCarga, raioPneu);
            } else if (caminhaoRadio.isSelected()) {
                double cargaMaxima = Double.parseDouble(cargaMaximaField.getText().replace(",", "."));
                double altura = Double.parseDouble(alturaField.getText().replace(",", "."));
                double largura = Double.parseDouble(larguraField.getText().replace(",", "."));
                double comprimento = Double.parseDouble(comprimentoField.getText().replace(",", "."));
                Vagao tipoVagao = (Vagao) tipoVagaoComboBox.getSelectedItem();
                novoVeiculo = new Caminhao(
                        descricaoField.getText(), placaField.getText(), marcaField.getText(),
                        nomeModeloField.getText(), modeloAnoField.getText(), ano,
                        (Cor) corComboBox.getSelectedItem(), (Funcao) funcaoComboBox.getSelectedItem(),
                        quilometragem, numPassageiros, consumo, velocidadeMax,
                        automaticoCheckBox.isSelected(), (Combustivel) combustivelComboBox.getSelectedItem(),
                        (Tracao) tracaoComboBox.getSelectedItem(), quantAssento, airBagCheckBox.isSelected(),
                        selectedImagePath, potencia, vidroEletricoCheckBox.isSelected(), arCondicionadoCheckBox.isSelected(),
                        multimidiaCheckBox.isSelected(), entradaUSBCheckBox.isSelected(), vidroFumeCheckBox.isSelected(),
                        peso, engateCheckBox.isSelected(), direcaoHidraulicaCheckBox.isSelected(),
                        valorDiario, cargaMaxima, altura, largura, comprimento, tipoVagao);
            }

            if (novoVeiculo != null) {
                boolean success;
                if (veiculoToEdit == null) {
                    success = veiculoController.cadastrarVeiculo(novoVeiculo);
                } else {
                    success = veiculoController.atualizarVeiculo(novoVeiculo);
                }

                if (success) {
                    JOptionPane.showMessageDialog(this, "Veículo " + (veiculoToEdit == null ? "cadastrado" : "atualizado") + " com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    MainScreen mainScreen = new MainScreen(loggedInFuncionario);
                    mainScreen.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao " + (veiculoToEdit == null ? "cadastrar" : "atualizar") + " veículo. Verifique a placa ou outros dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de veículo não selecionado ou dados inválidos.", "Erro", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos nos campos de números.", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao cadastrar/atualizar veículo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}