package entities;



import enums.*;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;



@DisplayName("Testes para a classe Veiculo")

class VeiculoTest {



    private VeiculoImpl veiculo;



    @BeforeEach

    void setUp() {

        veiculo = new VeiculoImpl(

                "Descrição Teste",

                "ABC1234",

                "Marca Teste",

                "Nome Teste",

                "Modelo Teste",

                2020,

                Cor.AZUL,

                Funcao.PASSEIO,

                10000.5,

                5,

                10.0,

                180.0,

                true,

                Combustivel.GASOLINA,

                Tracao.DIANTEIRA,

                5,

                true,

                "/caminho/foto.jpg",

                150.0,

                true,

                true,

                true,

                true,

                false,

                1200.0,

                false,

                true,

                250.0

        );

    }



    @Test

    @DisplayName("Deve inicializar o veículo com os valores corretos via construtor")

    void testConstructorInitialization() {

        assertNotNull(veiculo, "O objeto veículo não deve ser nulo.");

        assertEquals("Descrição Teste", veiculo.getDescricao(), "A descrição deve ser inicializada corretamente.");

        assertEquals("ABC1234", veiculo.getPlaca(), "A placa deve ser inicializada corretamente.");

        assertEquals("Marca Teste", veiculo.getMarca(), "A marca deve ser inicializada corretamente.");

        assertEquals("Nome Teste", veiculo.getNome(), "O nome deve ser inicializado corretamente.");

        assertEquals("Modelo Teste", veiculo.getModelo(), "O modelo deve ser inicializado corretamente.");

        assertEquals(2020, veiculo.getAno(), "O ano deve ser inicializado corretamente.");

        assertEquals(Cor.AZUL, veiculo.getCor(), "A cor deve ser inicializada corretamente.");

        assertEquals(Funcao.PASSEIO, veiculo.getFuncao(), "A função deve ser inicializada corretamente.");

        assertEquals(10000.5, veiculo.getQuilometragem(), "A quilometragem deve ser inicializada corretamente.");

        assertEquals(5, veiculo.getNumeroPassageiros(), "O número de passageiros deve ser inicializado corretamente.");

        assertEquals(10.0, veiculo.getConsumoCombustivelPLitro(), "O consumo de combustível deve ser inicializado corretamente.");

        assertEquals(180.0, veiculo.getVelocidadeMax(), "A velocidade máxima deve ser inicializada corretamente.");

        assertTrue(veiculo.isAutomatico(), "O status de automático deve ser inicializado como verdadeiro.");

        assertEquals(Combustivel.GASOLINA, veiculo.getCombustivel(), "O combustível deve ser inicializado corretamente.");

        assertEquals(Tracao.DIANTEIRA, veiculo.getTracao(), "A tração deve ser inicializada corretamente.");

        assertEquals(5, veiculo.getQuantAssento(), "A quantidade de assentos deve ser inicializada corretamente.");

        assertTrue(veiculo.isAirBag(), "O status de airbag deve ser inicializado como verdadeiro.");

        assertEquals("/caminho/foto.jpg", veiculo.getCaminhoFoto(), "O caminho da foto deve ser inicializado corretamente.");

        assertEquals(150.0, veiculo.getPotencia(), "A potência deve ser inicializada corretamente.");

        assertTrue(veiculo.isVidroEletrico(), "O status de vidro elétrico deve ser inicializado como verdadeiro.");

        assertTrue(veiculo.isArCondicionado(), "O status de ar condicionado deve ser inicializado como verdadeiro.");

        assertTrue(veiculo.isMultimidia(), "O status de multimídia deve ser inicializado como verdadeiro.");

        assertTrue(veiculo.isEntradaUSB(), "O status de entrada USB deve ser inicializado como verdadeiro.");

        assertFalse(veiculo.isVidroFume(), "O status de vidro fumê deve ser inicializado como falso.");

        assertEquals(1200.0, veiculo.getPeso(), "O peso deve ser inicializado corretamente.");

        assertFalse(veiculo.isEngate(), "O status de engate deve ser inicializado como falso.");

        assertTrue(veiculo.isDirecaoHidraulica(), "O status de direção hidráulica deve ser inicializado como verdadeiro.");

        assertEquals(250.0, veiculo.getValorDiario(), "O valor diário deve ser inicializado corretamente.");

        assertEquals(0, veiculo.getLocacoes(), "O contador de locações deve ser inicializado como 0.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a descrição")

    void testGetSetDescricao() {

        String novaDescricao = "Nova Descrição de Teste";

        veiculo.setDescricao(novaDescricao);

        assertEquals(novaDescricao, veiculo.getDescricao(), "A descrição deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a placa")

    void testGetSetPlaca() {

        String novaPlaca = "DEF5678";

        veiculo.setPlaca(novaPlaca);

        assertEquals(novaPlaca, veiculo.getPlaca(), "A placa deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o número de locações")

    void testGetSetLocacoes() {

        veiculo.setLocacoes(5);

        assertEquals(5, veiculo.getLocacoes(), "O número de locações deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a marca")

    void testGetSetMarca() {

        String novaMarca = "Nova Marca";

        veiculo.setMarca(novaMarca);

        assertEquals(novaMarca, veiculo.getMarca(), "A marca deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o modelo")

    void testGetSetModelo() {

        String novoModelo = "Novo Modelo";

        veiculo.setModelo(novoModelo);

        assertEquals(novoModelo, veiculo.getModelo(), "O modelo deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o nome")

    void testGetSetNome() {

        String novoNome = "Novo Nome";

        veiculo.setNome(novoNome);

        assertEquals(novoNome, veiculo.getNome(), "O nome deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o ano")

    void testGetSetAno() {

        veiculo.setAno(2022);

        assertEquals(2022, veiculo.getAno(), "O ano deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a cor")

    void testGetSetCor() {

        veiculo.setCor(Cor.VERMELHO);

        assertEquals(Cor.VERMELHO, veiculo.getCor(), "A cor deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a função")

    void testGetSetFuncao() {

        veiculo.setFuncao(Funcao.CARGA);

        assertEquals(Funcao.CARGA, veiculo.getFuncao(), "A função deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a quilometragem")

    void testGetSetQuilometragem() {

        veiculo.setQuilometragem(20000.0);

        assertEquals(20000.0, veiculo.getQuilometragem(), "A quilometragem deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o número de passageiros")

    void testGetSetNumeroPassageiros() {

        veiculo.setNumeroPassageiros(7);

        assertEquals(7, veiculo.getNumeroPassageiros(), "O número de passageiros deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o consumo de combustível por litro")

    void testGetSetConsumoCombustivelPLitro() {

        veiculo.setConsumoCombustivelPLitro(12.5);

        assertEquals(12.5, veiculo.getConsumoCombustivelPLitro(), "O consumo de combustível deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a velocidade máxima")

    void testGetSetVelocidadeMax() {

        veiculo.setVelocidadeMax(200.0);

        assertEquals(200.0, veiculo.getVelocidadeMax(), "A velocidade máxima deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de automático")

    void testGetSetAutomatico() {

        veiculo.setAutomatico(false);

        assertFalse(veiculo.isAutomatico(), "O status de automático deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o tipo de combustível")

    void testGetSetCombustivel() {

        veiculo.setCombustivel(Combustivel.ALCOOL);

        assertEquals(Combustivel.ALCOOL, veiculo.getCombustivel(), "O tipo de combustível deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o tipo de tração")

    void testGetSetTracao() {

        veiculo.setTracao(Tracao.INTEGRAL);

        assertEquals(Tracao.INTEGRAL, veiculo.getTracao(), "O tipo de tração deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a quantidade de assentos")

    void testGetSetQuantAssento() {

        veiculo.setQuantAssento(7);

        assertEquals(7, veiculo.getQuantAssento(), "A quantidade de assentos deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de airbag")

    void testGetSetAirBag() {

        veiculo.setAirBag(false);

        assertFalse(veiculo.isAirBag(), "O status de airbag deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o caminho da foto")

    void testGetSetCaminhoFoto() {

        String novoCaminho = "/novo/caminho/foto.png";

        veiculo.setCaminhoFoto(novoCaminho);

        assertEquals(novoCaminho, veiculo.getCaminhoFoto(), "O caminho da foto deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter a potência")

    void testGetSetPotencia() {

        veiculo.setPotencia(200.0);

        assertEquals(200.0, veiculo.getPotencia(), "A potência deve ser atualizada e retornada corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de vidro elétrico")

    void testGetSetVidroEletrico() {

        veiculo.setVidroEletrico(false);

        assertFalse(veiculo.isVidroEletrico(), "O status de vidro elétrico deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de ar condicionado")

    void testGetSetArCondicionado() {

        veiculo.setArCondicionado(false);

        assertFalse(veiculo.isArCondicionado(), "O status de ar condicionado deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de multimídia")

    void testGetSetMultimidia() {

        veiculo.setMultimidia(false);

        assertFalse(veiculo.isMultimidia(), "O status de multimídia deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de entrada USB")

    void testGetSetEntradaUSB() {

        veiculo.setEntradaUSB(false);

        assertFalse(veiculo.isEntradaUSB(), "O status de entrada USB deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de vidro fumê")

    void testGetSetVidroFume() {

        veiculo.setVidroFume(true);

        assertTrue(veiculo.isVidroFume(), "O status de vidro fumê deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o peso")

    void testGetSetPeso() {

        veiculo.setPeso(1500.0);

        assertEquals(1500.0, veiculo.getPeso(), "O peso deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de engate")

    void testGetSetEngate() {

        veiculo.setEngate(true);

        assertTrue(veiculo.isEngate(), "O status de engate deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o status de direção hidráulica")

    void testGetSetDirecaoHidraulica() {

        veiculo.setDirecaoHidraulica(false);

        assertFalse(veiculo.isDirecaoHidraulica(), "O status de direção hidráulica deve ser atualizado e retornado corretamente.");

    }



    @Test

    @DisplayName("Deve permitir alterar e obter o valor diário")

    void testGetSetValorDiario() {

        veiculo.setValorDiario(300.0);

        assertEquals(300.0, veiculo.getValorDiario(), "O valor diário deve ser atualizado e retornado corretamente.");

    }

}