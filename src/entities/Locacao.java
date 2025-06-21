package entities;

import java.io.Serializable; // Importar Serializable
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Locacao implements Serializable { // Implementar Serializable
    private static final long serialVersionUID = 1L; // Adicionar serialVersionUID

    private double valorLocacao;
    private LocalDateTime dataLocacao;
    private LocalDateTime dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao; // Pode ser null se ainda não devolvido
    private Veiculo veiculo;
    private Cliente cliente;

    // Construtor completo
    public Locacao(LocalDateTime dataLocacao, LocalDateTime dataPrevistaDevolucao, LocalDateTime dataDevolucao, Veiculo veiculo, Cliente cliente) {
        this.dataLocacao = dataLocacao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.veiculo = veiculo;
        this.cliente = cliente;
        // O valor da locação só deve ser calculado quando a locação for finalizada ou se todas as datas estiverem presentes
        // Ou, se for uma locação ativa, calcular o valor previsto
        this.valorLocacao = calcularValorTotal(); // Considerar quando este valor é calculado.
    }

    // Sobrecarga de construtor para locações ativas (sem dataDevolucao inicial)
    public Locacao(LocalDateTime dataLocacao, LocalDateTime dataPrevistaDevolucao, Veiculo veiculo, Cliente cliente) {
        this.dataLocacao = dataLocacao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = null; // Inicializa como null para locações ativas
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.valorLocacao = calcularValorPrevisto(); // Calcula o valor previsto para locações ativas
    }

    public double calcularValorTotal() {
        // Se dataDevolucao for nula, significa que a locação ainda está ativa,
        // então não podemos calcular o valor total definitivo ainda.
        // Neste caso, calculamos o valor previsto.
        if (dataDevolucao == null) {
            return calcularValorPrevisto();
        }

        if (veiculo == null || dataLocacao == null || dataPrevistaDevolucao == null) {
            throw new IllegalStateException("Veículo ou datas não podem ser nulos para cálculo do valor total.");
        }

        if (dataDevolucao.isBefore(dataLocacao)) {
            throw new IllegalStateException("Data de devolução não pode ser anterior à data de locação.");
        }

        long diasPrevistos = ChronoUnit.DAYS.between(dataLocacao, dataPrevistaDevolucao);
        if (diasPrevistos == 0) {
            diasPrevistos = 1; // Mínimo de 1 dia para cálculo da diária
        }

        double valorDiarias = veiculo.getValorDiario() * diasPrevistos;

        if (dataDevolucao.isAfter(dataPrevistaDevolucao)) {
            // Calcula dias de atraso e multa
            return valorDiarias + calcularMulta();
        }

        return valorDiarias;
    }

    // Novo método para calcular o valor previsto para locações ativas
    public double calcularValorPrevisto() {
        if (veiculo == null || dataLocacao == null || dataPrevistaDevolucao == null) {
            throw new IllegalStateException("Veículo ou datas não podem ser nulos para cálculo do valor previsto.");
        }
        long dias = ChronoUnit.DAYS.between(dataLocacao, dataPrevistaDevolucao);
        if (dias == 0) {
            dias = 1; // Mínimo de 1 dia
        }
        return veiculo.getValorDiario() * (int) dias;
    }


    public double calcularMulta() {
        if (dataPrevistaDevolucao == null || dataDevolucao == null) {
            return 0; // Não há multa se as datas não estiverem definidas
        }

        if (dataDevolucao.isBefore(dataPrevistaDevolucao)) {
            return 0; // Não há multa se a devolução foi antes ou na data prevista
        }

        long horasAtraso = ChronoUnit.HOURS.between(dataPrevistaDevolucao, dataDevolucao);
        if (horasAtraso < 3) {
            return 0; // Sem multa para menos de 3 horas de atraso
        }
        // Cada bloco de 3 horas de atraso custa uma diária completa
        int blocosDeTresHoras = (int) Math.ceil((double) horasAtraso / 3);
        return blocosDeTresHoras * veiculo.getValorDiario();
    }

    // Getters e Setters (já existentes)
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
        // Recalcular o valor da locação quando a data de devolução for definida
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