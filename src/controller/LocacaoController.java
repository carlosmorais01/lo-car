// src/controller/LocacaoController.java
package controller;

import entities.Cliente;
import entities.Locacao; // Importar a classe Locacao
import entities.Veiculo; // Importar a classe Veiculo

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocacaoController {

    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat";
    private List<Locacao> locacoes;

    public LocacaoController() {
        File dir = new File("dump/locacoes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.locacoes = loadLocacoes();
    }

    /**
     * Carrega a lista de locações de um arquivo serializado.
     *
     * @return Uma lista de objetos Locacao. Retorna uma lista vazia se o arquivo não existir ou houver erro.
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

    public boolean saveAllLocacoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCACOES_FILE_PATH))) {
            oos.writeObject(locacoes); // Salva a lista completa
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar locações no arquivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // NOVO MÉTODO AUXILIAR para escrever em arquivo serializado sem cabeçalho (para append)
    private static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // Não escreve o cabeçalho do stream se o arquivo já existir
            reset(); // Necessário para garantir que o stream está pronto para escrever
        }
    }


    /**
     * Realiza a operação de aluguel de um veículo.
     * Isso envolveria: registrar a locação, deduzir saldo, atualizar status do veículo.
     *
     * @param cliente O cliente que está alugando.
     * @param veiculo O veículo a ser alugado.
     * @param dias Quantidade de dias de aluguel.
     * @param valorTotal Valor total do aluguel.
     * @return true se o aluguel foi bem-sucedido, false caso contrário.
     */
    public boolean realizarLocacao(Cliente cliente, Veiculo veiculo, int dias, double valorTotal) {
        // 1. Validação de disponibilidade (já feita na UI, mas pode ser repetida aqui para segurança)
        if (veiculo.getLocacoes() > 0) { // Simplificação: se locacoes > 0, considero ocupado
            // Em um sistema real, você verificaria na lista de locações se há uma locação ativa para este veículo
            // veiculoController.estaLocado(veiculo)
            return false; // Veículo já locado
        }

        if (cliente.getSaldo() < valorTotal) {
            return false; // Saldo insuficiente
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
        boolean locacaoSalva = saveAllLocacoes();
        if (!locacaoSalva) {
            // Se a locação não pôde ser salva, remova da memória e retorne false
            this.locacoes.remove(novaLocacao);
            return false;
        }

        // 4. Deduzir saldo do cliente e persistir o cliente atualizado
        cliente.debitarSaldo(valorTotal);
        // O AuthController precisa de um método para atualizar um cliente existente
        // Você já tem uma lógica para isso em UserProfileScreen
        // Precisamos de um método mais genérico no AuthController para atualizar o cliente.
        // Por simplicidade aqui, vamos apenas "simular" salvando o cliente com saldo deduzido.
        // O AuthController.saveClients já sobrescreve a lista completa.
        // Então, é preciso: 1. Carregar todos os clientes, 2. Remover o cliente antigo, 3. Adicionar o cliente atualizado, 4. Salvar a lista.
        // Para isso, criar um método no AuthController: public boolean updateCliente(Cliente cliente)
        // Por enquanto, faremos a simulação local.

        // TODO: Chamar AuthController.updateCliente(cliente) aqui para persistir o novo saldo
        // boolean clienteAtualizado = authController.updateCliente(cliente);
        // if (!clienteAtualizado) {
        //     System.err.println("Erro ao atualizar saldo do cliente após locação.");
        //     // Idealmente, reverter a locação ou marcar como erro.
        //     return false;
        // }

        // 5. Atualizar o número de locações do veículo (para a lista de "mais alugados") e persistir o veículo
        veiculo.setLocacoes(veiculo.getLocacoes() + 1);
        // O VeiculoController.atualizarVeiculo(veiculo) já faz isso.
        // boolean veiculoAtualizado = veiculoController.atualizarVeiculo(veiculo);
        // if (!veiculoAtualizado) {
        //    System.err.println("Erro ao atualizar veículo após locação.");
        //    return false;
        // }

        System.out.println("Locação realizada para " + veiculo.getNome() + " por " + cliente.getNome());
        return true;
    }

    // Você também pode adicionar um método para registrar devoluções aqui.
    // public boolean registrarDevolucao(Locacao locacao, LocalDateTime dataDevolucaoReal) { ... }
}