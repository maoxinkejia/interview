# 面试第二季

## JUC多线程及高并发
#### volatile的理解
- volatile是java虚拟机提供的轻量级的同步机制
	- 保证可见性
	- 不保证原子性
	- 禁止指令重排

- JMM内存模型
	- JMM（java内存模型Java Memory Model）本身是一种抽象的概念，并不真实存在，他描述的是一组规则或规范，
	通过这组规范定义了程序中各个变量（包括实例字段，静态字段和构成数组对象的元素）的访问方式。
	- JMM关于同步的规定
		```
		1.线程解锁前，必须把共享变量的值刷新回主内存
		2.线程加锁前，必须读取主内存的最新值到自己的工作内存
		3.加锁解锁是同一把锁
		```
	- 由于JVM运行程序的实际是线程，而每个线程创建时JVM都会为其创建一个工作内存，工作内存是每个线程的
	私有数据区域，而java内存模型中规定所有变量都存储在主内存，主内存是共享内存区域，所有线程都可以访问，
	但线程对变量的操作（读取赋值等）必须要工作内存中进行，首先要将变量从主内存拷贝到自己的工作内存空间，
	然后对变量进行操作，操作完成后再将变量写回主内存，不能直接操作主内存中的变量，各个线程中存储主内存中的
	变量副本拷贝，因此不同的线程间无法访问对方的工作内存，线程的通信（传值）必须通过主内存来完成。

	- JMM内存模型在多线程开发中需要遵循以下特性
		- 可见性
			```
			各个线程对内存中共享变量的操作都是各个线程各自拷贝到自己的工作内存进行操作后再写回到主内存中的。
			若线程A修改了共享变量X的值但还未写回主内存时，另外一个线程B又对主内存中的共享变量X进行操作，
			此时A线程工作内存中共享变量X对于B线程来说并不可见。这种工作内存与主内存同步延迟现象就造成了可见性问题。
			```
		- 原子性
		
		- 有序性
		    ```
		    1.计算机在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排，一般分为以下步骤：
		      源代码 -->> 编译器优化的重排 -->> 指令并行的重排 -->> 内存系统的重排 -->> 最终执行的指令
		    
		    2.单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。
		    3.处理器在进行重排序时必须要考虑指令之间的数据依赖性。
		    4.多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确定的，结果无法预测。
		    ```

- 哪里用到过volatile
    - 单例模式DCL
        ```
        DCL（Double Check Lock，双端检锁）机制并不一定线程安全，因为有指令重排序存在，加入volatile可以禁止指令重排。
        原因在于某一个线程执行到第一次检测，读取到的instance不为null时，instance的引用对象可能没有完成初始化。
        instance = new instance();可以分为以下3步完成
        memory = allocate(); // 1.分配对象内存空间
        instance(memory); // 2.初始化对象
        instance = memory; // 3.设置instance指向刚分配的内存地址，此时instance != null
        
        因为步骤2和步骤3不存在数据依赖关系，而且无论重排前还是重拍后的执行结果在单线程中并没有改变，因此这种重排是允许的。
        
        ```
    
#### CAS
##### CAS是什么？
- 比较并交换 (Compare And Swap)
- CAS的底层原理
    - UnSafe类
    ```
    1.Unsafe 是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地（native）方法来访问，Unsafe相当于一个后门，
      基于该类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为
      Java中CAS操作的执行依赖于Unsafe类的方法。
    *- Unsafe类中的所有方法都是native修饰的，也就是说，Unsafe类中的方法都直接调用操作系统底层资源执行相应任务。
    2.变量valueOffset，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。
    3.变量value用volatile修饰，保证了多线程之间的内存可见性。
    ```
    - 工作原理
    ```
    1.CAS是一条CPU并发原语。
    2.它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。
    3.CAS并发原语体现在Java语言中就是sun.misc.Unsafe类中的各个方法。调用Unsafe类中的CAS方法，JVM会帮我们实现CAS汇编指令。
      这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，
      是由若干条指令组成的，用于完成某个功能的一个过程，并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说
      CAS是一条CPU原子指令，不会造成所谓的数据不一致问题。 
    ```
    
- CAS缺点
    - 如果CAS失败，会一直进行尝试，若长时间不成功，对CPU的开销很大。
    - 只能保证一个共享变量的操作，若对多个共享变量操作时，就需要锁来保证原子性了。
    - 引发ABA问题
        - ABA问题是怎么产生的
        - 原子引用 AtomicReference
        - 时间戳原子引用 AtomicStampedReference

#### 集合类不安全问题
##### java.util.ConcurrentModificationException
- 导致原因
    - 并发争抢修改导致，一个人正在写，另外一个人过来抢夺，导致数据的不一致性
- 解决方案
    - new Vector<>();
    - Collections.synchronizedList(new ArrayList<>());
    - new CopyOnWriteArrayList<>();
