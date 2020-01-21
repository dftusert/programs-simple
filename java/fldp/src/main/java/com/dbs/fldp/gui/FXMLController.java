package com.dbs.fldp.gui;

import com.dbs.fldp.conf.DBCData;
import com.dbs.fldp.conf.DBConf;
import com.dbs.fldp.conf.DMTData;
import com.dbs.fldp.dbacts.DBActV1;
import com.dbs.fldp.dbacts.DBActV2;
import com.dbs.fldp.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/***
 * FXML Controller содержит обработчики событий - интерфейс работы с пользователем и т.п.
 ***/
public class FXMLController implements Initializable {
    /***
     * window - главная панель компоновки
     * dbcfile - текстовое поле для выбранного dbc файла на вкладке "Конфигурация"
     * dmtfile - текстовое поле (поле) для выбранного dmt файла на вкладке "Конфигурация"
     * logfile - поле выбранного лог файла на вкладке "Конфигурация"
     * dbcSaveFile - поле выбранного dbc файла на вкладке "Создать dbc файл"
     * dmtSaveFile - поле выбранного dmt файла на вкладке "Создать dmt файл"
     * inpFile - поле для выбора файла, предназначенного к загрузке в БД, на вкладке "Основные действия"
     * fileidField - поле для ввода id файла, находящегося в БД, на вкладке "Основные действия"
     * outFile - поле для хранение выходного файла, на вкладке "Основные действия"
     * pidField- поле для ввода parent id файла, находящегося в БД, на вкладке "Основные действия"
     * logarea - место для вывода базовых логов приложения (не связанных с работой с БД) на вкладке "Конфигурация"
     * hostField - поле для заполнения хоста на вкладке "Создать dbc файл"
     * portField - поле для заполнения порта на вкладке "Создать dbc файл"
     * sidField - поле для заполнения ORACLE SID на вкладке "Создать dbc файл"
     * schemaField - поле для заполнения схемы (пользователя) на вкладке "Создать dbc файл" (схема != пользователь)
     * passwordField - поле для заполнения пароля для пользователя на вкладке "Создать dbc файл"
     * clientField - поле на на вкладке "Создать dbc файл", в основном jdbc:oracle:thin
     * filetbl - поле для заполнения "file-таблица" на вкладке "Создать dmt файл"
     * pathattr - поле для заполнения "path атрибут" на вкладке "Создать dmt файл"
     * tmstmpattr - поле для заполнения "tmstmp атрибут" на вкладке "Создать dmt файл"
     * pidattr - поле для заполнения "pid атрибут" на вкладке "Создать dmt файл"
     * linetbl - поле для заполнения "line-таблица" на вкладке "Создать dmt файл"
     * textattr - поле для заполнения "text атрибут" на вкладке "Создать dmt файл"
     * lposattr - поле для заполнения "lineposition атрибут" на вкладке "Создать dmt файл"
     * linefkrfile - поле для заполнения "FK в line ref file(id)" на вкладке "Создать dmt файл"
     * fileseq - поле для заполнения "sequence для file.id" на вкладке "Создать dmt файл"
     * lineseq - поле для заполнения "sequence для line.id" на вкладке "Создать dmt файл"
     * gb - поле для заполнения "GENBATCHCNT" на вкладке "Создать dmt файл"
     * gdb - поле для заполнения "GENDROPBATCHCNT" на вкладке "Создать dmt файл"
     * actChooser - элемент выбора метода взаимодействия с СУБД (DBActV1, DBActV2) на вкладке "Основные действия"
     ***/
    @FXML
    private VBox window;
    @FXML
    private TextField dbcfile, dmtfile, logfile, dbcSaveFile, dmtSaveFile, inpFile, fileidField, outFile, pidField;
    @FXML
    private TextArea logarea;
    @FXML
    private TextField hostField, portField, sidField, schemaField, passwordField, clientField;
    @FXML
    private TextField filetbl, pathattr, tmstmpattr, pidattr, linetbl, textattr, lposattr, linefkrfile, fileseq, lineseq, gb, gdb;
    @FXML
    private ChoiceBox<String> actChooser;

