package org.unidue.ub.libintel.almaconnector.service.bubi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Address;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiAddress;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiDataBriefDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.BubiDataFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiPricesRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaVendorService;

import java.util.ArrayList;
import java.util.List;

/**
 * offers functions around bubi data
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class BubiDataService {

    private final BubiDataRepository bubiDataRepository;

    private final BubiPricesRepository bubiPricesRepository;

    private final AlmaVendorService almaVendorService;

    /**
     * constructor based autowiring to the cbubi data repository
     *
     * @param bubiDataRepository the bubi data repository
     */
    BubiDataService(BubiDataRepository bubiDataRepository,
                    BubiPricesRepository bubiPricesRepository,
                    AlmaVendorService almaVendorService) {
        this.bubiDataRepository = bubiDataRepository;
        this.bubiPricesRepository = bubiPricesRepository;
        this.almaVendorService = almaVendorService;
    }

    /**
     * retrieves all saved bubi data
     *
     * @return a list of bubi data
     */
    public List<BubiDataBriefDto> listAllBubiData(String mode) {
        log.debug("listing all saved bubi data");
        List<BubiDataBriefDto> bubiData = new ArrayList<>();
        if ("active".equals(mode))
            this.bubiDataRepository.findByActiveOrderByName(true).forEach(entry -> bubiData.add(new BubiDataBriefDto(entry)));
        else
            this.bubiDataRepository.findAll().forEach(entry -> bubiData.add(new BubiDataBriefDto(entry)));
        return bubiData;
    }

    /**
     * retrieves the bubi data for a vendor and a given collection
     *
     * @param vendorID   the id of the vendor
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

    /**
     *
     * retrrives the data for a given bubi data id
     * @param bubiDataId the id of the given bubi
     * @return a bubi data DTO holding the full data for this bubi
     */
    public BubiDataFullDto getBubiData(String bubiDataId) {
        BubiData bubiData = this.bubiDataRepository.findById(bubiDataId).orElse(null);
        if (bubiData == null)
            return null;
        return new BubiDataFullDto(bubiData);
    }

    public BubiDataFullDto saveBubidata(BubiDataFullDto bubiDataFullDto) {
        BubiData bubiData = this.bubiDataRepository.findById(bubiDataFullDto.getVendorAccount()).orElse(null);
        if (bubiData == null)
            bubiData = new BubiData();
        bubiDataFullDto.updateBubidata(bubiData);
        for (BubiPrice price: bubiData.getBubiPrices()) {
            price.setBubiData(bubiData);
            bubiPricesRepository.save(price);
        }
        bubiData = this.bubiDataRepository.save(bubiData);
        return new BubiDataFullDto(bubiData);
    }

    public BubiDataFullDto toggleActive(String bubidataId) {
        BubiData bubiData = this.bubiDataRepository.findById(bubidataId).orElse(null);
        if (bubiData == null)
            return null;
        else {
            bubiData.setActive(!bubiData.getActive());
            bubiData = this.bubiDataRepository.save(bubiData);
            return new BubiDataFullDto(bubiData);
        }
    }

    public BubiAddress getBubiAddress(String bubiDataId) {
        BubiData bubiData = this.bubiDataRepository.findById(bubiDataId).orElse(null);
        if (bubiData == null) return null;
        Vendor vendor = this.almaVendorService.getVendorAccount(bubiDataId);
        for (Address address: vendor.getContactInfo().getAddress()) {
            if (address.getPreferred()) {
                BubiAddress bubiAddress = new BubiAddress()
                        .withCity(address.getCity())
                        .withName(vendor.getName())
                        .withCountry(address.getCountry().getDesc())
                        .withPlz(address.getPostalCode());
                if (address.getLine2() != null) {
                    bubiAddress.setAdditionaAddressLine(address.getLine1());
                    bubiAddress.setStreet(address.getLine2());
                } else
                    bubiAddress.setStreet(address.getLine1());
                return bubiAddress;
            }
        }
        return null;
    }


}
