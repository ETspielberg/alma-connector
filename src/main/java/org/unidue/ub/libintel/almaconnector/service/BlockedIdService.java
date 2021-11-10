package org.unidue.ub.libintel.almaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.BlockedId;
import org.unidue.ub.libintel.almaconnector.repository.BlockedIdRepository;

import java.time.LocalDateTime;

@Service
public class BlockedIdService {

    private final BlockedIdRepository blockedIdRepository;

    public BlockedIdService(BlockedIdRepository blockedIdRepository) {
        this.blockedIdRepository = blockedIdRepository;
    }

    public void blockId(String id) {
        this.blockedIdRepository.save(new BlockedId(id, LocalDateTime.now()));
    }

    public boolean check(String id) {
        return this.blockedIdRepository.findById(id).isPresent();
    }
}
