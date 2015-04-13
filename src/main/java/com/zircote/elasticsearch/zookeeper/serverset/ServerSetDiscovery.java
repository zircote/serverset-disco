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
package com.zircote.elasticsearch.zookeeper.serverset;

import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNodeService;
import org.elasticsearch.cluster.settings.DynamicSettings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.discovery.DiscoverySettings;
import org.elasticsearch.discovery.zen.ZenDiscovery;
import org.elasticsearch.discovery.zen.elect.ElectMasterService;
import org.elasticsearch.discovery.zen.ping.ZenPingService;
import org.elasticsearch.node.settings.NodeSettingsService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class ServerSetDiscovery extends ZenDiscovery{
    @Inject
    public ServerSetDiscovery(Settings settings, ClusterName clusterName, ThreadPool threadPool, TransportService transportService, ClusterService clusterService, NodeSettingsService nodeSettingsService, DiscoveryNodeService discoveryNodeService, ZenPingService pingService, ElectMasterService electMasterService, DiscoverySettings discoverySettings, DynamicSettings dynamicSettings) {
        super(settings, clusterName, threadPool, transportService, clusterService, nodeSettingsService, discoveryNodeService, pingService, electMasterService, discoverySettings, dynamicSettings);
    }
}
