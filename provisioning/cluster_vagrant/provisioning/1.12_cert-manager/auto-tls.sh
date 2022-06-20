kubectl get ingress \
    --all-namespaces \
    --output jsonpath='{.items}' \
    | jq '.[] | 
    {
        "patch": {
            "name": .metadata.name, 
            "namespace": .metadata.namespace,
            "annotations": (
                . = .metadata.annotations |
                to_entries |
                map(. = .key) |
                [(
                    .[] |
                    select(
                        . |
                        test("cert-manager.io")
                    )
                )]
            ),
            "hosts": [.spec.rules[].host]
        },
        "original": .
    }
    '