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
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VeiculoController {

    private List<Veiculo> veiculos;

    private static final String VEHICLE_PICS_DIR = "dump/vehicle_pics/";
    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat";

    public VeiculoController() {
        File vehiclePicDir = new File(VEHICLE_PICS_DIR);
        if (!vehiclePicDir.exists()) {
            vehiclePicDir.mkdirs();
        }
        this.veiculos = carregarTodosVeiculos();
    }

    public boolean excluirVeiculo(Veiculo veiculoParaExcluir) {

        boolean removedFromMemory = veiculos.removeIf(v -> v.getPlaca().equals(veiculoParaExcluir.getPlaca()));

        if (removedFromMemory) {

            if (veiculoParaExcluir instanceof Carro) {
                List<Carro> carrosAtualizados = veiculos.stream().filter(v -> v instanceof Carro).map(v -> (Carro) v).collect(Collectors.toList());
                return salvarListaDeVeiculosEmArquivo(carrosAtualizados, "dump/carros/carros.dat");
            } else if (veiculoParaExcluir instanceof Moto) {
                List<Moto> motosAtualizadas = veiculos.stream().filter(v -> v instanceof Moto).map(v -> (Moto) v).collect(Collectors.toList());
                return salvarListaDeVeiculosEmArquivo(motosAtualizadas, "dump/moto/motos.dat");
            } else if (veiculoParaExcluir instanceof Caminhao) {
                List<Caminhao> caminhoesAtualizados = veiculos.stream().filter(v -> v instanceof Caminhao).map(v -> (Caminhao) v).collect(Collectors.toList());
                return salvarListaDeVeiculosEmArquivo(caminhoesAtualizados, "dump/caminhao/caminhoes.dat");
            }
        }
        return false;
    }

    public boolean atualizarVeiculo(Veiculo veiculoAtualizado) {

        boolean removed = veiculos.removeIf(v -> v.getPlaca().equals(veiculoAtualizado.getPlaca()));
        if (!removed) {
            System.err.println("Erro ao atualizar: Veículo com placa '" + veiculoAtualizado.getPlaca() + "' não encontrado.");
            return false;
        }

        veiculos.add(veiculoAtualizado);

        if (veiculoAtualizado instanceof Carro) {
            List<Carro> carrosAtualizados = veiculos.stream().filter(v -> v instanceof Carro).map(v -> (Carro) v).collect(Collectors.toList());
            return salvarListaDeVeiculosEmArquivo(carrosAtualizados, "dump/carros/carros.dat");
        } else if (veiculoAtualizado instanceof Moto) {
            List<Moto> motosAtualizadas = veiculos.stream().filter(v -> v instanceof Moto).map(v -> (Moto) v).collect(Collectors.toList());
            return salvarListaDeVeiculosEmArquivo(motosAtualizadas, "dump/moto/motos.dat");
        } else if (veiculoAtualizado instanceof Caminhao) {
            List<Caminhao> caminhoesAtualizados = veiculos.stream().filter(v -> v instanceof Caminhao).map(v -> (Caminhao) v).collect(Collectors.toList());
            return salvarListaDeVeiculosEmArquivo(caminhoesAtualizados, "dump/caminhao/caminhoes.dat");
        }
        return false;
    }

    public String saveVehiclePicture(String originalImagePath, String placaVeiculo) {
        if (originalImagePath == null || originalImagePath.isEmpty()) {
            return null;
        }

        File originalFile = new File(originalImagePath);
        String fileExtension = "";
        int i = originalImagePath.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalImagePath.substring(i);
        }

        String cleanedPlate = placaVeiculo.replaceAll("[^a-zA-Z0-9.-]", "_");
        String newFileName = cleanedPlate + fileExtension;
        File newFile = new File(VEHICLE_PICS_DIR + newFileName);

        try {
            java.nio.file.Files.copy(originalFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return newFile.getAbsolutePath();
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

        boolean placaExiste = veiculos.stream().anyMatch(v -> v.getPlaca().equalsIgnoreCase(novoVeiculo.getPlaca()));
        if (placaExiste) {
            System.err.println("Erro: Veículo com a placa '" + novoVeiculo.getPlaca() + "' já existe.");
            return false;
        }

        String savedPhotoPath = saveVehiclePicture(novoVeiculo.getCaminhoFoto(), novoVeiculo.getPlaca());
        if (savedPhotoPath == null && novoVeiculo.getCaminhoFoto() != null && !novoVeiculo.getCaminhoFoto().isEmpty()) {

            System.err.println("Falha ao salvar a imagem do veículo. Cadastro abortado.");
            return false;
        }
        novoVeiculo.setCaminhoFoto(savedPhotoPath);

        veiculos.add(novoVeiculo);

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
        File file = new File(caminho);

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
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

    public List<Locacao> carregarLocacoes() {
        List<Locacao> lista = new ArrayList<>();
        File file = new File(LOCACOES_FILE_PATH);

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    List<?> rawList = (List<?>) obj;
                    for (Object item : rawList) {
                        if (item instanceof Locacao) {
                            lista.add((Locacao) item);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar locações: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Locacao não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return lista;
    }

    public List<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }

    public List<Veiculo> getVeiculosMaisAlugados(int limit) {
        return veiculos.stream().sorted(Comparator.comparingInt(Veiculo::getLocacoes).reversed()).limit(limit).collect(Collectors.toList());
    }

    public List<Veiculo> filtrarVeiculos(String termoBuscaGeral, Double precoMax, Cor cor, String statusDisponibilidade, Integer anoMin, Integer anoMax, String tipoVeiculo) {
        Stream<Veiculo> resultadoStream = veiculos.stream();

        List<Locacao> locacoesAtuais = carregarLocacoes();

        if (termoBuscaGeral != null && !termoBuscaGeral.trim().isEmpty()) {
            String[] termosIndividuais = termoBuscaGeral.toLowerCase().split("\\s+");

            resultadoStream = resultadoStream.filter(veiculo -> {
                String infoVeiculo = (veiculo.getNome() + " " + veiculo.getMarca() + " " + veiculo.getModelo() + " " + veiculo.getAno()).toLowerCase();
                return Arrays.stream(termosIndividuais).allMatch(termo -> infoVeiculo.contains(termo));
            });
        }

        if (precoMax != null) {
            resultadoStream = resultadoStream.filter(v -> v.getValorDiario() <= precoMax);
        }

        if (cor != null && cor != Cor.TODAS) {
            resultadoStream = resultadoStream.filter(v -> v.getCor() == cor);
        }

        if (statusDisponibilidade != null) {
            switch (statusDisponibilidade) {
                case "Disponíveis":
                    resultadoStream = resultadoStream.filter(v -> locacoesAtuais.stream().noneMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca()) && loc.getDataDevolucao() == null));
                    break;
                case "Próximos de Devolução":
                    resultadoStream = resultadoStream.filter(v -> {
                        LocalDateTime agora = LocalDateTime.now();
                        LocalDateTime limiteSuperior = agora.plusDays(3);

                        return locacoesAtuais.stream().anyMatch(loc -> loc.getVeiculo().getPlaca().equals(v.getPlaca()) && loc.getDataDevolucao() == null && loc.getDataPrevistaDevolucao() != null && !loc.getDataPrevistaDevolucao().isBefore(agora.toLocalDate().atStartOfDay()) && loc.getDataPrevistaDevolucao().isBefore(limiteSuperior));
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

    private <T extends Veiculo> boolean salvarListaDeVeiculosEmArquivo(List<T> lista, String caminhoDoArquivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoDoArquivo))) {
            oos.writeObject(lista);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de veículos em " + caminhoDoArquivo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean estaLocado(Veiculo veiculo) {
        List<Locacao> locacoesAtuais = carregarLocacoes();
        return locacoesAtuais.stream().anyMatch(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null);
    }
}