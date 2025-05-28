// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.veriprac.ui;


import com.devbaltasarq.veriprac.core.AppInfo;
import com.devbaltasarq.veriprac.core.PractVerifier;
import com.devbaltasarq.veriprac.core.Util;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.EnumMap;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class MainWindowCore {
    public enum Data {
        NIF, SURNAME, NAME, EMAIL, NUM, SUMMARY, PATH
    }

    public MainWindowCore()
    {
        this.logger = null;
        this.winUi = new MainWindowUI();
        this.verifier = new PractVerifier();
        this.FIELDS_AND_CHECKS = Map.of(
                this.getMainUI().getChkNif(), this.getMainUI().getEdNif(),
                this.getMainUI().getChkName(), this.getMainUI().getEdName(),
                this.getMainUI().getChkSurname(), this.getMainUI().getEdSurname(),
                this.getMainUI().getChkEmail(), this.getMainUI().getEdEmail(),
                this.getMainUI().getChkSummary(), this.getMainUI().getEdSummary(),
                this.getMainUI().getChkType(), this.getMainUI().getEdNum(),
                this.getMainUI().getChkPath(), this.getMainUI().getEdPath() );

        this.VERIFY_CHECKS = new EnumMap<>( Map.of(
                PractVerifier.Id.DirExists, this.winUi.getChkDirExists(),
                PractVerifier.Id.SubSrcExists, this.winUi.getChkSrcExists(),
                PractVerifier.Id.SubDocExists, this.winUi.getChkDocExists(),
                PractVerifier.Id.PDFsPresent, this.winUi.getChkPDFExists() ));

        this.DATA_FIELDS = new EnumMap<>( Map.of(
                Data.NUM, this.winUi.getEdNum(),
                Data.NIF, this.winUi.getEdNif(),
                Data.SURNAME, this.winUi.getEdSurname(),
                Data.NAME, this.winUi.getEdName(),
                Data.EMAIL, this.winUi.getEdEmail(),
                Data.SUMMARY, this.winUi.getEdSummary(),
                Data.PATH, this.winUi.getEdPath()
        ));

        this.setListeners();
        this.USER_HOME = this.retrieveUserHome();
        this.setPath( this.USER_HOME );
    }
        
    /** @return the user's home dir from the system. */
    private String retrieveUserHome()
    {
        String userHome = System.getProperty( "user.home" );
        
        if ( userHome == null
          || userHome.trim().length() == 0 )
        {
            userHome = ".";
        }

        return userHome;
    }

    /** Prepares all listeners, for buttons and menus. */
    private void setListeners()
    {
        this.winUi.getOpQuit().addActionListener(
                                (evt) -> this.onQuit());
        this.winUi.getOpAbout().addActionListener(
                                (evt) -> this.onAbout());
        this.winUi.getOpOpen().addActionListener(
                (evt) -> this.onOpen() );

        this.winUi.getBtOpen().addActionListener(
                (evt) -> this.onOpen() );
        this.winUi.getBtPack().addActionListener(
                (evt) -> this.onPack() );
        this.winUi.addWindowListener( new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent)
            {
            }

            @Override
            public void windowClosing(WindowEvent windowEvent)
            {
                MainWindowCore.this.onQuit();
            }

            @Override
            public void windowClosed(WindowEvent windowEvent)
            {
            }

            @Override
            public void windowIconified(WindowEvent windowEvent)
            {
            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent)
            {
            }

            @Override
            public void windowActivated(WindowEvent windowEvent)
            {
            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent)
            {
            }
        });
    }
    
    private void log(String msg)
    {
        if ( this.logger != null ) {
            this.logger.info( msg );
        }
        
        return;
    }

    /** Triggered by File >> Quit. */ 
    private void onQuit()
    {
        this.log( "Terminated." );
        LogManager.getLogManager().reset();
        System.exit( 0 );
    }
    
    /** Triggered by the File >> Open. */
    private void onOpen()
    {
        final var OPEN_DLG = new JFileChooser( this.verifier.getPath().getAbsolutePath() );
        final int RESPONSE_OK = JFileChooser.APPROVE_OPTION;
        
        OPEN_DLG.setDialogTitle( "Open directory" );
        OPEN_DLG.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        if ( OPEN_DLG.showOpenDialog( this.winUi ) == RESPONSE_OK ) {
            final File DIR = OPEN_DLG.getSelectedFile();

            if ( DIR != null ) {
                this.setPath( DIR.getAbsolutePath() );
            }
            
            this.prepareLog();
        }

        return;
    }

    private void onAbout()
    {
        this.winUi.getAboutPanel().setVisible( true );
        this.winUi.pack();
    }

    /** @return the window ui. */
    public final MainWindowUI getMainUI()
    {
        return this.winUi;
    }
    
    /** Gets the data, according to the given identifier.
      * @param data an enumerator pointing to the real data.
      * @return the value to retrieve.
      */
    private String getData(Data data)
    {
        String toret = this.DATA_FIELDS.get( data ).getText().trim();

        if ( data == Data.NIF ) {
            toret = toret.toUpperCase();
        }

        return toret;
    }

    /** Changes the PATH to pack&verify.
      * @param path the new path to a directory with an exercise.
      */
    private void setPath(String path)
    {
        this.verifier.setPath( new File( path ) );

        // Prepare the path to visualize
        if ( path.length() > 30 ) {
            path = "..." + path.substring( path.length() - 30 );
        }

        this.winUi.getEdPath().setText( path );
    }
    
    /** Prepares Veriprac's logging capabilities for this exercise. */
    private void prepareLog()
    {
        final Path LOG_FILE_PATH = Paths.get(
                                    this.verifier.getPath().getAbsolutePath(),
                                    "veriprac.log" );
        
        // Close the current log
        if ( this.logger != null ) {
            LogManager.getLogManager().reset();
        }
        
        // Create the log
        this.logger = Logger.getLogger( this.getClass().getPackageName() );
        
        try {
            final var HANDLER = new FileHandler( LOG_FILE_PATH.toString() );
            this.logger.addHandler( HANDLER );
            
            HANDLER.setFormatter( new SimpleFormatter() {
                private static final String format = "%1$tF %1$tT %2$-7s %3$3s %n";

                @Override
                public synchronized String format(LogRecord lr)
                {
                    return String.format( format,
                                            new Date( lr.getMillis() ),
                                            lr.getLevel().getName(),
                                            lr.getMessage()
                    );
                }
            });
            
            this.log( "Log created with PATH: " + LOG_FILE_PATH );
        } catch(IOException exc) {
            JOptionPane.showMessageDialog(
                                this.getMainUI(),
                                AppInfo.NAME,
                                "ERROR creating log" + exc.getMessage(),
                                JOptionPane.ERROR_MESSAGE );
        }
    }

    /** Checks that fields have valid info, and also the exercise itself. */
    private boolean chk()
    {
        boolean statusSet = false;
        boolean verified = false;
        boolean fieldsChecked;

        this.log( "Chk started... " );
        this.winUi.setStatus();

        // Set all checkboxes
        for(final var PAIR: FIELDS_AND_CHECKS.entrySet()) {
            PAIR.getKey().setState( true );
        }
        
        for(final var PAIR: VERIFY_CHECKS.entrySet()) {
            PAIR.getValue().setState( false );
        }

        fieldsChecked = this.chkFields();
        
        if ( fieldsChecked ) {
            verified = verifier.verify();

            this.log( "Checking verifier errors..." );
            for(final PractVerifier.Id ID: PractVerifier.Id.values()) {
                if ( !this.verifier.doesVerify( ID ) ) {
                    if ( !statusSet ) {
                        statusSet = false;
                        this.winUi.setStatus( ID.getErrorMsg() );
                        this.log( "Error: " + ID.getErrorMsg() );
                    }

                    this.VERIFY_CHECKS.get( ID ).setState( false );
                } else {
                    this.VERIFY_CHECKS.get( ID ).setState( true );
                }
            }
        }

        this.log( "Chk finished,"
                    + " fields: " + fieldsChecked
                    + " and verified: " + verified );
        return fieldsChecked && verified;
    }

    /** Triggered by the Pack button. This starts the magic. */
    private void onPack()
    {
        if ( this.chk() ) {
            try {
                this.createMarksFile();
                this.buildZip();

                javax.swing.JOptionPane.showMessageDialog(
                        this.winUi,
                        "Zip file created at: " + this.USER_HOME,
                        AppInfo.NAME,
                        JOptionPane.INFORMATION_MESSAGE );
            } catch(IOException exc) {
                javax.swing.JOptionPane.showMessageDialog(
                                this.winUi,
                                exc.getMessage(),
                                AppInfo.NAME,
                                javax.swing.JOptionPane.ERROR_MESSAGE );
            }
        }

        return;
    }

    /** Checks that the data fields have valid data. */
    private boolean chkFields()
    {
        boolean toret = true;
        
        this.log( "Checking field contents..." );

        // Set those checkboxes with fails
        for(final var PAIR: FIELDS_AND_CHECKS.entrySet()) {
            boolean val = !( PAIR.getValue().getText().trim().length() < 4 );

            if ( this.getMainUI().getEdNum() == PAIR.getValue() ) {
                val = !this.getMainUI().getEdNum().getText().isBlank();
            }

            PAIR.getKey().setState( val );
            toret &= val;
        }
        
        this.log( "All fields at least with some contents... " + toret );

        // Check the type choice
        if ( this.getMainUI().getEdType().getSelectedIndex() < 1 ) {
            this.getMainUI().getChkType().setState( false );

            toret = false;
            this.log( "Exercise type not selected." );
        }
        
        // Check the PATH field
        final String PATH = this.getMainUI().getEdPath().getText().trim();
        if ( PATH.equals( this.USER_HOME ) ) {
            final String PATH_MSG = "Invalid PATH: can't be user's home.";
            
            this.getMainUI().getChkPath().setState( false );            
            toret = false;
            this.log( PATH_MSG );
            this.winUi.setStatus( PATH_MSG );
        }
        
        this.log( "Checking fields returns " + toret );
        return toret;
    }

    /** Creates a text file with the data entered. */
    private void createMarksFile() throws IOException
    {
        final String PATH = this.getData( Data.PATH );
        final String NIF = this.getData( Data.NIF );
        final String SURNAME = this.getData( Data.SURNAME );
        final String NAME = this.getData( Data.NAME );
        final String SUMMARY = this.getData( Data.SUMMARY );
        final String FILE_NAME = Paths.get( PATH, "notas.txt" ).toString();
        
        this.log( "Creating marks file with..." );
        this.log( "Surname: " + SURNAME );
        this.log( "Name: " + NAME );
        this.log( "NIF: " + NIF );
        this.log( "Home: " + this.USER_HOME );
        this.log( "Path: " + PATH );
        this.log( "File: " + FILE_NAME );

        try (var output = new PrintStream( FILE_NAME )) {
            output.println( Util.buildMarksFileContents(
                                    NIF, SURNAME, NAME, SUMMARY ) );
        }

        this.log( "Marks file created." );
    }

    /** Creates the zip file. */
    private void buildZip() throws IOException
    {
        final String SURNAME = this.getData( Data.SURNAME );
        final String NAME = this.getData( Data.NAME );
        final String NIF = this.getData( Data.NIF );
        final String TARGET_PATH = this.getData( Data.PATH );
        
        this.log( "Creating ZIP with..." );
        this.log( "Surname: " + SURNAME );
        this.log( "Name: " + NAME );
        this.log( "NIF: " + NIF );
        this.log( "Home: " + this.USER_HOME );
        this.log( "Path: " + TARGET_PATH );

        Util.buildZip( NIF, SURNAME, NAME, this.USER_HOME, TARGET_PATH );
        this.log( "ZIP built." );
    }

    private final String USER_HOME;
    private Logger logger;
    private final MainWindowUI winUi;
    private final PractVerifier verifier;
    private final EnumMap<PractVerifier.Id, java.awt.Checkbox> VERIFY_CHECKS;
    private final EnumMap<Data, java.awt.TextField> DATA_FIELDS;
    private final Map<java.awt.Checkbox, java.awt.TextField> FIELDS_AND_CHECKS;
}