    /***
     * Получение расширения конфигурационного файла исходя из его типа
     * @param type тип конфигурационного файла
     * @return расширение конфигурационного файла или null
     ***/
    private String getConfExtFromType(String type) {
        if(!type.equals("dbc") && !type.equals("dmt")) return null;
        if(type.equals("dbc"))return "dbc";
        else return "dmt";
    }
    /***
     * Выбор файла пользователем, создание диалогового окна
     * @param desc описание расширения файла
     * @param ext расширение файла
     * @return File, выбранный пользователем или null в остальных случаях
     ***/
    private File fileChoose(String desc, String ext) {
        FileChooser fch = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(desc + " files (*." + ext + ")", "*." + ext);
        fch.getExtensionFilters().add(extFilter);
        return fch.showOpenDialog(window.getScene().getWindow());
    }
    
    /***
     * Выбор файла для сохранения пользователем, создание диалогового окна (модифицированного под выбор файла для сохранение)
     * @return File, выбранный пользователем или null в остальных случаях
     ***/
    private File fileSave() {
        FileChooser fch = new FileChooser();
        return fch.showSaveDialog(window.getScene().getWindow());
    }
    
    /**
     * Выбор файла и его отображение в нужном TextField
     * @param field поле, в котором будет находиться путь к файлу и его имя
     */
    private void setFile(TextField field) {
        File file = fileChoose("All", "*");
        if(file == null) return;
        field.setText(file.getAbsolutePath());
    }
    
    /***
     * Занесение информации в текстовое поле о выбранном файле для сохранения
     * @param outfname текстовое поле
     * @param type тип файла: dbc, dmt, на основании типа выбирается расширение
     ***/
    private void setConfFile(TextField outfname, String type) {
        String ext = getConfExtFromType(type);
        if(ext == null) return;
        
        File dfl = fileSave();
        if(dfl == null) return;
        
        if(Utils.checkFileExtension(dfl.getName(), ext)) outfname.setText(dfl.getAbsolutePath());
        else outfname.setText(dfl.getAbsolutePath() + '.' + ext);
    }

    /***
     * Создание и отображение окна для вывода дополнительной информации
     * @param type тип окна/предупреждения
     * @param title заголовок окна
     * @param message отображаемое сообщение
     ***/
    private void popupMessage(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        
        alert.show();
    }

