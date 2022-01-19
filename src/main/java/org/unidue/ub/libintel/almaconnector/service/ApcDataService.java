package org.unidue.ub.libintel.almaconnector.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.analytics.AlmaAnalyticsReportClient;
import org.unidue.ub.libintel.almaconnector.model.analytics.ApcData;
import org.unidue.ub.libintel.almaconnector.model.analytics.ApcReport;
import org.unidue.ub.libintel.almaconnector.model.openaccess.ApcStatistics;
import org.unidue.ub.libintel.almaconnector.model.openaccess.JournalApcDataDto;
import org.unidue.ub.libintel.almaconnector.repository.jpa.ApcStatisticsRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaCatalogService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaElectronicService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ApcDataService {

    private final ApcStatisticsRepository apcStatisticsRepository;

    private final AlmaAnalyticsReportClient almaAnalyticsReportClient;

    private final AlmaCatalogService almaCatalogService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaElectronicService almaElectronicService;

    ApcDataService(ApcStatisticsRepository apcStatisticsRepository,
                   AlmaAnalyticsReportClient almaAnalyticsReportClient,
                   AlmaCatalogService almaCatalogService,
                   AlmaPoLineService almaPoLineService,
                   AlmaElectronicService almaElectronicService) {
        this.almaCatalogService = almaCatalogService;
        this.almaAnalyticsReportClient = almaAnalyticsReportClient;
        this.apcStatisticsRepository = apcStatisticsRepository;
        this.almaPoLineService = almaPoLineService;
        this.almaElectronicService = almaElectronicService;
    }

    public void saveApcReports() {
        try {
            ApcReport apcReport = this.almaAnalyticsReportClient.getLongReport(ApcReport.PATH, ApcReport.class, "");
            log.debug("number of rows:" + apcReport.getRows().size() + ",  isfinished: " + apcReport.isFinished() + ", resumptionToken: " + apcReport.getResumptionToken());
            List<ApcData> apcDataList = new ArrayList<>(apcReport.getRows());
            while (!apcReport.isFinished() && apcReport.getResumptionToken() != null && !apcReport.getResumptionToken().isEmpty()) {
                apcReport = this.almaAnalyticsReportClient.getLongReport(ApcReport.PATH, ApcReport.class, apcReport.getResumptionToken());
                log.debug("number of rows:" + apcReport.getRows().size() + ",  isfinished: " + apcReport.isFinished() + ", resumptionToken: " + apcReport.getResumptionToken());
                apcDataList.addAll(apcReport.getRows());
            }
            for(ApcData apcData : apcDataList) {
                ApcStatistics apcStatistics;
                Optional<ApcStatistics> optional = this.apcStatisticsRepository.findById(apcData.getMmsId());
                if (optional.isPresent()) {
                    log.debug("updating existing entry " + apcData.getMmsId());
                    apcStatistics = optional.get();
                    apcStatistics.update(apcData);
                } else {
                    log.debug("creating new entry " + apcData.getMmsId());
                    apcStatistics = new ApcStatistics(apcData);
                    if (apcData.getMmsId() != null && !apcData.getMmsId().isEmpty()) {
                        try {
                            BibWithRecord bib = this.almaCatalogService.getRecord(apcData.getMmsId());
                            this.addBibRecord(apcStatistics, bib);
                        } catch (FeignException feignException) {
                            log.warn(String.format("could not read bib data: %s", feignException.getMessage()));
                        }
                    }
                }
                this.apcStatisticsRepository.save(apcStatistics);
            }
        } catch (Exception e) {
            log.error("could not save apc data. messaage: " + e.getMessage(), e);
        }
    }

    public List<ApcStatistics> getAllApcStatistics() {
        List<ApcStatistics> apcStatistics = new ArrayList<>();
        this.apcStatisticsRepository.findAll().forEach(apcStatistics::add);
        return apcStatistics;
    }

    public ApcStatistics getApcStatistics(String mmsId) {
        return this.apcStatisticsRepository.findById(mmsId).orElse(null);
    }

    public ApcStatistics saveApcStatistics(ApcStatistics apcStatistics) {
        return this.apcStatisticsRepository.save(apcStatistics);
    }

    public List<JournalApcDataDto> getAllJournalApcData() {
        List<JournalApcDataDto> journalApcDataDtos = new ArrayList<>();
        this.getAllApcStatistics().forEach(entry -> journalApcDataDtos.add(new JournalApcDataDto(entry)));
        return journalApcDataDtos;

    }

    public JournalApcDataDto saveJournalApcData(JournalApcDataDto journalApcDataDto, String mode) {
        ApcStatistics apcStatistics = this.apcStatisticsRepository.findById(journalApcDataDto.getMmsId()).orElse(null);
        if (apcStatistics != null) {
            apcStatistics.update(journalApcDataDto);
            switch (mode) {
                case "doi": {
                    this.almaCatalogService.updateIdentifier(journalApcDataDto.getMmsId(), journalApcDataDto.getDoi(), mode);
                    boolean updated = this.almaCatalogService.updateOaPortfolio(journalApcDataDto.getMmsId(), journalApcDataDto.getDoi());
                    if (!updated)
                        this.almaElectronicService.createOaPortfolio(journalApcDataDto.getMmsId(), journalApcDataDto.getDoi());
                    break;
                }
                case "duepublico": {
                    this.almaCatalogService.updateIdentifier(journalApcDataDto.getMmsId(), journalApcDataDto.getDuepublicoId(), mode);
                    boolean updated = this.almaCatalogService.updateMycorePortfolio(journalApcDataDto.getMmsId(), journalApcDataDto.getDuepublicoId());
                    if (!updated)
                        this.almaElectronicService.createDuepublicoPortfolio(journalApcDataDto.getMmsId(), journalApcDataDto.getDuepublicoId());
                    break;
                }
                case "comment": {
                    this.almaPoLineService.addNote(journalApcDataDto.getOrderNumber(), journalApcDataDto.getNote());
                    break;
                }
            }
            return new JournalApcDataDto(apcStatisticsRepository.save(apcStatistics));
        } else
            return null;
        }

    private void addBibRecord(ApcStatistics apcStatistics, BibWithRecord bib) {
        apcStatistics.setPublisher(bib.getPublisherConst());
        apcStatistics.setPublishingDate(bib.getDateOfPublication());
        apcStatistics.setTitle(bib.getTitle());
        if (bib.getRecord() == null || bib.getRecord().getDatafield() == null || bib.getRecord().getDatafield().size() == 0)
            log.warn("no bib record available");
        List<MarcDatafield> datafieldList = bib.getRecord().getDatafield();
        for (MarcDatafield datafield : datafieldList) {
            log.debug("found data field in record with tag: " + datafield.getTag());
            if (datafield.getTag() != null) {
                switch (datafield.getTag()) {
                    case "701": {
                        apcStatistics.setFirstAuthor(retrieveSubfield(datafield, "a"));
                        break;
                    }
                    case "710": {
                        apcStatistics.setFaculty(retrieveSubfield(datafield, "a"));
                        break;
                    }
                    case "100": {
                        apcStatistics.setFirstAuthor(bib.getAuthor());
                        break;
                    }
                    case "024": {
                        if ("doi".equals(retrieveSubfield(datafield, "2"))) {
                            String doi = retrieveSubfield(datafield, "a");
                            if (!"echte DOI".equals(doi) && !doi.isEmpty())
                                apcStatistics.setDoi(retrieveSubfield(datafield, "a"));
                        }
                        break;
                    }
                    case "500": {
                        apcStatistics.addComment(retrieveSubfield(datafield, "a"));
                        break;
                    }
                    case "773": {
                        apcStatistics.setJournal(retrieveSubfield(datafield, "t"));
                        apcStatistics.setIssn(retrieveSubfield(datafield, "x"));
                        break;
                    }
                    case "700": {
                        apcStatistics.addAuthor(retrieveSubfield(datafield, "a"));
                        break;
                    }
                    case "245": {
                        apcStatistics.setTitle(retrieveSubfield(datafield, "a"));
                        if (!retrieveSubfield(datafield, "b").isEmpty())
                            apcStatistics.addSubTitle(retrieveSubfield(datafield, "b"));
                        break;
                    }
                    default: {
                    }
                }
            }
        }
    }

    private String retrieveSubfield(MarcDatafield datafield, String subfield) {
        log.debug(String.format("data field has %d subfields", datafield.getSubfield().size()));
        for (MarcSubfield marcSubfield : datafield.getSubfield()) {
            log.debug(marcSubfield.getCode());
            if (marcSubfield.getCode().equals(subfield)) {
                log.debug(marcSubfield.getValue());
                return marcSubfield.getValue();
            }
        }
        return "";
    }
}
