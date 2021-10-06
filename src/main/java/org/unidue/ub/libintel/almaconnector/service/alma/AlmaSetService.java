package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;

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

    /**
     * constructor based autowiring to the sets api client and the item service
     *
     * @param setsApiClient   the client for the sets api
     */
    AlmaSetService(SetsApiClient setsApiClient) {
        this.setsApiClient = setsApiClient;
    }

    /**
     * retreives the member of a set for a given id
     *
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
     *
     * @param setName        the name of the set
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
     * adds an item to an existing set
     *
     * @param almaSetId       the id of the set
     * @param almaItemId      the item pid of the item to be added
     * @param itemDescription a description for this item
     */
    public void addMemberToSet(String almaSetId, String almaItemId, String itemDescription) {
        Member member = new Member().id(almaItemId).description(itemDescription);
        Set set = new Set().members(new Members().addMemberItem(member));
        try {
            this.setsApiClient.postConfSetsSetId(set, almaSetId, "add_members", "");
        } catch (FeignException fe){
            log.warn(String.format("could not add item to set | setId: %s, itemId: %s, message: %s", almaSetId, almaItemId, fe.getMessage()));
        }
    }

    /**
     * adds all items from the positions of an orderline
     * @param almaSetId the id of the alma set
     * @param bubiOrderLine the orderline holding the positions
     */
    public void addPositionsToSet(String almaSetId, BubiOrderLine bubiOrderLine) {
        bubiOrderLine.getBubiOrderlinePositions().forEach(
                bubiOrderlinePosition -> this.addMemberToSet(almaSetId, bubiOrderlinePosition.getAlmaItemId(),
                        bubiOrderLine.getTitle())
        );
    }

    /**
     * removes all items from the positions of an orderline
     * @param almaSetId the id of the alma set
     * @param bubiOrderLine the orderline holding the positions
     */
    public void removePositionsFromSet(String almaSetId, BubiOrderLine bubiOrderLine) {
        bubiOrderLine.getBubiOrderlinePositions().forEach(
                bubiOrderlinePosition -> this.removeMemberFromSet(almaSetId, bubiOrderlinePosition.getAlmaItemId())
        );
    }

    /**
     * removes an item from the set in alma
     *
     * @param almaSetId  the id of the set holding the item to be removed
     * @param almaItemId the pid of the item to be removed
     */
    public void removeMemberFromSet(String almaSetId, String almaItemId) {
        Member member = new Member().id(almaItemId);
        Set set = new Set().members(new Members().addMemberItem(member));
        this.setsApiClient.postConfSetsSetId(set, almaSetId, "delete_members", "");
    }
}
