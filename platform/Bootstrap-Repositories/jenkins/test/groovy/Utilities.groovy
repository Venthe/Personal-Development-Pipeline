import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

static String load(String it) {
    return new BufferedReader(new InputStreamReader(NormalizeEventTest.class.getClassLoader().getResourceAsStream(it), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"))
}
