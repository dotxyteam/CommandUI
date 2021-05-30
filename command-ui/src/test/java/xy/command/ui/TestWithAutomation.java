package xy.command.ui;

import java.io.File;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Test;

import xy.reflect.ui.util.MiscUtils;
import xy.reflect.ui.util.MoreSystemProperties;
import xy.ui.testing.Tester;
import xy.ui.testing.util.TestingUtils;

public class TestWithAutomation {

	Tester tester = new Tester();

	protected static void checkSystemProperty(String key, String expectedValue) {
		String value = System.getProperty(key);
		if (!MiscUtils.equalsOrBothNull(expectedValue, value)) {
			String errorMsg = "System property invalid value:\n" + "-D" + key + "=" + value + "\nExpected:\n" + "-D"
					+ key + "=" + expectedValue;
			System.err.println(errorMsg);
			throw new AssertionError(errorMsg);

		}
	}

	public static void setupTestEnvironment() {
		checkSystemProperty(MoreSystemProperties.DEBUG, "true");
	}

	@BeforeClass
	public static void beforeAllTests() {
		setupTestEnvironment();
		TestingUtils.purgeAllReportsDirectory();
	}

	@Test
	public void test() throws Exception {
		File comanndLineSpecFile = new File("test.cml");
		if (comanndLineSpecFile.exists()) {
			Files.delete(comanndLineSpecFile.toPath());
		}
		try {
			TestingUtils.assertSuccessfulReplay(tester, new File(
					System.getProperty("command-ui.project.directory", "./") + "test-specifications/test.stt"));
		} finally {
			Files.delete(comanndLineSpecFile.toPath());
		}
	}

}
