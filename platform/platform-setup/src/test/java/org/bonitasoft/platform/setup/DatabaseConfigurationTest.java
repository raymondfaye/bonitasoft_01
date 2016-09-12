package org.bonitasoft.platform.setup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

/**
 * @author Emmanuel Duchastenier
 */
public class DatabaseConfigurationTest {

    @Rule
    public TestRule clean = new RestoreSystemProperties();

    @Test
    public void bonita_database_values_can_be_overridden_by_system_properties() throws Exception {
        // given:
        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/database.properties"));
        properties.load(this.getClass().getResourceAsStream("/internal.properties"));

        System.setProperty("db.vendor", "mysql");
        System.setProperty("db.server.name", "postgresServer");
        System.setProperty("db.server.port", "3333");
        System.setProperty("db.database.name", "bonita_database");
        System.setProperty("db.user", "root");
        System.setProperty("db.password", "secret");

        // when:
        final DatabaseConfiguration bdmConfig = new DatabaseConfiguration("", properties, null);

        // then:
        assertThat(bdmConfig.getDbVendor()).isEqualTo("mysql");
        assertThat(bdmConfig.getServerName()).isEqualTo("postgresServer");
        assertThat(bdmConfig.getServerPort()).isEqualTo("3333");
        assertThat(bdmConfig.getDatabaseName()).isEqualTo("bonita_database");
        assertThat(bdmConfig.getDatabaseUser()).isEqualTo("root");
        assertThat(bdmConfig.getDatabasePassword()).isEqualTo("secret");
    }

    @Test
    public void bdm_database_values_can_be_overridden_by_system_properties() throws Exception {
        // given:
        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/internal.properties"));

        System.setProperty("bdm.db.vendor", "postgres");
        System.setProperty("bdm.db.server.name", "myServer");
        System.setProperty("bdm.db.server.port", "1111");
        System.setProperty("bdm.db.database.name", "internal_database");
        System.setProperty("bdm.db.user", "_user_");
        System.setProperty("bdm.db.password", "_pwd_");

        // when:
        final DatabaseConfiguration bdmConfig = new DatabaseConfiguration("bdm.", properties, null);

        // then:
        assertThat(bdmConfig.getDbVendor()).isEqualTo("postgres");
        assertThat(bdmConfig.getServerName()).isEqualTo("myServer");
        assertThat(bdmConfig.getServerPort()).isEqualTo("1111");
        assertThat(bdmConfig.getDatabaseName()).isEqualTo("internal_database");
        assertThat(bdmConfig.getDatabaseUser()).isEqualTo("_user_");
        assertThat(bdmConfig.getDatabasePassword()).isEqualTo("_pwd_");
    }

}
