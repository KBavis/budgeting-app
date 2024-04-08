package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.ConnectionRepository;
import com.bavis.budgetapp.enumeration.ConnectionStatus;
import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.service.impl.ConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class ConnectionServiceTests {

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    ConnectionServiceImpl connectionService;

    private Connection connection;

    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        connectionService = new ConnectionServiceImpl(connectionRepository);
        now = LocalDateTime.now();
        connection = Connection.builder()
                .connectionId(10L)
                .accessToken("access-token")
                .lastSyncTime(now)
                .institutionName("Bank of America")
                .connectionStatus(ConnectionStatus.CONNECTED)
                .build();
    }

    /**
     * Validate that our Connection Service can correctly save a Connection Entity
     */
    @Test
    public void testCreateConnection_Successful() {

        //Mock
        when(connectionRepository.save(connection)).thenReturn(connection);

        //Act
        Connection savedConnection = connectionService.create(connection);

        //Assert
        assertNotNull(savedConnection);
        assertEquals(10L, savedConnection.getConnectionId());
        assertEquals("access-token", savedConnection.getAccessToken());
        assertEquals(now, savedConnection.getLastSyncTime());
        assertEquals("Bank of America", savedConnection.getInstitutionName());
        assertEquals(ConnectionStatus.CONNECTED, savedConnection.getConnectionStatus());

        //Verify
        verify(connectionRepository, times(1)).save(connection);
    }
}
