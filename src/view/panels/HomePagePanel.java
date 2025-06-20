package view.panels;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomePagePanel {
    private JPanel contentPanel;
    private JTextField textField1;
    private JButton button1;
    private JLabel profilePicture;
    private JPanel imagemCarrosselPanel; // substitui o antigo JLabel
    private JPanel textoCarrosMaisAlugados;

    private CardLayout carrosselLayout;
    private Timer timer;
    private int imagemAtual = 0;
    private final String[] imagens = {
            "../../images/carrossel/carrossel1.jpg",
            "../../images/carrossel/carrossel2.jpg",
            "../../images/carrossel/carrossel3.png"
    };

    public HomePagePanel() {
        // Foto de perfil
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/pfp.png")));
        Image imagemRedimensionada = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        profilePicture.setIcon(new ImageIcon(imagemRedimensionada));

        // Configuração do painel do carrossel
        carrosselLayout = new CardLayout();
        imagemCarrosselPanel.setLayout(carrosselLayout);
        imagemCarrosselPanel.setBorder(new LineBorder(new Color(243, 243, 243), 7, true));

        // Adiciona as imagens ao painel do carrossel
        for (int i = 0; i < imagens.length; i++) {
            ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagens[i])));
            Image imgRedimensionada = img.getImage().getScaledInstance(800, 400, Image.SCALE_SMOOTH);

            JLabel labelImagem = new JLabel(new ImageIcon(imgRedimensionada));
            labelImagem.setHorizontalAlignment(SwingConstants.CENTER);

            imagemCarrosselPanel.add(labelImagem, "img" + i);
        }

        // Carrossel automático a cada 3 segundos
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    imagemAtual = (imagemAtual + 1) % imagens.length;
                    carrosselLayout.show(imagemCarrosselPanel, "img" + imagemAtual);
                });
            }
        }, 0, 4000); // 3 segundos

        // Borda para o texto de destaque
        textoCarrosMaisAlugados.setBorder(new LineBorder(new Color(10, 40, 61), 7, true));
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
