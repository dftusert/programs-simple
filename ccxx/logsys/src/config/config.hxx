#ifndef CONFIG_HXX
#define CONFIG_HXX

#include <string>

namespace logsys
{
/* log system settings config */
class Config
{
private:
    /***
     * logfile         file where logs saves
     * pathToCreate    path to create.sql with file
     * pathToInsert    path to insert.sql with file
     * pathToDelete    path to delete.sql with file
     * pathToViews     path to views.sql  with file
     *
     * path* includes scripts which will be used for create/insert/delete
     ***/
    std::string logfile, pathToCreate, pathToInsert, pathToDelete, pathToViews;
public:
    /* param [1] logfile - name of log file */
    Config(std::string) noexcept;

    void setLogfile(std::string) noexcept;
    void setPathToCreate(std::string) noexcept;
    void setPathToInsert(std::string) noexcept;
    void setPathToDelete(std::string) noexcept;
    void setPathToViews(std::string) noexcept;
    std::string getLogfile() noexcept;
    std::string getPathToCreate() noexcept;
    std::string getPathToInsert() noexcept;
    std::string getPathToDelete() noexcept;
    std::string getPathToViews() noexcept;
};
}
#endif // CONFIG_HXX
