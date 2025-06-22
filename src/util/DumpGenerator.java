package util;

import entities.*;
import enums.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class DumpGenerator {

    private static List<Carro> carros = new ArrayList<>();
    private static List<Moto> motos = new ArrayList<>();
    private static List<Caminhao> caminhoes = new ArrayList<>();
    private static List<Cliente> clientes = new ArrayList<>();
    private static List<Funcionario> funcionarios = new ArrayList<>();

    private static final String CLIENTES_FILE = "dump/clientes/clientes.dat";
    private static final String FUNCIONARIOS_FILE = "dump/funcionarios/funcionarios.dat";
    private static final String CARROS_FILE = "dump/carros/carros.dat";
    private static final String MOTOS_FILE = "dump/moto/motos.dat";
    private static final String CAMINHOES_FILE = "dump/caminhao/caminhoes.dat";
    private static final String LOCACOES_FILE = "dump/locacoes/locacoes.dat";

    public static void rodarDump() {
        new File("dump/carros").mkdirs();
        new File("dump/moto").mkdirs();
        new File("dump/caminhao").mkdirs();
        new File("dump/locacoes").mkdirs();
        new File("dump/clientes").mkdirs();
        new File("dump/funcionarios").mkdirs();

        boolean anyFileMissing = false;

        if (!new File(CLIENTES_FILE).exists()) {
            gerarClientes();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de clientes já existe. Pulando geração.");
        }

        if (!new File(FUNCIONARIOS_FILE).exists()) {
            gerarFuncionarios();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de funcionários já existe. Pulando geração.");
        }

        if (!new File(CARROS_FILE).exists()) {
            gerarCarros();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de carros já existe. Pulando geração.");
        }

        if (!new File(MOTOS_FILE).exists()) {
            gerarMotos();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de motos já existe. Pulando geração.");
        }

        if (!new File(CAMINHOES_FILE).exists()) {
            gerarCaminhoes();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de caminhões já existe. Pulando geração.");
        }

        if (!new File(LOCACOES_FILE).exists()) {
            gerarLocacoes();
            anyFileMissing = true;
        } else {
            System.out.println("Arquivo de locações já existe. Pulando geração.");
        }

        if (anyFileMissing) {
            System.out.println("Dumps gerados/atualizados com sucesso!");
        } else {
            System.out.println("Todos os arquivos .dat já existem. Nenhuma geração de dump foi necessária.");
        }
    }


    private static void gerarFuncionarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FUNCIONARIOS_FILE))) {
            Endereco endF1 = new Endereco("Goiânia", "GO", "Vila Nova", "Rua D", 300, "74640-000");
            Funcionario funcionario1 = new Funcionario("Ana Paula", "000.111.222-33", "62999998888", "ana.paula@locar.com", "221ef4c8ca4620d4e6641c3a1433e50b531a9e10aa097576add3ecfd7cf24c67", endF1, LocalDateTime.of(1988, Month.FEBRUARY, 10, 0, 0), Sexo.FEMININO, "/images/icons/logo.png");

            Endereco endF2 = new Endereco("Rio Verde", "GO", "Centro", "Av. Principal", 120, "75901-000");
            Funcionario funcionario2 = new Funcionario("Bruno Costa", "444.555.666-77", "64987654321", "bruno.costa@locar.com", "d62e4326b7bf99bbb6c7b6bfe9ef6367f24a9cc94f4ac04a475f150a996beb95", endF2, LocalDateTime.of(1992, Month.SEPTEMBER, 5, 0, 0), Sexo.MASCULINO, "/images/icons/logo.png");

            funcionarios.add(funcionario1);
            funcionarios.add(funcionario2);

            oos.writeObject(funcionarios);
            System.out.println("Funcionários gerados: " + funcionarios.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar funcionários: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarClientes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENTES_FILE))) {
            Endereco endC1 = new Endereco("Goiânia", "GO", "Centro", "Rua A", 100, "74000-000");
            Cliente cliente1 = new Cliente("João Silva", "123.456.789-00", "62998877665", "joao.silva@gmail.com", "55a5e9e78207b4df8699d60886fa070079463547b095d1a05bc719bb4e6cd251", endC1, LocalDateTime.of(1990, Month.JANUARY, 15, 0, 0), Sexo.MASCULINO, "/images/pfp_dump/joao.png");

            Endereco endC2 = new Endereco("Goiânia", "GO", "Setor Marista", "Avenida B", 50, "74150-100");
            Cliente cliente2 = new Cliente("Maria Oliveira", "987.654.321-99", "62991122334", "maria.oliveira@gmail.com", "6b08d780140e292a4af8ba3f2333fc1357091442d7e807c6cad92e8dcd0240b7", endC2, LocalDateTime.of(1985, Month.MARCH, 20, 0, 0), Sexo.FEMININO, "/images/pfp_dump/maria.png");

            Endereco endC3 = new Endereco("Anápolis", "GO", "Jardim das Américas", "Rua C", 20, "75000-000");
            Cliente cliente3 = new Cliente("Pedro Souza", "111.222.333-44", "62995544332", "pedro.souza@gmail.com", "e5857b335afdf35ca81a110bc81f38682f8a89892cc597f5398dfef82d42b513", endC3, LocalDateTime.of(1995, Month.JULY, 1, 0, 0), Sexo.MASCULINO, "/images/pfp_dump/pedro.png");

            clientes.add(cliente1);
            clientes.add(cliente2);
            clientes.add(cliente3);

            oos.writeObject(clientes);
            System.out.println("Clientes gerados: " + clientes.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarCarros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CARROS_FILE))) {
            Carro carro1 = new Carro("O Toyota Supra 2022 combina design arrojado com performance de alto nível: sua carroceria coupé possui linhas musculosas.", "ABC1234", "Toyota", "Supra", "2022", 2022,
                    Cor.BRANCO, Funcao.PASSEIO, 500.0, 2, 8.5, 250, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/veiculos/ABC1234.jpeg",
                    350, true, true, true, true, true, 1500, false, true, 500.0, 4, true);

            Carro carro2 = new Carro("O Hyundai Creta 2021 combina design moderno, bom espaço interno e tecnologia, ideal para quem busca um SUV urbano versátil e confortável.", "XYZ9876", "Hyundai", "Creta", "2021", 2021,
                    Cor.CINZA, Funcao.PASSEIO, 300.0, 5, 10.0, 190, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, true, "/images/veiculos/XYZ9876.png",
                    130, true, true, true, true, false, 1600, false, true, 300.0, 4, true);

            Carro carro3 = new Carro("A BMW Série 3 2023 une desempenho esportivo, tecnologia de ponta e elegância, oferecendo uma experiência premium ao dirigir.", "DEF5678", "BMW", "Série 3", "2023", 2023,
                    Cor.PRETO, Funcao.PASSEIO, 450.0, 5, 9.0, 220, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 5, true, "/images/veiculos/DEF5678.jpg",
                    250, true, true, true, true, true, 1700, false, true, 450.0, 4, true);

            Carro carro4 = new Carro("O Fiat Argo 2024 oferece um design renovado, economia de combustível e praticidade, sendo uma ótima opção para o dia a dia urbano.", "GHI0001", "Fiat", "Argo", "2024", 2024,
                    Cor.BRANCO, Funcao.PASSEIO, 150.0, 5, 12.0, 160, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, false, "/images/veiculos/GHI0001.jpg",
                    98, false, true, false, true, false, 1100, false, false, 150.0, 4, true);

            carro1.setLocacoes(12);
            carro2.setLocacoes(9);
            carro3.setLocacoes(6);
            carro4.setLocacoes(8);

            carros.add(carro1);
            carros.add(carro2);
            carros.add(carro3);
            carros.add(carro4);

            oos.writeObject(carros);
            System.out.println("Carros gerados: " + carros.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar carros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarMotos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MOTOS_FILE))) {
            Moto moto1 = new Moto("A Honda CG 160 Fan combina economia, robustez e conforto, sendo ideal para o uso diário com baixo custo de manutenção.", "MOT1234", "Honda", "CG 160", "Fan", 2023,
                    Cor.CINZA, Funcao.PASSEIO, 80.0, 2, 40.0, 120, false,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/veiculos/MOT1234.jpg",
                    15.0, false, false, false, true, false, 120, false, false, 80.0,
                    160, false, 17);

            Moto moto2 = new Moto("A Yamaha R3 2022 entrega desempenho esportivo, design agressivo e pilotagem ágil, perfeita para quem busca emoção sobre duas rodas.", "ESP5678", "Yamaha", "R3", "R3", 2022,
                    Cor.AZUL, Funcao.PASSEIO, 200.0, 2, 25.0, 180, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/veiculos/ESP5678.jpg",
                    42.0, true, true, true, true, false, 170, false, true, 200.0,
                    321, false, 17);

            Moto moto3 = new Moto("A Honda PCX 150 DLX 2024 oferece elegância, tecnologia e conforto urbano, combinando motor econômico com recursos premium para o dia a dia.", "SCO9999", "Honda", "PCX 150", "DLX", 2024,
                    Cor.BRANCO, Funcao.PASSEIO, 90.0, 2, 35.0, 100, true,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/veiculos/SCO9999.jpg",
                    13.0, false, false, false, true, false, 130, false, false, 90.0,
                    150, true, 14);

            motos.add(moto1);
            motos.add(moto2);
            motos.add(moto3);

            oos.writeObject(motos);
            System.out.println("Motos geradas: " + motos.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar motos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarCaminhoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMINHOES_FILE))) {
            Caminhao caminhao1 = new Caminhao("A Mercedes-Benz Axor 2644 2021 é um caminhão potente, confortável e ideal para o transporte pesado em longas distâncias.", "CAM1234", "Mercedes", "Axor", "2644", 2021,
                    Cor.BRANCO, Funcao.VIAGEM, 700.0, 3, 5.5, 110, true,
                    Combustivel.DIESEL, Tracao.TRASEIRA, 2, true, "/images/veiculos/CAM1234.png",
                    440, true, true, false, true, false, 8000, true, true, 700.0,
                    18000, 3.8, 2.5, 8.0, Vagao.BAU_SECO);

            Caminhao caminhao2 = new Caminhao("O Volvo FH 540 2020 é um caminhão pesado de alta performance, ideal para longas distâncias, oferecendo conforto e eficiência.", "CAM5678", "Volvo", "FH", "540", 2020,
                    Cor.CINZA, Funcao.VIAGEM, 800.0, 2, 4.5, 100, true,
                    Combustivel.DIESEL, Tracao.INTEGRAL, 2, true, "/images/veiculos/CAM5678.jpg",
                    540, true, true, false, true, false, 10000, true, true, 800.0,
                    25000, 4.0, 2.6, 9.0, Vagao.GRANELEIRO);

            caminhoes.add(caminhao1);
            caminhoes.add(caminhao2);

            oos.writeObject(caminhoes);
            System.out.println("Caminhões gerados: " + caminhoes.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar caminhões: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarLocacoes() {

        if (carros.isEmpty() || clientes.isEmpty() || motos.isEmpty() || caminhoes.isEmpty()) {
            System.err.println("Não foi possível gerar locações: listas de veículos ou clientes estão vazias (verifique se os dumps foram gerados/lidos antes).");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCACOES_FILE))) {

            // Cenário 1: Carro 4 (Compacto Urbano) está ATUALMENTE LOCADO
            Locacao locacaoAtivaCarro = new Locacao(
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now().plusDays(3),
                    null,
                    carros.get(3), // Carro 4: Compacto Urbano (GHI0001)
                    clientes.get(0) // João Silva
            );

            // Cenário 2: Moto 1 (Moto Urbana) JÁ FOI LOCADA e DEVOLVIDA
            Locacao locacaoAntigaMoto = new Locacao(
                    LocalDateTime.now().minusDays(10),
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now().minusDays(7),
                    motos.get(0), // Moto 1: Moto Urbana (MOT1234)
                    clientes.get(1) // Maria Oliveira
            );

            // Cenário 3: Caminhão 1 (Caminhão Baú) está PRÓXIMO DE DEVOLUÇÃO
            Locacao locacaoProximaDevolucaoCaminhao = new Locacao(
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().plusDays(1),
                    null,
                    caminhoes.get(0), // Caminhão 1: Caminhão Baú (CAM1234)
                    clientes.get(2) // Pedro Souza
            );

            List<Locacao> todasAsLocacoes = new ArrayList<>();
            todasAsLocacoes.add(locacaoAtivaCarro);
            todasAsLocacoes.add(locacaoAntigaMoto);
            todasAsLocacoes.add(locacaoProximaDevolucaoCaminhao);

            oos.writeObject(todasAsLocacoes);
            System.out.println("Locações geradas: " + todasAsLocacoes.size());

        } catch (Exception e) {
            System.err.println("Erro ao gerar locações: " + e.getMessage());
            e.printStackTrace();
        }
    }
}