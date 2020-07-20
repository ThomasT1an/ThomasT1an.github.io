# 动态规划

每个阶段只有一个	状态->递推；
每个阶段的最优状态都是由上一个阶段的最优状态得到的->贪心；
每个阶段的最优状态是由之前所有阶段的状态的组合得到的->搜索；
每个阶段的最优状态可以从之前某个阶段的某个或某些状态直接得到而不管之前这个状态是如何得到的->动态规划



考虑清楚 为了计算出某个状态 需要提前计算出哪些状态

初始条件：用转移方程算不出来但是又需要的值



## 面试题42 连续子数组最大和 /T53 最大自序和(e)

```java
/**
     * 1.dp定义：
     * dp[i]表示以nums[i]结尾的最大和子序列
     * 2.转移方程：
     * dp[i-1]<=0时 dp[i]=nums[i] 因为dp[i-1]<0时 对后续起副作用 一定比单独的nums[i]小
     * dp[i-1]>0时 dp[i]=dp[i-1]+nums[i]
     * 3.初始状态
     * dp[0]=nums[0]
     * 4.返回值
     * 返回整个dp数组中的最大值
     *
     * 优化 dp[i]只与dp[i-1]有关 所以只需要用2个变量存储
     */
    public static int maxSubArray(int[] nums) {
        int max=nums[0];
        int dp0=nums[0];
        int dp1=0;
        for(int i=1;i<nums.length;i++){
            if(dp0<=0)
                dp1=nums[i];
            else
                dp1=nums[i]+dp0;
            dp0=dp1;
            if(dp1>max)
                max=dp1;
        }
        return max;
    }
```

## 	T70 爬楼梯(e)

```java
/** dp定义：
 * dp[i]为上到第i层楼总共有几种方法
 * 转移方程：
 * dp[i]=dp[i-1]+dp[i-2]
 * 初始值：
 * dp[1]=1
 * dp[2]=2
 * 返回值：
 * dp[i]
 */
public int climbStairs(int n) {
    if(n==1)
        return 1;
    if(n==2)
        return 2;
    int dp1=1;
    int dp2=2;
    int dp3=0;
    for(int i=3;i<=n;i++){
        dp3=dp1+dp2;
        dp1=dp2;
        dp2=dp3;
    }
    return dp3;
}
```

## T5 最长回文子串的动态规划解法(m)

```java
dp定义：
dp[i][j] 表示 以s[i]开头到s[j]结尾的子串是否为回文串
转移方程：
若dp[i][j]是回文串 则去除他的两头的dp[i+1][j-1]也是回文串 且他的两头 s[i]==s[j]
每一个dp[i][j]状态需要他左下角的状态
边界情况：
[i+1][j-1]必须构成区间 即j-1-i-1>1 即 j-i>3
若j-i=3 如i=1 j=4 那么外层围成的区间就是[1][4] 需要看内层2和3是否相同  j-i=2时 围成的区间最大就是[1][3]
判断了头尾是否相同之后 只剩一个字符 肯定为true j-i<2就更不会用到了
初始值：
由于在j-i<3的情况下可以直接判断为true 所以不需要初始化
返回值：
每当一个dp[i][j]为true的时候 都记录下此时的i下标与长度 每次变更时取max

遍历（填表方向） 由于依赖左下角的值 所以要注意填表顺序 
for(int j=0;j<s.length;j++)
	for(int i=0;i<s.length;i++)



public static String longestPalindrome(String s) {
        if(s.length()<=1)
            return s;
        char[] chars=s.toCharArray();
        int len=chars.length;
        boolean[][] dp=new boolean[len][len];
        int maxLen=1;
        int maxI=0;
        for(int j=1;j<len;j++){
            for(int i=0;i<len;i++){
                if(chars[i]!=chars[j])
                    dp[i][j]=false;
                else{
                    if(j-i>=3)
                        dp[i][j]=dp[i+1][j-1];
                    else
                        dp[i][j]=true;
                }
                if(dp[i][j] && j-i+1>maxLen){
                    maxLen=j-i+1;
                    maxI=i;
                }
            }
        }
        return s.substring(maxI,maxI+maxLen);
    }
```



## T198.打家劫舍

