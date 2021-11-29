package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;
import org.unidue.ub.libintel.almaconnector.model.analytics.AusweisAblaufExterneReport;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;

import java.io.IOException;
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

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    @Value("${alma.set.name.benutzer.ausweisende}")
    private String AlmaSetIdBenutzerAusweisende;

    /**
     * constructor based autowiring to the sets api client and the item service
     *
     * @param setsApiClient the client for the sets api
     */
    AlmaSetService(SetsApiClient setsApiClient,
                   AlmaAnalyticsReportClient almaAnalyticsReportClient) {
        this.setsApiClient = setsApiClient;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
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
        if (almaItemId == null || almaItemId.isEmpty())
            return;
        Member member = new Member().id(almaItemId).description(itemDescription);
        Set set = new Set().members(new Members().addMemberItem(member));
        try {
            this.setsApiClient.postConfSetsSetId(set, almaSetId, "add_members", "");
        } catch (FeignException fe) {
            log.warn(String.format("could not add item to set | setId: %s, itemId: %s, message: %s", almaSetId, almaItemId, fe.getMessage()));
        }
    }

    public void addMemberListToSet(String almaSetId, List<String> almaIds, String itemDescription) {
        if (almaIds == null || almaIds.isEmpty()) return;
        Members members = new Members();
        for (String almaId : almaIds) {
            if (almaId == null || almaId.isEmpty()) continue;
            members.addMemberItem(new Member().id(almaId).description(itemDescription));
        }
        Set set = new Set().members(members);
        try {
            this.setsApiClient.postConfSetsSetId(set, almaSetId, "add_members", "");
        } catch (FeignException fe) {
            log.warn(String.format("could not add items to set setId: %s, message: %s", almaSetId, fe.getMessage()), fe);
        }
    }

    /**
     * adds all items from the positions of an orderline
     *
     * @param almaSetId     the id of the alma set
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
     *
     * @param almaSetId     the id of the alma set
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
        if (almaItemId == null || almaItemId.isEmpty())
            return;
        Member member = new Member().id(almaItemId);
        Set set = new Set().members(new Members().addMemberItem(member));
        this.setsApiClient.postConfSetsSetId(set, almaSetId, "delete_members", "");
    }

    /**
     * clears a set of all members
     * @param setId the set id of the set to be cleared
     */
    public void clearSet(String setId) {
        log.info("clearing set " + setId);
        // initialize values for member collection
        int limit = 500;
        int offset = 0;

        // retrieve total number of members
        Members members = this.setsApiClient.getConfSetsSetIdMembers(setId, "application/json", 1, offset);

        // if it is already empty, stop here
        if (members.getTotalRecordCount() == 0) return;

        // collect all members and remove them from set.
        for (offset = 0; offset < members.getTotalRecordCount(); offset += limit) {
            members = this.setsApiClient.getConfSetsSetIdMembers(setId, "application/json", limit, offset);
            try {
                this.setsApiClient.postConfSetsSetId(new Set().members(members), setId, "delete", "");
            } catch (FeignException feignException) {
                log.warn(String.format("could not clear set %s, message: %s", setId, feignException.getMessage()), feignException);
            }
        }
    }

    public void transferAusweisAblaufExterneAnalyticsReportToSet() {
        this.clearSet(AlmaSetIdBenutzerAusweisende);
        try {
            AusweisAblaufExterneReport ausweisAblaufExterneReport = this.almaAnalyticsReportClient.getReport(AusweisAblaufExterneReport.PATH, AusweisAblaufExterneReport.class);
            List<String> ids = new ArrayList<>();
            ausweisAblaufExterneReport.getRows().forEach(entry -> ids.add(entry.getPrimaryIdentifier()));
            log.info(String.format("retreived %d users, whose accout is going to expire", ids.size()));
            this.addMemberListToSet(AlmaSetIdBenutzerAusweisende, ids, "");
        } catch (IOException e) {
            log.error("could not retrieve analytics report AusweisAblaufExtern", e);
        }
    }
}
