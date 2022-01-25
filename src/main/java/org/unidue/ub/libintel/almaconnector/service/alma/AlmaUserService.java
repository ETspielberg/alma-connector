package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.alma.users.AlmaUserApiClient;

/**
 * offers functions around users in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaUserService {

    private final AlmaUserApiClient almaUserApiClient;

    /**
     * constructor based autowiring of the alma user api feign client
     *
     * @param almaUserApiClient the alma user api feign client
     */
    public AlmaUserService(AlmaUserApiClient almaUserApiClient) {
        this.almaUserApiClient = almaUserApiClient;
    }

    /**
     * retrieves a user by its id
     *
     * @param userId the user id
     * @return the <class>AlmaUser</class>> object
     */
    public AlmaUser getUser(String userId) {
        try {
            return this.almaUserApiClient.getAlmaUser(userId, "full");
        } catch (FeignException fe) {
            log.warn(String.format("could not retrieve alma user %s: %s", userId, fe.getMessage()), fe);
            return null;
        }
    }
}
