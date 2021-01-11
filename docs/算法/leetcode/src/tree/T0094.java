package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 11:34 2021-01-09
 */
public class T0094 {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        inorder(root,res);
        return res;
    }

    public void inorder(TreeNode root,List<Integer> res){
        if(root == null){
            return;
        }
        inorder(root.left,res);
        res.add(root.val);
        inorder(root.right,res);
    }
}
