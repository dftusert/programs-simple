package com.dbs.fldp.dbacts;

import com.dbs.fldp.conf.DBCData;
import com.dbs.fldp.conf.DMTData;
import com.dbs.fldp.exceptions.CustomException;
import com.dbs.fldp.utils.Utils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/***
 * Версия класса для работы с БД
 ***/
public class DBActV2 {
    /***
     * connection - соединение
     * dbcdata настройки соединения
     * dmtdata информация о таблицах и т.п.
     ***/
    private Connection connection;
    private DBCData dbcdata;
    private DMTData dmtdata;

    /***
     * Конструктор класса
     * @param dbcdata dbcdata настройки соединения
     * @param dmtdata dmtdata информация о таблицах и т.п.
     */
    public DBActV2(DBCData dbcdata, DMTData dmtdata) {
        try {
            Utils.log("+++Вход в конструктор DBActV2");
            
            this.dbcdata = dbcdata;
            this.dmtdata = dmtdata;
            
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword());
            if (connection == null) {
                throw new CustomException("Соединение не установлено");
            }

            connection.setAutoCommit(false);
            Utils.log("+++Установлено соединение с СУБД, выход из конструктора");
        } catch (SQLException | ClassNotFoundException | CustomException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
        }
    }
    
    /***
     * Закрытие соединения
     ***/
    public void closeConnection() {
        try {
            connection.close();
            Utils.log("+++Соединение с СУБД успешно закрыто");
        } catch (SQLException ex) {
            Utils.log("!!! Ошибка закрытия соединения: " + ex.getMessage());
        }
    }
    
   /***
     * Создание БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int createDB() {
        Utils.log("+++Начато создание БД");
            
        int result = DBActInterface.execCreate(connection, dbcdata, dmtdata);
        if(result < 0) return result;
        if(result > 0) {
            Utils.log("++Cоздание БД завершено с предупреждениями");
            return result;
        }
            
        Utils.log("+++Успешно создана БД");
        return 0;
    }

    /***
     * Удаление БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int dropDB() {
        Utils.log("+++Начато удаление БД");
            
        int result = DBActInterface.execDrop(connection, dmtdata);
        if(result < 0) return result;
        if(result > 0) {
            Utils.log("++Удаление БД завершено с предупреждениями");
            return result;
        }

        Utils.log("+++Успешно удалена БД");
        return 0;
    }
    
    /***
     * Перемещение файла в БД
     * @param file файл
     * @param pid pid родитель, если -1 - родителя нет, иначе pid указывает на родителя
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int moveFileToDB(File file, int pid) {
        Utils.log("+++Начата загрузка файла " + file.getAbsolutePath() + " в БД");
        
        int result;
        Utils.log("Начато выполнение addFileInfo блока");
        result = DBActInterface.execAddFileInfo(connection, dmtdata, file, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(),pid);
        if(result != 0) return result;
        Utils.log("Закончено выполнение addFileInfo блока");
                
        Utils.log("Начато выполнение getFlid блока");
        int fileid = DBActInterface.execGetFileIdByPath(connection, dmtdata, file);
        if(fileid < 0) return fileid;
        Utils.log("Закончено выполнение getFlid блока");

        Utils.log("Начато выполнение addLine блока");
        result = DBActInterface.execAddLine(connection, dmtdata, file, fileid);
        if(result != 0) return result;
        Utils.log("Данные добавлены");
        Utils.log("Закончено выполнение addLine блока");

        Utils.log("Начато обновление временной метки файла с file.id = " + fileid);
        if(DBActInterface.execUpdateTmstmp(connection, dmtdata, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(), fileid) != 0) return -1;
        Utils.log("Успешно закончено обновление временной метки файла с file.id = " + fileid);
        
        Utils.log("+++Файл" + file.getAbsolutePath() + " успешно перемещен в БД с file.id = " + fileid + " и pid = " + (pid == -1 ? "Не задан": pid));
        return fileid;
    }

    /***
     * Удаление файла из БД файла в БД
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public long deleteFileFromDB(int fileid) {
        Utils.log("+++Начато удаление файла с flid = " + fileid + " из БД");
            
        Utils.log("Начато выполнение checkFlid блока");
        if(DBActInterface.execCheckFileId(connection, dmtdata, fileid) != 0) return -1;
        Utils.log("Закончено выполнение checkFlid блока");
        Utils.log("Найден существующий в БД необходимый для удаления файл");

        long result;
        Utils.log("Начато выполнение deleteLines блока");
        result = DBActInterface.execDeleteLines(connection, dmtdata, fileid);
        Utils.log("Закончено выполнение deleteLines блока");
            
        Utils.log("+++Успешно удалено " + result + " строк из файла с file.id = " + fileid);
                
        Utils.log("Начато выполнение deleteFile блока");
        if(DBActInterface.execDeleteFileInfo(connection, dmtdata, fileid) != 0) return -1;
        Utils.log("Закончено выполнение deleteFile блока");
        Utils.log("+++Успешно удалена информация о файле с file.id = " + fileid);
                
        return result;
    }
    
    /***
     * Обновление файла в БД
     * @param file файл
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int updateFileInDB(File file, int fileid) {
            Utils.log("Начато выполнение checkFlid блока");
            if(DBActInterface.execCheckFileIdByFileId(connection, dmtdata, fileid) <= 0) return -1;
            Utils.log("Закончено выполнение checkFlid блока");
            Utils.log("Найден существующий в БД необходимый для обновления файл");
            
            long result;
            Utils.log("Начато выполнение deleteLines блока");
            result = DBActInterface.execDeleteLines(connection, dmtdata, fileid);
            if(result <= 0) return -1;
            Utils.log("Закончено выполнение deleteLines блока");
            
            Utils.log("+++Успешно удалено " + result + " строк из файла с file.id = " + fileid);
            
            Utils.log("Начато выполнение addLine блока");
            result = DBActInterface.execAddLine(connection, dmtdata, file, fileid);
            if(result != 0) return -1;
            Utils.log("Закончено выполнение addLine блока");

            Utils.log("Начато обновление временной метки файла с file.id = " + fileid);
            if(DBActInterface.execUpdateTmstmp(connection, dmtdata, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(), fileid) != 0) return -1;
            Utils.log("Успешно закончено обновление временной метки файла с file.id = " + fileid);
             
            Utils.log("+++Файл" + file.getAbsolutePath() + " успешно обновлен в БД с file.id = " + fileid);
            return 0;
    }
    
    /***
     * Выгрузка данных файла из БД в файл
     * @param file файл
     * @param fileid id файла в БД
     * @param sort true - сортировать строки, иначе false
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int moveDBToFile(File file, int fileid, boolean sort) {
        Utils.log("+++Начата выгрузка данных из БД в файл");
                
        Utils.log("Начато выполнение getLine блока");
        if(DBActInterface.execGetLines(connection, dmtdata, file, fileid, sort) != 0) return -1;
        
        Utils.log("Закончено выполнение getLine блока");
        Utils.log("+++Выгрузка данных из БД (file.id = " + fileid + ") в файл " + file.getAbsolutePath() + " успешно завершена");
        return 0;
    }
    
    /***
     * Обновление parent id файла в БД
     * @param pid parent id
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public int updateFilePID(int pid, int fileid) {
        Utils.log("+++Начато обновление parent id файла с file.id = " + fileid);
        
        int result = DBActInterface.execUpdateFilePID(connection, dmtdata, pid, fileid);
        if(result == 0) Utils.log("+++Успешно закончено обновление parent id файла с file.id = " + fileid + ", pid = " + (pid == -1 ? "Не задан": pid));
        return result;
    }
}