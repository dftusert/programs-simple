#ifndef TYPES_H_25122018
#define TYPES_H_25122018

#include "err.h"
#include "mdunion.h"

/* * узел дерева
 * __key - ключ, сравнение осуществляется по ключу
 * __value - значение
 * __height - высота (от данного узла к наиболее удаленному листу)
 * __parent - узел-родитель
 * __left_node - левый узел
 * __right_node - правый узел
 */
struct avl_node {
    union cell_t*     __key;
    union cell_t*     __value;
    uint_t            __height;

    struct avl_node*     __parent;
    struct avl_node*     __left_node;
    struct avl_node*     __right_node;
};

/* * АВЛ-дерево
 * __root - корневой узел
 * __cmp_function - функция сравнения ключей
 */
struct avl_tree {
    struct avl_node* __root;
    int (*__cmp_function)(union cell_t*, union cell_t*);
};

/* * инициализация дерева
 * struct avl_tree* - указатель на создаваемое дерево
 * enum sup_types - тип данных ключа
 * enum sup_types - тип данных значения
 * void* - указатель на значение ключа
 * void* - указатель на значение данных
 * int (*)(union cell_t*, union cell_t*) - функция сравнения ключей
 * enum sup_flags - флаги
 */
struct err_t init_avltree(struct avl_tree*, enum sup_types, enum sup_types, void*, void*, int (*)(union cell_t*, union cell_t*), enum sup_flags);

/* * создание узла
 * struct avl_node** - указатель на указатель создаваемого узла
 * enum sup_types - тип данных ключа
 * enum sup_types - тип данных значения
 * void* - указатель на значение ключа
 * void* - указатель на значение данных
 * enum sup_flags - флаги
 */
struct err_t __create_avlnode(struct avl_node**, enum sup_types, enum sup_types, void*, void*, enum sup_flags);

/* * создание узла используя union "целиком"
 * struct avl_node** - указатель на указатель создаваемого узла
 * union cell_t* - указатель на union ключа
 * union cell_t* - указатель на union данных
 * enum sup_flags - флаги
 */
struct err_t __create_au_avlnode(struct avl_node**, union cell_t*, union cell_t*, enum sup_flags);

/* * освобождение памяти поддерева
 * struct avl_node** - корень поддерева
 */
struct err_t __free_avlnodes(struct avl_node**);

/* * освобождение памяти узла
 * struct avl_node** - узел
 */
struct err_t __free_avlnode(struct avl_node**);

/* * освобождение памяти, занятой деревом
 * struct avl_tree* - указатель на дерево
 */
struct err_t free_avltree(struct avl_tree*);

/* * balance factor узла
 * struct avl_node* - указатель на узел
 * uint_t* - "возвращаемый через параметр" balance factor
 */
struct err_t __balance_factor(struct avl_node*, uint_t*);

/* * корректировка высоты узла
 * struct avl_node* - указатель на узел
 */
struct err_t __fix_height(struct avl_node*);

/* * малый правый поворот
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 */
struct err_t __small_right_rotate(struct avl_tree*, struct avl_node*);

/* * малый левый поворот
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 */
struct err_t __small_left_rotate(struct avl_tree*, struct avl_node*);

/* * большой правый поворот (могут быть проблемы/ошибки)
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 */
struct err_t __big_right_rotate(struct avl_tree*, struct avl_node*);

/* * большой левый поворот (могут быть проблемы/ошибки)
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 */
struct err_t __big_left_rotate(struct avl_tree*, struct avl_node*);

/* * балансировка "относительно" узла
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 */
struct err_t __balance(struct avl_tree*, struct avl_node*);

/* * вставка ключ-значение
 * struct avl_tree* - указатель на дерево
 * enum sup_types - тип данных ключа
 * enum sup_types - тип данных значения
 * void* - указатель на значение ключа
 * void* - указатель на значение данных
 */
struct err_t avl_insert(struct avl_tree*, enum sup_types, enum sup_types, void*, void*);

/* * вставка ключ-значение "относительно" поддерева от узла
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 * enum sup_types - тип данных ключа
 * enum sup_types - тип данных значения
 * void* - указатель на значение ключа
 * void* - указатель на значение данных
 */
struct err_t __avl_insert_kv(struct avl_tree*, struct avl_node*, enum sup_types, enum sup_types, void*, void*);

/* * удаление узла по ключу
 * struct avl_tree* - указатель на дерево
 * void* - указатель на значение ключа
 */
struct err_t avl_remove(struct avl_tree*, void*);

/* * удаление узла по ключу "относительно" поддерева от узла
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 * void* - указатель на значение ключа
 */
struct err_t __avl_remove(struct avl_tree*, struct avl_node*, void*);

/* * поиск минимального ключа "относительно" поддерева от узла
 * struct avl_node* - указатель на узел для поиска
 * struct avl_node** - "возвращаемый" найденный указатель на узел
 */
struct err_t __find_min(struct avl_node*, struct avl_node**);

/* * поиск минимального ключа
 * struct avl_tree* - указатель на дерево
 * enum sup_types - тип данных получаемого значения
 * void* - указатель на значение ключа
 * void* - указатель на получаемое значение
 * int - для совместимости со строками
 */
struct err_t avl_find(struct avl_tree*, enum sup_types, void*, void*, int);

/* * поиск минимального ключа "относительно" поддерева от узла
 * struct avl_tree* - указатель на дерево
 * struct avl_node* - указатель на узел
 * enum sup_types - тип данных получаемого значения
 * void* - указатель на значение ключа
 * void* - указатель на получаемое значение
 * int - для совместимости со строками
 */
struct err_t __avl_find(struct avl_tree*, struct avl_node*, enum sup_types, void*, void*, int);

#endif // TYPES_25122018