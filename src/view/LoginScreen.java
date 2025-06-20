// src/view/LoginScreen.java
package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente; // Importe a classe Cliente
import enums.Sexo; // Importe o enum Sexo, se necessário para testes/registro
import entities.Endereco; // Importe a classe Endereco, se necessário para testes/registro

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime; // Importe LocalDateTime
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
        setSize(800, 600); // Tamanho da janela de login
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Painel principal com BoxLayout para organizar verticalmente
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIManager.getColor("Panel.background")); // Usa a cor de fundo definida no UIManager
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Padding

        // Adiciona um espaço superior
        mainPanel.add(Box.createVerticalStrut(20));

        // Logo
        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logo.png")));

        Image image = logoIcon.getImage();
        Image newimg = image.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(newimg);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Campos de entrada
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, emailField.getPreferredSize().height + 10));
        emailField.setBackground(Color.WHITE);
        mainPanel.add(emailField);

        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");

        mainPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordField.getPreferredSize().height + 10));
        passwordField.setBackground(Color.WHITE);
        mainPanel.add(passwordField);

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Senha");

        mainPanel.add(Box.createVerticalStrut(30)); // Espaço entre campos e botões

        // Painel para os botões (para que fiquem lado a lado)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Centraliza com espaçamento
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Mantém o fundo igual ao mainPanel

        registerButton = new JButton("Cadastrar");
        registerButton.setPreferredSize(new Dimension(120, 40)); // Tamanho fixo para o botão
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        buttonPanel.add(registerButton);

        loginButton = new JButton("Entrar");
        loginButton.setPreferredSize(new Dimension(120, 40)); // Tamanho fixo para o botão
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel);

        // Adiciona um espaço inferior
        mainPanel.add(Box.createVerticalGlue()); // Empurra os componentes para cima

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
            // Aqui você pode fechar a tela de login e abrir a próxima tela da aplicação.
            // Ex: dispose(); new MainApplicationScreen(authenticatedClient).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        // Para fins de teste e demonstração da serialização, vamos simular um registro simples aqui.
        // Em um sistema real, isso abriria uma nova tela de cadastro de cliente.

        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Para simular o registro, preencha Email e Senha.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simula um novo cliente com dados mínimos para teste
        Endereco enderecoTeste = new Endereco("Cidade Teste", "Estado Teste", "Bairro Teste", "Rua Teste", 123, "12345-678");
        Cliente newClient = new Cliente(
                "Novo Usuário", // Nome
                "123.456.789-00", // CPF (apenas para teste, em real deveria ser validado e único)
                "99999-9999", // Telefone
                email, // Email do campo
                password, // Senha do campo (será hashed pelo controller)
                enderecoTeste,
                LocalDateTime.of(1990, 1, 1, 0, 0), // Data de Nascimento
                Sexo.MASCULINO // Sexo
        );

        boolean success = authController.registerClient(newClient);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registro simulado com sucesso! Tente fazer login.", "Registro", JOptionPane.INFORMATION_MESSAGE);
            emailField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar. Email ou CPF podem já estar em uso.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}