
package avs.ai;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.Member;
import avs.hazelcast.WorkLoadReturn;

// Consumer Class in Java
class Consumer implements Runnable {

    private final BlockingQueue<WorkLoadReturn> futureQueue;

    private WorkLoadReturn internalReturn = null;

    private int counter = 0;

    private AICore aiCore;

    private BlockingQueue<Callable<WorkLoadReturn>> workQueue;

    private Semaphore semaphore;

    private Member[] memberArray;

    private ISemaphore[] semaphoreArray;

    public Consumer(BlockingQueue<Callable<WorkLoadReturn>> workQueue, BlockingQueue<WorkLoadReturn> futureQueue, AICore aiCore, Semaphore semaphore) {
        this.aiCore = aiCore;
        this.futureQueue = futureQueue;
        this.workQueue = workQueue;
        this.semaphore = semaphore;

        Set<Member> members = aiCore.getMyWorker().getInstance().getCluster().getMembers();
        memberArray = new Member[members.size()];
        semaphoreArray = new ISemaphore[members.size()];
        int index = 0;
        for (Member m : members) {
            memberArray[index] = m;
            semaphoreArray[index] = aiCore.getMyWorker().getInstance().getSemaphore(m.getUuid());
            index++;
        }

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

    public synchronized void setCounter(int counter) {
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
            final int item = new Random().nextInt(memberArray.length);
            if (semaphoreArray[item].tryAcquire(30, TimeUnit.MILLISECONDS)) {
                aiCore.incrementWork();
                aiCore.getExecutorService().submitToMember(task, memberArray[item], new ExecutionCallback<WorkLoadReturn>() {

                    public void onResponse(WorkLoadReturn response) {
                        setCounter(getCounter() + response.getCounter() + 1);
                        if (null == getInternalReturn()) {
                            setInternalReturn(response);
                        }
                        if (getInternalReturn().getRating() < response.getRating()) {
                            setInternalReturn(response);
                        }
                        aiCore.incrementWorkDone();
                        semaphore.release();
                        semaphoreArray[item].release();
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
