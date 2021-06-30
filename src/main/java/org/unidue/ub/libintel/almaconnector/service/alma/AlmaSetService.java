package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.conf.Member;
import org.unidue.ub.alma.shared.conf.Members;
import org.unidue.ub.alma.shared.conf.Set;
import org.unidue.ub.libintel.almaconnector.clients.alma.conf.SetsApiClient;

@Service
public class AlmaSetService {

    private final SetsApiClient setsApiClient;

    private final Logger log = LoggerFactory.getLogger(AlmaSetService.class);

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

    public Members retrieveSetMembers(String setId) {
        try {
            return this.setsApiClient.getConfSetsSetIdMembers(setId, "application/json", 100, 0);
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
            Item item = this.almaItemService.scanInItem(member.getDescription(), ready);
            if (item.getItemData().getWorkOrderAt() != null)
                log.warn(String.format("item with barcode %s still in work order department %s", member.getDescription(), item.getItemData().getWorkOrderAt().getValue()));
        }
        return true;
    }
}
