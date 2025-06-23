package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A classe `RoundedImageLabel` é um componente {@link JLabel} customizado
 * que exibe uma imagem com bordas arredondadas.
 * A imagem é recortada para se ajustar a um retângulo com cantos arredondados,
 * proporcionando uma estética visual mais suave.
 */
public class RoundedImageLabel extends JLabel {

    private int cornerRadius;

    /**
     * Construtor para `RoundedImageLabel`.
     *
     * @param icon         O {@link ImageIcon} a ser exibido dentro do label.
     * @param cornerRadius O raio dos cantos arredondados em pixels. Um valor maior resulta em cantos mais arredondados.
     */
    public RoundedImageLabel(ImageIcon icon, int cornerRadius) {
        super(icon);
        this.cornerRadius = cornerRadius;
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setOpaque(false);
    }

    /**
     * Sobrescreve o método `paintComponent` para desenhar o label com bordas arredondadas.
     * <p>
     * Este método cria uma cópia do contexto gráfico, configura a suavização de serrilhado
     * (`ANTI_ALIASING`), define um clipe de forma retangular arredondada e então
     * invoca o `paintComponent` da superclasse para desenhar a imagem dentro do clipe definido.
     * Isso garante que apenas a parte da imagem que se encaixa na forma arredondada seja visível.
     * </p>
     *
     * @param g O objeto {@link Graphics} a ser usado para desenhar o componente.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.setClip(roundedRectangle);
        super.paintComponent(g2);

        g2.dispose();
    }
}