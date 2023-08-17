# compress - 哈夫曼编码实现文件压缩和解压



## 一、需求分析

### 1.1 功能需求

本课程设计是利用哈夫曼树的相关知识在 IDEA 环境下用 Java 实现一个文件压缩与解压工具。其具体功能要求如下：

- 统计字节出现的次数（频率）

- 根据频率生成节点列表
- 根据节点列表生成哈夫曼树
- 生成编码本
- 字符串编码：对字符串进行编码，降低其大小
- 字符串解码：对字符串进行解码，恢复成原字符串
- 压缩文件：实现文件的压缩
- 解压文件：实现文件的解压



### 1.2 数据需求

- 一个字符串，内容仅为英文或英文符号，大小任意
- 文本文件，内容仅为英文或英文符号，大小任意
- 文件的源路径，一个字符串，用于找到文件的位置
- 文件的目的路径，一个字符串，用于指定文件压缩/解压后的位置



### 1.3 性能需求

- 压缩后的文件必须比压缩前小
- 解压后文件大小和内容与源文件一致



## 二、概要设计

### 2.1 抽象数据类型

选择 树 抽象数据类型

```C
ADT Tree {
    数据元素 D: 一个集合，该集合中的所有元素具有相同的特性
    // 若 D 中仅含有一个数据元素，则 R 为空集；否则 R = {H}，H 是如下的二元关系：
    //（1）在 D 中存在唯一的称为根的数据元素 root ，它在关系 H 下没有前驱。
    //（2）除 root 以外，D 中每个节点在关系 H 下都有且仅有一个前驱。
    数据关系 R: 若 D 为空集，则为空树。
}

// 基本操作
InitTree(Tree);
DestroyTree(Tree);
CreateTree(Tree);
TreeEmpty(Tree);
Root(Tree);
Parent(Tree, x);
FristChild(Tree, x);
NextSibling(Tree, x);
InsertChild(Tree, p, Child);
DeleteChild(Tree, p, i);
TraverseTree(Tree, Visit());

```

数据结构定义如下：

```java
/**
 * 二叉树接口
 */
public interface BinaryTree<T>  {
    boolean isEmpty();
    void createTree();
    T getRoot();
    void preorderTraverse(Consumer<T>  consumer);
    void inorderTraverse(Consumer<T>  consumer);
    void postorderTraverse(Consumer<T>  consumer);
} 
```

```java
/**
 * 抽象二叉树节点
 */
public abstract class  Node<T, E> {
    private E value; // 数据
    private T left; // 左节点
    private T right; // 右节点
    
    // 操作
    public Node(E value);
    public boolean isLeaf();
    public boolean hasLeftChild();
    public boolean hasRightChild();
    public E getValue();
    public void setValue(E value);
    public T getLeftChild();
    public void setLeftChild(T left);
    public T getRightChild();
    public void setRightChild(T right);
}
```

```java
/**
 * 哈夫曼树节点
 */
public class HTNode extends  Node<HTNode, Byte> implements Comparable<HTNode> {
    private HTNode parent; // 父节点
    private int weight; // 权重
    private String path; // 路径
    
    // 操作
    public HTNode(Byte value, int weight);
    public HTNode getParent();
    public void setParent(HTNode parent);
    public int getWeight();
    public void setWeight(int weight);
    public String getPath();
    public void setPath(String path);
    public int compareTo(HTNode o);
    public String toString();
}
```

```java
public class HuffmanTree  implements BinaryTree<HTNode> {
    private HTNode root; // 根节点
    private final List<HTNode>  nodeList; // 节点列表
    private final List<HTNode>  leafList; // 叶子节点列表
    
    // 操作
    public HuffmanTree(HashMap<Byte,  Integer> weightMap);
    public boolean isEmpty();
    public HTNode getRoot();
    public void createTree();
    private void calcPath();
    public void  forEachLeaf(Consumer<HTNode> consumer);
    public List<HTNode> getLeafList();
    public void  preorderTraverse(Consumer<HTNode> consumer);
    public void  inorderTraverse(Consumer<HTNode> consumer);
    public void  postorderTraverse(Consumer<HTNode> consumer);
    private void preorderTraverse(HTNode  node, Consumer<HTNode> consumer);
    private void inorderTraverse(HTNode node,  Consumer<HTNode> consumer);
    private void postorderTraverse(HTNode  node, Consumer<HTNode> consumer);
}
```



### 2.2 项目类图
<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image002.png" alt=""/>



