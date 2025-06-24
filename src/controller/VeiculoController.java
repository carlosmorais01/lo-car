package controller;

import entities.Veiculo;
import entities.Carro;
import entities.Moto;
import entities.Caminhao;
import entities.Locacao;
import enums.Cor;
import exceptions.VeiculoControllerException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A classe VeiculoController gerencia as operações relacionadas a veículos,
 * incluindo carregamento, salvamento, cadastro, atualização, exclusão e filtragem de veículos,
 * bem como o gerenciamento de suas imagens e verificação de status de locação.
 */
public class VeiculoController {

    List<Veiculo> veiculos;

    /**
     * Diretório onde as imagens dos veículos são armazenadas.
     */
    private static final String VEHICLE_PICS_DIR = "dump/vehicle_pics/";
    /**
     * Caminho do arquivo onde os dados das locações são serializados.
     * Usado para verificar a disponibilidade dos veículos.
     */
    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat";

    /**
     * Construtor da classe VeiculoController.
     * Garante que o diretório para imagens de veículos exista e carrega todos os veículos persistidos.
     */
    public VeiculoController() {
        File vehiclePicDir = new File(VEHICLE_PICS_DIR);
        if (!vehiclePicDir.exists()) {
            vehiclePicDir.mkdirs();
        }
        this.veiculos = carregarTodosVeiculos();
    }

    /**
     * Exclui um veículo da lista em memória e persiste a alteração no arquivo correspondente ao tipo do veículo.
     *
     * @param veiculoParaExcluir O objeto Veiculo a ser excluído.
     * @return true se o veículo foi removido com sucesso da memória e do arquivo, false caso contrário.
     */
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

    /**
     * Atualiza os dados de um veículo existente na lista em memória e no arquivo de persistência.
     * O veículo é identificado pela sua placa.
     *
     * @param veiculoAtualizado O objeto Veiculo com os dados a serem atualizados.
     * @return true se o veículo foi encontrado, atualizado e salvo com sucesso, false caso contrário.
     */
    public boolean atualizarVeiculo(Veiculo veiculoAtualizado) {
        boolean removed = veiculos.removeIf(v -> v.getPlaca().equals(veiculoAtualizado.getPlaca()));
        if (!removed) {
            throw new VeiculoControllerException("Erro ao atualizar: Veículo com placa '" + veiculoAtualizado.getPlaca() + "' não encontrado.");
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

    /**
     * Salva uma imagem de veículo em um diretório específico, renomeando-a com a placa do veículo.
     * Caracteres inválidos na placa são substituídos por '_'.
     *
     * @param originalImagePath O caminho absoluto da imagem original a ser copiada.
     * @param placaVeiculo      A placa do veículo, usada para nomear o novo arquivo de imagem.
     * @return O caminho absoluto do arquivo de imagem salvo, ou null em caso de falha ou se o caminho original for nulo/vazio.
     */
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
            throw new VeiculoControllerException("Erro ao salvar imagem do veículo '" + placaVeiculo + "': " + e.getMessage());
        }
    }

