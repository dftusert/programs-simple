#include "log.hxx"
logsys::Log::Log(levels level, std::string message, int code) noexcept: level(level), code(code)
{
    if ( message.empty())
        this->message = std::string("Empty message");
    else
        this->message = message;

    std::time_t curr_time = std::time(nullptr);
    char tbuffer [ 150 ];                       // in real use smaller, than here
    std::strftime(tbuffer, sizeof(tbuffer), "%Y-%m-%d %H:%M:%S", std::localtime(&curr_time));
    timestamp = std::string(tbuffer);
}

logsys::levels logsys::Log::getLevel() noexcept { return level; }
std::string logsys::Log::getTimestamp() noexcept { return timestamp; }
std::string logsys::Log::getMessage() noexcept { return message; }
int logsys::Log::getCode() noexcept { return code; }
