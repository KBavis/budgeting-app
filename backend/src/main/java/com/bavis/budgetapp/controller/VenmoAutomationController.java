package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.response.VenmoAutomationDto;
import com.bavis.budgetapp.service.VenmoEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kellen Bavis
 *
 *         Controller for managing a user's Venmo email automation settings.
 */
@RestController
@RequestMapping("/venmo/automation")
@Log4j2
@RequiredArgsConstructor
public class VenmoAutomationController {

    private final VenmoEmailService venmoEmailService;

    /**
     * Get the current user's Venmo automation settings.
     *
     * @return - automation settings including the forwarding email address, or 204
     *         if not configured
     */
    @GetMapping
    public ResponseEntity<VenmoAutomationDto> getSettings() {
        VenmoAutomationDto settings = venmoEmailService.getAutomationSettings();
        if (settings == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(settings);
    }

    /**
     * Enable Venmo automation for the current user.
     * Creates a new automation record with a unique ingest token if one doesn't
     * exist,
     * or re-enables an existing disabled one.
     *
     * @return - the automation settings including the forwarding email address
     */
    @PostMapping("/enable")
    public ResponseEntity<VenmoAutomationDto> enable(
            @RequestParam(value = "emailProvider", defaultValue = "OTHER") String emailProvider) {
        VenmoAutomationDto dto = venmoEmailService.enableAutomation(emailProvider);
        return ResponseEntity.ok(dto);
    }

    /**
     * Disable Venmo automation for the current user.
     * Does not delete the record — the user can re-enable later with the same
     * ingest token.
     *
     * @return - 204 No Content
     */
    @DeleteMapping("/disable")
    public ResponseEntity<Void> disable() {
        venmoEmailService.disableAutomation();
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark phase 1 (forwarding address verification) as complete in Gmail.
     * Advances setupPhase from FORWARDING_VERIFICATION to FILTER_SETUP.
     *
     * @return - updated VenmoAutomationDto with setupPhase=FILTER_SETUP
     */
    @PostMapping("/complete-forwarding-verification")
    public ResponseEntity<VenmoAutomationDto> completeForwardingVerification() {
        VenmoAutomationDto dto = venmoEmailService.completeForwardingVerification();
        return ResponseEntity.ok(dto);
    }

    /**
     * Mark Phase 2 (email filter/rule setup) as complete.
     * Transitions setupPhase to COMPLETE, making the automation fully operational.
     *
     * @return - updated VenmoAutomationDto with setupPhase=COMPLETE
     */
    @PostMapping("/complete-filter-setup")
    public ResponseEntity<VenmoAutomationDto> completeFilterSetup() {
        VenmoAutomationDto dto = venmoEmailService.completeFilterSetup();
        return ResponseEntity.ok(dto);
    }
}
