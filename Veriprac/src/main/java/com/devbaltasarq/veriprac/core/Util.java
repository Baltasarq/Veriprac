// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.veriprac.core;


import java.io.File;
import java.io.IOException;
import java.text.Normalizer;


public final class Util {
    public static String encodeAsPlain(String str)
    {
        final StringBuilder TORET = new StringBuilder( str.length() );
        final String ALLOWED_CHRS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
        str = Normalizer.normalize( str, Normalizer.Form.NFD );

        for (char c : str.toCharArray()) {
            if ( ALLOWED_CHRS.indexOf( c ) > -1 ) {
                TORET.append( c );
            }
        }

        return TORET.toString();
    }

    public static String buildZipFileName(String usrHome, String nif, String surname, String name)
    {
        final String FILE_NAME_FMT = "$surname_$name-$nif";
        String fileName = FILE_NAME_FMT
                .replace( "$surname", surname )
                .replace( "$name", name )
                .replace( "$nif", nif ).toLowerCase();

        return new File( usrHome, encodeAsPlain( fileName ) ).getAbsolutePath();
    }

    public static String buildMarksFileContents(String nif, String surname, String name, String summary)
    {
        final String MARK_FILE_CONTENTS = """
        ===
        $surname, $name
        $nif
        ===
        
        $summary
        
        Cohesión:
        
        Robustez:
        
        Back end:
        
        Front end:
        
        Nota:
        """;

        return MARK_FILE_CONTENTS
                .replace( "$surname", surname )
                .replace( "$name", name )
                .replace( "$nif", nif )
                .replace( "$summary", summary );

    }

    public static void buildZip(
            String nif,
            String surname,
            String name,
            String usrHome,
            String targetPath) throws IOException
    {
        final File FILE_PATH = new File( Util.buildZipFileName( usrHome, nif, surname, name ));

        // Delete the zip file if it exists
        if ( FILE_PATH.exists() ) {
            FILE_PATH.delete();
        }

        // Create the zip file
        try (var zf = new net.lingala.zip4j.ZipFile( FILE_PATH.getAbsolutePath() ))
        {
            zf.addFolder( new File( targetPath ) );
        } catch(net.lingala.zip4j.exception.ZipException exc) {
            throw new IOException( exc.getMessage() );
        }

        return;
    }
}