```java
/*
    dp定义：
    dp[i]到第i家时的最大收益
    转移方程：
    对每一家只有两个选择 抢/不抢 抢这家 就去下下家 不抢这家 就去下家
    dp[i]=Max(dp[i-1],nums[i]+dp[i-2])
    返回值 dp[i]
    初始状态
    dp[0]=nums[0] 只有一家 抢
    dp[1]=max(nums[0],num[1]) 有两家 选大的那家
     */
    public static int rob(int[] nums) {
        int[] dp=new int[nums.length];
        if(nums.length==0)
            return 0;
        if(nums.length==1)
            return nums[0];
        if(nums.length==2)
            return Math.max(nums[0],nums[1]);
        dp[0]=nums[0];
        dp[1]=Math.max(nums[0],nums[1]);
        for(int i=2;i<nums.length;i++){
            dp[i]=Math.max(dp[i-1],nums[i]+dp[i-2]);
        }
        return dp[nums.length-1];
    }
```



## T303. 区域和检索 - 数组不可变  前缀和(e)



```java
class NumArray {
        private int[] sums;
        //sum[i...j] = sum[0...j] - sum[0...i-1]
        public NumArray(int[] nums) {
            sums=new int[nums.length+1];
            sums[0]=0;
            for(int i=0;i<nums.length;i++){
                sums[i+1]=nums[i]+sums[i];
            }
        }

        public int sumRange(int i, int j) {
            return sums[j+1]-sums[i];
        }
    }
```



## T62. 不同路径(m)

```java
 /*
    dp定义：
    dp[i][j]表示到达i j位置共有多少种方法
    转移方程:
    dp[i][j]=dp[i-1][j]+dp[i][j-1]
    填表顺序：
    对于每一个dp[i][j] 依赖他左和上的两个状态 从左到右-从上到下即可
    base case
    i为0 和j为0的时候考虑越界 即第一行和第一列 全部初始化为1 因为只能向下走/向右走
     */

    public static int uniquePaths(int m, int n) {
        int[][] dp=new int[m][n];
        for(int i=0;i<m;i++)
            dp[i][0]=1;
        for(int i=0;i<n;i++)
            dp[0][i]=1;
        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++)
                dp[i][j]=dp[i-1][j]+dp[i][j-1];
        }
        return dp[m-1][n-1];
    }
```

## T63. 不同路径 II(m)

```java
   /*
    dp定义：
    dp[i][j]表示到达i j位置共有多少种方法
    转移方程:
    dp[i][j]=dp[i-1][j]+dp[i][j-1]
    若遇到障碍 则当前dp[i][j]为0 这样可以保证对后续不起到影响
    填表顺序：
    对于每一个dp[i][j] 依赖他左和上的两个状态 从左到右-从上到下即可
    base case
    i为0 和j为0的时候考虑越界 即第一行和第一列 在不碰到障碍物的情况下初始化为1 否则为0 因为只能向下走/向右走
     */

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m=obstacleGrid.length;
        int n=obstacleGrid[0].length;
        int[][] dp=new int[m][n];
        for(int i=0;i<m;i++){
            if(obstacleGrid[i][0]!=1)
                dp[i][0]=1;
            else
                break;
        }
        for(int i=0;i<n;i++){
            if(obstacleGrid[0][i]!=1)
                dp[0][i]=1;
            else
                break;
        }
        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++){
                if(obstacleGrid[i][j]!=1)
                    dp[i][j]=dp[i-1][j]+dp[i][j-1];
            }
        }
        return dp[m-1][n-1];
    }
```

## T64. 最小路径和(m)

```java
/*
    dp定义：
    dp[i][j]表示到达i j位置时可能拿到的最小路径和
    转移方程
    dp[i][j]=min(dp[i-1][j],dp[i][j-1])+grid[i][j]
    填表顺序：
    从左到右-从上到下
    base case
    第一行和第一列 初始化为累加值
     */

    public int minPathSum(int[][] grid) {
        int m=grid.length;
        int n=grid[0].length;
        int[][] dp=new int[m][n];
        for(int i=0,sum=0;i<n;i++){
            sum+=grid[0][i];
            dp[0][i]=sum;
        }
        for(int i=0,sum=0;i<m;i++){
            sum+=grid[i][0];
            dp[i][0]=sum;
        }
        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++){
                dp[i][j]=Math.min(dp[i-1][j],dp[i][j-1])+grid[i][j];
            }
        }
        return dp[m-1][n-1];
    }
```

