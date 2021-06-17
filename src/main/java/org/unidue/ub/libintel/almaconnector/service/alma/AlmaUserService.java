package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.users.AlmaUserApiClient;

/**
 * offers functions around users in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class AlmaUserService {

    private final AlmaUserApiClient almaUserApiClient;

    /**
     * constructor based autowiring of the alma user api feign client
     * @param almaUserApiClient the alma user api feign client
     */
    public AlmaUserService(AlmaUserApiClient almaUserApiClient) {
        this.almaUserApiClient = almaUserApiClient;
    }

    /**
     * retreives a user by its id
     * @param userId the user id
     * @return the <class>AlmaUser</class>> object
     */
    public AlmaUser getUser(String userId) {
        return this.almaUserApiClient.getAlmaUser("application/json", userId, "full");
    }
}
