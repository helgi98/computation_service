package ua.edu.lnu.dcservice.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    private Properties dataSourceProperties() {
        Properties properties = new Properties();

        //is needed to connect to non local db
        properties.setProperty("ssl", "true");

        properties.setProperty("databaseName", env.getProperty("db.database.name"));
        properties.setProperty("serverName", env.getProperty("db.server.name"));
        properties.setProperty("portNumber", env.getProperty("db.port.number"));

        properties.setProperty("user", env.getProperty("db.user"));
        properties.setProperty("password", env.getProperty("db.password"));

        return properties;
    }

    private HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setDataSourceClassName(env.getProperty("data.source.class"));
        config.setMaximumPoolSize(Integer.parseInt(env.getProperty("maximum.pool.size")));
        config.setIdleTimeout(Integer.parseInt(env.getProperty("idle.timeout")));

        config.setDataSourceProperties(dataSourceProperties());

        return config;
    }
}
