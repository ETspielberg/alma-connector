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

    public BubiData getVendorAccount(String vendorID, String collection) {
        String campus = collection.startsWith("E") ? "E0001" : "D0001";
        List<BubiData> bubiData = this.bubiDataRepository.findByVendorIdAndCampus(vendorID, campus);
        if (bubiData.size() == 0)
            return new BubiData();
        else
            return bubiData.get(0);
    }
}
