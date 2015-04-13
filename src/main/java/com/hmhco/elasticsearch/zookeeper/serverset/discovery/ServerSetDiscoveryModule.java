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
