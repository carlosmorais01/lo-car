package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Locacao implements Serializable {
    private static final long serialVersionUID = 1L;

    private double valorLocacao;
    private LocalDateTime dataLocacao;
    private LocalDateTime dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao;
    private Veiculo veiculo;
    private Cliente cliente;

    public Locacao(LocalDateTime dataLocacao, LocalDateTime dataPrevistaDevolucao, Veiculo veiculo, Cliente cliente) {
        this.dataLocacao = dataLocacao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = null;
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.valorLocacao = calcularValorPrevisto();
    }

    private long calcularNumeroDeDiasParaCobranca(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            return 0;
        }

        long horas = ChronoUnit.HOURS.between(inicio, fim);
        return Math.max(1, (long) Math.ceil(horas / 24.0));
    }

    public double calcularValorTotal() {
        if (dataDevolucao == null) {
            return calcularValorPrevisto();
        }

        if (veiculo == null || dataLocacao == null || dataPrevistaDevolucao == null) {
            throw new IllegalStateException("Veículo ou datas não podem ser nulos para cálculo do valor total.");
        }

        if (dataDevolucao.isBefore(dataLocacao)) {
            throw new IllegalStateException("Data de devolução não pode ser anterior à data de locação.");
        }

        long diasPrevistos = calcularNumeroDeDiasParaCobranca(dataLocacao, dataPrevistaDevolucao);
        long diasEfetivos = calcularNumeroDeDiasParaCobranca(dataLocacao, dataDevolucao);

        long diasParaCobrar = Math.max(diasPrevistos, diasEfetivos);
        double valorDiarias = veiculo.getValorDiario() * diasParaCobrar;

        if (dataDevolucao.isAfter(dataPrevistaDevolucao)) {
            return valorDiarias + calcularMulta();
        }

        return valorDiarias;
    }


    public double calcularValorPrevisto() {
        if (veiculo == null || dataLocacao == null || dataPrevistaDevolucao == null) {
            throw new IllegalStateException("Veículo ou datas não podem ser nulos para cálculo do valor previsto.");
        }

        long diasPrevistos = calcularNumeroDeDiasParaCobranca(dataLocacao, dataPrevistaDevolucao);
        return veiculo.getValorDiario() * diasPrevistos;
    }


    public double calcularMulta() {
        if (dataPrevistaDevolucao == null || dataDevolucao == null) {
            return 0;
        }

        if (dataDevolucao.isBefore(dataPrevistaDevolucao)) {
            return 0;
        }

        long horasAtraso = ChronoUnit.HOURS.between(dataPrevistaDevolucao, dataDevolucao);

        if (horasAtraso < 3) {
            return 0;
        }

        int blocosDeTresHoras = (int) Math.ceil((double) horasAtraso / 3.0);
        return blocosDeTresHoras * veiculo.getValorDiario();
    }



    public double getValorLocacao() {
        return valorLocacao;
    }

    public void setValorLocacao(double valorLocacao) {
        this.valorLocacao = valorLocacao;
    }

    public LocalDateTime getDataLocacao() {
        return dataLocacao;
    }

    public void setDataLocacao(LocalDateTime dataLocacao) {
        this.dataLocacao = dataLocacao;
    }

    public LocalDateTime getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDateTime dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
        this.valorLocacao = calcularValorTotal();
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}