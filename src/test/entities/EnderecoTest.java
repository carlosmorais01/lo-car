package entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Endereco")
class EnderecoTest {

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco(
                "Goiânia",
                "GO",
                "Setor Central",
                "Rua 1",
                100,
                "74000-000"
        );
    }

    @Test
    @DisplayName("Deve inicializar o endereço com os valores corretos via construtor")
    void testConstructorInitialization() {
        assertNotNull(endereco, "O objeto endereço não deve ser nulo.");
        assertEquals("Goiânia", endereco.getCidade(), "A cidade deve ser inicializada corretamente.");
        assertEquals("GO", endereco.getEstado(), "O estado deve ser inicializado corretamente.");
        assertEquals("Setor Central", endereco.getBairro(), "O bairro deve ser inicializado corretamente.");
        assertEquals("Rua 1", endereco.getRua(), "A rua deve ser inicializada corretamente.");
        assertEquals(100, endereco.getNumero(), "O número deve ser inicializado corretamente.");
        assertEquals("74000-000", endereco.getCep(), "O CEP deve ser inicializado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a cidade")
    void testGetSetCidade() {
        String novaCidade = "Anápolis";
        endereco.setCidade(novaCidade);
        assertEquals(novaCidade, endereco.getCidade(), "A cidade deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o estado")
    void testGetSetEstado() {
        String novoEstado = "DF";
        endereco.setEstado(novoEstado);
        assertEquals(novoEstado, endereco.getEstado(), "O estado deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o bairro")
    void testGetSetBairro() {
        String novoBairro = "Setor Sul";
        endereco.setBairro(novoBairro);
        assertEquals(novoBairro, endereco.getBairro(), "O bairro deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a rua")
    void testGetSetRua() {
        String novaRua = "Avenida T-6";
        endereco.setRua(novaRua);
        assertEquals(novaRua, endereco.getRua(), "A rua deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o número")
    void testGetSetNumero() {
        int novoNumero = 250;
        endereco.setNumero(novoNumero);
        assertEquals(novoNumero, endereco.getNumero(), "O número deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o CEP")
    void testGetSetCep() {
        String novoCep = "75000-123";
        endereco.setCep(novoCep);
        assertEquals(novoCep, endereco.getCep(), "O CEP deve ser atualizado e retornado corretamente.");
    }
}