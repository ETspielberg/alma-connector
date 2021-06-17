package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiPrice;
import org.unidue.ub.libintel.almaconnector.repository.BubiPricesRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * offers functions around bubi prices
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class BubiPricesService {

    private final BubiPricesRepository bubiPricesRepository;

    private final Logger log = LoggerFactory.getLogger(BubiPricesService.class);

    /**
     * autobased autowiring to the bubi price repository
     * @param bubiPricesRepository the bubi price repository
     */
    BubiPricesService(BubiPricesRepository bubiPricesRepository) {
        this.bubiPricesRepository = bubiPricesRepository;
    }

    /**
     * calculates the prices for a given bubi orderline by the prices from the bubi price repository
     * @param bubiOrderLine the bubi orderline for which the price is to be calculated
     * @return the price to be paid for a given bubi orderline
     */
    public double calculatePriceForOrderline(BubiOrderLine bubiOrderLine) {
        double price = 0.0;
        String vendorAccount = bubiOrderLine.getVendorAccount();

        if (this.bubiPricesRepository.findAllByVendorAccount(vendorAccount) == null || this.bubiPricesRepository.findAllByVendorAccount(vendorAccount).size() == 0)
            this.createNewBubiPricesForVendorAccount(vendorAccount);

        // Grundpreis aus Bindung und Einband
        String bindingTypeName = String.format("%s-%s-%s", bubiOrderLine.getCover(), bubiOrderLine.getBinding().toUpperCase(), bubiOrderLine.getMediaType().toUpperCase());
        log.info("retrieving price for work " + bindingTypeName);
        BubiPrice bubiPrice = this.bubiPricesRepository.findByNameAndVendorAccount(bindingTypeName, vendorAccount);
        price += bubiPrice.getPrice();

        // ggf. Arbeitskosten
        if (bubiOrderLine.getHours() != 0.0)
            price += bubiOrderLine.getHours() * this.bubiPricesRepository.findByNameAndVendorAccount("hours", vendorAccount).getPrice();
        // ggf. Verlegerdecke
        if (bubiOrderLine.getBindPublisherSleeve())
            price += this.bubiPricesRepository.findByNameAndVendorAccount("bindPublisherSleeve", vendorAccount).getPrice();
        // ggf. Rücken überziehen
        if (bubiOrderLine.getCoverBack())
            price += this.bubiPricesRepository.findByNameAndVendorAccount("coverBack", vendorAccount).getPrice();
        // ggf. Kartentasche
        if (bubiOrderLine.getMapSlide())
            price += this.bubiPricesRepository.findByNameAndVendorAccount("mapSlide", vendorAccount).getPrice();
        //ggf. Sicherungsstreifen
        if (bubiOrderLine.getSecurityStrip())
            price += this.bubiPricesRepository.findByNameAndVendorAccount("securityStrip", vendorAccount).getPrice();
        return price;
    }

    /**
     * saves a single bubi price
     * @param bubiPrice a single bubi price
     * @return the saved bubi price
     */
    public BubiPrice saveBubiPrice(BubiPrice bubiPrice) {
        return bubiPricesRepository.save(bubiPrice);
    }

    /**
     * deletes all bubi prices by the corresponding vendor account
     * @param vendorAccount the vendor account id
     */
    public void deleteBubiPrices(String vendorAccount) {
        this.bubiPricesRepository.deleteAllByVendorAccount(vendorAccount);
    }

    /**
     * generates a set of new bubi prices for a new vendor account
     * @param vendorAccount the vendor account
     * @return a list of bubi prices created for this vendor account
     */
    public List<BubiPrice> createNewBubiPricesForVendorAccount(String vendorAccount) {
        List<BubiPrice> bubiPrices = new ArrayList<>();
        bubiPrices.add(new BubiPrice().withPrice(12.50).withName("bindPublisherSleeve").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(4.0).withName("coverBack").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(1.6).withName("mapSlide").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(0.1).withName("securityStrip").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(30.00).withName("hours").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withName("GW-K-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(6.0).withName("HPB-K-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StoPr-K-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-K-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withName("GW-F-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(6.0).withName("HPB-F-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StoPr-F-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-F-BOOK").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(14.80).withName("GW-K-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(17.50).withName("GW-F-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(6.0).withName("HPB-K-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(6.0).withName("HPB-F-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(4.50).withName("StoPr-K-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(5.50).withName("StoPr-F-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-K-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-F-JOURNAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(12.50).withName("GW-K-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(13.00).withName("GW-F-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(4.0).withName("HPB-K-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(6.0).withName("HPB-F-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StoPr-K-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StoPr-F-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-K-SERIAL").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(8.50).withName("StmPr-F-SERIAL").withVendorAccount(vendorAccount));
        this.bubiPricesRepository.saveAll(bubiPrices);
        return bubiPrices;
    }

    /**
     * retrieves all bubi prices for a given vendor account
     * @param vendorAccount the vendor account
     * @return a list of bubi prices as obtained from the repository for this vendor account.
     */
    public List<BubiPrice> getBubiPricesForVendorAccount(String vendorAccount) {
        return this.bubiPricesRepository.findAllByVendorAccount(vendorAccount);
    }
}
