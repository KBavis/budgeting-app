package com.bavis.budgetapp.services;

import com.bavis.budgetapp.config.MailgunConfig;
import com.bavis.budgetapp.dao.StagedVenmoPaymentRepository;
import com.bavis.budgetapp.dao.VenmoAutomationRepository;
import com.bavis.budgetapp.dto.response.VenmoAutomationDto;
import com.bavis.budgetapp.entity.StagedVenmoPayment;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.entity.VenmoAutomation;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.bavis.budgetapp.service.impl.VenmoEmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VenmoEmailServiceTests {

    @Mock
    private MailgunConfig mailgunConfig;

    @Mock
    private VenmoAutomationRepository venmoAutomationRepository;

    @Mock
    private StagedVenmoPaymentRepository stagedVenmoPaymentRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private VenmoEmailServiceImpl venmoEmailService;

    private User testUser;
    private VenmoAutomation testAutomation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(100L)
                .username("testuser")
                .name("Test User")
                .build();

        testAutomation = VenmoAutomation.builder()
                .automationId(1L)
                .user(testUser)
                .ingestToken("token123")
                .enabled(true)
                .build();
    }

    @Test
    void testVerifyMailgunSignature_ValidSignature() throws Exception {
        String key = "secret_key_123";
        String timestamp = "1620000000";
        String token = "random_token_456";

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        byte[] hmacBytes = mac.doFinal((timestamp + token).getBytes());
        String expectedSignature = HexFormat.of().formatHex(hmacBytes);

        when(mailgunConfig.getApiKey()).thenReturn(key);

        boolean isValid = venmoEmailService.verifyMailgunSignature(timestamp, token, expectedSignature);
        assertTrue(isValid);
    }

    @Test
    void testVerifyMailgunSignature_InvalidSignature() {
        when(mailgunConfig.getApiKey()).thenReturn("secret_key_123");

        boolean isValid = venmoEmailService.verifyMailgunSignature("1620000000", "token", "invalid_sig");
        assertFalse(isValid);
    }

    @Test
    void testProcessInboundEmail_Success_OutgoingPaymentStaged() {
        String recipient = "venmo-token123@mail.bavisbudgeting.com";
        String from = "venmo@venmo.com";
        String subject = "You paid John Doe $25.00";
        String bodyPlain = "$25.00\nDinner at Italian place\nSee transaction details in app";

        when(venmoAutomationRepository.findByIngestToken("token123")).thenReturn(Optional.of(testAutomation));
        when(stagedVenmoPaymentRepository.save(any(StagedVenmoPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String bodyHtml = "<p class=\"transaction-note secondary-text\">Dinner at Italian place</p>";
        venmoEmailService.processInboundEmail(recipient, from, subject, bodyPlain, bodyHtml);

        ArgumentCaptor<StagedVenmoPayment> captor = ArgumentCaptor.forClass(StagedVenmoPayment.class);
        verify(stagedVenmoPaymentRepository, times(1)).save(captor.capture());

        StagedVenmoPayment saved = captor.getValue();
        assertEquals(25.00, saved.getAmount());
        assertEquals("John Doe", saved.getCounterparty());
        assertEquals("Dinner at Italian place", saved.getDescription());
        assertEquals("Dinner at Italian place (John Doe)", saved.getEnrichedName());
        assertFalse(saved.isMatched());
    }

    @Test
    void testProcessInboundEmail_IncomingPayment_Ignored() {
        String recipient = "venmo-token123@mail.bavisbudgeting.com";
        String from = "venmo@venmo.com";
        String subject = "John Doe paid you $25.00"; // Incoming payment, not "You paid"
        String bodyPlain = "$25.00\nThanks for lunch";

        when(venmoAutomationRepository.findByIngestToken("token123")).thenReturn(Optional.of(testAutomation));

        venmoEmailService.processInboundEmail(recipient, from, subject, bodyPlain, null);

        verify(stagedVenmoPaymentRepository, never()).save(any());
    }

    @Test
    void testEnableAutomation_NewAutomation() {
        when(userService.getCurrentAuthUser()).thenReturn(testUser);
        when(venmoAutomationRepository.findByUserUserId(100L)).thenReturn(Optional.empty());
        when(venmoAutomationRepository.save(any(VenmoAutomation.class))).thenReturn(testAutomation);
        when(mailgunConfig.getIngestDomain()).thenReturn("mail.bavisbudgeting.com");

        VenmoAutomationDto result = venmoEmailService.enableAutomation("GMAIL");

        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertEquals("venmo-token123@mail.bavisbudgeting.com", result.getIngestEmail());
        verify(venmoAutomationRepository, times(1)).save(any(VenmoAutomation.class));
    }

    @Test
    void testDisableAutomation_Success() {
        when(userService.getCurrentAuthUser()).thenReturn(testUser);
        when(venmoAutomationRepository.findByUserUserId(100L)).thenReturn(Optional.of(testAutomation));

        venmoEmailService.disableAutomation();

        assertFalse(testAutomation.isEnabled());
        verify(venmoAutomationRepository, times(1)).save(testAutomation);
    }

    @Test
    void testProcessInboundEmail_ExactVenmoHtmlPayload() {
        String recipient = "venmo-token123@mail.bavisbudgeting.com";
        String from = "Venmo <venmo@venmo.com>";
        String subject = "You paid Jane Smith $15.50";
        String htmlPayload = """
            <!DOCTYPE html>
            <html>
              <body>
                <p class="text-center secondary-text title">You paid Jane Smith</p>
                <div class="amount-container">$15.50</div>
                <p class="transaction-note secondary-text">Coffee & Bakery</p>
              </body>
            </html>
            """;

        when(venmoAutomationRepository.findByIngestToken("token123")).thenReturn(Optional.of(testAutomation));
        when(stagedVenmoPaymentRepository.save(any(StagedVenmoPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        venmoEmailService.processInboundEmail(recipient, from, subject, null, htmlPayload);

        ArgumentCaptor<StagedVenmoPayment> captor = ArgumentCaptor.forClass(StagedVenmoPayment.class);
        verify(stagedVenmoPaymentRepository, times(1)).save(captor.capture());

        StagedVenmoPayment saved = captor.getValue();
        assertEquals(15.50, saved.getAmount());
        assertEquals("Jane Smith", saved.getCounterparty());
        assertEquals("Coffee & Bakery", saved.getDescription());
        assertEquals("Coffee & Bakery (Jane Smith)", saved.getEnrichedName());
    }
}
