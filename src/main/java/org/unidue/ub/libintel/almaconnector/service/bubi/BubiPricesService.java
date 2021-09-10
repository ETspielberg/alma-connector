package org.unidue.ub.libintel.almaconnector.service.bubi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiData;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiPricesRepository;
import org.unidue.ub.libintel.almaconnector.service.PriceNotFoundException;

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
@Slf4j
public class BubiPricesService {

    private final BubiPricesRepository bubiPricesRepository;

    private final BubiDataRepository bubiDataRepository;

    /**
     * autobased autowiring to the bubi price repository
     *
     * @param bubiPricesRepository the bubi price repository
     */
    BubiPricesService(BubiPricesRepository bubiPricesRepository,
                      BubiDataRepository bubiDataRepository) {
        this.bubiPricesRepository = bubiPricesRepository;
        this.bubiDataRepository = bubiDataRepository;
    }

    /**
     * calculates the prices for a given bubi orderline by the prices from the bubi price repository
     *
     * @param bubiOrderLine the bubi orderline for which the price is to be calculated
     */
    public void calculatePriceForOrderline(BubiOrderLine bubiOrderLine) throws PriceNotFoundException {
        double price = 0.0;
        String vendorAccount = bubiOrderLine.getVendorAccount();
        log.debug(String.format("calculating prices for order %s with vendor %s", bubiOrderLine.getBubiOrderLineId(), bubiOrderLine.getVendorAccount()));
        if (vendorAccount == null) {
            bubiOrderLine.setPrice(0.0);
            return;
        }
        Optional<BubiData> optional = this.bubiDataRepository.findById(vendorAccount);
        if (optional.isEmpty())
            throw new PriceNotFoundException("no bubi data found!");
        else {
            BubiData bubiData = optional.get();
            log.debug(String.format("found bubidata with %d prices", bubiData.getBubiPrices().size()));
            // if there are the appropriate execution costs, add the corresponding price. If not, write a comment, that
            // the price is missing
            log.info(String.format("retrieving execution price for binding %s, cover %s and media type %s", bubiOrderLine.getBinding().toUpperCase(Locale.ROOT), bubiOrderLine.getCover(), bubiOrderLine.getMediaType().toUpperCase(Locale.ROOT)));
            BubiPrice bubiPrice = bubiData.retrieveExecutionPrice(bubiOrderLine.getBinding().toUpperCase(Locale.ROOT), bubiOrderLine.getCover(), bubiOrderLine.getMediaType().toUpperCase(Locale.ROOT));
            if (bubiPrice == null) {
                bubiOrderLine.addComment("Keine Preisangaben für die Ausführung in den Buchbinderdaten gefunden!\n");
            } else {
                price += bubiPrice.getPrice();
            }
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
            //ggf. Kartentasche mit Buchrückenausgleich
            if (bubiOrderLine.getMapSlideWithCorrection())
                price += bubiData.getPriceMapSlideWithCorrection();
            price = price * bubiOrderLine.getBubiOrderlinePositions().size();
            bubiOrderLine.setPrice(price);
        }
    }

    /**
     * saves a single bubi price
     *
     * @param bubiPrice a single bubi price
     * @return the saved bubi price
     */
    public BubiPrice saveBubiPrice(BubiPrice bubiPrice) {
        return bubiPricesRepository.save(bubiPrice);
    }

    /**
     * deletes all bubi prices by the corresponding vendor account
     *
     * @param vendorAccount the vendor account id
     */
    public void deleteBubiPrices(String vendorAccount) {
        this.bubiDataRepository.findById(vendorAccount).ifPresent(bubiData -> this.bubiPricesRepository.deleteAll(bubiData.getBubiPrices()));
    }
}
