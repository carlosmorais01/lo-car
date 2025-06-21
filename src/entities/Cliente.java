package entities;

import enums.Sexo;
import java.io.Serializable; // Importar Serializable
import java.time.LocalDateTime;

public class Cliente extends Pessoa implements Serializable { // Adicionar implements Serializable
    private static final long serialVersionUID = 1L; // Adicionar serialVersionUID
    private double saldo;

    public Cliente(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo);
        this.saldo = 0;
    }

    public void adicionarSaldo(double valor) {
        this.saldo += valor;
    }

    public void resgatarSaldo(double valor) {
        this.saldo -= valor;
    }

    public double getSaldo() {
        return saldo;
    }
}