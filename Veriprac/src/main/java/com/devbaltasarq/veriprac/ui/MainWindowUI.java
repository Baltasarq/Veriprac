// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.veriprac.ui;


import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;

import com.devbaltasarq.veriprac.core.AppInfo;


public class MainWindowUI extends Frame {
    private static final int INI_WIDTH = 400;
    private static final int INI_HEIGHT = 525;

    public MainWindowUI()
    {
        final Font SYS_FONT = Font.decode( null );
        final Dimension WIN_DIMENSION = new Dimension( INI_WIDTH, INI_HEIGHT );
        final Dimension GAP_DIMENSION = new Dimension( 5, 5 );
        final BorderLayout LY_BORDER = new BorderLayout();
        final Panel PNL_MAIN = new Panel( new BorderLayout() );

        LY_BORDER.setHgap( 5 );
        LY_BORDER.setVgap( 5 );

        this.setTitle( AppInfo.NAME );
        this.setLayout( LY_BORDER );

        this.setFont(
                SYS_FONT.deriveFont( Font.PLAIN, SYS_FONT.getSize() + 2 ) );

        PNL_MAIN.add( this.buildInfoPanel(), BorderLayout.NORTH );
        PNL_MAIN.add( this.buildActionPanel(), BorderLayout.CENTER );
        PNL_MAIN.add( this.buildAboutPanel(), BorderLayout.SOUTH );

        this.add( PNL_MAIN, BorderLayout.CENTER );
        this.add( Box.createRigidArea( GAP_DIMENSION ), BorderLayout.WEST );
        this.add( Box.createRigidArea( GAP_DIMENSION ), BorderLayout.EAST );
        this.add( Box.createRigidArea( GAP_DIMENSION ), BorderLayout.NORTH );
        this.add( this.buildStatusBar(), BorderLayout.SOUTH );

        this.setMenuBar( this.buildMenuBar() );
        this.pack();
        this.setSize( WIN_DIMENSION );
        this.setMinimumSize( WIN_DIMENSION );
        this.setStatus();
        this.edNif.requestFocus();
        this.setVisible( true );
        this.setIcon();
    }

