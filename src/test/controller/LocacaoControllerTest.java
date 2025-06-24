package controller;

import entities.Cliente;
import entities.Endereco;
import entities.Locacao;
import entities.TestVehicle;
import entities.Veiculo;
import enums.Sexo;
import exceptions.LocacaoControllerException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import util.PasswordHasher;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LocacaoControllerTest {

    private LocacaoController locacaoController;

    @Mock
    private AuthController authControllerMock;

    @Mock
    private VeiculoController veiculoControllerMock;

    private static final String LOCACOES_FILE_PATH_ACTUAL = "dump/locacoes/locacoes.dat";

    @BeforeAll
    static void setupClass() throws IOException {
        new File("dump/locacoes/").mkdirs();
        cleanUpAllTestFiles();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        locacaoController = spy(new LocacaoController(authControllerMock, veiculoControllerMock));

        doReturn(new ArrayList<>()).when(locacaoController).loadLocacoes();

        clearDataFiles();
    }

    @AfterEach
    void tearDown() {
        clearDataFiles();
    }

    @AfterAll
    static void tearDownClass() throws IOException {
        cleanUpAllTestFiles();
        deleteDirectoryRecursive(Path.of("dump/locacoes/"));
        try {
            Files.deleteIfExists(Path.of("dump/"));
        } catch (java.nio.file.DirectoryNotEmptyException e) {
            System.err.println("Aviso: Diretório 'dump' ainda não está vazio após testes de LocacaoController.");
        }
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

    private static void cleanUpAllTestFiles() throws IOException {
        Files.deleteIfExists(Path.of(LOCACOES_FILE_PATH_ACTUAL));
    }

    private void clearDataFiles() {
        new File(LOCACOES_FILE_PATH_ACTUAL).delete();
    }

    private Endereco createTestEndereco() {
        return new Endereco("Cidade Teste " + UUID.randomUUID(), "Estado Teste", "Bairro Teste", "Rua Teste", 123, "12345-678");
    }

    private Cliente createTestClient(String email, String cpf, String password) {
        return new Cliente(
                "Cliente Teste " + UUID.randomUUID().toString(),
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

    private Veiculo createTestVeiculo(String placa, double valorDiario) {
        return new TestVehicle(placa, valorDiario);
    }
    @Test
    @Order(1)
    @DisplayName("Realizar locacao com sucesso")
    void testRealizarLocacaoSuccess() {
        Cliente cliente = createTestClient("cliente1@test.com", "111.111.111-11", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("ABC-1234", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(false);
        when(authControllerMock.updateCliente(any(Cliente.class))).thenReturn(true);
        when(veiculoControllerMock.atualizarVeiculo(any(Veiculo.class))).thenReturn(true);
        doReturn(true).when(locacaoController).saveAllLocacoes(anyList());

        boolean result = locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal);

        assertTrue(result, "A locação deveria ser realizada com sucesso");
        assertEquals(1, locacaoController.locacoes.size(), "Deveria haver uma locação na lista");
        assertEquals(veiculo.getPlaca(), locacaoController.locacoes.get(0).getVeiculo().getPlaca());
        assertEquals(cliente.getEmail(), locacaoController.locacoes.get(0).getCliente().getEmail());
        assertEquals(1000.0 - valorTotal, cliente.getSaldo(), 0.001, "Saldo do cliente deveria ser debitado");

        verify(veiculoControllerMock).estaLocado(veiculo);
        verify(authControllerMock).updateCliente(cliente);
        verify(veiculoControllerMock).atualizarVeiculo(veiculo);
        verify(locacaoController).saveAllLocacoes(anyList());
    }

    @Test
    @Order(2)
    @DisplayName("Realizar locacao - veiculo ja locado - Lanca LocacaoControllerException")
    void testRealizarLocacaoVeiculoJaLocadoThrowsException() {
        Cliente cliente = createTestClient("cliente2@test.com", "222.222.222-22", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("DEF-5678", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(true);
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal),
                "Deveria lançar LocacaoControllerException se o veículo já estiver locado"
        );
        assertEquals("Veículo já locado.", thrown.getMessage());

        assertTrue(locacaoController.locacoes.isEmpty(), "Nenhuma locação deveria ser adicionada");
        verify(authControllerMock, never()).updateCliente(any(Cliente.class));
        verify(veiculoControllerMock, never()).atualizarVeiculo(any(Veiculo.class));
        verify(locacaoController, never()).saveAllLocacoes(anyList());
    }

    @Test
    @Order(3)
    @DisplayName("Realizar locacao - saldo insuficiente - Lanca LocacaoControllerException")
    void testRealizarLocacaoSaldoInsuficienteThrowsException() {
        Cliente cliente = createTestClient("cliente3@test.com", "333.333.333-33", "senha123");
        Veiculo veiculo = createTestVeiculo("GHI-9012", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(false);
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal),
                "Deveria lançar LocacaoControllerException por saldo insuficiente"
        );
        assertEquals("Saldo insuficiente.", thrown.getMessage());

        assertTrue(locacaoController.locacoes.isEmpty(), "Nenhuma locação deveria ser adicionada");
        verify(authControllerMock, never()).updateCliente(any(Cliente.class));
        verify(veiculoControllerMock, never()).atualizarVeiculo(any(Veiculo.class));
        verify(locacaoController, never()).saveAllLocacoes(anyList());
    }

    @Test
    @Order(4)
    @DisplayName("Realizar locacao - falha ao salvar locacoes - Lanca LocacaoControllerException")
    void testRealizarLocacaoSaveAllLocacoesFailsThrowsException() {
        Cliente cliente = createTestClient("cliente4@test.com", "444.444.444-44", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("JKL-3456", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(false);
        doReturn(false).when(locacaoController).saveAllLocacoes(anyList());
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal),
                "Deveria lançar LocacaoControllerException se o salvamento falhar"
        );
        assertEquals("Falha ao salvar locação.", thrown.getMessage());

        assertTrue(locacaoController.locacoes.isEmpty(), "A locação deveria ser removida da lista após falha no salvamento");
        verify(authControllerMock, never()).updateCliente(any(Cliente.class));
        verify(veiculoControllerMock, never()).atualizarVeiculo(any(Veiculo.class));
        verify(locacaoController, times(1)).saveAllLocacoes(anyList());
    }

    @Test
    @Order(4)
    @DisplayName("Realizar locacao - falha ao atualizar cliente - Lanca LocacaoControllerException")
    void testRealizarLocacaoUpdateClientFailsThrowsException() {
        Cliente cliente = createTestClient("cliente_fail_update_c@test.com", "555.555.555-55", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("MNO-7890", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(false);
        doReturn(true).when(locacaoController).saveAllLocacoes(anyList());
        when(authControllerMock.updateCliente(any(Cliente.class))).thenReturn(false);
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal),
                "Deveria lançar LocacaoControllerException se a atualização do cliente falhar"
        );
        assertEquals("Erro ao atualizar saldo do cliente após locação.", thrown.getMessage());
        assertEquals(1, locacaoController.locacoes.size(), "A locação deveria permanecer na lista se o cliente/veículo falhar a atualização, a menos que haja rollback explícito.");
        verify(veiculoControllerMock, never()).atualizarVeiculo(any(Veiculo.class));
        verify(locacaoController, times(1)).saveAllLocacoes(anyList());
    }

    @Test
    @Order(4)
    @DisplayName("Realizar locacao - falha ao atualizar veiculo - Lanca LocacaoControllerException")
    void testRealizarLocacaoUpdateVehicleFailsThrowsException() {
        Cliente cliente = createTestClient("cliente_fail_update_v@test.com", "666.666.666-66", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("PQR-1234", 100.0);
        int dias = 5;
        double valorTotal = 500.0;

        when(veiculoControllerMock.estaLocado(veiculo)).thenReturn(false);
        doReturn(true).when(locacaoController).saveAllLocacoes(anyList());
        when(authControllerMock.updateCliente(any(Cliente.class))).thenReturn(true);
        when(veiculoControllerMock.atualizarVeiculo(any(Veiculo.class))).thenReturn(false);
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.realizarLocacao(cliente, veiculo, dias, valorTotal),
                "Deveria lançar LocacaoControllerException se a atualização do veículo falhar"
        );
        assertEquals("Erro ao atualizar veículo após locação.", thrown.getMessage());

        assertEquals(1, locacaoController.locacoes.size(), "A locação deveria permanecer na lista se a atualização do veículo falhar.");
        verify(authControllerMock, times(1)).updateCliente(any(Cliente.class));
        verify(veiculoControllerMock, times(1)).atualizarVeiculo(any(Veiculo.class));
        verify(locacaoController, times(1)).saveAllLocacoes(anyList());
    }


    @Test
    @Order(5)
    @DisplayName("Encontrar locacao ativa existente")
    void testEncontrarLocacaoAtivaExistente() {
        Cliente cliente = createTestClient("cliente5@test.com", "555.555.555-55", "senha123");
        Veiculo veiculo = createTestVeiculo("XYZ-7890", 200.0);

        Locacao locacaoAtiva = new Locacao(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(3),
                veiculo,
                cliente
        );
        doReturn(List.of(locacaoAtiva)).when(locacaoController).loadLocacoes();

        Locacao foundLocacao = locacaoController.encontrarLocacaoAtiva(veiculo);

        assertNotNull(foundLocacao, "Deveria encontrar uma locação ativa");
        assertEquals(veiculo.getPlaca(), foundLocacao.getVeiculo().getPlaca());
        assertNull(foundLocacao.getDataDevolucao(), "A locação encontrada deve ser ativa (dataDevolucao null)");
    }

    @Test
    @Order(6)
    @DisplayName("Nao encontrar locacao ativa para veiculo sem locacao")
    void testEncontrarLocacaoAtivaNaoEncontrada() {
        Veiculo veiculo = createTestVeiculo("TEST-1111", 150.0);
        doReturn(new ArrayList<>()).when(locacaoController).loadLocacoes();

        Locacao foundLocacao = locacaoController.encontrarLocacaoAtiva(veiculo);

        assertNull(foundLocacao, "Não deveria encontrar locação ativa para veículo sem locação");
    }

    @Test
    @Order(7)
    @DisplayName("Nao encontrar locacao ativa para veiculo com locacao ja finalizada")
    void testEncontrarLocacaoAtivaJaFinalizada() {
        Cliente cliente = createTestClient("cliente6@test.com", "666.666.666-66", "senha123");
        Veiculo veiculo = createTestVeiculo("TEST-2222", 150.0);

        Locacao locacaoFinalizada = new Locacao(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                veiculo,
                cliente
        );
        locacaoFinalizada.setDataDevolucao(LocalDateTime.now().minusDays(1));

        doReturn(List.of(locacaoFinalizada)).when(locacaoController).loadLocacoes();

        Locacao foundLocacao = locacaoController.encontrarLocacaoAtiva(veiculo);

        assertNull(foundLocacao, "Não deveria encontrar locação ativa para veículo já devolvido");
    }

    @Test
    @Order(10)
    @DisplayName("Registrar devolucao - locacao nula ou ja devolvida - Lanca LocacaoControllerException")
    void testRegistrarDevolucaoInvalidLocacaoThrowsException() {
        LocacaoControllerException thrownNull = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.registrarDevolucao(null),
                "Deveria lançar LocacaoControllerException para locação nula"
        );
        assertEquals("Locação inválida ou já devolvida.", thrownNull.getMessage());
        Locacao locacaoAlreadyReturned = new Locacao(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                createTestVeiculo("TEST-5555", 100.0),
                createTestClient("cliente9@test.com", "999.999.999-99", "senha123")
        );
        locacaoAlreadyReturned.setDataDevolucao(LocalDateTime.now().minusDays(1));

        LocacaoControllerException thrownReturned = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.registrarDevolucao(locacaoAlreadyReturned),
                "Deveria lançar LocacaoControllerException para locação já devolvida"
        );
        assertEquals("Locação inválida ou já devolvida.", thrownReturned.getMessage());

        verify(authControllerMock, never()).updateCliente(any(Cliente.class));
        verify(locacaoController, never()).saveAllLocacoes(anyList());
    }

    @Test
    @Order(11)
    @DisplayName("Registrar devolucao - locacao nao encontrada na lista interna - Lanca LocacaoControllerException")
    void testRegistrarDevolucaoLocacaoNotFoundInListThrowsException() {
        Cliente cliente = createTestClient("cliente10@test.com", "101.010.101-10", "senha123");
        Veiculo veiculo = createTestVeiculo("TEST-6666", 100.0);

        Locacao locacao = new Locacao(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                veiculo,
                cliente
        );
        doReturn(new ArrayList<>()).when(locacaoController).loadLocacoes();
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.registrarDevolucao(locacao),
                "Deveria lançar LocacaoControllerException se a locação não for encontrada para remoção/atualização"
        );
        assertEquals("Erro: Locação a ser devolvida não encontrada na lista para atualização.", thrown.getMessage());

        verify(authControllerMock, never()).updateCliente(any(Cliente.class));
        verify(locacaoController, never()).saveAllLocacoes(anyList());
    }

    @Test
    @Order(11)
    @DisplayName("Registrar devolucao - falha ao atualizar cliente com multa - Lanca LocacaoControllerException")
    void testRegistrarDevolucaoUpdateClientFailsThrowsException() {
        Cliente cliente = createTestClient("cliente_devol_fail@test.com", "777.777.777-70", "senha123");
        cliente.adicionarSaldo(1000.0);
        Veiculo veiculo = createTestVeiculo("MULTA-FAIL", 50.0);

        Locacao locacaoToDevolve = new Locacao(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1),
                veiculo,
                cliente
        );
        List<Locacao> currentLocacoes = new ArrayList<>();
        currentLocacoes.add(locacaoToDevolve);
        doReturn(currentLocacoes).when(locacaoController).loadLocacoes();

        doReturn(true).when(locacaoController).saveAllLocacoes(anyList());
        when(authControllerMock.updateCliente(any(Cliente.class))).thenReturn(false);
        LocacaoControllerException thrown = assertThrows(
                LocacaoControllerException.class,
                () -> locacaoController.registrarDevolucao(locacaoToDevolve),
                "Deveria lançar LocacaoControllerException se a atualização do cliente falhar"
        );
        assertEquals("Erro ao atualizar saldo do cliente com multa após devolução.", thrown.getMessage());

        verify(authControllerMock, times(1)).updateCliente(any(Cliente.class));
        verify(locacaoController, never()).saveAllLocacoes(anyList());
    }
    @Test
    @Order(12)
    @DisplayName("Persistencia: Salvamento e carregamento real de locacoes")
    void testRealSaveAndLoadLocacoes() throws IOException {
        new File(LOCACOES_FILE_PATH_ACTUAL).delete();

        LocacaoController realIOController = new LocacaoController(authControllerMock, veiculoControllerMock);

        Cliente cliente = createTestClient("cliente_io@test.com", "999.999.999-00", "senhaIO");
        cliente.adicionarSaldo(1000.0);

        Veiculo veiculo = createTestVeiculo("IO-1234", 75.0);
        Locacao locacao1 = new Locacao(
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                veiculo,
                cliente
        );
        locacao1.setDataDevolucao(LocalDateTime.now().minusHours(1));

        Locacao locacao2 = new Locacao(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                veiculo,
                cliente
        );

        realIOController.locacoes.add(locacao1);
        realIOController.locacoes.add(locacao2);

        boolean saved = realIOController.saveAllLocacoes(realIOController.locacoes);
        assertTrue(saved, "Deveria salvar locações no arquivo com sucesso");
        LocacaoController anotherIOController = new LocacaoController(authControllerMock, veiculoControllerMock);
        List<Locacao> loadedLocacoes = anotherIOController.loadLocacoes();

        assertNotNull(loadedLocacoes, "A lista de locações carregada não deve ser nula");
        assertEquals(2, loadedLocacoes.size(), "Deveria carregar 2 locações do arquivo");
        assertEquals(locacao1.getVeiculo().getPlaca(), loadedLocacoes.get(0).getVeiculo().getPlaca());
        assertEquals(locacao1.getCliente().getEmail(), loadedLocacoes.get(0).getCliente().getEmail());
        assertEquals(locacao1.getDataDevolucao(), loadedLocacoes.get(0).getDataDevolucao());

        assertEquals(locacao2.getVeiculo().getPlaca(), loadedLocacoes.get(1).getVeiculo().getPlaca());
        assertEquals(locacao2.getCliente().getEmail(), loadedLocacoes.get(1).getCliente().getEmail());
        assertNull(loadedLocacoes.get(1).getDataDevolucao());

        new File(LOCACOES_FILE_PATH_ACTUAL).delete();
    }
}