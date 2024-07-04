// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package ui;


import core.AppInfo;
import core.PractVerifier;
import core.Util;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.*;


public class MainWindowCore {
    public enum Data {
        NIF, SURNAME, NAME, EMAIL, NUM, SUMMARY, PATH
    }

    public MainWindowCore()
    {
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
        this.USER_HOME = System.getProperty( "user.home" );
        this.setPath( this.USER_HOME );
    }

    private String getData(Data data)
    {
        String toret = this.DATA_FIELDS.get( data ).getText().trim();

        if ( data == Data.NIF ) {
            toret = toret.toUpperCase();
        }

        return toret;
    }

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

    private void onQuit()
    {
        System.exit( 0 );
    }
    private void onOpen()
    {
        final var OPEN_DLG = new JFileChooser( this.verifier.getPath().getAbsolutePath() );
        OPEN_DLG.setDialogTitle( "Open directory" );
        OPEN_DLG.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        OPEN_DLG.showOpenDialog( this.winUi );
        final File DIR = OPEN_DLG.getSelectedFile();

        if ( DIR != null ) {
            this.setPath( DIR.getAbsolutePath() );
        }

        return;
    }

    private void onAbout()
    {
        this.winUi.getAboutPanel().setVisible( true );
        this.winUi.pack();
    }

    /** @return the window ui. */
    public MainWindowUI getMainUI()
    {
        return this.winUi;
    }

    private void setPath(String path)
    {
        this.verifier.setPath( new File( path ) );

        // Prepare the path to visualize
        if ( path.length() > 30 ) {
            path = "..." + path.substring( path.length() - 30 );
        }

        this.winUi.getEdPath().setText( path );
    }

    private boolean chk()
    {
        boolean statusSet = false;
        boolean verified;
        boolean fieldsChecked;

        this.winUi.setStatus();

        // Set all checkboxes
        for(final var PAIR: FIELDS_AND_CHECKS.entrySet()) {
            PAIR.getKey().setState( true );
        }

        for(final var PAIR: VERIFY_CHECKS.entrySet()) {
            PAIR.getValue().setState( true );
        }

        fieldsChecked = this.chkFields();
        verified = verifier.verify();

        if ( !verified ) {
            for(final PractVerifier.Id ID: PractVerifier.Id.values()) {
                if ( !this.verifier.doesVerify( ID ) ) {
                    if ( !statusSet ) {
                        statusSet = true;
                        this.winUi.setStatus( ID.getErrorMsg() );
                    }

                    VERIFY_CHECKS.get( ID ).setState( false );
                }
            }
        }

        return fieldsChecked && verified;
    }

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

    private boolean chkFields()
    {
        boolean toret = true;

        // Reset those checkboxes with fails
        for(final var PAIR: FIELDS_AND_CHECKS.entrySet()) {
            boolean val = !( PAIR.getValue().getText().trim().length() < 4 );

            if ( this.getMainUI().getEdNum() == PAIR.getValue() ) {
                val = !this.getMainUI().getEdNum().getText().isBlank();
            }

            PAIR.getKey().setState( val );
            toret &= val;
        }

        // Check the type choice
        if ( this.getMainUI().getEdType().getSelectedIndex() < 1 ) {
            this.getMainUI().getChkType().setState( false );

            toret = false;
        }

        return toret;
    }

    private void createMarksFile() throws IOException
    {
        final String NIF = this.getData( Data.NIF );
        final String SURNAME = this.getData( Data.SURNAME );
        final String NAME = this.getData( Data.NAME );
        final String SUMMARY = this.getData( Data.SUMMARY );
        final String FILE_NAME = Paths.get(
                                    this.getData( Data.PATH ),
                                    "notas.txt" ).toString();

        try (var output = new PrintStream( FILE_NAME )) {
            output.println( Util.buildMarksFileContents( NIF, SURNAME, NAME, SUMMARY) );
        }

        return;
    }

    private void buildZip() throws IOException
    {
        final String SURNAME = this.getData( Data.SURNAME );
        final String NAME = this.getData( Data.NAME );
        final String NIF = this.getData( Data.NIF );
        final String TARGET_PATH = this.getData( Data.PATH );

        Util.buildZip( NIF, SURNAME, NAME, this.USER_HOME, TARGET_PATH );
    }

    private final String USER_HOME;
    private final MainWindowUI winUi;
    private final PractVerifier verifier;
    private final EnumMap<PractVerifier.Id, java.awt.Checkbox> VERIFY_CHECKS;
    private final EnumMap<Data, java.awt.TextField> DATA_FIELDS;
    private final Map<java.awt.Checkbox, java.awt.TextField> FIELDS_AND_CHECKS;
}
