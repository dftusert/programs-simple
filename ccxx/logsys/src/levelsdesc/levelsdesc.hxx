#ifndef LEVELSDESC_HXX
#define LEVELSDESC_HXX

#include <vector>
#include <string>

namespace logsys
{
/* levels: ERROR, WARNING, INFO */
enum levels { ERROR, WARNING, INFO };

/* levels description, methods for simple work with levels */
class LevelsDesc
{
public:
    /* no constructor */
    LevelsDesc() = delete;

    /* return level value */
    static std::string getValue(levels) noexcept;

    /* return vector of levels values */
    static std::vector<std::string> getValues() noexcept;
};
}
#endif // LEVELSDESC_HXX
