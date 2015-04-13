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


import org.elasticsearch.discovery.zen.ZenDiscoveryModule;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.discovery.Discovery;

public class ServerSetDiscoveryModule extends ZenDiscoveryModule{
    @Inject
    public ServerSetDiscoveryModule(Settings settings) {
        if (settings.getAsBoolean("serversets.enabled", true)) {
            addUnicastHostProvider(ServerSetUnicastHostsProvider.class);
        }
    }

    @Override
    protected void bindDiscovery() {
        bind(Discovery.class).to(ServerSetDiscovery.class).asEagerSingleton();
    }
}
