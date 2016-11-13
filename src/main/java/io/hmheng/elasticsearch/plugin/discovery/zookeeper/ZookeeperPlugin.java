package io.hmheng.elasticsearch.plugin.discovery.zookeeper;



import io.hmheng.elasticsearch.cloud.zookeeper.ZookeeperInstancesService;
import io.hmheng.elasticsearch.cloud.zookeeper.ZookeeperModule;
import io.hmheng.elasticsearch.discovery.zookeeper.ZookeeperUnicastHostsProvider;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.discovery.DiscoveryModule;
import org.elasticsearch.discovery.zen.ZenDiscovery;
import org.elasticsearch.plugins.DiscoveryPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ZookeeperPlugin extends Plugin implements DiscoveryPlugin {
    protected String ZOOKEEPER = "zookeeper";
    protected final Logger logger = Loggers.getLogger(ZookeeperPlugin.class);
    private final Settings settings;
    public ZookeeperPlugin(Settings settings) {
        this.settings = settings;
        logger.trace("starting " + ZOOKEEPER + " discovery plugin...");
    }


    @Override
    public Collection<Module> createGuiceModules() {
        return Collections.singletonList(new ZookeeperModule());
    }

    public void onModule(DiscoveryModule discoveryModule) {
        logger.debug("Register " + ZOOKEEPER + " discovery type and " + ZOOKEEPER + " unicast provider");
        discoveryModule.addDiscoveryType(ZOOKEEPER, ZenDiscovery.class);
        discoveryModule.addUnicastHostProvider(ZOOKEEPER, ZookeeperUnicastHostsProvider.class);
    }
    @Override
    @SuppressWarnings("rawtypes") // Supertype uses raw type
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        logger.debug("Register gce compute service");
        Collection<Class<? extends LifecycleComponent>> services = new ArrayList<>();
        services.add(ZookeeperModule.getZookeeperImpl());
        return services;
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(
                // Register ZOOKEEPER settings
                ZookeeperInstancesService.ZK_ENSEMBLE
//                ZookeeperInstancesService.ZONE_SETTING,
//                ZookeeperUnicastHostsProvider.TAGS_SETTING,
//                ZookeeperInstancesService.REFRESH_SETTING,
//                ZookeeperInstancesService.RETRY_SETTING,
//                ZookeeperInstancesService.MAX_WAIT_SETTING
        );
    }
}
