package com.codicefun.compress.huffmancode;

import java.util.HashMap;
import java.util.List;

/**
 * 哈夫曼编码
 */
public class HuffmanCode {

    // 编码本
    private static HashMap<Byte, String> codeMap;

    public static HashMap<Byte, String> getCodeMap() {
        return codeMap;
    }

    public static void setCodeMap(HashMap<Byte, String> codeMap) {
        HuffmanCode.codeMap = codeMap;
    }

    /**
     * 获取字节的权重
     *
     * @param bytes 原始字节数组
     * @return HashMap<Byte, Integer> key 为字节，value 为权重
     */
    private static HashMap<Byte, Integer> getByteWeight(byte[] bytes) {
        HashMap<Byte, Integer> weightMap = new HashMap<>();

        // for (int i = 0; i < bytes.length; i++) {
        //     Integer v = weightMap.get(bytes[i]);
        //     if (v != null) {
        //         weightMap.put(bytes[i], v + 1);
        //     } else {
        //         weightMap.put(bytes[i], 1);
        //     }
        // }

        for (byte b : bytes) {
            weightMap.merge(b, 1, Integer::sum);
        }

        return weightMap;
    }

    /**
     * 创建编码本
     *
     * @param leafList 叶子节点列表
     */
    private static void createCodeMap(List<HTNode> leafList) {
        codeMap = new HashMap<>();
        leafList.forEach(node -> codeMap.put(node.getValue(), node.getPath()));
    }

    /**
     * 按照编码本和原始文本生成编码后的二进制字符串，再将其转为字节数组
     *
     * @param content 原始字节数组
     * @return 编码后的字节数组
     */
    private static byte[] getCode(byte[] content) {
        StringBuilder sb = new StringBuilder();

        for (byte b : content) {
            sb.append(codeMap.get(b));
        }

        int len = (sb.length() + 7) / 8; // 最小长度
        byte[] code = new byte[len]; // 编码数组
        // i 为字符串索引，j 为编码数组索引
        for (int i = 0, j = 0; j < len; i += 8, j++) {
            if (i + 8 > sb.length()) {
                // 最后一个字节不做转换处理，直接放入编码本
                codeMap.put(null, sb.substring(i));
            } else {
                code[j] = (byte) Integer.parseInt(sb.substring(i, i + 8), 2);
            }
        }

        return code;
    }
 
    /**
     * 编码
     *
     * @param bytes 原始字节数组
     * @return 编码后的字节数组
     */
    public static byte[] code(byte[] bytes) {
        // 计算字节的权重
        HashMap<Byte, Integer> weightMap = getByteWeight(bytes);
        // 创建哈夫曼树
        HuffmanTree tree = new HuffmanTree(weightMap);
        tree.createTree();
        // 生成编码本
        createCodeMap(tree.getLeafList());

        // 获得编码，并返回
        return getCode(bytes);
    }

    /**
     * 编码
     *
     * @param content 原始文本内容
     * @return 编码后的字节数组
     */
    public static byte[] code(String content) {
        // 将文本转为字节数组
        byte[] bytes = content.getBytes();

        return code(bytes);
    }

    /**
     * 将字节数组转为二进制字符串
     *
     * @param codes 编码后的字节数组
     * @return 二进制字符串
     */
    private static String getBinaryString(byte[] codes) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < codes.length; i++) {
            String str = Integer.toBinaryString(codes[i] | 0x100);
            String format;
            if (i == codes.length - 1) {
                // 如果是最后一个字节，不需要转换，直接从编码本中取出
                format = codeMap.get(null);
            } else {
                format = str.substring(str.length() - 8);
            }
            sb.append(format);
        }

        return sb.toString();
    }

    /**
     * 将编码的二进制字符串转为原始文本
     *
     * @param codes 编码后的二进制字符串
     * @return 原始文本
     */
    private static String getContent(String codes) {
        StringBuilder sb = new StringBuilder();
        StringBuilder content = new StringBuilder();
        HashMap<String, Byte> decodeMap = new HashMap<>();

        // 生成解码本
        codeMap.forEach((key, value) -> decodeMap.put(value, key));

        int index = 0;
        while (index < codes.length()) {
            sb.append(codes.charAt(index++));
            Byte b = decodeMap.get(sb.toString());
            if (b != null) {
                content.append((char) (byte) b);
                sb = new StringBuilder();
            }
        }

        return content.toString();
    }

    /**
     * 解码
     *
     * @param codes 编码后的字节数组
     * @return 原始文本内容
     */
    public static String decode(byte[] codes) {
        String binaryString = getBinaryString(codes);

        return getContent(binaryString);
    }

}
