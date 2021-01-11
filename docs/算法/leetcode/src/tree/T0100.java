package tree;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 11:55 2021-01-09
 */
public class T0100 {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        return isSame(p,q);
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
            return isSame(p.left,q.left) && isSame(p.right,q.right);
        }
        else {
            return false;
        }
    }
}
