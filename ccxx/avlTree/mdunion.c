#include "mdunion.h"
#include <stdlib.h>
#include <string.h>

struct err_t __check_value(enum sup_types type, void* value) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__check_value");

    if(type == T_STRING) {
        const char* strval = (const char*) value;
        for(uint_t i = 0; strval[i] != 0; ++i)
            if(i >= STRMAXLEN - 1) {
                reterr.__err_code = E_ITYPE;
                return reterr;
            }
    }

    return reterr;
}

struct err_t __set_union_val(union cell_t* data, enum sup_types type, void* value, enum sup_flags flags) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__set_union_val");

    if(data == NULL || (value == NULL && !(flags & I_ALLOW_NULLPTR))) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    g_err = __check_value(type, value);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK)
        return reterr;
    
    switch(type) {
        case T_VOIDPTR: data->__ptrval = (void*)(((void**) value)[0]); break;
        case T_CHAR:    data->__chval = (char)(((char*) value)[0]); break;
        case T_INT:     data->__intval = (int)(((int*) value)[0]); break;
        case T_UINT:    data->__uintval = (uint_t)(((uint_t*) value)[0]); break;
        case T_FLOAT:   data->__fltval = (float)(((float*) value)[0]); break;
        case T_DOUBLE:  data->__dblval = (double)(((double*) value)[0]); break;
        case T_STRING:  strncpy(data->__strval, (const char*) value, STRMAXLEN); break;
        default:        reterr.__err_code = E_ITYPE; return reterr; break;
    }

    return reterr;
}

struct err_t __get_union_val(union cell_t* data, enum sup_types type, void* value, int chars) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__get_union_val");

    if(data == NULL || value == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }
    if(chars == -1) chars = STRMAXLEN;

    switch(type) {
        case T_VOIDPTR:  ((void**) value)[0] = data->__ptrval; break;
        case T_CHAR:     ((char*) value)[0] = data->__chval; break;
        case T_INT:      ((int*) value)[0] = data->__intval; break;
        case T_UINT:     ((uint_t*) value)[0] = data->__uintval; break;
        case T_FLOAT:    ((float*) value)[0] = data->__fltval; break;
        case T_DOUBLE:   ((double*) value)[0] = data->__dblval; break;
        case T_STRING:   strncpy((char*) value, data->__strval, chars); break; 
        default:         reterr.__err_code = E_ITYPE; return reterr; break;
    }

    return reterr;
}

int cmpby_ptrval(union cell_t* u1, union cell_t* u2) { return u1->__ptrval - u2->__ptrval; }
int cmpby_chval(union cell_t* u1, union cell_t* u2) { return u1->__chval - u2->__chval; }
int cmpby_intval(union cell_t* u1, union cell_t* u2) { return u1->__intval - u2->__intval; }
int cmpby_uintval(union cell_t* u1, union cell_t* u2) { return u1->__uintval - u2->__uintval; }
int cmpby_fltval(union cell_t* u1, union cell_t* u2) {
    if(u1->__fltval > u2->__fltval - EPS && u1->__fltval < u2->__fltval + EPS)
        return 0;
    return u1->__fltval - u2->__fltval;
}

int cmpby_dblval(union cell_t* u1, union cell_t* u2) {
    if(u1->__dblval > u2->__dblval - EPS && u1->__dblval < u2->__dblval + EPS)
        return 0;
    return u1->__dblval - u2->__dblval;
}

int cmpby_strval(union cell_t* u1, union cell_t* u2) {
    uint_t len_str1, len_str2;
    for(len_str1 = 0; len_str1 < STRMAXLEN; ++len_str1)
        if(u1->__strval[len_str1] == 0) break;
    for(len_str2 = 0; len_str2 < STRMAXLEN; ++len_str2)
        if(u2->__strval[len_str2] == 0) break;

    if(len_str1 != len_str2) return len_str1 - len_str2;
    for(uint_t i = 0; i < len_str1; ++i)
        if(u1->__strval[i] > u2->__strval[i] || u1->__strval[i] < u2->__strval[i]) return u1->__strval[i] - u2->__strval[i]; 
    return 0;
}