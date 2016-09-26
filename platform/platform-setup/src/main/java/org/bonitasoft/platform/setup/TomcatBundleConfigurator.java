package org.bonitasoft.platform.setup;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.platform.exception.PlatformException;

/**
 * @author Emmanuel Duchastenier
 */
class TomcatBundleConfigurator extends BundleConfigurator {

    private static final String TOMCAT_BACKUP_FOLDER = "tomcat-backups";

    TomcatBundleConfigurator(Path rootPath) throws PlatformException {
        super(rootPath);
    }

    @Override
    protected String getBundleName() {
        return "Tomcat";
    }

    @Override
    void configureApplicationServer() throws PlatformException {
        loadProperties();

        final String dbVendor = standardConfiguration.getDbVendor();
        final String bdmDbVendor = bdmConfiguration.getDbVendor();
        final Path setEnvUnixFile = getPath("bin/setenv.sh", true);
        final Path setEnvWindowsFile = getPath("bin/setenv.bat", true);
        final Path bonitaXmlFile = getPath("conf/Catalina/localhost/bonita.xml", true);
        final Path bitronixFile = getPath("conf/bitronix-resources.properties", true);
        final File bonitaDbDriverFile = getDriverFile(dbVendor);
        final File bdmDriverFile = getDriverFile(bdmDbVendor);

        try {
            createBackupFolderIfNecessary("setup/" + TOMCAT_BACKUP_FOLDER);

            // 1. update setenv(.sh|.bat):
            String newContent = readContentFromFile(getTemplateFolderPath("setenv.bat"));
            newContent = updateSetEnvFile(newContent, dbVendor, "sysprop.bonita.db.vendor");
            newContent = updateSetEnvFile(newContent, bdmDbVendor, "sysprop.bonita.bdm.db.vendor");
            backupAndReplaceContentIfNecessary(setEnvWindowsFile, newContent,
                    "Setting Bonita BPM internal database vendor to '" + dbVendor + "' and Business Data database vendor to '" + bdmDbVendor
                            + "' in 'setenv.bat' file");

            newContent = readContentFromFile(getTemplateFolderPath("setenv.sh"));
            newContent = updateSetEnvFile(newContent, dbVendor, "sysprop.bonita.db.vendor");
            newContent = updateSetEnvFile(newContent, bdmDbVendor, "sysprop.bonita.bdm.db.vendor");
            backupAndReplaceContentIfNecessary(setEnvUnixFile, newContent,
                    "Setting Bonita BPM internal database vendor to '" + dbVendor + "' and Business Data database vendor to '" + bdmDbVendor
                            + "' in 'setenv.sh' file");

            //2. update bonita.xml:
            newContent = readContentFromFile(getTemplateFolderPath("bonita.xml"));
            newContent = updateBonitaXmlFile(newContent, standardConfiguration, "ds1");
            newContent = updateBonitaXmlFile(newContent, bdmConfiguration, "ds2");
            backupAndReplaceContentIfNecessary(bonitaXmlFile, newContent,
                    "Configuring file 'conf/Catalina/localhost/bonita.xml' with your DB values for Bonita BPM internal database on '" + dbVendor
                            + "' and for Business Data database on '" + bdmDbVendor + "'");

            // 3. update bitronix-resources.properties
            newContent = readContentFromFile(getTemplateFolderPath("bitronix-resources.properties"));
            newContent = updateBitronixFile(newContent, standardConfiguration, "ds1");
            newContent = updateBitronixFile(newContent, bdmConfiguration, "ds2");
            backupAndReplaceContentIfNecessary(bitronixFile, newContent,
                    "Configuring file 'conf/bitronix-resources.properties' with your DB values for Bonita BPM internal database on " + dbVendor
                            + " and for Business Data database on " + bdmDbVendor);

            //4. copy the JDBC drivers:
            final Path srcDriverFile = bonitaDbDriverFile.toPath();
            final Path targetBonitaDbDriverFile = getPath("lib/bonita").resolve(srcDriverFile.getFileName());
            copyDatabaseDriversIfNecessary(srcDriverFile, targetBonitaDbDriverFile, dbVendor);
            final Path srcBdmDriverFile = bdmDriverFile.toPath();
            final Path targetBdmDriverFile = getPath("lib/bonita").resolve(srcBdmDriverFile.getFileName());
            copyDatabaseDriversIfNecessary(srcBdmDriverFile, targetBdmDriverFile, bdmDbVendor);
            LOGGER.info("Tomcat auto-configuration complete.");
        } catch (PlatformException e) {
            restorePreviousConfiguration(setEnvUnixFile, setEnvWindowsFile, bonitaXmlFile, bitronixFile);
            throw e;
        }
    }

