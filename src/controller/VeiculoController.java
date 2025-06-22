package controller;

import entities.Veiculo;
import entities.Carro;
import entities.Moto;
import entities.Caminhao;
import entities.Locacao;
import enums.Cor;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator; // Importar Comparator
import java.util.Arrays; // Importar para usar Arrays.asList
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VeiculoController {

    private List<Veiculo> veiculos;
    private List<Locacao> locacoes;

    public VeiculoController() {
        this.veiculos = carregarTodosVeiculos();
        this.locacoes = carregarLocacoes();
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

    public List<Veiculo> getVeiculosMaisAlugados(int limit) {
        // Para carros "mais alugados", vamos usar o campo 'locacoes' na entidade Veiculo.
        // Em um sistema real, você faria um sumário das locações no período.
        return veiculos.stream()
                .sorted(Comparator.comparingInt(Veiculo::getLocacoes).reversed()) // Ordena pelo maior número de locações
                .limit(limit) // Limita a quantidade de veículos
                .collect(Collectors.toList());
    }

    public List<Veiculo> filtrarVeiculos(String termoBuscaGeral, Double precoMax, Cor cor, String statusDisponibilidade, Integer anoMin, Integer anoMax, String tipoVeiculo) {
        Stream<Veiculo> resultadoStream = veiculos.stream();

        // Filtro de busca geral (nome, marca, ano) - AGORA COM MÚLTIPLOS PARÂMETROS
        if (termoBuscaGeral != null && !termoBuscaGeral.trim().isEmpty()) {
            String[] termosIndividuais = termoBuscaGeral.toLowerCase().split("\\s+"); // Divide por espaços

            resultadoStream = resultadoStream.filter(veiculo -> {
                // Concatena todos os campos relevantes do veículo em uma única string para busca
                String infoVeiculo = (veiculo.getNome() + " " +
                        veiculo.getMarca() + " " +
                        veiculo.getModelo() + " " + // Adicionado Modelo também
                        veiculo.getAno()).toLowerCase();

                // Verifica se CADA termo individual está contido em infoVeiculo
                return Arrays.stream(termosIndividuais)
                        .allMatch(termo -> infoVeiculo.contains(termo));
            });
        }

        // Filtro por preço por dia (se houver)
        if (precoMax != null) {
            resultadoStream = resultadoStream.filter(v -> v.getValorDiario() <= precoMax);
        }

        if (cor != null && cor != Cor.TODAS) {
            resultadoStream = resultadoStream.filter(v -> v.getCor() == cor);
        }

        if (statusDisponibilidade != null) {
            switch (statusDisponibilidade) {
                case "Disponíveis":
                    resultadoStream = resultadoStream.filter(v -> {
                        return locacoes.stream()
                                .noneMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca()) && loc.getDataDevolucao() == null);
                    });
                    break;
                case "Próximos de Devolução":
                    resultadoStream = resultadoStream.filter(v -> {
                        LocalDateTime agora = LocalDateTime.now();
                        LocalDateTime limiteSuperior = agora.plusDays(3);

                        return locacoes.stream()
                                .anyMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca())
                                        && loc.getDataDevolucao() == null
                                        && loc.getDataPrevistaDevolucao() != null
                                        && !loc.getDataPrevistaDevolucao().isBefore(agora.toLocalDate().atStartOfDay())
                                        && loc.getDataPrevistaDevolucao().isBefore(limiteSuperior));
                    });
                    break;
                case "Todos":
                    // Não aplica filtro de status
                    break;
            }
        }

        // anoMin e anoMax continuam como parâmetros não utilizados, pois a busca geral já os considera.

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

    public boolean estaLocado(Veiculo veiculo) {
        return locacoes.stream()
                .anyMatch(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null);
    }
}