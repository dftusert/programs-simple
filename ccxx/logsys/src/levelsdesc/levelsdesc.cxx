#include "levelsdesc.hxx"
std::string logsys::LevelsDesc::getValue(levels level) noexcept
{
    switch(level)
    {
    case(ERROR):      return "ERROR";
    case(WARNING):    return "WARNING";
    case(INFO):       return "INFO";
    }
    return "UNCATEGORIZED"; // if added new level in levels, but miss it in this
}

std::vector<std::string> logsys::LevelsDesc::getValues() noexcept
{
    return {getValue(levels::ERROR), getValue(levels::WARNING), getValue(levels::INFO)};
}
