package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController; // Para atualizar o Cliente/Funcionario
import entities.Cliente;
import entities.Endereco;
import entities.Funcionario;
import entities.Pessoa;
import enums.Sexo;
import util.ImageScaler;
import view.components.HeaderPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class UserProfileScreen extends JFrame {

    private Pessoa loggedInUser;
    private AuthController authController;

    private JLabel profileImageLabel;
    private JTextField nameField;
    private JTextField cpfField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressStreetField;
    private JTextField addressNumberField;
    private JTextField addressNeighborhoodField;
    private JTextField addressCityField;
    private JTextField addressStateField;
    private JTextField addressCepField;
    private JTextField birthDateField;
    private JComboBox<Sexo> genderComboBox;
    private JLabel currentBalanceLabel; // Para clientes
    private JButton addBalanceButton; // Para clientes

    private JButton editButton;
    private JButton saveButton;
    private JButton cancelEditButton;

    private String initialProfileImagePath; // Para armazenar o caminho da imagem se for mudada
    private JButton changePhotoButton;


    public UserProfileScreen(Pessoa user) {
        this.loggedInUser = user;
        this.authController = new AuthController();
        initializeUI();
        displayUserData();
        setEditMode(false);
    }

    private void initializeUI() {
        setTitle("LoCar! - Perfil de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Apenas fecha esta janela
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // Painel de Conteúdo Principal
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(UIManager.getColor("Panel.background"));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100)); // Mais padding lateral

        // Seção da Imagem de Perfil
        JPanel profileImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        profileImagePanel.setOpaque(false);
        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(350, 350)); // Tamanho da imagem de perfil
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        profileImagePanel.add(profileImageLabel);

        changePhotoButton = new JButton("Trocar Foto");
        changePhotoButton.setFont(UIManager.getFont("Button.font").deriveFont(15f));
        changePhotoButton.setPreferredSize(new Dimension(150, 50));
        changePhotoButton.setVisible(false); // Inicia invisível
        changePhotoButton.addActionListener(e -> selectProfileImage());
        profileImagePanel.add(changePhotoButton);

        mainContentPanel.add(profileImagePanel);
        mainContentPanel.add(Box.createVerticalStrut(20));

        // Formulário de Detalhes do Usuário
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 colunas, espaçamento
        formPanel.setOpaque(false);

        // NOME
        formPanel.add(createLabel("Nome:"));
        nameField = createStyledTextField("");
        formPanel.add(nameField);

        // CPF (não editável)
        formPanel.add(createLabel("CPF:"));
        cpfField = createStyledTextField("");
        cpfField.setEditable(false); // CPF geralmente não é editável
        formPanel.add(cpfField);

        // TELEFONE
        formPanel.add(createLabel("Telefone:"));
        phoneField = createStyledTextField("");
        formPanel.add(phoneField);

        // EMAIL
        formPanel.add(createLabel("Email:"));
        emailField = createStyledTextField("");
        formPanel.add(emailField);

        // ENDEREÇO
        formPanel.add(createLabel("Rua:"));
        addressStreetField = createStyledTextField("");
        formPanel.add(addressStreetField);

        formPanel.add(createLabel("Número:"));
        addressNumberField = createStyledTextField("");
        formPanel.add(addressNumberField);

        formPanel.add(createLabel("Bairro:"));
        addressNeighborhoodField = createStyledTextField("");
        formPanel.add(addressNeighborhoodField);

        formPanel.add(createLabel("Cidade:"));
        addressCityField = createStyledTextField("");
        formPanel.add(addressCityField);

        formPanel.add(createLabel("Estado:"));
        addressStateField = createStyledTextField("");
        formPanel.add(addressStateField);

        formPanel.add(createLabel("CEP:"));
        addressCepField = createStyledTextField("");
        formPanel.add(addressCepField);

        // DATA DE NASCIMENTO
        formPanel.add(createLabel("Data de Nasc.:"));
        birthDateField = createStyledTextField("");
        formPanel.add(birthDateField);

        // SEXO
        formPanel.add(createLabel("Sexo:"));
        genderComboBox = new JComboBox<>(Sexo.values());
        genderComboBox.setFont(UIManager.getFont("TextField.font"));
        genderComboBox.setBackground(Color.WHITE);
        genderComboBox.setBorder(BorderFactory.createEmptyBorder());
        genderComboBox.setPreferredSize(new Dimension(280, 40));
        genderComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, genderComboBox.getPreferredSize().height));
        formPanel.add(genderComboBox);

        mainContentPanel.add(formPanel);
        mainContentPanel.add(Box.createVerticalStrut(20));

        // Painel de Botões de Ação (Editar, Salvar, Cancelar, e Adicionar Saldo)
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionButtonsPanel.setOpaque(false);
        actionButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza horizontalmente

        editButton = new JButton("Editar Perfil");
        editButton.setPreferredSize(new Dimension(180, 50));
        editButton.addActionListener(e -> setEditMode(true));
        editButton.setFont(editButton.getFont().deriveFont(Font.BOLD, 14f));
        actionButtonsPanel.add(editButton);

        saveButton = new JButton("Salvar Alterações");
        saveButton.setPreferredSize(new Dimension(180, 50));
        saveButton.addActionListener(e -> handleSaveChanges());
        saveButton.setFont(editButton.getFont().deriveFont(Font.BOLD, 14f));
        actionButtonsPanel.add(saveButton);

        cancelEditButton = new JButton("Cancelar");
        cancelEditButton.setPreferredSize(new Dimension(180, 50));
        cancelEditButton.setFont(editButton.getFont().deriveFont(Font.BOLD, 14f));
        cancelEditButton.addActionListener(e -> {
            displayUserData();
            setEditMode(false);
        });
        actionButtonsPanel.add(cancelEditButton);

        // Botão Adicionar Saldo (apenas para clientes)
        if (loggedInUser instanceof Cliente cliente) {
            formPanel.add(createLabel("Saldo Atual:"));
            currentBalanceLabel = createLabel("R$ " + String.format("%.2f", cliente.getSaldo())); // Será atualizado
            formPanel.add(currentBalanceLabel);

            addBalanceButton = new JButton("Adicionar Saldo");
            addBalanceButton.setPreferredSize(new Dimension(200, 50)); // Tamanho ajustado para o texto
            addBalanceButton.setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 16f)); // Fonte ajustada
            addBalanceButton.setBackground(new Color(0, 150, 0)); // Cor de fundo verde
            addBalanceButton.setForeground(Color.WHITE); // Cor do texto branca
            addBalanceButton.addActionListener(e -> handleAddBalance());
            actionButtonsPanel.add(addBalanceButton); // Adicionado ao mesmo painel dos outros botões
        }

        mainContentPanel.add(actionButtonsPanel);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f));
        label.setForeground(new Color(10, 40, 61));
        return label;
    }

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

    private void displayUserData() {
        // Carrega e exibe a imagem de perfil
        Image profileImage = null;
        String imagePath = loggedInUser.getCaminhoFoto();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File fileImage = new File(imagePath);
                if (fileImage.exists()) {
                    profileImage = ImageIO.read(fileImage);
                } else {
                    URL resourceUrl = getClass().getResource(imagePath);
                    if (resourceUrl != null) {
                        profileImage = ImageIO.read(resourceUrl);
                    } else {
                        System.err.println("Imagem de perfil não encontrada em: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar imagem de perfil: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar imagem de perfil: " + e.getMessage());
            }
        }
        if (profileImage != null) {
            profileImageLabel.setIcon(new ImageIcon(ImageScaler.getScaledImage(profileImage, 350, 350)));
        } else {
            profileImageLabel.setIcon(null);
            profileImageLabel.setText("Sem Foto");
        }

        nameField.setText(loggedInUser.getNome());
        cpfField.setText(loggedInUser.getCpf());
        phoneField.setText(loggedInUser.getTelefone());
        emailField.setText(loggedInUser.getEmail());
        addressStreetField.setText(loggedInUser.getEndereco().getRua());
        addressNumberField.setText(String.valueOf(loggedInUser.getEndereco().getNumero()));
        addressNeighborhoodField.setText(loggedInUser.getEndereco().getBairro());
        addressCityField.setText(loggedInUser.getEndereco().getCidade());
        addressStateField.setText(loggedInUser.getEndereco().getEstado());
        addressCepField.setText(loggedInUser.getEndereco().getCep());
        birthDateField.setText(loggedInUser.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        genderComboBox.setSelectedItem(loggedInUser.getSexo());

        if (loggedInUser instanceof Cliente cliente) {
            currentBalanceLabel.setText("R$ " + String.format("%.2f", cliente.getSaldo()));
        }
    }

    private void setEditMode(boolean enable) {
        nameField.setEditable(enable);
        phoneField.setEditable(enable);
        emailField.setEditable(enable);
        addressStreetField.setEditable(enable);
        addressNumberField.setEditable(enable);
        addressNeighborhoodField.setEditable(enable);
        addressCityField.setEditable(enable);
        addressStateField.setEditable(enable);
        addressCepField.setEditable(enable);
        birthDateField.setEditable(enable);
        genderComboBox.setEnabled(enable);

        changePhotoButton.setVisible(enable); // Botão para trocar foto
        editButton.setVisible(!enable); // Esconde Editar no modo edição
        saveButton.setVisible(enable); // Mostra Salvar
        cancelEditButton.setVisible(enable); // Mostra Cancelar

        if (loggedInUser instanceof Cliente) {
            addBalanceButton.setVisible(!enable); // Esconde Adicionar Saldo no modo edição, se preferir
            // ou mantém visível se a edição de saldo for separada
        }
    }

    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione sua Nova Foto de Perfil");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToLoad = fileChooser.getSelectedFile();
            try {
                Image originalImage = ImageIO.read(fileToLoad);
                profileImageLabel.setIcon(new ImageIcon(ImageScaler.getScaledImage(originalImage, 150, 150)));
                initialProfileImagePath = fileToLoad.getAbsolutePath(); // Salva o novo caminho temporariamente
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                initialProfileImagePath = null;
                profileImageLabel.setIcon(null);
            }
        }
    }

    private void handleSaveChanges() {
        // Validações e salvamento dos dados do perfil
        // TODO: Implementar validações (telefone, email, cpf, etc.)
        String newName = nameField.getText();
        String newPhone = phoneField.getText();
        String newEmail = emailField.getText();
        String newStreet = addressStreetField.getText();
        int newNumber;
        try {
            newNumber = Integer.parseInt(addressNumberField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número do endereço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String newNeighborhood = addressNeighborhoodField.getText();
        String newCity = addressCityField.getText();
        String newState = addressStateField.getText();
        String newCep = addressCepField.getText();
        LocalDate newBirthDate;
        try {
            newBirthDate = LocalDate.parse(birthDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de Nascimento inválida. Use dd/mm/aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Sexo newGender = (Sexo) genderComboBox.getSelectedItem();

        // Atualiza os dados no objeto loggedInUser
        loggedInUser.setNome(newName);
        loggedInUser.setTelefone(newPhone);
        loggedInUser.setEmail(newEmail);
        loggedInUser.getEndereco().setRua(newStreet);
        loggedInUser.getEndereco().setNumero(newNumber);
        loggedInUser.getEndereco().setBairro(newNeighborhood);
        loggedInUser.getEndereco().setCidade(newCity);
        loggedInUser.getEndereco().setEstado(newState);
        loggedInUser.getEndereco().setCep(newCep);
        loggedInUser.setDataNascimento(newBirthDate.atStartOfDay());
        loggedInUser.setSexo(newGender);

        // Se uma nova foto foi selecionada, salve-a e atualize o caminho
        if (initialProfileImagePath != null && !initialProfileImagePath.isEmpty()) {
            String savedPhotoPath = authController.saveProfilePicture(initialProfileImagePath, loggedInUser.getCpf());
            if (savedPhotoPath != null) {
                loggedInUser.setCaminhoFoto(savedPhotoPath);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar nova foto de perfil.", "Erro", JOptionPane.ERROR_MESSAGE);
                // Pode não querer retornar aqui, dependendo da criticidade da foto
            }
        }

        // Salvar as alterações (serializar o objeto Cliente/Funcionario atualizado)
        boolean success;
        if (loggedInUser instanceof Cliente) {
            List<Cliente> clients = authController.loadClients();
            // Encontra o cliente na lista e o substitui pelo objeto atualizado
            clients.removeIf(c -> c.getCpf().equals(loggedInUser.getCpf()));
            clients.add((Cliente) loggedInUser);
            success = authController.saveClients(clients);
        } else if (loggedInUser instanceof Funcionario) {
            List<Funcionario> employees = authController.loadEmployees();
            employees.removeIf(f -> f.getCpf().equals(loggedInUser.getCpf()));
            employees.add((Funcionario) loggedInUser);
            success = authController.saveEmployees(employees); // NOVO MÉTODO no AuthController
        } else {
            success = false; // Tipo de usuário desconhecido
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Perfil atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            setEditMode(false); // Volta ao modo de visualização
            // Re-exibir dados para refletir as mudanças (incluindo a foto, se for o caso)
            displayUserData();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao salvar alterações.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddBalance() {
        if (!(loggedInUser instanceof Cliente cliente)) {
            JOptionPane.showMessageDialog(this, "Apenas clientes podem adicionar saldo.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Quanto você deseja adicionar ao saldo?", "Adicionar Saldo", JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr.replace(",", "."));
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser positivo.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ImageIcon qrCodeIcon = null;
            try {
                URL imageUrl = getClass().getResource("/images/icons/qr-code.jpg");
                if (imageUrl != null) {
                    Image originalImage = ImageIO.read(imageUrl);
                    Image scaledImage = ImageScaler.getScaledImage(originalImage, 200, 200);
                    qrCodeIcon = new ImageIcon(scaledImage);
                } else {
                    System.err.println("Imagem do QR Code estático não encontrada em: /images/icons/qr-code.jpg");
                    qrCodeIcon = new ImageIcon(new byte[0]);
                }
            } catch (IOException e) {
                System.err.println("Erro de I/O ao carregar QR Code estático: " + e.getMessage());
                qrCodeIcon = new ImageIcon(new byte[0]);
            } catch (Exception e) {
                System.err.println("Erro inesperado ao carregar/escalar QR Code estático: " + e.getMessage());
                qrCodeIcon = new ImageIcon(new byte[0]);
            }

            JLabel qrCodeLabel = new JLabel();
            if (qrCodeIcon != null && qrCodeIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                qrCodeLabel.setIcon(qrCodeIcon);
                qrCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                qrCodeLabel.setVerticalAlignment(SwingConstants.CENTER);
            } else {
                qrCodeLabel.setText("Imagem do QR Code indisponível");
                qrCodeLabel.setForeground(Color.RED);
            }

            JPanel pixPanel = new JPanel(new BorderLayout(10, 10));
            pixPanel.setBackground(UIManager.getColor("OptionPane.background"));
            pixPanel.add(new JLabel("<html><p style='text-align: center;'>Escaneie o QR Code para pagar <b>R$ " + String.format("%.2f", amount) + "</b></p></html>"), BorderLayout.NORTH);
            pixPanel.add(qrCodeLabel, BorderLayout.CENTER);
            pixPanel.add(new JLabel("<html><p style='text-align: center;'>Aguarde alguns segundos pela confirmação...</p></html>"), BorderLayout.SOUTH);
            // --- FIM DA MODIFICAÇÃO ---

            JOptionPane.showMessageDialog(this,
                    pixPanel,
                    "Pagamento PIX",
                    JOptionPane.INFORMATION_MESSAGE);
            // Simular atraso do pagamento
            Timer timer = new Timer(3000, new ActionListener() { // 3 segundos
                @Override
                public void actionPerformed(ActionEvent e) {
                    cliente.adicionarSaldo(amount); // Adiciona o saldo
                    // Salvar o cliente atualizado (persiste o novo saldo)
                    List<Cliente> clients = authController.loadClients();
                    clients.removeIf(c -> c.getCpf().equals(cliente.getCpf()));
                    clients.add(cliente);
                    if (authController.saveClients(clients)) {
                        JOptionPane.showMessageDialog(UserProfileScreen.this, "Saldo adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        currentBalanceLabel.setText("R$ " + String.format("%.2f", cliente.getSaldo())); // Atualiza a exibição
                    } else {
                        JOptionPane.showMessageDialog(UserProfileScreen.this, "Erro ao salvar saldo atualizado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                    ((Timer) e.getSource()).stop(); // Para o timer
                }
            });
            timer.setRepeats(false); // Executa apenas uma vez
            timer.start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Digite um número.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}