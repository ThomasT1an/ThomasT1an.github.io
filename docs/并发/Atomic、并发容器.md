# 7.JUC包中原子操作源码解析

JUC包提供了一系列原子性操作类

均以Long类型举例，实现原理类似

## 7.1 原子变量操作类

AtomicLong

```Java
public class AtomicLong extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 1927816293512124184L;

    // setup to use Unsafe.compareAndSwapLong for updates
  //1.获取Unsafe实例
    private static final Unsafe unsafe = Unsafe.getUnsafe();
  //2.存储value变量的偏移量
    private static final long valueOffset;

    /**
     * Records whether the underlying JVM supports lockless
     * compareAndSwap for longs. While the Unsafe.compareAndSwapLong
     * method works in either case, some constructions should be
     * handled at Java level to avoid locking user-visible locks.
     */
  //3.判断JVM是否支持Long类型的无锁CAS
    static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();

    /**
     * Returns whether underlying JVM supports lockless CompareAndSet
     * for longs. Called only once and cached in VM_SUPPORTS_LONG_CAS.
     */
    private static native boolean VMSupportsCS8();

    static {
        try {
          //4.利用unsafe获取value在自身类中的偏移量
            valueOffset = unsafe.objectFieldOffset
                (AtomicLong.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }
	//5.存放实际变量值 被声明为volatile 在多线程下保证可见性
    private volatile long value;

    /**
     * Creates a new AtomicLong with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicLong(long initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicLong with initial value {@code 0}.
     */
    public AtomicLong() {
    }
```

主要函数：

```Java
public final long getAndIncrement() {
    return unsafe.getAndAddLong(this, valueOffset, 1L);
}

/**
 * Atomically decrements by one the current value.
 *
 * @return the previous value
 */
public final long getAndDecrement() {
    return unsafe.getAndAddLong(this, valueOffset, -1L);
}

/**
 * Atomically adds the given value to the current value.
 *
 * @param delta the value to add
 * @return the previous value
 */
public final long getAndAdd(long delta) {
    return unsafe.getAndAddLong(this, valueOffset, delta);
}
```

等 主要都是借助unsafe的getAndAddLong来实现的 区别不大

```Java
public final long getAndAddLong(Object var1, long var2, long var4) {
    long var6;
    do {
        var6 = this.getLongVolatile(var1, var2);
    } while(!this.compareAndSwapLong(var1, var2, var6, var6 + var4));

    return var6;
}
```

compareAndSwapLong的4个入参分别为obj,offest,expect,update

若obj对象在offest偏移量上的值与预期值expect相同，则更新为update，并返回true，否则不修改并返回false

这里首先用getLongVolatile方法 获取了var1对象在var2偏移量上的变量值，然后下一步用cas方法进行尝试

```Java
public final boolean compareAndSet(long expect, long update) {
    return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
}
```

同样是调用unsafe的CAS实现的CAS操作

```Java
public final void set(long newValue) {
    value = newValue;
}

/**
 * Eventually sets to the given value.
 *
 * @param newValue the new value
 * @since 1.6
 */
public final void lazySet(long newValue) {
    unsafe.putOrderedLong(this, valueOffset, newValue);
}
```

两个set方法 后者不保证其他线程能立刻察觉，但是性能上有优势

测试 lazySet确实快：

```Java
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 19:52 2020-07-07
 */
public class AtomicTest {
    static AtomicLong atomicLong = new AtomicLong();
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new Player().start();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);
    }
    private static class Player extends Thread {
        @Override
        public void run() {
            for(int i=0;i<1000000;i++)
                //或者改成set(i)
                atomicLong.lazySet(i);
        }
    }
}
```

JDK 8 新增的几个方法：getAndUpdate、updateAndGet、getAndAccumulate、accumulateAndGet

