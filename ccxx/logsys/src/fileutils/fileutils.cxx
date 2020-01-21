#include "fileutils.hxx"
std::string logsys::FileUtils::readFile(std::string filename)
{
    std::ifstream flrd;
    std::string text("");
    int sym;
    flrd.open(filename, std::ios::in);

    if(!flrd.is_open())
        throw std::invalid_argument("can't open file " + filename);

    while((sym = flrd.get()) != EOF)
        text.push_back(static_cast<char>(sym));
    return text;
}
