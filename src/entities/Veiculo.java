package entities;

import enums.Combustivel;
import enums.Cor;
import enums.Funcao;
import enums.Tracao;

import java.io.Serial;
import java.io.Serializable;

public abstract class Veiculo implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String descricao;
    private String placa;
    private String marca;
    private String nome;
    private String modelo;
    private int ano;
    private Cor cor;
    private Funcao funcao;
    private double quilometragem;
    private int numeroPassageiros;
    private double consumoCombustivelPLitro;
    private double velocidadeMax;
    private boolean automatico;
    private Combustivel combustivel;
    private Tracao tracao;
    private int quantAssento;
    private boolean airBag;
    private String caminhoFoto;
    private double potencia;
    private boolean vidroEletrico;
    private boolean arCondicionado;
    private boolean multimidia;
    private boolean entradaUSB;
    private boolean vidroFume;
    private double peso;
    private boolean engate;
    private boolean direcaoHidraulica;
    private double valorDiario;
    private int locacoes;

    public Veiculo(String descricao, String placa, String marca, String nome, String modelo, int ano, Cor cor, Funcao funcao, double quilometragem, int numeroPassageiros, double consumoCombustivelPLitro, double velocidadeMax, boolean automatico, Combustivel combustivel, Tracao tracao, int quantAssento, boolean airBag, String caminhoFoto, double potencia, boolean vidroEletrico, boolean arCondicionado, boolean multimidia, boolean entradaUSB, boolean vidroFume, double peso, boolean engate, boolean direcaoHidraulica, double valorDiario) {
        this.descricao = descricao;
        this.placa = placa;
        this.marca = marca;
        this.nome = nome;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.funcao = funcao;
        this.quilometragem = quilometragem;
        this.numeroPassageiros = numeroPassageiros;
        this.consumoCombustivelPLitro = consumoCombustivelPLitro;
        this.velocidadeMax = velocidadeMax;
        this.automatico = automatico;
        this.combustivel = combustivel;
        this.tracao = tracao;
        this.quantAssento = quantAssento;
        this.airBag = airBag;
        this.caminhoFoto = caminhoFoto;
        this.potencia = potencia;
        this.vidroEletrico = vidroEletrico;
        this.arCondicionado = arCondicionado;
        this.multimidia = multimidia;
        this.entradaUSB = entradaUSB;
        this.vidroFume = vidroFume;
        this.peso = peso;
        this.engate = engate;
        this.direcaoHidraulica = direcaoHidraulica;
        this.valorDiario = valorDiario;
        this.locacoes = 0;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public double getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(double quilometragem) {
        this.quilometragem = quilometragem;
    }

    public int getNumeroPassageiros() {
        return numeroPassageiros;
    }

    public void setNumeroPassageiros(int numeroPassageiros) {
        this.numeroPassageiros = numeroPassageiros;
    }

    public double getConsumoCombustivelPLitro() {
        return consumoCombustivelPLitro;
    }

    public void setConsumoCombustivelPLitro(double consumoCombustivelPLitro) {
        this.consumoCombustivelPLitro = consumoCombustivelPLitro;
    }

    public double getVelocidadeMax() {
        return velocidadeMax;
    }

    public void setVelocidadeMax(double velocidadeMax) {
        this.velocidadeMax = velocidadeMax;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }

    public Combustivel getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(Combustivel combustivel) {
        this.combustivel = combustivel;
    }

    public Tracao getTracao() {
        return tracao;
    }

    public void setTracao(Tracao tracao) {
        this.tracao = tracao;
    }

    public int getQuantAssento() {
        return quantAssento;
    }

    public void setQuantAssento(int quantAssento) {
        this.quantAssento = quantAssento;
    }

    public boolean isAirBag() {
        return airBag;
    }

    public void setAirBag(boolean airBag) {
        this.airBag = airBag;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    public boolean isVidroEletrico() {
        return vidroEletrico;
    }

    public void setVidroEletrico(boolean vidroEletrico) {
        this.vidroEletrico = vidroEletrico;
    }

    public boolean isArCondicionado() {
        return arCondicionado;
    }

    public void setArCondicionado(boolean arCondicionado) {
        this.arCondicionado = arCondicionado;
    }

    public boolean isMultimidia() {
        return multimidia;
    }

    public void setMultimidia(boolean multimidia) {
        this.multimidia = multimidia;
    }

    public boolean isEntradaUSB() {
        return entradaUSB;
    }

    public void setEntradaUSB(boolean entradaUSB) {
        this.entradaUSB = entradaUSB;
    }

    public boolean isVidroFume() {
        return vidroFume;
    }

    public void setVidroFume(boolean vidroFume) {
        this.vidroFume = vidroFume;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public boolean isEngate() {
        return engate;
    }

    public void setEngate(boolean engate) {
        this.engate = engate;
    }

    public boolean isDirecaoHidraulica() {
        return direcaoHidraulica;
    }

    public void setDirecaoHidraulica(boolean direcaoHidraulica) {
        this.direcaoHidraulica = direcaoHidraulica;
    }

    public double getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(double valorDiario) {
        this.valorDiario = valorDiario;
    }

    public int getLocacoes() {
        return locacoes;
    }

    public void setLocacoes(int locacoes) {
        this.locacoes = locacoes;
    }
}