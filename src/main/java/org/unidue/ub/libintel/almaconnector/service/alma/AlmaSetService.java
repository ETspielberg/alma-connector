package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * provides function for handling sets in alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class AlmaSetService {

    private final SetsApiClient setsApiClient;

    private final AlmaItemService almaItemService;

    /**
     * constructor based autowiring to the sets api client and the item service
     * @param setsApiClient the client for the sets api
     * @param almaItemService the item service
     */
    AlmaSetService(SetsApiClient setsApiClient, AlmaItemService almaItemService) {
        this.setsApiClient = setsApiClient;
        this.almaItemService = almaItemService;
    }

    /**
     * retreives the member of a set for a given id
     * @param setId the id of the set
     * @return a members object holding all members of the set
     */
    public Members retrieveSetMembers(String setId) {
        try {
            int limit = 100;
            int offset = 0;
            Members members = this.setsApiClient.getConfSetsSetIdMembers(setId, "application/json", limit, offset);
            List<Member> allMembers = members.getMember();
            while (allMembers.size() < members.getTotalRecordCount()) {
                offset += limit;
                Members newMembers = this.setsApiClient.getConfSetsSetIdMembers(setId, "application/json", limit, offset);
                allMembers.addAll(newMembers.getMember());
            }
            members.setMember(allMembers);
            return members;
        } catch (FeignException fe) {
            log.warn("could not retreive set " + setId, fe);
            return null;
        }
    }

    /**
     * creates a new set of items in alma
     * @param setName the name of the set
     * @param setDescription the description of the set
     * @return the new set
     */
    public Set createSet(String setName, String setDescription) {
        Set set = new Set()
                .name(setName)
                .description(setDescription)
                .type(new SetType().value("ITEMIZED"))
                .content(new SetContent().value("ITEM"))
                .status(new SetStatus().value("ACTIVE"));
        set.setPrivate(new SetPrivate().value("false"));
        set.setOrigin(null);
        List<Member> setMembers = new ArrayList<>();
        Members members = new Members().member(setMembers);
        set.setMembers(members);
        return this.setsApiClient.postConfSets(set, "", "", "", "", "", "");
    }

    /**
     * qdds an item to an existing set
     * @param almaSetId the id of the set
     * @param almaItemId the item pid of the item to be added
     * @param itemDescription a description for this item
     * @return the updated set
     */
    public Set addMemberToSet(String almaSetId, String almaItemId, String itemDescription) {
        Member member = new Member().id(almaItemId).description(itemDescription);
        Set set = new Set().members(new Members().addMemberItem(member));
        return this.setsApiClient.postConfSetsSetId(set,almaSetId, "add_members", "" );
    }

    /**
     * removes an item from the set in alma
     * @param almaSetId the id of the set holding the item to be removed
     * @param almaItemId the pid of the item to be removed
     * @return the updated set
     */
    public Set removeMemberFromSet(String almaSetId, String almaItemId) {
        Member member = new Member().id(almaItemId);
        Set set = new Set().members(new Members().addMemberItem(member));
        return this.setsApiClient.postConfSetsSetId(set,almaSetId, "delete_members", "" );
    }
}
