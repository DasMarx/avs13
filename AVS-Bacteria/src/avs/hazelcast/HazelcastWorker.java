package avs.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;


public class HazelcastWorker {
    
    private static volatile HazelcastInstance hz;
    
    public synchronized HazelcastInstance getInstance() {
        if (hz == null) {
            hz = Hazelcast.newHazelcastInstance();
            final ISemaphore hazelCastSemaphore = hz.getSemaphore(
                hz.getCluster().getLocalMember().getUuid());
            hazelCastSemaphore.init(100);
        }
        return hz;
    }

}
