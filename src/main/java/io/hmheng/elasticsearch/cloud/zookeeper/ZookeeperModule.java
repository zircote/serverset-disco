package io.hmheng.elasticsearch.cloud.zookeeper;

import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

import java.util.Collection;
import java.util.Map;


public class ZookeeperModule extends AbstractModule {
    protected static ZookeeperImpl zookeeperImpl;


    private String project_znode;
    private String discovery_endpoint;
    private static Logger logger = Loggers.getLogger(ZookeeperModule.class)
    public static ZookeeperImpl getZookeeperImpl() {
        return zookeeperImpl;
    }

    @Override
    protected void configure() {
        bind(ZookeeperService.class).to(ZookeeperImpl.class).asEagerSingleton();
    }

    public ZookeeperModule() {
        super();

        project_znode = ZookeeperInstancesService.PROJECT_ZNODE.get(settings);
        discovery_endpoint = ZookeeperInstancesService.DISCOVERY_ENDPOINT.get(settings);
        CuratorFramework client = null;
        ServiceDiscovery<InstanceDetails> serviceDiscovery = null;
        Map<String, ServiceProvider<InstanceDetails>> providers = Maps.newHashMap();
        try {

            client = CuratorFrameworkFactory.newClient(this.project_znode, new ExponentialBackoffRetry(1000, 3));
            client.start();
            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
            serviceDiscovery = ServiceDiscoveryBuilder
                    .builder(InstanceDetails.class)
                    .client(client).basePath(project_znode)
                    .serializer(serializer).build();
            try {
                serviceDiscovery.start();
            } catch (Exception e) {

            }
        } finally {
            for (ServiceProvider<InstanceDetails> cache : providers.values()) {
                CloseableUtils.closeQuietly(cache);
            }

            CloseableUtils.closeQuietly(serviceDiscovery);
            CloseableUtils.closeQuietly(client);
        }
    }

    private static void listInstances(ServiceDiscovery<InstanceDetails> serviceDiscovery) throws Exception {

        try {
            Collection<String> serviceNames = serviceDiscovery.queryForNames();
            logger.info(serviceNames.size() + " type(s)");
            for (String serviceName : serviceNames) {
                Collection<ServiceInstance<InstanceDetails>> instances = serviceDiscovery.queryForInstances(serviceName);
                logger.info(serviceName);
                for (ServiceInstance<InstanceDetails> instance : instances) {
                    outputInstance(instance);
                }
            }
        } finally {
            CloseableUtils.closeQuietly(serviceDiscovery);
        }
    }

    private static void outputInstance(ServiceInstance<InstanceDetails> instance) {
        logger.info(instance.getPayload().getDescription() + ": " + instance.buildUriSpec());
    }
}