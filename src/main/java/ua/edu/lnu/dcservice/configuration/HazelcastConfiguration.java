package ua.edu.lnu.dcservice.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    @Value("${hazelcast.group.name}")
    private String groupName;
    @Value("${hazelcast.group.password}")
    private String groupPassword;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        val groupConfig = new GroupConfig();
        groupConfig.setName(groupName);
        groupConfig.setPassword(groupPassword);

        val hazelcastNetworkConfig = new NetworkConfig();
        hazelcastNetworkConfig.setPort(5701);
        hazelcastNetworkConfig.setPortAutoIncrement(true);

        val hazelcastConfig = new Config();
        hazelcastConfig.setGroupConfig(groupConfig);
        hazelcastConfig.setNetworkConfig(hazelcastNetworkConfig);

        return Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
}
