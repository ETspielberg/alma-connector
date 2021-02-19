package org.unidue.ub.libintel.almaconnector.service;

import org.unidue.ub.libintel.almaconnector.model.SapData;

class LocalizationService {

    static SapData generateComment(SapData sapData) {
        switch (sapData.sapAccountData.getImportCheckString()) {
            case "681004002020P55300000030002":
            case "681010002020P55300000030002":
            case "681000002020P40100000130010":
            case "681002002020P55300000030002":
            case "681000002020P55300000030002":
            case "681005002020P55300000030002":
                break;
            default: {
                if ("B-HBZ".equals(sapData.vendorCode)) {
                    return sapData;
                }
                switch (sapData.sapAccountData.getLedgerAccount()) {
                    case "68100000": {
                        sapData.comment = "Monographien";
                        break;
                    }
                    case "68100200": {
                        sapData.comment = "Zeitschriften-Abo";
                        break;
                    }
                    case "68100210": {
                        sapData.comment = "Zeitschriften-Abo Verbrauch";
                        break;
                    }
                    case "68100300": {
                        sapData.comment = "Fortsetzungen";
                        break;
                    }
                    case "68100400": {
                        sapData.comment = "Elektron. Zeitschr., Kauf";
                        break;
                    }
                    case "68100500": {
                        sapData.comment = "Elektron. Zeitschr., Lizenz";
                        break;
                    }
                    case "68100600": {
                        sapData.comment = "Datenbanken, laufend/Kauf";
                        break;
                    }
                    case "68100700": {
                        sapData.comment = "Datenbanken, laufend/Lizenz";
                        break;
                    }
                    case "68100800": {
                        sapData.comment = "Datenbanken, einmalig/Kauf";
                        break;
                    }
                    case "68100900": {
                        sapData.comment = "Datenbanken, einmalig/Lizenz";
                        break;
                    }
                    case "68101000": {
                        sapData.comment = "Sonst. Non-Book-Materialien";
                        break;
                    }
                    case "68101100": {
                        sapData.comment = "Einband";
                        break;
                    }
                    case "68101200": {
                        sapData.comment = "Bestandserhaltung";
                        break;
                    }
                    case "68101900": {
                        sapData.comment = "Sonst. Literaturkosten";
                        break;
                    }
                    case "68910100": {
                        sapData.comment = "Aufwendungen f. Veroeffentlichungen";
                        break;
                    }
                    default:
                        sapData.comment = "";
                }
            }
        }
        return sapData;
    }
}
