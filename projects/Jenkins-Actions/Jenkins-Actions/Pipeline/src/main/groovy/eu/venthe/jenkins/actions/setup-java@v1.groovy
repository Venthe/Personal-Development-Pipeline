package eu.venthe.jenkins.actions

def distribution = with?.('distribution') ?: ""
def javaVersion = with?.('java-version') ?: ""

if (!distribution.equalsIgnoreCase("temurin")) {
    return [status: "not_ok"]
}

if (!javaVersion.equalsIgnoreCase("17")) {
    return [status: "not_ok"]
}

def TMP_DIR="${env.WORKSPACE_TMP}/${uses}"
def BIN_HOME = "/home/root/bin"
def JAVA_HOME = "${BIN_HOME}/java"
def JAVA_PATH = "${JAVA_HOME}/bin"
def ARCHIVE_NAME = "OpenJDK17U-jdk_x64_linux_hotspot_17.0.4.1_1.tar.gz"
sh script: """\
rm -rf ${TMP_DIR}
mkdir -p ${TMP_DIR}
cd ${TMP_DIR}
curl --location \
    --remote-name \
    https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.4.1%2B1/${ARCHIVE_NAME}
mkdir -p ${JAVA_HOME}
tar -xzf ${ARCHIVE_NAME} --directory ${JAVA_HOME} --strip-components=1
rm ${ARCHIVE_NAME}
ln -s ${JAVA_PATH}/java /usr/local/bin/java
""", label: stepName

additionalEnvironmentVariables['JAVA_HOME'] = JAVA_HOME

return [defaultRun: "", status: "ok"]
