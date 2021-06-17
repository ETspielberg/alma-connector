package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiData;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;

import java.util.List;

/**
 * offers functions around bubi data
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class BubiDataService {

    private final BubiDataRepository bubiDataRepository;

    /**
     * constructor based autowiring to the cbubi data repository
     * @param bubiDataRepository the bubi data repository
     */
    BubiDataService(BubiDataRepository bubiDataRepository) {
        this.bubiDataRepository = bubiDataRepository;
    }

    /**
     * retrieves all saved bubi data
     * @return a list of bubi data
     */
    public List<BubiData> listAllBubiData() {
        return this.bubiDataRepository.findAll();
    }

    /**
     * retrieves all saved bubi data marked as acitve
     * @return a list of active bubi data
     */
    public List<BubiData> listActiveBubiData() {
        return this.bubiDataRepository.findByActive(true);
    }

    /**
     * retrieves the bubi data for a vendor and a given collection
     * @param vendorID the id of the vendor
     * @param collection the collection
     * @return the bubi data
     */
    public BubiData getVendorAccount(String vendorID, String collection) {
        String campus = collection.startsWith("E") ? "E0001" : "D0001";
        List<BubiData> bubiDataList;
        if (vendorID == null || vendorID.isEmpty())
            bubiDataList = this.bubiDataRepository.findByCampusAndActive(campus, true);
        else
            bubiDataList = this.bubiDataRepository.findByVendorIdAndActive(vendorID, true);
        if (bubiDataList.size() == 0)
            return new BubiData();
        else if (bubiDataList.size() == 1)
            return bubiDataList.get(0);
        else {
            for (BubiData bubiData : bubiDataList) {
                if (campus.equals(bubiData.getCampus()))
                    return bubiData;
            }
            return bubiDataList.get(0);
        }
    }
}
