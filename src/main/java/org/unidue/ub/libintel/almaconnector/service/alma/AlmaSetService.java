package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AlmaSetService {

    private final SetsApiClient setsApiClient;

    private final AlmaItemService almaItemService;

    AlmaSetService(SetsApiClient setsApiClient, AlmaItemService almaItemService) {
        this.setsApiClient = setsApiClient;
        this.almaItemService = almaItemService;
    }

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

    public boolean scanInSet(String setId, boolean ready) {
        Members members = retrieveSetMembers(setId);
        if (members.getTotalRecordCount() == 0)
            return false;
        for (Member member: members.getMember()) {
            Item item = this.almaItemService.scanInItem(member.getId(), ready);
            if (item.getItemData().getWorkOrderAt() != null)
                log.warn(String.format("item with barcode %s still in work order department %s", member.getDescription(), item.getItemData().getWorkOrderAt().getValue()));
        }
        return true;
    }

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

    public Set addMemberToSet(String almaSetId, String almaItemId, String itemDescription) {
        Member member = new Member().id(almaItemId).description(itemDescription);
        Set set = new Set().members(new Members().addMemberItem(member));
        return this.setsApiClient.postConfSetsSetId(set,almaSetId, "add_members", "" );
    }

    public Set removeMemberFromSet(String almaSetId, String almaItemId) {
        Member member = new Member().id(almaItemId);
        Set set = new Set().members(new Members().addMemberItem(member));
        return this.setsApiClient.postConfSetsSetId(set,almaSetId, "delete_members", "" );
    }
}
