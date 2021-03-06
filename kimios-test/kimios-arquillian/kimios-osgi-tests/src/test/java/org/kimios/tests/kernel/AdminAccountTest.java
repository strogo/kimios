package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.TestAbstract;
import org.kimios.tests.deployments.OsgiDeployment;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AdminAccountTest extends KernelTestAbstract {

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        return OsgiDeployment.createArchive(AdminAccountTest.class.getSimpleName() + ".jar", null, AdminAccountTest.class);
    }

    @Before
    public void setUp() {
        this.init();

        this.setAdminSession(this.getSecurityController().startSession(TestAbstract.ADMIN_LOGIN, TestAbstract.ADMIN_SOURCE, TestAbstract.ADMIN_PWD));
    }

    @Test
    public void testStartSession() throws Exception {
        try {
            assertNotNull("Session is not null", this.getAdminSession());
            assertTrue("sessionId length > 0", this.getAdminSession().getUid().length() > 0);
        } catch (DataSourceException e) {
            e.printStackTrace();
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAdminStuff() throws Exception {
        boolean canCreateWorkspace = this.getSecurityController().canCreateWorkspace(this.getAdminSession());
        assertTrue(canCreateWorkspace);



//        boolean canRead = this.getSecurityController().canRead(this.adminSession, 0);
//        assertTrue(canRead);

        boolean hasStudioAccess = this.getSecurityController().hasStudioAccess(this.getAdminSession());
        assertTrue(hasStudioAccess);

        boolean hasReportingAccess = this.getSecurityController().hasReportingAccess(this.getAdminSession());
        assertFalse(hasReportingAccess);

        boolean isAdmin = this.getSecurityController().isAdmin(this.getAdminSession());
        assertTrue(isAdmin);

        boolean isAdmin2 = this.getSecurityController().isAdmin(TestAbstract.ADMIN_LOGIN, TestAbstract.ADMIN_SOURCE);
        assertTrue(isAdmin2);

        boolean isSessionAlive = this.getSecurityController().isSessionAlive(getAdminSession().getUid());
        assertTrue(isSessionAlive);

    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = this.getSecurityController().getUsers(TestAbstract.ADMIN_SOURCE);
        assertTrue("We have users, at least one, the default user", users.size() > 0);

        // admin is in users list
        boolean adminExists = false;
        for (User user : users) {
            if (user.getUid().equals(TestAbstract.ADMIN_LOGIN)) {
                adminExists = true;
                continue;
            }
        }
        assertTrue("admin user exists in data source", adminExists);
    }

    @Test
    public void testBundleContextInjection() throws Exception {
        assertNotNull("BundleContext injected", context);
        long bundleId = context.getBundle().getBundleId();
        assertEquals("System Bundle ID", 0, bundleId);
    }

    @Test
    public void testBundleInjection(@ArquillianResource Bundle bundle) throws Exception {
        // Assert that the bundle is injected
        assertNotNull("Bundle injected", bundle);

        // Assert that the bundle is in state RESOLVED
        // Note when the test bundle contains the test case it
        // must be resolved already when this test method is called
        assertEquals("Bundle RESOLVED", Bundle.RESOLVED, bundle.getState());

        // Start the bundle
        bundle.start();
        assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());

        // Assert the bundle context
        BundleContext context = bundle.getBundleContext();
        assertNotNull("BundleContext available", context);

        // Stop the bundle
        bundle.stop();
        assertEquals("Bundle RESOLVED", Bundle.RESOLVED, bundle.getState());

        // Start the bundle
        bundle.start();
        assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());
    }

}