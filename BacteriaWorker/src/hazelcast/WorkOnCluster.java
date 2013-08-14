package hazelcast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;


public class WorkOnCluster {
    public static String resultOnClusterSomewhere(HazelcastInstance myInstance, String myString) throws InterruptedException, ExecutionException {
        IExecutorService executorService = myInstance.getExecutorService("default");
        Future<String> future = executorService.submit(new Echo(myInstance.getName(), myString, 5));
        String echoResult = future.get();
        return echoResult;
    }
}
