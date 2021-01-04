# 面试第三季

## Java基础篇
#### 字符串常量池 string.intern()方法
#### 两数求和，LeeCode算法题

## JUC
#### 可重入锁
- 说明：同一个线程在外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁(前提，锁对象得是同一个对象)，不会因为之前已经
    获取过还没释放而阻塞
- Java中ReentrantLock和synchronized都是可重入锁，优点是可一定程度避免死锁
- 可重入锁的种类
    - 隐式锁(即synchronized关键字)默认是可重入锁
    - synchronized的重入的实现机理
        - 每个锁对象拥有一个锁计数器和一个指向持有该锁的线程的指针
        - 当执行monitorenter时，如果目标锁对象的计数器为零，那么说明它没有被其他线程所持有，Java虚拟机会将该锁对象的持有线程
            设置为当前线程，并且将计数器加1
        - 在目标锁对象的计数器不为零的情况下，如果锁对象的持有线程是当前线程，那么Java虚拟机可以将其计数器加1，否则需要等待，
            直至持有线程释放该锁。
        - 当执行monitorexit时，Java虚拟机则将需将锁对象的计数器减1。计数器为零代表锁已被释放。
    - 显式锁(即Lock)也有ReentrantLock这样的可重入锁
        - lock几次就必须unlock几次，否则会造成锁不释放
        
#### LockSupport
##### 是什么
- LockSupport是用来创建锁和其他同步类的基本线程阻塞原语。
- 使用了一种名为Permit(许可)的概念来做到阻塞和唤醒线程的功能，每个线程都有一个许可(permit),permit只有两个值1、0，默认是0
    也可以把许可看成是一种(0,1)信号量(Semaphore)，但与Semaphore不同的是，许可的累加上限是1
- java.util.concurrent.locks.LockSupport
##### 线程等待唤醒机制(wait/notify)
- 3种让线程等待和唤醒的方法
    - 使用Object中的wait()和notify()
    - 使用JUC包中Condition的await()和signal()
    - LockSupport类可以阻塞以及唤醒指定被阻塞的线程

- Object类中的wait和notify方法实现线程等待和唤醒
    - wait和notify必须在同步块或方法里面且成对出现使用
    - 先wait后notify才可以

- Condition接口中的await和signal方法实现线程的等待和唤醒
- 传统的synchronized和Lock实现等待唤醒通知的约束
    - 线程先要获得并持有锁，必须在锁块中
    - 必须要先等待后唤醒，线程才能够被唤醒
    
##### 主要方法
- 阻塞
    - park()/park(Object blocker)
        - permit默认是0，所以一开始调用park方法，当前线程就会阻塞，直到别的线程将当前线程的permit设置为1时，park方法会被唤醒，然后
            会将permit再次置为0并返回。
    - 阻塞当前线程/阻塞传入的具体线程
- 唤醒
    - unpark(Thread thread)
    - 唤醒处于阻塞状态的指定线程
    
- 使用时无锁块要求，且先唤醒后等待LockSupport照样支持

##### 重点说明
```text
    LockSupport是用来创建锁和其他同步类的基本线程阻塞原语。
    LockSupport是一个线程阻塞工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，阻塞之后也有对应的唤醒方法。归根结底，
LockSupport调用的Unsafe中的native方法。
    LockSupport提供park()和unpark()方法实现阻塞线程和接触线程阻塞的过程。
    LockSupport和每个使用它的线程都有一个许可(permit)关联，permit相当于1,0的开关，默认是0，调用一次unpark就加1变成1，调用一次
park会消费permit，也就是将1变成0，同事park立即返回。如果再次调用park会变成阻塞(因为permit为零了会阻塞在这里，一直到permit变
为1)，这时调用unpark会把permit置为1。
    每个线程都有一个相关的permit，permit最多只有一个，重复调用unpark也不会累计凭证。
```
- 形象的理解
    - 线程阻塞需要消耗凭证(permit)，这个凭证最多只有一个
    - 当调用park方法时
        - 如果有凭证，则会直接消耗掉这个凭证然后正常退出
        - 如果无凭证，就必须阻塞等待凭证可用
    - 当调用unpark方法时
        - 它会增加一个凭证，但凭证最多只能有1个，累加无效
        
#### AbstractQueuedSynchronizer之AQS
##### 是什么？
- 抽象的队列同步器
-  是用来构建锁或者其它同步器组件的重量级基础框架及整个JUC体系的基石，通过内置的FIFO队列来完成资源获取线程的排队工作，并通过一个
    int类型变量表示持有锁的状态。
    - 简单来说：一个state的字段来表示锁的占有状态，大于1则表示锁已被占有，小于1则表示锁已释放，对于未占有到锁的线程则放到CLH队列
        中(FIFO)等待再次占有锁

