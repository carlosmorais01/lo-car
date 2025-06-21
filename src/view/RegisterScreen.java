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

public class RegisterScreen extends JFrame {
    private AuthController authController;

    private JTextField nameField;
    private JTextField cpfField;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField birthDateField; // Para a data de nascimento
    private JComboBox<Sexo> genderComboBox;

    private JTextField cityField;
    private JTextField stateField;
    private JTextField neighborhoodField;
    private JTextField streetField;
    private JTextField numberField;
    private JTextField cepField;

    private JButton registerButton;
    private JButton backButton;

    public RegisterScreen() {
        authController = new AuthController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LoCar! - Cadastro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIManager.getColor("Panel.background"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logo.png")));
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

        // Atributos de Endereço
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

        // 1. Validações básicas (campos vazios)
        if (name.isEmpty() || cpf.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || birthDateStr.isEmpty() || selectedGender == null ||
                city.isEmpty() || state.isEmpty() || neighborhood.isEmpty() || street.isEmpty() ||
                numberStr.isEmpty() || cep.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validação de senha
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem. Por favor, verifique.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Validação de formato de data
        LocalDateTime birthDateTime = null;
        try {
            LocalDate birthLocalDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            birthDateTime = birthLocalDate.atStartOfDay(); // Converte para LocalDateTime no início do dia
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data de nascimento inválido. Use dd/mm/aaaa.", "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Validação de número (para o número da casa)
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

        // 5. Validações de formato
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
                selectedGender
        );

        boolean success = authController.registerClient(newClient);

        if (success) {
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso! Agora você pode fazer login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a tela de cadastro
            new LoginScreen().setVisible(true); // Retorna para a tela de login
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar. Email ou CPF já podem estar em uso.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}