package io.hmheng.elasticsearch.cloud.zookeeper;

import org.apache.curator.x.discovery.ServiceInstance;
import org.elasticsearch.common.component.Lifecycle;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.component.LifecycleListener;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Setting.Property;
import org.elasticsearch.common.unit.TimeValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


public interface ZookeeperInstancesService extends LifecycleComponent {
    String VERSION = "Elasticsearch/Zookeeper/1.0";


    /**
     * cloud.zookeeper.zk_ensemble: host:port[,host:port]...
     */
    Setting<String> ZK_ENSEMBLE = Setting.simpleString("cloud.zookeeper.zk_ensemble", Property.NodeScope);
    /**
     * Return a collection of running instances within the same GCE project
     * @return a collection of running instances within the same GCE project
     */
    public Collection<ServiceInstance<InstanceDetails>> getInstances();
}
