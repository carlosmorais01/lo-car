// AuthController.java
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
    private static final String EMPLOYEES_FILE_PATH = "dump/funcionarios/funcionarios.dat";
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

    public Pessoa authenticate(String email, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }

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

        return null;
    }

    public boolean saveEmployees(List<Funcionario> employees) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMPLOYEES_FILE_PATH))) {
            oos.writeObject(employees);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar funcionários no arquivo: ");
            e.printStackTrace();
            return false;
        }
    }

    public List<Funcionario> loadEmployees() {
        List<Funcionario> employees = new ArrayList<>();
        File file = new File(EMPLOYEES_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    List<?> rawList = (List<?>) obj;
                    for (Object item : rawList) {
                        if (item instanceof Funcionario) {
                            employees.add((Funcionario) item);
                        }
                    }
                } else {
                    System.err.println("Aviso: Arquivo de funcionários não contém uma lista serializada. Carregando objeto por objeto (formato antigo).");
                    employees = new ArrayList<>();
                    try (ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(file))) {
                        while (true) {
                            Object singleObj = ois2.readObject();
                            if (singleObj instanceof Funcionario) {
                                employees.add((Funcionario) singleObj);
                            }
                        }
                    } catch (java.io.EOFException eof2) { /* fim do arquivo */ }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler funcionários do arquivo: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Funcionario não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return employees;
    }

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
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Cliente não encontrada durante a desserialização: " + e.getMessage());
            }
        }
        return clients;
    }

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
     * NOVO MÉTODO: Atualiza um cliente na lista e salva no arquivo.
     * Encontra o cliente pelo CPF, remove o antigo e adiciona o atualizado.
     * @param clienteAtualizado O cliente com os dados atualizados.
     * @return true se o cliente foi atualizado e salvo com sucesso, false caso contrário.
     */
    public boolean updateCliente(Cliente clienteAtualizado) {
        List<Cliente> clients = loadClients();
        // Remove o cliente antigo (pelo CPF)
        boolean removed = clients.removeIf(c -> c.getCpf().equals(clienteAtualizado.getCpf()));
        if (!removed) {
            System.err.println("Erro: Cliente com CPF '" + clienteAtualizado.getCpf() + "' não encontrado para atualização.");
            return false; // Cliente não encontrado para atualização
        }
        // Adiciona o cliente atualizado
        clients.add(clienteAtualizado);
        // Salva a lista completa de volta
        return saveClients(clients);
    }

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
            return null;
        }

        File originalFile = new File(originalImagePath);
        String fileExtension = "";
        int i = originalImagePath.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalImagePath.substring(i);
        }

        String newFileName = cpf + fileExtension;
        File newFile = new File(PROFILE_PICS_DIR + newFileName);

        try {
            java.nio.file.Files.copy(originalFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return newFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem de perfil para " + cpf + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}