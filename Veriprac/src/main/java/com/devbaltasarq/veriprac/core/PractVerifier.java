// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.veriprac.core;


import java.io.File;
import java.util.*;
import java.util.function.Supplier;


/** Verifies various items in a path,
  * in which an exercise sits.
  */
public class PractVerifier {
    public static final String DIR_SRC = "src";
    public static final String DIR_DOC = "doc";
    private static final String[] PROHIBITED_EXTENSIONS = {
        ".doc",
        ".docx",
        ".odt"
    };

    /** The ids of the items to verify. */
    public enum Id {
        DirExists( "The directory does not exist." ),
        SubSrcExists( "The src/ subdirectory does not exist." ),
        SubDocExists( "The doc/ subdirectory does not exist." ),
        PDFsPresent( "Convert your documentation to PDF." );

        Id(String errorMsg)
        {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg()
        {
            return this.errorMsg;
        }

        private final String errorMsg;
    }

    public PractVerifier()
    {
        final Id[] ITEM_IDS = Id.values();

        this.items = new HashSet<>( ITEM_IDS.length );
        this.verifiers = new HashMap<>( ITEM_IDS.length );

        // Associate ids and verifies
        this.verifiers.put( Id.DirExists, this::chkDirExists );
        this.verifiers.put( Id.SubSrcExists, () -> this.chkSubDirExists( DIR_SRC ) );
        this.verifiers.put( Id.SubDocExists, () -> this.chkSubDirExists( DIR_DOC ) );
        this.verifiers.put( Id.PDFsPresent, this::chkDocumentation );
    }

    /** @return the path the pract sits in. */
    public File getPath()
    {
        return path;
    }

    /** Sets the path for the target pract.
      * @param path a string holding the new path.
      */
    public void setPath(File path)
    {
        this.path = path;
    }


    /** Verifies all items, given a path.
      * @return true if everything verifies.
      */
    public boolean verify()
    {
        boolean toret = true;

        this.items.clear();

        for(final Id ID: Id.values()) {
            if ( this.verifiers.get( ID ).get() ) {
                this.items.add( ID );
            } else {
                toret = false;
                break;
            }
        }

        return toret;
    }

    /** @return whether this item vierifies or not. */
    public boolean doesVerify(Id id)
    {
        return this.items.contains( id );
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();

        for(final Id ID: Id.values()) {
            TORET.append( ID.toString() );
            TORET.append( ": " );
            TORET.append( this.doesVerify( ID ) );
        }

        return TORET.toString();
    }

    private boolean chkDirExists()
    {
        return this.path.exists();
    }

    private boolean chkSubDirExists(String subdir)
    {
        return new File( this.path, subdir ).exists();
    }

    /** Checks that there are PDF files,
      * instead of doc or docx files.
      * @return true if the doucmentation verifies, false otherwise.
      */
    private boolean chkDocumentation()
    {
        final File SUB_DIR_DOC = new File( this.path, DIR_DOC );
        final File[] FILES = SUB_DIR_DOC.listFiles();
        boolean toret = false;

        if ( FILES != null
          && FILES.length > 0 )
        {
            toret = true;

            LOOP_OVER_FILES:
            for(final File FILE: FILES) {
                if ( FILE.isFile() ) {
                    for(final String EXT: PROHIBITED_EXTENSIONS) {
                        if ( FILE.getName().endsWith( EXT ) ) {
                            toret = false;
                            break LOOP_OVER_FILES;
                        }
                    }
                }
            }
        }

        return toret;
    }

    private File path;
    private final Set<Id> items;
    private final Map<Id, Supplier<Boolean>> verifiers;
}
