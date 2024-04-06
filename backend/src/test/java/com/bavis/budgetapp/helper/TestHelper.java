package com.bavis.budgetapp.helper;

import com.auth0.jwt.algorithms.Algorithm;
import com.bavis.budgetapp.config.JwtConfig;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.impl.JwtServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TestHelper.class);

    @Autowired
    private JwtService jwtService;


    public TestHelper() {
        jwtService = new JwtServiceImpl(createAlgorithm()); //instantiate jwt service
    }


    /**
     * Helper Function To Generate Valid Jwt Token
     *
     * @return
     *      - valid JWT Token
     */
    public String getValidJwtToken(){
        User fakeUser = User.builder()
                .name("Test User")
                .username("test-user")
                .password("password")
                .build();
        return jwtService.generateToken(fakeUser);
    }

    public Algorithm createAlgorithm() {
        JwtConfig jwtConfig = new JwtConfig();
        return jwtConfig.jwtAlgorithm();
    }

    // Helper function to create Balance Response Object
    public Object createBalanceResponse(String accountId, double balance) {
        return new Object() {
            public final Object[] accounts = {
                    new Object() {
                        public final String account_id = accountId;
                        public final Object balances = new Object() {
                            public final double available = balance;
                            public final double current = balance;
                            public final String iso_currency_code = "USD";
                            public final Object limit = null;
                            public final Object unofficial_currency_code = null;
                        };
                        public final String mask = "6593";
                        public final String name = "E CHECK 0001";
                        public final String official_name = "E-Checking";
                        public final String subtype = "checking";
                        public final String type = "depository";
                    }
            };
            public final Object item = new Object() {
                public final String[] available_products = { "balance", "signal", "identity", "recurring_transactions" };
                public final String[] billed_products = { "auth", "transactions" };
                public final Object consent_expiration_time = null;
                public final Object error = null;
                public final String institution_id = "ins_127163";
                public final String item_id = "7PYvP8JJPDs4B5y05LvnuBB71yaY1nfQpgBAb";
                public final String[] products = { "auth", "transactions" };
                public final String update_type = "background";
                public final String webhook = "";
            };
            public final String request_id = "rCl6oBmN5HJgz9e";
        };
    }

}