- 写时复制技术思想
```
    CopyOnWrite容器即写时复制的容器。往一个容器添加元素的时候，不直接往当前容器Object[]添加，而是先将当前容器object[]进行copy，
再将原容器的引用指向新容器 setArray(newElements); 这样做的好处是可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前
容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想。
```
- CopyOnWriteArraySet底层是CopyOnWriteArrayList, HashSet底层是HashMap


#### 公平锁/非公平锁/可重入锁/递归锁/自旋锁
##### 公平锁和非公平锁
- 是很么？
    - 公平锁：指多个线程按照申请锁的顺序来获取锁，先来后到。
    - 非公平锁：指多个线程获取锁的顺序不是按照申请锁的顺序，在高并发的情况下，有可能会造成优先级反转或者饥饿现象。
- 两者区别
    - ReentrantLock的构建函数可以指定boolean类型来得到公平锁或非公平锁，默认为非公平锁
    - 公平锁：在并发环境中，每个线程在获取锁时会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，
    就占有锁，否则就会加入到等待队列中，以后会按照FIFO的规则从队列中取到自己。
    - 非公平锁：比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再才用类似公平锁那种方式。
- 题外话
    - 非公平锁的优点在于吞吐量比公平锁大
    - synchronized也是一种非公平锁
    
##### 可重入锁（又名递归锁）
- 是什么
    - 指的是同一线程外层函数获得锁之后，内存递归函数仍然能获取该锁的代码，在同一个线程在外层方法获取锁的时候，在
    进入内层方法会自动获取锁。也就是说，线程可以进入任何一个它已经拥有的锁所同步着的代码块。
- ReentrantLock/synchronized就是一个典型的可重入锁
- 可重入锁最大的作用就是避免死锁

##### 自旋锁
- 是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是
循环会消耗CPU

##### 独占锁(写锁)/共享锁(读锁)/互斥锁
- 独占锁：指该锁一次只能被一个线程所持有。对ReentrantLock和synchronized而言都是独占锁
- 共享锁：指该锁可被多个线程持有，对ReentrantReadWriteLock其读锁就是共享锁，其写锁就是独占锁。读锁的共享锁可保证
并发读是非常高效的，读写，写读，写写的过程都是互斥的。
    
##### synchronized和Lock的区别
- 原始构成
    - synchronized是关键字属于JVM层面
        - monitorenter（底层通过monitor对象来完成，其实wait/nofity等方法也依赖于monitor对象，只有在同步块或方法中
        才能调用wait/notify等方法）
        - monitorenter
    - Lock是具体类(java.util.concurrent.locks.Lock)是api层面的锁
    
- 使用方法
    - synchronized不需要用户去手动释放锁，当sync代码执行完后系统会自动让线程释放对锁的占用
    - ReentrantLock则需要用户去手动释放锁，若没有主动释放锁，就有可能导致出现死锁现象。
        需要lock()和unlock()方法配合try/finally语句块来完成

- 等待是否可中断
    - sync不可中断，除非抛出异常或者正常运行完成
    - ReentrantLock可中断
        - 设置超时方法tryLock(long timeout, TimeUnit unit)
        - lockInterruptibly()放代码块中，调用interrupt()方法可中断

- 锁绑定多个条件Condition
    - sync没有
    - ReentrantLock用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像sync要么随机唤醒一个线程，要么唤醒全部线程
    
    
#### CountDownLatch/CyclicBarrier/Semaphore
- CountDownLatch
    - CountDownLatch通过AQS（AbstractQueuedSynchronizer）里面的共享锁来实现的。ReentrantLock也是使用AQS
    - 使用场景： 举个例子，有三个工人在为老板干活，这个老板有一个习惯，就是当三个工人把一天的活都干完了的时候，
        他就来检查所有工人所干的活。记住这个条件：三个工人先全部干完活，老板才检查。
    
- CyclicBarrier
    - 可循环使用的屏障。集齐七颗龙珠就能召唤神龙。
    
- Semaphore
    - 信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。（抢车位）
    

#### 阻塞队列
##### 概念
- 阻塞队列，顾名思义，首先它是一个队列。
    - 当阻塞队列是空时，从队列中获取元素的操作将会被阻塞
    - 当阻塞队列是满时，往队列里添加元素的操作将会被阻塞
##### 为什么用？有什么好处？
- 在多线程领域，所谓阻塞，在某些情况下会挂起线程（即阻塞），一但条件满足，被挂起的线程又会自动被唤醒
- 为什么需要BlockingQueue？好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都包办了
- 在JUC发布以前，在多线程环境下，每个程序员都必须自己控制这些细节，尤其还要兼顾效率和线程安全，复杂度直线上升
##### 架构设计
- BlockingQueue implements Queue implements Collection
##### 实现类
- **ArrayBlockingQueue：由数组结构组成的有界阻塞队列**
- **LinkedBlockingQueue：由链表结构组成的有界（但大小默认值为Integer.MAX_VALUE）阻塞队列**
- **SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列**

