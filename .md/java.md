## HashMap
    -键不能重复，相同会覆盖
    -允许null键、null值
    -打印输出时，和put()顺序不一致
## 多线程条件下，会有什么问题
    -put()，可能会元素丢失
    -get()和put()同时进行，有可能会get到null
    -有可能会导致死循环，cpu占用100%
## 为什么选择红黑树
    相对于avl(平衡二叉树)，红黑树旋转的次数比较小(不强制左边右边子树的高度差小于1),利于插入、删除
## hash冲突
    HashMap选择冲突的直接形成链表，超出一定长度，形成红黑树
## ConcurrentHashMap
    1.8之后
    -数据结构。
        Node[]数组 + 链表 + 红黑树
    -线程安全。
        初始化Node[]采用CAS + volatile
        放数据使用synchronized
## ThreadLocal
    -内存泄漏。如果使用的线程池，当任务结束，但是线程并未销毁(线程池)。那么threadlocals并不会被GC。需要手动thread.remove()
    
