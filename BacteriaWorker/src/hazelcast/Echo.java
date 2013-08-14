
package hazelcast;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

public class Echo implements Callable<String>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -533323918071010944L;

    String input = null;

    String instanceName;

    int deepness = 1;

    public Echo(String instanceName, int deepness) {
        this.instanceName = instanceName;
        this.deepness = deepness;
    }

    public Echo(String instanceName, String input, int deepness) {
        this.instanceName = instanceName;
        this.input = input;
        this.deepness = deepness;
    }

    public String call() {
        HazelcastInstance tmpInstance = Hazelcast.getHazelcastInstanceByName(instanceName);

        StringBuilder sb = new StringBuilder();
        if (deepness > 0) {
            try {
                String myString = resultOnClusterSomewhere(tmpInstance,  input + " - deepness: " + deepness, deepness - 1);
                sb.append(myString).append(System.getProperty("line.separator"));
                myString = resultOnClusterSomewhere(tmpInstance,  input + " - deepness: " + deepness, deepness - 1);
                sb.append(myString).append(System.getProperty("line.separator"));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (deepness < 0) {
            try {
                String myString = resultOnClusterSomewhere(tmpInstance, input + " - deepness: " + deepness, deepness + 1);
                sb.append(myString).append(System.getProperty("line.separator"));
                myString = resultOnClusterSomewhere(tmpInstance, input + " - deepness: " + deepness, deepness + 1);
                sb.append(myString).append(System.getProperty("line.separator"));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        sb.append(tmpInstance.getCluster().getLocalMember().toString() + ":" + input);

        return sb.toString();
    }

    private String resultOnClusterSomewhere(HazelcastInstance myInstance, String myString, int deepness) throws InterruptedException, ExecutionException {
        IExecutorService executorService = myInstance.getExecutorService("default");
        Future<String> future = executorService.submit(new Echo(myInstance.getName(), myString, deepness));
        String echoResult = future.get();
        return echoResult;
    }

}
