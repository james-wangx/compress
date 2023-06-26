package com.codicefun.compress.binarytree;

import java.util.function.Consumer;

/**
 * 二叉树接口
 *
 * @param <T> 具体节点类型
 */
@SuppressWarnings("unused")
public interface BinaryTree<T> {

    /**
     * 判断树是否为空
     *
     * @return true or false
     */
    boolean isEmpty();

    /**
     * 创建树
     */
    void createTree();

    /**
     * 获取树的根节点
     *
     * @return 根节点
     */
    T getRoot();

    /**
     * 先序遍历
     *
     * @param consumer 遍历时的操作
     */
    void preorderTraverse(Consumer<T> consumer);

    /**
     * 中序遍历
     *
     * @param consumer 遍历时的操作
     */
    void inorderTraverse(Consumer<T> consumer);

    /**
     * 后序遍历
     *
     * @param consumer 遍历时的操作
     */
    void postorderTraverse(Consumer<T> consumer);

}
