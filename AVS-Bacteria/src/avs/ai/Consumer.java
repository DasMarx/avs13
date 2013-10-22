
package avs.ai;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import avs.hazelcast.WorkLoadReturn;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;

// Consumer Class in Java
class Consumer implements Runnable {

    private WorkLoadReturn internalReturn = null;

    private int counter = 0;

    private AICore aiCore;

    private BlockingQueue<Callable<WorkLoadReturn>> workQueue;

    private Semaphore semaphore;

    private Member[] memberArray;

    private final Semaphore[] semaphoreArray;
    
    final Random r = new Random();

    public Consumer(BlockingQueue<Callable<WorkLoadReturn>> workQueue, AICore aiCore, Semaphore semaphore,Member[] memberArray, Semaphore[] semaphoreArray) {
        this.aiCore = aiCore;
        this.workQueue = workQueue;
        this.semaphore = semaphore;
        this.memberArray = memberArray;
        this.semaphoreArray = semaphoreArray;
        
       

    }

    @Override
    public void run() {
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Thread.currentThread().setName("AVS:Consumer Thread");
        boolean running = true;
        while (running) {
            try {
                if (!sendToQueue(workQueue.poll(1, TimeUnit.MILLISECONDS)) && !aiCore.ProducerStillRunning()) {
                    if (workQueue.size() == 0) {
                        running = false;
                    }
                }
            } catch (InterruptedException e) {
                running = false;
                e.printStackTrace();

            }

        }

    }

    public WorkLoadReturn getInternalReturn() {
        return internalReturn;
    }

    public void setInternalReturn(WorkLoadReturn internalReturn) {
        this.internalReturn = internalReturn;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * @param myCallback
     * @param task
     * @throws InterruptedException
     */
    private boolean sendToQueue(Callable<WorkLoadReturn> task) throws InterruptedException {
        if (null == task) {
            return false;
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) {
            final int item = r.nextInt(memberArray.length);
            if (semaphoreArray[item].availablePermits() > 0 && semaphoreArray[item].tryAcquire(100,TimeUnit.MILLISECONDS)) {
                
                aiCore.getExecutorService().submitToMember(task, memberArray[item], new ExecutionCallback<WorkLoadReturn>() {

                    public void onResponse(WorkLoadReturn response) {
                        setCounter(getCounter() + response.getCounter() + 1);
                        if (null == getInternalReturn()) {
                            setInternalReturn(response);
                        }
                        if (getInternalReturn().getRating() < response.getRating()) {
                            setInternalReturn(response);
                        }
                        semaphore.release();
                        semaphoreArray[item].release();
                        aiCore.incrementWorkDone();
                    }

                    public void onFailure(Throwable t) {
                        semaphore.release();
                        semaphoreArray[item].release();
                        t.printStackTrace();
                    }
                });
                return true;
            }

        }

    }

}
