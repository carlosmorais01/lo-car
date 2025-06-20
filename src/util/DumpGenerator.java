package util;

import entities.*;
import enums.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

public class DumpGenerator {
    public static void main(String[] args) {
        gerarCarros();
        gerarMotos();
        gerarCaminhoes();
    }

    private static void gerarCarros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/carros/carros.dat"))) {
            Carro carro1 = new Carro("Carro esportivo", "ABC1234", "Toyota", "Supra", "2022", 2022,
                    Cor.VERMELHO, Funcao.PASSEIO, 15000, 2, 8.5, 250, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel1.jpg",
                    350, true, true, true, true, true, 1500, false, true, 500.0, 2, false);

            Carro carro2 = new Carro("SUV familiar", "XYZ9876", "Hyundai", "Creta", "2021", 2021,
                    Cor.BRANCO, Funcao.PASSEIO, 40000, 5, 10.0, 190, false,
                    Combustivel.FLEX, Tracao.DIANTEIRA, 5, true, "/images/carrossel/carrossel3.png",
                    130, true, true, true, true, false, 1600, false, true, 300.0, 5, true);

            oos.writeObject(carro1);
            oos.writeObject(carro2);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static void gerarMotos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/moto/motos.dat"))) {
            Moto moto1 = new Moto("Moto urbana", "MOT1234", "Honda", "CG 160", "Fan", 2023,
                    Cor.PRETO, Funcao.PASSEIO, 5000, 2, 40.0, 120, false,
                    Combustivel.GASOLINA, Tracao.DIANTEIRA, 2, false, "/images/carrossel/carrossel2.jpg",
                    15.0, false, false, false, true, false, 120, false, false, 80.0,
                    160, false, 17, 1, false);

            Moto moto2 = new Moto("Moto esportiva", "ESP5678", "Yamaha", "R3", "R3", 2022,
                    Cor.AZUL, Funcao.PASSEIO, 8000, 2, 25.0, 180, true,
                    Combustivel.GASOLINA, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel2.jpg",
                    42.0, true, true, true, true, false, 170, false, true, 200.0,
                    321, false, 17, 3, true);

            oos.writeObject(moto1);
            oos.writeObject(moto2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void gerarCaminhoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump/caminhao/caminhoes.dat"))) {
            Caminhao caminhao1 = new Caminhao("Caminhão baú", "CAM1234", "Mercedes", "Axor", "2644", 2021,
                    Cor.VERMELHO, Funcao.VIAGEM, 60000, 3, 5.5, 110, true,
                    Combustivel.DIESEL, Tracao.TRASEIRA, 2, true, "/images/carrossel/carrossel2.jpg",
                    440, true, true, false, true, false, 8000, true, true, 700.0,
                    18000, 3.8, 2.5, 8.0, Vagao.BAU_SECO, 2, false);

            Caminhao caminhao2 = new Caminhao("Caminhão graneleiro", "CAM5678", "Volvo", "FH", "540", 2020,
                    Cor.BRANCO, Funcao.VIAGEM, 120000, 2, 4.5, 100, true,
                    Combustivel.DIESEL, Tracao.INTEGRAL, 2, true, "/images/carrossel/carrossel2.jpg",
                    540, true, true, false, true, false, 10000, true, true, 800.0,
                    25000, 4.0, 2.6, 9.0, Vagao.GRANELEIRO, 4, true);

            oos.writeObject(caminhao1);
            oos.writeObject(caminhao2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
