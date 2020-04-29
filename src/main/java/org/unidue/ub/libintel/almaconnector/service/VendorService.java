package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaVendorApiClient;

@Service
public class VendorService {

    private final AlmaVendorApiClient almaVendorApiClient;

    private final Logger log = LoggerFactory.getLogger(VendorService.class);

    VendorService(AlmaVendorApiClient almaVendorApiClient) {
        this.almaVendorApiClient = almaVendorApiClient;
    }

    @Cacheable("vendors")
    public Vendor getVendorAccount(String vendorAccountCode) {
        try {
            return this.almaVendorApiClient.getVendorsVendorCode("aaplication/json", vendorAccountCode);
        } catch (FeignException fe) {
            log.warn("could not retrieve vendor " + vendorAccountCode, fe);
            return null;
        }
    }
}