```Java
//将旧值传给updateFunction得到新值，然后比较更新，如失败则重复循环，直至成功返回旧值
public final int getAndUpdate(IntUnaryOperator updateFunction) {
    int prev, next;
    do {
        prev = get();
        next = updateFunction.applyAsInt(prev);
    } while (!compareAndSet(prev, next));
    return prev;
}
//将旧值传给updateFunction得到新值，然后比较更新，如失败则重复循环，直至成功返回新值
public final int updateAndGet(IntUnaryOperator updateFunction) {
    int prev, next;
    do {
        prev = get();
        next = updateFunction.applyAsInt(prev);
    } while (!compareAndSet(prev, next));
    return next;
}
//将旧值和x传给accumulatorFunction得到新值，然后比较更新，如失败则重复循环，直至成功返回旧值
public final int getAndAccumulate(int x,
                                  IntBinaryOperator accumulatorFunction) {
    int prev, next;
    do {
        prev = get();
        next = accumulatorFunction.applyAsInt(prev, x);
    } while (!compareAndSet(prev, next));
    return prev;
}
//将旧值和x传给accumulatorFunction得到新值，然后比较更新，如失败则重复循环，直至成功返回新值
public final int accumulateAndGet(int x,
                                  IntBinaryOperator accumulatorFunction) {
    int prev, next;
    do {
        prev = get();
        next = accumulatorFunction.applyAsInt(prev, x);
    } while (!compareAndSet(prev, next));
    return next;
}
```

## 7.2 特殊说明：AtomicReference

在解决ABA问题时经常出现的一个类，与其他Atomic类相似，但是比较的是引用

## 7.3 JDK 8 新增的原子操作类LongAdder

简单介绍：

Atomic原子类通过CAS提供了非阻塞的原子性操作，他的性能相比使用阻塞算法的同步器来说已经有了一定提升。

使用Atomic原子类时，主要的性能瓶颈在于，在高并发时，大量线程会去竞争同一个原子变量，但是同时只有一个线程的CAS操作可以成功，这就导致了大量竞争失败的线程，在while中一直自旋尝试，从而浪费CPU资源

JDK8新增了一个原子性递增/递减类LongAdder用来克服上述Atomic原子类在高并发下的缺点，他的核心思想是：把多个线程竞争的同一个变量，分解为多个变量，让这些线程去竞争多个资源

LongAdder内部维护一个Cell数组，每个Cell里面有一个初始值为0的long型变量，不同的线程会竞争Cell数组中的不同Cell，而且如果某个线程竞争失败，他并不会在当前Cell上一直自旋，而是会去尝试其他Cell，最后，在获取LongAdder当前值时，是把所有Cell变量的value值累加后返回的。



代码分析：

```Java
public class LongAdder extends Striped64 implements Serializable {
```

LongAdder继承了Striped64类

```Java
//1.
@sun.misc.Contended static final class Cell {
  //2.
    volatile long value;
    Cell(long x) { value = x; }
    final boolean cas(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
    }

    // Unsafe mechanics
  //3.
    private static final sun.misc.Unsafe UNSAFE;
    private static final long valueOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> ak = Cell.class;
            valueOffset = UNSAFE.objectFieldOffset
                (ak.getDeclaredField("value"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
//4.
/** Number of CPUS, to place bound on table size */
static final int NCPU = Runtime.getRuntime().availableProcessors();

/**
 * Table of cells. When non-null, size is a power of 2.
 */
//下面这三个变量都为5.
transient volatile Cell[] cells;

/**
 * Base value, used mainly when there is no contention, but also as
 * a fallback during table initialization races. Updated via CAS.
 */
transient volatile long base;

/**
 * Spinlock (locked via CAS) used when resizing and/or creating Cells.
 */
transient volatile int cellsBusy;
```

1.Contended注解 避免伪共享

2.内部维护一个volatile的变量value

3.unsafe拿到value的偏移量，用于cas

4.获得CPU数量，这个数量将作为cells数组的大小上线，因为最多同时存在CPU数量个线程竞争资源

5.cells数组就是用来分摊竞争压力的数组，base字段是当竞争未产生时直接在此累加的变量，cellBusy用来实现自旋锁，状态只有0和1，当创建Cell元素，扩容Cell数组、或者初始化Cell数组时，使用CAS操作该变量来保证同时只有一个线程可以进行其中之一的操作。



**long sum()**

```Java
public long sum() {
    Cell[] as = cells; Cell a;
    long sum = base;
    if (as != null) {
        for (int i = 0; i < as.length; ++i) {
            if ((a = as[i]) != null)
                sum += a.value;
        }
    }
    return sum;
}
```

返回当前的值，内部操作是累加cell数组内部所有cell元素的value值再加上base

由于计算的时候没有对Cell数组加锁，所以在累加过程中可能有其他线程进行了修改或者扩容

所以这个函数的值并不非常精确，并不能拿到调用这个方法时的实际快照值

**void reset()**

```Java
public void reset() {
    Cell[] as = cells; Cell a;
    base = 0L;
    if (as != null) {
        for (int i = 0; i < as.length; ++i) {
            if ((a = as[i]) != null)
                a.value = 0L;
        }
    }
}
```

