// src/application/Main.java
package application;

import com.formdev.flatlaf.FlatDarkLaf;
import view.LoginScreen;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        // Configura o FlatLaf como o Look and Feel
        try {
            // Define o tema FlatDarkLaf como base
            FlatDarkLaf.setup(); // Alterado para FlatDarkLaf.setup()

            UIManager.put("Component.arc", 5); // Arredonda os cantos dos componentes
            UIManager.put("Button.arc", 20);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("TabbedPane.arc", 5);
            UIManager.put("ProgressBar.arc", 5);
            UIManager.put("ScrollBar.arc", 999); // Arredonda as barras de rolagem
            UIManager.put("Table.arc", 5);
            UIManager.put("TitlePane.unifiedBackground", false); // Ajuste para melhor visualização do título
            UIManager.put("TitlePane.buttonHoverColor", new Color(255,255,255, 50));
            UIManager.put("TitlePane.buttonPressedColor", new Color(255,255,255, 100));


            // Cores personalizadas
            UIManager.put("Panel.background", new Color(243, 243, 243)); // Fundo principal da aplicação #F3F3F3
            UIManager.put("TextComponent.background", new Color(255, 255, 255));
            UIManager.put("Button.background", new Color(10, 40, 61)); // Cor principal #0A283D
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(15, 55, 85));
            UIManager.put("Button.pressedBackground", new Color(5, 25, 40));
            UIManager.put("Label.foreground", new Color(10, 40, 61)); // Cor do texto dos labels
            UIManager.put("TextField.foreground", new Color(10, 40, 61));
            UIManager.put("PasswordField.foreground", new Color(10, 40, 61));

            // Configura a fonte Roboto Slab ou alternativa
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            boolean robotoSlabAvailable = false;
            boolean montserratAvailable = false;

            // Para carregar fontes customizadas se necessário (ex: de um arquivo .ttf no seu projeto)
            try (InputStream is = Main.class.getResourceAsStream("/fonts/Roboto_Slab/static/RobotoSlab-Regular.ttf")) {
                if (is != null) {
                    Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                    ge.registerFont(customFont);
                    robotoSlabAvailable = true;
                }
            } catch (Exception ex) {
                System.err.println("Erro ao carregar a fonte Roboto Slab do arquivo: " + ex.getMessage());
            }

            // Verifica se as fontes estão disponíveis no sistema ou foram carregadas
            String[] fontNames = ge.getAvailableFontFamilyNames();
            for (String fontName : fontNames) {
                if (fontName.equalsIgnoreCase("Roboto Slab")) {
                    robotoSlabAvailable = true;
                }
                if (fontName.equalsIgnoreCase("Montserrat")) {
                    montserratAvailable = true;
                }
            }

            Font appFont;
            if (robotoSlabAvailable) {
                appFont = new Font("Roboto Slab", Font.PLAIN, 14);
            } else if (montserratAvailable) {
                appFont = new Font("Montserrat", Font.PLAIN, 14);
            } else {
                appFont = new Font("Roboto", Font.PLAIN, 14); // Fallback para Roboto padrão
            }

            // Define a fonte para todos os componentes
            UIManager.put("Button.font", appFont);
            UIManager.put("Label.font", appFont);
            UIManager.put("TextField.font", appFont);
            UIManager.put("PasswordField.font", appFont);
            UIManager.put("TextArea.font", appFont);
            UIManager.put("ComboBox.font", appFont);
            UIManager.put("List.font", appFont);
            UIManager.put("Table.font", appFont);
            UIManager.put("TableHeader.font", appFont);
            UIManager.put("TitledBorder.font", appFont);
            UIManager.put("CheckBox.font", appFont);
            UIManager.put("RadioButton.font", appFont);
            UIManager.put("OptionPane.font", appFont);

        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace para mais detalhes do erro
        }

        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}