package com.dbs.fldp.conf;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import static java.sql.Types.INTEGER;

/***
 * Класс конфигурации dmt (информация о таблицах, тригерах, последовательностях и т.п.)
 ***/
public class DMTData {
    /***
     * FILESTRG_TBL - имя таблицы для file
     * FILEPATH_CLMN - имя атрибута path
     * FILETMSTMP_CLMN - имя атрибута tmstmp
     * LINESTRG_TBL - имя таблицы line
     * LINETEXT_CLMN - имя атрибута text
     * LINELPOS_CLMN - имя атрибута line position
     * LINEFLID_CLMN - имя атрибута FK flid в line ref на file(id)
     * LINESEQ - последовательность для line.id
     * FILESEQ - последовательность для file.id
     * GENBATCHCNT - количество "пакетов" создания (т.к. разделено создание каждого "объекта" (таблицы, тригера и т.п.)
     * GENDROPBATCHCNT - то же что и GENBATCHCNT, только для удаления
     * DEF_GENBATCHCNT - значение GENBATCHCNT по умолчанию
     * DEF_GENDROPBATCHCNT - значение GENDROPBATCHCNT по умолчанию
    ***/
    private final String FILESTRG_TBL;
    private final String FILEPATH_CLMN;
    private final String FILETMSTMP_CLMN;
    private final String FILEPID_CLMN;
    
    private final String LINESTRG_TBL;
    private final String LINETEXT_CLMN;
    private final String LINELPOS_CLMN;
    private final String LINEFLID_CLMN;
    
    private final String LINESEQ;
    private final String FILESEQ;
    
    private final int GENBATCHCNT;
    private final int GENDROPBATCHCNT;
    
    public static int DEF_GENBATCHCNT = 8;
    public static int DEF_GENDROPBATCHCNT = 4;

    /**
     * Конструктор класса
     * @param filestrg_tbl имя таблицы для file
     * @param filepath_clmn имя атрибута path
     * @param filetmstmp_clmn имя атрибута tmstmp
     * @param filepid_clmn имя атрибута pid
     * @param linestrg_tbl имя таблицы line
     * @param linetext_clmn имя атрибута text
     * @param linelpos_clmn имя атрибута line position
     * @param lineflid_clmn имя атрибута FK flid в line ref на file(id)
     * @param lineseq имя последовательности для line.id
     * @param fileseq имя последовательности для file.id
     * @param genbatchcnt количество "пакетов" создания
     * @param gendropbatchcnt то же что и GENBATCHCNT, только для удаления
     */
    public DMTData(String filestrg_tbl,
                   String filepath_clmn,
                   String filetmstmp_clmn,
                   String filepid_clmn,
                   String linestrg_tbl,
                   String linetext_clmn,
                   String linelpos_clmn,
                   String lineflid_clmn,
                   String lineseq,
                   String fileseq,
                   int genbatchcnt,
                   int gendropbatchcnt) {
        FILESTRG_TBL = filestrg_tbl;
        FILEPATH_CLMN = filepath_clmn;
        FILETMSTMP_CLMN = filetmstmp_clmn;
        FILEPID_CLMN = filepid_clmn;
        LINESTRG_TBL = linestrg_tbl;
        LINETEXT_CLMN = linetext_clmn;
        LINELPOS_CLMN = linelpos_clmn;
        LINEFLID_CLMN = lineflid_clmn;
        LINESEQ = lineseq;
        FILESEQ = fileseq;
        
        if(genbatchcnt != 0) GENBATCHCNT = genbatchcnt;
        else GENBATCHCNT = DEF_GENBATCHCNT;
        if(genbatchcnt != 0) GENDROPBATCHCNT = gendropbatchcnt;
        else GENDROPBATCHCNT = DEF_GENDROPBATCHCNT;
    }
    
