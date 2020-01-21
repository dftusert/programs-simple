#ifndef LOGSYS_HXX
#define LOGSYS_HXX

#include "../config/config.hxx"
#include "../fileutils/fileutils.hxx"
#include "../log/log.hxx"
#include "../levelsdesc/levelsdesc.hxx"
#include <string>
#include <map>
#include <vector>
#include <exception>
#include <sqlite3.h>

namespace logsys
{
/* main class, LogSYStem */
class LogSYS
{
private:
    /***
     * cfg         settings ( db name, path to sqls scripts )
     * CONN_STR    jdbc connection string
     * sqls        scripts from files from config
     ***/
    Config cfg;
    std::string const CONN_STR;
    std::map<std::string, std::string> sqls;

    /* fill sqls map: script key <-> script text */
    void setSqls();

    /* create main table for logs */
    void createTable();

    /* add views (views named by levels, count of views - count of levels */
    void addViews();

public:
    /* param [1] cfg  -  config to use */
    LogSYS(Config);

    /* adds new log to table */
    void log(Log);

    /* delete log from param [1], insert new log from param [2] */
    void updateLog(Log, Log);

    /* delete log from param [1] from table */
    void deleteLog(Log);

    /***
     * execute prepared statement, manages log from param [2]
     * executing prepared statement from sqls map, using script assoc with key
     * from param [1]
    ***/
    void execPstmtSQL(std::string, Log);

    Config getCfg() noexcept;
};
}
#endif // LOGSYS_HXX
