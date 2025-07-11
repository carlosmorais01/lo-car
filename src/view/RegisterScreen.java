package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente;
import entities.Endereco;
import enums.Sexo;
import util.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * A tela de registro de usuários para o sistema LoCar!.
 * Permite que novos clientes se cadastrem fornecendo informações pessoais e de endereço.
 */
public class RegisterScreen extends JFrame {
    private AuthController authController;

    private JTextField nameField;
    private JTextField cpfField;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField birthDateField;
    private JComboBox<Sexo> genderComboBox;

    private JTextField cityField;
    private JTextField stateField;
    private JTextField neighborhoodField;
    private JTextField streetField;
    private JTextField numberField;
    private JTextField cepField;

    private JLabel profileImagePreview;
    private String selectedImagePath;

    private JButton registerButton;
    private JButton backButton;

    /**
     * Construtor para a classe RegisterScreen.
     * Inicializa o controlador de autenticação e a interface do usuário.
     */
    public RegisterScreen() {
        authController = new AuthController();
        initializeUI();
    }

    /**
     * Inicializa e configura os componentes da interface do usuário da tela de registro.
     */
    private void initializeUI() {
        setTitle("LoCar! - Cadastro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIManager.getColor("Panel.background"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/icons/logo.png")));
        Image scaledImage = ImageScaler.getScaledImage(logoIcon.getImage(), 150, 150);
        logoIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("Crie sua conta LoCar!");
        titleLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 30f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 20, 15));
        formPanel.setBackground(UIManager.getColor("Panel.background"));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));

        nameField = createStyledTextField("Nome Completo");
        formPanel.add(nameField);
        cpfField = createStyledTextField("CPF (xxx.xxx.xxx-xx)");
        formPanel.add(cpfField);
        phoneField = createStyledTextField("Telefone (xx) xxxxx-xxxx");
        formPanel.add(phoneField);
        emailField = createStyledTextField("Email");
        formPanel.add(emailField);
        passwordField = createStyledPasswordField("Senha");
        formPanel.add(passwordField);
        confirmPasswordField = createStyledPasswordField("Confirmar Senha");
        formPanel.add(confirmPasswordField);
        birthDateField = createStyledTextField("Data de Nascimento (dd/mm/aaaa)");
        formPanel.add(birthDateField);

        genderComboBox = new JComboBox<>(Sexo.values());
        genderComboBox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sexo");
        genderComboBox.setFont(UIManager.getFont("TextField.font"));
        genderComboBox.setBackground(Color.WHITE);
        genderComboBox.setBorder(BorderFactory.createEmptyBorder());
        JPanel genderPanel = new JPanel(new BorderLayout());
        genderPanel.add(genderComboBox, BorderLayout.CENTER);
        genderPanel.setBackground(UIManager.getColor("Panel.background"));
        formPanel.add(genderPanel);

        cityField = createStyledTextField("Cidade");
        formPanel.add(cityField);
        stateField = createStyledTextField("Estado");
        formPanel.add(stateField);
        neighborhoodField = createStyledTextField("Bairro");
        formPanel.add(neighborhoodField);
        streetField = createStyledTextField("Rua");
        formPanel.add(streetField);
        numberField = createStyledTextField("Número");
        formPanel.add(numberField);
        cepField = createStyledTextField("CEP (xxxxx-xxx)");
        formPanel.add(cepField);

        JPanel imageSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        imageSelectionPanel.setBackground(UIManager.getColor("Panel.background"));

        profileImagePreview = new JLabel();
        profileImagePreview.setPreferredSize(new Dimension(100, 100));
        profileImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        profileImagePreview.setVerticalAlignment(SwingConstants.CENTER);
        imageSelectionPanel.add(profileImagePreview);

        JButton selectImageButton = new JButton("Selecionar Foto");
        selectImageButton.addActionListener(e -> selectProfileImage());
        imageSelectionPanel.add(selectImageButton);

        headerPanel.add(imageSelectionPanel);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));

        backButton = new JButton("Voltar");
        backButton.setPreferredSize(new Dimension(200, 70));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginScreen().setVisible(true);
            }
        });

        registerButton = new JButton("Cadastrar");
        registerButton.setPreferredSize(new Dimension(200, 70));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterAttempt();
            }
        });
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Cria um JTextField com estilo padronizado e um placeholder.
     * @param placeholder O texto do placeholder a ser exibido no campo.
     * @return Um JTextField estilizado.
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 10));

        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        field.setBackground(Color.WHITE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(UIManager.getFont("TextField.font"));

        return field;
    }

    /**
     * Cria um JPasswordField com estilo padronizado e um placeholder.
     * @param placeholder O texto do placeholder a ser exibido no campo.
     * @return Um JPasswordField estilizado.
     */
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 10));

        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        field.setBackground(Color.WHITE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setFont(UIManager.getFont("PasswordField.font"));

        return field;
    }

    /**
     * Lida com a tentativa de registro do usuário, validando os campos
     * e chamando o controlador de autenticação para registrar o cliente.
     */
    private void handleRegisterAttempt() {
        String name = nameField.getText();
        String cpf = cpfField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String birthDateStr = birthDateField.getText();
        Sexo selectedGender = (Sexo) genderComboBox.getSelectedItem();

        String city = cityField.getText();
        String state = stateField.getText();
        String neighborhood = neighborhoodField.getText();
        String street = streetField.getText();
        String numberStr = numberField.getText();
        String cep = cepField.getText();

        if (name.isEmpty() || cpf.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || birthDateStr.isEmpty() || selectedGender == null ||
                city.isEmpty() || state.isEmpty() || neighborhood.isEmpty() || street.isEmpty() ||
                numberStr.isEmpty() || cep.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem. Por favor, verifique.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime birthDateTime = null;
        try {
            LocalDate birthLocalDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            birthDateTime = birthLocalDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data de nascimento inválido. Use dd/mm/aaaa.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int number;
        try {
            number = Integer.parseInt(numberStr);
            if (number <= 0) {
                JOptionPane.showMessageDialog(this, "Número do endereço deve ser um valor positivo.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número do endereço inválido. Por favor, digite apenas números.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Formato de CPF inválido. Use xxx.xxx.xxx-xx.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!phone.matches("\\(\\d{2}\\) \\d{4,5}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Formato de Telefone inválido. Use (xx) xxxxx-xxxx.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cep.matches("\\d{5}-\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Formato de CEP inválido. Use xxxxx-xxx.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Endereco newAddress = new Endereco(city, state, neighborhood, street, number, cep);

        Cliente newClient = new Cliente(
                name,
                cpf,
                phone,
                email,
                password,
                newAddress,
                birthDateTime,
                selectedGender,
                selectedImagePath
        );

        boolean success = authController.registerClient(newClient);

        if (success) {
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso! Agora você pode fazer login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginScreen().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar. Email ou CPF já podem estar em uso.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre um seletor de arquivos para o usuário escolher uma imagem de perfil.
     * A imagem selecionada é exibida no preview e seu caminho é armazenado.
     */
    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione sua Foto de Perfil");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToLoad = fileChooser.getSelectedFile();
            try {
                ImageIcon originalIcon = new ImageIcon(fileToLoad.getAbsolutePath());

                Image scaledImage = ImageScaler.getScaledImage(originalIcon.getImage(), 100, 100);
                profileImagePreview.setIcon(new ImageIcon(scaledImage));
                selectedImagePath = fileToLoad.getAbsolutePath();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                selectedImagePath = null;
                profileImagePreview.setIcon(null);
            }
        }
    }

}