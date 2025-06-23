package entities;

import enums.Sexo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Funcionario")
class FuncionarioTest {

    private Endereco enderecoTeste;
    private LocalDateTime dataNascimentoTeste;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        enderecoTeste = new Endereco("Cidade Func", "Estado Func", "Bairro Func", "Rua Func", 789, "98765-432");
        dataNascimentoTeste = LocalDateTime.of(1988, 1, 1, 0, 0);


        Field ultimoIdField = Funcionario.class.getDeclaredField("ultimoId");
        ultimoIdField.setAccessible(true);
        ultimoIdField.set(null, 0);
    }

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {


        Field ultimoIdField = Funcionario.class.getDeclaredField("ultimoId");
        ultimoIdField.setAccessible(true);
        ultimoIdField.set(null, 0);
    }


    @Test
    @DisplayName("Deve inicializar Funcionario com ID crescente e outros valores corretos")
    void testConstructorInitializationAndIdGeneration() {
        Funcionario func1 = new Funcionario(
                "Funcionario Um",
                "222.333.444-55",
                "77777-7777",
                "func1@email.com",
                "senhaFunc1",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.MASCULINO,
                "/caminho/foto_func1.png"
        );

        Funcionario func2 = new Funcionario(
                "Funcionario Dois",
                "333.444.555-66",
                "66666-6666",
                "func2@email.com",
                "senhaFunc2",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.FEMININO,
                "/caminho/foto_func2.png"
        );

        assertNotNull(func1, "O objeto func1 não deve ser nulo.");
        assertEquals("Funcionario Um", func1.getNome(), "O nome herdado deve ser inicializado corretamente.");
        assertEquals(1, func1.getId(), "O ID do primeiro funcionário deve ser 1.");

        assertNotNull(func2, "O objeto func2 não deve ser nulo.");
        assertEquals("Funcionario Dois", func2.getNome(), "O nome herdado deve ser inicializado corretamente.");
        assertEquals(2, func2.getId(), "O ID do segundo funcionário deve ser 2.");

        assertNotEquals(func1.getId(), func2.getId(), "Os IDs dos funcionários devem ser únicos.");
    }

    @Test
    @DisplayName("Deve permitir obter e alterar o ID")
    void testGetSetId() {
        Funcionario func = new Funcionario(
                "Funcionario Teste",
                "444.555.666-77",
                "55555-5555",
                "funcTeste@email.com",
                "senhaFuncTeste",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.MASCULINO,
                "/caminho/foto_funcTeste.png"
        );

        assertEquals(1, func.getId(), "O ID inicial deve ser 1.");

        func.setId(99);
        assertEquals(99, func.getId(), "O ID deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve herdar os getters e setters da classe Pessoa")
    void testInheritedPessoaMethods() {
        Funcionario func = new Funcionario(
                "Herdeiro Pessoa",
                "555.666.777-88",
                "44444-4444",
                "herdeiro@email.com",
                "senhaHerdeiro",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.MASCULINO,
                "/caminho/foto_herdeiro.png"
        );

        assertEquals("Herdeiro Pessoa", func.getNome(), "O nome herdado deve ser correto.");
        String novoTelefone = "11111-1111";
        func.setTelefone(novoTelefone);
        assertEquals(novoTelefone, func.getTelefone(), "O telefone herdado deve ser atualizado corretamente.");
        assertEquals(Sexo.MASCULINO, func.getSexo(), "O sexo herdado deve ser correto.");
    }
}
