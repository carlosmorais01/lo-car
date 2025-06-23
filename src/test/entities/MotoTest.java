package entities;

import enums.*; // Importar todos os enums necessários
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Moto")
class MotoTest {

    private Moto moto;

    @BeforeEach
    void setUp() {
        // Inicializa um objeto Moto antes de cada teste com valores de exemplo
        moto = new Moto(
                "Moto de Rua Esportiva", // descricao
                "MOTO5678", // placa
                "MarcaMoto", // marca
                "NomeMoto", // nome
                "ModeloEsportiva", // modelo
                2021, // ano
                Cor.PRETO, // cor
                Funcao.PASSEIO, // funcao
                8000.0, // quilometragem
                2, // numeroPassageiros
                20.0, // consumoCombustivelPLitro
                200.0, // velocidadeMax
                false, // automatico
                Combustivel.GASOLINA, // combustivel
                Tracao.TRASEIRA, // tracao
                2, // quantAssento
                false, // airBag
                "/caminho/foto_moto.jpg", // caminhoFoto
                80.0, // potencia
                false, // vidroEletrico
                false, // arCondicionado
                true, // multimidia
                true, // entradaUSB
                false, // vidroFume
                180.0, // peso
                false, // engate
                false, // direcaoHidraulica
                150.0, // valorDiario
                600, // cilindradas (específico de Moto)
                true, // portaCarga (específico de Moto)
                17 // raioPneu (específico de Moto)
        );
    }

    @Test
    @DisplayName("Deve inicializar Moto com os valores específicos e herdados corretos")
    void testMotoConstructorInitialization() {
        assertNotNull(moto, "O objeto moto não deve ser nulo.");

        // Testes de atributos específicos de Moto
        assertEquals(600, moto.getCilindradas(), "As cilindradas devem ser inicializadas corretamente.");
        assertTrue(moto.isPortaCarga(), "O status de porta carga deve ser inicializado como verdadeiro.");
        assertEquals(17, moto.getRaioPneu(), "O raio do pneu deve ser inicializado corretamente.");

        // Opcional: Testar alguns atributos herdados para garantir que o construtor base foi chamado corretamente
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
