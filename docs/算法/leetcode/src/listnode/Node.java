package listnode;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 12:16 2021-01-08
 */
public class Node {
    public int key;
    public int val;
    public Node next;
    public Node pre;
    public Node(int key,int val){
        this.key = key;
        this.val = val;
    }
}
