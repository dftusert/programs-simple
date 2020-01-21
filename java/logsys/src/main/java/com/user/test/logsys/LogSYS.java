package com.user.test.logsys;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class LogSYS
{
    /****************************************************
     * cfg - settings ( db name, path to sqls scripts )
     * CONN_STR - jdbc connection string
     * sqlQuery - scripts from files from config
     ***************************************************/
    private final Config cfg;
    private final String CONN_STR;
    private final HashMap< String, String > sqlQuery;
    
    /************************************
     * LogSys - constructor of class<br>
     * construct instance of this class
     * 
     * @param cfg - config to use
     * 
     * @throws IOException
     ************************************/
    LogSYS ( Config cfg ) throws ClassNotFoundException, SQLException, IOException
    {
        sqlQuery = new HashMap<> ( );
        new File ( "logs" ).mkdir ( );
        this.cfg = cfg;
        CONN_STR = "jdbc:sqlite:logs/" + cfg.getLogfile ( );
        
        addSqls ( );
        createMainTable ( );
        addViews ( );
    }
    
    /************************************
     * addSqls<br>
     * add scripts to "scripts storage"
     * 
     * @throws IOException
     ************************************/
    private void addSqls ( ) throws IOException
    {
        sqlQuery.put ( "create", FileUtil.readFile ( cfg.getPathToCreate ( ) ) );
        sqlQuery.put ( "insert", FileUtil.readFile ( cfg.getPathToInsert ( ) ) );
        sqlQuery.put ( "delete", FileUtil.readFile ( cfg.getPathToDelete ( ) ) );
        sqlQuery.put ( "views", FileUtil.readFile ( cfg.getPathToViews ( ) ) );
    }
    
    /************************************
     * createMainTable<br>
     * creates main table for logs
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SQLException
     ************************************/
    private void createMainTable ( ) throws ClassNotFoundException, IOException, SQLException
    {
        Class.forName ( "org.sqlite.JDBC" );
        try ( Connection conn = DriverManager.getConnection ( CONN_STR );
              Statement stmt = conn.createStatement ( ) )
        {
            stmt.execute ( sqlQuery.get ( "create" ) );
        }
    }
    
    /***********************************************************
     * addViews<br>
     * add views for all levels ( ERROR, WARNING, INFO, etc. )
     * 
     * @throws SQLException
     **********************************************************/
    private void addViews ( ) throws SQLException
    {
        try ( Connection conn = DriverManager.getConnection ( CONN_STR );
              Statement stmt = conn.createStatement ( ) )
        {
            levels [ ] arrlevel = levels.class.getEnumConstants ( );
            String query;
            for ( int i = 0; i < arrlevel.length; ++i )
            {
                query = sqlQuery.get ( "views" ).replaceFirst ( "[?]", arrlevel [ i ].getValue ( ) );
                query = query.replaceFirst ( "[?]", arrlevel [ i ].getValue ( ) );
                stmt.execute ( query );
            }
        }
    }
    
    /*******************************************
     * log<br>
     * add new log to main log table
     *
     * @param log - log to add
     * 
     * @throws SQLException
     ******************************************/
    public void log ( Log log ) throws SQLException { execSQL ( "insert", log ); }
    
    /***********************************
     * deleteLog<br>
     * delete log from main log table
     * 
     * @param log log to delete
     * 
     * @throws SQLException
     ***********************************/
    public void deleteLog ( Log log ) throws SQLException { execSQL ( "delete", log ); }

    /***********************************
     * updateLog<br>
     * update log from main log table
     * 
     * @param prev - log to delete
     * @param next - log to add
     * 
     * @throws SQLException
     ***********************************/
    public void updateLog ( Log prev, Log next ) throws SQLException { execSQL ( "delete", prev ); execSQL ( "insert", next ); }
    
    /*********************************************************************
     * execSQL<br>
     * execute delete/insert scripts
     * 
     * @param sqlQueryKey - key from sqlQuery, can be only insert/delete
     * @param log - log for insert/delete/update
     * 
     * @throws SQLException
     ********************************************************************/
    public void execSQL ( String sqlQueryKey, Log log ) throws SQLException
    {
        try ( Connection conn = DriverManager.getConnection ( CONN_STR );
              PreparedStatement pstmt = conn.prepareStatement ( sqlQuery.get ( sqlQueryKey ) ) )
        {
            pstmt.setString ( 1, log.getTimestmp ( ) );
            pstmt.setString ( 2, log.getLevel ( ).getValue ( ) );
            pstmt.setInt ( 3, log.getCode ( ) );
            pstmt.setString ( 4, log.getMessage ( ) );

            pstmt.execute ( );
        }
    }
    
    /**********************
     * getCfg<br>
     * returns cfg
     * 
     * @return used config
     **********************/
    public Config getCfg ( ) { return cfg; }
}
