package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiPrice;
import org.unidue.ub.libintel.almaconnector.repository.BubiPricesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubiPricesService {

    private final BubiPricesRepository bubiPricesRepository;

    private final Logger log = LoggerFactory.getLogger(BubiPricesService.class);

    BubiPricesService(BubiPricesRepository bubiPricesRepository) {
        this.bubiPricesRepository = bubiPricesRepository;
    }

    public double calculatePriceForOrderline(BubiOrderLine bubiOrderLine) {
        double price = 0.0;
        String vendorAccount = bubiOrderLine.getVendorAccount();

        if (this.bubiPricesRepository.findAllByVendorAccount(vendorAccount) == null || this.bubiPricesRepository.findAllByVendorAccount(vendorAccount).size() == 0)
            this.createNewBubiPricesForVendorAccount(vendorAccount);

        // Grundpreis aus Bindung und Einband
        String bindingTypeName = String.format("%s-%s-%s", bubiOrderLine.getCover(), bubiOrderLine.getBinding().toUpperCase(), bubiOrderLine.getMediaType().toUpperCase());
        log.info("retrieving price for work " + bindingTypeName);
        price += this.bubiPricesRepository.findByNameAndVendorAccount(bindingTypeName, vendorAccount).getPrice();

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

    public BubiPrice saveBubiPrice(BubiPrice bubiPrice) {
        return bubiPricesRepository.save(bubiPrice);
    }

    public void deleteBubiPrices(String vendorAccount) {
        this.bubiPricesRepository.deleteAllByVendorAccount(vendorAccount);
    }

    public List<BubiPrice> createNewBubiPricesForVendorAccount(String vendorAccount) {
        List<BubiPrice> bubiPrices = new ArrayList<>();
        bubiPrices.add(new BubiPrice().withPrice(12.50).withName("bindPublisherSleeve").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(4.0).withName("coverBack").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(1.6).withName("mapSlide").withVendorAccount(vendorAccount));
        bubiPrices.add(new BubiPrice().withPrice(0.1).withName("securityStrip").withVendorAccount(vendorAccount));
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
    
    public List<BubiPrice> getBubiPricesForVendorAccount(String vendorAccount) {
        return this.bubiPricesRepository.findAllByVendorAccount(vendorAccount);
    }
}
