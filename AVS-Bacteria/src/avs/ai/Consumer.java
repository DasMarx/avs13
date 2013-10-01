package avs.ai;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import avs.hazelcast.WorkLoadReturn;

// Consumer Class in Java
class Consumer implements Runnable {

    private final BlockingQueue<WorkLoadReturn> futureQueue;

    private WorkLoadReturn internalReturn = null;

    private int counter = 0;

    private AtomicInteger concurrentExecution;

    private AICore aiCore;

    public Consumer(BlockingQueue<WorkLoadReturn> futureQueue2, AICore aiCore, AtomicInteger concurrentExecution) {
        this.aiCore = aiCore;
        this.futureQueue = futureQueue2;
        this.concurrentExecution = concurrentExecution;
    }

    @Override
    public void run() {
        // while (!Thread.currentThread().isInterrupted()) {
        // if (aiCore.consumerProducerStillRunning()) {
        boolean running = true;
        while (running) {
            try {
                WorkLoadReturn myReturn;
                myReturn = futureQueue.poll(100, TimeUnit.MILLISECONDS);
                if (null != myReturn) {
//                    concurrentExecution.decrementAndGet();
                    setCounter(getCounter() + myReturn.getCounter() + 1);
                    if (null == getInternalReturn()) {
                        setInternalReturn(myReturn);
                    }
                    if (getInternalReturn().getAi() < myReturn.getAi()) {
                        setInternalReturn(myReturn);
                    }
                } else {
                    if (!aiCore.ProducerStillRunning()) {
                        if (concurrentExecution.get() == 0) {
                            running = false;
                        }
                    }
                }

                // for (Workload w : myReturn.getMyWorkloadLinkedList()) {
                // newWorkQueue.add(w);
                // }
                // newWorkQueue.addAll(myReturn.getMyWorkloadLinkedList());
            } catch (InterruptedException ex) {
                running = false;
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

}