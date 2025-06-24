package entities;

import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Caminhao")
class CaminhaoTest {

    private Caminhao caminhao;

    @BeforeEach
    void setUp() {

        caminhao = new Caminhao(
                "Caminhão de Carga Pesada",
                "CAM9876",
                "MarcaCaminhao",
                "NomeCaminhao",
                "ModeloCaminhao",
                2022,
                Cor.BRANCO,
                Funcao.CARGA,
                50000.0,
                2,
                3.5,
                120.0,
                true,
                Combustivel.DIESEL,
                Tracao.INTEGRAL,
                3,
                true,
                "/caminho/foto_caminhao.jpg",
                400.0,
                true,
                true,
                false,
                true,
                true,
                8000.0,
                true,
                true,
                800.0,
                20000.0,
                4.0,
                2.8,
                10.5,
                Vagao.BAU_SECO
        );
    }

    @Test
    @DisplayName("Deve inicializar Caminhao com os valores específicos e herdados corretos")
    void testCaminhaoConstructorInitialization() {
        assertNotNull(caminhao, "O objeto caminhao não deve ser nulo.");
        assertEquals(20000.0, caminhao.getCargaMaxima(), "A carga máxima deve ser inicializada corretamente.");
        assertEquals(4.0, caminhao.getAltura(), "A altura deve ser inicializada corretamente.");
        assertEquals(2.8, caminhao.getLargura(), "A largura deve ser inicializada corretamente.");
        assertEquals(10.5, caminhao.getComprimento(), "O comprimento deve ser inicializado corretamente.");
        assertEquals(Vagao.BAU_SECO, caminhao.getTipoVagao(), "O tipo de vagão deve ser inicializado corretamente.");
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
