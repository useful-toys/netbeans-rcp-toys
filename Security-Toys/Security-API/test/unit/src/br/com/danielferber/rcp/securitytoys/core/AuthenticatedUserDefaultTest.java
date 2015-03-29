/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.core;

import br.com.danielferber.rcp.securitytoys.api.AuthorizationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Daniel Felix Ferber
 */
public class AuthenticatedUserDefaultTest {

    public AuthenticatedUserDefaultTest() {
    }

    @Test
    public void testCheckAndLogAny1() {
        Set<String> resources = Collections.EMPTY_SET;
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        resources = Collections.EMPTY_SET;
        Assert.assertFalse(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("a"));
        Assert.assertFalse(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "b"));
        Assert.assertFalse(user.checkAndLogAny(resources));
    }

    @Test
    public void testCheckAndLogAny2() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        resources = Collections.EMPTY_SET;
        Assert.assertFalse(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("a"));
        Assert.assertTrue(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("c"));
        Assert.assertFalse(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "b"));
        Assert.assertTrue(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "c"));
        Assert.assertTrue(user.checkAndLogAny(resources));

        resources = new TreeSet<String>(Arrays.asList("c", "d"));
        Assert.assertFalse(user.checkAndLogAny(resources));

    }

    @Test
    public void testGetLogin() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        Assert.assertEquals("a", user.getLogin());
    }

    @Test
    public void testGetName() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        Assert.assertEquals("b", user.getName());
    }

    @Test
    public void testGetResources() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        Assert.assertEquals(resources, user.getResources());
    }

    @Test
    public void testIsResourceGranted1() {
        Set<String> resources = Collections.EMPTY_SET;
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        Assert.assertFalse(user.isResourceGranted("c"));
    }

    @Test
    public void testIsResourceGranted2() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        Assert.assertTrue(user.isResourceGranted("a"));
        Assert.assertTrue(user.isResourceGranted("b"));
        Assert.assertFalse(user.isResourceGranted("c"));
    }

    @Test(expected = AuthorizationException.NotAuthorized.class)
    public void testResourceGranted1() throws Exception {
        Set<String> resources = Collections.EMPTY_SET;
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        user.resourceGranted("c");
    }

    @Test()
    public void testResourceGranted2() throws Exception {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        user.resourceGranted("a");
        user.resourceGranted("b");
    }

    public void testIsAnyResourceGranted1() {
        Set<String> resources = Collections.EMPTY_SET;
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        resources = new TreeSet<String>(Arrays.asList("a"));
        Assert.assertFalse(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "b"));
        Assert.assertFalse(user.isAnyResourceGranted(resources));
    }

    @Test
    public void testIsAnyResourceGranted2() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        resources = Collections.EMPTY_SET;
        Assert.assertFalse(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("a"));
        Assert.assertTrue(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("c"));
        Assert.assertFalse(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "b"));
        Assert.assertTrue(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("a", "c"));
        Assert.assertTrue(user.isAnyResourceGranted(resources));

        resources = new TreeSet<String>(Arrays.asList("c", "d"));
        Assert.assertFalse(user.isAnyResourceGranted(resources));
    }

    @Test
    public void testIsAnyResourceGranted3() {
        Set<String> resources = Collections.EMPTY_SET;
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        String[] resources2 = new String[]{"a"};
        Assert.assertFalse(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"a", "b"};
        Assert.assertFalse(user.isAnyResourceGranted(resources2));
    }

    @Test
    public void testIsAnyResourceGranted4() {
        Set<String> resources = new TreeSet<String>(Arrays.asList("a", "b"));
        AuthenticatedUserDefault user = new AuthenticatedUserDefault("a", "b", resources);

        String[] resources2 = new String[] {};
        Assert.assertFalse(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"a"};
        Assert.assertTrue(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"c"};
        Assert.assertFalse(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"a", "b"};
        Assert.assertTrue(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"a", "c"};
        Assert.assertTrue(user.isAnyResourceGranted(resources2));

        resources2 = new String[]{"c", "d"};
        Assert.assertFalse(user.isAnyResourceGranted(resources2));
    }
}
