package entities;

import enums.*; // Importar todos os enums necessários
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Carro")
class CarroTest {

    private Carro carro;

    @BeforeEach
    void setUp() {
        // Inicializa um objeto Carro antes de cada teste com valores de exemplo
        carro = new Carro(
                "Carro Esportivo de Alta Performance", // descricao
                "ESP1234", // placa
                "MarcaCarro", // marca
                "NomeCarro", // nome
                "ModeloEsportivo", // modelo
                2023, // ano
                Cor.VERMELHO, // cor
                Funcao.ALTO_DESEMPENHO, // funcao
                15000.0, // quilometragem
                2, // numeroPassageiros
                8.0, // consumoCombustivelPLitro
                280.0, // velocidadeMax
                true, // automatico
                Combustivel.GASOLINA, // combustivel
                Tracao.TRASEIRA, // tracao
                2, // quantAssento
                true, // airBag
                "/caminho/foto_carro.jpg", // caminhoFoto
                300.0, // potencia
                true, // vidroEletrico
                true, // arCondicionado
                true, // multimidia
                true, // entradaUSB
                false, // vidroFume
                1400.0, // peso
                false, // engate
                true, // direcaoHidraulica
                500.0, // valorDiario
                4, // portas (específico de Carro)
                true // aerofolio (específico de Carro)
        );
    }

    @Test
    @DisplayName("Deve inicializar Carro com os valores específicos e herdados corretos")
    void testCarroConstructorInitialization() {
        assertNotNull(carro, "O objeto carro não deve ser nulo.");

        // Testes de atributos específicos de Carro
        assertEquals(4, carro.getPortas(), "O número de portas deve ser inicializado corretamente.");
        assertTrue(carro.isAerofolio(), "O status de aerofólio deve ser inicializado como verdadeiro.");

        // Opcional: Testar alguns atributos herdados para garantir que o construtor base foi chamado corretamente
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