### 2.3 各类概要

#### 2.3.1 Node.java

抽象类，定义了二叉树节点的一些通用属性和方法，供具体的存储类拓展。

#### 2.3.2 BinaryTree.java

接口，定义二叉树的一些通用方法，供具体的存储类实现。

#### 2.3.3  HTNode.java

哈夫曼树节点的存储实现，继承 Node<T ,E> 抽象方法。除了常规的value、left和right属性外，还要添加parent 表示父节点，添加 weight 表示该节点的权重，添加 path表示该节点的路径也即编码。将节点常用的操作分装成相应的方法。

#### 2.3.4  HuffmanTree.java

哈夫曼树的存储实现，实现 BinaryTree&lt;T> 接口。可以根据权重字典生成对应的哈夫曼树,实现树的先序遍历、中序遍历、后续遍历操作。可以使用函数式编程，能够将自定义函数传递给遍历方法，可以在遍历的同时完成各种自定义操作。因为数据只存储在叶子节点上，因此还要实现遍历叶子节点的操作，利用函数式编程，方法和正常遍历操作类似。能够计算节点的路径，即数据的哈夫曼编码。

#### 2.3.5 HuffmanCode.java

哈夫曼编码的具体实现。可以根据文本内容生成相应的哈夫曼树，再根据叶子节点生成相应的编码本，对文本内容重新编码后返回新的编码。解码与此编码的操作相逆，根据编码本还原出原先的文本内容并返回。

#### 2.3.6 HuffmanCodeTest.java

使用 junit 框架对 HuffmanCode 的编码和解码功能进行测试。

#### 2.3.7 Main.java

负责显示程序菜单，获取用户输入，根据用户输入的选项继续接下来的操作，比如显示帮助文档，压缩文件，解压文件或者退出程序。完成一项操作后继续循环打印菜单，知道用户选择退出位置，期间若用户输入选项不合法，会抛出异常，提醒用户正确的输入。



## 三、详细设计

### 3.1 Node<T, E> 抽象类

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image003.png" alt="img" style="width: 300px"/>

以下是 UML 图中有关数据和方法的详细说明：

- 类型参数
    - T表示 Node Type 节点类型，是具体实现类的类型，例如： public class  HTNodde extends Node<HTNode, E>，这时 T 就是具体的  HTNode类型。
    - E表示 Element Type 节点内 value元素的类型，例如：public class HTNode extends Node<HTNode, Byte>，这时 E 就是具体的 Byte 类型。
- 成员变量
    - value 表示节点内的元素，即节点内存放的数据，类型是 E 的对象
    - left表示该节点的左子节点
    - right表示该节点的右子节点
- 方法
    - Node() 空构造方法，抽象类不允许实例化，但空构造方法可以用于给子类创建对象
    - Node(E) 构造方法，初始化节点元素，将参数赋值到  value 上
    - isLeaf() 通过判断该节点的左孩子和右孩子是否为空，来判断该节点是否为叶子节点
    - hasLeftChild() 通过判断该节点的左孩子是否为空，来判断该节点是否有左子节点
    - hasRightChild() 通过判断该节点的右孩子是否为空，来判断该节点是否有右子节点
    - getValue() 返回该节点的 value 属性，获取节点元素
    - setValue(E) 通过参数设置该节点的 value 属性，设置节点元素
    - getLeftChild() 返回该节点的 left 属性即左孩子，获取左子节点
    - setLeftChild(T) 通过参数设置该节点的  left 属性，设置左子节点
    - getRightChild() 返回该节点的 right 属性即右孩子，获取右子节点
    - setRightChild(T) 通过参数设置该节点的 right 属性，设置右子节点



### 3.2 BinaryTree&lt;T> 接口

UML 类图：

BinaryTree&lt;T> 二叉树接口通过使用 Consumer&lt;T> 函数式接口可以实现函数式编程，通过传递函数实现遍历时的自定义操作。

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image004.png" alt="img" style="width: 400px" />

以下是 UML 图中有关数据和方法的详细说明：

- 类型参数
    - T 表示 Node Type 树中节点的类型
