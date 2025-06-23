package entities;

import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Carro")
class CarroTest {

    private Carro carro;

    @BeforeEach
    void setUp() {

        carro = new Carro(
                "Carro Esportivo de Alta Performance",
                "ESP1234",
                "MarcaCarro",
                "NomeCarro",
                "ModeloEsportivo",
                2023,
                Cor.VERMELHO,
                Funcao.ALTO_DESEMPENHO,
                15000.0,
                2,
                8.0,
                280.0,
                true,
                Combustivel.GASOLINA,
                Tracao.TRASEIRA,
                2,
                true,
                "/caminho/foto_carro.jpg",
                300.0,
                true,
                true,
                true,
                true,
                false,
                1400.0,
                false,
                true,
                500.0,
                4,
                true
        );
    }

    @Test
    @DisplayName("Deve inicializar Carro com os valores específicos e herdados corretos")
    void testCarroConstructorInitialization() {
        assertNotNull(carro, "O objeto carro não deve ser nulo.");

        assertEquals(4, carro.getPortas(), "O número de portas deve ser inicializado corretamente.");
        assertTrue(carro.isAerofolio(), "O status de aerofólio deve ser inicializado como verdadeiro.");

        assertEquals("ESP1234", carro.getPlaca(), "A placa herdada deve ser inicializada corretamente.");
        assertEquals("MarcaCarro", carro.getMarca(), "A marca herdada deve ser inicializada corretamente.");
        assertEquals(500.0, carro.getValorDiario(), "O valor diário herdado deve ser inicializado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o número de portas")
    void testGetSetPortas() {
        int novasPortas = 2;
        carro.setPortas(novasPortas);
        assertEquals(novasPortas, carro.getPortas(), "O número de portas deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o status do aerofólio")
    void testGetSetAerofolio() {
        carro.setAerofolio(false);
        assertFalse(carro.isAerofolio(), "O status de aerofólio deve ser atualizado e retornado corretamente.");
    }
}
