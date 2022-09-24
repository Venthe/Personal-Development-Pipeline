import com.lesfurets.jenkins.unit.declarative.DeclarativePipelineTest
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class JenkinsDeclarativePipelineTestExtension implements BeforeTestExecutionCallback {

    @Override
    void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        extensionContext.testInstance
                .filter { DeclarativePipelineTest.class.isAssignableFrom it.getClass() }
                .map { (DeclarativePipelineTest) it }
                .ifPresent { it.setUp() }
    }
}
