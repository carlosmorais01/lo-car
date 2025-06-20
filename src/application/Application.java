package application;

import view.panels.HomePagePanel;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LoCar!");

            HomePagePanel painel = new HomePagePanel();
            frame.setContentPane(painel.getContentPanel());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            frame.setVisible(true);
        });
    }
}