/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 14:56 2021-02-08
 */
public class T226 {
    public int diameterOfBinaryTree(TreeNode root) {
        /**
         * 对每一节点 求左子树最深度+右子树最深度 即可
         */
    }

    public int maxDeep(TreeNode node){
        if(node == null){
            return 0;
        }
        return Math.max(maxDeep(node.left)+1,maxDeep(node.right)+1);
    }
}
