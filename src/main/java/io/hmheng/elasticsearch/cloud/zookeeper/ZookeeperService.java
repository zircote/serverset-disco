package io.hmheng.elasticsearch.cloud.zookeeper;

import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Setting.Property;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;


interface ZookeeperService {
    static final String     PATH = "/discovery/example";
    Setting<Boolean> AUTO_ATTRIBUTE_SETTING = Setting.boolSetting("cloud.node.auto_attributes", false, Property.NodeScope);

    Setting<List<String>> ZOOKEEPERS_SETTING =
            Setting.listSetting("discovery.zookeeper-serverset.zookeepers", new ArrayList<>(), s -> s.toString(), Property.NodeScope);
    /**
     * discovery.zookeeper-serverset.endpoint:
     *
     */
    Setting<Settings> SERVERSET_ENDPOINT_SETTING = Setting.groupSetting("discovery.zookeeper-serverset.endpoint.", Property.NodeScope);
}
