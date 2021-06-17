package org.unidue.ub.libintel.almaconnector.service.alma;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaVendorApiClient;

/**
 * offers functions around vendors in Alma
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class AlmaVendorService {

    private final AlmaVendorApiClient almaVendorApiClient;

    private final Logger log = LoggerFactory.getLogger(AlmaVendorService.class);

    /**
     * constructor based autowiring of the alma vendor api feign client.
     * @param almaVendorApiClient the alma vendor api feign client
     */
    AlmaVendorService(AlmaVendorApiClient almaVendorApiClient) {
        this.almaVendorApiClient = almaVendorApiClient;
    }

    /**
     * retrieves the vendor by the vendor account code
     * @param vendorAccountCode the code for the vendor account
     * @return a vendor object
     */
    @Cacheable("vendors")
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP", "ROLE_ALMA_Invoice Operator Extended" })
    public Vendor getVendorAccount(String vendorAccountCode) {
        try {
            Vendor vendor = this.almaVendorApiClient.getVendorsVendorCode("application/json", vendorAccountCode);
            log.debug("retrieved vendor " + vendor.getCode());
            return vendor;
        } catch (FeignException fe) {
            log.warn("could not retrieve vendor " + vendorAccountCode, fe);
            return null;
        }
    }
}
