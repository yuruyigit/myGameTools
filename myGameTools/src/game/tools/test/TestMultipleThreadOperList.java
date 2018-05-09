package game.tools.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author zhouzhibin
 * 
 * 为什么线程安全的Vector也不能线程安全地遍历呢？
 * 其实道理也很简单，看Vector源码可以发现它的很多方法都加上了synchronized来进行线程同步，
 * 例如add()、remove()、set()、get()，但是Vector内部的synchronized方法无法控制到遍历操作，
 * 所以即使是线程安全的Vector也无法做到线程安全地遍历。

	如果想要线程安全地遍历Vector，需要我们去手动在遍历时给Vector加上synchronized锁，
	防止遍历的同时进行remove操作。相当于校长等待老师点完学生后，再叫走学生。代码如下：
	
	<b>测试多线程操作List遍历，避免java.util.ConcurrentModificationException异常错误。</b>
 */
public class TestMultipleThreadOperList {
	public static void main(String[] args) {

	    // 初始化一个list，放入5个元素
//	    final List<Integer> list = new ArrayList<>();
	    List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>(5));
	    for(int i = 0; i < 5; i++) {
	        list.add(i);
	    }

	    // 线程一：通过Iterator遍历List
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
	            // synchronized来锁住list，remove操作会在遍历完成释放锁后进行
	            synchronized (list) 
	            {
	                for(int item : list) {
	                    System.out.println("遍历元素：" + item);
	                    // 由于程序跑的太快，这里sleep了1秒来调慢程序的运行速度
	                    try {
	                        Thread.sleep(1000);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }
	    }).start();

	    // 线程二：remove一个元素
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
	            // 由于程序跑的太快，这里sleep了1秒来调慢程序的运行速度
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }

	            list.remove(4);
	            System.out.println("list.remove(4)");
	        }
	    }).start();
	}
}