##### AQS为什么是最重要的基石
- ReentrantLock、CountDownLatch、ReentrantReadWriteLock、Semaphore.....都继承了AbstractQueuedSynchronizer类
- 进一步理解锁和同步器的关系
    - 锁，面向锁的使用者 `定义了程序员和锁交互的使用层API，隐藏了实现细节，你调用即可。`
    - 同步器，面向锁的实现着 `比如Java并发大神DougLee，提出统一规范，并简化了锁的实现，屏蔽了同步状态管理、阻塞线程排队和通知、
        唤醒机制等。`

##### 能干嘛
- 加锁会导致阻塞
    -  有阻塞就需要排队，实现排队必然需要有某种形式的队列来进行管理
- 解释说明
    ```text
        抢到资源的线程直接使用处理业务逻辑，抢不到资源的必然涉及一种排队等候机制。抢占资源失败的线程继续去等待，但等候线程仍然保留
    获取锁的可能且获取锁流程仍然在继续。
        如果共享资源被占用，就需要一定的阻塞等候唤醒机制来保证锁分配。这个机制主要用的是CLH队列的变体实现的，将暂时获取不到锁的
    线程加入到队列中，这个队列就是AQS的抽象表现。它将请求共享资源的线程封装成队列的节点(Node)，通过CAS、自旋以及LockSupport.park()
    的方式，维护state变量的状态，使并发达到同步的控制效果
    ```
    
##### AQS初识
```text
    AQS使用一个volatile的int类型的成员变量来表示同步状态，通过内置的FIFO队列来完成资源获取的排队工作，将每条要去抢占资源的线程封装
成一个Node节点来实现锁的分配，通过CAS完成对State值的修改
```

##### 内部体系架构
- AQS自身
    - AQS的int变量
        - 同步状态state成员变量
        - 等于0就是没人，自由状态可以抢占
        - 大于等于1，就是有人，需要等待
    - AQS的CLH队列
        - CLH队列(三个大牛的名字组成)，为一个双向队列
    - 小总结
        - 有阻塞就需要排队，实现排队必然需要度列
        - state变量 + CLH变种的双端队列
        
- 内部类Node
    - Node的int变量
        - Node的等待状态waitStatus成员变量
        - 等待队列中线程的等待状态，每一个线程就是一个Node
    - Node讲解 查看源码可以得知
        - Node实质上就是一个双向链表，里面包含了前指针和后指针，指向了前一个node节点，和后一个node节点，当第一个线程需要入队时，
            系统会构建一个空的傀儡节点，来充当链表的头/尾节点，头节点会指向真实的第一个node，当前node的前一个节点会指向头节点。
        - 每一个node都有一个waitStatus状态，表示当前节点的等待状态，初始为0，需要置为-1时才能进行park；当unpark时需要将此状态再次
            设置为0(通过CAS)
            
            
## Spring
#### Spring的AOP顺序
##### AOP常用注解
- @Before
- @After
- @AfterReturning
- @AfterThrowing
- @Around
##### spring4，aop通知顺序
- 正常顺序
    ```
    1.环绕通知之前
    2.before
    3.方法
    4.环绕通知之后
    5.after
    6.afterReturning
    ```
- 异常时顺序
    ```
    1.环绕通知之前
    2.before
    3.after
    4.afterThrowing
    ```
##### spring5，aop通知顺序
- 正常顺序
    ```
    1.环绕通知之前
    2.before
    3.方法
    4.afterReturning
    5.after
    6.环绕通知之后
    ```
- 异常顺序
    ```
    1.环绕通知之前
    2.before
    3.afterThrowing
    4.after
    ```
        
##### 切点表达式
**execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))**

#### 循环依赖
- 问题：
    - spring中的三级缓存分别是什么？三个Map有什么不同？
    - Spring容器是什么？
    - 什么是循环依赖？如何检测是否存在循环依赖？
    - 开发中是否见到过循环依赖的异常？多例情况下，循环依赖问题为什么无法解决？
    
##### 什么是循环依赖
- 多个bean之间相互依赖，形成了一个闭环，比如A依赖于B，B依赖于C，C依赖于A
    - A @Autowired B
    - B @Autowired C
    - C @Autowired A
- 通常来说，如果问spring容器内部如何解决循环依赖，一定是指默认的单例Bean中，属性相互引用的场景。
##### 两种注入方式对循环依赖的影响
- 构造器注入
    - 官方解释：无法解决此问题，会形成俄罗斯套娃，一层套一层
- set方法注入
    - 官方推荐使用
- 结论
    - AB循环依赖问题只要A的注入方式是setter且singleton，就不会有循环依赖问题

##### BeanCurrentlyCreationException
- spring容器在默认的单例(singleton)的场景是支持循环依赖的，不报错
- 若为prototype时则是不支持循环依赖的，会报错

