#!/usr/bin/env bash

ansible-playbook ./provision-kubernetes.yml --list-tags
ansible-playbook ./provision-kubernetes.yml --syntax-check