- PriorityBlockingQueue：支持优先级排序的无解阻塞队列
- DelayQueue：使用优先级队列实现的延迟无界阻塞队列
- LinkedTransferQueue：由链表结构组成的无界阻塞队列
- LinkedBlockingDeQue：由链表结构组成的双向阻塞队列

#### 线程池
##### 线程池的优势
```
线程池做的工作主要是控制运行的线程数量，处理过程中将任务放入队列，然后再线程创建后启动这些任务，如果线程数量超过了
最大数量超出数量的线程会排队等候，等其他线程执行完毕，再从队列中取出任务来执行。
**他的主要特点为：线程复用；控制最大并发数；管理线程。**
```
- 第一：降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
- 第二：提高响应速度。当任务到达时，任务可以不需要等现线程创建就能立即执行。
- 第三：提高线程的可管理性。线程是稀缺资源，如果无限制创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池
    可以进行统一的分配，调优和监控
    
##### 线程池的重要参数
- corePoolSize 线程池的常驻核心线程数
- maximumPoolSize 线程池能够容纳同时执行的最大线程数
- keepAliveTime 多余的空闲线程存活时间，若现有线程数大于corePoolSize时，根据存活时间，将超时的空闲线程进行销毁，
    直到只剩下corePoolSize为止
- unit keepAliveTime的单位
- workQueue 任务队列，被提交但尚未被执行的任务存储在此队列
- threadFactory 生成线程池中工作线程的工厂，一般使用默认即可
- handler 拒绝策略，当队列满了并且持续有任务提交时，拒绝处理的策略

##### 线程池工作原理
- 在创建了线程池后，等待提交过来的任务请求。
- 当调用了execute()方法添加一个请求任务时，线程会做如下判断
    - 如果正在运行的线程数量小于corePoolSize，那么马上创建线程运行这个任务
    - 如果正在运行的线程数量大于或等于corePoolSize,那么将这个任务放入队列
    - 如果这时候队列满了，且正在运行的线程数量还小于maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务
    - 如果队列满了且正在运行的线程数量大于或等于maximumPoolSize，那么线程池会启动饱和和拒绝策略来执行
- 当一个县城完成任务时，它会从队列中取下一个任务来执行
- 当一个线程无事可做超过一定时间(keepAliveTime)时，线程池会判断
    - 如果当前运行的线程数大于corePoolSize，那么这个线程就会被停掉
    - 所有线程池的所有任务完成后，它最终会收缩到corePoolSize的大小
    
##### 线程池拒绝策略
- AbortPolicy(默认) 直接抛出RejectedExecutionException异常阻止系统正常运行
- CallerRunsPolicy 调用者运行的一种机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，
    从而降低新任务的流量(主线程自己执行)
- DiscardOldestPolicy 抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务
- DiscardPolicy 直接丢弃任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的一种办法

##### 线程池配置策略
- cpu密集型
    - Runtime.getRuntime().availableProcessors()获取CPU核数，一般公式： cpu核数 + 1 个线程的线程池
- io密集型
    - 参考公式： CPU核数 / (1 - 阻塞系数)  阻塞系数在0.8-0.9之间
    - 比如：8核CPU  8/ (1 - 0.9) = 80个线程数
    
#### 死锁
- jstack pid

---

## JVM && GC
#### JVM垃圾回收时如何确定垃圾？是否知道什么是GC Roots？
- 什么是垃圾？
    - 内存中已经不再被使用到的空间就是垃圾
- 如何判断一个对象是否可以被回收
    - 枚举根节点做可达性分析(跟搜索路径)
    ```
    为了解决引用计数法的循环引用问题，Java使用了可达性分析的方法  **跟踪(Tracing)**
    所谓"GC Roots"或者说Tracing GC的"根集合"就是一组必须活跃的引用。
    基本思路就是通过一系列名为"GC Roots"的对象作为起始点，从这个被称为GC Roots 的对象开始向下搜索，如果一个对象到GC Roots没有
    任何引用链相连时，则说明此对象不可用。也即给定一个集合的引用作为根出发，通过引用关系遍历对象图，能被遍历到的(可到达的)对象
    就被判定为存活，没有被遍历到的就自然被判定为死亡。
    ```
- Java中可以作为GC Roots的对象
    - 虚拟机栈(栈帧中的局部变量区，也叫作局部变量表)中引用的对象
    - 方法区中的类静态属性引用的对象
    - 方法去中常量引用的对象
    - 本地方法栈中JNI(Native方法)引用的对象
- GC算法：
    - 引用计数
    - 复制
    - 标记清除
    - 标记压缩

