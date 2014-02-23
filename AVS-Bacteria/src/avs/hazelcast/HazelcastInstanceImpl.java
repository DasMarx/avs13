package avs.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;


public class HazelcastInstanceImpl {
    
    private static volatile HazelcastInstance hz;
    
    public synchronized HazelcastInstance getInstance() {
        if (hz == null) {
            hz = Hazelcast.newHazelcastInstance();
        }
        return hz;
    }

}
