#ifndef LOG_HXX
#define LOG_HXX

#include "../levelsdesc/levelsdesc.hxx"
#include <string>
#include <ctime>
#include <locale>

namespace logsys
{
class Log
{
private:
    /***
     * level       log level: ERROR, WARNING, INFO, etc.
     * timestmp    time when log created
     * message     log message
     * code        log code
     ***/
    levels level;
    std::string timestamp;
    std::string message;
    int code;
public:
    /* creates log, adds time to log */
    Log(levels, std::string, int) noexcept;

    levels getLevel() noexcept;
    std::string getTimestamp() noexcept;
    std::string getMessage() noexcept;
    int getCode() noexcept;
};
}
#endif // LOG_HXX
