package com.user.test.logsys;

public class Config
{
    /************************************************************************
     * logfile        file where logs saves
     * pathToCreate   path to create.sql with file
     * pathToInsert   path to insert.sql with file
     * pathToDelete   path to delete.sql with file
     * pathToViews    path to views.sql  with file
     * 
     * path* - includes scripts which will be used for create/insert/delete
     ***********************************************************************/
    private String logfile, pathToCreate, pathToInsert, pathToDelete, pathToViews;
    
    /************************************
     * LogSys - constructor of class<br>
     * construct instance of this class
     * 
     * @param logfile - name of log file
     ************************************/
    Config ( String logfile )
    {
        this.logfile = logfile;
        this.pathToCreate = "sql/create.sql";
        this.pathToInsert = "sql/insert.sql";
        this.pathToDelete = "sql/delete.sql";
        this.pathToViews = "sql/views.sql";
    }
    
    public void setLogfile ( String logfile ) { this.logfile = logfile; }
    public String getLogfile ( ) { return logfile; }
    public void setPathToCreate ( String path ) { pathToCreate = path; }
    public String getPathToCreate ( ) { return pathToCreate; }
    public void setPathToInsert ( String path ) { pathToInsert = path; }
    public String getPathToInsert ( ) { return pathToInsert; }
    public void setPathToDelete ( String path ) { pathToDelete = path; }
    public String getPathToDelete ( ) { return pathToDelete; }
    public void setPathToViews ( String path ) { pathToViews = path; }
    public String getPathToViews ( ) { return pathToViews; }
}
