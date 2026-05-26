package br.com.locar.entities;

import br.com.locar.enums.Combustivel;
import br.com.locar.enums.Cor;
import br.com.locar.enums.Funcao;
import br.com.locar.enums.Tracao;

import java.io.Serial;

public class TestVehicle extends Veiculo {
    @Serial
    private static final long serialVersionUID = 1L;

    public TestVehicle(String placa, double valorDiario) {
        super("DescTest", placa, "MarcaTest", "NomeTest", "ModeloTest", 2020, Cor.PRETO, Funcao.PASSEIO,
                10000, 5, 10.0, 180.0, true, Combustivel.GASOLINA, Tracao.DIANTEIRA,
                5, true, "foto.jpg", 150.0, true, true, true, true,
                false, 1200.0, false, true, valorDiario);
    }

    public void adicionarLocacao() {
        setLocacoes(getLocacoes() + 1);
    }
}