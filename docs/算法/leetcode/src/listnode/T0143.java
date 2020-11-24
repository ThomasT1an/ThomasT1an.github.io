package listnode;



import java.util.*;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 18:44 2020-11-08
 */
public class T0143 {
    public ListNode sortList(ListNode head) {
        List<ListNode> nodes = new ArrayList<>();
        while(head != null){
            nodes.add(head);
            head = head.next;
        }
        Collections.sort(nodes,(n1,n2) -> n1.val - n2.val);
        ListNode preHead = new ListNode(-1);
        ListNode cur =preHead;
        for(int i=0;i<nodes.size();i++){
            cur.next = nodes.get(i);
            cur = cur.next;
        }
        return preHead.next;

    }

}