## T91. 解码方法(m)

```java
/*
    dp定义：
    dp[i]表示解析到第i个数字的时候共有多少种解法
    转移方程：
    对于遍历到的某一个数字i
    若他!=0 则表示这个数字可以单独解码
    dp[i]+=dp[i-1]
    再看他前面的那个数字 若这两个数字组合<=26且>=10 说明可以和上一个数字组合解码
    dp[i]+=dp[i-2]
    base case
    dp[0] dp[1]
     */

    public static int numDecodings(String s) {
        if(s.length()==0)
            return 0;
        char[] nums=s.toCharArray();
        int[] dp=new int[nums.length];
        if(nums[0] == '0')
            return 0;
        dp[0]= nums[0] =='0' ? 0:1;
        if(s.length()==1)
            return dp[0];
        if(nums[1] != '0')
            dp[1]+=dp[0];
        int num=getIntNum(nums[0],nums[1]);
        if(num>=10 && num<=26)
            dp[1]+=1;
        if(s.length()==2)
            return dp[1];
        for(int i=2;i<s.length();i++){
            if(nums[i]!='0')
                dp[i]+=dp[i-1];
            num=getIntNum(nums[i-1],nums[i]);
            if(num>=10 && num<=26)
                dp[i]+=dp[i-2];
        }
        return dp[s.length()-1];
    }

    public static int getIntNum(char c1,char c2){
        String s=""+c1+c2;
        int res=Integer.valueOf(s);
        return res;
    }
```

## T120. 三角形最小路径和(m)

```java
/*
    dp定义
    dp[i][j]表示到达第i层第j个数字的最小路径和
    转移方程：
    dp[i][j]=min(dp[i-1][j],dp[i-1][j-1])+triangle[i][j]
    注：每一层的第一个数字只能从前者取 每一层的最后一个数字只能从后者取
    填表顺序：
    从上到下 从左到右
    base case
    第1层的1个数字
    返回值 最下层中最小的数字
     */

    public static int minimumTotal(List<List<Integer>> triangle) {
        List<List<Integer>> dp=new ArrayList<>();
        List<Integer> dp0=new ArrayList<Integer>(Arrays.asList(triangle.get(0).get(0)));
        if(triangle.size()==1)
            return dp0.get(0);
        dp.add(dp0);
        for(int i=1;i<triangle.size();i++){
            List<Integer> floorN=new ArrayList<>();
            for(int j=0;j<triangle.get(i).size();j++){
                int up1;
                int up2;
                if(j==triangle.get(i).size()-1)
                    up1=dp.get(i-1).get(j-1);
                else
                    up1=dp.get(i-1).get(j);
                if(j==0)
                    up2=dp.get(i-1).get(j);
                else
                    up2=dp.get(i-1).get(j-1);
                int res=Math.min(up1,up2)+triangle.get(i).get(j);
                floorN.add(res);
            }
            dp.add(floorN);
        }
        int minRes=Integer.MAX_VALUE;
        for(int i=0;i<dp.get(dp.size()-1).size();i++){
            if(dp.get(dp.size()-1).get(i)<minRes)
                minRes=dp.get(dp.size()-1).get(i);
        }
        return minRes;
    }
```

## T139. 单词拆分(m)

```java
  /*
    dp定义：
    boolean型dp数组 dp[i]表示以第i个字符结尾的字符串是否满足要求
    转移方程：
    对于遍历到的每一个dp[i]
    再次遍历 将他拆分为两个子串
    看前者子串的dp是否为true 若为true 看后者子串是否在字典中 若都在 则dp[i]为true
    只要有任何一个情况能够满足 则可以跳出内层循环
    base case：
    dp[0]=0 因为空串满足情况
     */

    public static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet=new HashSet<>();
        wordSet.addAll(wordDict);
        boolean[] dp=new boolean[s.length()+1];
        dp[0]=true;
        for(int i=1;i<=s.length();i++){
            for(int j=0;j<i;j++){
                if(dp[j] && wordSet.contains(s.substring(j,i))){
                    dp[i]=true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }
```

## T312. 戳气球(h)

