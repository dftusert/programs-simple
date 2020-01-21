#include "logsys.hxx"
logsys::LogSYS::LogSYS(Config cfg): cfg(cfg),
                  CONN_STR("jdbc:sqlite:logs/" + cfg.getLogfile()),
                  sqls(std::map<std::string, std::string>())
{ setSqls(); createTable(); addViews(); }

logsys::Config logsys::LogSYS::getCfg() noexcept { return cfg; }

void logsys::LogSYS::setSqls()
{
    sqls.emplace("create", FileUtils::readFile(cfg.getPathToCreate()));
    sqls.emplace("insert", FileUtils::readFile(cfg.getPathToInsert()));
    sqls.emplace("delete", FileUtils::readFile(cfg.getPathToDelete()));
    sqls.emplace("views", FileUtils::readFile(cfg.getPathToViews()));
}

void logsys::LogSYS::createTable()
{
    sqlite3 *db;
    char* error;
    if (sqlite3_open(cfg.getLogfile().c_str(), &db))
        throw std::runtime_error("Error opening/creating database " + cfg.getLogfile() + ", at createTable stage, error: " + sqlite3_errmsg(db));

    if (sqlite3_exec(db, sqls.at("create").c_str(), nullptr, nullptr, &error))
    {
        sqlite3_close(db);
        std::string message(error);
        sqlite3_free(error);
        throw std::runtime_error("Error creating table at createTable stage for database " + cfg.getLogfile() + ", error: " +message);
    }
    sqlite3_close(db);
}

void logsys::LogSYS::addViews()
{
    sqlite3 *db;
    char* error;
    if (sqlite3_open(cfg.getLogfile().c_str(), &db))
        throw std::runtime_error("Error opening/creating database " + cfg.getLogfile() + ", at addViewsstage, error: " + sqlite3_errmsg(db));

    std::vector<std::string> all_levels(LevelsDesc::getValues());
    std::vector<std::string>::iterator it_begin = all_levels.begin();

    std::string query;
    while(it_begin != all_levels.end())
    {
        query = sqls.at("views");
        // sets view name (first ? for view name)
        query = query.substr(0, query.find('?')) + *it_begin + query.substr(query.find('?') + 1, query.length() - 1);

        // sets select..... where TYPE='?'.... to select.... where TYPE='<*it_begin>'....
        query = query.substr(0, query.find('?')) + *it_begin + query.substr(query.find('?') + 1, query.length() - 1);

        if (sqlite3_exec(db, query.c_str(), nullptr, nullptr, &error))
        {
            sqlite3_close(db);
            std::string message(error);
            sqlite3_free(error);
            throw std::runtime_error("Error creating view: " + *it_begin + " for database " + cfg.getLogfile() + ", error: " +message);
        }
        ++it_begin;
    }
    sqlite3_close(db);
}

void logsys::LogSYS::execPstmtSQL(std::string sqlsKey, Log log)
{
    sqlite3 *db;
    if (sqlite3_open(cfg.getLogfile().c_str(), &db))
        throw std::runtime_error("Error opening/creating database " + cfg.getLogfile() + ", at addViewsstage, error: " + sqlite3_errmsg(db));

    sqlite3_stmt* pstmt;
    sqlite3_prepare_v2(db, sqls.at(sqlsKey).c_str(), -1, &pstmt, nullptr);

    // fix later
    std::string stamp = log.getTimestamp();
    std::string levelval = LevelsDesc::getValue(log.getLevel());
    int code = log.getCode();
    std::string message = log.getMessage();

    sqlite3_bind_text(pstmt, 1, stamp.c_str(), -1, nullptr);
    sqlite3_bind_text(pstmt, 2, levelval.c_str(), -1, nullptr);
    sqlite3_bind_int(pstmt, 3, code);
    sqlite3_bind_text(pstmt, 4, message.c_str(), -1, nullptr);

    // check total errors
    if (sqlite3_step(pstmt) != SQLITE_DONE)
    {
        sqlite3_close(db);
        sqlite3_finalize(pstmt);
        throw std::runtime_error("Error in sql " + sqlsKey);
    }

    // check logic
    int rows_changes = sqlite3_changes(db);
    if(sqlsKey == "insert" && rows_changes != 1)
        throw std::runtime_error("Error inserting log to database: " + cfg.getLogfile() +
        "\r\nlog: [ timestamp: " +  log.getTimestamp() + ", level: " + LevelsDesc::getValue(log.getLevel()) +
        ", code: " + std::to_string(log.getCode()) + ", message: " + log.getMessage() + " ];");;
    if(sqlsKey == "delete" && !(rows_changes > 0))
        throw std::runtime_error("Error deleting log(s), no such log(s) in database: " + cfg.getLogfile() +
        "\r\nlog: [ timestamp: " +  log.getTimestamp() + ", level: " + LevelsDesc::getValue(log.getLevel()) +
         ", code: " + std::to_string(log.getCode()) + ", message: " + log.getMessage() + " ];");

    sqlite3_finalize(pstmt);
    sqlite3_close(db);
}

void logsys::LogSYS::log(Log log) { execPstmtSQL("insert", log); }
void logsys::LogSYS::deleteLog(Log log) { execPstmtSQL("delete", log); }
void logsys::LogSYS::updateLog(Log prev, Log next) { execPstmtSQL("delete", prev); execPstmtSQL("insert", next); }
