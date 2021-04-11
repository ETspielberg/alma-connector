package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiData;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;

import java.util.List;

@Service
public class BubiDataService {

    private final BubiDataRepository bubiDataRepository;

    BubiDataService(BubiDataRepository bubiDataRepository) {
        this.bubiDataRepository = bubiDataRepository;
    }

    public List<BubiData> listAllBubiData() {
        return this.bubiDataRepository.findAll();
    }

    public List<BubiData> listActiveBubiData() {
        return this.bubiDataRepository.findByActive(true);
    }

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
