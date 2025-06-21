// src/view/LoginScreen.java
package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente;
import util.ImageScaler;

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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        // Adiciona um espaço superior
        mainPanel.add(Box.createVerticalStrut(20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logo.png")));
        Image scaledImage = ImageScaler.getScaledImage(logoIcon.getImage(), 350, 350);
        logoIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Campos de entrada
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(600,70));
        emailField.setMaximumSize(new Dimension(700, emailField.getPreferredSize().height + 10));
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        emailField.setBackground(Color.WHITE);
        mainPanel.add(emailField);

        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");

        mainPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(600,70));
        passwordField.setMaximumSize(new Dimension(700, passwordField.getPreferredSize().height + 10));
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordField.setBackground(Color.WHITE);
        mainPanel.add(passwordField);

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Senha");

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Centraliza com espaçamento
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Mantém o fundo igual ao mainPanel

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

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente authenticatedClient = authController.authenticate(email, password);

        if (authenticatedClient != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo, " + authenticatedClient.getNome() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            // Aqui fechar a tela de login e abrir a próxima tela da aplicação.
            // Ex: dispose(); new MainApplicationScreen(authenticatedClient).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        dispose();
        new RegisterScreen().setVisible(true);
    }
}