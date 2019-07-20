import com.yiziton.dataweb.waybill.config.Constants;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public final class MyRunner extends SpringJUnit4ClassRunner {


    /**
     * Construct a new {@code SpringJUnit4ClassRunner} and initialize a
     * {@link TestContextManager} to provide Spring testing functionality to
     * standard JUnit tests.
     *
     * @param clazz the test class to be run
     * @see #createTestContextManager(Class)
     */
    public MyRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        System.setProperty(Constants.PLATFORM_BASE_PACKAGE,"com.yiziton");

    }
}
