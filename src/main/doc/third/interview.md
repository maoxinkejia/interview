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
        - 当执行monitorexit时，Java虚拟机则将需将锁对象的计数器减1。计数器为零代表锁已被释放。33
    - 显式锁(即Lock)也有ReentrantLock这样的可重入锁
        - lock几次就必须unlock几次，否则会造成锁不释放
        
#### LockSupport
##### 是什么
- LockSupport是用来创建锁和其他同步类的基本线程阻塞原语。
- 使用了一种名为Permit(许可)的概念来做到阻塞和唤醒线程的功能，每个线程都有一个许可(permit),permit只有两个值1、0，默认是0
    也可以把许可看成是一种(0,1)信号量(Semaphore)，但与Semaphore不同的是，许可的累加上限是1
- java.util.concurrent.locks.LockSupport
##### 线程等待唤醒机制(wait/notify)
- 3中让线程等待和唤醒的方法
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
    
- 使用时无锁块要求，先唤醒后等待LockSupport照样支持