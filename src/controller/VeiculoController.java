package controller;

import entities.Veiculo;
import entities.Carro;
import entities.Moto;
import entities.Caminhao;
import entities.Locacao;
import enums.Cor;

import java.io.*;
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

    private static final String VEHICLE_PICS_DIR = "dump/vehicle_pics/";

    public VeiculoController() {
        File vehiclePicDir = new File(VEHICLE_PICS_DIR);
        if (!vehiclePicDir.exists()) {
            vehiclePicDir.mkdirs();
        }
        this.veiculos = carregarTodosVeiculos();
        this.locacoes = carregarLocacoes();
    }

    public String saveVehiclePicture(String originalImagePath, String placaVeiculo) {
        if (originalImagePath == null || originalImagePath.isEmpty()) {
            return null; // Nenhuma imagem para salvar
        }

        File originalFile = new File(originalImagePath);
        String fileExtension = "";
        int i = originalImagePath.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalImagePath.substring(i);
        }

        // Nomeia a imagem com a placa do veículo (e remove caracteres inválidos, se houver)
        String cleanedPlate = placaVeiculo.replaceAll("[^a-zA-Z0-9.-]", "_"); // Mantém letras, números, ponto e traço
        String newFileName = cleanedPlate + fileExtension;
        File newFile = new File(VEHICLE_PICS_DIR + newFileName);

        try {
            java.nio.file.Files.copy(originalFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return newFile.getAbsolutePath(); // Retorna o caminho absoluto do arquivo salvo
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem do veículo '" + placaVeiculo + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private List<Veiculo> carregarTodosVeiculos() {
        List<Veiculo> veiculosCarregados = new ArrayList<>();
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/carros/carros.dat", Carro.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/moto/motos.dat", Moto.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/caminhao/caminhoes.dat", Caminhao.class));
        return veiculosCarregados;
    }

    public boolean cadastrarVeiculo(Veiculo novoVeiculo) {
        // Primeiro, verifica se a placa já existe para evitar duplicatas
        boolean placaExiste = veiculos.stream()
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(novoVeiculo.getPlaca()));
        if (placaExiste) {
            System.err.println("Erro: Veículo com a placa '" + novoVeiculo.getPlaca() + "' já existe.");
            return false;
        }

        // NOVO: Salvar a imagem do veículo e atualizar o caminho no objeto Veiculo
        String savedPhotoPath = saveVehiclePicture(novoVeiculo.getCaminhoFoto(), novoVeiculo.getPlaca());
        if (savedPhotoPath == null && novoVeiculo.getCaminhoFoto() != null && !novoVeiculo.getCaminhoFoto().isEmpty()) {
            // Se a imagem deveria existir mas não pôde ser salva, considere falha no cadastro
            System.err.println("Falha ao salvar a imagem do veículo. Cadastro abortado.");
            return false;
        }
        novoVeiculo.setCaminhoFoto(savedPhotoPath); // Atualiza o objeto Veiculo com o novo caminho da imagem

        // Adiciona o novo veículo à lista em memória
        veiculos.add(novoVeiculo);

        // Salva o veículo no arquivo de dados serializado específico do seu tipo
        if (novoVeiculo instanceof Carro) {
            return salvarVeiculoEmArquivo((Carro) novoVeiculo, "dump/carros/carros.dat");
        } else if (novoVeiculo instanceof Moto) {
            return salvarVeiculoEmArquivo((Moto) novoVeiculo, "dump/moto/motos.dat");
        } else if (novoVeiculo instanceof Caminhao) {
            return salvarVeiculoEmArquivo((Caminhao) novoVeiculo, "dump/caminhao/caminhoes.dat");
        }
        return false;
    }

    private <T extends Veiculo> boolean salvarVeiculoEmArquivo(T veiculoParaSalvar, String caminhoDoArquivo) {
        List<T> listaAtualizada = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoDoArquivo))) {
            while (true) {
                Object obj = ois.readObject();
                if (obj.getClass().equals(veiculoParaSalvar.getClass())) {
                    listaAtualizada.add((T) obj);
                }
            }
        } catch (java.io.EOFException eof) {
        } catch (Exception e) {
            System.err.println("Aviso: Erro ao carregar lista existente de " + caminhoDoArquivo + " para atualização: " + e.getMessage());
        }

        listaAtualizada.add(veiculoParaSalvar);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoDoArquivo))) {
            oos.writeObject(listaAtualizada);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar veículo em " + caminhoDoArquivo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private <T extends Veiculo> List<T> carregarVeiculosDeArquivo(String caminho, Class<T> tipo) {
        List<T> lista = new ArrayList<>();
        File file = new File(caminho); // Crie um objeto File

        if (file.exists() && file.length() > 0) { // Garante que o arquivo existe e não está vazio
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) { // Espera que o objeto lido seja uma List
                    List<?> l = (List<?>) obj;
                    for (Object item : l) {
                        if (tipo.isInstance(item)) {
                            lista.add(tipo.cast(item));
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler veículos de " + caminho + ": " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Classe não encontrada ao carregar veículos de " + caminho + ": " + e.getMessage());
            }
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
                    break;
            }
        }

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