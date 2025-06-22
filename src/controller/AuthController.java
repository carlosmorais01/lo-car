// src/controller/AuthController.java
package controller;

import entities.Cliente;
import entities.Funcionario;
import entities.Pessoa;
import util.PasswordHasher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthController {
    private static final String CLIENTS_FILE_PATH = "dump/clientes/clientes.dat";
    private static final String EMPLOYEES_FILE_PATH = "dump/funcionarios/funcionarios.dat"; // NOVO: Caminho para funcionários
    private static final String PROFILE_PICS_DIR = "dump/profile_pics/";

    public AuthController() {
        File dir = new File("dump/clientes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File profileDir = new File(PROFILE_PICS_DIR);
        if (!profileDir.exists()) {
            profileDir.mkdirs();
        }
        File employeeDir = new File("dump/funcionarios/");
        if (!employeeDir.exists()) {
            employeeDir.mkdirs();
        }
    }

    /**
     * Tenta autenticar um usuário (Cliente ou Funcionario) com base no email e senha.
     *
     * @param email O email do usuário.
     * @param password A senha do usuário (texto simples).
     * @return O objeto Pessoa (Cliente ou Funcionario) se a autenticação for bem-sucedida, caso contrário, null.
     */
    public Pessoa authenticate(String email, String password) { // Retorna Pessoa
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }

        // Tenta autenticar como Cliente
        List<Cliente> clients = loadClients();
        Optional<Cliente> authenticatedClient = clients.stream()
                .filter(c -> c.getEmail().equals(email) && c.getSenha().equals(hashedPassword))
                .findFirst();
        if (authenticatedClient.isPresent()) {
            return authenticatedClient.get();
        }

        List<Funcionario> employees = loadEmployees();
        Optional<Funcionario> authenticatedEmployee = employees.stream()
                .filter(f -> f.getEmail().equals(email) && f.getSenha().equals(hashedPassword))
                .findFirst();
        if (authenticatedEmployee.isPresent()) {
            return authenticatedEmployee.get();
        }

        return null; // Nenhuma autenticação bem-sucedida
    }

    public List<Funcionario> loadEmployees() {
        List<Funcionario> employees = new ArrayList<>();
        File file = new File(EMPLOYEES_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject(); // Tenta ler a lista inteira de uma vez
                if (obj instanceof List) { // Se for uma lista
                    List<?> rawList = (List<?>) obj;
                    for (Object item : rawList) { // Itera sobre os elementos da lista
                        if (item instanceof Funcionario) { // Verifica o tipo de cada elemento
                            employees.add((Funcionario) item);
                        }
                    }
                } else {
                    System.err.println("Aviso: Arquivo de funcionários não contém uma lista serializada. Carregando objeto por objeto (formato antigo).");
                    employees = new ArrayList<>(); // Reseta a lista
                    try (ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(file))) {
                        while (true) {
                            Object singleObj = ois2.readObject();
                            if (singleObj instanceof Funcionario) {
                                employees.add((Funcionario) singleObj);
                            }
                        }
                    } catch (java.io.EOFException eof2) { /* fim do arquivo */ }
                    // NUNCA COPIE O FALLBACK PARA CÓDIGO FINAL SE NÃO FOR NECESSÁRIO COMPATIBILIDADE
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler funcionários do arquivo: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Funcionario não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return employees;
    }

    /**
     * Carrega a lista de clientes de um arquivo serializado.
     *
     * @return Uma lista de objetos Cliente. Retorna uma lista vazia se o arquivo não existir ou houver erro.
     */
    public List<Cliente> loadClients() {
        List<Cliente> clients = new ArrayList<>();
        File file = new File(CLIENTS_FILE_PATH);

        if (file.exists() && file.length() > 0) {
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
            System.err.println("Erro ao salvar clientes no arquivo: ");
            e.printStackTrace();
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

        boolean exists = clients.stream().anyMatch(c ->
                c.getEmail().equals(newClient.getEmail()) || c.getCpf().equals(newClient.getCpf()));

        if (exists) {
            return false;
        }

        String hashedPassword = PasswordHasher.hashPassword(newClient.getSenha());
        if (hashedPassword == null) {
            return false;
        }
        newClient.setSenha(hashedPassword);

        String savedPhotoPath = saveProfilePicture(newClient.getCaminhoFoto(), newClient.getCpf());
        newClient.setCaminhoFoto(savedPhotoPath);
        clients.add(newClient);
        return saveClients(clients);
    }

    public String saveProfilePicture(String originalImagePath, String cpf) {
        if (originalImagePath == null || originalImagePath.isEmpty()) {
            return null; // Nenhuma imagem para salvar
        }

        File originalFile = new File(originalImagePath);
        String fileExtension = "";
        int i = originalImagePath.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalImagePath.substring(i); // Ex: .png
        }

        String newFileName = cpf + fileExtension; // Nomeia a imagem com o CPF do cliente
        File newFile = new File(PROFILE_PICS_DIR + newFileName);

        try {
            java.nio.file.Files.copy(originalFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return newFile.getAbsolutePath(); // Retorna o caminho absoluto do arquivo salvo
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem de perfil para " + cpf + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}