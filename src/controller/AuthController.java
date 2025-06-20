// src/controller/AuthController.java
package controller;

import entities.Cliente;
import util.PasswordHasher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthController {
    private static final String CLIENTS_FILE_PATH = "dump/clientes/clients.dat"; // Caminho do arquivo de clientes serializados

    public AuthController() {
        // Garante que o diretório 'dump/clientes/' exista
        File dir = new File("dump/clientes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Tenta autenticar um usuário com base no email e senha fornecidos.
     * A senha é hashed antes da comparação.
     *
     * @param email O email do usuário.
     * @param password A senha do usuário (texto simples).
     * @return O objeto Cliente se a autenticação for bem-sucedida, caso contrário, null.
     */
    public Cliente authenticate(String email, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            return null; // Erro ao gerar o hash da senha
        }

        List<Cliente> clients = loadClients();
        Optional<Cliente> authenticatedClient = clients.stream()
                .filter(c -> c.getEmail().equals(email) && c.getSenha().equals(hashedPassword))
                .findFirst();

        return authenticatedClient.orElse(null);
    }

    /**
     * Carrega a lista de clientes de um arquivo serializado.
     *
     * @return Uma lista de objetos Cliente. Retorna uma lista vazia se o arquivo não existir ou houver erro.
     */
    public List<Cliente> loadClients() {
        List<Cliente> clients = new ArrayList<>();
        File file = new File(CLIENTS_FILE_PATH);

        if (file.exists() && file.length() > 0) { // Verifica se o arquivo existe e não está vazio
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    clients = (List<Cliente>) obj;
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler clientes do arquivo: " + e.getMessage());
                // Pode ser útil para depuração, mas não queremos lançar exceção para o usuário final
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Cliente não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return clients;
    }

    /**
     * Salva a lista de clientes em um arquivo serializado.
     *
     * @param clients A lista de clientes a ser salva.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */
    public boolean saveClients(List<Cliente> clients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENTS_FILE_PATH))) {
            oos.writeObject(clients);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar clientes no arquivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adiciona um novo cliente à lista e o salva no arquivo.
     * Antes de adicionar, a senha do cliente é hashed.
     *
     * @param newClient O novo cliente a ser adicionado.
     * @return true se o cliente foi adicionado e salvo com sucesso, false caso contrário (ex: email já existe).
     */
    public boolean registerClient(Cliente newClient) {
        List<Cliente> clients = loadClients();

        // Verifica se já existe um cliente com o mesmo email ou CPF
        boolean exists = clients.stream().anyMatch(c ->
                c.getEmail().equals(newClient.getEmail()) || c.getCpf().equals(newClient.getCpf()));

        if (exists) {
            return false; // Cliente com email ou CPF já cadastrado
        }

        // Hashing da senha antes de salvar
        String hashedPassword = PasswordHasher.hashPassword(newClient.getSenha());
        if (hashedPassword == null) {
            return false; // Erro ao gerar o hash da senha
        }
        newClient.setSenha(hashedPassword); // Define a senha hashed para o cliente

        clients.add(newClient);
        return saveClients(clients);
    }
}