package entities;

import enums.Sexo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Cliente")
class ClienteTest {

    private Cliente cliente;
    private Endereco enderecoTeste;
    private LocalDateTime dataNascimentoTeste;

    @BeforeEach
    void setUp() {
        enderecoTeste = new Endereco("Cidade Cliente", "Estado Cliente", "Bairro Cliente", "Rua Cliente", 456, "87654-321");
        dataNascimentoTeste = LocalDateTime.of(1995, 8, 20, 0, 0);

        // Inicializa um objeto Cliente antes de cada teste
        cliente = new Cliente(
                "Cliente Teste",
                "111.222.333-44",
                "88888-8888",
                "cliente@email.com",
                "senhaCliente",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.FEMININO,
                "/caminho/foto_cliente.png"
        );
    }

    @Test
    @DisplayName("Deve inicializar Cliente com saldo zero e outros valores corretos")
    void testConstructorInitialization() {
        assertNotNull(cliente, "O objeto cliente não deve ser nulo.");
        assertEquals("Cliente Teste", cliente.getNome(), "O nome herdado deve ser inicializado corretamente.");
        assertEquals("111.222.333-44", cliente.getCpf(), "O CPF herdado deve ser inicializado corretamente.");
        assertEquals(0.0, cliente.getSaldo(), "O saldo deve ser inicializado como 0.0.");
    }

    @Test
    @DisplayName("Deve adicionar saldo corretamente")
    void testAdicionarSaldo() {
        cliente.adicionarSaldo(100.0);
        assertEquals(100.0, cliente.getSaldo(), "O saldo deve ser incrementado corretamente.");

        cliente.adicionarSaldo(50.50);
        assertEquals(150.50, cliente.getSaldo(), "O saldo deve ser incrementado múltiplas vezes.");
    }

    @Test
    @DisplayName("Deve debitar saldo corretamente")
    void testDebitarSaldo() {
        cliente.adicionarSaldo(200.0); // Garante que há saldo para debitar
        cliente.debitarSaldo(75.0);
        assertEquals(125.0, cliente.getSaldo(), "O saldo deve ser decrementado corretamente.");

        cliente.debitarSaldo(25.50);
        assertEquals(99.50, cliente.getSaldo(), "O saldo deve ser decrementado múltiplas vezes.");
    }

    @Test
    @DisplayName("Deve permitir saldo negativo ao debitar se não houver validação de negócio na classe")
    void testDebitarSaldoPodeFicarNegativo() {
        // Se a regra de negócio permitir saldo negativo, este teste é válido.
        // Caso contrário, a classe Cliente precisaria de validação para impedir saldo negativo.
        cliente.debitarSaldo(50.0); // Saldo inicial 0.0, então ficará -50.0
        assertEquals(-50.0, cliente.getSaldo(), "O saldo deve permitir valores negativos se não houver validação.");
    }

    @Test
    @DisplayName("Deve obter o saldo atual")
    void testGetSaldo() {
        cliente.adicionarSaldo(1000.0);
        assertEquals(1000.0, cliente.getSaldo(), "GetSaldo deve retornar o valor atual do saldo.");
    }
}
