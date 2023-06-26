package com.codicefun.compress.huffmancode;

import com.codicefun.compress.binarytree.BinaryTree;

import java.util.*;
import java.util.function.Consumer;

/**
 * 哈夫曼树
 */
@SuppressWarnings("unused")
public class HuffmanTree implements BinaryTree<HTNode> {

    private HTNode root; // 根节点
    private final List<HTNode> nodeList; // 节点列表
    private final List<HTNode> leafList; // 叶子节点列表

    public HuffmanTree(HashMap<Byte, Integer> weightMap) {
        this.nodeList = new ArrayList<>();
        this.leafList = new ArrayList<>();
        // 生成节点列表
        weightMap.forEach((k, v) -> nodeList.add(new HTNode(k, v)));
    }

    /**
     * 判断树是否为空
     *
     * @return true or false
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * 获取根节点
     *
     * @return 根节点
     */
    @Override
    public HTNode getRoot() {
        return root;
    }

    /**
     * 生成哈夫曼树
     */
    public void createTree() {
        while (nodeList.size() > 1) {
            Collections.sort(nodeList);
            HTNode leftNode = nodeList.get(0);
            leftNode.setPath("0");
            HTNode rightNode = nodeList.get(1);
            rightNode.setPath("1");
            HTNode parentNode = new HTNode(null, leftNode.getWeight() + rightNode.getWeight());
            parentNode.setLeftChild(leftNode);
            parentNode.setRightChild(rightNode);
            leftNode.setParent(parentNode);
            rightNode.setParent(parentNode);
            nodeList.remove(leftNode);
            nodeList.remove(rightNode);
            nodeList.add(parentNode);
        }

        this.root = nodeList.get(0);
        calcPath();
    }

    /**
     * 计算节点路径, 并将叶子节点加入列表中
     */
    private void calcPath() {
        preorderTraverse(node -> {
            if (node.getParent() != null) {
                node.setPath(node.getParent().getPath() + node.getPath());
            }
            if (node.isLeaf()) {
                leafList.add(node);
            }
        });
    }

    /**
     * 遍历叶子节点
     *
     * @param consumer 遍历时的操作
     */
    public void forEachLeaf(Consumer<HTNode> consumer) {
        preorderTraverse(node -> {
            if (node.isLeaf()) {
                consumer.accept(node);
            }
        });
    }

    /**
     * 获取所有叶子节点
     *
     * @return 叶子节点列表
     */
    public List<HTNode> getLeafList() {
        return leafList;
    }

    /**
     * 先序遍历
     *
     * @param consumer 遍历时的操作
     */
    @Override
    public void preorderTraverse(Consumer<HTNode> consumer) {
        preorderTraverse(root, consumer);
    }

    /**
     * 中序遍历
     *
     * @param consumer 遍历时的操作
     */
    @Override
    public void inorderTraverse(Consumer<HTNode> consumer) {
        inorderTraverse(root, consumer);
    }

    /**
     * 后序遍历
     *
     * @param consumer 遍历时的操作
     */
    @Override
    public void postorderTraverse(Consumer<HTNode> consumer) {
        postorderTraverse(root, consumer);
    }

    /**
     * 先序遍历
     *
     * @param node     当前节点
     * @param consumer 遍历时的操作
     */
    private void preorderTraverse(HTNode node, Consumer<HTNode> consumer) {
        consumer.accept(node);

        if (node.hasLeftChild()) {
            preorderTraverse(node.getLeftChild(), consumer);
        }

        if (node.hasRightChild()) {
            preorderTraverse(node.getRightChild(), consumer);
        }
    }

    /**
     * 中序遍历
     *
     * @param node     当前节点
     * @param consumer 遍历时的操作
     */
    private void inorderTraverse(HTNode node, Consumer<HTNode> consumer) {
        if (node.hasLeftChild()) {
            inorderTraverse(node.getLeftChild(), consumer);
        }

        consumer.accept(node);

        if (node.hasRightChild()) {
            inorderTraverse(node.getRightChild(), consumer);
        }
    }

    /**
     * 后序遍历
     *
     * @param node     当前节点
     * @param consumer 遍历时的操作
     */
    private void postorderTraverse(HTNode node, Consumer<HTNode> consumer) {
        if (node.hasLeftChild()) {
            postorderTraverse(node, consumer);
        }

        if (node.hasRightChild()) {
            postorderTraverse(node, consumer);
        }

        consumer.accept(node);
    }

}
