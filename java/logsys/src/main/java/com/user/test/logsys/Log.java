package com.user.test.logsys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log
{
    /****************************************************
     * level - log level: ERROR, WARNING, INFO, etc.
     * message - log message
     * code - log code
     * timestmp - time when log created
     ****************************************************/
    private final levels level;
    private final String message;
    private final Integer code;
    private final String timestmp;
    
    /********************************
     * Log - constructor<br>
     * creates log, adds time to log
     *******************************/
    Log ( levels level, String message, Integer code )
    {
        if ( level == null )
            this.level = levels.INFO;
        else
            this.level = level;
        
        if ( message == null )
            this.message = "Empty message";
        else
            this.message = message;
        
        if ( code == null )
            this.code = 0;
        else
            this.code = code;
        
        DateFormat time = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss:SSSSSSS" );
        Calendar calendar = Calendar.getInstance ( );
        timestmp = time.format ( calendar.getTime ( ) );
    }
    
    public levels getLevel ( ) { return level; }
    public String getMessage ( ) { return message; }
    public Integer getCode ( ) { return code; }
    public String getTimestmp ( ) { return timestmp; }
}
