package entities;

public class Endereco {
    private String cidade;
    private String estado;
    private String bairro;
    private String rua;
    private int numero;
    private String cep;

    public Endereco(String cidade, String estado, String bairro, String rua, int numero, String cep) {
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.cep = cep;
    }
}
