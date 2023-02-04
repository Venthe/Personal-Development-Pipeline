package eu.venthe.jenkins.actions

def version = with['version']

def TMP_DIR="${env.WORKSPACE_TMP}/${uses}"
def GRADLE_PATH="/root/bin/gradle"

sh script: """\
rm -rf ${TMP_DIR}
mkdir -p ${TMP_DIR}
cd ${TMP_DIR}
curl --location --remote-name "https://downloads.gradle-dn.com/distributions/gradle-${version}-bin.zip"
mkdir -p "${GRADLE_PATH}"
unzip -q "gradle-${version}-bin.zip" -d "${GRADLE_PATH}"
rm gradle-${version}-bin.zip
ln -s ${GRADLE_PATH}/gradle-7.4.2/bin/gradle /usr/local/bin/gradle
""", label: stepName

return [defaultRun: "", status: "ok"]
