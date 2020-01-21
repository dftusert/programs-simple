package com.user.test.logsys;

import java.io.IOException;
import java.sql.SQLException;

public class Main
{
    public static void main ( String [ ] args )
    {
        Config defcfg = new Config ( "ex.db" );
        try
        {
            LogSYS logsys = new LogSYS ( defcfg );
            Log log1 = new Log ( levels.INFO, "added new log", null );
            Log log2 = new Log ( levels.WARNING, "warning log", 1 );
            Log log3 = new Log ( levels.ERROR, "error log", -1 );
            
            logsys.log ( log1 );
            logsys.log ( log2 );
            logsys.log ( log3 );
        }
        catch ( ClassNotFoundException | SQLException | IOException ex )
        {
            System.out.println ( "Error: " + ex.getMessage ( ) );
        }
    }
}