```Java
 /**
     * 边界处理：
     * 对数组最左和最右都填充一个1
     * 暴力解法：
     * 对每一个气球i，如果他是最后被戳破的那一个，那么最终得分就是max[0...i-1] + 打爆他的得分 + max[i+1 .....nums.length-1]
     * 其中，max的定义为：在这个区间中，不管以何种顺序戳破气球，能够得到的最大的分数
     * 这个max函数用同样的思想，尝试让区间中的每一个气球，成为最后被戳破的气球，从而统计出可能达到的最大值
     * max函数的终止条件为，某一个气球左右都没有存活的气球了，此时分数就是max[左边]+max[右边]+打爆他所得到的分数
     * 对于常规情况（最后打爆的是区间内的某个气球），分数是上面这一行的计算方式
     * 对于函数max[L,R]来说，有两种特殊情况需要注意，即最后打爆的是区间最左边的气球或最右边的气球
     * 若最后打爆的是最左边的气球，那么分数就少一个他左边的区间得分
     * 右边同理
     * 对于 "打爆某个气球的得分"的计算，由于被打爆气球的左边/右边可能有一些气球被打爆了 所以要找到离他最近的没有被打爆的气球进行相乘
     * 对于max[L,R]中的i(L<=i<=R)来说 i是最后被打爆的 那么他左边的气球是nums[L-1] 右边的气球是nums[R+1]
     *
     * 备忘录法优化：
     * 假如有长度为10的nums，在假定3是最后被打爆的气球时，需要计算max[4,10]的值
     * 假如2是最后被打爆的气球时，需要计算max[3,10]的值，计算过程中，假如3是[3,10]中最后被打爆的值，又需要计算max[4,10]造成重复计算
     * 用一个二维数组记录已经计算的所有值即可完成备忘录法
     */
```

暴力解法：

```Java
public static void main(String[] args) {
    int[] nums=new int[]{3,1,5,8};
    System.out.print(maxCoins(nums));
}

public static int maxCoins(int[] nums) {
    if(nums == null || nums.length == 0)
        return 0;
    if(nums.length == 1)
        return nums[0];
    int arrs[] = new int[nums.length+2];
    arrs[0]=1;
    arrs[nums.length+1]=1;
    for(int i=1;i<arrs.length-1;i++)
        arrs[i]=nums[i-1];
    return max(arrs,1,nums.length);
}

private static int max(int[]arrs,int L,int R){
    //如果边界重合了 说明范围内只有一个气球了 直接打爆
    if(L==R){
        return boom(arrs,L-1,L,R+1);
    }
    //两种特殊情况 最后打爆的是区间最左或是最右的气球
    int max=Math.max(boom(arrs,L-1,L,R+1)+max(arrs,L+1,R),
                     boom(arrs,L-1,R,R+1)+max(arrs,L,R-1));
    //其他情况 尝试区间中间的每一个气球是最后被打爆的气球
    for(int i=L+1;i<R;i++){
        max=Math.max(max,boom(arrs,L-1,i,R+1)+max(arrs,L,i-1)+max(arrs,i+1,R));
    }
    return max;
}

private static int boom(int arrs[],int i,int L,int R){
    return arrs[L] * arrs[i] * arrs[R];
}
```

备忘录法：

```Java
* 备忘录法优化：
     * 假如有长度为10的nums，在假定3是最后被打爆的气球时，需要计算max[4,10]的值
     * 假如2是最后被打爆的气球时，需要计算max[3,10]的值，计算过程中，假如3是[3,10]中最后被打爆的值，又需要计算max[4,10]造成重复计算
     * 用一个二维数组记录已经计算的所有值即可完成备忘录法
     * 使用备忘录法必须考虑填表顺序，根据某个位置的依赖关系得出：
     * 本题填写顺序为从左往右，从下往上
```

```Java
 public static int maxCoins(int[] nums) {
        if(nums == null || nums.length == 0)
            return 0;
        if(nums.length == 1)
            return nums[0];
        int arrs[] = new int[nums.length+2];
        arrs[0]=1;
        arrs[nums.length+1]=1;
        for(int i=1;i<arrs.length-1;i++)
            arrs[i]=nums[i-1];
        int[][] dp=new int[arrs.length][arrs.length];
        //先填写basecase
        for(int i=1;i<=nums.length;i++){
            dp[i][i]=arrs[i-1]*arrs[i]*arrs[i+1];
        }
        //按顺序填表
        for(int L=nums.length;L>=1;L--){
            for(int R=L+1;R<=nums.length;R++){
                dp[L][R]=Math.max(boom(arrs,L-1,L,R+1)+dp[L+1][R],
                        boom(arrs,L-1,R,R+1)+dp[L][R-1]);
                for(int i=L+1;i<R;i++){
                    dp[L][R]=Math.max(dp[L][R],boom(arrs,L-1,i,R+1)+dp[L][i-1]+dp[i+1][R]);
                }
            }
        }
        return dp[1][nums.length];
    }
```



