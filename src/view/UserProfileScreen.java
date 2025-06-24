package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import entities.Cliente;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A classe `UserProfileScreen` representa a tela de perfil do usuário na aplicação LoCar!.
 * Ela exibe os dados pessoais e de endereço do usuário logado e permite a edição dessas informações,
 * bem como a adição de saldo para clientes.
 */
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
    private JLabel currentBalanceLabel;
    private JButton addBalanceButton;

    private JButton editButton;
    private JButton saveButton;
    private JButton cancelEditButton;

    private String initialProfileImagePath;
    private JButton changePhotoButton;

    /**
     * Construtor para `UserProfileScreen`.
     *
     * @param user O objeto {@link Pessoa} representando o usuário atualmente logado.
     */
    public UserProfileScreen(Pessoa user) {
        this.loggedInUser = user;
        this.authController = new AuthController();
        initializeUI();
        displayUserData();
        setEditMode(false);
    }

    /**
     * Inicializa e configura os componentes da interface do usuário para a tela de perfil.
     * Isso inclui o cabeçalho (`HeaderPanel`), painéis para exibir a imagem de perfil e formulário de dados,
     * e botões de ação para editar, salvar, cancelar e adicionar saldo.
     */
    private void initializeUI() {
        setTitle("LoCar! - Perfil de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
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
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(UIManager.getColor("Panel.background"));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JPanel profileImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        profileImagePanel.setOpaque(false);
        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(350, 350));
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        profileImagePanel.add(profileImageLabel);

        changePhotoButton = new JButton("Trocar Foto");
        changePhotoButton.setFont(UIManager.getFont("Button.font").deriveFont(15f));
        changePhotoButton.setPreferredSize(new Dimension(150, 50));
        changePhotoButton.setVisible(false);
        changePhotoButton.addActionListener(e -> selectProfileImage());
        profileImagePanel.add(changePhotoButton);

        mainContentPanel.add(profileImagePanel);
        mainContentPanel.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        formPanel.add(createLabel("Nome:"));
        nameField = createStyledTextField("");
        formPanel.add(nameField);

        formPanel.add(createLabel("CPF:"));
        cpfField = createStyledTextField("");
        cpfField.setEditable(false);
        formPanel.add(cpfField);

        formPanel.add(createLabel("Telefone:"));
        phoneField = createStyledTextField("");
        formPanel.add(phoneField);

        formPanel.add(createLabel("Email:"));
        emailField = createStyledTextField("");
        formPanel.add(emailField);

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

        formPanel.add(createLabel("Data de Nasc.:"));
        birthDateField = createStyledTextField("");
        formPanel.add(birthDateField);

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

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionButtonsPanel.setOpaque(false);
        actionButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        if (loggedInUser instanceof Cliente cliente) {
            formPanel.add(createLabel("Saldo Atual:"));
            currentBalanceLabel = createLabel("R$ " + String.format("%.2f", cliente.getSaldo()));
            formPanel.add(currentBalanceLabel);

            addBalanceButton = new JButton("Adicionar Saldo");
            addBalanceButton.setPreferredSize(new Dimension(200, 50));
            addBalanceButton.setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 16f));
            addBalanceButton.setBackground(new Color(0, 150, 0));
            addBalanceButton.setForeground(Color.WHITE);
            addBalanceButton.addActionListener(e -> handleAddBalance());
            actionButtonsPanel.add(addBalanceButton);
        }

        mainContentPanel.add(actionButtonsPanel);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Cria um {@link JLabel} com um estilo padronizado para exibir informações de perfil.
     *
     * @param text O texto a ser exibido no label.
     * @return O {@link JLabel} estilizado.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f));
        label.setForeground(new Color(10, 40, 61));
        return label;
    }

    /**
     * Cria um {@link JTextField} com um estilo padronizado para edição de informações de perfil,
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
     * Exibe os dados do usuário logado nos campos da interface.
     * Carrega a imagem de perfil do usuário e preenche todos os campos de texto e o combobox de sexo
     * com as informações correspondentes. Se o usuário for um cliente, também atualiza o saldo atual.
     */
    private void displayUserData() {
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

    /**
     * Define o modo de edição da tela de perfil.
     * Quando {@code enable} é true, os campos se tornam editáveis e os botões de salvar/cancelar/trocar foto são visíveis.
     * Quando {@code enable} é false, os campos são desabilitados para edição e o botão de edição é visível.
     *
     * @param enable true para habilitar o modo de edição, false para desabilitá-lo.
     */
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

        changePhotoButton.setVisible(enable);
        editButton.setVisible(!enable);
        saveButton.setVisible(enable);
        cancelEditButton.setVisible(enable);

        if (loggedInUser instanceof Cliente) {
            addBalanceButton.setVisible(!enable);
        }
    }

    /**
     * Abre um seletor de arquivos para o usuário escolher uma nova imagem de perfil.
     * A imagem selecionada é reescalada, exibida em `profileImageLabel` e seu caminho
     * absoluto é armazenado temporariamente em `initialProfileImagePath`.
     */
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
                initialProfileImagePath = fileToLoad.getAbsolutePath();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                initialProfileImagePath = null;
                profileImageLabel.setIcon(null);
            }
        }
    }

    /**
     * Lida com a ação de salvar as alterações no perfil do usuário.
     * Coleta os dados dos campos, validações básicas (formato de número e data),
     * atualiza o objeto {@link Pessoa} logado e persiste as mudanças através do {@link AuthController}.
     * Se uma nova foto de perfil foi selecionada, ela também é salva.
     */
    private void handleSaveChanges() {
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

        if (initialProfileImagePath != null && !initialProfileImagePath.isEmpty()) {
            String savedPhotoPath = authController.saveProfilePicture(initialProfileImagePath, loggedInUser.getCpf());
            if (savedPhotoPath != null) {
                loggedInUser.setCaminhoFoto(savedPhotoPath);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar nova foto de perfil.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

        boolean success;
        if (loggedInUser instanceof Cliente) {
            List<Cliente> clients = authController.loadClients();
            clients.removeIf(c -> c.getCpf().equals(loggedInUser.getCpf()));
            clients.add((Cliente) loggedInUser);
            success = authController.saveClients(clients);
        } else if (loggedInUser instanceof Funcionario) {
            List<Funcionario> employees = authController.loadEmployees();
            employees.removeIf(f -> f.getCpf().equals(loggedInUser.getCpf()));
            employees.add((Funcionario) loggedInUser);
            success = authController.saveEmployees(employees);
        } else {
            success = false;
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Perfil atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            setEditMode(false);
            displayUserData();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao salvar alterações.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lida com a ação de adicionar saldo à conta de um cliente.
     * Solicita ao usuário o valor a ser adicionado, simula um pagamento PIX com um timer
     * e, após a "confirmação", adiciona o valor ao saldo do cliente e persiste a alteração.
     * Exibe mensagens de sucesso ou erro.
     */
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

            JOptionPane.showMessageDialog(this,
                    pixPanel,
                    "Pagamento PIX",
                    JOptionPane.INFORMATION_MESSAGE);
            Timer timer = new Timer(3000, new ActionListener() {
                /**
                 * Lida com a ação do timer de simulação de pagamento PIX.
                 * Adiciona o valor ao saldo do cliente e persiste o cliente atualizado.
                 * Exibe uma mensagem de sucesso ou erro e para o timer.
                 * @param e O evento de ação do timer.
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    cliente.adicionarSaldo(amount);
                    List<Cliente> clients = authController.loadClients();
                    clients.removeIf(c -> c.getCpf().equals(cliente.getCpf()));
                    clients.add(cliente);
                    if (authController.saveClients(clients)) {
                        JOptionPane.showMessageDialog(UserProfileScreen.this, "Saldo adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        currentBalanceLabel.setText("R$ " + String.format("%.2f", cliente.getSaldo()));
                    } else {
                        JOptionPane.showMessageDialog(UserProfileScreen.this, "Erro ao salvar saldo atualizado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                    ((Timer) e.getSource()).stop();
                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Digite um número.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}