    /***
     * Разбор JSON-файла и обновление полей в DBConf (dисdataб dmtdata)
     * @param file JSON-файл
     * @param dclass класс по которому будет заполнятся экземпляр
     * @param scmsg сообщение при удачном выполнении
     ***/
    private void parseJSONFile(File file, Class dclass, String scmsg) {
        try (FileReader reader = new FileReader(file)) {
            StringBuilder builder = new StringBuilder();

            int sym;
            while ((sym = reader.read()) != -1) builder.append((char) sym);

            Gson gson = new Gson();
            try {
                if (dclass == DBCData.class) {
                    DBCData dbcdata = (DBCData) gson.fromJson(builder.toString(), dclass);
                    if(!DBConf.checkDBCData(dbcdata)) {
                        logarea.appendText('[' + Utils.getCurrentTime() + "]: Обнаружены некорректные данные в dbc файле (необходимо изменить или создать dbc файл)\n");
                        popupMessage(AlertType.INFORMATION, "Ошибка", "Некорректные данные в dbc файле");
                        return;
                    }
                
                    DBConf.dbcdata = dbcdata;
                } else if (dclass == DMTData.class) {
                    DMTData dmtdata = (DMTData) gson.fromJson(builder.toString(), dclass);
                    if(!DBConf.checkDMTData(dmtdata)) {
                        logarea.appendText('[' + Utils.getCurrentTime() + "]: Обнаружены некорректные данные в dmt файле (необходимо изменить или создать dmt файл)\n");
                        popupMessage(AlertType.INFORMATION, "Ошибка", "Некорректные данные в dmt файле");
                        return;
                    }
                    DBConf.dmtdata = dmtdata;
                }
            } catch(JsonSyntaxException ex) {
                if(dclass == DBCData.class) logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка при разборе dbc файла: " + ex.getMessage() + '\n');
                else if (dclass == DMTData.class) logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка при разборе dmt файла: " + ex.getMessage() + '\n');
                
                popupMessage(AlertType.INFORMATION, "Ошибка", "Произошла ошибка при разборе файла, см. logArea");
                return;
            }

            logarea.appendText('[' + Utils.getCurrentTime() + "]: " + scmsg + '\n');
        } catch (IOException ex) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка: " + ex.getMessage() + '\n');
            popupMessage(AlertType.INFORMATION, "Ошибка", "Произошла ошибка: " + ex.getMessage());
        }
    }
    
    /***
     * Загрузка файла dbc/dmt в систему с последующим обновлением
     * @param dfl JSON-файл конфигурации (dbc, dmt)
     * @param type тип (dbc, dmt)
     ***/
    private void loadNewConfSettings(File dfl, String type) {
        String ext = getConfExtFromType(type);
        if(ext == null) return;
        
        if (Utils.checkFileExtension(dfl.getName(), ext)) {
            if(type.equals("dbc")) dbcfile.setText(dfl.getAbsolutePath());
            else dmtfile.setText(dfl.getAbsolutePath());
            
            if (!dfl.canRead()) {
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка чтения " + type + " файла, недостаточно прав\n");
                popupMessage(AlertType.INFORMATION, "Информация", "Ошибка чтения " + type + " файла, недостаточно прав");
                return;
            }

           if(type.equals("dbc")) parseJSONFile(dfl, DBCData.class, "Записана новая конфигурация подключения" + dfl.getAbsolutePath());
           else parseJSONFile(dfl, DMTData.class, "Записана новая информация о таблицах: " + dfl.getAbsolutePath());
        } else {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Выбран несоответствующий расширению " + type + " файл\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Выбран несоответствующий расширению " + type + " файл");
        }
    }
    
    /***
     * Изменение log-файла
     * @param logf log-файл
     ***/
    private void loadLogFile(File logf) {
        try {
            if (!logf.canWrite() && !(logf.createNewFile() && logf.canWrite() && logf.delete())) {
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка записи/создания/удаления, недостаточно прав для работы с log файлом: " + logf.getAbsolutePath() + '\n');
                popupMessage(AlertType.INFORMATION, "Информация", "Ошибка записи/создания/удаления, недостаточно прав для работы с log файлом");
                return;
            }
            
            if(logf.exists()){ logf.delete(); logf.createNewFile(); }
            logfile.setText(logf.getAbsolutePath());
            DBConf.logfile = logf;
            
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Изменен log файл: " + logf.getAbsolutePath() + '\n');
        } catch (IOException ex) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка: " + ex.getMessage() + '\n');
        }
    }
    
    /***
     * Проверка на существование dbcdata и dmtdata в DBConf
     * @return true если конфигурация существует, false - хотя бы одно поле null
     ***/
    private boolean isConfExist() {
        if(DBConf.dbcdata == null) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не заданы настройки подключения");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не заданы настройки подключения\n");
            return false;
        }
        
        if(DBConf.dmtdata == null) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не задана информация о таблицах");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не задана информация о таблицах\n");
            return false;
        }
        
        return true;
    }
    
    /***
     * Выбор и последующая загрузка конфигурации
     * @param type тип конфигурации (dbc, dmt)
     ***/
    private void chooseConfFile(String type) {
        String ext = getConfExtFromType(type);
        if(ext == null) return;
        
        File dbcf = fileChoose(type, ext);
        if (dbcf == null) return;
        loadNewConfSettings(dbcf, type);
    }
    
    /***
     * Синхронизация с файлом и последующая загрузка конфигурации
     * @param srcfname поле с путем и именем к файлу
     * @param type тип конфигурации (dbc, dmt)
     ***/
    private void syncConfFile(TextField srcfname, String type) {
        if(srcfname.getText().isEmpty()) return;
        File dfl = new File(srcfname.getText());
        loadNewConfSettings(dfl, type);
    }
    
    /***
     * Выгрузка из БД в файл
     * @param sort true - сортировать строки, иначе false
     ***/
    private void writeFromDB(boolean sort) {
        if(!isConfExist()) return;
        if(fileidField.getText().isEmpty()) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран file.id");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран file.id для удаления из БД\n");
            return;
        }
        
        if(outFile.getText().isEmpty()) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран файл для загрузки");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран файл для загрузки в БД\n");
            return;
        }
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        File inpf = new File(outFile.getText());
        
        int fileid, result = -1;
        try {
            fileid = Integer.parseUnsignedInt(fileidField.getText());
            
            switch (selected) {
            case "DBActV1":
                result = DBActV1.moveDBToFile(DBConf.dbcdata, DBConf.dmtdata, inpf, fileid, sort);
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                result = dbactv2.moveDBToFile(inpf, fileid, sort);
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
            }
            
            if(result < 0) {
                popupMessage(AlertType.INFORMATION, "Информация", "Ошибка выгрузки в файл");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка обновления файла с file.id = " + fileid + " файлом " + inpf.getAbsolutePath() + ", см. лог\n");
            } else {
                popupMessage(AlertType.INFORMATION, "Информация", "Выгрузка в файл успешно произведена");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Выгрузка в файл успешно произведена, file.id = " + fileid + " файл = " + inpf.getAbsolutePath() + '\n');
            }
        } catch(NumberFormatException ex) {
            popupMessage(AlertType.INFORMATION, "Информация", "file.id не является целым положительным числом");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: file.id не является целым положительным числом file.id = " + fileidField.getText() +'\n');
        }
    }
    
    /***
     * Обработчик выбора dbc-файла
     ***/
    @FXML
    private void chooseDBC() {
        chooseConfFile("dbc");
    }
    
    /***
     * Обработчик cинхронизации dbc-файла
     ***/
    @FXML
    private void syncDBC() {
        syncConfFile(dbcfile, "dbc");
    }
    
    /***
     * Обработчик выбора dmt-файла
     ***/
    @FXML
    private void chooseDMT() {
        chooseConfFile("dmt");
    }
    
    /***
     * Обработчик cинхронизации dmt-файла
     ***/
    @FXML
    private void syncDMT() {
        syncConfFile(dmtfile, "dmt");
    }

    /***
     * Обработчик выбора log-файла
     ***/
    @FXML
    private void chooseLogFile() {
        File logf = fileChoose("All", "*");
        if (logf == null) return;
        
        loadLogFile(logf);
    }
    
    /***
     * Обработчик cинхронизации log-файла
     ***/
    @FXML
    private void syncLogFile() {
        if(logfile.getText().isEmpty()) return;
        File logf = new File(logfile.getText());
        loadLogFile(logf);
    }
    
    /***
     * Обработчик очищения logArea
     ***/    
    @FXML
    private void clearLogArea() {
        logarea.clear();
    }

    /***
     * Обработчик создания dbc-файла
     ***/
    @FXML
    private void createDBCFile() {
        String base, host, sid, username, password; int port;

        if(!clientField.getText().isEmpty()) base = clientField.getText();
        else base = "jdbc:oracle:thin";
        
        host = hostField.getText();
        try {
            port = Integer.parseUnsignedInt(portField.getText());
        } catch(NumberFormatException ex) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Указанный порт не является целым положительным числом: " + portField.getText() + '\n');
            popupMessage(AlertType.INFORMATION, "Информация", "Указанный порт не является целым положительным числом");
            return;
        }
        
        sid = sidField.getText();
        username = schemaField.getText();
        password = passwordField.getText();
        
        DBCData dbcdata = new DBCData(base, host, port, sid, username, password);
        if(!DBConf.checkDBCData(dbcdata)) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Обнаружены неверно заполненные поля при создании dbc файла\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Обнаружены неверно заполненные поля");
            return;
        }

        if(dbcSaveFile.getText().isEmpty()) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран файл для сохранения нового dbc файла\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран файл для сохранения");
            return;
        }
        
        try(FileWriter writer = new FileWriter(dbcSaveFile.getText())) {
            Gson gson = new Gson();
            String text = gson.toJson(dbcdata);
            
            writer.write(text);
            writer.flush();
            
            popupMessage(AlertType.INFORMATION, "Информация", "Создана новая конфигурация подключения (dbc): " + dbcSaveFile.getText());
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Создана новая конфигурация подключения (dbc): " + dbcSaveFile.getText() + '\n');
        } catch (IOException ex) {
            popupMessage(AlertType.INFORMATION, "Информация", "Произошла ошибка, см. logArea");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка создания dbc файла: " + ex.getMessage() + '\n');
        }
    }
    
    /***
     * Обработчик создания dmt-файла
     ***/
    @FXML
    private void createDMTFile() {
        String fst, fpc, tms, pda, lst, ltc, llpc, lfic, lseq, fseq; int gbc, gdbc;
        
        if(!filetbl.getText().isEmpty()) fst = filetbl.getText();
        else fst = "filestorage";
        
        if(!pathattr.getText().isEmpty()) fpc = pathattr.getText();
        else fpc = "fpath";
        
        if(!pidattr.getText().isEmpty()) pda = pidattr.getText();
        else pda = "pid";
        
        if(!tmstmpattr.getText().isEmpty()) tms = tmstmpattr.getText();
        else tms = "tmstmp";
        
        if(!linetbl.getText().isEmpty()) lst = linetbl.getText();
        else lst = "linestorage";
        
        if(!textattr.getText().isEmpty()) ltc = textattr.getText();
        else ltc = "ltext";
        
        if(!lposattr.getText().isEmpty()) llpc = lposattr.getText();
        else llpc = "lpos";
        
        if(!linefkrfile.getText().isEmpty()) lfic = linefkrfile.getText();
        else lfic = "file_id";
        
        if(!lineseq.getText().isEmpty()) lseq = lineseq.getText();
        else lseq = "lineseq";
        
        if(!fileseq.getText().isEmpty()) fseq = fileseq.getText();
        else fseq = "fileseq";
        
        try {
            if(!gb.getText().isEmpty()) gbc = Integer.parseInt(gb.getText());
            else gbc = 0;
            
            if(!gdb.getText().isEmpty()) gdbc = Integer.parseInt(gdb.getText());
            else gdbc = 0;
        } catch(NumberFormatException ex) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Указанный GENDROPBATCHCNT или GENDROPBATCHCNT не является целым положительным числом для создания нового dmt файла\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Указанный GENDROPBATCHCNT или GENDROPBATCHCNT не является целым положительным числом");
            return;
        }
        
        DMTData dmtdata = new DMTData(fst, fpc, tms, pda, lst, ltc, llpc, lfic, lseq, fseq, gbc, gdbc);
        if(!DBConf.checkDMTData(dmtdata)) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Обнаружены неверно заполненные поля при создании нового dmt файла\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Обнаружены неверно заполненные поля");
            return;
        }

        if(dmtSaveFile.getText().isEmpty()) {
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран файл для сохранения нового dmt файла\n");
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран файл для сохранения");
            return;
        }
        
        try(FileWriter writer = new FileWriter(dmtSaveFile.getText())) {
            Gson gson = new Gson();
            String text = gson.toJson(dmtdata);
            
            writer.write(text);
            writer.flush();
            
            popupMessage(AlertType.INFORMATION, "Информация", "Создана новая информация о таблицах (dmt): " + dmtSaveFile.getText());
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Создана новая информация о таблицах (dmt): " + dmtSaveFile.getText() + '\n');
        } catch (IOException ex) {
            popupMessage(AlertType.INFORMATION, "Информация", "Произошла ошибка, см. logArea");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Произошла ошибка создания dbc файла: " + ex.getMessage() + '\n');
        }
    }
    
    /***
     * Обработчик выбора dbc-файла
     ***/
    @FXML
    private void setDBCFile() {
        setConfFile(dbcSaveFile, "dbc");
    }
    
    /***
     * Обработчик выбора dmt-файла
     ***/
    @FXML
    private void setDMTFile() {
        setConfFile(dmtSaveFile, "dmt");
    }
    
    /***
     * Обработчик создания БД
     ***/
    @FXML
    private void createDB() {
        if(!isConfExist()) return;
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        int retcode = -1;
        
        switch (selected) {
            case "DBActV1":
                retcode = DBActV1.createDB(DBConf.dbcdata, DBConf.dmtdata);
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                retcode = dbactv2.createDB();
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
        }
        
        if(retcode == 0) {
            popupMessage(AlertType.INFORMATION, "Информация", "Успешно создана БД");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Успешно создана БД\n");
        } else if (retcode > 0) {
            popupMessage(AlertType.INFORMATION, "Информация", "Предупреждение: несоответствие GENBATCHCNT, см. лог");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Предупреждение: несоответствие GENBATCHCNT, см. лог\n");
        }
        else {
            popupMessage(AlertType.INFORMATION, "Информация", "Ошибка создания БД, см. лог");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка создания БД, см. лог\n");
        }
    }
    
    /***
     * Обработчик удаления БД
     ***/
    @FXML
    private void dropDB() {
        if(!isConfExist()) return;
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        int retcode = -1;
        
        switch (selected) {
            case "DBActV1":
                retcode = DBActV1.dropDB(DBConf.dbcdata, DBConf.dmtdata);
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                retcode = dbactv2.dropDB();
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
        }
        
        if(retcode == 0) {
            popupMessage(AlertType.INFORMATION, "Информация", "Успешно удалена БД");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Успешно удалена БД\n");
        } else if (retcode > 0) {
            popupMessage(AlertType.INFORMATION, "Информация", "Предупреждение: несоответствие GENDROPBATCHCNT, см. лог");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Предупреждение: несоответствие GENDROPBATCHCNT, см. лог\n");
        }
        else {
            popupMessage(AlertType.INFORMATION, "Информация", "Ошибка удаления БД, см. лог");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка удаления БД, см. лог\n");
        }
    }
    
    /***
     * Обработчик определения файла для загрузки
     ***/
    @FXML
    private void uploadInpFile() {
        setFile(inpFile);
    }
    
    /***
     * Обработчик загрузки файла в БД
     ***/
    @FXML
    private void loadFileToDB() {
        if(!isConfExist()) return;
        if(inpFile.getText().isEmpty()) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран файл для загрузки");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран файл для загрузки в БД\n");
            return;
        }
        File inpf = new File(inpFile.getText());
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        int fileid = -1;
        
        int pid = -1;
        if(!pidField.getText().isEmpty()) {
            try {
                pid = Integer.parseUnsignedInt(pidField.getText());
            } catch(NumberFormatException ex) {
                popupMessage(AlertType.INFORMATION, "Информация", "Введенный PID не является целым положительным числом");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Введенный PID не является целым положительным числом\n");
                return;
            }
        }
        
        switch (selected) {
            case "DBActV1":
                fileid = DBActV1.moveFileToDB(DBConf.dbcdata, DBConf.dmtdata, inpf, pid);
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                fileid = dbactv2.moveFileToDB(inpf, pid);
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
        }
        
        if(fileid == -1) {
            popupMessage(AlertType.INFORMATION, "Информация", "Ошибка перемещения файла в БД, см. лог");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка перемещения файла в БД, см. лог\n");
        } else {
            popupMessage(AlertType.INFORMATION, "Информация", "Файл успешно перемещен в БД, см. лог logArea");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Файл " + inpf.getAbsolutePath() + " успешно перемещен в БД, с file.id = " + fileid + " и pid = " + (pid == -1 ? "Не задан": pid) + '\n');
        }
    }
    
    /***
     * Обработчик загрузки файла в БД
     ***/
    @FXML
    private void deleteFileFromDB() {
        if(!isConfExist()) return;
        if(fileidField.getText().isEmpty()) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран file.id");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран file.id для удаления из БД\n");
            return;
        }
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        int flid; long result = -1;
        try {
            flid = Integer.parseUnsignedInt(fileidField.getText());
            
            switch (selected) {
            case "DBActV1":
                result = DBActV1.deleteFileFromDB(DBConf.dbcdata, DBConf.dmtdata, flid);
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                result = dbactv2.deleteFileFromDB(flid);
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
            }
            
            if(result <= 0) {
                popupMessage(AlertType.INFORMATION, "Информация", "Ошибка удаления файла");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка удаления файла с file.id = " + flid + ", см. лог\n");
            } else {
                popupMessage(AlertType.INFORMATION, "Информация", "Файл успешно удален");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Файл с file.id = " + flid + ", успешно удален, удалено " + result + " строк\n");
            }
        } catch(NumberFormatException ex) {
            popupMessage(AlertType.INFORMATION, "Информация", "file.id не является целым положительным числом");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: file.id не является целым положительным числом file.id = " + fileidField.getText() +'\n');
        }
    }
    
    /***
     * Обработчик обновления файла в БД
     ***/
    @FXML
    private void updateFileInDB() {
        if(!isConfExist()) return;
        if(fileidField.getText().isEmpty()) {
            popupMessage(AlertType.INFORMATION, "Информация", "Не выбран file.id");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран file.id для удаления из БД\n");
            return;
        }
        
        int pid = -2;
        if(!pidField.getText().isEmpty()) {
            try {
                pid = Integer.parseInt(pidField.getText());
                if(pid < -1) {
                    popupMessage(AlertType.INFORMATION, "Информация", "Введенный pid < -1");
                    logarea.appendText('[' + Utils.getCurrentTime() + "]: Введенный pid < -1\n");
                }
            } catch(NumberFormatException ex) {
                popupMessage(AlertType.INFORMATION, "Информация", "Введенный PID не является целым положительным числом");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Введенный PID не является целым положительным числом\n");
                return;
            }
        }
        
        String selected = actChooser.getSelectionModel().getSelectedItem();
        
        File inpf;
        if(inpFile.getText().isEmpty()) inpf = null;
        else inpf = new File(inpFile.getText());
        
        int fileid, result = -1;
        try {
            fileid = Integer.parseUnsignedInt(fileidField.getText());
            
            switch (selected) {
            case "DBActV1":
                if(inpf != null) {
                    result = DBActV1.updateFileInDB(DBConf.dbcdata, DBConf.dmtdata, inpf, fileid);
                    if(result != 0) break;
                }
                
                if(pid >= -1) {
                    result = DBActV1.updateFilePID(DBConf.dbcdata, DBConf.dmtdata, pid, fileid);
                    if(result != 0) break;
                }
                break;
            case "DBActV2":
                DBActV2 dbactv2 = new DBActV2(DBConf.dbcdata, DBConf.dmtdata);
                
                if(inpf != null) {
                    result = dbactv2.updateFileInDB(inpf, fileid);
                    if(result != 0) break;
                }
                
                if(pid >= -1) {
                    result = dbactv2.updateFilePID(pid, fileid);
                    if(result != 0) break;
                }
                
                dbactv2.closeConnection();
                break;
            default:
                popupMessage(AlertType.INFORMATION, "Информация", "Не выбран используемый метод");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Не выбран используемый метод\n");
                break;
            }
            
            if(result < 0) {
                popupMessage(AlertType.INFORMATION, "Информация", "Ошибка обновления файла");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Ошибка обновления файла с file.id = " + fileid + ", см. лог\n");
            } else {
                popupMessage(AlertType.INFORMATION, "Информация", "Файл успешно обновлен");
                logarea.appendText('[' + Utils.getCurrentTime() + "]: Файл с file.id = " + fileid + ", успешно обновлен\n");
            }
        } catch(NumberFormatException ex) {
            popupMessage(AlertType.INFORMATION, "Информация", "file.id не является целым положительным числом");
            logarea.appendText('[' + Utils.getCurrentTime() + "]: file.id не является целым положительным числом file.id = " + fileidField.getText() +'\n');
        }
    }
    
    /***
     * Обработчик выбора выходного файла
     * @return void
     ***/
    @FXML
    private void setOutFile() {
        setFile(outFile);
    }
    
    /***
     * Обработчик записи из БД в файл с отсортированными строками 
     * @return void
     ***/
    @FXML
    private void writeFromDBNoSorted() {
        writeFromDB(false);
    }
    
    /***
     * Обработчик записи из БД в файл
     * @return void
     ***/
    @FXML
    private void writeFromDBSorted() {
        writeFromDB(true);
    }
    
    /***
     * Обработчик выключения логирования в файл
     * @return void
     ***/
    @FXML
    private void noLog() {
        DBConf.logfile = null;
        logfile.clear();
	logarea.appendText('[' + Utils.getCurrentTime() + "]: Файл для логирования больше не используется, вывод логов перенаправляется на терминал\n");
    }
    
    /***
     * initialize-метод
     * @param url
     * @param rb
     ***/
    @Override
    public void initialize(URL url, ResourceBundle rb) {
          actChooser.setItems(FXCollections.observableArrayList("DBActV1", "DBActV2"));
          actChooser.setValue(actChooser.getItems().get(0));
    }
}