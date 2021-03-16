package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.libintel.almaconnector.clients.users.AlmaUserApiClient;

@Service
public class AlmaUserService {

    private final AlmaUserApiClient almaUserApiClient;

    public AlmaUserService(AlmaUserApiClient almaUserApiClient) {
        this.almaUserApiClient = almaUserApiClient;
    }

    public AlmaUser getBriefUser(String userId) {
        return this.almaUserApiClient.getAlmaUser("application/json", userId, "brief");
    }

    public AlmaUser getUser(String userId) {
        return this.almaUserApiClient.getAlmaUser("application/json", userId, "full");
    }
}
