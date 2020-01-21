#include "config.hxx"
logsys::Config::Config(std::string logfile) noexcept:
                           logfile(logfile),
                           pathToCreate("sql/create.sql"), pathToInsert("sql/insert.sql"),
                           pathToDelete("sql/delete.sql"), pathToViews("sql/views.sql"){}

void logsys::Config::setLogfile(std::string logfile) noexcept { this->logfile = logfile; }
void logsys::Config::setPathToCreate(std::string path) noexcept { pathToCreate = path; }
void logsys::Config::setPathToInsert(std::string path) noexcept { pathToInsert = path; }
void logsys::Config::setPathToDelete(std::string path) noexcept { pathToDelete = path; }
void logsys::Config::setPathToViews(std::string path) noexcept { pathToViews = path; }

std::string logsys::Config::getLogfile() noexcept { return logfile; }
std::string logsys::Config::getPathToCreate() noexcept { return pathToCreate; }
std::string logsys::Config::getPathToInsert() noexcept { return pathToInsert; }
std::string logsys::Config::getPathToDelete() noexcept { return pathToDelete; }
std::string logsys::Config::getPathToViews() noexcept { return pathToViews; }
