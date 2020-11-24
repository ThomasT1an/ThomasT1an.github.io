package listnode;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 17:33 2020-11-08
 */
public class T0002 {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode res = new ListNode();
        ListNode cur = res;
        int carry = 0;
        while(l1 != null && l2!=null){
            int num1 = l1 == null ? 0 : l1.val;
            int num2 = l2 == null ? 0 : l2.val;
            int sum = num1 + num2 + carry;
            if(sum >= 10){
                carry = 1;
            }
            else{
                carry = 0;
            }
            sum = sum % 10;
            cur.next = new ListNode(sum);
            cur = cur.next;
            l1 = l1.next;
            l2 = l2.next;
        }
        if(l1 == null){
            while(l2 != null){
                int sum = l2.val + carry;
                if(sum >= 10){
                    sum = sum %10;
                    carry = 1;
                }
                else{
                    carry = 0;
                }
                cur.next = new ListNode(sum);
                l2 = l2.next;
                cur = cur.next;
            }
        }
        if(l2== null){
            while(l1 != null){
                int sum = l1.val + carry;
                if(sum >= 10){
                    sum = sum %10;
                    carry = 1;
                }
                else{
                    carry = 0;
                }
                cur.next = new ListNode(sum);
                l1 = l1.next;
                cur = cur.next;
            }
        }
        if(carry != 0){
            cur.next = new ListNode(carry);
        }
        return res.next;
    }
}
