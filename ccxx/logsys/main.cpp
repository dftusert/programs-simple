#include <iostream>
#include "config.hxx"
#include "logsys.hxx"

int main()
{
    logsys::Config dbconf("ex.db");
    logsys::LogSYS logsys(dbconf);
    logsys::Log log(logsys::levels::INFO, "Hello, from log", 0);
    logsys::Log log2(logsys::levels::INFO, "Update log", 1);
    logsys.log(log);
    //log = logsys::Log(logsys::levels::ERROR, "ERR", 11); // TEST ERROR
    logsys.updateLog(log,log2);
    return 0;
}
