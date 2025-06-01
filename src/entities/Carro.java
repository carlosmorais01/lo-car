package entities;

import enums.Combustivel;
import enums.Cor;
import enums.Funcao;
import enums.Tracao;

public class Carro extends Veiculo {
    private int portas;
    private boolean aerofolio;

    public Carro(String descricao, String placa, String marca, String nome, String modelo, int ano, Cor cor, Funcao funcao, double quilometragem, int numeroPassageiros, double consumoCombustivelPLitro, double velocidadeMax, boolean automatico, Combustivel combustivel, Tracao tracao, int quantAssento, boolean airBag, String caminhoFoto, double potencia, boolean vidroEletrico, boolean arCondicionado, boolean multimidia, boolean entradaUSB, boolean vidroFume, double peso, boolean engate, boolean direcaoHidraulica, double valorDiario, int portas, boolean aerofolio) {
        super(descricao, placa, marca, nome, modelo, ano, cor, funcao, quilometragem, numeroPassageiros, consumoCombustivelPLitro, velocidadeMax, automatico, combustivel, tracao, quantAssento, airBag, caminhoFoto, potencia, vidroEletrico, arCondicionado, multimidia, entradaUSB, vidroFume, peso, engate, direcaoHidraulica, valorDiario);
        this.portas = portas;
        this.aerofolio = aerofolio;
    }

    public int getPortas() {
        return portas;
    }

    public void setPortas(int portas) {
        this.portas = portas;
    }

    public boolean isAerofolio() {
        return aerofolio;
    }

    public void setAerofolio(boolean aerofolio) {
        this.aerofolio = aerofolio;
    }
}