    private String updateBitronixFile(String content, DatabaseConfiguration configuration, final String bitronixDatasourceAlias) throws PlatformException {
        Map<String, String> replacements = new HashMap<>(7);
        replacements.put("@@" + bitronixDatasourceAlias + "_driver_class_name@@", configuration.getXaDriverClassName());
        replacements.put("@@" + bitronixDatasourceAlias + "_database_connection_user@@", configuration.getDatabaseUser());
        replacements.put("@@" + bitronixDatasourceAlias + "_database_connection_password@@", configuration.getDatabasePassword());
        replacements.put("@@" + bitronixDatasourceAlias + "_database_test_query@@", configuration.getTestQuery());

        // Let's uncomment and replace dbvendor-specific values:
        if (POSTGRES.equals(configuration.getDbVendor())) {
            replacements.putAll(uncommentLineAndReplace("@@" + bitronixDatasourceAlias + "_postgres_server_name@@", configuration.getServerName()));
            replacements.putAll(uncommentLineAndReplace("@@" + bitronixDatasourceAlias + "_postgres_port_number@@", configuration.getServerPort()));
            replacements.putAll(uncommentLineAndReplace("@@" + bitronixDatasourceAlias + "_postgres_database_name@@", configuration.getDatabaseName()));
        } else {
            replacements.putAll(uncommentLineAndReplace("@@" + bitronixDatasourceAlias + "_database_connection_url@@", configuration.getUrl()));
        }

        return replaceValues(content, replacements);
    }

    private Map<String, String> uncommentLineAndReplace(String originalValue, String replacement) {
        return Collections.singletonMap("#[ ]*(.*)=" + originalValue, "$1=" + replacement);
    }

    private String updateBonitaXmlFile(String content, DatabaseConfiguration configuration, final String bitronixDatasourceAlias) throws PlatformException {
        Map<String, String> replacements = new HashMap<>(5);
        replacements.put("@@" + bitronixDatasourceAlias + ".database_connection_user@@", configuration.getDatabaseUser());
        replacements.put("@@" + bitronixDatasourceAlias + ".database_connection_password@@", configuration.getDatabasePassword());
        replacements.put("@@" + bitronixDatasourceAlias + ".driver_class_name@@", configuration.getNonXaDriverClassName());
        replacements.put("@@" + bitronixDatasourceAlias + ".database_connection_url@@", escapeXmlCharacters(configuration.getUrl()));
        replacements.put("@@" + bitronixDatasourceAlias + ".database_test_query@@", configuration.getTestQuery());
        return replaceValues(content, replacements);
    }

    private String updateSetEnvFile(String setEnvFileContent, String dbVendor, final String systemPropertyName) throws PlatformException {
        final Map<String, String> replacementMap = Collections.singletonMap("-D" + systemPropertyName + "=.*\"",
                "-D" + systemPropertyName + "=" + dbVendor + "\"");

        return replaceValues(setEnvFileContent, replacementMap);
    }

    void restorePreviousConfiguration(Path setEnvUnixFile, Path setEnvWindowsFile, Path bonitaXmlFile, Path bitronixFile) throws PlatformException {
        LOGGER.warn("Problem encountered, restoring previous configuration");
        // undo file copies:
        restoreOriginalFile(bonitaXmlFile);
        restoreOriginalFile(bitronixFile);
        restoreOriginalFile(setEnvUnixFile);
        restoreOriginalFile(setEnvWindowsFile);
    }
}
