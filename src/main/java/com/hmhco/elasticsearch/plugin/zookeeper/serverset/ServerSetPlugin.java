package com.hmhco.elasticsearch.plugin.zookeeper.serverset;

import com.twitter.common.zookeeper.Group;
import com.twitter.common.zookeeper.ZooKeeperClient;
import org.elasticsearch.plugins.AbstractPlugin;

public class ServerSetPlugin extends AbstractPlugin {
    protected ZooKeeperClient zkClient;
    protected Group group;

    public String name() {
        return "serverset-plugin";
    }

    public String description() {
        return "Zookeeper ServerSet Plugin";
    }

}
