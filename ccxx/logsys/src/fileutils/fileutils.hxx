#ifndef FILEUTILS_H
#define FILEUTILS_H

#include <string>
#include <fstream>
#include <exception>

namespace logsys
{
/* work with files */
class FileUtils
{
public:
    /* no constructor */
    FileUtils() = delete;

    /***
     * reads text from file
     * param [1] filename - name of file
     * return text from file
     ***/
    static std::string readFile(std::string);
};
}
#endif // FILEUTILS_H
