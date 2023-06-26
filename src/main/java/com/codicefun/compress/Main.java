package com.codicefun.compress;

import com.codicefun.compress.huffmancode.HuffmanCode;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String[] menuItem = {
            "\n====================",
            "程序主菜单",
            "1. 帮助文档",
            "2. 压缩文件",
            "3. 解压文件",
            "0. 退出系统",
            "===================="
    };

    public static void main(String[] args) {
        int choice = -1;

        System.out.println("""
                ****************************************
                            哈夫曼编码压缩程序""");

        while (choice != 0) {
            choice = menu();
            switch (choice) {
                case 1 -> help();
                case 2 -> compress();
                case 3 -> decompress();
            }
        }
    }

    /**
     * 打印菜单, 输入选项
     *
     * @return choice 0~3
     */
    private static int menu() {
        int choice;
        int maxChoice = menuItem.length - 4;

        for (String item : menuItem) {
            System.out.println(item);
        }

        System.out.printf("请输入你的选项(%d~%d)：", 0, maxChoice);
        choice = scanner.nextInt();

        while (choice < 0 || choice > maxChoice) {
            System.out.printf("选项有误，请重新输入，（选项范围：%d~%d）：", 0, maxChoice);
            choice = scanner.nextInt();
        }

        return choice;
    }

    /**
     * 打印说明文档
     */
    private static void help() {
        System.out.println("""
                本系统使用哈夫曼树对文件进行编码后重新存储，以实现压缩的目的。
                选择压缩文件后，输入源文件和压缩后文件路径，即可压缩文件。
                选择解压文件后，输入压缩文件和解压后文件路径，即可解压文件。""");
    }

    /**
     * 压缩
     */
    private static void compress() {
        System.out.print("源文件路径：");
        String srcPath = scanner.next();
        System.out.print("解压后路径：");
        String desPath = scanner.next();

        try {
            compress(srcPath, desPath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 解压
     */
    private static void decompress() {
        System.out.print("压缩文件路径：");
        String srcPath = scanner.next();
        System.out.print("解压后的路径：");
        String desPath = scanner.next();

        try {
            decompress(srcPath, desPath);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 压缩文件
     *
     * @param srcPath 源文件路径
     * @param desPath 压缩路径
     */
    private static void compress(String srcPath, String desPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcPath);
             FileOutputStream fos = new FileOutputStream(desPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            byte[] buf = new byte[fis.available()];
            if (fis.read(buf) != -1) {
                byte[] codes = HuffmanCode.code(buf);
                oos.writeObject(codes);
                oos.writeObject(HuffmanCode.getCodeMap());
                System.out.println("compress success");
            } else {
                System.out.println("compress fail");
            }
        }
    }

    /**
     * 解压文件
     *
     * @param srcPath 源文件路径
     * @param desPath 解压文件路径
     */
    @SuppressWarnings("unchecked")
    private static void decompress(String srcPath, String desPath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(srcPath);
             ObjectInputStream ois = new ObjectInputStream(fis);
             FileWriter fw = new FileWriter(desPath);
             BufferedWriter br = new BufferedWriter(fw)) {
            byte[] codes = (byte[]) ois.readObject();
            HuffmanCode.setCodeMap((HashMap<Byte, String>) ois.readObject());
            String content = HuffmanCode.decode(codes);
            br.write(content);
            System.out.println("decompress success");
        }
    }

}
