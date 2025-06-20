package util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageScaler {
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

    /**
     * Redimensiona uma imagem com transparência usando fator de escala, preservando qualidade e canal alpha.
     *
     * @param originalImage Imagem original com ou sem transparência
     * @param scaleFactor   Fator de escala (0.5 = reduz pela metade, 2.0 = dobra)
     * @return BufferedImage redimensionada com preservação de transparência
     */
    public static BufferedImage resizeImageByScale(Image originalImage, float scaleFactor) {
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException("O fator de escala deve ser maior que zero.");
        }

        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);
        int newWidth = Math.round(originalWidth * scaleFactor);
        int newHeight = Math.round(originalHeight * scaleFactor);

        // Usa ARGB para manter transparência
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Ativa qualidade máxima de renderização e suporte ao canal alpha
        g2d.setComposite(AlphaComposite.Src);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }
}
