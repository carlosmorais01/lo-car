package entities;

import enums.*; // Importar todos os enums necessários
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Caminhao")
class CaminhaoTest {

    private Caminhao caminhao;

    @BeforeEach
    void setUp() {
        // Inicializa um objeto Caminhao antes de cada teste com valores de exemplo
        caminhao = new Caminhao(
                "Caminhão de Carga Pesada", // descricao
                "CAM9876", // placa
                "MarcaCaminhao", // marca
                "NomeCaminhao", // nome
                "ModeloCaminhao", // modelo
                2022, // ano
                Cor.BRANCO, // cor
                Funcao.CARGA, // funcao
                50000.0, // quilometragem
                2, // numeroPassageiros
                3.5, // consumoCombustivelPLitro
                120.0, // velocidadeMax
                true, // automatico
                Combustivel.DIESEL, // combustivel
                Tracao.INTEGRAL, // tracao
                3, // quantAssento
                true, // airBag
                "/caminho/foto_caminhao.jpg", // caminhoFoto
                400.0, // potencia
                true, // vidroEletrico
                true, // arCondicionado
                false, // multimidia
                true, // entradaUSB
                true, // vidroFume
                8000.0, // peso
                true, // engate
                true, // direcaoHidraulica
                800.0, // valorDiario
                20000.0, // cargaMaxima (específico de Caminhao)
                4.0, // altura (específico de Caminhao)
                2.8, // largura (específico de Caminhao)
                10.5, // comprimento (específico de Caminhao)
                Vagao.BAU_SECO // tipoVagao (específico de Caminhao)
        );
    }

    @Test
    @DisplayName("Deve inicializar Caminhao com os valores específicos e herdados corretos")
    void testCaminhaoConstructorInitialization() {
        assertNotNull(caminhao, "O objeto caminhao não deve ser nulo.");

        // Testes de atributos específicos de Caminhao
        assertEquals(20000.0, caminhao.getCargaMaxima(), "A carga máxima deve ser inicializada corretamente.");
        assertEquals(4.0, caminhao.getAltura(), "A altura deve ser inicializada corretamente.");
        assertEquals(2.8, caminhao.getLargura(), "A largura deve ser inicializada corretamente.");
        assertEquals(10.5, caminhao.getComprimento(), "O comprimento deve ser inicializado corretamente.");
        assertEquals(Vagao.BAU_SECO, caminhao.getTipoVagao(), "O tipo de vagão deve ser inicializado corretamente.");

        // Opcional: Testar alguns atributos herdados para garantir que o construtor base foi chamado corretamente
        assertEquals("CAM9876", caminhao.getPlaca(), "A placa herdada deve ser inicializada corretamente.");
        assertEquals(2022, caminhao.getAno(), "O ano herdado deve ser inicializado corretamente.");
        assertEquals(800.0, caminhao.getValorDiario(), "O valor diário herdado deve ser inicializado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a carga máxima")
    void testGetSetCargaMaxima() {
        double novaCargaMaxima = 25000.0;
        caminhao.setCargaMaxima(novaCargaMaxima);
        assertEquals(novaCargaMaxima, caminhao.getCargaMaxima(), "A carga máxima deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a altura")
    void testGetSetAltura() {
        double novaAltura = 4.5;
        caminhao.setAltura(novaAltura);
        assertEquals(novaAltura, caminhao.getAltura(), "A altura deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a largura")
    void testGetSetLargura() {
        double novaLargura = 3.0;
        caminhao.setLargura(novaLargura);
        assertEquals(novaLargura, caminhao.getLargura(), "A largura deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o comprimento")
    void testGetSetComprimento() {
        double novoComprimento = 12.0;
        caminhao.setComprimento(novoComprimento);
        assertEquals(novoComprimento, caminhao.getComprimento(), "O comprimento deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o tipo de vagão")
    void testGetSetTipoVagao() {
        caminhao.setTipoVagao(Vagao.GRANELEIRO);
        assertEquals(Vagao.GRANELEIRO, caminhao.getTipoVagao(), "O tipo de vagão deve ser atualizado e retornado corretamente.");
    }
}
