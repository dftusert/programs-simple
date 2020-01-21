package com.user.test.logsys;

public enum levels
{
    /*
     * ERROR - ERROR FLAG
     * WARNING - WARNING FLAG
     * INFO _ INFO FLAG
     */
    ERROR,
    WARNING,
    INFO;
    
    /**************************
     * getValue<br>
     * return of current value
     * 
     * @return level value
     *************************/
    public String getValue ( )
    {
        switch ( this )
        {
            case ERROR:   return "ERROR";
            case WARNING: return "WARNING";    
            case INFO:    return "INFO";
            default:      return "UNCATEGORIZED";
        }
    }
}