# 滑动窗口最大值 双端队列

LinkedList就是一个双端队列，底层是用双向联表实现的

维护一个窗口大小的双端队列，对每一次窗口的滑动均有以下规则：

1.每一次滑动均会涉及新的数据，队列中的值均从大到小排序（不能相等），若新加入的数大于之前的数，则将之前的数全部丢弃，直到能够满足从大到小排序，再将新数据从右端入队

2.每一次滑动均会有一个数据过期，由于加入数据到双端队列是从右侧加入，故左侧数据最易过期，判断左侧数据是否过期，若过期，则从左侧剔除这一数据



在保证这两条规则的情况下，每一个滑动窗口的最大值，均是双端队列最左侧的值，原因有如下：

1.最左侧数据是最大的

2.最左侧数据并未过期



在双端队列中存储数据的下标，可以更好的判断是否过期，若不这么做而按照数字比对来判断过期，则无法判断重复的数字



```java
class Solution {
   LinkedList<Integer> list=new LinkedList<Integer>();
    public  int[] maxSlidingWindow(int[] nums, int k) {
        if(nums.length==0)
            return new int[0];
        int[] res=new int[nums.length-k+1];
        int resindex=0;
        //先达到窗口大小，这段时间只加入队列而不用判断是否过期
        for(int j=0;j<k;j++)
            addlist(list,nums,j);
        res[resindex]=nums[list.peekFirst()];
        for(int i=k;i<nums.length;i++){
            addlist(list,nums,i);
            outlist(list,i-k);
            res[++resindex]=nums[list.peekFirst()];
        }
        return res;
    }

    //加入数据
    private  void addlist(LinkedList list,int[]nums,int index){
        if(list.isEmpty())
            list.addLast(index);
        else{
            while(nums[index]>=nums[(int)list.peekLast()]){
                list.pollLast();
                if(list.isEmpty())
                    break;
            }
            list.addLast(index);
        }

    }
    //判断数据是否过期，若过期则出队
    private  void outlist(LinkedList list,int index){
        while(index>=(int)list.peekFirst())
            list.pollFirst();
    }
}
```





# 单调栈结构相关

单调栈（monotone-stack）是指栈内元素（栈底到栈顶）都是（严格）单调递增或者单调递减的。

如果有新的元素入栈，栈调整过程中 *会将所有破坏单调性的栈顶元素出栈，并且出栈的元素不会再次入栈* 。由于每个元素只有一次入栈和出栈的操作，所以 *单调栈的维护时间复杂度是O(n)* 。

单调栈性质：
\1. 单调栈里的元素具有单调性。
\2. 递增（减）栈中可以找到元素左右两侧比自身小（大）的第一个元素。

我们主要使用第二条性质，该性质主要体现在栈调整过程中，下面以递增栈为例（假设所有元素都是唯一），当新元素入栈。
\+ 对于出栈元素来说：找到右侧第一个比自身小的元素。
\+ 对于新元素来说：等待所有破坏递增顺序的元素出栈后，找到左侧第一个比自身小的元素。



核心代码(入栈)：

```java
void monotoneStack(int[] nums){
        for(int i=0;i<nums.length;i++){
            if(stack.isEmpty())
                stack.push(i);//压入的是下标
            else{
                //此处控制递增/递减/严格递增/严格递减
                while(nums[i]<=nums[stack.peek()]){
                    stack.pop();
                    if(stack.isEmpty())
                        break;
                }
                stack.push(i);
            }
        }
  //此处栈不为空的话 还要再做剩余元素的出栈操作
     }
```

每个元素出栈的时候，可以得到举例这个元素左右两侧的比自身大(小)的第一个元素

通常在出栈的时候进行题目逻辑的处理

https://leetcode-cn.com/problems/largest-rectangle-in-histogram/solution/bao-li-jie-fa-zhan-by-liweiwei1419/

