package controller;

import entities.Cliente;
import entities.Locacao;
import entities.Veiculo;
import exceptions.LocacaoControllerException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A classe LocacaoController gerencia as operações relacionadas a locações de veículos,
 * incluindo o registro de novos aluguéis, carregamento, salvamento e finalização de locações,
 * além de interagir com os controladores de autenticação e veículos para manter a consistência dos dados.
 */
public class LocacaoController {

    /**
     * Caminho do arquivo onde os dados das locações são serializados.
     */
    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat";
    private List<Locacao> locacoes;

    private AuthController authController;
    private VeiculoController veiculoController;

    /**
     * Construtor da classe LocacaoController.
     * Garante que o diretório de persistência de locações exista e inicializa as listas
     * de locações e as instâncias dos controladores dependentes.
     */
    public LocacaoController() {
        File dir = new File("dump/locacoes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.locacoes = loadLocacoes();
        this.authController = new AuthController();
        this.veiculoController = new VeiculoController();
    }

    /**
     * Carrega a lista de locações de um arquivo serializado.
     *
     * @return Uma lista de objetos Locacao. Retorna uma lista vazia se o arquivo não existir ou houver erro durante a leitura.
     */
    public List<Locacao> loadLocacoes() {
        List<Locacao> loadedLocacoes = new ArrayList<>();
        File file = new File(LOCACOES_FILE_PATH);

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    List<?> rawList = (List<?>) obj;
                    for (Object item : rawList) {
                        if (item instanceof Locacao) {
                            loadedLocacoes.add((Locacao) item);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler locações do arquivo: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Locacao não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return loadedLocacoes;
    }

    /**
     * Salva a lista completa de locações em um arquivo serializado.
     *
     * @return true se a operação de salvamento for bem-sucedida, false caso contrário.
     */
    public boolean saveAllLocacoes(List<Locacao> novasLocacoes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCACOES_FILE_PATH))) {
            oos.writeObject(novasLocacoes);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar locações no arquivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Realiza a operação de aluguel de um veículo.
     * Este método valida a disponibilidade do veículo e o saldo do cliente,
     * cria uma nova locação, a persiste, debita o saldo do cliente e
     * atualiza o contador de locações do veículo.
     *
     * @param cliente O cliente que está alugando.
     * @param veiculo O veículo a ser alugado.
     * @param dias Quantidade de dias de aluguel.
     * @param valorTotal Valor total do aluguel.
     * @return true se o aluguel foi bem-sucedido, false caso contrário.
     */
    public boolean realizarLocacao(Cliente cliente, Veiculo veiculo, int dias, double valorTotal) {
        if (veiculoController.estaLocado(veiculo)) {
            throw new LocacaoControllerException("Veículo já locado.");
        }

        if (cliente.getSaldo() < valorTotal) {
            throw new LocacaoControllerException("Saldo insuficiente.");
        }

        Locacao novaLocacao = new Locacao(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(dias),
                veiculo,
                cliente
        );

        this.locacoes.add(novaLocacao);

        boolean locacaoSalva = saveAllLocacoes(locacoes);
        if (!locacaoSalva) {
            this.locacoes.remove(novaLocacao);
            throw new LocacaoControllerException("Falha ao salvar locação.");
        }

        cliente.debitarSaldo(valorTotal);
        boolean clienteAtualizado = authController.updateCliente(cliente);
        if (!clienteAtualizado) {
            throw new LocacaoControllerException("Erro ao atualizar saldo do cliente após locação.");
        }

        veiculo.adicionarLocacao();
        boolean veiculoAtualizado = veiculoController.atualizarVeiculo(veiculo);
        if (!veiculoAtualizado) {
            throw new LocacaoControllerException("Erro ao atualizar veículo após locação.");
        }

        return true;
    }

    /**
     * Encontra a locação ativa (aquela que ainda não possui uma data de devolução) para um veículo específico.
     * Este método recarrega a lista de locações do armazenamento para garantir que os dados estejam atualizados.
     *
     * @param veiculo O veículo cuja locação ativa está sendo procurada.
     * @return O objeto Locacao ativa se encontrada, ou {@code null} se não houver uma locação ativa para o veículo.
     */
    public Locacao encontrarLocacaoAtiva(Veiculo veiculo) {
        this.locacoes = loadLocacoes();
        Optional<Locacao> locacaoAtiva = this.locacoes.stream()
                .filter(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null)
                .findFirst();
        return locacaoAtiva.orElse(null);
    }

    /**
     * Registra a devolução de um veículo, finalizando a locação ativa.
     * Este método define a data de devolução para a locação, calcula e aplica multas ao saldo do cliente,
     * e persiste as atualizações tanto na locação quanto no cliente.
     *
     * @param locacaoDevolvida A locação a ser finalizada (devolvida).
     * @return true se a devolução foi registrada e persistida com sucesso, false caso contrário.
     */
    public boolean registrarDevolucao(Locacao locacaoDevolvida) {
        if (locacaoDevolvida == null || locacaoDevolvida.getDataDevolucao() != null) {
            throw new LocacaoControllerException("Locação inválida ou já devolvida.");
        }

        locacaoDevolvida.setDataDevolucao(LocalDateTime.now());
        double multa = locacaoDevolvida.calcularMulta();

        Cliente cliente = locacaoDevolvida.getCliente();
        if (multa > 0) {
            cliente.debitarSaldo(multa);
            boolean clienteAtualizado = authController.updateCliente(cliente);
            if (!clienteAtualizado) {
                throw new LocacaoControllerException("Erro ao atualizar saldo do cliente com multa após devolução.");
            }
        }

        boolean removed = this.locacoes.removeIf(l ->
                l.getVeiculo().getPlaca().equals(locacaoDevolvida.getVeiculo().getPlaca()) &&
                        l.getDataLocacao().equals(locacaoDevolvida.getDataLocacao()));

        if (!removed) {
            throw new LocacaoControllerException("Erro: Locação a ser devolvida não encontrada na lista para atualização.");
        }

        this.locacoes.add(locacaoDevolvida);
        boolean salvo = saveAllLocacoes(locacoes);
        if (!salvo) {
            throw new LocacaoControllerException("Erro ao salvar locações após devolução.");
        }
        return true;
    }
}