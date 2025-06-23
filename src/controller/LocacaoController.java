package controller;

import entities.Cliente;
import entities.Locacao;
import entities.Veiculo;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocacaoController {

    private static final String LOCACOES_FILE_PATH = "dump/locacoes/locacoes.dat"; 
    private List<Locacao> locacoes; 

    private AuthController authController; 
    private VeiculoController veiculoController; 

    public LocacaoController() {
        File dir = new File("dump/locacoes/"); 
        if (!dir.exists()) { 
            dir.mkdirs(); 
        }
        this.locacoes = loadLocacoes(); 
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
        if (veiculoController.estaLocado(veiculo)) { 
            System.err.println("Veículo já locado."); 
            return false; 
        }

        if (cliente.getSaldo() < valorTotal) { 
            System.err.println("Saldo insuficiente."); 
            return false; 
        }

        Locacao novaLocacao = new Locacao( 
                LocalDateTime.now(), 
                LocalDateTime.now().plusDays(dias), 
                null,
                veiculo, 
                cliente 
        );

        this.locacoes.add(novaLocacao); 

        boolean locacaoSalva = saveAllLocacoes(); 
        if (!locacaoSalva) { 
            this.locacoes.remove(novaLocacao); 
            System.err.println("Falha ao salvar locação."); 
            return false; 
        }

        cliente.debitarSaldo(valorTotal); 
        boolean clienteAtualizado = authController.updateCliente(cliente); 
        if (!clienteAtualizado) { 
            System.err.println("Erro ao atualizar saldo do cliente após locação."); 
            return false; 
        }

        veiculo.setLocacoes(veiculo.getLocacoes() + 1); 
        boolean veiculoAtualizado = veiculoController.atualizarVeiculo(veiculo); 
        if (!veiculoAtualizado) { 
            System.err.println("Erro ao atualizar veículo após locação."); 
            return false; 
        }

        System.out.println("Locação realizada para " + veiculo.getNome() + " por " + cliente.getNome()); 
        return true; 
    }

    /**
     * Encontra a locação ativa para um veículo e um cliente específicos.
     * @param veiculo O veículo da locação.
     * @return A Locacao ativa, se encontrada, ou null.
     */
    public Locacao encontrarLocacaoAtiva(Veiculo veiculo) {
        this.locacoes = loadLocacoes(); 
        Optional<Locacao> locacaoAtiva = this.locacoes.stream() 
                .filter(loc -> loc.getVeiculo().getPlaca().equals(veiculo.getPlaca()) && loc.getDataDevolucao() == null) 
                .findFirst();
        return locacaoAtiva.orElse(null);
    }

    /**
     * Registra a devolução de um veículo, calcula multas e atualiza o saldo do cliente.
     * @param locacao A locação a ser finalizada.
     * @return true se a devolução foi registrada com sucesso, false caso contrário.
     */
    public boolean registrarDevolucao(Locacao locacao) {
        if (locacao == null || locacao.getDataDevolucao() != null) {
            System.err.println("Locação inválida ou já devolvida.");
            return false;
        }

        locacao.setDataDevolucao(LocalDateTime.now());
        double multa = locacao.calcularMulta();

        Cliente cliente = locacao.getCliente();
        if (multa > 0) {
            cliente.debitarSaldo(multa);
            boolean clienteAtualizado = authController.updateCliente(cliente);
            if (!clienteAtualizado) {
                System.err.println("Erro ao atualizar saldo do cliente com multa após devolução.");
                return false;
            }
        }

        boolean removed = this.locacoes.removeIf(l ->
                l.getVeiculo().getPlaca().equals(locacao.getVeiculo().getPlaca()) &&
                        l.getDataLocacao().equals(locacao.getDataLocacao()));

        if (!removed) {
            System.err.println("Erro: Locação a ser devolvida não encontrada na lista para atualização.");
            return false;
        }

        this.locacoes.add(locacao);
        boolean salvo = saveAllLocacoes();
        if (!salvo) {
            System.err.println("Erro ao salvar locações após devolução.");
            return false;
        }

        System.out.println("Devolução do veículo " + locacao.getVeiculo().getNome() + " registrada para " + cliente.getNome() + " com multa de R$" + String.format("%.2f", multa));
        return true;
    }
}