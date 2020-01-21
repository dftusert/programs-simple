package com.dbs.fldp.dbacts;

import com.dbs.fldp.conf.DBCData;
import com.dbs.fldp.conf.DMTData;
import com.dbs.fldp.exceptions.CustomException;
import com.dbs.fldp.utils.Utils;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

/***
 * Интерфейс (набор методов) для работы с БД
 ***/
public class DBActInterface {
    /***
     * Создание таблиц, последовательностей, триггеров и т.п.
     * @param connection соединение
     * @param dbcdata настройки соединения
     * @param dmtdata информация о таблицах и т.п.
     * @return 0 - нормально/gbc - предупреждения/-1 - ошибка
     ***/
    public static int execCreate(Connection connection, DBCData dbcdata, DMTData dmtdata) {
        try(Statement create = connection.createStatement()) {
            Utils.log("Начато добавление 'пакетов' для создания БД");
            
            create.addBatch(dmtdata.generateFileStrg_Tbl());
            Utils.log("Пакет generateFileStrg_Tbl добавлен");

            create.addBatch(dmtdata.generateLineStrg_Tbl());
            Utils.log("Пакет generateLineStrg_Tbl добавлен");

            create.addBatch(dmtdata.generateLineStrgFileId_FK());
            Utils.log("Пакет generateLineStrgFlid_FK добавлен");
            
            create.addBatch(dmtdata.generateFileStrgPID_FK());
            Utils.log("Пакет generateFileStrgPID_FK добавлен");

            create.addBatch(dmtdata.generateFileSeq());
            Utils.log("Пакет generateFileSeq добавлен");

            create.addBatch(dmtdata.generateLineSeq());
            Utils.log("Пакет generateLineSeq добавлен");

            create.addBatch(dmtdata.generateTBIFID(dbcdata));
             Utils.log("Пакет generateTBIFID добавлен");

            create.addBatch(dmtdata.generateTBILID(dbcdata));
            Utils.log("Пакет generateTBILID добавлен");
                
            int gbc;
            if ((gbc = create.executeLargeBatch().length) != dmtdata.getGenBatchCnt()) {
                connection.commit();
                Utils.log("!! Предупреждение: количество выполненных пакетов не совпадает с указанным, выполнено " + gbc + ", указано " + dmtdata.getGenBatchCnt());
                return gbc;
            }

            connection.commit();
            return 0;
        } catch(SQLException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Удаление таблиц, последовательностей, триггеров и т.п.
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @return 0 - нормально/gbc - предупреждения/-1 - ошибка
     ***/
    public static int execDrop(Connection connection, DMTData dmtdata) {
        try(Statement drop = connection.createStatement()) {
            Utils.log("Начато добавление 'пакетов' для удаления БД");
        
            drop.addBatch(dmtdata.generateDropLineSeq());
            Utils.log("Пакет generateDropLineSeq добавлен");

            drop.addBatch(dmtdata.generateDropFileSeq());
            Utils.log("Пакет generateDropFileSeq добавлен");

            drop.addBatch(dmtdata.generateDropLineStrg_Tbl());
            Utils.log("Пакет generateDropFileSeq добавлен");

            drop.addBatch(dmtdata.generateDropFileStrg_Tbl());
            Utils.log("Пакет generateDropFileStrg_Tbl добавлен");

            int gbc;
            if ((gbc = drop.executeLargeBatch().length) != dmtdata.getGenDropBatchCnt()) {
                connection.commit();
                Utils.log("!! Предупреждение: количество выполненных пакетов не совпадает с указанным, выполнено " + gbc + ", указано " + dmtdata.getGenDropBatchCnt());
                return gbc;
            }

            connection.commit();
            return 0;
        } catch(SQLException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Добавление информации о файле
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @param tmstmp временная метка
     * @param format формат временной метки
     * @param pid родитель, если -1 - родителя нет, иначе pid указывает на родителя
     * @return 0 - нормально, иначе - ошибка
     ***/
    public static int execAddFileInfo(Connection connection, DMTData dmtdata, File file, String tmstmp, String format, int pid) {
        int result;
        try (PreparedStatement addFileInfo = connection.prepareStatement(dmtdata.getAddFileInfoSQL())) {
            dmtdata.bindAddFileInfoParams(addFileInfo, file.getAbsolutePath(), tmstmp, format, pid);
            
            result = addFileInfo.executeUpdate();
            if(result != 1) throw new CustomException("Результат выполнения addFileInfo не равен 1, результат = " + result);
            
            connection.commit();
            return 0;
        } catch (CustomException | SQLException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Получение fileid по файлу
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @return fileid (> 0) - нормально/-1 - ошибка
     ***/
    public static int execGetFileIdByPath(Connection connection, DMTData dmtdata, File file) {
        int fileid = -1;
        try (PreparedStatement getFileId = connection.prepareStatement(dmtdata.getGetFileIdSQL())) {
            dmtdata.bindGetFileIdParams(getFileId, file.getAbsolutePath());
            
            try (ResultSet rset = getFileId.executeQuery()) {
                while (rset.next())
                    fileid = rset.getInt("id");
                
                if(fileid == -1) throw new CustomException("Невозможно получить id файла, file.id = " + fileid);
            }
            
            return fileid;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Добавление строк файла в БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param file файл
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execAddLine(Connection connection, DMTData dmtdata, File file, int fileid) {
        int sym, lineCnt = 0, result;
        StringBuilder line = new StringBuilder("");
        try {
            try (PreparedStatement addLine = connection.prepareStatement(dmtdata.getAddLineSQL());
                 FileReader freader = new FileReader(file)) {
                if (freader == null) throw new CustomException("Не удалось открыть файл " + file.getAbsolutePath() + " на чтение");
                
                Utils.log("Файл " + file.getAbsolutePath() + " открыт на чтение");
                while ((sym = freader.read()) != -1) {
                    if (sym == '\n') {
                        ++lineCnt;
                        dmtdata.bindAddLineParams(addLine, line.toString(), lineCnt, fileid);
                        result = addLine.executeUpdate();
                        if(result != 1) throw new CustomException("Результат выполнения addLine не равен 1, результат = " + result + ", строка = " + lineCnt + " значение = " + line.toString());
                        
                        line.setLength(0);
                    } else line.append((char) sym);
                }
            }
            
            Utils.log("Файл " + file.getAbsolutePath() + " закрыт");
            connection.commit();
            
            return 0;
        } catch(CustomException | IOException | SQLException ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Проверка существования файла в БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execCheckFileId(Connection connection, DMTData dmtdata, int fileid) {
        int fcnt = -1;
        try(PreparedStatement checkFlid = connection.prepareStatement(dmtdata.getCheckFlidSQL())){
            dmtdata.bindCheckFileIdParams(checkFlid, fileid);
            try (ResultSet rset = checkFlid.executeQuery()) {
                while(rset.next())
                    fcnt = rset.getInt(1);
                if(fcnt != 1) throw new CustomException("Файл с file.id = " + fileid + " в БД не найден fcnt = " + fcnt);
            }
            
            return 0;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Удаление строк файла из БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static long execDeleteLines(Connection connection, DMTData dmtdata, int fileid) {
        long result;
        try(PreparedStatement deleteLines = connection.prepareStatement(dmtdata.getDeleteLinesSQL())){
            dmtdata.bindDeleteLinesParams(deleteLines, fileid);
            result = deleteLines.executeLargeUpdate();
            
            if(result <= 0) return -1;
            
            connection.commit();
            return result;
        } catch (Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Удаление информации о файле из БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execDeleteFileInfo(Connection connection, DMTData dmtdata, int fileid) {
        try(PreparedStatement deleteFile = connection.prepareStatement(dmtdata.getDeleteFileSQL())) {
            dmtdata.bindDeleteFileParams(deleteFile, fileid);
            if(deleteFile.executeUpdate() <= 0) throw new CustomException("Ошибка удаления информации о файле из БД, file.id = " + fileid);
            
            return 0;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Проверка существования заданного fileid в БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param fileid fileid (id файла в БД)
     * @return 1 - нормально/-1 - ошибка
     ***/
    public static int execCheckFileIdByFileId(Connection connection, DMTData dmtdata, int fileid) {
        int fcnt = -1;
        try(PreparedStatement checkFlid = connection.prepareStatement(dmtdata.getCheckFlidSQL())){
            dmtdata.bindCheckFileIdParams(checkFlid, fileid);
            try (ResultSet rset = checkFlid.executeQuery()) {
                while(rset.next())
                    fcnt = rset.getInt(1);
                if(fcnt != 1) throw new CustomException("Файл с file.id = " + fileid + " в БД не найден fcnt = " + fcnt);   
            }
            
            return fcnt;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Выгрузка данных из БД в файл
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param file  выходной файл
     * @param fileid fileid (id файла в БД)
     * @param sort true - сортировать строки, false - не сортировать
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execGetLines(Connection connection, DMTData dmtdata, File file, int fileid, boolean sort) {
        String sql, value;
        if(sort) sql = dmtdata.getOutputFileSortedSQL();
        else sql = dmtdata.getOutputFileSQL();
        try(PreparedStatement getLine = connection.prepareStatement(sql);
                    FileWriter fwriter = new FileWriter(file)) {
            Utils.log("Файл " + file.getAbsolutePath() + " открыт на запись");
            
            if(sort) dmtdata.bindOutputFileSortedParams(getLine, fileid);
            else dmtdata.bindOutputFileParams(getLine, fileid);
                    
            try(ResultSet rset = getLine.executeQuery()) {
                while(rset.next()) {
                    value = rset.getString(dmtdata.getLineText_Clmn());
                    
                    if (value != null) fwriter.write(value + '\n');
                    else fwriter.write('\n');
                    
                    fwriter.flush();
                }
            }
            
            return 0;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Обновление временной метки файла в БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param tmstmp  временная метка
     * @param format формат временной метки
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execUpdateTmstmp(Connection connection, DMTData dmtdata, String tmstmp, String format, int fileid) {
        try(PreparedStatement updateTmstmp = connection.prepareStatement(dmtdata.getUpdateTmstmpSQL())) {
            dmtdata.bindUpdateTmstmpParams(updateTmstmp, tmstmp, format, fileid);
            if(updateTmstmp.executeUpdate() <= 0) throw new CustomException("Ошибка удаления обновления временной метки файла, file.id = " + fileid);
            return 0;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
    
    /***
     * Обновление временной метки файла в БД
     * @param connection соединение
     * @param dmtdata информация о таблицах и т.п.
     * @param pid новый parent id
     * @param fileid fileid (id файла в БД)
     * @return 0 - нормально/-1 - ошибка
     ***/
    public static int execUpdateFilePID(Connection connection, DMTData dmtdata, int pid, int fileid) {
        try(PreparedStatement updateFilePID = connection.prepareStatement(dmtdata.getUpdateFilePID())) {
            dmtdata.bindUpdateFilePIDParams(updateFilePID, pid, fileid);
            if(updateFilePID.executeUpdate() <= 0) throw new CustomException("Ошибка удаления обновления временной метки файла, file.id = " + fileid);
            return 0;
        } catch(Exception ex) {
            Utils.log("!!! Ошибка: " + ex.getMessage());
            return -1;
        }
    }
}