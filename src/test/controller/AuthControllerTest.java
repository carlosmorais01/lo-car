package test.controller;

import controller.AuthController;
import entities.Cliente;
import entities.Endereco;
import entities.Funcionario;
import entities.Pessoa;
import enums.Sexo; // Importar o enum Sexo
import org.junit.jupiter.api.*;
import util.PasswordHasher;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime; // Usar LocalDateTime
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID; // Para gerar CPFs e emails únicos

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test; // Esta é a importação que está faltando para a anotação @Test
import org.junit.jupiter.api.TestMethodOrder;

// Garante a ordem de execução dos testes. Útil para testes que dependem de um estado anterior
// mas em um projeto real, testes devem ser o mais independentes possível.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    private AuthController authController;
    // Note: Mantive os caminhos originais do AuthController para simplicidade neste exemplo.
    // Em um cenário real, você injetaria caminhos temporários na AuthController para isolamento.

    @BeforeAll // Executado uma vez antes de todos os testes desta classe
    static void setupClass() throws IOException {
        // Garante que os diretórios de dump existam antes de qualquer teste
        new File("dump/clientes/").mkdirs();
        new File("dump/funcionarios/").mkdirs();
        new File("dump/profile_pics/").mkdirs(); // Garante o diretório de fotos

        // Limpa quaisquer arquivos de teste residuais de execuções anteriores,
        // para garantir um estado limpo no início da suíte de testes.
        cleanUpAllTestFiles();
    }

    @BeforeEach // Executado antes de cada método de teste
    void setUp() {
        // Instancia um novo AuthController para cada teste para garantir isolamento
        authController = new AuthController();
        // Limpa os arquivos de dados ANTES de cada teste para garantir um estado limpo
        clearDataFiles();
    }

    @AfterEach // Executado depois de cada método de teste
    void tearDown() {
        // Limpa os arquivos de dados DEPOIS de cada teste
        clearDataFiles();
    }

    @AfterAll // Executado uma vez depois de todos os testes desta classe
    static void tearDownClass() throws IOException {
        // Limpa todos os diretórios e arquivos criados pelos testes
        cleanUpAllTestFiles();
        // Agora, tente deletar o diretório 'dump' se estiver vazio
        // deleteDirectoryRecursive(Path.of("dump")); // <-- Comentei esta linha
        // Vamos ser mais específicos e deletar os subdiretórios que criamos/usamos
        deleteDirectoryRecursive(Path.of("dump/clientes/"));
        deleteDirectoryRecursive(Path.of("dump/funcionarios/"));
        deleteDirectoryRecursive(Path.of("dump/profile_pics/"));

        // E só delete o diretório 'dump' se ele estiver VAZIO
        // Isso previne o DirectoryNotEmptyException se algo mais foi criado lá
        try {
            Files.deleteIfExists(Path.of("dump"));
        } catch (DirectoryNotEmptyException e) {
            System.err.println("Aviso: Diretório 'dump' ainda não está vazio. Deletar manualmente se necessário.");
            // Não falha o teste por isso, apenas avisa.
        }
    }

    // --- Métodos Auxiliares para Limpeza ---
    private static void cleanUpAllTestFiles() throws IOException {
        // Deleta os arquivos .dat diretamente
        Files.deleteIfExists(Path.of(AuthController.CLIENTS_FILE_PATH));
        Files.deleteIfExists(Path.of(AuthController.EMPLOYEES_FILE_PATH));

        // Deleta o conteúdo do diretório de fotos recursivamente
        deleteDirectoryRecursive(Path.of(AuthController.PROFILE_PICS_DIR));
    }

    private void clearDataFiles() {
        // No @BeforeEach e @AfterEach, apenas deletamos os arquivos .dat
        // A limpeza de fotos é mais intensiva e está nos @BeforeAll / @AfterAll
        new File(AuthController.CLIENTS_FILE_PATH).delete();
        new File(AuthController.EMPLOYEES_FILE_PATH).delete();
    }

    // --- Métodos Auxiliares para Criar Entidades de Teste ---
    private Endereco createTestEndereco() {
        return new Endereco("Cidade Teste", "Estado Teste", "Bairro Teste", "Rua Teste", 123, "12345-678");
    }

    private Cliente createTestClient(String email, String cpf, String password, String photoPath) {
        return new Cliente(
                "Teste Cliente",
                cpf,
                "9999-8888",
                email,
                password, // A senha será hashed dentro do registerClient ou para autenticação
                createTestEndereco(),
                LocalDateTime.of(1990, 1, 1, 0, 0),
                Sexo.FEMININO, // Ou Sexo.MASCULINO, dependendo do que você quer testar
                photoPath
        );
    }

    private Funcionario createTestFuncionario(String email, String cpf, String password) {
        return new Funcionario(
                "Teste Funcionario",
                cpf,
                "7777-6666",
                email,
                password, // A senha será hashed dentro da autenticação
                createTestEndereco(),
                LocalDateTime.of(1985, 5, 10, 0, 0),
                Sexo.MASCULINO,
                null // Funcionário pode não ter foto de perfil para este teste
        );
    }

    // --- Testes para Autenticação ---
    @Test
    @Order(1)
    @DisplayName("Teste de autenticação com cliente válido")
    void testAuthenticateValidClient() {
        String email = "cliente_autenticacao@test.com";
        String password = "senha123";
        // Cria o cliente com a senha já hashed, como seria após um registro
        Cliente cliente = createTestClient(email, "111.111.111-11", PasswordHasher.hashPassword(password), null);

        List<Cliente> clients = new ArrayList<>();
        clients.add(cliente);
        authController.saveClients(clients);

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
        // Cria o funcionário com a senha já hashed
        Funcionario funcionario = createTestFuncionario(email, "000.111.222-33", PasswordHasher.hashPassword(password));

        List<Funcionario> employees = new ArrayList<>();
        employees.add(funcionario);
        authController.saveEmployees(employees);

        Pessoa authenticatedPerson = authController.authenticate(email, password);

        assertNotNull(authenticatedPerson, "Deveria autenticar o funcionário válido");
        assertTrue(authenticatedPerson instanceof Funcionario, "Deveria retornar um objeto Funcionario");
        assertEquals(email, authenticatedPerson.getEmail(), "O email autenticado deve corresponder");
    }

    @Test
    @Order(3)
    @DisplayName("Teste de autenticação com credenciais inválidas")
    void testAuthenticateInvalidCredentials() {
        // Salva um cliente e um funcionário válidos para garantir que o sistema não confunda
        authController.saveClients(List.of(createTestClient("valido@test.com", "111.111.111-12", PasswordHasher.hashPassword("senhaValida"), null)));
        authController.saveEmployees(List.of(createTestFuncionario("valido_func@test.com", "222.222.222-33", PasswordHasher.hashPassword("senhaValidaFunc"))));

        Pessoa authenticatedPerson = authController.authenticate("inexistente@test.com", "senhaInvalida");
        assertNull(authenticatedPerson, "Não deveria autenticar com credenciais inválidas");
    }

    @Test
    @Order(4)
    @DisplayName("Teste de autenticação com senha incorreta")
    void testAuthenticateIncorrectPassword() {
        String email = "cliente2@test.com";
        String correctPassword = "senhaCorreta";
        Cliente cliente = createTestClient(email, "987.654.321-00", PasswordHasher.hashPassword(correctPassword), null);
        authController.saveClients(List.of(cliente));

        Pessoa authenticatedPerson = authController.authenticate(email, "senhaErrada");
        assertNull(authenticatedPerson, "Não deveria autenticar com senha incorreta");
    }

    @Test
    @Order(5)
    @DisplayName("Teste de autenticação com email incorreto")
    void testAuthenticateIncorrectEmail() {
        String email = "cliente3@test.com";
        String password = "senha123";
        Cliente cliente = createTestClient(email, "123.000.456-00", PasswordHasher.hashPassword(password), null);
        authController.saveClients(List.of(cliente));

        Pessoa authenticatedPerson = authController.authenticate("emailIncorreto@test.com", password);
        assertNull(authenticatedPerson, "Não deveria autenticar com email incorreto");
    }

    // --- Testes para Registro de Cliente ---
    @Test
    @Order(6)
    @DisplayName("Teste de registro de novo cliente com sucesso")
    void testRegisterNewClientSuccess() {
        String uniqueCpf = "111.222.333-44";
        String uniqueEmail = "novo.cliente@example.com";
        // Cria um arquivo temporário para a foto de perfil simulada
        String tempPhotoPath = createTempPhotoFile(uniqueCpf + ".png");

        Cliente newClient = createTestClient(uniqueEmail, uniqueCpf, "senhaNova", tempPhotoPath);

        boolean registered = authController.registerClient(newClient);
        assertTrue(registered, "Deveria registrar o novo cliente com sucesso");

        // Verifica se o cliente foi realmente salvo e pode ser carregado
        List<Cliente> clients = authController.loadClients();
        assertEquals(1, clients.size());
        assertEquals(uniqueEmail, clients.get(0).getEmail());
        assertNotNull(clients.get(0).getSenha(), "A senha deve ter sido hashed");
        assertNotEquals("senhaNova", clients.get(0).getSenha(), "A senha original não deve estar salva");


        // Testar autenticação do cliente recém-registrado
        Pessoa authenticated = authController.authenticate(uniqueEmail, "senhaNova");
        assertNotNull(authenticated, "O cliente recém-registrado deve ser autenticável");
    }

    @Test
    @Order(7)
    @DisplayName("Teste de registro de cliente com email já existente")
    void testRegisterClientExistingEmail() {
        String cpf = "222.333.444-55";
        String email = "existente@example.com";
        Cliente existingClient = createTestClient(email, cpf, PasswordHasher.hashPassword("senhaExiste"), null);
        authController.saveClients(List.of(existingClient)); // Salva um cliente existente

        Cliente newClientAttempt = createTestClient(email, "333.444.555-66", "outraSenha", null);
        boolean registered = authController.registerClient(newClientAttempt);

        assertFalse(registered, "Não deveria registrar cliente com email já existente");
        assertEquals(1, authController.loadClients().size(), "A quantidade de clientes não deve mudar");
    }

    @Test
    @Order(8)
    @DisplayName("Teste de registro de cliente com CPF já existente")
    void testRegisterClientExistingCpf() {
        String cpf = "444.555.666-77";
        String email = "email_novo_teste@example.com";
        Cliente existingClient = createTestClient("email_existente@example.com", cpf, PasswordHasher.hashPassword("senhaExiste"), null);
        authController.saveClients(List.of(existingClient)); // Salva um cliente existente

        Cliente newClientAttempt = createTestClient(email, cpf, "outraSenha", null);
        boolean registered = authController.registerClient(newClientAttempt);

        assertFalse(registered, "Não deveria registrar cliente com CPF já existente");
        assertEquals(1, authController.loadClients().size(), "A quantidade de clientes não deve mudar");
    }

    // --- Testes para Update de Cliente ---
    @Test
    @Order(9)
    @DisplayName("Teste de atualização de cliente existente")
    void testUpdateClienteSuccess() {
        String cpf = "999.888.777-66";
        Cliente originalClient = createTestClient("original@test.com", cpf, PasswordHasher.hashPassword("senhaOriginal"), null);
        authController.saveClients(List.of(originalClient));

        // Crie um novo cliente com o mesmo CPF, mas dados atualizados
        Cliente updatedClient = createTestClient("atualizado@test.com", cpf, PasswordHasher.hashPassword("senhaAtualizada"), null);
        updatedClient.setNome("Cliente Atualizado"); // Altera um atributo para verificar a atualização

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
    @DisplayName("Teste de atualização de cliente inexistente")
    void testUpdateClienteNotFound() {
        String cpf = "111.000.222-33";
        Cliente nonExistentClient = createTestClient("inexistente@test.com", cpf, "senhaQualquer", null);

        // Garante que não há clientes salvos
        authController.saveClients(new ArrayList<>());

        boolean updated = authController.updateCliente(nonExistentClient);
        assertFalse(updated, "Não deveria atualizar um cliente que não existe");
        assertEquals(0, authController.loadClients().size(), "A lista de clientes deve permanecer vazia");
    }

    // --- Testes para Carregamento e Salvamento ---
    @Test
    @Order(11)
    @DisplayName("Teste de salvamento e carregamento de clientes")
    void testSaveAndLoadClients() {
        List<Cliente> initialClients = new ArrayList<>();
        initialClients.add(createTestClient("c1@test.com", "111.111.111-11", PasswordHasher.hashPassword("p1"), null));
        initialClients.add(createTestClient("c2@test.com", "222.222.222-22", PasswordHasher.hashPassword("p2"), null));

        assertTrue(authController.saveClients(initialClients), "Deveria salvar clientes com sucesso");

        List<Cliente> loadedClients = authController.loadClients();
        assertNotNull(loadedClients, "A lista de clientes carregada não deve ser nula");
        assertEquals(initialClients.size(), loadedClients.size(), "O número de clientes carregados deve corresponder");

        // Verifica se os conteúdos são os mesmos (ordenando para garantir a comparação)
        loadedClients.sort(Comparator.comparing(Pessoa::getCpf));
        initialClients.sort(Comparator.comparing(Pessoa::getCpf));

        for (int i = 0; i < initialClients.size(); i++) {
            assertEquals(initialClients.get(i).getCpf(), loadedClients.get(i).getCpf());
            assertEquals(initialClients.get(i).getEmail(), loadedClients.get(i).getEmail());
            // Não compare a senha hashed diretamente, mas sim que não é null
            assertNotNull(loadedClients.get(i).getSenha());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Teste de carregamento de clientes com arquivo vazio")
    void testLoadClientsEmptyFile() throws IOException {
        // Cria um arquivo vazio para simular um arquivo existente mas sem conteúdo
        new File(AuthController.CLIENTS_FILE_PATH).createNewFile();
        List<Cliente> clients = authController.loadClients();
        assertNotNull(clients);
        assertTrue(clients.isEmpty(), "Deveria retornar uma lista vazia se o arquivo estiver vazio");
    }

    @Test
    @Order(13)
    @DisplayName("Teste de carregamento de clientes com arquivo inexistente")
    void testLoadClientsNonExistentFile() {
        // Garante que o arquivo não existe
        new File(AuthController.CLIENTS_FILE_PATH).delete();
        List<Cliente> clients = authController.loadClients();
        assertNotNull(clients);
        assertTrue(clients.isEmpty(), "Deveria retornar uma lista vazia se o arquivo não existir");
    }

    // --- Testes para saveProfilePicture ---
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
    @DisplayName("Teste de salvamento de foto de perfil com caminho nulo")
    void testSaveProfilePictureNullPath() {
        String savedPath = authController.saveProfilePicture(null, "111.222.333-00");
        assertNull(savedPath, "Deveria retornar null para caminho de foto nulo");
    }

    @Test
    @Order(16)
    @DisplayName("Teste de salvamento de foto de perfil com caminho vazio")
    void testSaveProfilePictureEmptyPath() {
        String savedPath = authController.saveProfilePicture("", "111.222.333-00");
        assertNull(savedPath, "Deveria retornar null para caminho de foto vazio");
    }

    @Test
    @Order(17)
    @DisplayName("Teste de salvamento de foto de perfil com arquivo original inexistente")
    void testSaveProfilePictureNonExistentOriginalFile() {
        String savedPath = authController.saveProfilePicture("caminho/inexistente/foto.jpg", "111.222.333-00");
        assertNull(savedPath, "Deveria retornar null para arquivo original inexistente");
    }

    // --- Métodos Auxiliares para Testes de Arquivo ---
    private String createTempPhotoFile(String fileName) {
        try {
            // Cria um arquivo temporário vazio para simular uma imagem
            // Usa UUID para garantir um nome de diretório temporário único
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "test_images_" + UUID.randomUUID().toString());
            Files.createDirectories(tempDir);
            Path tempFilePath = Files.createTempFile(tempDir, fileName.split("\\.")[0], "." + fileName.split("\\.")[1]);
            // Escreve alguns bytes para simular um arquivo real
            Files.write(tempFilePath, new byte[]{1, 2, 3, 4, 5});
            return tempFilePath.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Falha ao criar arquivo temporário para teste de foto: " + e.getMessage());
            return null;
        }
    }

    // Método auxiliar para deletar diretórios recursivamente
    private static void deleteDirectoryRecursive(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder()) // Deleta o conteúdo antes do diretório
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("Falha ao deletar: " + p + " - " + e.getMessage());
                            // Opcional: relançar ou falhar o teste aqui se a falha for crítica
                        }
                    });
        }
    }
}