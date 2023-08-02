package de.training;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The smoke test for the service. Starts and stops the service including time runtime tracking.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SpringApplicationSmokeTest {

    /**
     * The only test here: Ensures the service starts and shuts down in time as expected.
     */
    @Test
    void testServiceStartAndShutDown() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        PetshopWebApp.main(new String[] {});
        stopWatch.stop();

        Assertions.assertThat(stopWatch.getTotalTimeSeconds()).isLessThan(9);
    }
}