重置操作，把base置为0，若cell数组有元素，则元素置为0



主要看add方法

```Java
public void add(long x) {
    Cell[] as; long b, v; int m; Cell a;
  //caseBase处为1
    if ((as = cells) != null || !casBase(b = base, b + x)) {
        boolean uncontended = true;
        if (as == null || (m = as.length - 1) < 0 ||
            //getProbe处为2
            (a = as[getProbe() & m]) == null ||
            //3
            !(uncontended = a.cas(v = a.value, v + x)))
          //4
            longAccumulate(x, null, uncontended);
    }
}
```

1处caseBase：

```Java
final boolean casBase(long cmp, long val) {
    return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
}
```

在cell为空的时候在基础变量base上进行累加，这个时候就和Atomic原子类操作一样

如果不为空则会执行下面的代码：

2处getProbe：

```Java
static final int getProbe() {
    return UNSAFE.getInt(Thread.currentThread(), PROBE);
}
```

当前线程一个访问cells数组中的哪一个cell是通过getProbe()&m计算的，其中m是当前cells数组元素个数-1，getProbe()获取了当前线程中变量threadLocalRandomProbe的值，他是线程独立的，在代码4中被初始化



3处是对2中获取到应该访问的那个cells数组中的cell元素的操作，使用cas的方法

```Java
final boolean cas(long cmp, long val) {
    return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
}
```

将值累加进去



4.处是在当前线程映射的元素不存在 或者存在但是CAS操作失败的时候执行的，他用来初始化和扩容cells数组

看这段代码：

```Java
final void longAccumulate(long x, LongBinaryOperator fn,
                          boolean wasUncontended) {
    int h;
  //1.若probe为0（默认值）则调用ThreadLocalRandom.current();进行初始化probe
    if ((h = getProbe()) == 0) {
        ThreadLocalRandom.current(); // force initialization
        h = getProbe();
        wasUncontended = true;
    }
    boolean collide = false;                // True if last slot nonempty
    for (;;) {
        Cell[] as; Cell a; int n; long v;
      //2
        if ((as = cells) != null && (n = as.length) > 0) {
          //3
            if ((a = as[(n - 1) & h]) == null) {
                if (cellsBusy == 0) {       // Try to attach new Cell
                    Cell r = new Cell(x);   // Optimistically create
                    if (cellsBusy == 0 && casCellsBusy()) {
                        boolean created = false;
                        try {               // Recheck under lock
                            Cell[] rs; int m, j;
                            if ((rs = cells) != null &&
                                (m = rs.length) > 0 &&
                                rs[j = (m - 1) & h] == null) {
                                rs[j] = r;
                                created = true;
                            }
                        } finally {
                            cellsBusy = 0;
                        }
                        if (created)
                            break;
                        continue;           // Slot is now non-empty
                    }
                }
                collide = false;
            }
            else if (!wasUncontended)       // CAS already known to fail
                wasUncontended = true;      // Continue after rehash
          //4 当前Cell存在 则执行CAS 
          else if (a.cas(v = a.value, ((fn == null) ? v + x :
                                         fn.applyAsLong(v, x))))
                break;
          //5 当前Cell数组元素大于CPU个数  
          else if (n >= NCPU || cells != as)
                collide = false;            // At max size or stale
          //6 是否有冲突 
          else if (!collide)
                collide = true;
          //7 如果当前元素没有达到CPU个数 并且有冲突则扩容 calCellsBusy()用CAS操作把cellBusy设为1
          else if (cellsBusy == 0 && casCellsBusy()) {
                try {
                    if (cells == as) {      // Expand table unless stale
                      //7.1 
                      Cell[] rs = new Cell[n << 1];
                        for (int i = 0; i < n; ++i)
                            rs[i] = as[i];
                        cells = rs;
                    }
                } finally {
                  //7.2 扩容完成后 把cellsBusy设回0 下面初始化也是这个流程
                    cellsBusy = 0;
                }
            //7.3 重置冲突标记
                collide = false;
                continue;                   // Retry with expanded table
            }
          //7.4 重新计算hash值，用xorshift算法
            h = advanceProbe(h);
        }
      //8 初始化Cell数组
        else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
            boolean init = false;
            try {                           // Initialize table
                if (cells == as) {
                  //8.1 初始化大小为2
                    Cell[] rs = new Cell[2];
                  //8.2
                    rs[h & 1] = new Cell(x);
                    cells = rs;
                    init = true;
                }
            } finally {
                cellsBusy = 0;
            }
            if (init)
                break;
        }
        else if (casBase(v = base, ((fn == null) ? v + x :
                                    fn.applyAsLong(v, x))))
            break;                          // Fall back on using base
    }
}
```

 cellsBusy是一个标识符，为0说明当前cells数组没有在被初始化/扩容/新建元素