    /***
     * Создание таблицы file
     * @return ddl-скрипт
     ***/
    public String generateFileStrg_Tbl() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("create table ");
        sql.append(FILESTRG_TBL);
        
        sql.append("(\nid integer primary key,\n");
        
        sql.append(FILEPATH_CLMN);
        sql.append(" varchar2(200) not null,\n");
        
        sql.append(FILETMSTMP_CLMN);
        sql.append(" timestamp not null,\n");
        
        sql.append(FILEPID_CLMN);
        sql.append(" integer)");
        
        return sql.toString();
    }
    
    /***
     * Создание таблицы line
     * @return ddl-скрипт
     ***/
    public String generateLineStrg_Tbl() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("create table ");
        sql.append(LINESTRG_TBL);
        
        sql.append("(\nid integer primary key,\n");
        
        sql.append(LINETEXT_CLMN);
        sql.append(" varchar2(2500),\n");
        
        sql.append(LINELPOS_CLMN);
        sql.append(" integer not null,\n");
        
        sql.append(LINEFLID_CLMN);
        sql.append(" integer not null)\n");
        
        return sql.toString();
    }
    
    /***
     * Alter table для FK constraint на line.fileid -> file.id
     * @return ddl-скрипт
     ***/
    public String generateLineStrgFileId_FK() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("alter table ");
        sql.append(LINESTRG_TBL);
        sql.append(" add constraint lstrg_");
        sql.append(LINESTRG_TBL);
        sql.append("_id_fk\nforeign key (");
        sql.append(LINEFLID_CLMN);
        sql.append(") references ");
        sql.append(FILESTRG_TBL);
        sql.append("(id)\n");
        
        return sql.toString();
    }
    
    /***
     * Alter table для FK constraint на file.pid -> file.id
     * @return ddl-скрипт
     ***/
    public String generateFileStrgPID_FK() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("alter table ");
        sql.append(FILESTRG_TBL);
        sql.append(" add constraint fstrg_");
        sql.append(FILESTRG_TBL);
        sql.append("_id_fk\nforeign key (");
        sql.append(FILEPID_CLMN);
        sql.append(") references ");
        sql.append(FILESTRG_TBL);
        sql.append("(id)\n");
        
        return sql.toString();
    }

    /***
     * Создание последовательности lineseq
     * @return ddl-скрипт
     ***/
    public String generateLineSeq() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("create sequence ");
        sql.append(LINESEQ);
        sql.append(" start with 1 increment by 1\n");
        
        
        return sql.toString();
    }
    
    /***
     * Создание последовательности fileseq
     * @return ddl-скрипт
     ***/
    public String generateFileSeq() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("create sequence ");
        sql.append(FILESEQ);
        sql.append(" start with 1 increment by 1\n");
        
        return sql.toString();
    }
    
    /***
     * Создание BI-триггера на file
     * @param dbcdata настройки подключения, берется имя пользователя
     * @return sql-скрипт
     ***/
    public String generateTBIFID(DBCData dbcdata) {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("create or replace trigger T_BI_");
        sql.append(FILESTRG_TBL);
        sql.append("_ID\nbefore insert on \"");
        sql.append(dbcdata.getUsername());
        sql.append("\".");
        sql.append(FILESTRG_TBL);
        sql.append("\nfor each row\nbegin\nselect ");
        sql.append(FILESEQ);
        sql.append(".nextval into :NEW.id from dual; \nend;");     
        
        return sql.toString();
    }
    
    /***
     * Создание BI-триггера на line
     * @param dbcdata настройки подключения, берется имя пользователя
     * @return sql-скрипт
     ***/
    public String generateTBILID(DBCData dbcdata) {
        StringBuilder sql = new StringBuilder("");
  
        sql.append("create or replace trigger T_BI_");
        sql.append(LINESTRG_TBL);
        sql.append("_ID\nbefore insert on \"");
        sql.append(dbcdata.getUsername());
        sql.append("\".");
        sql.append(LINESTRG_TBL);
        sql.append("\nfor each row\nbegin\nselect ");
        sql.append(LINESEQ);
        sql.append(".nextval into :NEW.id from dual; \nend;");
        
        return sql.toString();
    }
    
    /***
     * Скрипт удаления file
     * @return sql-скрипт
     ***/
    public String generateDropLineStrg_Tbl() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("drop table ");
        sql.append(LINESTRG_TBL);
        
        return sql.toString();
    }
    
    /***
     * Скрипт удаления line
     * @return sql-скрипт
     ***/
    public String generateDropFileStrg_Tbl() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("drop table ");
        sql.append(FILESTRG_TBL);
        
        return sql.toString();
    }
    
    /***
     * Скрипт удаления fileseq
     * @return sql-скрипт
     ***/
    public String generateDropFileSeq() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("drop sequence ");
        sql.append(FILESEQ);
        
        return sql.toString();
    }
    
    /***
     * Скрипт удаления lineseq
     * @return sql-скрипт
     ***/
    public String generateDropLineSeq() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("drop sequence ");
        sql.append(LINESEQ);
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на добавлении информации о файле
     * @return SQL-скрипт
     ***/
    public String getAddFileInfoSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("insert into ");
        sql.append(FILESTRG_TBL);
        sql.append('(');
        sql.append(FILEPATH_CLMN);
        sql.append(',');
        sql.append(FILETMSTMP_CLMN);
        sql.append(',');
        sql.append(FILEPID_CLMN);
        sql.append(") values(?,TO_TIMESTAMP(?,?),?)");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на получение id файла в БД
     * @return SQL-скрипт
     ***/
    public String getGetFileIdSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("select id from ");
        sql.append(FILESTRG_TBL);
        sql.append(" where ");
        sql.append(FILEPATH_CLMN);
        sql.append("=? order by id asc");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на вставку строки файла
     * @return SQL-скрипт
     ***/
    public String getAddLineSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("insert into ");
        sql.append(LINESTRG_TBL);
        sql.append('(');
        sql.append(LINETEXT_CLMN);
        sql.append(',');
        sql.append(LINELPOS_CLMN);
        sql.append(',');
        sql.append(LINEFLID_CLMN);
        sql.append(") values(?,?,?)");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на проверку существования файла
     * @return SQL-скрипт
     ***/
    public String getCheckFlidSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("select count(*) from ");
        sql.append(FILESTRG_TBL);
        sql.append(" where id=?");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на удаление строк в файле с заданным fileid
     * @return SQL-скрипт
     ***/
    public String getDeleteLinesSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("delete from ");
        sql.append(LINESTRG_TBL);
        sql.append(" where ");
        sql.append(LINEFLID_CLMN);
        sql.append("=?");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на удаление информации о файле с заданным fileid
     * @return SQL-скрипт
     ***/
    public String getDeleteFileSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("delete from ");
        sql.append(FILESTRG_TBL);
        sql.append(" where id=?");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на получение сортированных строк
     * @return SQL-скрипт
     ***/
    public String getOutputFileSortedSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("select ");
        sql.append(LINETEXT_CLMN);
        sql.append(" from ");
        sql.append(LINESTRG_TBL);
        sql.append(" where ");
        sql.append(LINEFLID_CLMN);
        sql.append("=? order by NLSSORT(");
        sql.append(LINETEXT_CLMN);
        sql.append(",'NLS_SORT=BINARY') desc");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на получение строк
     * @return SQL-скрипт
     ***/
    public String getOutputFileSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("select ");
        sql.append(LINETEXT_CLMN);
        sql.append(" from ");
        sql.append(LINESTRG_TBL);
        sql.append(" where ");
        sql.append(LINEFLID_CLMN);
        sql.append("=? order by ");
        sql.append(LINELPOS_CLMN);
        sql.append(" asc");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на обновление временной метки
     * @return SQL-скрипт
     ***/
    public String getUpdateTmstmpSQL() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("update ");
        sql.append(FILESTRG_TBL);
        sql.append(" set ");
        sql.append(FILETMSTMP_CLMN);
        sql.append(" =TO_TIMESTAMP(?,?) where id=?");
        
        return sql.toString();
    }
    
    /***
     * Получение SQL-скрипта на обновление parent id
     * @return SQL-скрипт
     ***/
    public String getUpdateFilePID() {
        StringBuilder sql = new StringBuilder("");
        
        sql.append("update ");
        sql.append(FILESTRG_TBL);
        sql.append(" set ");
        sql.append(FILEPID_CLMN);
        sql.append(" =? where id=?");
        
        return sql.toString();
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param path параметр path
     * @param tmstmp временная метка
     * @param format формат временной метки
     * @param pid родитель, если -1 - родителя нет, иначе pid указывает на родителя
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindAddFileInfoParams(PreparedStatement pstmt, String path, String tmstmp, String format, int pid) throws SQLException {
        pstmt.setString(1, path);
        pstmt.setString(2, tmstmp);
        pstmt.setString(3, format);
        if(pid == -1) pstmt.setNull(4, INTEGER);
        else pstmt.setInt(4, pid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param path параметр path
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindGetFileIdParams(PreparedStatement pstmt, String path) throws SQLException {
        pstmt.setString(1, path);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindCheckFileIdParams(PreparedStatement pstmt, int fileid) throws SQLException {
        pstmt.setInt(1, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param line строка файла
     * @param lineCnt номер строки в файле
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindAddLineParams(PreparedStatement pstmt, String line,int lineCnt, int fileid) throws SQLException {
        pstmt.setString(1, line);
        pstmt.setInt(2, lineCnt);
        pstmt.setInt(3, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindDeleteLinesParams(PreparedStatement pstmt, int fileid) throws SQLException {
        pstmt.setInt(1, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindDeleteFileParams(PreparedStatement pstmt, int fileid) throws SQLException {
        pstmt.setInt(1, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindOutputFileSortedParams(PreparedStatement pstmt, int fileid) throws SQLException {
        pstmt.setInt(1, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindOutputFileParams(PreparedStatement pstmt, int fileid) throws SQLException {
        pstmt.setInt(1, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param tmstmp временная метка
     * @param format формат временной метки
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindUpdateTmstmpParams(PreparedStatement pstmt, String tmstmp, String format, int fileid) throws SQLException {
        pstmt.setString(1, tmstmp);
        pstmt.setString(2, format);
        pstmt.setInt(3, fileid);
    }
    
    /***
     * Привязка параметров к pstmt 
     * @param pstmt prepared statement
     * @param pid parent id
     * @param fileid идентификатор файла
     * @throws java.sql.SQLException невозможно привязать параметры
     ***/
    public void bindUpdateFilePIDParams(PreparedStatement pstmt, int pid, int fileid) throws SQLException {
        if(pid == -1) pstmt.setNull(1, INTEGER);
        else pstmt.setInt(1, pid);
        pstmt.setInt(2, fileid);
    }
    
    /* геттеры */
    public String getFileStrg_Tbl() { return FILESTRG_TBL; }
    public String getFilePath_Clmn() { return FILEPATH_CLMN; }
    public String getLineStrg_Tbl() { return LINESTRG_TBL; }
    public String getLineText_Clmn() { return LINETEXT_CLMN; }
    public String getLineLPos_Clmn() { return LINELPOS_CLMN; }
    public String getLineFlId_Clmn() { return LINEFLID_CLMN; }
    
    public String getLineSeq() { return LINESEQ; }
    public String getFileSeq() { return FILESEQ; }
    
    public int getGenBatchCnt() { return GENBATCHCNT; }
    public int getGenDropBatchCnt() { return GENDROPBATCHCNT; }
}