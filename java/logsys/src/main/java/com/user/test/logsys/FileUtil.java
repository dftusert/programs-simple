package com.user.test.logsys;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil
{
    /*********************************
     * readFile<br>
     * reads text from file
     * 
     * @param filename - name of file
     
     * @return text from file
     * 
     * @throws IOException
     * @throws FileNotFoundException
     ********************************/
    public static String readFile ( String filename ) throws FileNotFoundException, IOException
    {
        String line, text = "";
        try ( BufferedReader reader = new BufferedReader ( new FileReader ( filename ) ) )
        {
            while ( ( line = reader.readLine ( ) ) != null )
                text += line + "\r\n";
        }
        
        return text;
    }
}