用CAS来占用标记，结束操作后还原

8.1中说明cells初始化大小为2，，然后使用h&1计算当前线程应该访问cell数组的哪个位置，也就是当前线程的threadLocalRandomProbe & cell元素个数-1



7中是cell数组扩容的逻辑，要进入7需要满足两个条件，当前cell元素个数小于当前机器CPU个数并且当前多个线程访问了cells数组中的同一个cell元素，从而导致某一个CAS操作失败

扩容后新元素的值是null，只有使用到的时候才会初始化

整个Cell数组都是遵循延迟加载策略的，这是由于Cells占用的内存是相对比较大的

7.4对CAS失败的线程重新计算当前线程的随机值threadLocalRandomProbe，以减少下次访问cells元素时的冲突机会



## 7.4 LongAccumulator类

LongAdder是一个特殊的LongAccumulator

```Java
public LongAccumulator(LongBinaryOperator accumulatorFunction,
                       long identity) {
    this.function = accumulatorFunction;
    base = this.identity = identity;
}
```

传入的LongBinaryOrerator：

```Java
@FunctionalInterface
public interface LongBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left the first operand
     * @param right the second operand
     * @return the operator result
     */
    long applyAsLong(long left, long right);
}
```

是一个可以自己实现运算规则的表达式

即，LongAccumulator在LongAdder的基础上可以自定义运算规则，还可以设置非0的初始值



# 8. CopyOnWriteArrayList

## 8.1 ArrayList为什么是线程不安全的

```Java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 09:52 2020-07-09
 */

public class ListTest implements Runnable {
    //线程不安全
    private List threadList = new ArrayList();
    //线程安全
    //private List threadList = Collections.synchronizedList(new ArrayList());
    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //把当前线程名称加入list中
        threadList.add(Thread.currentThread().getName());
    }
    public static void main(String[] args) throws InterruptedException {
        ListTest listThread = new ListTest();
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(listThread, "Thread:"+String.valueOf(i));
            thread.start();
        }
        //等待子线程执行完
        Thread.sleep(2000);
        System.out.println(listThread.threadList.size());
        //输出list中的值
        for (int i = 0; i < listThread.threadList.size(); i++) {
            if (listThread.threadList.get(i) == null) {
                System.out.println();
            }
            System.out.print(listThread.threadList.get(i) + " ");
        }
    }
}
```

新建100个线程，将每个线程的名字加入ArrayList

结束后List中预期会有100个元素

但是实际上会出现：

1.元素不足100个，有的值没有出现

2.报错Exception in thread "Thread:26" java.lang.ArrayIndexOutOfBoundsException: 33
	at java.util.ArrayList.add(ArrayList.java:463)
	at ListTest.run(ListTest.java:24)
	at java.lang.Thread.run(Thread.java:748)

3.元素内容为null

出现这一结果的原因是在ArrayList的add方法中：

```Java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```

其中： elementData[size++] = e;

赋值和size++是两条语句

1. elementData[size] = e；
2. size ++；
   假设A线程执行完第一条语句时，CPU暂停执行A线程转而去执行B线程，此时ArrayList的size并没有加一，这时在ArrayList中B线程就会覆盖掉A线程赋的值，而此时，A线程和B线程先后执行size++，便会跳过一个size不赋值，出现值为null的情况

而报错OutOfBounds的情况，是由于扩容机制导致

A线程在执行ensureCapacity(size+1)后没有继续执行，此时恰好minCapacity等于oldCapacity，B线程再去执行，同样由于minCapacity等于oldCapacity，ArrayList并没有增加长度，B线程可以继续执行赋值（elementData[size] = e）并size ++也执行了，此时，CPU又去执行A线程的赋值操作，由于size值加了1，size值大于了ArrayList的最大长度，
因此便出现了ArrayIndexOutOfBoundsException异常。

## 8.2 CopyOnWriteArrayList解析

并发包中的并发List只有CopyOnWriteArrayList，他是一个线程安全的ArrayList，对其进行的写操作都是在快照上进行的，采用了写时复制策略

