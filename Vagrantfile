VAGRANTFILE_API_VERSION ||= "2"
IMAGE_NAME ||= "generic/ubuntu2010"
IMAGE_VERSION ||= "3.2.18"
DEPLOYMENT_NAME ||= "kubernetes-"
SUBNET_IP_BASE ||= "192.168.3"
CONTAINERD_VERSION ||= "1.4.4"
KUBERNETES_VERSION ||= "1.21"

# TODO: Explore HA control plane
#  Currently limited to 1
MAIN_NODE_NAME ||= "#{DEPLOYMENT_NAME}main-node"
MAIN_NODE_MEMORY ||= 2048
MAIN_NODE_CPU_COUNT ||= 2
MAIN_NODE_TAINT ||= true
MAIN_NODE_ADAPTER_ID ||= 1

WORKER_NODE_COUNT ||= 3
WORKER_NODE_NAME ||= "#{DEPLOYMENT_NAME}worker-node"
WORKER_NODE_MEMORY ||= 8192
WORKER_NODE_CPU_COUNT ||= 4

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.box = IMAGE_NAME
    config.vm.box_version = IMAGE_VERSION

    config.vm :hyperv do |hyperv|
        hyperv.provider.enable_virtualization_extensions = true
        hyperv.provider.linked_clone = true
        hyperv.synced_folder ".", "/vagrant", disabled: true
        hyperv.provider "hyperv"
    end

    config.vm.define "#{MAIN_NODE_NAME}" do |mainNode|
        mainNode.vm.provider :virtualbox do |virtualbox|
            mainNode.vm.network "private_network", ip: "#{SUBNET_IP_BASE}.#{50}", :adapter => 2
            virtualbox.name = "#{MAIN_NODE_NAME}"
            config.vm.network :forwarded_port, guest: 22, host: 3200, id: 'ssh'
        end
        [:virtualbox, :hyperv].each do |provider|
            mainNode.vm.provider provider do |vm_provider|
                vm_provider.memory = MAIN_NODE_MEMORY
                vm_provider.cpus = MAIN_NODE_CPU_COUNT
            end
        end
        mainNode.vm.provider :hyperv do |hyperv|
            hyperv.vmname = "#{MAIN_NODE_NAME}"
            hyperv.maxmemory = MAIN_NODE_MEMORY
        end

        mainNode.vm.hostname = "#{MAIN_NODE_NAME}"
        if MAIN_NODE_TAINT == false
            mainNode.vm.disk :disk, name: "main-node-cephfs", size: "100GB"
        end

        # TODO: Inventory file
        mainNode.vm.provision "control-plane", type: "ansible" do |ansible|
            ansible.playbook = "provision-kubernetes.yml"
            ansible.extra_vars = {
                containerd_version: "#{CONTAINERD_VERSION}"
                kubernetes_version: "#{KUBERNETES_VERSION}"
                taint: MAIN_NODE_TAINT,
                node:  "#{MAIN_NODE_NAME}",
                adapter_id: MAIN_NODE_ADAPTER_ID
            }
        end
        mainNode.vm.provision "rook", type: "ansible" do |ansible|
            ansible.playbook = "deprecated/1.7_csi-rook.yml"
            ansible.extra_vars = {
                rook_version: "release-1.5",
                reset: false,
                namespace: "rook-ceph"
            }
        end
    end

    (1..WORKER_NODE_COUNT).each do |i|
        config.vm.define "#{WORKER_NODE_NAME}-#{i}" do |workerNode|
            workerNode.vm.provider :virtualbox do |virtualbox|
                workerNode.vm.network "private_network", ip: "#{SUBNET_IP_BASE}.#{150 + i}", :adapter => 2
                virtualbox.name = "#{WORKER_NODE_NAME}-#{i}"
            end
            [:virtualbox, :hyperv].each do |provider|
                workerNode.vm.provider provider do |vm_provider|
                    vm_provider.memory = WORKER_NODE_MEMORY
                    vm_provider.cpus = WORKER_NODE_CPU_COUNT
                end
            end
            workerNode.vm.provider :hyperv do |hyperv|
                hyperv.vmname = "#{WORKER_NODE_NAME}-#{i}"
                hyperv.maxmemory = WORKER_NODE_MEMORY
            end

            workerNode.vm.hostname = "#{WORKER_NODE_NAME}-#{i}"
            workerNode.vm.disk :disk, name: "worker-node-cephfs-#{i}", size: "100GB"
            
            # TODO: Inventory file
            workerNode.vm.provision "worker-node" , type: "ansible" do |ansible|
                ansible.playbook = "provision-kubernetes.yml"
                ansible.extra_vars = {
                    containerd_version: "#{CONTAINERD_VERSION}"
                    kubernetes_version: "#{KUBERNETES_VERSION}"
                }
            end
        end
    end
end
