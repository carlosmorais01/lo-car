// src/view/frames/MainFrame.java

package view.frames;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JButton btnWelcome;
    private JLabel lblMessage;

    public MainFrame() {
        setTitle("Sistema LoCar - Gerenciamento de Veículos");
        setSize(800, 600); // Tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Layout Manager (vamos usar um BorderLayout simples para este exemplo)
        setLayout(new BorderLayout());

        // Painel para os componentes
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centraliza componentes com espaçamento

        btnWelcome = new JButton("Diga Olá!");
        lblMessage = new JLabel("Bem-vindo ao LoCar!");
        lblMessage.setFont(new Font("Arial", Font.BOLD, 18)); // Aumenta a fonte do rótulo

        panel.add(btnWelcome);
        panel.add(lblMessage);

        // Adiciona o painel ao centro do JFrame
        add(panel, BorderLayout.CENTER);

        // Os listeners e a lógica de interação serão definidos no Controller.
        // O JFrame expõe os componentes para que o Controller possa adicioná-los.
    }

    // Métodos para o Controller acessar os componentes
    public JButton getBtnWelcome() {
        return btnWelcome;
    }

    public JLabel getLblMessage() {
        return lblMessage;
    }
}