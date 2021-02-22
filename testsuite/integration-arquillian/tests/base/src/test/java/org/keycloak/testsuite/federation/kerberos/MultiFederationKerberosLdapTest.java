package org.keycloak.testsuite.federation.kerberos;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.federation.kerberos.CommonKerberosConfig;
import org.keycloak.models.LDAPConstants;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.storage.ldap.LDAPStorageProviderFactory;
import org.keycloak.storage.ldap.kerberos.LDAPProviderKerberosConfig;
import org.keycloak.testsuite.KerberosEmbeddedServer;
import org.keycloak.testsuite.util.KerberosRule;

import java.util.Collections;
import java.util.List;

public class MultiFederationKerberosLdapTest extends AbstractKerberosTest {
    private static final String PROVIDER_CONFIG_LOCATION = "classpath:kerberos/kerberos-ldap-connection.properties";

    @ClassRule
    public static KerberosRule kerberosRule = new KerberosRule(PROVIDER_CONFIG_LOCATION, KerberosEmbeddedServer.DEFAULT_KERBEROS_REALM);


    @Override
    protected KerberosRule getKerberosRule() {
        return kerberosRule;
    }


    @Override
    protected CommonKerberosConfig getKerberosConfig() {
        return new LDAPProviderKerberosConfig(getUserStorageConfiguration());
    }

    @Override
    protected ComponentRepresentation getUserStorageConfiguration() {
        return getUserStorageConfiguration("kerberos-ldap-main", LDAPStorageProviderFactory.PROVIDER_NAME);
    }

    /**
     * Creates second LDAPUserStorage, which is configured with higher priority than default one
     * and with UsersDN pointing to empty organizational unit
     *
     * @return - List containing preconfigures storage
     */
    @Override
    protected List<? extends ComponentRepresentation> getAdditionalComponents() {
        ComponentRepresentation comp = getUserStorageConfiguration("kerberos-ldap-empty", LDAPStorageProviderFactory.PROVIDER_NAME);
        comp.getConfig().putSingle(LDAPConstants.USERS_DN, "ou=Empty,dc=keycloak,dc=org");
        comp.getConfig().putSingle("priority", "0");
        return Collections.singletonList(comp);
    }

    @Test
    public void spnegoLoginTest() throws Exception {
        assertSuccessfulSpnegoLogin("hnelson", "hnelson", "secret");

        // Assert user was imported and hasn't any required action on him. Profile info is synced from LDAP
        assertUser("hnelson", "hnelson@keycloak.org", "Horatio", "Nelson", false);
    }
}
