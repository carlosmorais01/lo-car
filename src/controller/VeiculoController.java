package controller;

import entities.Veiculo;
import entities.Carro;
import entities.Moto;
import entities.Caminhao;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class VeiculoController {

    private List<Veiculo> veiculos;

    public VeiculoController() {
        this.veiculos = carregarTodosVeiculos(); // carregar todos os veículos ao iniciar
    }

    // ✅ Carrega os veículos de todos os arquivos
    private List<Veiculo> carregarTodosVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();

        veiculos.addAll(carregarVeiculosDeArquivo("dump/carros/carros.dat", Carro.class));
        veiculos.addAll(carregarVeiculosDeArquivo("dump/moto/motos.dat", Moto.class));
        veiculos.addAll(carregarVeiculosDeArquivo("dump/caminhao/caminhoes.dat", Caminhao.class));

        return veiculos;
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

    // ✅ Método chamado pela tela para listar todos
    public List<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }

    // ✅ Método para filtrar por nome
    public List<Veiculo> filtrarPorNome(String nome) {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo v : veiculos) {
            if (v.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(v);
            }
        }
        return resultado;
    }
}