#### 如何配置JVM参数调优，有哪些JVM系统默认值
- JVM参数类型
    - 标配参数 （-version -help....）
    - X参数(了解)
        - -Xint:解释执行
        - -Xcomp:第一次使用就编译成本地代码
        - -Xmixed:混合模式

    - XX参数(重要)
        - Boolean类型
            - -XX:+/-某个属性值，+:代表开启，-:代表关闭
            - case： -XX:+PrintGCDetails
        - KV设值类型
            - 公式:  -XX:key=value
            - case:  -XX:MetaspaceSize=128m
        - jinfo -flag key pid
        - jinfo -flags pid
- 盘点家底查看JVM默认值
    - java -XX:+PrintFlagsInitial  JVM初始默认参数
    - java -XX:+PrintFlagsFinal [-version || -XX:MetaspaceSize=512m]   JVM调整过后参数
    - =是初始参数， :=是修改后的参数
    - java -XX:+PrintCommandLineFlags -version

#### 平时常用的基本参数配置有哪些
- -Xms 等价于 -XX:InitialHeapSize 默认为物理内存的1/64
- -Xmx 等价于 -XX:MaxHeapSize 默认为物理内存的1/4
- -Xss 等价于 -XX:ThreadStackSize 设置单个线程栈的大小，一般默认为512~1024k
    - jinfo -flag ThreadStackSize -->> 系统默认0，但等价于1024k(官网给出)
- -Xmn 设置年轻代大小
- -XX:MetaspaceSize 设置元空间大小
    - 元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：元空间并
        不在虚拟机中，而是使用本地内存。因此默认情况下，元空间的大小仅受本地内存限制。
    - -Xmx10m -Xmx10m -XX:MetaspaceSize=1024m -XX:+PrintFlagsFinal
- -XX:+PrintGCDetails 打印GC详情
- -XX:SurvivorRatio 幸存区比例 默认8:1:1
    - case: -XX:SurvivorRatio=4  4:1:1
- -XX:NewRatio 年轻代老年代比例，默认2，即1:2
    - case: -XX:NewRatio=4  即新生代占1，老年代占4  1:4
    - case: -XX:NewRatio=2  即新生代占1，老年代占2  1:2
- -XX:MaxTenuringThreshold 查看默认进入老年代年龄
    - case: -XX:MaxTenuringThreshold=0 若设置成0，则年轻代对象不经过Survivor区，直接进入老年代。对于老年代比较多的应用，可以提高效率
    - 默认15，设置区间在0-15之间


#### 强引用、弱引用、软引用、虚引用
##### 强引用
- 当内存不足，JVM开始垃圾回收，对于强引用的对象，就算是出现了OOM也不会对该对象进行回收
- 强引用是我们最常见的普通对象引用，只要还有强引用指向一个对象，就能表明对象还活着，垃圾收集器不会碰这种对象。在Java中最常见的
    就是强引用，把一个对象赋给一个引用变量，这个引用变量就是一个强引用。当一个对象被强引用变量引用时，它处于可达状态，它是不可能
    被垃圾回收机制回收的，即使该对象以后永远不会被用到JVM也不会回收。因此强引用是造成Java内存泄漏的主要原因之一。    
- 对于一个普通的对象，如果没有其他的引用关系，只要超过了引用的作用域，或者显式地将相应（强）引用赋值为null，一般认为就是可以被
    垃圾收集的了
##### 软引用
- 软引用是一种相对强引用弱化了一些的引用，需要用java.lang.ref.SoftReference类来实现，可以让对象豁免一些垃圾收集
- 对于只有软引用的对象来说
    - 当系统内存充足时，不会被回收
    - 当系统内存不足时，会被回收
- 软引用通常用在对内存敏感的程序中，比如高速缓存就有用到软引用，内存够用的时候就留，不够用就回收
##### 弱引用
- 弱引用需要用java.lang.ref.WeakReference类来实现，它比软引用的生存期更短
- 对于只有弱引用的对象来说，只要垃圾回收机制一运行，不管JVM的内存空间是否足够，都会回收该对象占用的内存
- 软引用和弱引用的适用场景
    - 假如应用需要读取大量的本地图片
        - 如果每次读取图片都从硬盘读取则会严重影响性能
        - 如果一次性全部加载到内存中又可能造成内存溢出
    - 此时使用软引用可以解决这个问题
    - 设计思路是：用一个HashMap来保存图片的路径和相应图片对象关联的软引用之间的映射关系，在内存不足时，JVM会自动回收这些缓存图片对象
        所占用的空间，从而有效的避免了OOM的问题
    - Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
##### 虚引用
- 虚引用需要java.lang.ref.PhantomReference类来实现
- 顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么他就和没有任何引用一样，
    在任何时候都可能被垃圾回收器回收，它不能单独使用也不能通过它访问对象，虚引用必须和引用队列(ReferenceQueue)联合使用。
