import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

//@ExtendWith(JenkinsDeclarativePipelineTestExtension.class)
class ExampleJobTest extends BasePipelineTest {

    @BeforeEach
    void be() {
        scriptRoots += 'src/main/groovy/eu/venthe/jenkins/pipelines'
        super.setUp()
        helper.registerAllowedMethod("readFile", [String], { fileName ->
            return readContentOfFile(fileName)
        })
        helper.registerAllowedMethod('script', [Closure], {
            it.delegate = delegate
            helper.callClosure(it)
        })
        helper.registerAllowedMethod('steps', [Closure], {
            it.delegate = delegate
            helper.callClosure(it)
        })
        helper.registerAllowedMethod('unstash', [Closure], {
            it.delegate = delegate
            helper.callClosure(it)
        })
        helper.registerAllowedMethod('stashedFile', [Closure], {
        })
    }

    private String readContentOfFile(file) {
        def normalizedPath = file.replace(".jenkins/workflows/", "")
        def inputStream = this.getClass()
                .getResourceAsStream(normalizedPath)
        def reader = new InputStreamReader(
                inputStream,
                StandardCharsets.UTF_8)
        return new BufferedReader(reader).lines()
                .collect(Collectors.joining(System.lineSeparator()))
    }

    @Test
    void should_execute_without_errors() throws Exception {
        // given
//        addEnvVar("WORKSPACE", "false")
//        addEnvVar("WORKSPACE_TMP", "/foo")
//        addEnvVar("BUILD_TAG", "tag@tag")
//        addEnvVar("JENKINS_HOME", "src/main/groovy/eu/venthe/jenkins")
//        addEnvVar("PATH", "/usr/bin")
        addEnvVar("ACTIONS_HOME", "./src/main/groovy/eu/venthe/jenkins")
        addParam('EXECUTION_ENVIRONMENT', 'docker')
        addParam('INPUT', '')

        //when
        runScript("ActionsWorkflow.jenkinsfile")

        // then
        assertJobStatusSuccess()
        printCallStack()
    }

    private static fromClass(def it) { "${it.getSimpleName()}.jenkinsfile".toString() }
}
/*
helper.addShMock("cat `pwd`/.jenkins/workflows/example.yml", WORK, 0)
helper.registerAllowedMethod("sh", [Map.class], {c -> "bcc19744"})
helper.registerAllowedMethod("timeout", [Map.class, Closure.class], null)
helper.registerAllowedMethod("timestamps", [], { println 'Printing timestamp' })
helper.registerAllowedMethod(method("readFile", String.class), { file ->
    return Files.contentOf(new File(file), Charset.forName("UTF-8"))
})
helper.registerAllowedMethod("customMethodWithArguments", [String, int, Collection], { String stringArg, int intArg, Collection collectionArg ->
    return println "executing mock closure with arguments (arguments: '${stringArg}', '${intArg}', '${collectionArg}')"
})
*/
