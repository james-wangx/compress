package com.codicefun.compress.huffmancode;

import com.codicefun.compress.binarytree.Node;

@SuppressWarnings("unused")
public class HTNode extends Node<HTNode, Byte> implements Comparable<HTNode> {

    private HTNode parent; // 父节点
    private int weight; // 权重
    private String path; // 节点路径

    public HTNode() {
    }

    public HTNode(Byte value, int weight) {
        super(value);
        this.weight = weight;
        this.path = "";
    }

    public HTNode getParent() {
        return parent;
    }

    public void setParent(HTNode parent) {
        this.parent = parent;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int compareTo(HTNode o) {
        return this.weight - o.weight;
    }

    @Override
    public String toString() {
        return String.format("{value=%d,weight=%d,path='%s'}", getValue(), weight, path);
    }

}