- 方法
    - isEmpty()判断该树是否为空树，具体实现交给实现类 HuffmanTree
    - createTree() 创建树，具体实现交给实现类 HuffmanTree
    - getRoot() 获取根节点，具体实现交给实现类 HuffmanTree
    - preorderTraverse(Consumer&lt;T>) 先序遍历，需要传递一个函数式接口，在遍历同时可以完成自定义操作，具体实现交给实现类 HuffmanTree
    - inorderTraverse(Consumer&lt;T>) 中序遍历，需要传递一个函数式接口，在遍历同时可以完成自定义操作，具体实现交给实现类 HuffmanTree
    - postorderTraverse(Consumer&lt;T>) 后序遍历，需要传递一个函数式接口，在遍历同时可以完成自定义操作，具体实现交给实现类 HuffmanTree



### 3.3 HTNode 类

HTNode 哈弗曼节点类通过继承 Node 节点抽象类实现一些属性和方法的拓展，同时实现了 Comparable&lt;T>  接口方便排序。

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/image-20230626142847091.png" style="width: 600px"  alt="img"/>

以下是 UML 图中有关数据和方法的详细说明：

- 成员变量
    - parent 表示该节点的父节点
    - weight 表示该节点中数据所占的权重
    - path 表示该节点的路径，即编码（左  0 右 1）
- 方法
    - HTNode() 空构造方法，创建一个 HTNode 对象
    - HTNode(Byte, int) 构造方法，将参数赋值到 value 属性和 weight 属性，来初始化节点内的元素和权重
    - getWeight() 返回 weight 属性即权重，来获取节点的权重
    - setWeight(int) 将参数赋值到 weight 属性，来设置节点的权重
    - getPath() 返回 path 属性即路径，来获取节点的路径
    - setPath(String) 将参数赋值给 path 属性，来设置节点的路径
    - compareTo(HTNode) 通过实现 Comparable&lt;T> 接口实现 compareTo(&lt;T>) 方法可以让节点之间可以排序，这里要实现升序排序，返回用该节点的 weight 属性 - 参数对象的  weight 属性的值。
    - toString() 定义打印节点对象时显示的字符串，格式为{value=?,weight=?,path='?'}



### 3.4 HuffmanTree 类

HuffmanTree 哈夫曼树类通过实现 BinaryTree&lt;T> 接口规定的方法来实现二叉树的基本操作。HuffmanTree 和 HTNode 之间是一对多的聚合关系，表示一个哈夫曼树可以由零个或多个哈夫曼树节点聚合而成，节点可以独立于树存在。哈夫曼树需要接收一个权重字典来创建树，因此需要用到  HashMap<K, V> 类，为了实现收集节点和排序等操作，需要用到Collections 类，ArrayList&lt;E>类和 List&lt;E> 接口，还要使用 Consumer&lt;T> 函数式接口实现函数式编程，它们是哈夫曼树的依赖。执行自定义函数时，调用  accept(T)。

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image006.png" alt="img" />

以下是 UML 图中有关数据和方法的详细说明：

- 成员变量

    - root 表示根节点
    - nodeList 表示节点列表
    - leafList 表示叶子节点列表

