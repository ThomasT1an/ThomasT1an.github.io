package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 11:59 2021-01-09
 */
public class T0101 {
    public boolean isSymmetric(TreeNode root) {
        if(root == null){
            return true;
        }
        return isSame(root.left,root.right);
    }
    public boolean isSame(TreeNode p,TreeNode q){
        if(p == null || q == null){
            if(p == null && q == null){
                return true;
            }
            else{
                return false;
            }
        }
        if(p.val == q.val){
            return isSame(p.left,q.right) && isSame(p.right,q.left);
        }
        else {
            return false;
        }
    }
}
