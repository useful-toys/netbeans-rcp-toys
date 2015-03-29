package br.com.danielferber.rcp.securitytoys.api;

import java.util.Collection;
import java.util.Set;

/**
 * Describes an user and resources granted for use.
 *
 * @author Daniel Felix Ferber
 */
public interface AuthenticatedUser {

    /**
     * @return String used as user identifier when logging in.
     */
    String getLogin();

    /**
     * @return Actual user name.
     */
    String getName();

    /**
     * @return A set of resources names the user is granted to use.
     */
    Set<String> getResources();

    /**
     * Check if the user is granted to use given resource.
     *
     * @param resouceName Resource name to query.
     * @return <code>true</code> if granted; <code>false</code> otherwise.
     */
    boolean isResourceGranted(final String resouceName);

    /**
     * Check if the user is granted to use at leat one of given resources.
     *
     * @param resourceNames A collection of resource names to query.
     * @return <code>true</code> if granted; <code>false</code> otherwise.
     */
    boolean isAnyResourceGranted(final Collection<String> resourceNames);

    /**
     * Check if the user is granted to use at leat one of given resources.
     *
     * @param resourceNames A list of resource names to query.
     * @return <code>true</code> if granted; <code>false</code> otherwise.
     */
    boolean isAnyResourceGranted(final String... resourceNames);

    /**
     * Raise an exception if user is not granted to use a given resource.
     *
     * @param resouceName Resource name to query.
     * @throws AuthorizationException if not granted.
     */
    void resourceGranted(final String resouceName) throws AuthorizationException;

}
