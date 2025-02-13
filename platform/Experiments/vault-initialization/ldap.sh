#!/usr/bin/env bash

vault auth enable ldap

ldap_url="ldap://openldap:389"
ldap_userdn="ou=people,dc=home,dc=arpa"
ldap_groupdn="ou=groups,dc=home,dc=arpa"

vault write \
  auth/ldap/config \
  binddn="cn=readonly,dc=home,dc=arpa" \
  bindpass="readonly" \
  url="${ldap_url}" \
  userdn="${ldap_userdn}" \
  groupdn="${ldap_groupdn}" \
  userattr="uid" \
  discoverdn=true

vault write auth/ldap/groups/Administrators policies=admin
vault write "auth/ldap/groups/Non-Interactive Users" policies=readonly-infrastructure