- 方法

    - HuffmanTree(HashMap<Byte, Integer>) 构造方法，new 两个 ArrayList对象初始化节点列表和叶子节点列表，然后遍历参数权重字典，根据权重字典中的字节和权重创建节点列表

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image007.png"  alt=""/>

    - isEmpty() 通过判断该树的根节点是否为空，来判断该树是否为空

    - getRoot() 返回该树的 root 属性即根节点

    - createTree() 对nodeList 进行升序排序，选两个权重最小的节点为左孩子和右孩子，左孩子  path 属性赋值为 "0"，右孩子  path 属性赋值为 "1"。根据两个孩子节点，创建它们的父节点，父节点  value 属性为 null， weight 权重为两个孩子的权重之和。设置父节点的左孩子和右孩子，将左孩子和右孩子的父节点设置为刚创建的父节点。将左孩子和右孩子移除 nodeList，将父节点加入 nodeList，重读上述过程，直到 nodeList 只剩下最后一个节点。  将该树的 root 根节点赋值为 nodeList 的最后那个节点。最后计算每个节点的路径并设置叶子节点列表。

      UML 顺序图如下：
      
      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image008.png"  alt="" style="zoom: 50%; display :block; margin :0 auto;"/>

    - preorderTraverse(HTNode, Consumer&lt;HTNode>)  先序遍历具体的递归实现，先执行传递过来的函数。再判断该节点是否有左孩子，如果有，再次调用本方法，但是传递该节点的左孩子。最后判断该节点是否有右孩子，如果有，再次调用本方法，但是传递该节点的右孩子。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image009.png"  alt="" style="zoom: 50%; display :block; margin :0 auto;"/>

    - inorderTraverse(HTNode, Consumer&lt;HTNode>)  中序遍历具体的递归实现，先判断该节点是否有左孩子，如果有，再次调用本方法，但是传递该节点的左孩子。再执行传递过来的函数。最后判断该节点是否有右孩子，如果有，再次调用本方法，但是传递该节点的右孩子。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image010.png"  alt="" style="zoom: 50%; display :block; margin :0 auto;"/>

    - postorderTraverse(HTNode, Consumer&lt;HTNode>)  后序遍历具体的递归实现，先判断该节点是否有左孩子，如果有，再次调用本方法，但是传递该节点的左孩子。再判断该节点是否有右孩子，如果有，再次调用本方法，但是传递该节点的右孩子。最后执行传递过来的函数。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image011.png"  alt="" style="zoom: 50%; display :block; margin :0 auto;"/>

    - preorderTraverse(Consumer&lt;HTNode>) 为方便调用，用重载可以实现只传递一个自定义函数便可完成遍历，指明需要传递的参数 node 就是该树的根节点 root，表示从根节点开始遍历。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image012.png" alt="img" style="width: 400px" />

    - inorderTraverse(Consumer&lt;HTNode>)为方便调用，用重载可以实现只传递一个自定义函数便可完成遍历，指明需要传递的参数 node 就是该树的根节点 root，表示从根节点开始遍历。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image013.png" alt="img" style="width: 400px" />

    - postorderTraverse(Consumer&lt;HTNode>)为方便调用，用重载可以实现只传递一个自定义函数便可完成遍历，指明需要传递的参数 node 就是该树的根节点 root，表示从根节点开始遍历。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image014.png" alt="img" style="width: 400px" />

    - calcPath() 遍历节点（三种遍历方式都可以，默认使用先序遍历），若该节点不是根节点，计算该节点的 path 属性为它父节点的 path 属性 + 自己的 path 属性。如果该节点是叶子节点，则将该节点加入 leafList 叶子节点列表中。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image015.png" alt="img"  />

    - getLeafList() 返回该树的 leafList 属性，来获取该树的叶子节点列表  forEachLeaf(Consumer&lt;HTNode>) 遍历每个叶子节点，通过传递函数实现遍历时的自定义操作

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image016.png" alt="img" style="width: 500px" />

### 3.5 HuffmanCode 类

HuffmanCode 使用到的哈夫曼树存储在  HuffmanTree 类中。使用 HashMap<K, V>  存储编码本，使用 List&lt;E> 表示哈夫曼树上的叶子节点列表。

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image017.png" alt="img"  />
以下是 UML 图中有关数据和方法的详细说明：

- 成员变量

    - codeMap 是编码本，Key 为 Byte 类型表示原字节数据，Value 为 String 类型表示新的编码

- 方法

    - getCodeMap() 返回 codeMap 属性，获取编码本  setCodeMap(HashMap<Byte, String>) 通过传递过来的参数设置编码本

    - getByteWeight(byte[]) 遍历数组，统计各个字节出现的次数计为权重值，写入权重字典

      UML 顺序图如下所示：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image018.png" alt="img" style="width: 600px" />

    - createCodeMap(List&lt;HTNode>) 根据叶子节点列表创建编码本，遍历叶子节点列表，讲节点的 value  属性作为 key，path 属性作为 value 存入 codeMap 中

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image019.png" alt="img" style="width: 600px" />

    - getCode(byte[]) 遍历原始文本的字节数组，按照编码本将其转换为二进制的字符串。接下来对该字符串重新编码：将该字符串的长度  + 7 再模 8 得到最小的字节数。以该字节数为大小创建一个字节数组，也是编码数组。同时遍历该字节数组和字符串片段（8 个字符为一个片段），字符串索引每遍历一次 + 8，编码数组索引每遍历一次  + 1。将字符串索引 + 8 判断该字节是否为最后一个字符串片段，如果是，则不做转换处理，直接放入编码本，key 为 null，value 为该字符串片段。如果不是最后一个，则获取本个字符串片段，然后将其转为字节存到编码数组中。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image020.png" alt="img"  />

    - code(byte[]) 传递字节数组进行编码，先计算字节的权重，再根据权重创建哈夫曼树，然后生成编码本，最后计算编码。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image021.png" alt="img" style="width: 600px" />

    - code(String) 对 code(bytes[]) 的重载，先将字符串转为字节数组，再调用 code(bytes[])

      UML 图如下所示：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image022.png" alt="img" style="width: 400px" />

    - getBinaryString(byte[]) 创建一个StringBuilder对象用于字符串拼接。将字节数组转为二进制字符串，遍历编码数组，将编码和  0x100 进行按位或运算得到长度为 9 位的字符串。根据编码数组的长度和当前索引判断当前编码是否为最后一个，如果是，则不需要转换，直接从编码本中将对应的字符串取出（key 为 null） 加入拼接字符串后面。如果不是最后一个编码，则取字符串的后  8 位加入拼接字符串后面。遍历完后返回拼接字符串。

      UML 顺序图如下所示：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image023.png" alt="img"  />

    - getContent(String) 将编码的二进制字符串转为原始文本。创建两个StringBuilder对象，一个用于字符串片段的拼接，一个用于拼接整体原始字符串。根据编码表生成解码表，具体做法就是将编码表的 key 转为解码表的 value，编码表的 value 转为解码表的 key。遍历编码二进制字符串，将当前位置的字符加入字符串片段的后面，比对解码本，以当前字符串片段为 key 获取 value。如果 value  不为空说明找到原始字节数据，将其转为字符串后加到整体字符串后面，重新创建一个字符串片段，使其为空继续遍历。如果 value 为空说明目前还没找到，继续遍历。最后循环结束后将 StringBuilder  对象转为 String 后返回。

      UML 顺序图如下所示：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image024.png" alt="img"  />

    - decode(byte[]) 将字节数组转为二进制字符串，然后再转为原始字符串返回

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image025.png" alt="img" style="width: 600px" />



