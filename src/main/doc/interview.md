# 面试-高级

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
      基于该类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，起内部方法操作可以像C的指针一样直接操作内存，因为
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
    - 非公平锁的优点在于吞吐量比公平锁打
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
- 当内存不足，JVM开始垃圾回收，对于强引用对的对象，就算是出现了OOM也不会对该对象进行回收
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
    内存，然后通过一个存储在Java堆里面的DirectByteBuffer对象作业这块内存的引用进行操作。这样能在一些场景中显著提高性能，避免了再Java堆
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
        - 优化case: java -Xmx3800m -Xms3800m -Xmn2g -Xss128k -XX:+UseParallelGC -XX:ParallelGCThreads=20 -XX:+UseParallelOldGC MaxGCPauseMillis=100 -XX:MaxGCPauseMillis=100
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

#### G1垃圾收集器