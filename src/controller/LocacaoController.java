// LocacaoController.java
package controller;

import entities.Cliente;
import entities.Locacao;
import entities.Veiculo;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocacaoController {

    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat";
    private List<Locacao> locacoes;

    // Adicionar instâncias dos outros controllers
    private AuthController authController;
    private VeiculoController veiculoController;

    public LocacaoController() {
        File dir = new File("dump/locacoes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.locacoes = loadLocacoes();
        // Inicializar os controllers dependentes
        this.authController = new AuthController();
        this.veiculoController = new VeiculoController();
    }

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

    public boolean saveAllLocacoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCACOES_FILE_PATH))) {
            oos.writeObject(locacoes);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar locações no arquivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }

    public boolean realizarLocacao(Cliente cliente, Veiculo veiculo, int dias, double valorTotal) {
        // 1. Validação de disponibilidade usando o VeiculoController
        if (veiculoController.estaLocado(veiculo)) { //
            System.err.println("Veículo já locado.");
            return false;
        }

        if (cliente.getSaldo() < valorTotal) {
            System.err.println("Saldo insuficiente.");
            return false;
        }

        // 2. Criar a Locação
        Locacao novaLocacao = new Locacao(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(dias),
                null,
                veiculo,
                cliente
        );

        // Adiciona à lista em memória
        this.locacoes.add(novaLocacao);

        // Persiste a lista completa de locações
        boolean locacaoSalva = saveAllLocacoes(); //
        if (!locacaoSalva) {
            this.locacoes.remove(novaLocacao);
            System.err.println("Falha ao salvar locação.");
            return false;
        }

        // 3. Debitar saldo do cliente e persistir o cliente atualizado
        cliente.debitarSaldo(valorTotal); //
        boolean clienteAtualizado = authController.updateCliente(cliente); //
        if (!clienteAtualizado) {
            System.err.println("Erro ao atualizar saldo do cliente após locação.");
            // Idealmente, aqui você reverteria a locação ou marcaria como erro no sistema de logs.
            return false;
        }

        // 4. Atualizar o número de locações do veículo e persistir o veículo
        veiculo.setLocacoes(veiculo.getLocacoes() + 1); //
        boolean veiculoAtualizado = veiculoController.atualizarVeiculo(veiculo); //
        if (!veiculoAtualizado) {
            System.err.println("Erro ao atualizar veículo após locação.");
            return false;
        }

        System.out.println("Locação realizada para " + veiculo.getNome() + " por " + cliente.getNome());
        return true;
    }
}