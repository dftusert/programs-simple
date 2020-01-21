#include "types.h"
#include <stdlib.h>
#include <string.h>

struct err_t init_avltree(struct avl_tree* avlt, enum sup_types type_key, enum sup_types type_val, void* key, void* value, int (*cmp_func)(union cell_t*, union cell_t*), enum sup_flags flags) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "init_avltree");
    
    if(avlt != NULL && !(flags & I_FORCE_NULLPTR)) {
        reterr.__err_code = E_NOTNULLPTR;
        return reterr;
    }

    if(cmp_func == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    avlt->__cmp_function = cmp_func;
    g_err = __create_avlnode(&avlt->__root, type_key, type_val, key, value, I_FORCE_NULLPTR);
    merge_err(&reterr, &g_err);

    return reterr; 
}

struct err_t __create_avlnode(struct avl_node** node, enum sup_types type_key, enum sup_types type_val, void* key, void* value, enum sup_flags flags) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__create_avlnode");
    
    if(node != NULL && !(flags & I_FORCE_NULLPTR)) {
        reterr.__err_code = E_NOTNULLPTR;
        return reterr;
    }

    *node = (struct avl_node*) malloc(sizeof(struct avl_node));
    if(node == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }

    (*node)->__height = 1;
    (*node)->__left_node = NULL;
    (*node)->__right_node = NULL;

    (*node)->__key = (union cell_t*) malloc(sizeof(union cell_t));
    if((*node)->__key == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }

    struct err_t retcode = __set_union_val((*node)->__key, type_key, key, 0);
    merge_err(&reterr, &retcode);
    if(retcode.__err_code != E_OK)
        return reterr;

    (*node)->__value = (union cell_t*) malloc(sizeof(union cell_t));
    if((*node)->__value == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }
    
    retcode = __set_union_val((*node)->__value, type_val, value, I_ALLOW_NULLPTR);
    merge_err(&reterr, &retcode);

    return reterr;
}

struct err_t __create_au_avlnode(struct avl_node** node, union cell_t* key, union cell_t* value, enum sup_flags flags) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__create_au_avlnode");
    
    if(node != NULL && !(flags & I_FORCE_NULLPTR)) {
        reterr.__err_code = E_NOTNULLPTR;
        return reterr;
    }

    *node = (struct avl_node*) malloc(sizeof(struct avl_node));
    if(node == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }

    (*node)->__height = 1;
    (*node)->__left_node = NULL;
    (*node)->__right_node = NULL;

    (*node)->__key = (union cell_t*) malloc(sizeof(union cell_t));
    if((*node)->__key == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }

    (*node)->__key = key;

    (*node)->__value = (union cell_t*) malloc(sizeof(union cell_t));
    if((*node)->__value == NULL) {
        reterr.__err_code = E_MEMALLOCERR;
        return reterr;
    }
    
    (*node)->__value = value;
    return reterr;
}

struct err_t __free_avlnodes(struct avl_node** node) {
    if(*node == NULL)  {
        struct err_t reterr;
        init_err(&reterr, E_OK, 1);
        append_err_fname(&reterr, "__free_avlnodes");

        return reterr;
    }

    __free_avlnodes(&((*node)->__left_node));
    __free_avlnodes(&((*node)->__right_node));

    free((*node)->__key);
    free((*node)->__value);
    free(*node);
    *node = NULL;

    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__free_avlnodes");

    return reterr;
}

struct err_t __free_avlnode(struct avl_node** node) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__free_avlnode");
    
    if(*node == NULL)  {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    free((*node)->__key);
    free((*node)->__value);
    free(*node);
    *node = NULL;

    return reterr;
}

struct err_t free_avltree(struct avl_tree* avlt) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "free_avltree");
    g_err = __free_avlnodes(&(avlt->__root));
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __balance_factor(struct avl_node* node, uint_t* bfactor) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__balance_factor");
    
    uint_t right_height = 0, left_height = 0;
    if(node->__right_node != NULL)
        right_height = node->__right_node->__height;
    if(node->__left_node != NULL)
        left_height = node->__left_node->__height;
    *bfactor = right_height - left_height;

    return reterr;
}

