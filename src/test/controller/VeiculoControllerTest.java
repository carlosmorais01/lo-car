package controller;

import entities.Carro;
import entities.Caminhao;
import entities.Moto;
import entities.Veiculo;
import entities.Locacao;
import enums.Combustivel;
import enums.Cor;
import enums.Funcao;
import enums.Sexo;
import enums.Tracao;
import enums.Vagao;
import exceptions.VeiculoControllerException;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VeiculoControllerTest {

    private VeiculoController veiculoController;



    private static final String VEHICLE_PICS_DIR_ACTUAL = "dump/vehicle_pics/";
    private static final String CARROS_FILE = "dump/carros/carros.dat";
    private static final String MOTOS_FILE = "dump/moto/motos.dat";
    private static final String CAMINHOES_FILE = "dump/caminhao/caminhoes.dat";
    private static final String LOCACOES_FILE_PATH_ACTUAL = "dump/locacoes/locacoes.dat";

    @BeforeAll
    static void setupClass() throws IOException {

        new File("dump/carros").mkdirs();
        new File("dump/moto").mkdirs();
        new File("dump/caminhao").mkdirs();
        new File("dump/locacoes").mkdirs();
        new File(VEHICLE_PICS_DIR_ACTUAL).mkdirs();
        cleanUpAllTestFiles();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        veiculoController = spy(new VeiculoController());

        clearSpecificVehicleDataFiles();



        doReturn(new ArrayList<>()).when(veiculoController).carregarTodosVeiculos();
        doReturn(new ArrayList<>()).when(veiculoController).carregarVeiculosDeArquivo(eq(CARROS_FILE), any());
        doReturn(new ArrayList<>()).when(veiculoController).carregarVeiculosDeArquivo(eq(MOTOS_FILE), any());
        doReturn(new ArrayList<>()).when(veiculoController).carregarVeiculosDeArquivo(eq(CAMINHOES_FILE), any());
        doReturn(new ArrayList<>()).when(veiculoController).carregarLocacoes();

        doReturn(true).when(veiculoController).salvarVeiculoEmArquivo(any(Veiculo.class), anyString());

        doReturn(true).when(veiculoController).salvarListaDeVeiculosEmArquivo(anyList(), anyString());


        doReturn("path/to/mocked/image.png").when(veiculoController).saveVehiclePicture(anyString(), anyString());








        veiculoController.veiculos = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        clearSpecificVehicleDataFiles();
    }

    @AfterAll
    static void tearDownClass() throws IOException {
        cleanUpAllTestFiles();
        deleteDirectoryRecursive(Path.of("dump/carros/"));
        deleteDirectoryRecursive(Path.of("dump/moto/"));
        deleteDirectoryRecursive(Path.of("dump/caminhao/"));
        deleteDirectoryRecursive(Path.of("dump/locacoes/"));
        deleteDirectoryRecursive(Path.of(VEHICLE_PICS_DIR_ACTUAL));

        try {
            Files.deleteIfExists(Path.of("dump/"));
        } catch (java.nio.file.DirectoryNotEmptyException e) {
            System.err.println("Aviso: Diretório 'dump' ainda não está vazio após testes de VeiculoController.");
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
        Files.deleteIfExists(Path.of(CARROS_FILE));
        Files.deleteIfExists(Path.of(MOTOS_FILE));
        Files.deleteIfExists(Path.of(CAMINHOES_FILE));
        Files.deleteIfExists(Path.of(LOCACOES_FILE_PATH_ACTUAL));

        File vehiclePicsDir = new File(VEHICLE_PICS_DIR_ACTUAL);
        if (vehiclePicsDir.exists()) {
            for (File file : vehiclePicsDir.listFiles()) {
                Files.deleteIfExists(file.toPath());
            }
        }
    }

    private void clearSpecificVehicleDataFiles() {
        new File(CARROS_FILE).delete();
        new File(MOTOS_FILE).delete();
        new File(CAMINHOES_FILE).delete();
        new File(LOCACOES_FILE_PATH_ACTUAL).delete();
    }

    private Carro createTestCarro(String placa, double valorDiario) {
        return new Carro("DescCarro", placa, "MarcaCarro", "NomeCarro", "ModeloCarro", 2020, Cor.PRETO, Funcao.PASSEIO,
                10000, 5, 10.0, 180.0, true, Combustivel.GASOLINA, Tracao.DIANTEIRA,
                5, true, "foto_carro.jpg", 150.0, true, true, true, true,
                false, 1200.0, false, true, valorDiario, 4, true);
    }

    private Moto createTestMoto(String placa, double valorDiario) {
        return new Moto("DescMoto", placa, "MarcaMoto", "NomeMoto", "ModeloMoto", 2022, Cor.AZUL, Funcao.PASSEIO,
                5000, 2, 25.0, 150, false, Combustivel.GASOLINA, Tracao.TRASEIRA,
                2, false, "foto_moto.jpg", 20.0, false, false, false, true,
                false, 200, false, false, valorDiario, 250, true, 17);
    }

    private Caminhao createTestCaminhao(String placa, double valorDiario) {
        return new Caminhao("DescCaminhao", placa, "MarcaCaminhao", "NomeCaminhao", "ModeloCaminhao", 2018, Cor.BRANCO, Funcao.CARGA,
                50000, 3, 4.0, 100, false, Combustivel.DIESEL, Tracao.INTEGRAL,
                3, true, "foto_caminhao.jpg", 400.0, true, true, false, false,
                false, 10000, true, true, valorDiario, 20000, 4.0, 2.5, 7.0, Vagao.BAU_SECO);
    }


    private Locacao createTestLocacao(Veiculo veiculo, LocalDateTime dataDevolucao) {

        entities.Cliente clienteTest = new entities.Cliente(
                "Test Client", "123.123.123-12", "999999999", "test@client.com", "hashedpass",
                new entities.Endereco("City", "ST", "Bairro", "Rua", 1, "12345-000"),
                LocalDateTime.of(1990, 1, 1, 0, 0), Sexo.MASCULINO, null
        );

        Locacao locacao = new Locacao(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(2),
                veiculo,
                clienteTest
        );

        if (dataDevolucao != null) {
            locacao.setDataDevolucao(dataDevolucao);
        }

        return locacao;
    }


    @Test
    @Order(4)
    @DisplayName("Cadastrar veículo com placa já existente - Lança VeiculoControllerException")
    void testCadastrarVeiculoPlacaExistenteThrowsException() {
        Carro carroExistente = createTestCarro("ABC-1234", 200.0);
        veiculoController.veiculos.add(carroExistente);

        Carro novoCarroComPlacaExistente = createTestCarro("ABC-1234", 250.0);

        VeiculoControllerException thrown = assertThrows(
                VeiculoControllerException.class,
                () -> veiculoController.cadastrarVeiculo(novoCarroComPlacaExistente),
                "Deveria lançar VeiculoControllerException se a placa já existe"
        );
        assertEquals("Erro: Veículo com a placa 'ABC-1234' já existe.", thrown.getMessage());
        assertEquals(1, veiculoController.listarTodos().size(), "A quantidade de veículos não deveria mudar");
        verify(veiculoController, never()).saveVehiclePicture(anyString(), anyString());
        verify(veiculoController, never()).salvarVeiculoEmArquivo(any(Veiculo.class), anyString());
    }

    @Test
    @Order(5)
    @DisplayName("Cadastrar veículo com falha ao salvar imagem - Lança VeiculoControllerException")
    void testCadastrarVeiculoSavePictureFailsThrowsException() throws IOException {
        Carro novoCarro = createTestCarro("FALHA-FOTO", 300.0);
        novoCarro.setCaminhoFoto("caminho/fake/foto.jpg");

        doThrow(new VeiculoControllerException("Erro ao salvar imagem")).when(veiculoController).saveVehiclePicture(anyString(), anyString());




        VeiculoControllerException thrown = assertThrows(
                VeiculoControllerException.class,
                () -> veiculoController.cadastrarVeiculo(novoCarro),
                "Deveria lançar VeiculoControllerException se a imagem não puder ser salva"
        );

        assertEquals("Erro ao salvar imagem", thrown.getMessage());

        assertTrue(veiculoController.listarTodos().isEmpty(), "Nenhum veículo deveria ser adicionado à lista");
        verify(veiculoController).saveVehiclePicture(anyString(), anyString());
        verify(veiculoController, never()).salvarVeiculoEmArquivo(any(Veiculo.class), anyString());
    }

    @Test
    @Order(6)
    @DisplayName("Atualizar veículo existente com sucesso")
    void testAtualizarVeiculoSuccess() throws IOException {
        Carro carroOriginal = createTestCarro("ATT-1234", 200.0);
        veiculoController.veiculos.add(carroOriginal);

        Carro carroAtualizado = createTestCarro("ATT-1234", 250.0);
        carroAtualizado.setNome("Carro Atualizado");

        doReturn(true).when(veiculoController).salvarListaDeVeiculosEmArquivo(anyList(), anyString());

        boolean result = veiculoController.atualizarVeiculo(carroAtualizado);

        assertTrue(result, "Deveria atualizar o veículo com sucesso");
        assertEquals(1, veiculoController.listarTodos().size(), "Deveria haver 1 veículo na lista após atualização");
        assertEquals("Carro Atualizado", veiculoController.listarTodos().get(0).getNome());
        assertEquals(250.0, veiculoController.listarTodos().get(0).getValorDiario());

        verify(veiculoController).salvarListaDeVeiculosEmArquivo(anyList(), eq("dump/carros/carros.dat"));
    }

    @Test
    @Order(7)
    @DisplayName("Atualizar veículo inexistente - Lança VeiculoControllerException")
    void testAtualizarVeiculoNotFoundThrowsException() {
        Carro carroInexistente = createTestCarro("NAO-EXISTE", 300.0);

        veiculoController.veiculos.clear();

        VeiculoControllerException thrown = assertThrows(
                VeiculoControllerException.class,
                () -> veiculoController.atualizarVeiculo(carroInexistente),
                "Deveria lançar VeiculoControllerException se o veículo não for encontrado"
        );
        assertEquals("Erro ao atualizar: Veículo com placa 'NAO-EXISTE' não encontrado.", thrown.getMessage());
        assertTrue(veiculoController.listarTodos().isEmpty(), "Nenhum veículo deveria ser adicionado ou removido");
        verify(veiculoController, never()).salvarListaDeVeiculosEmArquivo(anyList(), anyString());
    }

    @Test
    @Order(8)
    @DisplayName("Excluir veículo existente com sucesso")
    void testExcluirVeiculoSuccess() throws IOException {
        Carro carroParaExcluir = createTestCarro("DEL-1234", 100.0);
        veiculoController.veiculos.add(carroParaExcluir);

        doReturn(true).when(veiculoController).salvarListaDeVeiculosEmArquivo(anyList(), anyString());

        boolean result = veiculoController.excluirVeiculo(carroParaExcluir);

        assertTrue(result, "Deveria excluir o veículo com sucesso");
        assertTrue(veiculoController.listarTodos().isEmpty(), "A lista de veículos deveria estar vazia");
        verify(veiculoController).salvarListaDeVeiculosEmArquivo(anyList(), eq("dump/carros/carros.dat"));
    }

    @Test
    @Order(9)
    @DisplayName("Excluir veículo inexistente")
    void testExcluirVeiculoNotFound() {
        Carro carroInexistente = createTestCarro("DEL-NAO-EXISTE", 100.0);

        veiculoController.veiculos.clear();

        boolean result = veiculoController.excluirVeiculo(carroInexistente);

        assertFalse(result, "Não deveria excluir um veículo inexistente");
        assertTrue(veiculoController.listarTodos().isEmpty(), "A lista de veículos deveria permanecer vazia");
        verify(veiculoController, never()).salvarListaDeVeiculosEmArquivo(anyList(), anyString());
    }

    @Test
    @Order(11)
    @DisplayName("Salvar imagem de veículo com caminho nulo - Retorna null")
    void testSaveVehiclePictureNullPathReturnsNull() {



        String savedPath = veiculoController.saveVehiclePicture(null, "NULL-PATH");
        assertNull(savedPath, "Deveria retornar null para caminho original nulo");
    }

    @Test
    @Order(14)
    @DisplayName("Verificar veiculo locado - Retorna true")
    void testEstaLocadoTrue() {
        Veiculo veiculoLocado = createTestCarro("LOC-0001", 100.0);
        Locacao locacaoAtiva = createTestLocacao(veiculoLocado, null);

        doReturn(List.of(locacaoAtiva)).when(veiculoController).carregarLocacoes();

        assertTrue(veiculoController.estaLocado(veiculoLocado), "Veículo deveria estar locado");
        verify(veiculoController).carregarLocacoes();
    }

    @Test
    @Order(15)
    @DisplayName("Verificar veiculo nao locado - Retorna false")
    void testEstaLocadoFalse() {
        Veiculo veiculoNaoLocado = createTestCarro("NAO-LOCADO", 100.0);

        doReturn(new ArrayList<>()).when(veiculoController).carregarLocacoes();

        assertFalse(veiculoController.estaLocado(veiculoNaoLocado), "Veículo não deveria estar locado");
        verify(veiculoController).carregarLocacoes();
    }

    @Test
    @Order(16)
    @DisplayName("Verificar veiculo com locacao finalizada - Retorna false")
    void testEstaLocadoComLocacaoFinalizada() {
        Veiculo veiculoDevolvido = createTestCarro("DEVOLVIDO", 100.0);
        Locacao locacaoFinalizada = createTestLocacao(veiculoDevolvido, LocalDateTime.now().minusDays(1));

        doReturn(List.of(locacaoFinalizada)).when(veiculoController).carregarLocacoes();

        assertFalse(veiculoController.estaLocado(veiculoDevolvido), "Veículo com locação finalizada não deveria estar locado");
        verify(veiculoController).carregarLocacoes();
    }

    @Test
    @Order(18)
    @DisplayName("Filtrar veículos por preço máximo e cor")
    void testFiltrarVeiculosPrecoCor() {
        Carro carro1 = createTestCarro("CAR-003", 100.0); carro1.setCor(Cor.BRANCO);
        Carro carro2 = createTestCarro("CAR-004", 150.0); carro2.setCor(Cor.PRETO);
        Moto moto1 = createTestMoto("MOTO-002", 80.0); moto1.setCor(Cor.AZUL);

        veiculoController.veiculos.addAll(List.of(carro1, carro2, moto1));
        doReturn(new ArrayList<>()).when(veiculoController).carregarLocacoes();

        List<Veiculo> resultados = veiculoController.filtrarVeiculos(null, 120.0, Cor.BRANCO, "Todos", null, null, "Todos os Modelos");

        assertEquals(1, resultados.size(), "Deveria encontrar 1 carro branco com preço <= 120");
        assertEquals("CAR-003", resultados.get(0).getPlaca());
    }

    @Test
    @Order(19)
    @DisplayName("Filtrar veículos por tipo")
    void testFiltrarVeiculosPorTipo() {
        Carro carro1 = createTestCarro("CAR-005", 100.0);
        Moto moto1 = createTestMoto("MOTO-003", 80.0);
        Caminhao caminhao1 = createTestCaminhao("CAM-002", 500.0);

        veiculoController.veiculos.addAll(List.of(carro1, moto1, caminhao1));
        doReturn(new ArrayList<>()).when(veiculoController).carregarLocacoes();

        List<Veiculo> carros = veiculoController.filtrarVeiculos(null, null, Cor.TODAS, "Todos", null, null, "Carro");
        assertEquals(1, carros.size());
        assertTrue(carros.get(0) instanceof Carro);

        List<Veiculo> motos = veiculoController.filtrarVeiculos(null, null, Cor.TODAS, "Todos", null, null, "Moto");
        assertEquals(1, motos.size());
        assertTrue(motos.get(0) instanceof Moto);

        List<Veiculo> caminhoes = veiculoController.filtrarVeiculos(null, null, Cor.TODAS, "Todos", null, null, "Caminhão");
        assertEquals(1, caminhoes.size());
        assertTrue(caminhoes.get(0) instanceof Caminhao);

        List<Veiculo> todos = veiculoController.filtrarVeiculos(null, null, Cor.TODAS, "Todos", null, null, "Todos os Modelos");
        assertEquals(3, todos.size());
    }

    @Test
    @Order(20)
    @DisplayName("Filtrar veículos por 'Próximos de Devolução'")
    void testFiltrarVeiculosProximosDevolucao() {
        Carro carroProximo = createTestCarro("PROX-001", 100.0);
        Carro carroNaoProximo = createTestCarro("NAO-PROX", 100.0);
        Carro carroDevolvido = createTestCarro("DEVOLV", 100.0);

        Locacao locacaoProxima = createTestLocacao(carroProximo, null);
        locacaoProxima.setDataLocacao(LocalDateTime.now().minusDays(1));
        locacaoProxima.setDataPrevistaDevolucao(LocalDateTime.now().plusHours(24));

        Locacao locacaoNaoProxima = createTestLocacao(carroNaoProximo, null);
        locacaoNaoProxima.setDataLocacao(LocalDateTime.now().minusDays(10));
        locacaoNaoProxima.setDataPrevistaDevolucao(LocalDateTime.now().plusDays(5));

        Locacao locacaoDevolvida = createTestLocacao(carroDevolvido, LocalDateTime.now().minusDays(1));

        veiculoController.veiculos.addAll(List.of(carroProximo, carroNaoProximo, carroDevolvido));
        doReturn(List.of(locacaoProxima, locacaoNaoProxima, locacaoDevolvida)).when(veiculoController).carregarLocacoes();

        List<Veiculo> resultados = veiculoController.filtrarVeiculos(null, null, Cor.TODAS, "Próximos de Devolução", null, null, "Todos os Modelos");

        assertEquals(1, resultados.size());
        assertEquals("PROX-001", resultados.get(0).getPlaca());
    }

    private String createTempPhotoFile(String fileName) {
        try {
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "test_images_" + UUID.randomUUID().toString());
            Files.createDirectories(tempDir);

            String name = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            Path tempFilePath = Files.createTempFile(tempDir, name, extension);

            Files.write(tempFilePath, new byte[]{1, 2, 3, 4, 5});
            return tempFilePath.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Falha ao criar arquivo temporário para teste de foto: " + e.getMessage());
            return null;
        }
    }
}