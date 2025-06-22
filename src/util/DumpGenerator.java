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

    public static void main(String[] args) {
        new File("dump/carros").mkdirs();
        new File("dump/moto").mkdirs();
        new File("dump/caminhao").mkdirs();
        new File("dump/locacoes").mkdirs();
        new File("dump/clientes").mkdirs();

        gerarClientes();
        gerarCarros();
        gerarMotos();
        gerarCaminhoes();
        gerarLocacoes();

        System.out.println("Dumps gerados com sucesso!");
    }

    private static void gerarClientes() { /* ... código idêntico ao anterior ... */
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/clientes/clientes.dat"))) {
            Endereco endC1 = new Endereco("Goiânia", "GO", "Centro", "Rua A", 100, "74000-000");
            Cliente cliente1 = new Cliente("João Silva", "123.456.789-00", "62998877665", "joao.silva@email.com", "senha123", endC1, LocalDateTime.of(1990, Month.JANUARY, 15, 0, 0), Sexo.MASCULINO, "");

            Endereco endC2 = new Endereco("Goiânia", "GO", "Setor Marista", "Avenida B", 50, "74150-100");
            Cliente cliente2 = new Cliente("Maria Oliveira", "987.654.321-99", "62991122334", "maria.oliveira@email.com", "senha456", endC2, LocalDateTime.of(1985, Month.MARCH, 20, 0, 0), Sexo.FEMININO, "");

            Endereco endC3 = new Endereco("Anápolis", "GO", "Jardim das Américas", "Rua C", 20, "75000-000");
            Cliente cliente3 = new Cliente("Pedro Souza", "111.222.333-44", "62995544332", "pedro.souza@email.com", "abc@123", endC3, LocalDateTime.of(1995, Month.JULY, 1, 0, 0), Sexo.MASCULINO, "");

            clientes.add(cliente1);
            clientes.add(cliente2);
            clientes.add(cliente3);

            for (Cliente c : clientes) {
                oos.writeObject(c);
            }
            System.out.println("Clientes gerados: " + clientes.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarCarros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/carros/carros.dat"))) {
            // Remova os dois últimos argumentos (locacoes, locado) do construtor
            Carro carro1 = new Carro("O Toyota Supra 2022 combina design arrojado com performance de alto nível: sua carroceria coupé possui linhas musculosas, capô longo e proporções equilibradas, realçando uma estética esportiva e moderna.", "ABC1234", "Toyota", "Supra", "2022", 2022,
                    Cor.VERMELHO, Funcao.PASSEIO, 500.0, 2, 8.5, 250, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel1.jpg",
                    350, true, true, true, true, true, 1500, false, true, 500.0, 4, true);

            Carro carro2 = new Carro("SUV Familiar", "XYZ9876", "Hyundai", "Creta", "2021", 2021,
                    Cor.BRANCO, Funcao.PASSEIO, 300.0, 5, 10.0, 190, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, true, "/images/carrossel/carrossel3.png",
                    130, true, true, true, true, false, 1600, false, true, 300.0, 4, true);

            Carro carro3 = new Carro("Sedan de Luxo", "DEF5678", "BMW", "Série 3", "2023", 2023,
                    Cor.AZUL, Funcao.PASSEIO, 450.0, 5, 9.0, 220, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 5, true, "/images/carrossel/carrossel1.jpg",
                    250, true, true, true, true, true, 1700, false, true, 450.0, 4, true);

            Carro carro4 = new Carro("Compacto Urbano", "GHI0001", "Fiat", "Argo", "2024", 2024,
                    Cor.PRETO, Funcao.PASSEIO, 150.0, 5, 12.0, 160, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, false, "/images/carrossel/carrossel3.png",
                    98, false, true, false, true, false, 1100, false, false, 150.0, 4, true);

            carro1.setLocacoes(12);
            carro2.setLocacoes(9);
            carro3.setLocacoes(6);
            carro4.setLocacoes(8);

            carros.add(carro1);
            carros.add(carro2);
            carros.add(carro3);
            carros.add(carro4);

            for (Carro c : carros) {
                oos.writeObject(c);
            }
            System.out.println("Carros gerados: " + carros.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar carros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarMotos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/moto/motos.dat"))) {
            // Remova os dois últimos argumentos (locacoes, locado) do construtor
            Moto moto1 = new Moto("Moto Urbana", "MOT1234", "Honda", "CG 160", "Fan", 2023,
                    Cor.PRETO, Funcao.PASSEIO, 80.0, 2, 40.0, 120, false,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/carrossel/carrossel2.jpg",
                    15.0, false, false, false, true, false, 120, false, false, 80.0,
                    160, false, 17);

            Moto moto2 = new Moto("Moto Esportiva", "ESP5678", "Yamaha", "R3", "R3", 2022,
                    Cor.AZUL, Funcao.PASSEIO, 200.0, 2, 25.0, 180, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel2.jpg",
                    42.0, true, true, true, true, false, 170, false, true, 200.0,
                    321, false, 17);

            Moto moto3 = new Moto("Scooter", "SCO9999", "Honda", "PCX 150", "DLX", 2024,
                    Cor.CINZA, Funcao.PASSEIO, 90.0, 2, 35.0, 100, true,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/carrossel/carrossel2.jpg",
                    13.0, false, false, false, true, false, 130, false, false, 90.0,
                    150, true, 14);

            motos.add(moto1);
            motos.add(moto2);
            motos.add(moto3);

            for (Moto m : motos) {
                oos.writeObject(m);
            }
            System.out.println("Motos geradas: " + motos.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar motos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarCaminhoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/caminhao/caminhoes.dat"))) {
            // Remova os dois últimos argumentos (locacoes, locado) do construtor
            Caminhao caminhao1 = new Caminhao("Caminhão Baú", "CAM1234", "Mercedes", "Axor", "2644", 2021,
                    Cor.VERMELHO, Funcao.VIAGEM, 700.0, 3, 5.5, 110, true,
                    Combustivel.DIESEL, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel2.jpg",
                    440, true, true, false, true, false, 8000, true, true, 700.0,
                    18000, 3.8, 2.5, 8.0, Vagao.BAU_SECO);

            Caminhao caminhao2 = new Caminhao("Caminhão Graneleiro", "CAM5678", "Volvo", "FH", "540", 2020,
                    Cor.BRANCO, Funcao.VIAGEM, 800.0, 2, 4.5, 100, true,
                    Combustivel.DIESEL, Tracao.INTEGRAL, 2, true, "/images/carrossel/carrossel2.jpg",
                    540, true, true, false, true, false, 10000, true, true, 800.0,
                    25000, 4.0, 2.6, 9.0, Vagao.GRANELEIRO);

            caminhoes.add(caminhao1);
            caminhoes.add(caminhao2);

            for (Caminhao c : caminhoes) {
                oos.writeObject(c);
            }
            System.out.println("Caminhões gerados: " + caminhoes.size());
        } catch (Exception e) {
            System.err.println("Erro ao gerar caminhões: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerarLocacoes() {
        if (carros.isEmpty() || clientes.isEmpty() || motos.isEmpty() || caminhoes.isEmpty()) {
            System.err.println("Não foi possível gerar locações: listas de veículos ou clientes estão vazias.");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/locacoes/locacoes.dat"))) {

            // Cenário 1: Carro 4 (Compacto Urbano) está ATUALMENTE LOCADO
            Locacao locacaoAtivaCarro = new Locacao(
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now().plusDays(3),
                    null,
                    carros.get(3), // Carro 4: Compacto Urbano (GHI0001)
                    clientes.get(0) // João Silva
            );
            oos.writeObject(locacaoAtivaCarro);
            System.out.println("Locação ativa para: " + carros.get(3).getNome() + " (" + carros.get(3).getPlaca() + ")");

            // Cenário 2: Moto 1 (Moto Urbana) JÁ FOI LOCADA e DEVOLVIDA
            Locacao locacaoAntigaMoto = new Locacao(
                    LocalDateTime.now().minusDays(10),
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now().minusDays(7),
                    motos.get(0), // Moto 1: Moto Urbana (MOT1234)
                    clientes.get(1) // Maria Oliveira
            );
            oos.writeObject(locacaoAntigaMoto);
            System.out.println("Locação devolvida para: " + motos.get(0).getNome() + " (" + motos.get(0).getPlaca() + ")");

            // Cenário 3: Caminhão 1 (Caminhão Baú) está PRÓXIMO DE DEVOLUÇÃO
            Locacao locacaoProximaDevolucaoCaminhao = new Locacao(
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().plusDays(1),
                    null,
                    caminhoes.get(0), // Caminhão 1: Caminhão Baú (CAM1234)
                    clientes.get(2) // Pedro Souza
            );
            oos.writeObject(locacaoProximaDevolucaoCaminhao);
            System.out.println("Locação próxima de devolução para: " + caminhoes.get(0).getNome() + " (" + caminhoes.get(0).getPlaca() + ")");

            // Cenário 4: Carro 1 (Carro Esportivo) está DISPONÍVEL (nunca locado neste dump)

        } catch (Exception e) {
            System.err.println("Erro ao gerar locações: " + e.getMessage());
            e.printStackTrace();
        }
    }
}