##### DefaultSingletonBeanRegistry类，实现了三级缓存，解决循环依赖的问题
- 第一级缓存(也叫单例池)singletonObjects：存放已经经历了完整生命周期的Bean对象
- 第二级缓存：earlySingletonObjects，存放早期暴露出来的Bean对象，Bean的生命周期未结束
- 第三级缓存：singletonFactories，可以存放生成Bean的工厂

- 只有单例的bean会通过三级缓存提前暴露来解决循环依赖的问题，而非单例的bean，每次从容器中获取都是一个新的对象，都会重新创建，
    所以非单例的bean是没有缓存的，不会将其放到三级缓存中
    
##### 三级缓存
- 实例化/初始化
    - 实例化：内存中申请一块内存空间
    - 初始化属性填充：完成属性的各种赋值3
- 3个Map和四大方法
    - singletonObjects 一级缓存  ConcurrentHashMap<String, Object>
    - earlySingletonObjects 二级缓存 HashMap<String, Object>
    - singletonFactories 三级缓存 HashMap<String, ObjectFactory<?>>
    - getSingleton
    - doCreateBean
    - populateBean
    - addSingleton
- 总结：
    - A创建过程中需要B，于是A将自己放到三级缓存里面，去实例化B
    - B实例化的时候发现需要A，于是B先查一级缓存，没有，再查二级缓存，还是没有，再查三级缓存，找到了A，然后把三级缓存里面的这个A
        放到了二级缓存里面，并删除三级缓存里面的A
    - B顺利初始化完毕，将自己放到一级缓存里面（此时B里面的A依然是创建中状态），然后回来接着创建A，此时B已经创建完成，直接从一级
        缓存里面拿到B，然后完成创建，并将A自己放到一级缓存里面。
        
        
## Redis
#### 安装redis6.0.8
##### 参考redis中/英文官网，redis有bug，需要升级至6.0.8版本

#### 传统的5大数据类型
##### redis八种数据类型
- String
    - 常用命令：
        - set key value
        - get key
        - mset k1 v1 k2 v2 k3 v3
        - mget k1 k2 k3
        - incr key
        - incrby key increment
        - decr key
        - decrby key decrement
        - strlen key
        - setnx key value
        - set key value [ex seconds] [px milliseconds] [nx] [xx]
            - ex: 多少秒后后期
            - px：多少毫秒后过期
            - nx：当key不存在的时候，才创建key，效果等同于setnx
            - xx：当key存在的时候覆盖key
- Hash
    - hset key field value
    - hget key field
    - hmset key f1 v1 f2 v2
    - hmget key f1 f2
    - hgetall key
    - hlen key
    - 等等
- List
    - lpush k v
    - rpush k v
    - lrange k 0 -1
    - llen key
- Set
    - sadd key v1 v2 v3 v4
    - smembers key
        - 获取集合中的所有元素
    - srandmember key [count]
        - 随机弹出多个元素，不删除该元素，count为弹出个数，不写为1个
    - spop key [count]
        - 随机弹出多个元素，但删除该元素，count为弹出个数，不写为1个
    - scard key 
        - 获取集合中的元素个数
    - sinter k1 k2
        - 两个集合取交集，相同点
    - sdiff k1 k2
        - 两个集合取差集，不同点
    - sunion k1 k2
        - 两个集合取并集，合并后的集合
- SortedSet(ZSet)
    - zadd key score value
    - zrange key 0 -1
        - 按照元素分数从小到大的顺序，返回索引从start到stop之间的所有元素
    - zscrore key member
        - 获取元素的分数
    - zrem key member
        - 删除元素
    - zrangebyscore key min max withscores [limit offset count]
        - 获取指定分数范围的元素
    - zincrby key increment member
        - 增加某个元素的分数
    - zcard key
        - 获取集合中元素的数量
    - zcount key min max
        - 获取指定分数范围内的元素个数
    - zremrangebyrank key start stop
        - 按照排名范围删除元素
- Bitmap
- HyperLogLog
- GEO

**在redis中可以使用： help @string 命令查看该数据类型对应的所有命令**

#### 分布式锁
##### redisson，官方推荐

#### redis缓存过期淘汰策略
##### redis内存满了怎么办？
- redis默认内存多少？在哪里查看？如何修改配置？
    - redis.conf 中配置，如果不配置或配置为0，在64位操作系统中不限制内存大小，在32位操作系统中最多使用3G
        - maxmemory
        - 一般推荐redis设置的内存为最大物理内存的四分之三
    - 通过命令查看
        - 在进入redis后，通过config set maxmemory 104857600设置redis最大内存，单位byte
        - 通过config get maxmemory查看当前redis设置的物理内存
    - 通过info memory命令也可以进行查看（单独使用info可以查看所有redis客户端信息）
- redis内存打满后
    - OOM command not allowed when used memory异常报错

##### redis缓存淘汰策略
- 过期key是如何被删除的？是否一过期就自动被删除？
    - 定时删除
        - 
