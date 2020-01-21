#include "err.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int init_err(struct err_t* err, enum err errn, unsigned short apfname) {
    if(err == NULL)
        return ERR_RCODE;
    err->__err_code = errn;
    err->__append_fname = apfname;
    err->__fcount = 0;
    return 0;
}

int merge_err(struct err_t* to, struct err_t* from) {
    to->__err_code = from->__err_code;
    if(!to->__append_fname)
        return 0;
    uint_t tcount = to->__fcount;
    for(uint_t i = tcount; i < MAX_FUNC_COUNT && i < tcount + from->__fcount; ++i) {
        to->__funcname[i] = from->__funcname[i - tcount];
        ++(to->__fcount);
    }

    if(tcount + from->__fcount > MAX_FUNC_COUNT - 1)
        return ERR_RCODE;

    return 0;
}

int append_err_fname(struct err_t* err, const char* func) {
    if(err->__fcount == MAX_FUNC_COUNT - 1)
        return ERR_RCODE;

    err->__funcname[err->__fcount] = func;
    ++(err->__fcount);
    return 0;
}

int set_err_fcount(struct err_t* err, uint_t fcount) {
    if(fcount > MAX_FUNC_COUNT - 1)
        return ERR_RCODE;
    err->__fcount = fcount;
    return 0;
}

int err_desc(struct err_t g_err, struct err_info_t* info) {
    info->__fcount = g_err.__fcount;
    switch(g_err.__err_code) {
        case E_OK:            info->__desc = "Ok"; break;
        case E_NULLPTR:       info->__desc = "Nullptr"; break;
        case E_NOTNULLPTR:    info->__desc = "Not Nullptr"; break;
        case E_MEMALLOCERR:   info->__desc = "Memory Allocation Error"; break;
        case E_ISIZE:         info->__desc = "Incorrect Index"; break;
        case E_ITYPE:         info->__desc = "Incorrect Type"; break;
        case E_NOTFOUND:      info->__desc = "Element not found"; break;
        default:              info->__desc = "Error Description Not Found"; break;
    }

    for(uint_t i = 0; i < g_err.__fcount; ++i)
        info->__funcname[i] = g_err.__funcname[i];
    return 0;
}

void debug(struct err_t g_err, const char* ok_text) {
    struct err_info_t info;
    err_desc(g_err, &info);

    if(g_err.__err_code != E_OK) {
        printf("Error: %s\n", info.__desc);
        for(uint_t i = 0; i < info.__fcount; ++i)
            printf("%s\n", info.__funcname[i]);
        exit(-1);
    } else { printf("%s\n", ok_text); for(uint_t i = 0; i < info.__fcount; ++i) printf("%s\n", info.__funcname[i]); }
}
