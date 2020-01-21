package com.dbs.fldp.conf;

/***
 * Конфигурация соединения с СУБД ORACLE
 ***/
public class DBCData {
    /***
     * BASE - обычно jdbc:oracle:thin
     * HOST - хост
     * PORT - порт
     * SID - ORACLE SID
     * USERNAME - имя пользователя
     * PASSWORD - пароль
     ***/
    private final String BASE;
    private final String HOST;
    private final int PORT;
    private final String SID;
    private final String USERNAME;
    private final String PASSWORD;
    
    /***
     * Конструктор класа
     * @param base - обычно jdbc:oracle:thin
     * @param host - хост
     * @param port - порт
     * @param sid - ORACLE SID
     * @param username - имя пользователя
     * @param password - пароль
     ***/
    public DBCData(String base, String host, int port, String sid, String username, String password) {
        BASE = base; HOST = host; PORT = port; SID = sid; USERNAME = username; PASSWORD = password;
    }
    
    /***
     * Получение строки подключения
     * @return строка подключения
     ***/
    public String getConnectionString() {
        return BASE + ":@" + HOST + ':' + PORT + ':' + SID;
    }
    
    /* геттеры */
    public String getBase() { return BASE; }
    public String getHost() { return HOST; }
    public int getPort() { return PORT; }
    public String getSid() { return SID; }
    public String getUsername() { return USERNAME; }
    public String getPassword() { return PASSWORD; }
}
