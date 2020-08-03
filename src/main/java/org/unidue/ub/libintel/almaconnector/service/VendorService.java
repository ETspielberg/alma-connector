package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaVendorApiClient;

@Service
public class VendorService {

    private final AlmaVendorApiClient almaVendorApiClient;

    private final Logger log = LoggerFactory.getLogger(VendorService.class);

    /**
     * constructor based autowiring of the Feign client.
     * @param almaVendorApiClient the Feign client for the Alma Vendor API
     */
    VendorService(AlmaVendorApiClient almaVendorApiClient) {
        this.almaVendorApiClient = almaVendorApiClient;
    }

    /**
     * retrieves the vendor by the vendor account code
     * @param vendorAccountCode the code for the vendor account
     * @return a vendor object
     */
    @Cacheable("vendors")
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public Vendor getVendorAccount(String vendorAccountCode) {
        try {
            Vendor vendor = this.almaVendorApiClient.getVendorsVendorCode("application/json", vendorAccountCode);
            log.info("retrieved vendor " + vendor.getCode());
            return vendor;
        } catch (FeignException fe) {
            log.warn("could not retrieve vendor " + vendorAccountCode, fe);
            return null;
        }
    }
}
