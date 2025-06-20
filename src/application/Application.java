// src/application/Main.java (versão atualizada)

package application;

import view.frames.MainFrame;
import controller.MainController; // Importa o seu Controller
import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();

            // Instancia o Controller e associa-o à MainFrame
            // Agora o Controller gerenciará os eventos da MainFrame
            MainController mainController = new MainController(mainFrame);

            mainFrame.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        });
    }
}