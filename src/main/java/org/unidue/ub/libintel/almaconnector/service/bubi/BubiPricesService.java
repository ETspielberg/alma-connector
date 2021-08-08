package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiPricesRepository;
import org.unidue.ub.libintel.almaconnector.service.PriceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    private final BubiDataRepository bubiDataRepository;

    private Logger log = LoggerFactory.getLogger(BubiPricesService.class);

    /**
     * autobased autowiring to the bubi price repository
     * @param bubiPricesRepository the bubi price repository
     */
    BubiPricesService(BubiPricesRepository bubiPricesRepository,
                      BubiDataRepository bubiDataRepository) {
        this.bubiPricesRepository = bubiPricesRepository;
        this.bubiDataRepository = bubiDataRepository;
    }

    /**
     * calculates the prices for a given bubi orderline by the prices from the bubi price repository
     * @param bubiOrderLine the bubi orderline for which the price is to be calculated
     * @return the price to be paid for a given bubi orderline
     */
    public double calculatePriceForOrderline(BubiOrderLine bubiOrderLine) throws PriceNotFoundException {
        double price = 0.0;
        String vendorAccount = bubiOrderLine.getVendorAccount();
        Optional<BubiData> optional = this.bubiDataRepository.findById(vendorAccount);
        if (optional.isEmpty())
            throw new PriceNotFoundException("no bubi data found!");
        else {
            BubiData bubiData = optional.get();
            log.info(String.valueOf(bubiData.getBubiPrices().size()));
            if (bubiData.getBubiPrices().size() == 0)
                this.createNewBubiPricesForVendorAccount(bubiData);
            BubiPrice bubiPrice = bubiData.retrieveExecutionPrice(bubiOrderLine.getBinding(), bubiOrderLine.getCover(), bubiOrderLine.getMediaType().toUpperCase(Locale.ROOT));
            if (bubiPrice == null)
                throw new PriceNotFoundException("execution price not found");
            price += bubiPrice.getPrice();

            // ggf. Arbeitskosten
            if (bubiOrderLine.getHours() != 0.0)
                price += bubiOrderLine.getHours() * bubiData.getPricePerHour();
            // ggf. Verlegerdecke
            if (bubiOrderLine.getBindPublisherSleeve())
                price += bubiData.getPriceBindPublisherSleeve();
            // ggf. Rücken überziehen
            if (bubiOrderLine.getCoverBack())
                price += bubiData.getPriceCoverBack();
            // ggf. Kartentasche
            if (bubiOrderLine.getMapSlide())
                price += bubiData.getPriceMapSlide();
            //ggf. Sicherungsstreifen
            if (bubiOrderLine.getSecurityStrip())
                price += bubiData.getPriceSecurityStrip();
            price = price * bubiOrderLine.getBubiOrderlinePositions().size();
            return price;
        }
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
        this.bubiDataRepository.findById(vendorAccount).ifPresent(bubiData -> bubiData.getBubiPrices().forEach(this.bubiPricesRepository::delete));
    }

    /**
     * generates a set of new bubi prices for a new vendor account
     * @param bubiData the bubi data
     */
    public void createNewBubiPricesForVendorAccount(BubiData bubiData) {
        List<BubiPrice> bubiPrices = new ArrayList<>();

        // create execution prices for books
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("K").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("F").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("K").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("F").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("K").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("F").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("K").withMaterialType("BOOK").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("F").withMaterialType("BOOK").withBubiData(bubiData));

        // create execution prices for journal
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("K").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("F").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("K").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("F").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("K").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("F").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("K").withMaterialType("JOURNAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("F").withMaterialType("JOURNAL").withBubiData(bubiData));

        // create execution prices for series
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("K").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("GW").withBinding("F").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("K").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StoPr").withBinding("F").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("K").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("HPB").withBinding("F").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("K").withMaterialType("SERIAL").withBubiData(bubiData));
        bubiPrices.add(new BubiPrice().withPrice(13.0).withCover("StmPr").withBinding("F").withMaterialType("SERIAL").withBubiData(bubiData));

        this.bubiPricesRepository.saveAll(bubiPrices);
    }
}
