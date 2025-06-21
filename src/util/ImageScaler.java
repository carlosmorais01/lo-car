package util;

import javax.swing.*;
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
     * Reescalona uma imagem para as dimensões especificadas.
     * @param originalImage A imagem original a ser reescalada.
     * @param targetWidth A largura desejada.
     * @param targetHeight A altura desejada.
     * @return A imagem reescalada.
     */
    public static Image scaleImage(Image originalImage, int targetWidth, int targetHeight) {
        if (originalImage == null) {
            return null;
        }
        // Usamos Image.SCALE_SMOOTH para uma melhor qualidade de reescalonamento
        return originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }

    /**
     * Reescalona uma imagem carregada de um caminho de arquivo.
     * Este método é útil se você quiser carregar e escalar em uma única etapa.
     * @param imagePath O caminho do arquivo da imagem.
     * @param targetWidth A largura desejada.
     * @param targetHeight A altura desejada.
     * @return A imagem reescalada.
     */
    public static Image scaleImage(String imagePath, int targetWidth, int targetHeight) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        return scaleImage(originalIcon.getImage(), targetWidth, targetHeight);
    }
}