- 主要作用就是确保对象被finalize以后，做某些事情的机制，在垃圾收集器将对象从内存中清除出去之前做必要的清理工作。

#### OOM
##### java.lang.StackOverFlowError
##### java.lang.OutOfMemoryError:Java heap space
##### java.lang.OutOfMemoryError:GC overhead limit exceeded
- GC回收时间过长时会抛出此异常，过长的定义是，超过98%的时间用来做GC并且回收了不到2%的堆内存，连续多次GC都只回收了不到2%的极端情况下才会抛出。
- 假如不抛出GC overhead limit exceeded错误会发生什么情况呢？那就是GC清理的那么点内存会很快再次填满，迫使GC再次执行，这样就形成恶性循环，
    CPU使用率一直是100%，而GC却没有任何成果
##### java.lang.OutOfMemoryError:Direct buffer memory
- 写NIO程序经常使用ByteBuffer来读取或者写入数据，这是一种基于通道(Channel)与缓冲区(Buffer)的I/O方式，他可以使用Native函数库直接分配堆外
    内存，然后通过一个存储在Java堆里面的DirectByteBuffer对象作业这块内存的引用进行操作。这样能在一些场景中显著提高性能，避免了在Java堆
    和Native堆中来回复制数据。
    - ByteBuffer.allocate(capability) 分配JVM堆内存，属于GC管辖范围，由于需要拷贝所以相对速度较慢
    - ByteBuffer.allocateDirect(capability) 分配OS本地内存，不属于GC管辖范围，由于不需要内存拷贝所以速度相对较快。
- 但如果不断分配本地内存，堆内存很少使用，那么JVM就不需要执行GC，DirectByteBuffer对象们就不会被回收，这时候堆内存充足，但本地内存可能
    已经是用光了，再次尝试分配本地内存就会出现OutOfMemoryError。
##### java.lang.OutOfMemoryError:unable to create new native thread
- 导致原因：
    - 应用创建了太多线程，超过了系统承载极限
    - linux系统默认允单个进程可以创建的线程数是1024个(非root用户，root用户没有限制)
- 解决办法：
    - 分析应用是否真的需要创建那么多线程，若不需要则修改程序减少线程数
    - 修改linux配置调优 /etc/security/limits.d/90-nproc.conf
##### java.lang.OutOfMemoryError:Metaspace

#### 垃圾回收算法和垃圾回收器的关系
- GC算法是内存回收的方法论，垃圾收集器就是算法落地实现
- 4种主要垃圾收集器
    - Serial串行垃圾回收器
        - 它为单线程环境设计且只使用一个线程进行垃圾回收，会暂停所有的用户线程。不适用于服务器环境。
    - Parallel并行垃圾回收器
        - 多个垃圾收集线程并行工作，此时用户线程是暂停的，适用于科学计算/大数据处理首台处理等弱交互场景
        - 优化case: java -Xmx3G -Xms3G -XX:+UseParallelGC -XX:ParallelGCThreads=20 -XX:+UseParallelOldGC -XX:MaxGCPauseMillis=100
    - CMS并发垃圾回收器
        - 用户线程和垃圾收集线程同时执行(不一定是并行，可能交替执行)，不需要停顿用户线程，互联网公司多用它，适用对响应时间有要求的场景
        - 优化case: java -Xmx3550m -Xms3550m -Xmn2g -Xss128k-XX:ParallelGCThreads=20  -XX:+UseConcMarkSweepGC -XX:+UseParNewGC  -XX:CMSFullGCsBeforeCompaction=5 -XX:+UseCMSCompactAtFullCollection
    - G1垃圾回收器
        - 将堆内存分割成不同的区域然后并发的对其进行垃圾回收
        - 优化case: java -Xmx12m -Xms3m -Xmn1m -XX:PermSize=20m -XX:MaxPermSize=20m -XX:+UseSerialGC -jar java-application.jar

#### 怎么查看服务器默认垃圾收集器？生产上如何配置垃圾收集器？谈谈对垃圾收集器的理解
##### java的gc回收的类型主要有：
- UseSerialGC
- UseParallelGC
- UseConcMarkSweepGC
- UseParNewGC
- UseParallelOldGC
- UseG1GC
##### 垃圾收集器
- 部分参数预先说明
    - DefNew -->> Default New Generation
    - Tenured -->> Old
    - ParNew -->> Parallel New Generation
    - PSYoungGen -->> Parallel Scavenge
    - ParOldGen -->> Parallel Old Generation
