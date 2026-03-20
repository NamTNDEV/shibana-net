package com.shibana.social_service.service;

import com.shibana.social_service.dto.ConnectionStatus;
import com.shibana.social_service.repo.neo4j.ConnectionRepo;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class ConnectionsService {
    ConnectionRepo connectionRepo;

    ConnectionStatus getConnectStatuses(String viewerId, String targetId) {
        return connectionRepo.getConnectionStatus(viewerId, targetId);
    }
}
