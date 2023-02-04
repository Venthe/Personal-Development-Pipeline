import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

@Grapes([
        @Grab('com.fasterxml.jackson.core:jackson-databind:2.13.3'),
        @Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3'),
        @Grab('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3')
])

// FIXME: Remove custom implementation of YAML parser
//  snakeYaml from jenkins converts 'on' to true...
static Map parseYaml(input) {
    def mapper = new ObjectMapper(new YAMLFactory())
    def parsedJson = mapper.readTree(input)
    return mapper.convertValue(parsedJson, Map.class)
}