struct err_t __fix_height(struct avl_node* node) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__fix_height");
	
    uint_t right_height = 0, left_height = 0;
    if(node->__right_node != NULL)
        right_height = node->__right_node->__height;
    if(node->__left_node != NULL)
        left_height = node->__left_node->__height;
    
    if(right_height > left_height) node->__height = right_height + 1;
    else node->__height = left_height + 1;

    return reterr;
}

struct err_t __small_right_rotate(struct avl_tree* avlt, struct avl_node* node) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__small_right_rotate");

    if(avlt == NULL || node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

	struct avl_node* left_node = node->__left_node;
    struct avl_node* parent = node->__parent;

    if(left_node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    struct avl_node* right_node = left_node->__right_node;

    if(parent == NULL) avlt->__root = left_node;
    else {
        if(parent->__left_node == node) parent->__left_node = left_node;
        else parent->__right_node = left_node;
    }

    left_node->__right_node = node;
    node->__left_node = right_node;

    node->__parent = left_node;
    if(right_node != NULL) right_node->__parent = node;

    g_err = __fix_height(node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;
    g_err = __fix_height(left_node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

    return reterr;
}

struct err_t __small_left_rotate(struct avl_tree* avlt, struct avl_node* node) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__small_left_rotate");

    if(avlt == NULL || node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

	struct avl_node* right_node = node->__right_node;
    struct avl_node* parent = node->__parent;

    if(right_node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    struct avl_node* left_node = right_node->__left_node;

    if(parent == NULL) avlt->__root = right_node;
    else {
        if(parent->__left_node == node) parent->__left_node = right_node;
        else parent->__right_node = right_node;
    }

    right_node->__left_node = node;
    node->__right_node = left_node;

    node->__parent = right_node;
    if(right_node != NULL) left_node->__parent = node;

    g_err = __fix_height(node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;
    g_err = __fix_height(right_node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

    return reterr;
}

struct err_t __big_right_rotate(struct avl_tree* avlt, struct avl_node* node) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__big_right_rotate");

    if(node->__left_node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    g_err = __small_left_rotate(avlt, node->__left_node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;
    g_err = __small_right_rotate(avlt, node);
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __big_left_rotate(struct avl_tree* avlt, struct avl_node* node) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__big_left_rotate");

    if(node->__right_node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    g_err = __small_right_rotate(avlt, node->__right_node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;
    g_err = __small_left_rotate(avlt, node);
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __balance(struct avl_tree* avlt, struct avl_node* node) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__balance");

    uint_t bfactor, bfactor_lv;
    g_err = __balance_factor(node, &bfactor);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

	if(bfactor == -2) {
        g_err = __balance_factor(node->__right_node, &bfactor_lv);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;

		if( bfactor_lv > 0 ) __big_left_rotate(avlt, node);
		else __small_left_rotate(avlt, node);
	}
	else if(bfactor == 2) {
        g_err = __balance_factor(node->__left_node, &bfactor_lv);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;

		if(bfactor_lv < 0) __big_right_rotate(avlt, node);
        else __small_right_rotate(avlt, node);
	}

	return reterr;
}

struct err_t avl_insert(struct avl_tree* avlt, enum sup_types type_key, enum sup_types type_val, void* key, void* value) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "avl_insert");
    g_err = __avl_insert_kv(avlt, avlt->__root, type_key, type_val, key, value);
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __avl_insert_kv(struct avl_tree* avlt, struct avl_node* node, enum sup_types type_key, enum sup_types type_val, void* key, void* value) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__avl_insert_kv");
    
    if(avlt == NULL || node == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    struct avl_node* parent;

    while(node != NULL) {
        parent = node;
        if(avlt->__cmp_function(key, node->__key) < 0) node = node->__left_node;
        else node = node->__right_node;
    } 
    
    g_err = __create_avlnode(&node, type_key, type_val, key, value, I_FORCE_NULLPTR);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

    if(avlt->__cmp_function(key, parent->__key) < 0) parent->__left_node = node;
    else parent->__right_node = node;
    node->__parent = parent;

    while(parent != NULL) {
        g_err = __fix_height(parent);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;
        g_err = __balance(avlt, parent);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;
        parent = parent->__parent;
    }

    return reterr;
}

struct err_t avl_remove(struct avl_tree* avlt, void* key) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "avl_remove");

    if(avlt == NULL || key == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    g_err = __avl_remove(avlt, avlt->__root, key);
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __avl_remove(struct avl_tree* avlt, struct avl_node* node, void* key) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__avl_remove");
    
    if(avlt == NULL || node == NULL || key == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    int cmpres;
    while(node != NULL) {
        cmpres = avlt->__cmp_function(key, node->__key);
        if(cmpres < 0) node = node->__left_node;
        else if(cmpres > 0) node = node->__right_node;
        else break;
    }

    if(node == NULL) {
        reterr.__err_code = E_NOTFOUND;
        return reterr;
    }

    struct avl_node* parent = node->__parent;
    struct avl_node* left_node = node->__left_node;
    struct avl_node* right_node = node->__right_node;
    struct avl_node *min, *min_parent, *new_node;

    if(right_node == NULL) {
        min = left_node;
        g_err = __find_min(min, &min_parent);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;
    }
    else {
        g_err = __find_min(right_node, &min);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;
        min_parent = min->__parent;
        min_parent->__left_node = NULL;
        min->__left_node = left_node;
    }

    if(parent == NULL) avlt->__root = min;
    else {
        if(parent->__left_node == node) parent->__left_node = min;
        else parent->__right_node = min;
    }

    min->__right_node = right_node;


    g_err = __create_au_avlnode(&new_node, min->__key, min->__value, I_FORCE_NULLPTR);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

    if(avlt->__cmp_function(key, parent->__key) < 0) parent->__left_node = new_node;
    else parent->__right_node = new_node;
    new_node->__parent = parent;

    g_err = __free_avlnode(&node);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;
  
    g_err = __free_avlnode(&min);
    merge_err(&reterr, &g_err);
    if(g_err.__err_code != E_OK) return reterr;

    while(min_parent != NULL) {
        g_err = __fix_height(min_parent);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;
        g_err = __balance(avlt, min_parent);
        merge_err(&reterr, &g_err);
        if(g_err.__err_code != E_OK) return reterr;

        min_parent = min_parent->__parent;
    }

    return reterr;
}

struct err_t __find_min(struct avl_node* node, struct avl_node** found) {
    struct err_t reterr;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__find_min");

    if(node == NULL || found == NULL) {
        reterr.__err_code = E_NULLPTR;
        return reterr;
    }

    while(node != NULL) {
        *found = node;
        node = node->__left_node;
    }

    return reterr;
}

struct err_t avl_find(struct avl_tree* avlt, enum sup_types type_val, void* key, void* value, int chars) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "find");

    g_err = __avl_find(avlt, avlt->__root, type_val, key, value, chars);
    merge_err(&reterr, &g_err);
    return reterr;
}

struct err_t __avl_find(struct avl_tree* avlt, struct avl_node* node, enum sup_types type_val, void* key, void* value, int chars) {
    struct err_t reterr, g_err;
    init_err(&reterr, E_OK, 1);
    append_err_fname(&reterr, "__find");

    int cmpres;
    while(node != NULL) {
        cmpres = avlt->__cmp_function(key, node->__key);
        if(cmpres < 0) node = node->__left_node;
        else if(cmpres > 0) node = node->__right_node;
        else break;
    }
    if(node == NULL) {
        reterr.__err_code = E_NOTFOUND;
        return reterr;
    }

    g_err = __get_union_val(node->__value, type_val, value, chars);
    merge_err(&reterr, &g_err);

    return reterr;
}