package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente;
import util.ImageScaler;

import entities.Funcionario;
import entities.Pessoa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * A classe `LoginScreen` representa a tela de login da aplicação LoCar!.
 * Ela permite que os usuários insiram suas credenciais (email e senha) para acessar o sistema,
 * ou optem por se cadastrar como um novo usuário.
 */
public class LoginScreen extends JFrame {
    private AuthController authController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * Construtor para a tela de login.
     * Inicializa o controlador de autenticação e a interface do usuário.
     */
    public LoginScreen() {
        authController = new AuthController();
        initializeUI();
    }

    /**
     * Inicializa e configura os componentes da interface do usuário para a tela de login.
     * Isso inclui o logotipo, campos de texto para email e senha, e botões de login e cadastro.
     */
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
        mainPanel.add(Box.createVerticalStrut(20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/icons/logo.png")));
        Image scaledImage = ImageScaler.getScaledImage(logoIcon.getImage(), 350, 350);
        logoIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);

        mainPanel.add(Box.createVerticalStrut(30));
        emailField = new JTextField(20);
        criarCampoTextoArredondado(mainPanel, emailField);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        emailField.addActionListener(e -> handleLogin());

        mainPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField(20);
        criarCampoTextoArredondado(mainPanel, passwordField);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Senha");
        passwordField.addActionListener(e -> handleLogin());

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));

        registerButton = new JButton("Cadastrar");
        registerButton.setPreferredSize(new Dimension(300, 70));
        registerButton.addActionListener(new ActionListener() {
            /**
             * Lida com a ação de clique do botão "Cadastrar".
             * Fecha a tela de login e abre a tela de registro.
             * @param e O evento de ação.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        buttonPanel.add(registerButton);

        loginButton = new JButton("Entrar");
        loginButton.setPreferredSize(new Dimension(300, 70));
        loginButton.addActionListener(new ActionListener() {
            /**
             * Lida com a ação de clique do botão "Entrar".
             * Invoca o método {@link #handleLogin()} para processar a tentativa de login.
             * @param e O evento de ação.
             */
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

    /**
     * Configura as propriedades de tamanho e borda para um campo de texto,
     * adicionando-o ao painel principal.
     *
     * @param mainPanel O painel principal onde o campo de texto será adicionado.
     * @param field O campo de texto a ser configurado.
     */
    private void criarCampoTextoArredondado(JPanel mainPanel, JTextField field) {
        field.setPreferredSize(new Dimension(600, 70));
        field.setMaximumSize(new Dimension(700, field.getPreferredSize().height + 10));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(Color.WHITE);
        mainPanel.add(field);
    }

    /**
     * Lida com a tentativa de login do usuário.
     * Valida os campos de email e senha, tenta autenticar o usuário
     * através do {@link AuthController}, e redireciona para a tela principal
     * ({@link MainScreen}) em caso de sucesso, ou exibe uma mensagem de erro.
     */
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pessoa authenticatedUser = authController.authenticate(email, password);

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo, " + authenticatedUser.getNome() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            if (authenticatedUser instanceof Cliente) {
                MainScreen mainScreen = new MainScreen((Cliente) authenticatedUser);
                mainScreen.setVisible(true);
            } else if (authenticatedUser instanceof Funcionario) {
                MainScreen mainScreen = new MainScreen((Funcionario) authenticatedUser);
                mainScreen.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lida com a ação de ir para a tela de cadastro.
     * Fecha a tela de login e abre uma nova instância de {@link RegisterScreen}.
     */
    private void handleRegister() {
        dispose();
        new RegisterScreen().setVisible(true);
    }
}