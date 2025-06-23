package controller;

import controller.AuthController;
import entities.Cliente;
import entities.Endereco;
import entities.Funcionario;
import entities.Pessoa;
import enums.Sexo;
import exceptions.AuthControllerException;
import org.junit.jupiter.api.*;
import util.PasswordHasher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    private AuthController authController;



    private static final String CLIENTES_FILE_PATH_ACTUAL = AuthController.CLIENTS_FILE_PATH;
    private static final String EMPLOYEES_FILE_PATH_ACTUAL = AuthController.EMPLOYEES_FILE_PATH;
    private static final String PROFILE_PICS_DIR_ACTUAL = AuthController.PROFILE_PICS_DIR;


    @BeforeAll
    static void setupClass() throws IOException {

        new File("dump/clientes/").mkdirs();
        new File("dump/funcionarios/").mkdirs();
        new File(PROFILE_PICS_DIR_ACTUAL).mkdirs();
        cleanUpAllTestFiles();
    }

    @BeforeEach
    void setUp() {
        authController = new AuthController();
        clearDataFiles();
    }

    @AfterEach
    void tearDown() {
        clearDataFiles();
    }

    @AfterAll
    static void tearDownClass() throws IOException {
        cleanUpAllTestFiles();

        deleteDirectoryRecursive(Path.of("dump/clientes/"));
        deleteDirectoryRecursive(Path.of("dump/funcionarios/"));
        deleteDirectoryRecursive(Path.of(PROFILE_PICS_DIR_ACTUAL));

        try {
            Files.deleteIfExists(Path.of("dump"));
        } catch (java.nio.file.DirectoryNotEmptyException e) {
            System.err.println("Aviso: Diretório 'dump' ainda não está vazio. Deletar manualmente se necessário.");
        }
    }

    private static void cleanUpAllTestFiles() throws IOException {
        Files.deleteIfExists(Path.of(CLIENTES_FILE_PATH_ACTUAL));
        Files.deleteIfExists(Path.of(EMPLOYEES_FILE_PATH_ACTUAL));
        File testPicsDir = new File(PROFILE_PICS_DIR_ACTUAL);
        if (testPicsDir.exists()) {
            for (File file : testPicsDir.listFiles()) {
                Files.deleteIfExists(file.toPath());
            }
        }
    }

    private void clearDataFiles() {
        new File(CLIENTES_FILE_PATH_ACTUAL).delete();
        new File(EMPLOYEES_FILE_PATH_ACTUAL).delete();

    }

    private static void deleteDirectoryRecursive(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("Falha ao deletar: " + p + " - " + e.getMessage());
                        }
                    });
        }
    }

    private Endereco createTestEndereco() {
        return new Endereco("Cidade Teste", "Estado Teste", "Bairro Teste", "Rua Teste", 123, "12345-678");
    }

    private Cliente createTestClient(String email, String cpf, String password) {
        return new Cliente(
                "Teste Cliente",
                cpf,
                "9999-8888",
                email,
                PasswordHasher.hashPassword(password),
                createTestEndereco(),
                LocalDateTime.of(1990, 1, 1, 0, 0),
                Sexo.FEMININO,
                null
        );
    }

    private Funcionario createTestFuncionario(String email, String cpf, String password) {
        return new Funcionario(
                "Teste Funcionario",
                cpf,
                "7777-6666",
                email,
                PasswordHasher.hashPassword(password),
                createTestEndereco(),
                LocalDateTime.of(1985, 5, 10, 0, 0),
                Sexo.MASCULINO,
                null
        );
    }

    @Test
    @Order(1)
    @DisplayName("Teste de autenticação com cliente válido")
    void testAuthenticateValidClient() {
        String email = "cliente_autenticacao@test.com";
        String password = "senha123";
        Cliente cliente = createTestClient(email, "111.111.111-11", password);
        authController.saveClients(List.of(cliente));

        Pessoa authenticatedPerson = authController.authenticate(email, password);

        assertNotNull(authenticatedPerson, "Deveria autenticar o cliente válido");
        assertTrue(authenticatedPerson instanceof Cliente, "Deveria retornar um objeto Cliente");
        assertEquals(email, authenticatedPerson.getEmail(), "O email autenticado deve corresponder");
    }

    @Test
    @Order(2)
    @DisplayName("Teste de autenticação com funcionário válido")
    void testAuthenticateValidEmployee() {
        String email = "funcionario_autenticacao@test.com";
        String password = "admin123";
        Funcionario funcionario = createTestFuncionario(email, "000.111.222-33", password);
        authController.saveEmployees(List.of(funcionario));

        Pessoa authenticatedPerson = authController.authenticate(email, password);

        assertNotNull(authenticatedPerson, "Deveria autenticar o funcionário válido");
        assertTrue(authenticatedPerson instanceof Funcionario, "Deveria retornar um objeto Funcionario");
        assertEquals(email, authenticatedPerson.getEmail(), "O email autenticado deve corresponder");
    }

    @Test
    @Order(3)
    @DisplayName("Teste de autenticação com credenciais inválidas - Lanca AuthControllerException")
    void testAuthenticateInvalidCredentialsThrowsException() {
        authController.saveClients(List.of(createTestClient("valido@test.com", "111.111.111-12", "senhaValida")));
        authController.saveEmployees(List.of(createTestFuncionario("valido_func@test.com", "222.222.222-33", "senhaValidaFunc")));
        
        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.authenticate("inexistente@test.com", "senhaInvalida"),
                "Deveria lançar AuthControllerException para credenciais inválidas"
        );
        assertEquals("Email ou senha incorretos.", thrown.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Teste de autenticação com senha incorreta - Lanca AuthControllerException")
    void testAuthenticateIncorrectPasswordThrowsException() {
        String email = "cliente2@test.com";
        String correctPassword = "senhaCorreta";
        Cliente cliente = createTestClient(email, "987.654.321-00", correctPassword);
        authController.saveClients(List.of(cliente));

        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.authenticate(email, "senhaErrada"),
                "Deveria lançar AuthControllerException para senha incorreta"
        );
        assertEquals("Email ou senha incorretos.", thrown.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Teste de autenticação com email incorreto - Lanca AuthControllerException")
    void testAuthenticateIncorrectEmailThrowsException() {
        String email = "cliente3@test.com";
        String password = "senha123";
        Cliente cliente = createTestClient(email, "123.000.456-00", password);
        authController.saveClients(List.of(cliente));

        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.authenticate("emailIncorreto@test.com", password),
                "Deveria lançar AuthControllerException para email incorreto"
        );
        assertEquals("Email ou senha incorretos.", thrown.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Teste de registro de cliente com email já existente - Lanca AuthControllerException")
    void testRegisterClientExistingEmailThrowsException() {
        String cpf = "222.333.444-55";
        String email = "existente@example.com";
        Cliente existingClient = createTestClient(email, cpf, "senhaExiste");
        authController.saveClients(List.of(existingClient));

        Cliente newClientAttempt = createTestClient(email, "333.444.555-66", "outraSenha");

        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.registerClient(newClientAttempt),
                "Deveria lançar AuthControllerException para email já existente"
        );
        assertEquals("Email ou CPF já cadastrado.", thrown.getMessage());
        assertEquals(1, authController.loadClients().size(), "A quantidade de clientes não deve mudar");
    }

    @Test
    @Order(8)
    @DisplayName("Teste de registro de cliente com CPF já existente - Lanca AuthControllerException")
    void testRegisterClientExistingCpfThrowsException() {
        String cpf = "444.555.666-77";
        String email = "email_novo_test_reg@example.com";
        Cliente existingClient = createTestClient("email_existente_reg@example.com", cpf, "senhaExiste");
        authController.saveClients(List.of(existingClient));

        Cliente newClientAttempt = createTestClient(email, cpf, "outraSenha");

        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.registerClient(newClientAttempt),
                "Deveria lançar AuthControllerException para CPF já existente"
        );
        assertEquals("Email ou CPF já cadastrado.", thrown.getMessage());
        assertEquals(1, authController.loadClients().size(), "A quantidade de clientes não deve mudar");
    }

    @Test
    @Order(8)
    @DisplayName("Teste de registro de cliente com erro de hash de senha - Lanca AuthControllerException")
    void testRegisterClientPasswordHashErrorThrowsException() {
        String uniqueCpf = "111.111.111-99";
        String uniqueEmail = "reg_hash_error@test.com";
        Cliente newClient = createTestClient(uniqueEmail, uniqueCpf, "senhaQualquer");














    }

    @Test
    @Order(9)
    @DisplayName("Teste de atualização de cliente existente")
    void testUpdateClienteSuccess() {
        String cpf = "999.888.777-66";
        Cliente originalClient = createTestClient("original@test.com", cpf, "senhaOriginal");
        authController.saveClients(List.of(originalClient));

        Cliente updatedClient = createTestClient("atualizado@test.com", cpf, "senhaAtualizada");
        updatedClient.setNome("Cliente Atualizado");

        boolean updated = authController.updateCliente(updatedClient);
        assertTrue(updated, "Deveria atualizar o cliente com sucesso");

        List<Cliente> clients = authController.loadClients();
        assertEquals(1, clients.size(), "Deve haver apenas um cliente na lista após a atualização");
        assertEquals("Cliente Atualizado", clients.get(0).getNome(), "O nome do cliente deve ser atualizado");
        assertEquals("atualizado@test.com", clients.get(0).getEmail(), "O email do cliente deve ser atualizado");
        assertNotEquals(PasswordHasher.hashPassword("senhaOriginal"), clients.get(0).getSenha(), "A senha deve ter sido atualizada e hashed");
    }

    @Test
    @Order(10)
    @DisplayName("Teste de atualização de cliente inexistente - Lanca AuthControllerException")
    void testUpdateClienteNotFoundThrowsException() {
        String cpf = "111.000.222-33";
        Cliente nonExistentClient = createTestClient("inexistente@test.com", cpf, "senhaQualquer");

        authController.saveClients(new ArrayList<>());

        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.updateCliente(nonExistentClient),
                "Deveria lançar AuthControllerException para cliente inexistente"
        );
        assertEquals("Erro: Cliente com CPF '" + cpf + "' não encontrado para atualização.", thrown.getMessage());
        assertEquals(0, authController.loadClients().size(), "A lista de clientes deve permanecer vazia");
    }

    @Test
    @Order(11)
    @DisplayName("Teste de salvamento e carregamento de clientes")
    void testSaveAndLoadClients() {
        List<Cliente> initialClients = new ArrayList<>();
        initialClients.add(createTestClient("c1@test.com", "111.111.111-11", "p1"));
        initialClients.add(createTestClient("c2@test.com", "222.222.222-22", "p2"));

        assertTrue(authController.saveClients(initialClients), "Deveria salvar clientes com sucesso");

        List<Cliente> loadedClients = authController.loadClients();
        assertNotNull(loadedClients, "A lista de clientes carregada não deve ser nula");
        assertEquals(initialClients.size(), loadedClients.size(), "O número de clientes carregados deve corresponder");

        loadedClients.sort(Comparator.comparing(Pessoa::getCpf));
        initialClients.sort(Comparator.comparing(Pessoa::getCpf));

        for (int i = 0; i < initialClients.size(); i++) {
            assertEquals(initialClients.get(i).getCpf(), loadedClients.get(i).getCpf());
            assertEquals(initialClients.get(i).getEmail(), loadedClients.get(i).getEmail());
            assertNotNull(loadedClients.get(i).getSenha());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Teste de carregamento de clientes com arquivo vazio")
    void testLoadClientsEmptyFile() throws IOException {
        new File(CLIENTES_FILE_PATH_ACTUAL).createNewFile();
        List<Cliente> clients = authController.loadClients();
        assertNotNull(clients);
        assertTrue(clients.isEmpty(), "Deveria retornar uma lista vazia se o arquivo estiver vazio");
    }

    @Test
    @Order(13)
    @DisplayName("Teste de carregamento de clientes com arquivo inexistente")
    void testLoadClientsNonExistentFile() {
        new File(CLIENTES_FILE_PATH_ACTUAL).delete();
        List<Cliente> clients = authController.loadClients();
        assertNotNull(clients);
        assertTrue(clients.isEmpty(), "Deveria retornar uma lista vazia se o arquivo não existir");
    }

    @Test
    @Order(14)
    @DisplayName("Teste de salvamento de foto de perfil com sucesso")
    void testSaveProfilePictureSuccess() throws IOException {
        String cpf = "123.456.789-99";
        String originalTempPath = createTempPhotoFile("temp_photo.png");

        String savedPath = authController.saveProfilePicture(originalTempPath, cpf);
        assertNotNull(savedPath, "O caminho da foto salva não deve ser nulo");
        assertTrue(new File(savedPath).exists(), "A foto deve existir no novo caminho");
        assertTrue(savedPath.contains(cpf + ".png"), "O nome do arquivo salvo deve conter o CPF");
    }

    @Test
    @Order(15)
    @DisplayName("Teste de salvamento de foto de perfil com caminho nulo - Lanca AuthControllerException")
    void testSaveProfilePictureNullPathThrowsException() {
        String cpf = "111.222.333-00";
        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.saveProfilePicture(null, cpf),
                "Deveria lançar AuthControllerException para caminho de foto nulo"
        );
        assertEquals("Caminho de foto inválido: null", thrown.getMessage());
    }

    @Test
    @Order(16)
    @DisplayName("Teste de salvamento de foto de perfil com caminho vazio - Lanca AuthControllerException")
    void testSaveProfilePictureEmptyPathThrowsException() {
        String cpf = "111.222.333-00";
        AuthControllerException thrown = assertThrows(
                AuthControllerException.class,
                () -> authController.saveProfilePicture("", cpf),
                "Deveria lançar AuthControllerException para caminho de foto vazio"
        );
        assertEquals("Caminho de foto inválido: ", thrown.getMessage());
    }

    @Test
    @Order(17)
    @DisplayName("Teste de salvamento de foto de perfil com arquivo original inexistente - Nao lanca AuthControllerException (trata IOException)")
    void testSaveProfilePictureNonExistentOriginalFileReturnsNull() {



        String savedPath = authController.saveProfilePicture("caminho/inexistente/foto.jpg", "111.222.333-00");
        assertNull(savedPath, "Deveria retornar null para arquivo original inexistente");
    }

    private String createTempPhotoFile(String fileName) {
        try {
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "test_images_" + UUID.randomUUID());
            Files.createDirectories(tempDir);
            Path tempFilePath = Files.createTempFile(tempDir, fileName.split("\\.")[0], "." + fileName.split("\\.")[1]);
            Files.write(tempFilePath, new byte[]{1, 2, 3, 4, 5});
            return tempFilePath.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Falha ao criar arquivo temporário para teste de foto: " + e.getMessage());
            return null;
        }
    }
}