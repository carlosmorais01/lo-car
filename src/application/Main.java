package application;

import com.formdev.flatlaf.FlatDarkLaf;
import util.DumpGenerator;
import view.LoginScreen;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();

            UIManager.put("Component.arc", 5);
            UIManager.put("Button.arc", 20);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("TabbedPane.arc", 5);
            UIManager.put("ProgressBar.arc", 5);
            UIManager.put("ScrollBar.arc", 999);
            UIManager.put("Table.arc", 5);
            UIManager.put("TitlePane.unifiedBackground", false);

            UIManager.put("TitlePane.unifiedBackground", false);

            UIManager.put("TitlePane.background", new Color(243, 243, 243));
            UIManager.put("TitlePane.inactiveBackground", new Color(243, 243, 243));

            UIManager.put("TitlePane.foreground", new Color(74, 74, 74));
            UIManager.put("TitlePane.inactiveForeground", new Color(39, 39, 39));

            UIManager.put("TitlePane.buttonHoverColor", new Color(255,255,255, 50));
            UIManager.put("TitlePane.buttonPressedColor", new Color(255,255,255, 100));

            UIManager.put("Panel.background", new Color(243, 243, 243));
            UIManager.put("TextComponent.background", new Color(255, 255, 255));
            UIManager.put("Button.background", new Color(10, 40, 61));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(15, 55, 85));
            UIManager.put("Button.pressedBackground", new Color(5, 25, 40));
            UIManager.put("Label.foreground", new Color(43, 43, 43));
            UIManager.put("TextField.foreground", new Color(10, 40, 61));
            UIManager.put("PasswordField.foreground", new Color(10, 40, 61));
            UIManager.put("RadioButton.borderColor", new Color(200, 0, 0));

            UIManager.put("OptionPane.background", new Color(243, 243, 243));
            UIManager.put("OptionPane.messageForeground", new Color(43, 43, 43));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            boolean robotoSlabAvailable = false;
            boolean montserratAvailable = false;

            try (InputStream is = Main.class.getResourceAsStream("/fonts/Roboto_Slab/static/RobotoSlab-Regular.ttf")) {
                if (is != null) {
                    Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                    ge.registerFont(customFont);
                    robotoSlabAvailable = true;
                }
            } catch (Exception ex) {
                System.err.println("Erro ao carregar a fonte Roboto Slab do arquivo: " + ex.getMessage());
            }

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
                appFont = new Font("Roboto Slab", Font.PLAIN, 27);
            } else if (montserratAvailable) {
                appFont = new Font("Montserrat", Font.PLAIN, 27);
            } else {
                appFont = new Font("Roboto", Font.PLAIN, 27);
            }

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

            DumpGenerator.rodarDump();

        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}