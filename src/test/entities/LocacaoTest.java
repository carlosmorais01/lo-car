package entities;

import enums.Combustivel;
import enums.Cor;
import enums.Funcao;
import enums.Sexo;
import enums.Tracao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Locacao")
class LocacaoTest {

    private Veiculo veiculoTeste;
    private Cliente clienteTeste;
    private Endereco enderecoTeste;

    @BeforeEach
    void setUp() {
        veiculoTeste = new VeiculoImpl(
                "Descrição Veiculo Teste", "ABC-0000", "Marca Teste", "Nome Teste", "Modelo Teste", 2020,
                Cor.AZUL, Funcao.PASSEIO, 10000, 5, 10.0, 180.0, true,
                Combustivel.GASOLINA, Tracao.DIANTEIRA, 5, true, "/caminho/foto.jpg",
                150.0, true, true, true, true, false, 1200.0, false, true, 100.0
        );

        enderecoTeste = new Endereco("Cidade Cliente", "Estado Cliente", "Bairro Cliente", "Rua Cliente", 123, "12345-678");
        clienteTeste = new Cliente("Cliente Teste", "111.222.333-44", "99999-9999", "cliente@test.com", "senha", enderecoTeste, LocalDateTime.of(1990, 1, 1, 0, 0), Sexo.MASCULINO, "/caminho/cliente.jpg");
    }

    @Test
    @DisplayName("Deve inicializar locação com dataDevolucao nula e calcular valor previsto")
    void testConstructorActiveLocacao() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(5).plusHours(1);

