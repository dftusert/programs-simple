#ifndef MDUNION_H_25122018
#define MDUNION_H_25122018

#include "err.h"

/* максимальный размер строки */
#define STRMAXLEN 64

/* погрешность при сравнении */
#define EPS 1e-3

/* множество поддерживаемых типов */
enum sup_types{T_VOIDPTR, T_CHAR, T_INT, T_UINT, T_FLOAT, T_DOUBLE, T_STRING};

/* множество флаггов */
enum sup_flags{I_FORCE_NULLPTR = 1, I_ALLOW_NULLPTR = 2};

/* объединение для хранения данных некоторого из типов (используется совместно с sup_types) */
union cell_t {
    void*      __ptrval;
    char       __chval;
    int        __intval;
    uint_t     __uintval;
    float      __fltval;
    double     __dblval;
    char       __strval[STRMAXLEN];
};

/* * проверка значения на правильность относительно его типа
 * sup_types - тип значения
 * void* - указатель на значение
 */
struct err_t __check_value(enum sup_types, void*);

/* * установка значения в union согласно типу
 * union cell_t* - указатель на union
 * enum sup_types - тип значения
 * void* - указатель на значение
 * enum sup_flags - флаги
 */
struct err_t __set_union_val(union cell_t*, enum sup_types, void*, enum sup_flags);

/* * получение значения из union согласно типу
 * union cell_t* - указатель на union
 * enum sup_types - тип значения
 * void* - указатель на значение
 * int - для совместимости со строками
 */
struct err_t __get_union_val(union cell_t*, enum sup_types, void*, int);

/* функции сравнения значений одного union с другим по разным типам из sup_types, лучше использовать самописный */
int cmpby_ptrval(union cell_t*, union cell_t*);
int cmpby_chval(union cell_t*, union cell_t*);
int cmpby_intval(union cell_t*, union cell_t*);
int cmpby_uintval(union cell_t*, union cell_t*);
int cmpby_fltval(union cell_t*, union cell_t*);
int cmpby_dblval(union cell_t*, union cell_t*);
int cmpby_strval(union cell_t*, union cell_t*);

#endif // MDUNION_H_25122018