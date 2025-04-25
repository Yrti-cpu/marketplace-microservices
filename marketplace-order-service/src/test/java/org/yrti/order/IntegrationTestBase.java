package org.yrti.order;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.yrti.order.containers.PostgresTestContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @BeforeAll
    static void setUp() {
        PostgresTestContainer.getInstance().start();
    }
}
