package util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A classe `ImageScaler` fornece métodos utilitários para reescalar imagens.
 * Ela inclui funcionalidades para reescalar uma imagem diretamente para um {@code BufferedImage}
 * com alta qualidade de renderização, bem como métodos para reescalar a partir de um objeto {@code Image}
 * ou diretamente de um caminho de arquivo.
 */
public class ImageScaler {
    /**
     * Reescalona uma imagem de origem para as dimensões especificadas,
     * utilizando renderização de alta qualidade para um {@code BufferedImage}.
     * Este método é útil para obter um controle mais fino sobre a qualidade do reescalonamento.
     *
     * @param srcImg A imagem de origem a ser reescalada.
     * @param w A largura desejada para a imagem reescalada.
     * @param h A altura desejada para a imagem reescalada.
     * @return Um {@code BufferedImage} contendo a imagem reescalada com as dimensões e qualidade especificadas.
     */
    public static BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}