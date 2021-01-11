package tree;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 11:49 2021-01-09
 */
public class T0104 {
    public int maxDepth(TreeNode root) {
        int deep = 0;
        return getMax(root,deep);
    }

    public int getMax(TreeNode node,int deep){
        if(node == null){
            return deep;
        }
        return Math.max(getMax(node.left,deep+1),getMax(node.right,deep+1));
    }
}