```Java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 8673264195747942595L;

    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    private transient volatile Object[] array;
```

使用一个可重入锁来保证同时只有一个线程对array进行修改

array字段使用volatile声明，让其他线程可以捕捉到他的变化

初始化：

```Java
/**
 * Creates an empty list.
 */
public CopyOnWriteArrayList() {
    setArray(new Object[0]);
}

/**
 * Creates a list containing the elements of the specified
 * collection, in the order they are returned by the collection's
 * iterator.
 *
 * @param c the collection of initially held elements
 * @throws NullPointerException if the specified collection is null
 */
public CopyOnWriteArrayList(Collection<? extends E> c) {
    Object[] elements;
    if (c.getClass() == CopyOnWriteArrayList.class)
        elements = ((CopyOnWriteArrayList<?>)c).getArray();
    else {
        elements = c.toArray();
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elements.getClass() != Object[].class)
            elements = Arrays.copyOf(elements, elements.length, Object[].class);
    }
    setArray(elements);
}

/**
 * Creates a list holding a copy of the given array.
 *
 * @param toCopyIn the array (a copy of this array is used as the
 *        internal array)
 * @throws NullPointerException if the specified array is null
 */
public CopyOnWriteArrayList(E[] toCopyIn) {
    setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
}
```

可以从已存在的集合中生成CopyOnWriteArrayList，把集合中所有元素拷贝过去

若无入参，则初始化数组长度为0



添加元素 add(E e)方法 其他方法差不多：

```Java
/**
 * Appends the specified element to the end of this list.
 *
 * @param e element to be appended to this list
 * @return {@code true} (as specified by {@link Collection#add})
 */
public boolean add(E e) {
  //1
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
      //2
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
      //3
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```

1处获取独占锁并加锁

2处复制整个array至新数组，在新数组末尾添加这次插入的元素

3.使用新数组替换老数组



get元素：

```Java
public E get(int index) {
    return get(getArray(), index);
}
```

```Java
final Object[] getArray() {
    return array;
}
```

```Java
private E get(Object[] a, int index) {
    return (E) a[index];
}
```

get方法实际由两个步骤组成，1获取array数组，2在array数组中取指定下标的值

若A线程完成步骤1后，B线程进行了remove操作，删除了数组中的某个元素，完成后A线程执行步骤2，此时仍然能拿到删除前的元素，这就是写时复制策略造成的弱一致性问题



修改指定元素

```Java
public E set(int index, E element) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        E oldValue = get(elements, index);

        if (oldValue != element) {
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len);
            newElements[index] = element;
            setArray(newElements);
        } else {
          //这里！！！！！！！！1
            // Not quite a no-op; ensures volatile write semantics
            setArray(elements);
        }
        return oldValue;
    } finally {
        lock.unlock();
    }
}
```

这里要注意的点就是注释的地方，若是指定位置要修改的元素和新值时一致的，也要重新设置一次array，虽然他的内容完全没有改变，这是为了保证volatile语义



弱一致性的迭代器：

```Java
The "snapshot" style iterator method uses a
* reference to the state of the array at the point that the iterator
* was created. This array never changes during the lifetime of the
* iterator, so interference is impossible and the iterator is
* guaranteed not to throw {@code ConcurrentModificationException}.
* The iterator will not reflect additions, removals, or changes to
* the list since the iterator was created.  Element-changing
* operations on iterators themselves ({@code remove}, {@code set}, and
* {@code add}) are not supported. These methods throw
* {@code UnsupportedOperationException}.
```

在获取迭代器时，这个迭代器持有的是当前数组的快照，从而保证在获取迭代器后，其他线程对list的修改是不可见的，

同时，不支持使用迭代器进行remove set add操作，因为在快照上进行这些操作是没有意义的



缺点及改进：

```Java
* <p>This is ordinarily too costly, but may be <em>more</em> efficient
* than alternatives when traversal operations vastly outnumber
* mutations, and is useful when you cannot or don't want to
* synchronize traversals, yet need to preclude interference among
* concurrent threads. 
```

由于每次写操作都会新建数组并复制所有元素，所以消耗很大，只适合在读多写少的场景下使用

**个人理解：可以使用两个数组来交替使用，用一个标识符控制当前在使用哪一个数组，当写操作来的时候，用一个数组处理，结束后把对另一个数组进行同样的操作，加锁期间读操作读取的是不进行处理的那个数组**

