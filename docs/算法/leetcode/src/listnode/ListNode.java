package listnode;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 17:31 2020-11-08
 */
public class ListNode {
    int val;
    ListNode next;
    ListNode(){}
    ListNode(int val){this.val = val;}
    ListNode(int val,ListNode next){
        this.val = val;
        this.next = next;
    }
}