    private void setIcon()
    {
        try {
            ImageIcon iconImg = new ImageIcon(
                                    this.getClass().getClassLoader()
                                    .getResource( "pencil.png" ) );
            this.setIconImage( iconImg.getImage() );
        } catch(Exception e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Imposible to apply icon: " + e.getMessage(),
                    AppInfo.NAME,
                    JOptionPane.ERROR_MESSAGE );
        }
    }

    private Checkbox buildNotEditableChk(String msg)
    {
        final Checkbox TORET = new Checkbox( msg );

        TORET.addItemListener(
                (evt) -> TORET.setState(
                        !TORET.getState() ));

        return TORET;
    }

    private Panel buildRegularInput(Checkbox chk, Label lbl, Component ed)
    {
        final BorderLayout LY_MAIN_BORDER = new BorderLayout();
        final BorderLayout LY_LBL_BORDER = new BorderLayout();
        final Panel TORET = new Panel( LY_MAIN_BORDER );
        final Panel PNL_LBL = new Panel( LY_LBL_BORDER );

        LY_MAIN_BORDER.setVgap( 10 );
        LY_MAIN_BORDER.setHgap( 10 );
        LY_LBL_BORDER.setVgap( 5 );
        LY_LBL_BORDER.setHgap( 5 );

        PNL_LBL.add( chk, BorderLayout.WEST );
        PNL_LBL.add( lbl, BorderLayout.CENTER );
        TORET.add( PNL_LBL, BorderLayout.WEST );
        TORET.add( ed, BorderLayout.CENTER );

        return TORET;
    }

    private Panel buildPersonalInfo()
    {
        final Label LBL_NAME = new Label( "Nombre" );
        final Label LBL_SURNAME = new Label( "Apellidos" );
        final Label LBL_NIF = new Label( "NIF" );
        final Label LBL_EMAIL = new Label( "E.mail" );
        final Dimension DIM_GAP = new Dimension( 5, 5 );
        final Panel TORET = new Panel();

        // Prepare the NIF panel
        this.chkNif = this.buildNotEditableChk( "" );
        this.edNif = new TextField( 12 );
        final Panel PNL_NIF =
                this.buildRegularInput( this.chkNif, LBL_NIF, this.edNif );

        // Prepare the surname panel
        this.chkSurname = this.buildNotEditableChk( "" );
        this.edSurname = new TextField( 80 );
        final Panel PNL_SURNAME =
                this.buildRegularInput( this.chkSurname, LBL_SURNAME, this.edSurname );

        // Prepare the name panel
        this.chkName = this.buildNotEditableChk( "" );
        this.edName = new TextField( 80 );
        final Panel PNL_NAME =
                this.buildRegularInput( this.chkName, LBL_NAME, this.edName );

        // Prepare the e.mail panel
        this.chkEmail = this.buildNotEditableChk( "" );
        this.edEmail = new TextField( 40 );
        final Panel PNL_EMAIL =
                this.buildRegularInput( this.chkEmail, LBL_EMAIL, this.edEmail );

        TORET.setLayout( new BoxLayout( TORET, BoxLayout.Y_AXIS ) );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_NIF );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_SURNAME );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_NAME );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_EMAIL );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        return TORET;
    }

    private Panel buildAppPanel()
    {
        final Label LBL_TYPE = new Label( "Tipo" );
        final Label LBL_SUMMARY = new Label( "Resumen" );
        final Label LBL_PATH = new Label( "Path" );
        final Dimension DIM_GAP = new Dimension( 5, 5 );
        final Panel TORET = new Panel();

        // Prepare the type panel
        this.chkType = this.buildNotEditableChk( "" );
        this.edType = new Choice();
        this.edNum = new TextField( 2 );
        this.edNum.setText( "1" );
        this.edType.addItem( "<Elige>" );
        this.edType.addItem( "Práctica" );
        this.edType.addItem( "Proyecto" );
        this.edType.addItem( "Otro" );
        final Panel PNL_TYPE =
                this.buildRegularInput( this.chkType, LBL_TYPE, this.edType );
        PNL_TYPE.add( this.edNum, BorderLayout.EAST );

        // Prepare the summary panel
        this.edSummary = new TextField( 80 );
        this.chkSummary = this.buildNotEditableChk( "" );
        final Panel PNL_SUMMARY =
                this.buildRegularInput( this.chkSummary, LBL_SUMMARY, this.edSummary );

        // Prepare the path panel
        this.chkPath = this.buildNotEditableChk( "" );
        this.edPath = new TextField( 80 );
        this.edPath.setEnabled( false );
        this.btOpen = new Button( "..." );
        final Panel PNL_PATH =
                this.buildRegularInput( this.chkPath, LBL_PATH, this.edPath );
        PNL_PATH.add( this.btOpen, BorderLayout.EAST );

        TORET.setLayout( new BoxLayout( TORET, BoxLayout.Y_AXIS ) );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_TYPE );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_SUMMARY );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        TORET.add( PNL_PATH );
        TORET.add( Box.createRigidArea( DIM_GAP ));
        return TORET;
    }

    private Panel buildInfoPanel()
    {
        final Panel TORET = new Panel();
        final BoxLayout LY_BOX = new BoxLayout( TORET, BoxLayout.Y_AXIS );

        TORET.setLayout( LY_BOX );
        TORET.add( this.buildPersonalInfo() );
        TORET.add( this.buildAppPanel() );
        return TORET;
    }

    private MenuBar buildMenuBar()
    {
        final MenuBar TORET = new MenuBar();
        final Menu FILE = new Menu( "Archivo" );
        final Menu HELP = new Menu( "Ayuda" );
        this.opOpen = new MenuItem ( "Open",
                                new MenuShortcut( KeyEvent.VK_O ) );
        this.opQuit = new MenuItem( "Quit",
                                new MenuShortcut( KeyEvent.VK_Q ));
        this.opAbout = new MenuItem( "Acerca de..." );

        FILE.add( this.opOpen );
        FILE.add( this.opQuit );
        HELP.add( this.opAbout );

        TORET.add( FILE );
        TORET.add( HELP );
        return TORET;
    }

    private Panel buildAboutPanel()
    {
        final Label LBL = new Label( AppInfo.COMPLETE_NAME
                                            + " | "
                                            + AppInfo.ABOUT );
        final Button BT_OK = new Button( "Ok" );

        LBL.setFont( this.getFont().deriveFont( Font.BOLD ) );

        BT_OK.setBackground( Color.lightGray );
        BT_OK.addActionListener(
                (evt) -> this.pnlAbout.setVisible( false ) );

        this.pnlAbout = new Panel();
        this.pnlAbout.setBackground( Color.decode( "#FFF77D" ) );
        this.pnlAbout.add( LBL );
        this.pnlAbout.add( BT_OK );
        this.pnlAbout.setVisible( false );
        return this.pnlAbout;
    }

    private Panel buildChksPanel()
    {
        final Panel TORET = new Panel();
        final BoxLayout LY_BOX = new BoxLayout( TORET, BoxLayout.Y_AXIS );

        this.chkSrc = this.buildNotEditableChk( "Is src/ present" );
        this.chkDoc = this.buildNotEditableChk( "Is doc/ present" );
        this.chkPDF = this.buildNotEditableChk( "PDFs present in doc/" );

        TORET.setLayout( LY_BOX );
        TORET.add( this.chkSrc);
        TORET.add( this.chkDoc);
        TORET.add( this.chkPDF);
        return TORET;
    }

    private Panel buildActionButtons()
    {
        final Panel TORET = new Panel();

        this.btPack = new Button( "Pack" );
        TORET.add( this.btPack );
        return TORET;
    }

    private Panel buildActionPanel()
    {
        final Panel TORET = new Panel();
        final BoxLayout LY_BOX = new BoxLayout( TORET, BoxLayout.Y_AXIS );

        TORET.setLayout( LY_BOX );
        TORET.add( this.buildChksPanel() );
        TORET.add( this.buildActionButtons() );
        return TORET;
    }

    private Label buildStatusBar()
    {
        this.lblStatus = new Label();
        return this.lblStatus;
    }

    public final void setStatus()
    {
        this.lblStatus.setText( "Ready" );
    }

    public void setStatus(String msg)
    {
        this.lblStatus.setText( msg );
    }

    public MenuItem getOpQuit()
    {
        return this.opQuit;
    }

    public MenuItem getOpAbout()
    {
        return this.opAbout;
    }
    public MenuItem getOpOpen()
    {
        return this.opOpen;
    }

    public Panel getAboutPanel()
    {
        return this.pnlAbout;
    }

    public Button getBtOpen()
    {
        return this.btOpen;
    }
    public Button getBtPack()
    {
        return this.btPack;
    }

    public TextField getEdName()
    {
        return this.edName;
    }
    public Checkbox getChkName()
    {
        return this.chkName;
    }

    public TextField getEdEmail()
    {
        return this.edEmail;
    }
    public Checkbox getChkEmail()
    {
        return this.chkEmail;
    }

    public TextField getEdNif()
    {
        return this.edNif;
    }
    public Checkbox getChkNif()
    {
        return this.chkNif;
    }

    public TextField getEdNum()
    {
        return this.edNum;
    }
    public Choice getEdType() {
        return this.edType;
    }
    public Checkbox getChkType()
    {
        return this.chkType;
    }

    public TextField getEdSurname()
    {
        return this.edSurname;
    }
    public Checkbox getChkSurname()
    {
        return this.chkSurname;
    }

    public TextField getEdSummary()
    {
        return this.edSummary;
    }
    public Checkbox getChkSummary()
    {
        return this.chkSummary;
    }
    
    public Checkbox getChkPath()
    {
        return this.chkPath;
    }

    public Checkbox getChkDirExists()
    {
        return this.chkPath;
    }

    public Checkbox getChkSrcExists()
    {
        return this.chkSrc;
    }

    public Checkbox getChkDocExists()
    {
        return this.chkDoc;
    }

    public Checkbox getChkPDFExists()
    {
        return this.chkPDF;
    }
    
    public void setShownPath(String path)
    {
        // Prepare the path to visualize
        if ( path.length() > 30 ) {
            path = "..." + path.substring( path.length() - 30 );
        }
        
        this.edPath.setText( path );
    }

    private Button btOpen;
    private Button btPack;
    private MenuItem opQuit;
    private MenuItem opAbout;
    private MenuItem opOpen;
    private Choice edType;
    private TextField edNum;
    private TextField edSummary;
    private TextField edPath;
    private Checkbox chkName;
    private Checkbox chkSurname;
    private Checkbox chkEmail;
    private Checkbox chkNif;
    private Checkbox chkSrc;
    private Checkbox chkDoc;
    private Checkbox chkPDF;
    private Checkbox chkType;
    private Checkbox chkSummary;
    private Checkbox chkPath;
    private TextField edName;
    private TextField edSurname;
    private TextField edNif;
    private TextField edEmail;
    private Panel pnlAbout;
    private Label lblStatus;
}
