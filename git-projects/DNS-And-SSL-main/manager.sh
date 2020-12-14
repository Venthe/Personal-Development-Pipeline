PASSPHRASE_FILE="passphrase.txt"
ROOT_SUBJECT_FILE="root_subject.txt"
CSR_SUBJECT_FILE="csr_subject.txt"
ENCRYPTION_BITS=4096
OUTPUT_DIR=${OUTPUT_DIR:-./}

function clean() {
    rm $OUTPUT_DIR/*.pem $OUTPUT_DIR/*.key $OUTPUT_DIR/*.srl $OUTPUT_DIR/*.csr $OUTPUT_DIR/*.crt &2> /dev/null
}

function generate_key() {
    local KEY_NAME=${1}
    openssl genrsa \
            -des3 \
            -passout file:"${PASSPHRASE_FILE}" \
            -out $OUTPUT_DIR/"${KEY_NAME}" \
            ${ENCRYPTION_BITS}
}

# certificate signing request
function generate_csr() {
    local KEY_NAME=$1
    local CSR_NAME=$2
    openssl req \
            -new \
            -key $OUTPUT_DIR/${KEY_NAME} \
            -passin file:"${PASSPHRASE_FILE}" \
            -subj "$(cat $CSR_SUBJECT_FILE)" \
            -out $OUTPUT_DIR/${CSR_NAME}
}

function generate_pem() {
    local KEY_NAME=${1}
    local PEM_NAME=${2}
    openssl req \
        -x509 \
        -new \
        -nodes \
        -key $OUTPUT_DIR/"${KEY_NAME}" \
        -sha512 \
        -passin file:"${PASSPHRASE_FILE}" \
        -days 1825 \
        -subj "$(cat $ROOT_SUBJECT_FILE)" \
        -out $OUTPUT_DIR/"${PEM_NAME}"
}

function generate_certificate() {
    local CSR=$1
    local PEM=$2
    local PEM_KEY=$3
    local CRT_NAME=$4
    local EXT_NAME=$5
    openssl x509 \
        -req \
        -in $OUTPUT_DIR/${CSR} \
        -CA $OUTPUT_DIR/${PEM} \
        -CAkey $OUTPUT_DIR/${PEM_KEY} \
        -CAcreateserial \
        -out $OUTPUT_DIR/${CRT_NAME} \
        -passin file:"${PASSPHRASE_FILE}" \
        -days 825 \
        -sha512 \
        -extfile ${EXT_NAME}
}

function read_pem() {
    local PEM_NAME=${2}
    openssl x509 -in $OUTPUT_DIR/"${PEM_NAME}" -noout -text
}

$@