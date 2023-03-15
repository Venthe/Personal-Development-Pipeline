# TODO

## Immediate TODO

- Add waiting for host being present in ansible
- Review security in terms of
  - secrets
  - password generation
  - certificate rotation
- Security Certificates/RBAC per container
- Investigate autoscaling for pods
- Verify problems with prometheus targets
  - https://prometheus.home.arpa/targets?search=
- Observability:
  - Add alerts for nodes
- Automatic backup for the cluster
  - Manual CRON job
  - Velero
- Auto unseal for vault
- Add retry and a queue to mediator. Simple UI to retrigger?
- Better handling of related NPM packages
  - Currently NPM packages are rebuild manually. NPM workspaces?
- Investigate Oauth2 / Dex
- Add nexus passthrough
  - APT
  - Maven
- Add synchronizer for
  - Opensearch
  - ETCD
  - Redis
- Add LDAP to Kubernetes dashboard
  - https://computingforgeeks.com/kubernetes-and-active-directory-integration/

## Services to deploy

- PlantUML: https://artifacthub.io/packages/helm/stevehipwell/plantuml
- Project tracking:
  - Redmine
- Documentation
  - Ghost
  - Xwiki
  - Dokuwiki
  - Mediawiki
- Communication
  - Mail server
  - RocketChat
- Continuous Integration
  - Sonarqube
- OnCall
  - https://github.com/grafana/oncall
- Backstage
    - How to use it best
- Tracing
  - Zipkin
  - Jaeger
