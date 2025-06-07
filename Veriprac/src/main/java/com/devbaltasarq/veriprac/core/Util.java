// Veriprac (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.veriprac.core;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.EnumSet;


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

        return new File( usrHome,
                         encodeAsPlain( fileName ) + ".zip" ).getAbsolutePath();
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
    
    public static void pack(final Path TARGET_PATH, final Path ZIP_FILE)
            throws IOException
    {
        try (
                FileOutputStream fos = new FileOutputStream( ZIP_FILE.toFile() );
                ZipOutputStream zos = new ZipOutputStream( fos ) )
        {
            final Path REAL_START_PATH = TARGET_PATH.toRealPath();
            
            Files.walkFileTree(
                    REAL_START_PATH,
                    EnumSet.of( FileVisitOption.FOLLOW_LINKS ),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {   
                    zos.putNextEntry(
                            new ZipEntry(
                                REAL_START_PATH.relativize( file ).toString() ));
                    Files.copy( file, zos );
                    
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
    
    public static void buildZip(
            String nif,
            String surname,
            String name,
            String usrHome,
            String targetPath) throws IOException
    {
        final File FILE_PATH = new File(
                            Util.buildZipFileName( usrHome, nif, surname, name ));
        final File TEMP_PATH = File.createTempFile( FILE_PATH.toString(), "zip" );

        pack( Path.of( targetPath ), TEMP_PATH.toPath() );
        
        Files.copy(
                TEMP_PATH.toPath(),
                FILE_PATH.toPath(),
                StandardCopyOption.REPLACE_EXISTING );
    }
}
