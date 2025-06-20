package application;

import entities.*;
import enums.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Veiculo> veiculos = new ArrayList<>();

        String path = Paths.get("dump", "carros.txt").toString();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha = br.readLine();

            while (linha != null && !linha.isEmpty()) {
                String[] dados = linha.split("#");

                Carro c = new Carro(
                        dados[0],                            // descrição
                        dados[1],                            // placa
                        dados[2],                            // marca
                        dados[3],                            // nome
                        dados[4],                            // modelo
                        Integer.parseInt(dados[5]),          // ano
                        Cor.valueOf(dados[6]),              // cor
                        Funcao.valueOf(dados[7]),           // função
                        Double.parseDouble(dados[8]),       // quilometragem
                        Integer.parseInt(dados[9]),         // número de passageiros
                        Double.parseDouble(dados[10]),      // consumo combustível
                        Double.parseDouble(dados[11]),      // velocidade máxima
                        Boolean.parseBoolean(dados[12]),    // automático
                        Combustivel.valueOf(dados[13]),     // combustível
                        Tracao.valueOf(dados[14]),          // tração
                        Integer.parseInt(dados[15]),        // quantidade de assentos
                        Boolean.parseBoolean(dados[16]),    // air bag
                        dados[17],                          // caminho foto
                        Double.parseDouble(dados[18]),      // potência
                        Boolean.parseBoolean(dados[19]),    // vidro elétrico
                        Boolean.parseBoolean(dados[20]),    // ar condicionado
                        Boolean.parseBoolean(dados[21]),    // multimídia
                        Boolean.parseBoolean(dados[22]),    // entrada USB
                        Boolean.parseBoolean(dados[23]),    // vidro fumê
                        Double.parseDouble(dados[24]),      // peso
                        Boolean.parseBoolean(dados[25]),    // engate
                        Boolean.parseBoolean(dados[26]),    // direção hidráulica
                        Double.parseDouble(dados[27]),      // valor diário
                        Integer.parseInt(dados[28]),        // portas
                        Boolean.parseBoolean(dados[29])     // aerofólio
                );

                veiculos.add(c);
                linha = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Erro no formato do arquivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao converter valor de enum: " + e.getMessage());
        }

        Endereco end = new Endereco(
                "casad",
                "asdad",
                "dasdasd",
                "asdads",
                12313,
                "123123213"
        );

        Cliente c1 = new Cliente(
                "Carlos",
                "1231313",
                "3131231",
                "asdasdadas",
                "dasdasd",
                end,
                LocalDateTime.now().minusYears(19),
                Sexo.MASCULINO
        );

        Locacao l1 = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(7).plusHours(3), veiculos.get(0), c1);
        Locacao l2 = new Locacao(LocalDateTime.now(), LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(7), veiculos.get(1), c1);
        System.out.println(l1.calcularValorTotal());
        System.out.println(l2.calcularValorTotal());

    }
}