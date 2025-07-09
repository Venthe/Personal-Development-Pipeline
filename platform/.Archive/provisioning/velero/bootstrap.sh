#!/usr/bin/env bash

NAMESPACE="backup"
RELEASE_NAME="velero"

helm repo add vmware-tanzu https://vmware-tanzu.github.io/helm-charts

helm uninstall \
    --namespace="${NAMESPACE}" \
    "${RELEASE_NAME}" 2>/dev/null 1>/dev/null || true

# TODO: Move to ZFS znapshotter?
cat <<EOF > values.yml
deployRestic: true
# Disabled due to restic
snapshotsEnabled: false
backupsEnabled: false
restic:
  privileged: false
schedules:
  mainBackup:
    disabled: true
    # labels:
    #   myenv: foo
    # annotations:
    #   myenv: foo
    schedule: "1/30 0 * * *"
    useOwnerReferencesInBackup: false
    template:
      ttl: "240h"
      # includedNamespaces:
      # - foo

# Init containers to add to the Velero deployment's pod spec. At least one plugin provider image is required.
# If the value is a string then it is evaluated as a template.
#initContainers:
#  - name: velero-plugin-for-csi
#    image: velero/velero-plugin-for-csi:v0.3.0
#    imagePullPolicy: IfNotPresent
#    volumeMounts:
#      - mountPath: /target
#        name: plugins
#features: EnableCSI

##
## Parameters for the 'default' BackupStorageLocation and VolumeSnapshotLocation,
## and additional server settings.
##
configuration:
  provider: custom
EOF
# credentials:
#     secretContents:
#         cloud: <FULL PATH TO FILE>
# configuration:
#     provider: <PROVIDER NAME>
#     backupStorageLocation:
#         name: <BACKUP STORAGE LOCATION NAME>
#         bucket: <BUCKET NAME>
#         config:
#         region: <REGION>
#     volumeSnapshotLocation:
#         name: <VOLUME SNAPSHOT LOCATION NAME>
#         config:
#         region: <REGION>
#     initContainers:
#     - name: velero-plugin-for-<PROVIDER NAME>
#         image: velero/velero-plugin-for-<PROVIDER NAME>:<PROVIDER PLUGIN TAG>
#         volumeMounts:
#         - mountPath: /target
#             name: plugins

helm upgrade \
    --install \
    --create-namespace \
    --namespace="${NAMESPACE}" \
    --values="./values.yml" \
    "${RELEASE_NAME}" \
    vmware-tanzu/velero