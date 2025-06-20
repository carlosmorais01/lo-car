// src/controller/MainController.java

package controller;

import view.frames.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane; // Para exibir uma mensagem simples

public class MainController {

    private MainFrame mainFrame;

    public MainController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // Adiciona o listener ao botão da MainFrame
        this.mainFrame.getBtnWelcome().addActionListener(new WelcomeButtonListener());
    }

    // Classe interna para o listener do botão
    private class WelcomeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Lógica de negócios (neste caso, apenas uma mensagem)
            JOptionPane.showMessageDialog(mainFrame, "Olá do sistema LoCar!", "Boas-vindas", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.getLblMessage().setText("Olá, você clicou no botão!");
        }
    }
}