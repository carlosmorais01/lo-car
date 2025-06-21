package controller;

import entities.Veiculo;
import entities.Carro;
import entities.Moto;
import entities.Caminhao;
import entities.Locacao; // Importar a classe Locacao
import enums.Cor;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDateTime; // Para trabalhar com datas
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Importar Stream

public class VeiculoController {

    private List<Veiculo> veiculos;
    private List<Locacao> locacoes; // Para armazenar as locações

    public VeiculoController() {
        this.veiculos = carregarTodosVeiculos();
        this.locacoes = carregarLocacoes(); // Carregar as locações ao iniciar
    }

    private List<Veiculo> carregarTodosVeiculos() {
        List<Veiculo> veiculosCarregados = new ArrayList<>();

        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/carros/carros.dat", Carro.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/moto/motos.dat", Moto.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/caminhao/caminhoes.dat", Caminhao.class));

        return veiculosCarregados;
    }

    private <T extends Veiculo> List<T> carregarVeiculosDeArquivo(String caminho, Class<T> tipo) {
        List<T> lista = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            while (true) {
                Object obj = ois.readObject();
                if (tipo.isInstance(obj)) {
                    lista.add(tipo.cast(obj));
                }
            }
        } catch (java.io.EOFException eof) {
            // Fim do arquivo, esperado
        } catch (Exception e) {
            System.err.println("Erro ao carregar veículos de " + caminho + ": " + e.getMessage());
        }

        return lista;
    }

    // ✅ Novo método para carregar locações
    private List<Locacao> carregarLocacoes() {
        List<Locacao> lista = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dump/locacoes/locacoes.dat"))) {
            while (true) {
                Object obj = ois.readObject();
                if (obj instanceof Locacao) {
                    lista.add((Locacao) obj);
                }
            }
        } catch (java.io.EOFException eof) {
            // Fim do arquivo, esperado
        } catch (Exception e) {
            System.err.println("Erro ao carregar locações: " + e.getMessage());
        }
        return lista;
    }

    public List<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }

    // ✅ Método de filtro geral, corrigido para usar Stream
    public List<Veiculo> filtrarVeiculos(String nome, Double precoMax, Cor cor, String statusDisponibilidade, Integer anoMin, Integer anoMax, String tipoVeiculo) {
        // Começa com um Stream dos veículos
        Stream<Veiculo> resultadoStream = veiculos.stream();

        // Filtro por nome (se houver)
        if (nome != null && !nome.trim().isEmpty()) {
            resultadoStream = resultadoStream.filter(v -> v.getNome().toLowerCase().contains(nome.toLowerCase()));
        }

        // Filtro por preço por dia (se houver)
        if (precoMax != null) {
            resultadoStream = resultadoStream.filter(v -> v.getValorDiario() <= precoMax);
        }

        // Filtro por cor (se houver)
        if (cor != null) {
            resultadoStream = resultadoStream.filter(v -> v.getCor() == cor);
        }

        // Filtro por status de disponibilidade
        if (statusDisponibilidade != null) {
            switch (statusDisponibilidade) {
                case "Disponíveis":
                    resultadoStream = resultadoStream.filter(v -> {
                        // Um veículo é disponível se não houver locação ativa para ele
                        return locacoes.stream()
                                .noneMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca()) && loc.getDataDevolucao() == null);
                    });
                    break;
                case "Próximos de Devolução":
                    resultadoStream = resultadoStream.filter(v -> {
                        // Um veículo está "próximo de devolução" se estiver locado
                        // E sua data prevista de devolução for nos próximos X dias (ex: 3 dias)
                        // ou seja, ele está locado e ainda não foi devolvido
                        return locacoes.stream()
                                .anyMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca())
                                        && loc.getDataDevolucao() == null // Ainda não devolvido
                                        && loc.getDataPrevistaDevolucao() != null
                                        && loc.getDataPrevistaDevolucao().isAfter(LocalDateTime.now()) // A devolução ainda não passou
                                        && loc.getDataPrevistaDevolucao().isBefore(LocalDateTime.now().plusDays(3))); // Nos próximos 3 dias
                    });
                    break;
                case "Todos":
                    // Já é o comportamento padrão, não precisa filtrar
                    break;
            }
        }

        // Filtro por ano (se houver)
        if (anoMin != null) {
            resultadoStream = resultadoStream.filter(v -> v.getAno() >= anoMin);
        }
        if (anoMax != null) {
            resultadoStream = resultadoStream.filter(v -> v.getAno() <= anoMax);
        }

        // Novo: Filtro por tipo de veículo (Carro, Moto, Caminhão)
        if (tipoVeiculo != null && !tipoVeiculo.equals("Todos os Modelos")) {
            switch (tipoVeiculo) {
                case "Carro":
                    resultadoStream = resultadoStream.filter(v -> v instanceof Carro);
                    break;
                case "Moto":
                    resultadoStream = resultadoStream.filter(v -> v instanceof Moto);
                    break;
                case "Caminhão":
                    resultadoStream = resultadoStream.filter(v -> v instanceof Caminhao);
                    break;
            }
        }

        return resultadoStream.collect(Collectors.toList());
    }

    // Método auxiliar para obter o status de locação de um veículo
    // Isso é útil para exibir o status no card (Disponível/Indisponível)
    public boolean estaLocado(Veiculo veiculo) {
        return locacoes.stream()
                .anyMatch(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null);
    }
}