- 新生代
    - 串行GC(Serial/Serial Copying)
        ```
            串行收集器是最古老，最稳定以及效率最高的收集器，只使用一个线程去回收但其再进行垃圾收集过程中可能会产生较长的停顿(STW)。虽然
        在垃圾收集过程中需要暂停所有其他的工作线程，但是它简单高效，对于限定单个CPU来说没有线程交互的开销可以获得最高的单线程垃圾收集
        效率，因此Serial垃圾收集器依然是Java虚拟机运行在Client模式下默认的新生代垃圾收集器。
        ```
        - 参数： -XX:+UseSerialGC
            - 开启后会使用Serial(Young区用) + Serial Old(Old区用)的收集器组合
            - -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseSerialGC
    - 并行GC(ParNew)
        ```
        1.使用多线程进行垃圾回收，会STW暂停其它所有工作线程直到它收集结束。
        2.ParNew收集器其实就是Serial收集器新生代的并行多线程版本，最常见的应用场景是配合老年代的CMS GC工作，其余的行为和Serial收集器完全
            一样，ParNew在垃圾收集过程中同样也要暂停所有其他的工作线程。它是很多java虚拟机运行在Server模式下新生代的默认垃圾收集器。
        ```
        - 参数： -XX:+UseParNewGC (启动此收集器时，只影响新生代的收集，不影响老年代)
            - 开启后会使用ParNew + Serial Old的组合模式，但java8已经不再被推荐
            - -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseParNewGC
    - 并行回收GC(Parallel/Parallel Scavenge)
        ```
        1.Parallel Scavenge收集器类似ParNew也是一个新生代垃圾收集器，使用复制算法，也是一个并行的多线程的垃圾收集器，俗称吞吐量优先级
            收集器。
        2.他关注的重点是：可控制的吞吐量(ThoughPut=运行用户代码时间/(运行用户代码时间 + 垃圾收集时间)，也即比如程序运行100分钟，垃圾收集
            时间1分钟，吞吐量就是99%)。高吞吐量意味着高效利用cpu的时间，他多用于在后台运算而不需要太多交互的任务。
        3.自适应调节策略也是ParallelScavenge收集器与ParNew收集器的一个重要区别。(自适应调节策略：虚拟机会根据当前系统的运行情况收集性能
            监控信息，动态调整这些参数以提供最合适停顿时间(-XX:MaxGCPauseMills)或最大吞吐量)。
        ```
        - 参数： -XX:+UseParallelGC 或 -XX:+UseParallelOldGC(可互相激活)
        - 参数： -XX:ParallelGCThreads=n  启动多少个GC线程

- 老年代
    - 串行GC(Serial Old/Serial MSC)
        - Serial Old是Serial垃圾收集器老年代版本，它同样是单线程的收集器，使用标记-整理算法，这个收集器也主要是运行在Client默认
            的Java虚拟机默认的老年代垃圾收集器
        - 在Server模式下，主要有两个用途
            - 在JDK1.5之前的版本中与新生代的Parallel Scavenge收集器搭配使用(Parallel Scavenge + Serial Old)
            - 作为老年代版本中使用CMS收集器的后备垃圾收集方案。
    - 并行GC(Parallel Old/Parallel MCS)
        ```
        1.Parallel Old收集器是Parallel Scavenge的老年代版本，使用多线程的标记-整理算法，Parallel Old收集器在JDK1.6才开始提供。
        2.在JDK1.6之前，新生代使用Parallel Scavenge收集器只能搭配老年代的Serial Old收集器，只能保证新生代的吞吐量优先，无法保证
            整体的吞吐量。
        3.Parallel Old正是为了在老年代同样提供吞吐量优先的垃圾收集器，如果系统对吞吐量要求比较高，JDK1.8后可以考虑新生代Parallel 
            Scavenge和老年代Parallel Old收集器的搭配策略。
        ```
        - 参数： -XX:+UseParallelOldGC
    - 并发标记清除GC(CMS)
        ```
        1.CMS收集器(Concurrent Mark Sweep:并发标记清除)是一种以获取最短回收停顿时间为目标的收集器。
        2.适合应用在互联网站或者B/S系统的服务器上，这类应用尤其重视服务器的响应速度，希望系统停顿时间最短
        3.CMS非常适合堆内存大、CPU核数多的服务器端应用，也是G1出现之前大型应用的首选收集器
        4.CMS并发标记清楚，并发收集低停顿，并发指的是与用户线程一起执行
        ```
        - 参数： -XX:+UseConcMarkSweepGC 开启后会自动将 -XX:+UseParNewGC打开 ``开启该参数后，使用ParNew(Young区用) + CMS(Old区用) + 
            Serial Old的收集器组合，Serial Old将作为CMS出错的后备收集器``
        - 4步过程
            - 初始标记(CMS initial mark) ``只是标记一下GC Roots能直接关联的对象，速度很快，仍然需要暂停所有的工作线程``
            - 并发标记(CMS Concurrent mark)和用户线程一起 ``进行GC Roots跟踪的过程，和用户线程一起工作，不需要暂停工作线程。
                主要标记过程，标记全部对象``
            - 重新标记(CMS remark) ``为了修正在并发标记期间，因用户程序继续运行而导致标记产生变动的那一部分对象的标记记录，仍然需要
                暂停所有的工作线程``
            - 并发清除(CMS concurrent sweep)和用户线程一起 ``清楚GC Roots不可达对象，和用户线程一起工作，不需要暂停工作线程。基于标记
                结果，直接清理对象。由于消耗最长的并发标记和并发清除过程中，垃圾收集线程可以和用户现在一起并发工作，所以总体上来看
                CMS收集器的内存回收和用户线程是一起并发执行。``
        - 优点： 并发收集停顿低
        - 缺点：
            - 并发执行，对CPU资源压力大 ``由于并发进行，CMS在收集与应用线程会同时增加对堆内存的占用，也就是说，CMS必须要在老年代
                堆内存用尽之前完成垃圾回收，否则CMS回收失败时，将触发担保机制，串行老年代收集器将会以STW的方式进行一次GC，从而
                造成较大的停顿时间。``
            - 采用的标记清除算法会导致大量碎片 ``提供了 -XX:CMSFullGCsBeForeCompaction(默认0，即每次都进行内存整理)来指定多少次CMS
                收集之后，进行一次压缩的Full GC。``

