package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; // Importar para formas arredondadas

public class RoundedImageLabel extends JLabel {

    private int cornerRadius;

    public RoundedImageLabel(ImageIcon icon, int cornerRadius) {
        super(icon); // Chama o construtor do JLabel para definir o ícone
        this.cornerRadius = cornerRadius;
        setHorizontalAlignment(CENTER); // Centraliza a imagem no label
        setVerticalAlignment(CENTER);
        // Não é necessário setOpaque(false) aqui se você remover o fillRect,
        // mas é uma boa prática para indicar que o componente é transparente.
        setOpaque(false); // Garante que o componente não pinte seu próprio fundo por padrão.
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Suaviza as bordas

        // Cria um clip com cantos arredondados
        // Subtrai 1 da largura e altura para evitar problemas de arredondamento em algumas plataformas
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.setClip(roundedRectangle);

        // REMOVIDO: As linhas que pintavam o background explicitamente
        // g2.setColor(getBackground());
        // g2.fillRect(0, 0, getWidth(), getHeight());

        // Desenha a imagem (chama o método paintComponent original do JLabel)
        super.paintComponent(g2);

        g2.dispose(); // Libera os recursos gráficos
    }
}