    /**
     * Carrega todos os veículos (Carros, Motos, Caminhões) de seus respectivos arquivos serializados.
     *
     * @return Uma lista consolidada de todos os objetos Veiculo carregados.
     */
    List<Veiculo> carregarTodosVeiculos() {
        List<Veiculo> veiculosCarregados = new ArrayList<>();
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/carros/carros.dat", Carro.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/moto/motos.dat", Moto.class));
        veiculosCarregados.addAll(carregarVeiculosDeArquivo("dump/caminhao/caminhoes.dat", Caminhao.class));
        return veiculosCarregados;
    }

    /**
     * Cadastra um novo veículo no sistema.
     * Verifica se a placa do veículo já existe e salva a imagem do veículo antes de persistir o objeto.
     *
     * @param novoVeiculo O objeto Veiculo a ser cadastrado.
     * @return true se o veículo foi cadastrado e salvo com sucesso, false caso contrário (ex: placa já existe ou falha ao salvar imagem).
     */
    public boolean cadastrarVeiculo(Veiculo novoVeiculo) {
        boolean placaExiste = veiculos.stream().anyMatch(v -> v.getPlaca().equalsIgnoreCase(novoVeiculo.getPlaca()));
        if (placaExiste) {
            throw new VeiculoControllerException("Erro: Veículo com a placa '" + novoVeiculo.getPlaca() + "' já existe.");
        }
        String savedPhotoPath = saveVehiclePicture(novoVeiculo.getCaminhoFoto(), novoVeiculo.getPlaca());
        if (savedPhotoPath == null && novoVeiculo.getCaminhoFoto() != null && !novoVeiculo.getCaminhoFoto().isEmpty()) {
            throw new VeiculoControllerException("Falha ao salvar a imagem do veículo. Cadastro abortado.");
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

    /**
     * Salva um veículo específico em seu respectivo arquivo serializado.
     * Este método carrega a lista existente, adiciona o novo veículo e sobrescreve o arquivo.
     *
     * @param <T>               O tipo de veículo (deve estender Veiculo).
     * @param veiculoParaSalvar O objeto do veículo a ser salvo.
     * @param caminhoDoArquivo  O caminho do arquivo .dat onde o veículo será salvo.
     * @return true se o veículo foi salvo com sucesso, false caso contrário.
     */
    <T extends Veiculo> boolean salvarVeiculoEmArquivo(T veiculoParaSalvar, String caminhoDoArquivo) {
        List<T> listaAtualizada = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoDoArquivo))) {
            while (true) {
                Object obj = ois.readObject();
                if (obj.getClass().equals(veiculoParaSalvar.getClass())) {
                    listaAtualizada.add((T) obj);
                }
            }
        } catch (java.io.EOFException ignored) {
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

    /**
     * Carrega uma lista de veículos de um tipo específico a partir de um arquivo serializado.
     *
     * @param <T>     O tipo de veículo (deve estender Veiculo).
     * @param caminho O caminho do arquivo .dat de onde os veículos serão carregados.
     * @param tipo    A classe do tipo de veículo esperado (ex: Carro.class).
     * @return Uma lista de objetos do tipo especificado. Retorna uma lista vazia se o arquivo não existir, estiver vazio ou houver erro.
     */
    <T extends Veiculo> List<T> carregarVeiculosDeArquivo(String caminho, Class<T> tipo) {
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

    /**
     * Carrega a lista de todas as locações registradas a partir do arquivo de locações.
     * Este método é usado para garantir que a verificação de disponibilidade de veículos
     * e outras operações relacionadas a locações utilizem os dados mais recentes.
     *
     * @return Uma lista de objetos Locacao. Retorna uma lista vazia se o arquivo não existir, estiver vazio ou houver erro.
     */
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

    /**
     * Retorna uma cópia da lista de todos os veículos carregados na memória.
     *
     * @return Uma nova ArrayList contendo todos os objetos Veiculo.
     */
    public List<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }

    /**
     * Retorna uma lista dos veículos mais alugados, ordenada pelo número de locações em ordem decrescente,
     * e limitada pela quantidade especificada.
     *
     * @param limit O número máximo de veículos a serem retornados.
     * @return Uma lista de Veiculo representando os veículos mais alugados.
     */
    public List<Veiculo> getVeiculosMaisAlugados(int limit) {
        return veiculos.stream()
                .sorted(Comparator.comparingInt(Veiculo::getLocacoes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Filtra a lista de veículos com base em vários critérios, como termo de busca geral, preço máximo, cor,
     * status de disponibilidade, ano de fabricação e tipo de veículo.
     * A disponibilidade do veículo é verificada consultando a lista de locações ativas.
     *
     * @param termoBuscaGeral       Termo para buscar em nome, marca, modelo e ano (insensível a maiúsculas/minúsculas).
     * @param precoMax              Preço diário máximo do veículo.
     * @param cor                   Cor do veículo.
     * @param statusDisponibilidade Status de disponibilidade ("Disponíveis", "Próximos de Devolução", "Todos").
     * @param anoMin                Ano mínimo de fabricação.
     * @param anoMax                Ano máximo de fabricação.
     * @param tipoVeiculo           Tipo específico de veículo ("Carro", "Moto", "Caminhão", "Todos os Modelos").
     * @return Uma lista de Veiculo que corresponde aos critérios de filtro.
     */
    public List<Veiculo> filtrarVeiculos(String termoBuscaGeral, Double precoMax, Cor cor, String statusDisponibilidade, Integer anoMin, Integer anoMax, String tipoVeiculo) {
        Stream<Veiculo> resultadoStream = veiculos.stream();
        List<Locacao> locacoesAtuais = carregarLocacoes();
        if (termoBuscaGeral != null && !termoBuscaGeral.trim().isEmpty()) {
            String[] termosIndividuais = termoBuscaGeral.toLowerCase().split("\\s+");

            resultadoStream = resultadoStream.filter(veiculo -> {
                String infoVeiculo = (veiculo.getNome() + " " +
                        veiculo.getMarca() + " " +
                        veiculo.getModelo() + " " +
                        veiculo.getAno()).toLowerCase();
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

    /**
     * Salva uma lista de veículos de um tipo específico em um arquivo serializado, sobrescrevendo o conteúdo existente.
     *
     * @param <T>              O tipo de veículo (deve estender Veiculo).
     * @param lista            A lista de objetos Veiculo a ser salva.
     * @param caminhoDoArquivo O caminho do arquivo .dat onde a lista será salva.
     * @return true se a lista foi salva com sucesso, false caso contrário.
     */
    <T extends Veiculo> boolean salvarListaDeVeiculosEmArquivo(List<T> lista, String caminhoDoArquivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoDoArquivo))) {
            oos.writeObject(lista);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de veículos em " + caminhoDoArquivo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica se um determinado veículo está atualmente locado.
     * Este método recarrega a lista de locações para garantir que a verificação use os dados mais recentes.
     *
     * @param veiculo O veículo a ser verificado.
     * @return true se o veículo estiver locado (tiver uma locação ativa), false caso contrário.
     */
    public boolean estaLocado(Veiculo veiculo) {
        List<Locacao> locacoesAtuais = carregarLocacoes();
        return locacoesAtuais.stream().anyMatch(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null);
    }
}