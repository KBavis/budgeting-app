package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.LinkToken;
import com.bavis.budgetapp.service.impl.PlaidServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle requests regarding User entity
 *
 * @author Kellen Bavis
 */
@RestController
@RequestMapping("/user")
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final PlaidServiceImpl plaidService;
    private final UserServiceImpl userService;

    /**
     * Endpoint to allow users to refresh their Plaid Link Token,
     * enabling users to add accounts after initial registration
     * regardless of if their previous LinkToken has expired
     *
     * @return
     *      - ewly generated Link Token
     */
    @PutMapping("/refresh/link-token")
    public ResponseEntity<LinkToken> refreshLinkToken() {
        log.info("Received request to refresh Plaid Link Token");
        //Fetch Authenticated User
        User currentAuthUser = userService.getCurrentAuthUser();
        Long userId = currentAuthUser.getUserId();

        //Generate New LinkToken & Update User
        LinkToken updatedLinkToken = plaidService.generateLinkToken(userId);
        currentAuthUser.setLinkToken(updatedLinkToken);
        userService.update(userId, currentAuthUser);
        return ResponseEntity.ok(updatedLinkToken);
    }
}
