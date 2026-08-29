package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.ConnectionRepository;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.service.impl.ConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class ConnectionServiceTests {

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    ConnectionServiceImpl connectionService;

    private Connection connection;

    private LocalDateTime now;

    @BeforeEach
    public void setup(){

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

    /**
     * Ensures our Connection Service properly handles repository failures
     */
    @Test
    public void testCreateConnection_Failure() {
        //Mock
        String dataIntegrityErrorMessage = "An issue occurred regarding Data Integrity when saving!";
        when(connectionRepository.save(connection)).thenThrow(new DataIntegrityViolationException(dataIntegrityErrorMessage));

        //Act & Assert
        ConnectionCreationException exception = assertThrows(ConnectionCreationException.class, () -> {
            connectionService.create(connection);
        });
        assertNotNull(exception);
        assertEquals("Error occurred when creating a connection {" + dataIntegrityErrorMessage + "}", exception.getMessage());

        //Verify
        verify(connectionRepository, times(1)).save(connection);
    }

    @Test
    void testUpdateConnection_Successful() {
        // Arrange
        Long connectionId = 10L;
        LocalDateTime lastSyncTime = LocalDateTime.now();
        String accessToken = "access-token";
        String institutionName = "J.P Morgan";
        String previousCursor = "previous-cursor";
        String originalCursor = "original-cursor";

        Connection connectionWithUpdates = Connection.builder()
                .previousCursor(previousCursor)
                .originalCursor(originalCursor)
                .lastSyncTime(lastSyncTime)
                .build();

        Connection connectionToUpdate = Connection.builder()
                .connectionId(connectionId)
                .connectionStatus(ConnectionStatus.CONNECTED)
                .accessToken(accessToken)
                .institutionName(institutionName)
                .previousCursor(null)
                .originalCursor(null)
                .lastSyncTime(LocalDateTime.now().minusDays(1))
                .build();

        Connection expectedUpdatedConnection = Connection.builder()
                .connectionId(connectionId)
                .connectionStatus(ConnectionStatus.CONNECTED)
                .lastSyncTime(lastSyncTime)
                .accessToken(accessToken)
                .institutionName(institutionName)
                .previousCursor(previousCursor)
                .originalCursor(originalCursor)
                .lastSyncTime(lastSyncTime)
                .build();

        // Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(connectionToUpdate));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Connection actualConnection = connectionService.update(connectionWithUpdates, 10L);

        // Assert
        assertNotNull(actualConnection);
        assertEquals(expectedUpdatedConnection.getConnectionId(), actualConnection.getConnectionId());
        assertEquals(expectedUpdatedConnection.getConnectionStatus(), actualConnection.getConnectionStatus());
        assertEquals(expectedUpdatedConnection.getLastSyncTime(), actualConnection.getLastSyncTime());
        assertEquals(expectedUpdatedConnection.getInstitutionName(), actualConnection.getInstitutionName());
        assertEquals(expectedUpdatedConnection.getPreviousCursor(), actualConnection.getPreviousCursor());
        assertEquals(expectedUpdatedConnection.getOriginalCursor(), actualConnection.getOriginalCursor());
        assertEquals(expectedUpdatedConnection.getLastSyncTime(), actualConnection.getLastSyncTime());

        // Verify
        verify(connectionRepository, times(1)).findById(10L);
        verify(connectionRepository, times(1)).save(any(Connection.class));
    }
    @Test
    void testUpdateConnection_ConnectionIdNotFound_Failure() {
        //Arrange
        String previousCursor = "previousCursor";
        Connection connectionWithUpdates = Connection.builder()
                .previousCursor(previousCursor)
                .build();

        //Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            connectionService.update(connectionWithUpdates, 10L);
        });
        assertNotNull(exception);
        assertEquals("Unable to find Connection with ID 10 to update.", exception.getMessage());

        //Verify
        verify(connectionRepository, times(1)).findById(10L);
    }

}
