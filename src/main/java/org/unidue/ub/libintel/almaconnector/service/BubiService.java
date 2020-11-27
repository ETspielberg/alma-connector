package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;

import java.util.List;

@Service
public class BubiService {

    private final BubiDataRepository bubiDataRepository;

    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final CoreDataRepository coreDataRepository;

    public BubiService(BubiDataRepository bubiDataRepository,
                BubiOrderRepository bubiOrderRepository,
                BubiOrderLineRepository bubiOrderLineRepository,
                CoreDataRepository coreDataRepository) {
        this.bubiDataRepository = bubiDataRepository;
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.coreDataRepository = coreDataRepository;
    }

    public BubiOrder getBubiOrders(String orderNumber) {
        return this.bubiOrderRepository.getOne(orderNumber);
    }

    public List<CoreData> getAllCoreData() {
        return this.coreDataRepository.findAll();
    }

    public CoreData getCoreData(String id) {
        return this.coreDataRepository.getOne(id);
    }

    public CoreData saveCoreData(CoreData coreData) {
        return this.coreDataRepository.save(coreData);
    }
}
