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

    public Locacao(LocalDateTime dataLocacao, LocalDateTime dataPrevistaDevolucao, LocalDateTime dataDevolucao, Veiculo veiculo, Cliente cliente) {
        this.dataLocacao = dataLocacao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.valorLocacao = calcularValorTotal();
    }

    public Locacao(LocalDateTime dataLocacao, LocalDateTime dataPrevistaDevolucao, Veiculo veiculo, Cliente cliente) {
        this.dataLocacao = dataLocacao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = null;
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.valorLocacao = calcularValorPrevisto();
    }

    private long calcularNumeroDeDiasParaCobranca(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            return 0;
        }

        long diasCompletos = ChronoUnit.DAYS.between(inicio.toLocalDate(), fim.toLocalDate());

        if (diasCompletos == 0 && inicio.isBefore(fim)) {
            return 1;
        }

        double horas = ChronoUnit.HOURS.between(inicio, fim);
        long diasArredondadosParaCima = (long) Math.ceil(horas / 24.0);

        return Math.max(1, diasArredondadosParaCima);
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

        long diasEfetivos = calcularNumeroDeDiasParaCobranca(dataLocacao, dataDevolucao);
        double valorDiarias = veiculo.getValorDiario() * diasEfetivos;

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