
package avs.ai;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import avs.hazelcast.WorkLoadReturn;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import static avs.game.Constants.*;

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

    public Consumer(BlockingQueue<Callable<WorkLoadReturn>> workQueue, AICore aiCore, Semaphore semaphore, Member[] memberArray, Semaphore[] semaphoreArray) {
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

    public synchronized void increaseCounter(int count) {
        this.counter += count;
    }

    public synchronized void compareWorkload(WorkLoadReturn response) {
//        System.out.println("response rating for " + response.getInitialX() + " " + response.getInitialY() + " is: " + response.getRating());
        if (null == getInternalReturn()) {
            setInternalReturn(response);
        } else if (getInternalReturn().getRating() < response.getRating()) {
            setInternalReturn(response);
        }
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
        boolean sendToHazelcast = false;
        while (!sendToHazelcast) {
            final int item = r.nextInt(memberArray.length);
            if (semaphoreArray[item].tryAcquire(MAX_WAIT_TIME_FOR_SEMAPHORE_ACQUIRE, TimeUnit.MILLISECONDS)) {
//                System.out.println("Sending job to member " + memberArray[item].toString());
                try {
                    aiCore.getExecutorService().submitToMember(task, memberArray[item], new ExecutionCallback<WorkLoadReturn>() {

                        public void onResponse(WorkLoadReturn response) {
                            increaseCounter(response.getCounter() + 1);
                            compareWorkload(response);
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
                } catch (RejectedExecutionException e) {
                    semaphore.release();
                    semaphoreArray[item].release();
                } finally {
                    sendToHazelcast = true;
                }
                return true;
            }
        }
        return false;
    }

}
