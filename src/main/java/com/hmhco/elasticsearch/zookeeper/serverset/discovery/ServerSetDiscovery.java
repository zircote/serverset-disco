package com.hmhco.elasticsearch.zookeeper.serverset.discovery;

import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNodeService;
import org.elasticsearch.cluster.settings.DynamicSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.discovery.DiscoverySettings;
import org.elasticsearch.discovery.zen.ZenDiscovery;
import org.elasticsearch.discovery.zen.elect.ElectMasterService;
import org.elasticsearch.discovery.zen.ping.ZenPingService;
import org.elasticsearch.node.settings.NodeSettingsService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 * User: allenr1
 * Date: 4/11/15
 * Time: 10:29 PM
 */
public class ServerSetDiscovery extends ZenDiscovery{
    public ServerSetDiscovery(Settings settings, ClusterName clusterName, ThreadPool threadPool, TransportService transportService, ClusterService clusterService, NodeSettingsService nodeSettingsService, DiscoveryNodeService discoveryNodeService, ZenPingService pingService, ElectMasterService electMasterService, DiscoverySettings discoverySettings, DynamicSettings dynamicSettings) {
        super(settings, clusterName, threadPool, transportService, clusterService, nodeSettingsService, discoveryNodeService, pingService, electMasterService, discoverySettings, dynamicSettings);
    }
}
