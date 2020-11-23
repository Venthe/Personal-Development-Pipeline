#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
# if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

git clone https://exxsyseng@bitbucket.org/exxsyseng/nfs-provisioning.git

cd nfs-provisioning
kubectl create -f rbac.yaml
# kubectl get clusterrole,clusterrolebinding,role,rolebinding | grep nfs
kubectl create -f class.yaml
# sed "s/<<>>/nfs/" greetings.txt
# kubectl create -f deployment.yaml
cd -
cat <<EOF > kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
    - nfs-provisioning/deployment.yaml

patchesStrategicMerge:
    - deployment-patch.yml
EOF
cat <<EOF > deployment-patch.yml
kind: Deployment
apiVersion: apps/v1
metadata:
  name: nfs-client-provisioner
spec:
    template:
        spec:
          containers:
            - name: nfs-client-provisioner
              env:
                - name: NFS_SERVER
                  value: nfs.local
          volumes:
            - name: nfs-client-root
              nfs:
                server: nfs.local
                path: /srv/nfs/kubedata
EOF
kubectl apply -k ./
# kubectl get all
#kubectl kustomize ./

kubectl patch storageclass managed-nfs-storage -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'