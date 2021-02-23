package org.keycloak.storage.ldap;

import org.junit.Test;
import org.keycloak.common.constants.KerberosConstants;
import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ldap.kerberos.LDAPProviderKerberosConfig;

import static org.junit.Assert.*;

public class LDAPStorageProviderTest {

    @Test
    public void stateKeyShouldBeEqualForSameKerberosConfiguration() {
        LDAPProviderKerberosConfig config = createKerberosConfig("sample-principal", "sample-keytab");

        String key1 = LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config);
        String key2= LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config);

        assertEquals(key1, key2);
    }

    @Test
    public void stateKeyShouldNotBeEqualForDifferentServerPrincipalConfiguration() {
        LDAPProviderKerberosConfig config1 = createKerberosConfig("sample-principal", "sample-keytab");
        LDAPProviderKerberosConfig config2 = createKerberosConfig("other-principal", "sample-keytab");

        String key1 = LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config1);
        String key2= LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config2);

        assertNotEquals(key1, key2);
    }

    @Test
    public void stateKeyShouldNotBeEqualForDifferentKeytabConfiguration() {
        LDAPProviderKerberosConfig config1 = createKerberosConfig("sample-principal", "sample-keytab");
        LDAPProviderKerberosConfig config2 = createKerberosConfig("sample-principal", "other-keytab");

        String key1 = LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config1);
        String key2= LDAPStorageProvider.getPartiallyAuthenticatedUserStateKey(config2);

        assertNotEquals(key1, key2);
    }

    private LDAPProviderKerberosConfig createKerberosConfig(String serverPrincipal, String keytab) {
        ComponentModel model = new ComponentModel();
        model.put(KerberosConstants.SERVER_PRINCIPAL, serverPrincipal);
        model.put(KerberosConstants.KEYTAB, keytab);
        return new LDAPProviderKerberosConfig(model);
    }

}