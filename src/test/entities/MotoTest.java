package entities;

import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Moto")
class MotoTest {

    private Moto moto;

    @BeforeEach
    void setUp() {
        moto = new Moto(
                "Moto de Rua Esportiva",
                "MOTO5678",
                "MarcaMoto",
                "NomeMoto",
                "ModeloEsportiva",
                2021,
                Cor.PRETO,
                Funcao.PASSEIO,
                8000.0,
                2,
                20.0,
                200.0,
                false,
                Combustivel.GASOLINA,
                Tracao.TRASEIRA,
                2,
                false,
                "/caminho/foto_moto.jpg",
                80.0,
                false,
                false,
                true,
                true,
                false,
                180.0,
                false,
                false,
                150.0,
                600,
                true,
                17
        );
    }

    @Test
    @DisplayName("Deve inicializar Moto com os valores específicos e herdados corretos")
    void testMotoConstructorInitialization() {
        assertNotNull(moto, "O objeto moto não deve ser nulo.");
        assertEquals(600, moto.getCilindradas(), "As cilindradas devem ser inicializadas corretamente.");
        assertTrue(moto.isPortaCarga(), "O status de porta carga deve ser inicializado como verdadeiro.");
        assertEquals(17, moto.getRaioPneu(), "O raio do pneu deve ser inicializado corretamente.");
        assertEquals("MOTO5678", moto.getPlaca(), "A placa herdada deve ser inicializada corretamente.");
        assertEquals("MarcaMoto", moto.getMarca(), "A marca herdada deve ser inicializada corretamente.");
        assertEquals(150.0, moto.getValorDiario(), "O valor diário herdado deve ser inicializado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter as cilindradas")
    void testGetSetCilindradas() {
        int novasCilindradas = 750;
        moto.setCilindradas(novasCilindradas);
        assertEquals(novasCilindradas, moto.getCilindradas(), "As cilindradas devem ser atualizadas e retornadas corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o status de porta carga")
    void testGetSetPortaCarga() {
        moto.setPortaCarga(false);
        assertFalse(moto.isPortaCarga(), "O status de porta carga deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o raio do pneu")
    void testGetSetRaioPneu() {
        int novoRaioPneu = 18;
        moto.setRaioPneu(novoRaioPneu);
        assertEquals(novoRaioPneu, moto.getRaioPneu(), "O raio do pneu deve ser atualizado e retornado corretamente.");
    }
}
