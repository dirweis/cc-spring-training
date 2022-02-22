package de.infoteam;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

/**
 * The smoke test for the service. Starts and stops the service including time runtime tracking.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.0
 *
 */
class SpringApplicationSmokeTest {

	/**
	 * The only test here: Ensures the service starts and shuts down in time as expected.
	 */
	@Test
	void testServiceStartAndShutDown() {

		final StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		String errorMessage = null;

		try {
			PetshopWebApp.main(new String[] {});
			stopWatch.stop();
		} catch (final Throwable e) {
			errorMessage = e.getLocalizedMessage();
		}

		assertThat(errorMessage).isNull();

		assertThat(stopWatch.getTotalTimeSeconds()).isLessThan(8);
	}
}
