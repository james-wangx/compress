package com.codicefun.compress.binarytree;

/**
 * Abstract Binary Tree Node 抽象二叉树节点
 *
 * @param <T> Node Type 具体节点类型
 * @param <E> Element Type 节点数据类型
 */
@SuppressWarnings("unused")
public abstract class Node<T, E> {

    private E value; // 数据
    private T left; // 左节点
    private T right; // 右节点

    public Node() {
    }

    public Node(E value) {
        this.value = value;
    }

    /**
     * 判断当前节点是否为叶子节点
     *
     * @return true or false
     */
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    /**
     * 判断当前节点是否有左孩子
     *
     * @return true or false
     */
    public boolean hasLeftChild() {
        return this.left != null;
    }

    /**
     * 判断当前节点是否有右孩子
     *
     * @return true or false
     */
    public boolean hasRightChild() {
        return this.right != null;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public T getLeftChild() {
        return left;
    }

    public void setLeftChild(T left) {
        this.left = left;
    }

    public T getRightChild() {
        return right;
    }

    public void setRightChild(T right) {
        this.right = right;
    }

}
