#include "mdunion.h"
#include "types.h"

#include <stdio.h>

int main() {
    struct avl_tree tree;
    int keys[5]={0,1,2,3,4};
    double data[5]={0.5,1.5,2.5,3.5,4.5};
    double fnd1, fnd2, fnd3;
    debug(init_avltree(&tree, T_INT, T_DOUBLE, &(keys[1]), &(data[1]), cmpby_intval, I_FORCE_NULLPTR), "init_avltree: OK");
    debug(avl_insert(&tree, T_INT, T_DOUBLE, &(keys[0]), &(data[0])), "avl_insert: OK");
    debug(avl_insert(&tree, T_INT, T_DOUBLE, &(keys[2]), &(data[2])), "avl_insert: OK");
    debug(avl_insert(&tree, T_INT, T_DOUBLE, &(keys[4]), &(data[4])), "avl_insert: OK");

    debug(avl_find(&tree, T_DOUBLE, &(keys[0]), &fnd1, 0), "avl_find: OK");
    debug(avl_find(&tree, T_DOUBLE, &(keys[1]), &fnd2, 0), "avl_find: OK");
    debug(avl_find(&tree, T_DOUBLE, &(keys[2]), &fnd3, 0), "avl_find: OK");
    debug(avl_remove(&tree, &(keys[2])), "avl_find: OK");
    debug(avl_find(&tree, T_DOUBLE, &(keys[0]), &fnd1, 0), "avl_find: OK");
    debug(avl_find(&tree, T_DOUBLE, &(keys[1]), &fnd2, 0), "avl_find: OK");

    printf("%lf-%lf\n", data[0], fnd1);
    printf("%lf-%lf\n", data[1], fnd2);
    printf("%lf-%lf\n", data[2], fnd3);

    printf("AVL height: %u\n", tree.__root->__height);

    debug(free_avltree(&tree), "free_avltree: OK");
    return 0;
}