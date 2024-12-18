// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package veriprac.ui;


public class Ppal {
    public static void main(String[] args)
    {
        launchGuiApp();
    }

    private static void launchGuiApp()
    {
        // Prepare look & feel, if possible
        try {
            System.setProperty( "awt.aatext", "true" );
            System.setProperty( "swing.aatext", "true" );
            System.setProperty( "awt.useSystemAAFontSettings", "on" );
        } catch(Exception ignored) {
            System.err.println( "[ERR] unable to configure GUI: "
                                    + ignored.getMessage() );
        }

        // Launch app
        new MainWindowCore();
    }
}
