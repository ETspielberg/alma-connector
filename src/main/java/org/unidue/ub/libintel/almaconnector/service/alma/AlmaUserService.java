package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.alma.shared.user.UserNote;
import org.unidue.ub.alma.shared.user.UserNoteNoteType;
import org.unidue.ub.libintel.almaconnector.clients.alma.users.AlmaUserApiClient;

import java.util.List;

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
     * retreives a user by its id
     *
     * @param userId the user id
     * @return the <class>AlmaUser</class>> object
     */
    public AlmaUser getUser(String userId) {
        return this.almaUserApiClient.getAlmaUser("application/json", userId, "full");
    }

    public AlmaUser updateUser(AlmaUser almaUser) {
        return this.almaUserApiClient.putAlmaUsersUserId(almaUser, "application/json", almaUser.getPrimaryId(),"", "", "" );
    }

    public void setExpireyNote(List<String> ids) {
        for (String userId : ids) {
            AlmaUser almaUser = this.getUser(userId);
            UserNote userNote = new UserNote()
                    .popupNote(true)
                    .userViewable(true)
                    .noteType(new UserNoteNoteType().value("POPUP"));
            if ("en".equals(almaUser.getPreferredLanguage().getValue()))
                userNote.setNoteText("Your limited account expires soon.");
            else
                userNote.setNoteText("Ihr befristeter Ausweis endet in Kürze.");
            almaUser.getUserNote().add(userNote);
            this.updateUser(almaUser);
        }
    }
}
