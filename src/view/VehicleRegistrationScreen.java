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
import java.time.LocalDateTime;
import java.util.Objects;

public class VehicleRegistrationScreen extends JFrame {

    private Funcionario loggedInFuncionario;
    private VeiculoController veiculoController;

    // Campos de input para o Veículo (atributos gerais)
    private JTextField descricaoField;
    private JTextField placaField;
    private JTextField marcaField;
    private JTextField nomeModeloField; // Nome (ex: Corolla) ou Modelo (ex: Civic)
    private JTextField modeloAnoField; // Ano do modelo
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

    // Campos específicos por tipo de veículo
    private JRadioButton carroRadio;
    private JRadioButton motoRadio;
    private JRadioButton caminhaoRadio;
    private ButtonGroup tipoVeiculoGroup;
    private JPanel specificFieldsPanel; // Painel que muda conforme o tipo

    // Campos específicos de Carro
    private JTextField portasField;
    private JCheckBox aerofolioCheckBox;

    // Campos específicos de Moto
    private JTextField cilindradasField;
    private JCheckBox portaCargaCheckBox;
    private JTextField raioPneuField;

    // Campos específicos de Caminhão
    private JTextField cargaMaximaField;
    private JTextField alturaField;
    private JTextField larguraField;
    private JTextField comprimentoField;
    private JComboBox<Vagao> tipoVagaoComboBox;

    private JLabel vehicleImagePreview;
    private String selectedImagePath;

    private JButton registerVehicleButton;
    private JButton backButton;

    public VehicleRegistrationScreen(Funcionario funcionario) {
        this.loggedInFuncionario = funcionario;
        this.veiculoController = new VeiculoController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LoCar! - Cadastrar Veículo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));

        // Header (simplificado para esta tela, ou usar HeaderPanel se quiser)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(10, 38, 64)); // Mesma cor do HeaderPanel
        JLabel title = new JLabel("Cadastro de Veículo");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Painel do formulário principal
        JPanel formContentPanel = new JPanel();
        formContentPanel.setLayout(new BoxLayout(formContentPanel, BoxLayout.Y_AXIS));
        formContentPanel.setBackground(UIManager.getColor("Panel.background"));
        formContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Seleção do tipo de veículo (Rádio Buttons)
        JPanel typeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        typeSelectionPanel.setBackground(UIManager.getColor("Panel.background"));
        JLabel typeLabel = new JLabel("Tipo de Veículo:");
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD, 16f));
        typeSelectionPanel.add(typeLabel);

        tipoVeiculoGroup = new ButtonGroup();
        carroRadio = new JRadioButton("Carro");
        motoRadio = new JRadioButton("Moto");
        caminhaoRadio = new JRadioButton("Caminhão");

        carroRadio.setSelected(true); // Começa com Carro selecionado
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

        // Painel para campos gerais do veículo
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


        // Painel para campos específicos do tipo de veículo
        specificFieldsPanel = new JPanel(new CardLayout());
        specificFieldsPanel.setOpaque(false); // Transparente para herdar o fundo do formContentPanel

        // Painel para campos de Carro
        JPanel carFields = new JPanel(new GridLayout(0, 2, 20, 15));
        carFields.setOpaque(false);
        portasField = createStyledTextField("Número de Portas");
        carFields.add(portasField);
        aerofolioCheckBox = new JCheckBox("Aerofólio");
        aerofolioCheckBox.setBackground(UIManager.getColor("Panel.background"));
        aerofolioCheckBox.setForeground(UIManager.getColor("Label.foreground"));
        carFields.add(aerofolioCheckBox);
        specificFieldsPanel.add(carFields, "Carro");

        // Painel para campos de Moto
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

        // Painel para campos de Caminhão
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

        // Painel de seleção de imagem do veículo
        JPanel imageUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        imageUploadPanel.setBackground(UIManager.getColor("Panel.background"));

        vehicleImagePreview = new JLabel();
        vehicleImagePreview.setPreferredSize(new Dimension(150, 100)); // Tamanho da prévia
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


        // Painel de Botões (Finalizar Cadastro, Voltar)
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

        // Chame a atualização inicial para mostrar os campos de carro
        updateSpecificFieldsPanel();
    }

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

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 10));
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(Color.WHITE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(UIManager.getFont("TextField.font").deriveFont(16f)); // Fonte menor que o padrão do Main
        return field;
    }

    private <E extends Enum<E>> JComboBox<E> createStyledComboBox(E[] values, String placeholder) {
        JComboBox<E> comboBox = new JComboBox<>(values);
        comboBox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        comboBox.setFont(UIManager.getFont("TextField.font").deriveFont(16f)); // Fonte menor que o padrão do Main
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createEmptyBorder()); // Sem borda, FlatLaf arredonda se TextComponent.arc estiver setado
        comboBox.setPreferredSize(new Dimension(280, 40));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
        return comboBox;
    }

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

    private void handleVehicleRegistration() {
        // Validações básicas (campos vazios)
        if (placaField.getText().isEmpty() || marcaField.getText().isEmpty() || nomeModeloField.getText().isEmpty() ||
                modeloAnoField.getText().isEmpty() || quilometragemField.getText().isEmpty() ||
                numeroPassageirosField.getText().isEmpty() || consumoCombustivelField.getText().isEmpty() ||
                velocidadeMaxField.getText().isEmpty() || potenciaField.getText().isEmpty() ||
                pesoField.getText().isEmpty() || valorDiarioField.getText().isEmpty() ||
                quantAssentoField.getText().isEmpty() || selectedImagePath == null || selectedImagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios e selecione uma imagem.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validações de números
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

            // Validações específicas do tipo de veículo
            Veiculo novoVeiculo = null;
            String tipoVeiculo = ((CardLayout)specificFieldsPanel.getLayout()).toString(); // Isto não pega o nome da carta, vai precisar de outra forma

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
                // Salvar o veículo
                boolean success = veiculoController.cadastrarVeiculo(novoVeiculo); // NOVO MÉTODO NO CONTROLLER

                if (success) {
                    JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    // Opcional: Limpar campos ou voltar para MainScreen
                    dispose();
                    MainScreen mainScreen = new MainScreen(loggedInFuncionario);
                    mainScreen.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar veículo. Verifique a placa ou outros dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de veículo não selecionado ou dados inválidos.", "Erro", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos nos campos de números.", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao cadastrar veículo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}