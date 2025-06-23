package controller;

import entities.Cliente;
import entities.Funcionario;
import entities.Pessoa;
import exceptions.AuthControllerException;
import util.PasswordHasher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A classe AuthController gerencia operações de autenticação e persistência
 * de dados para usuários (Clientes e Funcionários), incluindo carregamento,
 * salvamento, registro e atualização de perfis, bem como o gerenciamento de fotos de perfil.
 */
public class AuthController {
    /**
     * Caminho do arquivo onde os dados dos clientes são serializados.
     */
    public static final String CLIENTS_FILE_PATH = "dump/clientes/clientes.dat";
    /**
     * Caminho do arquivo onde os dados dos funcionários são serializados.
     */
    public static final String EMPLOYEES_FILE_PATH = "dump/funcionarios/funcionarios.dat";
    /**
     * Diretório onde as fotos de perfil são armazenadas.
     */
    public static final String PROFILE_PICS_DIR = "dump/profile_pics/";

    /**
     * Construtor da classe AuthController.
     * Garante que os diretórios necessários para persistência de dados (clientes, funcionários, fotos de perfil)
     * existam, criando-os se não o fizerem.
     */
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
     * Tenta autenticar um usuário (Cliente ou Funcionario) com base no email e senha fornecidos.
     * A senha fornecida é primeiro hashed para comparação com as senhas armazenadas.
     *
     * @param email O email do usuário.
     * @param password A senha do usuário (texto simples).
     * @return O objeto Pessoa (Cliente ou Funcionario) se a autenticação for bem-sucedida e as credenciais corresponderem, caso contrário, null.
     */
    public Pessoa authenticate(String email, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            throw new AuthControllerException("Erro ao processar a senha.");
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

        throw new AuthControllerException("Email ou senha incorretos.");
    }

    /**
     * Salva a lista de funcionários em um arquivo serializado no caminho {@code EMPLOYEES_FILE_PATH}.
     *
     * @param employees A lista de objetos Funcionario a ser salva.
     * @return true se a operação de salvamento for bem-sucedida, false caso contrário.
     */
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

    /**
     * Carrega a lista de funcionários de um arquivo serializado.
     * Se o arquivo não existir, estiver vazio ou houver um erro durante a leitura,
     * uma lista vazia é retornada. Inclui tratamento para formatos de serialização antigos (objeto por objeto).
     *
     * @return Uma lista de objetos Funcionario.
     */
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

    /**
     * Carrega a lista de clientes de um arquivo serializado no caminho {@code CLIENTS_FILE_PATH}.
     *
     * @return Uma lista de objetos Cliente. Retorna uma lista vazia se o arquivo não existir ou houver erro durante a leitura.
     */
    public List<Cliente> loadClients() {
        List<Cliente> clients = new ArrayList<>(); // Inicia com uma lista mutável
        File file = new File(CLIENTS_FILE_PATH);

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    // A SOLUÇÃO: Sempre crie um novo ArrayList a partir da lista carregada
                    clients = new ArrayList<>((List<Cliente>) obj); //
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler clientes do arquivo: " + e.getMessage());
                clients = new ArrayList<>(); // Garante que clients não seja nulo ou problemático em caso de erro
            } catch (ClassNotFoundException e) {
                System.err.println("Classe Cliente não encontrada durante a desserialização: " + e.getMessage());
                clients = new ArrayList<>(); // Garante que clients não seja nulo ou problemático em caso de erro
            }
        }
        return clients;
    }

    /**
     * Salva a lista de clientes em um arquivo serializado no caminho {@code CLIENTS_FILE_PATH}.
     *
     * @param clients A lista de objetos Cliente a ser salva.
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
     * Atualiza um cliente existente na lista de clientes e salva a lista atualizada no arquivo.
     * O cliente é identificado pelo seu CPF. Se o cliente não for encontrado, a operação falha.
     *
     * @param clienteAtualizado O objeto Cliente com os dados atualizados.
     * @return true se o cliente foi encontrado, atualizado e a lista salva com sucesso, false caso contrário.
     */
    public boolean updateCliente(Cliente clienteAtualizado) {
        List<Cliente> clients = loadClients();
        boolean removed = clients.removeIf(c -> c.getCpf().equals(clienteAtualizado.getCpf()));
        if (!removed) {
            throw new AuthControllerException("Erro: Cliente com CPF '" + clienteAtualizado.getCpf() + "' não encontrado para atualização.");
        }
        clients.add(clienteAtualizado);
        return saveClients(clients);
    }

    /**
     * Adiciona um novo cliente à lista e o salva no arquivo.
     * Antes de adicionar, verifica se o email ou CPF do novo cliente já existe.
     * A senha do cliente é hashed antes de ser salva. A foto de perfil também é salva.
     *
     * @param newClient O novo cliente a ser adicionado.
     * @return true se o cliente foi adicionado e salvo com sucesso, false caso contrário (ex: email/CPF já existe ou falha no hashing/salvamento da foto).
     */
    public boolean registerClient(Cliente newClient) {
        List<Cliente> clients = loadClients();

        boolean exists = clients.stream().anyMatch(c ->
                c.getEmail().equals(newClient.getEmail()) || c.getCpf().equals(newClient.getCpf()));

        if (exists) {
            throw new AuthControllerException("Email ou CPF já cadastrado.");
        }

        String hashedPassword = PasswordHasher.hashPassword(newClient.getSenha());
        if (hashedPassword == null) {
            throw new AuthControllerException("Erro ao processar a senha para registro.");
        }
        newClient.setSenha(hashedPassword);

        String savedPhotoPath = saveProfilePicture(newClient.getCaminhoFoto(), newClient.getCpf());
        newClient.setCaminhoFoto(savedPhotoPath);
        clients.add(newClient);
        return saveClients(clients);
    }

    /**
     * Salva uma imagem de perfil em um diretório específico, renomeando-a com o CPF do usuário.
     * Se o caminho da imagem original for nulo ou vazio, ou se houver um erro durante o salvamento, retorna null.
     *
     * @param originalImagePath O caminho absoluto da imagem original a ser copiada.
     * @param cpf O CPF do usuário, usado para nomear o novo arquivo de imagem.
     * @return O caminho absoluto do arquivo de imagem salvo, ou null em caso de falha.
     */
    public String saveProfilePicture(String originalImagePath, String cpf) {
        if (originalImagePath == null || originalImagePath.isEmpty()) {
            throw new AuthControllerException("Caminho de foto inválido: " + originalImagePath);
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