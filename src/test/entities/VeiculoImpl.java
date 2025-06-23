package entities;



import enums.*;


public class VeiculoImpl extends Veiculo {



    public VeiculoImpl(String descricao, String placa, String marca, String nome, String modelo, int ano, Cor cor, Funcao funcao, double quilometragem, int numeroPassageiros, double consumoCombustivelPLitro, double velocidadeMax, boolean automatico, Combustivel combustivel, Tracao tracao, int quantAssento, boolean airBag, String caminhoFoto, double potencia, boolean vidroEletrico, boolean arCondicionado, boolean multimidia, boolean entradaUSB, boolean vidroFume, double peso, boolean engate, boolean direcaoHidraulica, double valorDiario) {

        super(descricao, placa, marca, nome, modelo, ano, cor, funcao, quilometragem, numeroPassageiros, consumoCombustivelPLitro, velocidadeMax, automatico, combustivel, tracao, quantAssento, airBag, caminhoFoto, potencia, vidroEletrico, arCondicionado, multimidia, entradaUSB, vidroFume, peso, engate, direcaoHidraulica, valorDiario);

    }

}