        Locacao locacao = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);

        assertNotNull(locacao);
        assertEquals(dataLocacao, locacao.getDataLocacao());
        assertEquals(dataPrevistaDevolucao, locacao.getDataPrevistaDevolucao());
        assertNull(locacao.getDataDevolucao());
        assertEquals(veiculoTeste, locacao.getVeiculo());
        assertEquals(clienteTeste, locacao.getCliente());

        assertEquals(600.0, locacao.getValorLocacao(), 0.001);
    }

    @Test
    @DisplayName("Deve calcular valor previsto corretamente para diferentes durações")
    void testCalcularValorPrevisto() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);

        Locacao locacao1Dia = new Locacao(dataLocacao, dataLocacao.plusHours(12), veiculoTeste, clienteTeste);
        assertEquals(100.0, locacao1Dia.calcularValorPrevisto(), 0.001, "1 dia de locação (menos de 24h).");

        Locacao locacao24h = new Locacao(dataLocacao, dataLocacao.plusDays(1), veiculoTeste, clienteTeste);
        assertEquals(100.0, locacao24h.calcularValorPrevisto(), 0.001, "1 dia de locação (exatamente 24h).");

        Locacao locacao2dias1h = new Locacao(dataLocacao, dataLocacao.plusDays(2).plusHours(1), veiculoTeste, clienteTeste);
        assertEquals(300.0, locacao2dias1h.calcularValorPrevisto(), 0.001, "2 dias e 1 hora de locação (arredonda para 3 dias).");

        Locacao locacao7Dias = new Locacao(dataLocacao, dataLocacao.plusDays(7), veiculoTeste, clienteTeste);
        assertEquals(700.0, locacao7Dias.calcularValorPrevisto(), 0.001, "7 dias de locação.");
    }

    @Test
    @DisplayName("Deve calcular multa corretamente para diferentes atrasos")
    void testCalcularMulta() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(2);

        Locacao locacaoNoAtraso = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoNoAtraso.setDataDevolucao(dataPrevistaDevolucao.plusHours(2));
        assertEquals(0.0, locacaoNoAtraso.calcularMulta(), 0.001, "Multa deve ser zero para atraso menor que 3 horas.");

        Locacao locacaoAtraso3h = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoAtraso3h.setDataDevolucao(dataPrevistaDevolucao.plusHours(3));
        assertEquals(100.0, locacaoAtraso3h.calcularMulta(), 0.001, "Multa deve ser 1 diária para 3 horas de atraso.");

        Locacao locacaoAtraso5h = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoAtraso5h.setDataDevolucao(dataPrevistaDevolucao.plusHours(5));
        assertEquals(200.0, locacaoAtraso5h.calcularMulta(), 0.001, "Multa deve ser 2 diárias para 5 horas de atraso.");

        Locacao locacaoAtraso24h = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoAtraso24h.setDataDevolucao(dataPrevistaDevolucao.plusHours(24));
        assertEquals(800.0, locacaoAtraso24h.calcularMulta(), 0.001, "Multa deve ser 8 diárias para 24 horas de atraso.");

        Locacao locacaoAntesPrevisto = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoAntesPrevisto.setDataDevolucao(dataPrevistaDevolucao.minusDays(1));
        assertEquals(0.0, locacaoAntesPrevisto.calcularMulta(), 0.001, "Multa deve ser zero para devolução antes do previsto.");

        Locacao locacaoNoPrevisto = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoNoPrevisto.setDataDevolucao(dataPrevistaDevolucao);
        assertEquals(0.0, locacaoNoPrevisto.calcularMulta(), 0.001, "Multa deve ser zero para devolução na data prevista.");
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente com e sem multa")
    void testCalcularValorTotal() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(2);

        Locacao locacaoAtiva = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        assertEquals(200.0, locacaoAtiva.calcularValorTotal(), 0.001, "Valor total de locação ativa deve ser o valor previsto.");

        Locacao locacaoNoPrazo = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoNoPrazo.setDataDevolucao(dataPrevistaDevolucao);
        assertEquals(200.0, locacaoNoPrazo.calcularValorTotal(), 0.001, "Valor total deve ser só as diárias quando no prazo.");

        Locacao locacaoComAtraso = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoComAtraso.setDataDevolucao(dataPrevistaDevolucao.plusHours(4));
        assertEquals(500.0, locacaoComAtraso.calcularValorTotal(), 0.001, "Valor total deve incluir multa para atraso.");
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException se veículo for nulo ao calcular valor total")
    void testCalcularValorTotalThrowsExceptionIfVeiculoNull() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(2);
        Locacao locacaoInvalida = new Locacao(dataLocacao, dataPrevistaDevolucao, null, clienteTeste);
        locacaoInvalida.setDataDevolucao(dataLocacao.plusDays(2));

        assertThrows(IllegalStateException.class, () -> locacaoInvalida.calcularValorTotal(),
                "Deve lançar IllegalStateException se veículo for nulo.");
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException se dataLocacao for nula ao calcular valor total")
    void testCalcularValorTotalThrowsExceptionIfDataLocacaoNull() {
        LocalDateTime dataPrevistaDevolucao = LocalDateTime.of(2025, 6, 22, 10, 0);
        LocalDateTime dataDevolucao = LocalDateTime.of(2025, 6, 22, 10, 0);
        Locacao locacaoInvalida = new Locacao(null, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoInvalida.setDataDevolucao(dataDevolucao);

        assertThrows(IllegalStateException.class, () -> locacaoInvalida.calcularValorTotal(),
                "Deve lançar IllegalStateException se dataLocacao for nula.");
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException se dataPrevistaDevolucao for nula ao calcular valor total")
    void testCalcularValorTotalThrowsExceptionIfDataPrevistaDevolucaoNull() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataDevolucao = LocalDateTime.of(2025, 6, 22, 10, 0);
        Locacao locacaoInvalida = new Locacao(dataLocacao, null, veiculoTeste, clienteTeste);
        locacaoInvalida.setDataDevolucao(dataDevolucao);

        assertThrows(IllegalStateException.class, () -> locacaoInvalida.calcularValorTotal(),
                "Deve lançar IllegalStateException se dataPrevistaDevolucao for nula.");
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException se dataDevolucao for anterior à dataLocacao ao calcular valor total")
    void testCalcularValorTotalThrowsExceptionIfDevolucaoBeforeLocacao() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(2);
        LocalDateTime dataDevolucaoInvalida = dataLocacao.minusDays(1);
        Locacao locacaoInvalida = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        locacaoInvalida.setDataDevolucao(dataDevolucaoInvalida);

        assertThrows(IllegalStateException.class, () -> locacaoInvalida.calcularValorTotal(),
                "Deve lançar IllegalStateException se dataDevolucao for anterior à dataLocacao.");
    }

    @ParameterizedTest(name = "Início: {0}, Fim: {1} -> Dias: {2}")
    @MethodSource("provideDatesForCalcularNumeroDeDiasParaCobranca")
    @DisplayName("Deve calcular o número de dias para cobrança corretamente")
    void testCalcularNumeroDeDiasParaCobranca(LocalDateTime inicio, LocalDateTime fim, long expectedDias) throws Exception {
        Method method = Locacao.class.getDeclaredMethod("calcularNumeroDeDiasParaCobranca", LocalDateTime.class, LocalDateTime.class);
        method.setAccessible(true);

        long actualDias = (long) method.invoke(null, inicio, fim);

        assertEquals(expectedDias, actualDias, "O número de dias para cobrança deve ser calculado corretamente.");
    }

    private static Stream<Arguments> provideDatesForCalcularNumeroDeDiasParaCobranca() {
        LocalDateTime base = LocalDateTime.of(2025, 7, 1, 10, 0);
        return Stream.of(
                Arguments.of(base, base.plusDays(1), 1L),
                Arguments.of(base, base.plusDays(5), 5L),
                Arguments.of(base, base.plusDays(0).plusHours(1), 1L),
                Arguments.of(base, base.plusDays(0).plusHours(23), 1L),
                Arguments.of(base, base.plusDays(1).plusHours(1), 2L),
                Arguments.of(base, base.plusDays(1).plusHours(23), 2L),

                Arguments.of(base, base, 0L),
                Arguments.of(base.plusDays(1), base, 0L),

                Arguments.of(null, null, 0L),
                Arguments.of(base, null, 0L),
                Arguments.of(null, base, 0L)
        );
    }

    @Test
    @DisplayName("Deve permitir obter e alterar o valor da locação")
    void testGetSetValorLocacao() {
        Locacao locacao = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(1), veiculoTeste, clienteTeste);
        locacao.setValorLocacao(999.99);
        assertEquals(999.99, locacao.getValorLocacao(), 0.001);
    }

    @Test
    @DisplayName("Deve permitir obter e alterar a data de locação")
    void testGetSetDataLocacao() {
        Locacao locacao = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(1), veiculoTeste, clienteTeste);
        LocalDateTime novaData = LocalDateTime.of(2025, 1, 1, 0, 0);
        locacao.setDataLocacao(novaData);
        assertEquals(novaData, locacao.getDataLocacao());
    }

    @Test
    @DisplayName("Deve permitir obter e alterar a data prevista de devolução")
    void testGetSetDataPrevistaDevolucao() {
        Locacao locacao = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(1), veiculoTeste, clienteTeste);
        LocalDateTime novaData = LocalDateTime.of(2025, 2, 1, 0, 0);
        locacao.setDataPrevistaDevolucao(novaData);
        assertEquals(novaData, locacao.getDataPrevistaDevolucao());
    }

    @Test
    @DisplayName("Deve permitir obter e alterar a data de devolução e recalcular valor total")
    void testGetSetDataDevolucaoAndRecalculateTotal() {
        LocalDateTime dataLocacao = LocalDateTime.of(2025, 6, 20, 10, 0);
        LocalDateTime dataPrevistaDevolucao = dataLocacao.plusDays(2);

        Locacao locacao = new Locacao(dataLocacao, dataPrevistaDevolucao, veiculoTeste, clienteTeste);
        assertEquals(200.0, locacao.getValorLocacao(), 0.001, "Valor inicial deve ser o previsto.");
        assertNull(locacao.getDataDevolucao(), "Data de devolução deve ser nula inicialmente.");

        LocalDateTime dataDevolucaoReal = dataPrevistaDevolucao.plusHours(4);
        locacao.setDataDevolucao(dataDevolucaoReal);
        assertEquals(dataDevolucaoReal, locacao.getDataDevolucao());


        assertEquals(500.0, locacao.getValorLocacao(), 0.001, "Valor total deve ser recalculado após setar dataDevolucao.");
    }

    @Test
    @DisplayName("Deve permitir obter e alterar o veículo")
    void testGetSetVeiculo() {
        Locacao locacao = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(1), veiculoTeste, clienteTeste);
        Veiculo novoVeiculo = new VeiculoImpl("Novo", "TEST-0000", "New", "Test", "Model", 2024, Cor.BRANCO, Funcao.PADRAO, 100, 4, 10, 100, false, Combustivel.GASOLINA, Tracao.DIANTEIRA, 4, false, "", 50, false, false, false, false, false, 500, false, false, 50.0);
        locacao.setVeiculo(novoVeiculo);
        assertEquals(novoVeiculo, locacao.getVeiculo());
    }

    @Test
    @DisplayName("Deve permitir obter e alterar o cliente")
    void testGetSetCliente() {
        Locacao locacao = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(1), veiculoTeste, clienteTeste);
        Cliente novoCliente = new Cliente("Novo Cliente", "222.333.444-55", "11111-1111", "novo@test.com", "senha", enderecoTeste, LocalDateTime.of(1980, 1, 1, 0, 0), Sexo.MASCULINO, "");
        locacao.setCliente(novoCliente);
        assertEquals(novoCliente, locacao.getCliente());
    }
}