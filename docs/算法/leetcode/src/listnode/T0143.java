package listnode;



import java.util.*;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 18:44 2020-11-08
 */
public class T0143 {
    public int fourSumCount(int[] A, int[] B, int[] C, int[] D) {
        //MAP存储AB两个数组的和 和出现的次数 然后遍历C+D
        //这样复杂度就是两个On方
        Map<Integer,Integer> sumAB = new HashMap<>();
        for(int i = 0;i < A.length;i++){
            for(int j = 0;j < B.length;j++){
                Integer sum = A[i]+B[j];
                if(sumAB.containsKey(sum)){
                    sumAB.put(sum,sumAB.get(sum)+1);
                }
                else{
                    sumAB.put(sum,1);
                }
            }
        }
        Integer res = 0;
        for(int i = 0;i<C.length;i++){
            for(int j=0;j<D.length;j++){
                Integer sum = C[i]+D[j];
                Integer need = 0 - sum;
                if(sumAB.containsKey(need)){
                    res = res + sumAB.get(need);
                }
            }
        }
        return res;
    }

}
