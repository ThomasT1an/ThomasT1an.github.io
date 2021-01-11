package listnode;

import java.util.HashMap;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 12:14 2021-01-08
 */
public class LRUCache {

    private HashMap<Integer,Node> map;
    private DoubleList list;
    private int cap;
    public LRUCache(int capacity) {
        this.cap = capacity;
        map = new HashMap<>();
        list = new DoubleList();
    }

    public int get(int key) {
        if(!map.containsKey(key)){
            return -1;
        }
        else{
            Node node = map.get(key);
            list.addHead(list.del(node));
            return node.val;
        }
    }

    public void put(int key, int value) {
        if(map.containsKey(key)){
            Node node = map.get(key);
            node.val = value;
            list.addHead(list.del(node));
            map.put(key,node);
        }
        else {
            if(list.getSize() == cap){
                Node node = list.delTail();
                if(node != null){
                    map.remove(node.key);
                }
            }
            Node node = new Node(key,value);
            list.addHead(node);
            map.put(key,node);
        }
    }



    class Node {
        public int key;
        public int val;
        public Node next;
        public Node pre;
        public Node(int key,int val){
            this.key = key;
            this.val = val;
        }
    }

    class DoubleList{
        private Node head;
        private Node tail;
        private int size;
        public DoubleList(){
            head = new Node(0,0);
            tail = new Node(0,0);
            head.pre = tail;
            tail.next = head;
            size = 0;
        }

        //头插
        public void addHead(Node tmp){
            Node preNode = head.pre;
            preNode.next = tmp;
            tmp.pre = preNode;
            tmp.next = head;
            head.pre = tmp;
            size++;
        }
        //尾删
        public Node delTail(){
            if(tail.next == head){
                return null;
            }
            Node nextNode = tail.next;
            return del(nextNode);
        }
        //任意位置删除并返回
        public Node del(Node t){
            t.pre.next = t.next;
            t.next.pre =t.pre;
            t.next = null;
            t.pre = null;
            size --;
            return t;
        }

        public int getSize(){
            return size;
        }
    }

    public static void main(String[] args) {
        LRUCache lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3
        lRUCache.get(4);    // 返回 4

    }
}