##### 如何选择垃圾收集器
- 单CPU或小内存
    - -XX:+UseSerialGC
- 多CPU，需要最大吞吐量，如后台计算型应用
    - -XX:+UseParallelGC 或者 -XX:+UseParallelOldGC
- 多CPU，追求低停顿时间，需快速响应，如互联网应用
    - -XX:+UseConcMarkSweepGC


#### G1垃圾收集器
##### G1是什么
`G1(Garbage-First)收集器，是一款面向服务端应用的收集器.应用在多处理器和大容量内存环境中，在实现高吞吐量的同时，尽可能的满足垃圾
    收集暂停时间的要求。`
- 具有以下特性
    - 像CMS收集器一样，能与应用程序线程并发执行
    - 整理空闲空间更
    - 需要更多时间来预测GC停顿时间
    - 不希望牺牲大量的吞吐西能
    - 不需要更大的Java Heap
- G1收集器的设计目标是取代CMS收集器，它同CMS相比，在以下方面表现的更出色：
    - G1是一个有整理内存过程的垃圾收集器，不会产生很多内存碎片
    - G1的STW更可控，G1在停顿时间上添加了预测机制，用户可以指定期望停顿时间
- G1主要改变的是Eden，Survivor，Tenured等内存区域不再是连续的了，而是变成了一个个大小一样的region，每个region从1M到32M不等。
    一个region有可能属于Eden，Survivor，Tenured内存区域。
- **特点**
    - G1充分利用多CPU、多核环境硬件优势，尽量缩短STW
    - G1整体上采用标记-整理算法，局部是通过复制算法，不会产生内存碎片
    - 宏观上看G1之中不再区分年轻代和养老代。把内存划分成多个独立的子区域(Region)，可以近似理解为一个围棋的棋盘。
    - G1收集器里面讲整个的内存区都混合在一起了，但其本身依然在小范围内要进行年轻代和老年代的区分，保留了新生代和老年代，但它们
        不再是物理隔离的，而是一部分Region的集合且不需要Region是连续的，也就是说依然会采用不同的GC方式来处理不同的区域。
    - G1虽然也是分代收集器，但整个内存分区不存在物理上的年轻代与老年代的区别，也不需要完全独立的Survivor堆做复制准备。G1只有
        逻辑上的分代概念，或者说每个分区都可能随G1的运行在不同代之间前后切换。
    
##### 底层原理
- Region区域化垃圾收集器： 最大好处是化整为零，避免全内存扫描，只需要按照区域来进行扫描即可
    - 区域化内存划片Region，整体编为了一些不连续的内存区域，避免了全内存区的GC操作。
        - 核心思想是将整个堆内存区域分成大小相同的子区域(Region)，在JVM启动时会自动设置这些子区域的大小
    - 在堆的使用上，G1并不要求对象的存储一定是物理上连续的只要逻辑上连续即可，每个分区也不会固定地为某个代服务，可以按需在年轻
        代和老年代之间切换。
        - 启动时可以通过参数 -XX:G1HeapRegionSize=n可指定分区大小(1MB-32MB，且必须是2的幂)，默认将整堆划分为2048个分区。
        - 大小范围在1MB-32MB，最多能设置2048个区域，也就是能够支持的最大内存为： 32MB * 2048 = 65536MB = 64G内存
    - 在G1中，还有一种特殊的区域，叫Humongous(巨大的)区域。
        - 如果一个对象占用的空间超过了分区容量50%以上，G1收集器就认为这是一个巨型对象，这些巨型对象默认会被分配在老年代
        - 但如果它是一个短期存在的巨型对象，就会对垃圾收集器造成负面影响。
        - 为了解决这个问题，G1划分了一个Humongous区，它用来专门存放巨型对象。如果一个H区装不下一个巨型对象，那么G1会寻找连续的
            H分区来存储，为了能找到连续的H区，有时候不得不启动Full GC
