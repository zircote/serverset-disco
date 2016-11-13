package io.hmheng.elasticsearch.discovery.zookeeper;

import io.hmheng.elasticsearch.cloud.zookeeper.InstanceDetails;
import io.hmheng.elasticsearch.cloud.zookeeper.ZookeeperInstancesService;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.util.Supplier;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkAddress;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastHostsProvider;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;


public class ZookeeperUnicastHostsProvider extends AbstractComponent implements UnicastHostsProvider {
    @Inject

    protected ZookeeperInstancesService zookeeperInstancesService;
    private TransportService transportService;
    private NetworkService networkService;

    private final TimeValue refreshInterval = TimeValue.ZERO;
    private long lastRefresh;
    private List<DiscoveryNode> cachedDiscoNodes;
    private String project_znode = "/discovery/elasticsearch";
    private String discovery_endpoint = "default";


    public ZookeeperUnicastHostsProvider(Settings settings, ZookeeperInstancesService zookeeperInstancesService,
                                         TransportService transportService,
                                         NetworkService networkService) {
        super(settings);
        this.zookeeperInstancesService = zookeeperInstancesService;
        this.transportService = transportService;
        this.networkService = networkService;

        this.refreshInterval = ZookeeperInstancesService.REFRESH_SETTING.get(settings);
        this.project_znode = ZookeeperInstancesService.PROJECT_ZNODE.get(settings);
        this.discovery_endpoint = ZookeeperInstancesService.DISCOVERY_ENDPOINT.get(settings);

    }


    /**
     * Builds the dynamic list of unicast hosts to be used for unicast discovery.
     */
    @Override
    public List<DiscoveryNode> buildDynamicNodes() {

        // We check that needed properties have been set
        if (this.project_znode == null || this.discovery_endpoint.isEmpty()) {
            throw new IllegalArgumentException("one or more gce discovery settings are missing. " +
                    "Check elasticsearch.yml file. Should have [" + ZookeeperInstancesService.PROJECT_ZNODE.getKey() +
                    "] and [" + ZookeeperInstancesService.PROJECT_ZNODE.getKey() + "].");
        }

        if (refreshInterval.millis() != 0) {
            if (cachedDiscoNodes != null &&
                    (refreshInterval.millis() < 0 || (System.currentTimeMillis() - lastRefresh) < refreshInterval.millis())) {
                if (logger.isTraceEnabled()) logger.trace("using cache to retrieve node list");
                return cachedDiscoNodes;
            }
            lastRefresh = System.currentTimeMillis();
        }

        cachedDiscoNodes = new ArrayList<>();
        String ipAddress = null;
        try {
            InetAddress inetAddress = networkService.resolvePublishHostAddresses(null);
            if (inetAddress != null) {
                ipAddress = NetworkAddress.format(inetAddress);
            }
        } catch (IOException e) {
            // We can't find the publish host address... Hmmm. Too bad :-(
            // We won't simply filter it
        }

        try {
            Collection<InstanceDetails> instances = zookeeperInstancesService.getInstances();

            if (instances == null) {
                logger.trace("no instance found for project [{}], zones [{}].", this.project_znode, this.discovery_endpoint);
                return cachedDiscoNodes;
            }

            for (InstanceDetails instance : instances) {
                String name = instance.getName();
                String type = instance.getMachineType();

                String status = instance.getStatus();

                // We don't want to connect to TERMINATED status instances
                // See https://github.com/elastic/elasticsearch-cloud-gce/issues/3
                if (Status.TERMINATED.equals(status)) {
                    logger.debug("node {} is TERMINATED. Ignoring", name);
                    continue;
                }



                String ip_public = null;
                String ip_private = null;


                try {
                        String address = ip_private;
                        // Test if we have es_port metadata defined here
                        address = address.concat(":").concat((String) port);

                        // ip_private is a single IP Address. We need to build a TransportAddress from it
                        // If user has set `es_port` metadata, we don't need to ping all ports
                        // we only limit to 1 addresses, makes no sense to ping 100 ports
                        TransportAddress[] addresses = transportService.addressesFromString(address, 1);

                        for (TransportAddress transportAddress : addresses) {
                            logger.trace("adding {}, type {}, address {}, transport_address {}, status {}", name, type,
                                    ip_private, transportAddress, status);
                            cachedDiscoNodes.add(new DiscoveryNode("#cloud-" + name + "-" + 0, transportAddress,
                                    emptyMap(), emptySet(), Version.CURRENT.minimumCompatibilityVersion()));
                        }
                } catch (Exception e) {
                    final String finalIpPrivate = ip_private;
                    logger.warn((Supplier<?>) () -> new ParameterizedMessage("failed to add {}, address {}", name, finalIpPrivate), e);
                }

            }
        } catch (Exception e) {
            logger.warn("exception caught during discovery", e);
        }

        logger.debug("{} node(s) added", cachedDiscoNodes.size());
        logger.debug("using dynamic discovery nodes {}", cachedDiscoNodes);

        return cachedDiscoNodes;
    }
}
