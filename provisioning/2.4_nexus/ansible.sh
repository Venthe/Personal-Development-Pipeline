#!/usr/bin/env bash

ansible-playbook -i kubernetes-node-a, --user=jlipiec --private-key=~/.ssh/id_rsa --ask-become ./ansible.yml
