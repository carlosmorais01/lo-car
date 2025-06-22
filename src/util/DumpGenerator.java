package util;

import entities.*;
import enums.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.Month; // Para usar Month.JANUARY, etc.
import java.io.File; // Para criar diretórios

public class DumpGenerator {
    public static void main(String[] args) {
        // Garante que os diretórios existem
        new File("dump/carros").mkdirs();
        new File("dump/moto").mkdirs();
        new File("dump/caminhao").mkdirs();
        new File("dump/locacoes").mkdirs();
        new File("dump/clientes").mkdirs();

        gerarCarros();
        gerarMotos();
        gerarCaminhoes();
        gerarLocacoes();
    }

    private static void gerarCarros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/carros/carros.dat"))) {
            Carro carro1 = new Carro("Carro esportivo", "ABC1234", "Toyota", "Supra", "2022", 2022,
                    Cor.VERMELHO, Funcao.PASSEIO, 15000, 2, 8.5, 250, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/veiculos/supramk4.jpg",
                    350, true, true, true, true, true, 1500, false, true, 500.0, 2, false);

            Carro carro2 = new Carro("SUV familiar", "XYZ9876", "Hyundai", "Creta", "2021", 2021,
                    Cor.BRANCO, Funcao.PASSEIO, 40000, 5, 10.0, 190, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, true, "/images/veiculos/creta.jpg",
                    130, true, true, true, true, false, 1600, false, true, 300.0, 5, true);

            Carro carro3 = new Carro("Sedan de luxo", "DEF5678", "BMW", "Série 3", "2023", 2023,
                    Cor.AZUL, Funcao.PASSEIO, 8000, 5, 9.0, 220, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 5, true, "/images/veiculos/bmwserie3.jpg",
                    250, true, true, true, true, true, 1700, false, true, 450.0, 4, false);

            oos.writeObject(carro1);
            oos.writeObject(carro2);
            oos.writeObject(carro3);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static void gerarMotos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/moto/motos.dat"))) {
            Moto moto1 = new Moto("Moto urbana", "MOT1234", "Honda", "CG 160", "Fan", 2023,
                    Cor.PRETO, Funcao.PASSEIO, 5000, 2, 40.0, 120, false,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/veiculos/CG160.jpg",
                    15.0, false, false, false, true, false, 120, false, false, 80.0,
                    160, false, 17, 1, false);

            Moto moto2 = new Moto("Moto esportiva", "ESP5678", "Yamaha", "R3", "R3", 2022,
                    Cor.AZUL, Funcao.PASSEIO, 8000, 2, 25.0, 180, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/veiculos/YamahaR3.jpg",
                    42.0, true, true, true, true, false, 170, false, true, 200.0,
                    321, false, 17, 3, true);

            Moto moto3 = new Moto("Scooter", "SCO9999", "Honda", "PCX 150", "DLX", 2024,
                    Cor.CINZA, Funcao.PASSEIO, 1200, 2, 35.0, 100, true,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/veiculos/hondaPCX.jpeg",
                    13.0, false, false, false, true, false, 130, false, false, 90.0,
                    150, true, 14, 0, false);

            oos.writeObject(moto1);
            oos.writeObject(moto2);
            oos.writeObject(moto3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void gerarCaminhoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/caminhao/caminhoes.dat"))) {
            Caminhao caminhao1 = new Caminhao("Caminhão baú", "CAM1234", "Mercedes", "Axor", "2644", 2021,
                    Cor.VERMELHO, Funcao.VIAGEM, 60000, 3, 5.5, 110, true,
                    Combustivel.DIESEL, Tracao.TRASEIRA, 2, true, "/images/veiculos/mercedesAxor.png",
                    440, true, true, false, true, false, 8000, true, true, 700.0,
                    18000, 3.8, 2.5, 8.0, Vagao.BAU_SECO, 2, false);

            Caminhao caminhao2 = new Caminhao("Caminhão graneleiro", "CAM5678", "Volvo", "FH", "540", 2020,
                    Cor.BRANCO, Funcao.VIAGEM, 120000, 2, 4.5, 100, true,
                    Combustivel.DIESEL, Tracao.INTEGRAL, 2, true, "/images/veiculos/VolvoFH.jpg",
                    540, true, true, false, true, false, 10000, true, true, 800.0,
                    25000, 4.0, 2.6, 9.0, Vagao.GRANELEIRO, 4, true);

            oos.writeObject(caminhao1);
            oos.writeObject(caminhao2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void gerarLocacoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/locacoes/locacoes.dat"))) {
            // IMPORTANTE: Para fins de serialização, os objetos Veiculo e Cliente dentro de Locacao
            // precisam ser as mesmas instâncias ou ter os mesmos valores identificadores (ex: placa, CPF)
            // que foram serializados nos seus próprios arquivos .dat, para que o controller os encontre.
            // Para simplicidade no dump, estou recriando-os aqui.

            // Clientes (recriando para ter instâncias para a locação)
            Endereco endC1 = new Endereco("Goiânia", "GO", "Centro", "Rua 1", 100, "74000-000");
            Cliente cliente1Loc = new Cliente("João Silva", "123.456.789-00", "62998877665", "joao.silva@email.com", "senha123", endC1, LocalDateTime.of(1990, Month.JANUARY, 15, 0, 0), Sexo.MASCULINO, "");

            Endereco endC2 = new Endereco("Goiânia", "GO", "Setor Marista", "Avenida Principal", 50, "74150-100");
            Cliente cliente2Loc = new Cliente("Maria Oliveira", "987.654.321-99", "62991122334", "maria.oliveria@email.com", "senha456", endC2, LocalDateTime.of(1985, Month.MARCH, 20, 0, 0), Sexo.FEMININO, "");

            // Veículos (recriando para ter instâncias para a locação)
            // Usar as PLACAS que foram usadas em gerarCarros/gerarMotos
            Carro carroParaLocacao = new Carro("Sedan de luxo", "DEF5678", "BMW", "Série 3", "2023", 2023,
                    Cor.AZUL, Funcao.PASSEIO, 8000, 5, 9.0, 220, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 5, true, "/images/carrossel/carrossel1.jpg",
                    250, true, true, true, true, true, 1700, false, true, 450.0, 4, false);

            Moto motoParaLocacao = new Moto("Scooter", "SCO9999", "Honda", "PCX 150", "DLX", 2024,
                    Cor.CINZA, Funcao.PASSEIO, 1200, 2, 35.0, 100, true,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/carrossel/carrossel2.jpg",
                    13.0, false, false, false, true, false, 130, false, false, 90.0,
                    150, true, 14, 0, false);

            // Locação ATIVA para o carro (data de devolução é null)
            Locacao locacaoCarroAtiva = new Locacao(
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().plusDays(2),
                    null, // Ainda não devolvido
                    carroParaLocacao,
                    cliente1Loc
            );

            // Locação JÁ DEVOLVIDA
            Locacao locacaoMotoDevolvida = new Locacao(
                    LocalDateTime.now().minusDays(10),
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now().minusDays(7).plusHours(2), // Devolvido 2h depois do previsto
                    motoParaLocacao,
                    cliente2Loc
            );

            oos.writeObject(locacaoCarroAtiva);
            oos.writeObject(locacaoMotoDevolvida);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}