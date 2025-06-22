package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente;
import util.ImageScaler;

import entities.Funcionario; // Importar Funcionario
import entities.Pessoa;     // Importar Pessoa (se ainda não importado)

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class LoginScreen extends JFrame {
    private AuthController authController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginScreen() {
        authController = new AuthController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LoCar! - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        // Adiciona um espaço superior
        mainPanel.add(Box.createVerticalStrut(20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/logo.png")));
        Image scaledImage = ImageScaler.getScaledImage(logoIcon.getImage(), 350, 350);
        logoIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Campos de entrada
        emailField = new JTextField(20);
        criarCampoTextoArredondado(mainPanel, emailField);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        // Adiciona ActionListener para o Enter
        emailField.addActionListener(e -> handleLogin()); //

        mainPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField(20);
        criarCampoTextoArredondado(mainPanel, passwordField);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Senha");
        // Adiciona ActionListener para o Enter
        passwordField.addActionListener(e -> handleLogin()); //

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));

        registerButton = new JButton("Cadastrar");
        registerButton.setPreferredSize(new Dimension(300, 70));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        buttonPanel.add(registerButton);

        loginButton = new JButton("Entrar");
        loginButton.setPreferredSize(new Dimension(300, 70));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel);

        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private void criarCampoTextoArredondado(JPanel mainPanel, JTextField field) {
        field.setPreferredSize(new Dimension(600,70));
        field.setMaximumSize(new Dimension(700, field.getPreferredSize().height + 10)); // Usar field.getPreferredSize()
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(Color.WHITE);
        mainPanel.add(field);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pessoa authenticatedUser = authController.authenticate(email, password); // Retorna Pessoa

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo, " + authenticatedUser.getNome() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            if (authenticatedUser instanceof Cliente) {
                MainScreen mainScreen = new MainScreen((Cliente) authenticatedUser);
                mainScreen.setVisible(true);
            } else if (authenticatedUser instanceof Funcionario) {
                // Se o funcionário for para a MainScreen também, passe-o
                MainScreen mainScreen = new MainScreen((Funcionario) authenticatedUser); // Novo construtor em MainScreen
                mainScreen.setVisible(true);
                // Ou, se houver uma tela específica para funcionário:
                // FuncionarioScreen funcionarioScreen = new FuncionarioScreen((Funcionario) authenticatedUser);
                // funcionarioScreen.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        dispose();
        new RegisterScreen().setVisible(true);
    }
}