package entities;

import entities.enums.*;

public class Caminhao extends Veiculo {
    private double cargaMaxima;
    private double altura;
    private double largura;
    private double comprimento;
    private Vagao tipoVagao;

    public Caminhao(String descricao, String placa, String marca, String nome, String modelo, int ano, Cor cor, Funcao funcao, double quilometragem, int numeroPassageiros, double consumoCombustivelPLitro, double velocidadeMax, boolean automatico, Combustivel combustivel, Tracao tracao, int quantAssento, boolean airBag, String caminhoFoto, double potencia, boolean vidroEletrico, boolean arCondicionado, boolean multimidia, boolean entradaUSB, boolean vidroFume, double peso, boolean engate, boolean direcaoHidraulica, double valorDiario, double cargaMaxima, double altura, double largura, double comprimento, Vagao tipoVagao) {
        super(descricao, placa, marca, nome, modelo, ano, cor, funcao, quilometragem, numeroPassageiros, consumoCombustivelPLitro, velocidadeMax, automatico, combustivel, tracao, quantAssento, airBag, caminhoFoto, potencia, vidroEletrico, arCondicionado, multimidia, entradaUSB, vidroFume, peso, engate, direcaoHidraulica, valorDiario);
        this.cargaMaxima = cargaMaxima;
        this.altura = altura;
        this.largura = largura;
        this.comprimento = comprimento;
        this.tipoVagao = tipoVagao;
    }

    public double getCargaMaxima() {
        return cargaMaxima;
    }

    public void setCargaMaxima(double cargaMaxima) {
        this.cargaMaxima = cargaMaxima;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getLargura() {
        return largura;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }

    public double getComprimento() {
        return comprimento;
    }

    public void setComprimento(double comprimento) {
        this.comprimento = comprimento;
    }

    public Vagao getTipoVagao() {
        return tipoVagao;
    }

    public void setTipoVagao(Vagao tipoVagao) {
        this.tipoVagao = tipoVagao;
    }
}
