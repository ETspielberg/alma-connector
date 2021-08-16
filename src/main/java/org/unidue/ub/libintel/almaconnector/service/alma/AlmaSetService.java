package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.Member;
import org.unidue.ub.alma.shared.conf.Members;
import org.unidue.ub.alma.shared.conf.Set;
import org.unidue.ub.alma.shared.conf.Sets;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;

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


    public Set retrieveSet(String setId) {
        try {
            return this.setsApiClient.getSetsBySetId(setId);
        } catch (FeignException fe) {
            log.warn("could not retreive set " + setId, fe);
            return null;
        }
    }

    public Sets getBubiSets() {
        int limit = 100;
        int offset = 0;
        Sets sets = this.setsApiClient.getConfSets("", "ITEMIZED", "name~Bubi", limit, offset, "UI");
        List<Set> allSets = sets.getSet();
        while (allSets.size() < sets.getTotalRecordCount()) {
            offset += limit;
            Sets newSets = this.setsApiClient.getConfSets("", "ITEMIZED", "name~Bubi", limit, offset, "UI");
            allSets.addAll(newSets.getSet());
        }
        sets.setSet(allSets);
        return sets;
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

    public Set createSet(Set set) {
        return this.setsApiClient.postConfSets(set, "", "", "", "", "", "");
    }

    public Set addMemberToSet(String almaItemId, String almaSetId) {
        Set set = new Set().members(new Members().addMemberItem(new Member().id(almaItemId)));
        return this.setsApiClient.postConfSetsSetId(set,almaSetId, "add_members", "" );
    }
}
