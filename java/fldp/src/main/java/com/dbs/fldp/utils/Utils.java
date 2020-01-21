package com.dbs.fldp.utils;

import com.dbs.fldp.conf.DBConf;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/***
 * Класс для дополнительных функций, необходимых для работы
 ***/
public class Utils {

    /***
     * Проверка на одинаковые расширения файлов
     * @param filename имя файла
     * @param ext расширение для проверки
     * @return true если расширения совпадают, иначе false
     ***/
    public static boolean checkFileExtension(String filename, String ext) {
        String fext;
        int idx = filename.lastIndexOf(".");

        if (idx == -1) fext = "";
        else fext = filename.substring(idx + 1);

        return fext.equals(ext);
    }

    /***
     * Получение текущей временной метки
     * @return строка с yyyy-MM-dd HH:mm:ss:SSSSSSS
     ***/
    public static String getCurrentTime() {
        DateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSSSSS");
        Calendar calendar = Calendar.getInstance();
        return time.format(calendar.getTime());
    }
    
    /***
     * Получение формата временной метки для БД
     * @return формат
     ***/
    public static String getTmstmpFormatForDB() {
        return "YYYY-MM-DD HH24:MI:SS:FF7";
    }
    
    /***
     * Логгирование действий с БД
     * @param message сообщение
     ***/
    public static void log(String message) {
        if(DBConf.logfile == null) System.out.println('[' + getCurrentTime() + "]: " + message);
        else {
            try (FileWriter writer = new FileWriter(DBConf.logfile, true)) {
                writer.write('[' + getCurrentTime() + "]: " + message + "\n\n");
                writer.flush();
            } catch (IOException ex) {
                System.out.println('[' + getCurrentTime() + "]: Ошибка записи в лог сообщения" + message + ": " + ex.getMessage() + '\n');
            } 
        }
    }
}
