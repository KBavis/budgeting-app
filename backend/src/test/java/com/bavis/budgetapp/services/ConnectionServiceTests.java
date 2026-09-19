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

    @Test
    void testMarkDisconnected_FlagsConnectionWithReason() {
        //Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Connection flagged = connectionService.markDisconnected(10L, "ITEM_LOGIN_REQUIRED", "login required");

        //Assert
        assertEquals(ConnectionStatus.DISCONNECTED, flagged.getConnectionStatus());
        assertEquals("ITEM_LOGIN_REQUIRED", flagged.getErrorCode());
        assertEquals("login required", flagged.getErrorMessage());
        assertTrue(flagged.requiresReauthentication());
        verify(connectionRepository, times(1)).save(connection);
    }

    @Test
    void testMarkDisconnected_TruncatesOverlyLongMessages() {
        //Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Connection flagged = connectionService.markDisconnected(10L, "SOME_ERROR", "x".repeat(2000));

        //Assert - must fit the column
        assertEquals(500, flagged.getErrorMessage().length());
    }

    @Test
    void testMarkDisconnected_ConnectionNotFound_Throws() {
        when(connectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> connectionService.markDisconnected(99L, "ITEM_LOGIN_REQUIRED", "login required"));
        verify(connectionRepository, never()).save(any());
    }

    @Test
    void testMarkDisconnected_OtherPlaidErrors_DoNotRequireReauthentication() {
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Connection flagged = connectionService.markDisconnected(10L, "ITEM_NOT_FOUND", "gone");

        assertEquals(ConnectionStatus.DISCONNECTED, flagged.getConnectionStatus());
        assertFalse(flagged.requiresReauthentication(), "only ITEM_LOGIN_REQUIRED can be fixed via update mode");
    }

    /**
     * A successful sync passes a healthy Connection through update(), which must clear a previously reported error
     */
    @Test
    void testUpdateConnection_HealthyConnection_ClearsPreviouslyReportedError() {
        //Arrange
        Connection persisted = Connection.builder()
                .connectionId(10L)
                .connectionStatus(ConnectionStatus.DISCONNECTED)
                .errorCode("ITEM_LOGIN_REQUIRED")
                .errorMessage("login required")
                .build();
        Connection healthy = Connection.builder()
                .connectionStatus(ConnectionStatus.CONNECTED)
                .lastSyncTime(LocalDateTime.now())
                .previousCursor("cursor")
                .build();

        //Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(persisted));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Connection updated = connectionService.update(healthy, 10L);

        //Assert
        assertEquals(ConnectionStatus.CONNECTED, updated.getConnectionStatus());
        assertNull(updated.getErrorCode());
        assertNull(updated.getErrorMessage());
        assertFalse(updated.requiresReauthentication());
    }

    @Test
    void testMarkConnected_ClearsErrorAndMarksConnected() {
        //Arrange
        Connection flagged = Connection.builder()
                .connectionId(10L)
                .connectionStatus(ConnectionStatus.DISCONNECTED)
                .errorCode("ITEM_LOGIN_REQUIRED")
                .errorMessage("login required")
                .build();

        //Mock
        when(connectionRepository.findById(10L)).thenReturn(Optional.of(flagged));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Connection result = connectionService.markConnected(10L);

        //Assert
        assertEquals(ConnectionStatus.CONNECTED, result.getConnectionStatus());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());
        assertFalse(result.requiresReauthentication());
        verify(connectionRepository, times(1)).save(flagged);
    }

    @Test
    void testMarkConnected_ConnectionNotFound_Throws() {
        when(connectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> connectionService.markConnected(99L));
        verify(connectionRepository, never()).save(any());
    }
}
