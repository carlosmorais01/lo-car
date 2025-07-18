package entities;

import enums.Combustivel;
import enums.Cor;
import enums.Funcao;
import enums.Tracao;

public class Moto extends Veiculo {
    private int cilindradas;
    private boolean portaCarga;
    private int raioPneu;

    public Moto(String descricao, String placa, String marca, String nome, String modelo, int ano, Cor cor, Funcao funcao, double quilometragem, int numeroPassageiros, double consumoCombustivelPLitro, double velocidadeMax, boolean automatico, Combustivel combustivel, Tracao tracao, int quantAssento, boolean airBag, String caminhoFoto, double potencia, boolean vidroEletrico, boolean arCondicionado, boolean multimidia, boolean entradaUSB, boolean vidroFume, double peso, boolean engate, boolean direcaoHidraulica, double valorDiario, int cilindradas, boolean portaCarga, int raioPneu) {
        super(descricao, placa, marca, nome, modelo, ano, cor, funcao, quilometragem, numeroPassageiros, consumoCombustivelPLitro, velocidadeMax, automatico, combustivel, tracao, quantAssento, airBag, caminhoFoto, potencia, vidroEletrico, arCondicionado, multimidia, entradaUSB, vidroFume, peso, engate, direcaoHidraulica, valorDiario);
        this.cilindradas = cilindradas;
        this.portaCarga = portaCarga;
        this.raioPneu = raioPneu;
    }

    public int getCilindradas() {
        return cilindradas;
    }

    public void setCilindradas(int cilindradas) {
        this.cilindradas = cilindradas;
    }

    public boolean isPortaCarga() {
        return portaCarga;
    }

    public void setPortaCarga(boolean portaCarga) {
        this.portaCarga = portaCarga;
    }

    public int getRaioPneu() {
        return raioPneu;
    }

    public void setRaioPneu(int raioPneu) {
        this.raioPneu = raioPneu;
    }
}
