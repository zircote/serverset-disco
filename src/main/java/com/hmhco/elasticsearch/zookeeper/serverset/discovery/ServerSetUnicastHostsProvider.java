/**
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2015 Robert Allen <zircote@gmail.com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *     use this file except in compliance with the License. You may obtain a copy of
 *     the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations under
 *     the License.
 *
 */
package com.hmhco.elasticsearch.zookeeper.serverset.discovery;

import com.google.common.collect.ImmutableSet;
import com.twitter.common.net.pool.DynamicHostSet;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.ServerSetImpl;
import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.common.zookeeper.ZooKeeperClient.Credentials;
import com.twitter.thrift.Endpoint;
import com.twitter.thrift.ServiceInstance;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.ZooDefs;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastHostsProvider;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.common.collect.Lists;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerSetUnicastHostsProvider extends AbstractComponent implements UnicastHostsProvider {

    private LinkedBlockingQueue<ImmutableSet<ServiceInstance>> serverSetBuffer;
    private DynamicHostSet.HostChangeMonitor<ServiceInstance> serverSetMonitor;
    private ArrayList<InetSocketAddress> zkEnsemble = new ArrayList<InetSocketAddress>();
    private Amount<Integer, Time> zkTimeout = Amount.of(10, Time.SECONDS);
    private Credentials zkCredentials = Credentials.NONE;
    private static final List<ACL> ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    private String SERVICE;
    private TransportService transportService;
    private Version version;

    public ServerSetUnicastHostsProvider(Settings settings, Version version, TransportService transportService) {
        super(settings);
        transportService = transportService;
        version = version;
        setUp();
        SERVICE = settings.get("discovery.serverset.serverset_path");
        try {
            ServerSetImpl client = createServerSet();
            client.monitor(serverSetMonitor);
        } catch (DynamicHostSet.MonitorException|IOException e) {
            logger.error(e.toString());
        }

    }

    private void setUp() {
        serverSetBuffer = new LinkedBlockingQueue<ImmutableSet<ServiceInstance>>();
        serverSetMonitor = new DynamicHostSet.HostChangeMonitor<ServiceInstance>() {
            public void onChange(ImmutableSet<ServiceInstance> serverSet) {
                serverSetBuffer.offer(serverSet);
            }
        };
    }

    private ServerSetImpl createServerSet() throws IOException {
        return new ServerSetImpl(createZkClient(), ACL, SERVICE);
    }

    private ZooKeeperClient createZkClient() {
        for (String i : settings.getAsArray("discovery.serverset.zk_ensemble")) {
            String parts[] = i.split(":");
            zkEnsemble.add(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
        }
        return new ZooKeeperClient(zkTimeout, zkCredentials, zkEnsemble);
    }

    public List<DiscoveryNode> buildDynamicNodes() {
        ArrayList<DiscoveryNode> discoNodes = Lists.newArrayList();
        try {
            ImmutableSet<ServiceInstance> items = ImmutableSet.copyOf(serverSetBuffer.take());

            for (ServiceInstance x : items) {
                Map<String, Endpoint> additionalEndpoints = x.getAdditionalEndpoints();
                for (Endpoint ep : additionalEndpoints.values()) {
                    String Id = "#serverset-" + ep.getHost() + ep.getPort();
                    TransportAddress[] addresses = transportService.addressesFromString(ep.getHost() + ":" + ep.getPort());
                    discoNodes.add(new DiscoveryNode(Id, addresses[0], version.minimumCompatibilityVersion()));
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return discoNodes;
    }

}
