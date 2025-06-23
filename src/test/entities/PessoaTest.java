package entities;

import enums.Sexo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe Pessoa")
class PessoaTest {

    private PessoaImpl pessoa;
    private Endereco enderecoTeste;
    private LocalDateTime dataNascimentoTeste;

    @BeforeEach
    void setUp() {
        enderecoTeste = new Endereco("Cidade Teste", "Estado Teste", "Bairro Teste", "Rua Teste", 123, "12345-678");
        dataNascimentoTeste = LocalDateTime.of(1990, 5, 10, 0, 0);

        pessoa = new PessoaImpl(
                "Nome Teste",
                "123.456.789-00",
                "99999-9999",
                "teste@email.com",
                "senha123",
                enderecoTeste,
                dataNascimentoTeste,
                Sexo.MASCULINO,
                "/caminho/foto.png"
        );
    }

    @Test
    @DisplayName("Deve inicializar a pessoa com os valores corretos via construtor")
    void testConstructorInitialization() {
        assertNotNull(pessoa, "O objeto pessoa não deve ser nulo.");
        assertEquals("Nome Teste", pessoa.getNome(), "O nome deve ser inicializado corretamente.");
        assertEquals("123.456.789-00", pessoa.getCpf(), "O CPF deve ser inicializado corretamente.");
        assertEquals("99999-9999", pessoa.getTelefone(), "O telefone deve ser inicializado corretamente.");
        assertEquals("teste@email.com", pessoa.getEmail(), "O email deve ser inicializado corretamente.");
        assertEquals("senha123", pessoa.getSenha(), "A senha deve ser inicializada corretamente.");
        assertEquals(enderecoTeste, pessoa.getEndereco(), "O endereço deve ser inicializado corretamente.");
        assertEquals(dataNascimentoTeste, pessoa.getDataNascimento(), "A data de nascimento deve ser inicializada corretamente.");
        assertEquals(Sexo.MASCULINO, pessoa.getSexo(), "O sexo deve ser inicializado corretamente.");
        assertEquals("/caminho/foto.png", pessoa.getCaminhoFoto(), "O caminho da foto deve ser inicializado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o nome")
    void testGetSetNome() {
        String novoNome = "Novo Nome da Pessoa";
        pessoa.setNome(novoNome);
        assertEquals(novoNome, pessoa.getNome(), "O nome deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o CPF")
    void testGetSetCpf() {
        String novoCpf = "987.654.321-99";
        pessoa.setCpf(novoCpf);
        assertEquals(novoCpf, pessoa.getCpf(), "O CPF deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o telefone")
    void testGetSetTelefone() {
        String novoTelefone = "88888-8888";
        pessoa.setTelefone(novoTelefone);
        assertEquals(novoTelefone, pessoa.getTelefone(), "O telefone deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o email")
    void testGetSetEmail() {
        String novoEmail = "novo@email.com";
        pessoa.setEmail(novoEmail);
        assertEquals(novoEmail, pessoa.getEmail(), "O email deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a senha")
    void testGetSetSenha() {
        String novaSenha = "nova_senha_forte";
        pessoa.setSenha(novaSenha);
        assertEquals(novaSenha, pessoa.getSenha(), "A senha deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o endereço")
    void testGetSetEndereco() {
        Endereco novoEndereco = new Endereco("Nova Cidade", "Novo Estado", "Novo Bairro", "Nova Rua", 456, "54321-098");
        pessoa.setEndereco(novoEndereco);
        assertEquals(novoEndereco, pessoa.getEndereco(), "O endereço deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter a data de nascimento")
    void testGetSetDataNascimento() {
        LocalDateTime novaDataNascimento = LocalDateTime.of(2000, 1, 1, 0, 0);
        pessoa.setDataNascimento(novaDataNascimento);
        assertEquals(novaDataNascimento, pessoa.getDataNascimento(), "A data de nascimento deve ser atualizada e retornada corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o sexo")
    void testGetSetSexo() {
        pessoa.setSexo(Sexo.FEMININO);
        assertEquals(Sexo.FEMININO, pessoa.getSexo(), "O sexo deve ser atualizado e retornado corretamente.");
    }

    @Test
    @DisplayName("Deve permitir alterar e obter o caminho da foto")
    void testGetSetCaminhoFoto() {
        String novoCaminhoFoto = "/novo/caminho/foto2.png";
        pessoa.setCaminhoFoto(novoCaminhoFoto);
        assertEquals(novoCaminhoFoto, pessoa.getCaminhoFoto(), "O caminho da foto deve ser atualizado e retornado corretamente.");
    }
}