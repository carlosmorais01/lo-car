package entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Locacao {
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

    public double calcularValorTotal() {
        if (veiculo == null || dataLocacao == null || dataDevolucao == null || dataPrevistaDevolucao == null) {
            throw new IllegalStateException("Veículo ou datas não podem ser nulos");
        }

        if (dataDevolucao.isBefore(dataLocacao)) {
            throw new IllegalStateException("Data de devolução não pode ser anterior à data de locação");
        }

        long dias = ChronoUnit.DAYS.between(dataLocacao, dataPrevistaDevolucao);
        if (dias == 0) {
            dias = 1; // Mínimo de 1 dia
        }

        if (dataDevolucao.isAfter(dataPrevistaDevolucao)) {
            return (veiculo.getValorDiario() * (int) dias) + calcularMulta();
        }

        return veiculo.getValorDiario() * (int) dias;
    }

    public double calcularMulta() {
        long horas = ChronoUnit.HOURS.between(dataPrevistaDevolucao, dataDevolucao);
        if (horas < 3) {
            return 0;
        }
        int horasTaxa = (int) Math.ceil((double)horas/3);
        return horasTaxa * veiculo.getValorDiario();
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

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
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