- 回收步骤
    - 针对Eden区进行收集，Eden区耗尽后会被触发，主要是小区域收集 + 形成连续的内存块，避免内存碎片
- 4步过程(同CMS一样)

##### 常用配置参数
- -XX:+UseG1GC
- -XX:G1HeapRegionSize=n 设置G1区域的大小，值是2的幂范围是1-32MB
- -XX:MaxGCPauseMills=n 设置最大GC停顿时间，这是个软目标，JVM将尽可能(但不保证)停顿小于这个时间
- -XX:InitiatingHeapOccupancyPercent=n 堆占用了多少的时候就触发GC，默认为45
- -XX:ConcGCThreads=n 设置并发GC使用的线程数
- -XX:G1ReservePercent=n 设置作为空闲空间的预留内存百分比，以降低目标空间溢出的风险，默认是10%


## 服务器性能诊断分析
#### 整机
##### top
- load average 系统最近1分钟，5分钟，15分的系统负载值。如果三个值想加再除以3再乘以100%，大于60%，说明系统负载较重
- uptime 简易版查看load average参数
- 按数字键1，查看各个cpu执行情况

##### CPU
- vmstat -n 2 3
    - procs
        - r:运行和等待CPU时间片的进程数，原则上1核的CPU的运行队列不要超过2，整个系统的运行队列不能超过总核数的2倍，否则代表
            系统压力过大
        - b:等待资源的进程数，比如正在等待磁盘I/O、网络I/O等。
    - cpu
        - us:用户进程消耗CPU时间百分比，us值高，用户进程消耗CPU时间多，如果长期大于50%，优化程序
        - sy:内核进程消耗的CPU时间百分比
        - us + sy参考值为80%，若想加大于80%，说明可能存在CPU不足。
        - id:系统处于空间的CPU百分比
        - wa:系统等待IO的CPU时间百分比
        - st:来自于一个虚拟机偷取的CPU时间百分比
- mpstat -P ALL 2
    - 打印出所有cpu进程的消耗
- pidstat -u 1 -p pid
    - 指定pid，查看当前进程的cpu使用率

##### 内存
- free -m -s 2
- pidstat -p pid -r 2

##### 硬盘
- df -h
-du * -sh

##### 网络I/O
- iostat -xdk 2 3
    - rkB/s 每秒读取数据量kb
    - wkB/s 每秒写入数据量kb
    - svctm I/O请求的平均服务时间，单位毫秒
    - await I/O请求的平均等待时间，单位毫秒，值越小性能越好
    - util 一秒钟有百分之几的时间用于I/O操作，接近100%时，表示磁盘带宽跑满，需要优化程序或者增加磁盘
    ```
    1.rkB/s、wkB/s根据系统应用不同会有不同的值，但有规律遵循：长期、超大数据读写，肯定不正常
    2.svctm的值与await的值很接近，表示几乎没有I/O等待，磁盘性能好
    3.如果await的值远高于svctm的值，表示I/O队列等待太长，需要优化程序或更换更快磁盘
    ```
- pidstat -d 2 -p pid

##### 磁盘I/O
- ifstat


#### 生产环境cpu过高分析
- ps -mp pid -o THREAD,tid,time
    - -m 显示所有的线程
    - -p pid进程使用cpu的时间
    - -o 该参数后是用户自定义格式
- 将线程号转换为16进制(英文小写格式) printf "%x\n" 或 计算器
- jstack pid |grep tid -A60


## GitHub
#### in关键字
- xxx in:name 项目名中包含xxx的
- xxx in:description 项目描述中包含xxx的
- xxx in:readme 项目中readme文件中包含xxx的
- 组合使用 xxx in:name,readme,description

#### stars或fork数量关键字
- xxx stars:> 1000 或者  xxx stars:>= 1000
- xxx forks:> 500 或者 xxx forks:>= 500
- 100..200 在100-200之间
- 组合使用： springboot forks:2000..4000 stars 6000..8000

#### awesome关键字
- awesome系列一般是用来收集学习、工具、书籍类相关的项目

#### 高亮显示
- 地址 + #L13-L23
    - 例： https://github.com/maoxinkejia/interview/blob/main/src/main/java/com/zmx/study/interview/second/lock/ReentrantLockDemo.java#L7-L24

#### github之快捷键
https://docs.github.com/en/free-pro-team@latest/github/getting-started-with-github/keyboard-shortcuts
