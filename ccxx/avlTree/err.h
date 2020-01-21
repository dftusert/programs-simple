#ifndef ERR_H_25122018
#define ERR_H_25122018

typedef unsigned int uint_t;

/* Количество сохраненных имен функций */
#define MAX_FUNC_COUNT 25
/* "Сигнал" об ошибки для ошибок */
#define ERR_RCODE -1

/* Возможные ошибки */
enum err{E_OK, E_NULLPTR, E_NOTNULLPTR, E_MEMALLOCERR, E_ISIZE, E_ITYPE, E_NOTFOUND};

/* * структура ошибки
 * __err_code - код ошибки
 * __append_fname - добавлять данные или нет при слиянии ошибок
 * __fcount - текущее количество сохраненных имен функций
 * __funcname[MAX_FUNC_COUNT] - сохраненные имена функций
 */
struct err_t {
    enum err           __err_code;
    unsigned short     __append_fname;
    uint_t             __fcount;
    const char*        __funcname[MAX_FUNC_COUNT];
};

/* * структура информации по ошибке
 * __desc - описание ошибки
 * __fcount - текущее количество сохраненных имен функций
 * __funcname[MAX_FUNC_COUNT] - сохраненные имена функций
 */
struct err_info_t {
    const char*     __desc;
    uint_t          __fcount;
    const char*     __funcname[MAX_FUNC_COUNT];
};

/* * инициализация ошибки
 * struct err_t* - указатель на создаваемую ошибку
 * enum err - первичный код ошибки
 * unsigned short - поле __append_fname
 */
int init_err(struct err_t*, enum err, unsigned short);

/* * слияние ошибок (добавление данных 2 ошибки к 1)
 * struct err_t* - указатель на базовую (первую) ошибку
 * struct err_t* - указатель на вторичную (вторую) ошибку
 */
int merge_err(struct err_t*, struct err_t*);

/* * добавление имени функции в __funcname[MAX_FUNC_COUNT]
 * struct err_t* - указатель на меняемую ошибку
 * const char* - имя функции
 */
int append_err_fname(struct err_t*, const char*);

/* * изменение количества хранимых функций (от 0 до MAX_FUNC_COUNT)
 * struct err_t* - указатель на меняемую ошибку
 * uint_t - новое число сохраненных функций (__fcount)
 */
int set_err_fcount(struct err_t*, uint_t);

/* * получение подробных данных об ошибке (err_info_t) на основе err_t
 * struct err_t - ошибка
 * struct err_info_t* - возвращаемое описание ошибки
 */
int err_desc(struct err_t, struct err_info_t*);

/* * "Отладка"
 * struct err_t - ошибка
 * const char* - отображаемое сообщение если ошибки нет (E_OK)
 */
void debug(struct err_t err, const char*);

#endif // ERR_H_25122018