### 3.6 HuffmanCodeTest 类

对 HuffmanCode  类的编码和解码功能进行测试，需要用到单元测试框架junit，这里只需要使用 @Test 注解和 Assertions 类的功能。

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image026.png" alt="img" style="width: 600px" />

以下是 UML 图中有关数据和方法的详细说明：

- 方法

    - testCodeAndDecode() 测试编码和解码的操作，将一个字符串进行编码再解码后，与原字符串进行比较，一样则测试用过，否则抛出异常。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image027.png" alt="img" style="width: 600px" />     

### 3.7 Main 类

使用 Scanner  类获取用户输入的选项。使用 HashMap<K, V> 类获取和设置编码本。在压缩和解压的时候，需要读写文件，FileInputStream类和FileOutputStream类可以用来读写文件，也可以用来创建  ObjectInputStream 对象和 ObjectOutputStream对象用来序列化和反序列化 Java 对象。使用FileWriter和 BufferedWriter可以在解压的时候将字符串写入文本文件。

UML 类图：

<img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image028.png" alt="img"  />
以下是 UML 图中有关数据和方法的详细说明：

- 成员变量

    - scanner 是Scanner 类的对象，用来获取用户输入的选项
    - menuItem 是一个字符串数组，存储整个菜单和所有选项，选项包括：1.帮助文档，2.压缩文件，3.解压文件，0.退出系统。

- 方法

    - main(String[])打印程序名，作者，时间等信息。显示程序菜单，根据用户的选项判断应该执行的操作，压缩、解压或退出等。重复上述操作，直到选项为  0。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image029.png" alt="img" style="width: 400px" />

    - menu() 显示菜单，获取用户输入的选项，判断选项是否合法，不合法则继续显示菜单，同时提示用户正确的输入。

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image030.png" alt="img" style="width: 600px" />  

    - help() 打印帮助文档  compress(String, String) 根据相关路径，读取文件然后重新编码后再写入文件，除了用到正常的FileInputStream和FileOutputStream外，还要使用ObjectOutputStream完成对字节数组和编码本的序列化，完成压缩

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image031.png" alt="img"  />

    - decompress(String, String) 根据相关路径，读取文件获取编码本，需要使用 ObjectInputStream完成字节数组和编码本对象的反序列化，对照编码还内容后再写入文件，完成解压

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image032.png" alt="img"  />

    - compress() 对compress(String, String) 方法的重载，获取用户输入的源文件路径和压缩后的路径，然后进行压缩

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image033.png" alt="img"  />

    - decompress() 对decompress(String, String) 方法的重载，获取用户输入的压缩文件路径和解压后的路径，然后进行解压

      UML 顺序图如下：

      <img src="https://cdn.jsdelivr.net/gh/james-wangx/typora-images/2023/06/clip_image034.png" alt="img"  />



## 四、代码实现

见源代码
