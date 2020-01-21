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
 * Устаревшая версия класса для работы с БД
 * @deprecated устаревшая версия для работы с БД
 ***/
public class DBActV1 {
    /***
     * Создание БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int createDB(DBCData dbcdata, DMTData dmtdata) {
        try {
            Utils.log("+++Начато создание БД");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД");

                connection.setAutoCommit(false);

                Utils.log("+++Начато создание БД");

                int result = DBActInterface.execCreate(connection, dbcdata, dmtdata);
                if (result < 0) {
                    return result;
                }
                if (result > 0) {
                    Utils.log("++Cоздание БД завершено с предупреждениями");
                    return result;
                }

                Utils.log("+++Успешно создана БД");
                return 0;
            }
        } catch (ClassNotFoundException | SQLException | CustomException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }

    /***
     * Удаление БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int dropDB(DBCData dbcdata, DMTData dmtdata) {
        try {
            Utils.log("+++Начато удаление БД");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД");

                connection.setAutoCommit(false);

                Utils.log("+++Начато удаление БД");

                int result = DBActInterface.execDrop(connection, dmtdata);
                if (result < 0) {
                    return result;
                }
                if (result > 0) {
                    Utils.log("++Удаление БД завершено с предупреждениями");
                    return result;
                }

                Utils.log("+++Успешно удалена БД");
                return 0;
            }
        } catch (ClassNotFoundException | SQLException | CustomException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }

    /***
     * Перемещение файла в БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @param pid pid родитель, если -1 - родителя нет, иначе pid указывает на родителя
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int moveFileToDB(DBCData dbcdata, DMTData dmtdata, File file, int pid) {
        try {
            Utils.log("+++Начата загрузка файла " + file.getAbsolutePath() + " в БД");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД, файл " + file.getAbsolutePath() + " открыт на чтение");

                connection.setAutoCommit(false);

                int result;
                Utils.log("Начато выполнение addFileInfo блока");
                result = DBActInterface.execAddFileInfo(connection, dmtdata, file, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(), pid);
                if (result != 0) {
                    return result;
                }
                Utils.log("Закончено выполнение addFileInfo блока");

                Utils.log("Начато выполнение getFlid блока");
                int fileid = DBActInterface.execGetFileIdByPath(connection, dmtdata, file);
                if (fileid < 0) {
                    return fileid;
                }
                Utils.log("Закончено выполнение getFlid блока");

                Utils.log("Начато выполнение addLine блока");
                result = DBActInterface.execAddLine(connection, dmtdata, file, fileid);
                if (result != 0) {
                    return result;
                }
                
                Utils.log("Данные добавлены");
                Utils.log("Закончено выполнение addLine блока");
                
                Utils.log("Начато обновление временной метки файла с file.id = " + fileid);
                if(DBActInterface.execUpdateTmstmp(connection, dmtdata, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(), fileid) != 0) return -1;
                Utils.log("Успешно закончено обновление временной метки файла с file.id = " + fileid);
                
                Utils.log("+++Файл" + file.getAbsolutePath() + " успешно перемещен в БД с file.id = " + fileid + " и pid = " + (pid == -1 ? "Не задан": pid));
                return fileid;
            }
        } catch (ClassNotFoundException | SQLException | CustomException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }

    /***
     * Удаление файла из БД файла в БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static long deleteFileFromDB(DBCData dbcdata, DMTData dmtdata, int fileid) {
        try {
            Utils.log("+++Начато удаление файла с flid = " + fileid + " из БД");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД");

                connection.setAutoCommit(false);

                Utils.log("Начато выполнение checkFlid блока");
                if (DBActInterface.execCheckFileId(connection, dmtdata, fileid) != 0) {
                    return -1;
                }
                Utils.log("Закончено выполнение checkFlid блока");
                Utils.log("Найден существующий в БД необходимый для удаления файл");

                long result;
                Utils.log("Начато выполнение deleteLines блока");
                result = DBActInterface.execDeleteLines(connection, dmtdata, fileid);
                Utils.log("Закончено выполнение deleteLines блока");

                Utils.log("+++Успешно удалено " + result + " строк из файла с file.id = " + fileid);

                Utils.log("Начато выполнение deleteFile блока");
                if (DBActInterface.execDeleteFileInfo(connection, dmtdata, fileid) != 0) {
                    return -1;
                }
                Utils.log("Закончено выполнение deleteFile блока");
                Utils.log("+++Успешно удалена информация о файле с file.id = " + fileid);

                return result;
            }
        } catch (ClassNotFoundException | SQLException | CustomException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }

    /***
     * Обновление файла в БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int updateFileInDB(DBCData dbcdata, DMTData dmtdata, File file, int fileid) {
        try {
            Utils.log("+++Начато удаление файла с flid = " + fileid + " из БД");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД");

                connection.setAutoCommit(false);

                Utils.log("Начато выполнение checkFlid блока");
                if (DBActInterface.execCheckFileIdByFileId(connection, dmtdata, fileid) <= 0) {
                    return -1;
                }
                Utils.log("Закончено выполнение checkFlid блока");
                Utils.log("Найден существующий в БД необходимый для обновления файл");

                long result;
                Utils.log("Начато выполнение deleteLines блока");
                result = DBActInterface.execDeleteLines(connection, dmtdata, fileid);
                if (result <= 0) {
                    return -1;
                }
                Utils.log("Закончено выполнение deleteLines блока");

                Utils.log("+++Успешно удалено " + result + " строк из файла с file.id = " + fileid);

                Utils.log("Начато выполнение addLine блока");
                result = DBActInterface.execAddLine(connection, dmtdata, file, fileid);
                if (result != 0) {
                    return -1;
                }
                Utils.log("Закончено выполнение addLine блока");
                
                Utils.log("Начато обновление временной метки файла с file.id = " + fileid);
                if(DBActInterface.execUpdateTmstmp(connection, dmtdata, Utils.getCurrentTime(), Utils.getTmstmpFormatForDB(), fileid) != 0) return -1;
                Utils.log("Успешно закончено обновление временной метки файла с file.id = " + fileid);
                
                Utils.log("+++Файл" + file.getAbsolutePath() + " успешно обновлен в БД с file.id = " + fileid);
                return 0;
            }
        } catch (ClassNotFoundException | SQLException | CustomException  ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }

    /***
     * Выгрузка данных файла из БД в файл
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @param fileid id файла в БД
     * @param sort true - сортировать строки, иначе false
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int moveDBToFile(DBCData dbcdata, DMTData dmtdata, File file, int fileid, boolean sort) {
        try {
            Utils.log("+++Начата выгрузка данных из БД в файл");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД, файл " + file.getAbsolutePath() + " открыт на запись");

                connection.setAutoCommit(false);

                Utils.log("+++Начата выгрузка данных из БД в файл");
                
        Utils.log("Начато выполнение getLine блока");
        if(DBActInterface.execGetLines(connection, dmtdata, file, fileid, sort) != 0) return -1;
        
        Utils.log("Закончено выполнение getLine блока");
        Utils.log("+++Выгрузка данных из БД (file.id = " + fileid + ") в файл " + file.getAbsolutePath() + " успешно завершена");
                return 0;
            }
        } catch (ClassNotFoundException | SQLException | CustomException  ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Обновление parent id файла в БД
     * @deprecated 
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @param pid parent id
     * @param fileid id файла в БД
     * @return код завершения (исходя из исполненных statements из DBActInterface)
     ***/
    public static int updateFilePID(DBCData dbcdata, DMTData dmtdata, int pid, int fileid) {
        try {
            Utils.log("+++Начато обновление parent id файла с file.id = " + fileid);

            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbcdata.getConnectionString(), dbcdata.getUsername(), dbcdata.getPassword())) {
                if (connection == null) {
                    throw new CustomException("Соединение не установлено");
                }

                Utils.log("Установлено соединение с СУБД");
                connection.setAutoCommit(false);
                
                int result = DBActInterface.execUpdateFilePID(connection, dmtdata, pid, fileid);
                if(result == 0) Utils.log("+++Успешно закончено обновление parent id файла с file.id = " + fileid + ", pid = " + (pid == -1 ? "Не задан": pid));
                return result;
            }
        } catch (ClassNotFoundException | SQLException | CustomException  ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
}