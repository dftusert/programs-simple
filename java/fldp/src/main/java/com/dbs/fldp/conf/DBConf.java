package com.dbs.fldp.conf;

import java.io.File;

/***
 * Класс конфигурации
 ***/
public class DBConf {
    /***
     * dbcdata - конфигурация dbc (настройки соединения c ORACLE)
     * dmtdata - конфигурация dmt (информация о таблицах, тригерах, последовательностях и т.п.)
     * logfile - файл для записи логов о работе с БД (но не с приложением)
    ***/
    public static DBCData dbcdata = null;
    public static DMTData dmtdata = null;
    public static File logfile = null;
     
    /***
     * Проверка конфигурации dbc
     * @param dbcdata экземпляр с конфигурацией dbc
     * @return true если конфигурация приемлема, иначе false
     ***/
    public static boolean checkDBCData(DBCData dbcdata) {
        if(dbcdata == null) return false;
        
        if(dbcdata.getBase() == null || dbcdata.getBase().isEmpty()) return false;
        if(dbcdata.getHost() == null || dbcdata.getHost().isEmpty()) return false;
        if(dbcdata.getPort() <= 0) return false;
        if(dbcdata.getSid() == null || dbcdata.getSid().isEmpty()) return false;
        if(dbcdata.getUsername() == null || dbcdata.getUsername().isEmpty()) return false;
        return !(dbcdata.getPassword() == null);
    }
    
    /***
     * Проверка конфигурации dmt
     * @param dmtdata экземпляр с конфигурацией dmt
     * @return true если конфигурация приемлема, иначе false
     ***/
    public static boolean checkDMTData(DMTData dmtdata) {
        if(dmtdata == null) return false;
        if(dmtdata.getFileStrg_Tbl() == null || dmtdata.getFilePath_Clmn() == null || 
           dmtdata.getFileStrg_Tbl().isEmpty() || dmtdata.getFilePath_Clmn().isEmpty()) return false;
        
        if(dmtdata.getLineStrg_Tbl() == null || dmtdata.getLineText_Clmn() == null || dmtdata.getLineLPos_Clmn() == null || dmtdata.getLineFlId_Clmn() == null ||
           dmtdata.getLineStrg_Tbl().isEmpty() || dmtdata.getLineText_Clmn().isEmpty() || dmtdata.getLineLPos_Clmn().isEmpty() || dmtdata.getLineFlId_Clmn().isEmpty()) return false;
        if(dmtdata.getLineSeq() == null || dmtdata.getFileSeq() == null ||
           dmtdata.getLineSeq().isEmpty() || dmtdata.getFileSeq().isEmpty()) return false;
        return dmtdata.getGenBatchCnt() > 0 && dmtdata.getGenDropBatchCnt() > 0;
    }
}