from library.system import yq


def update_helm_property(namespace, application_name, property_key, property_value):
    return yq(["-i",
               "-e",
               f'. | (.helm.{application_name} | .properties.{property_key}) = "{property_value}"',
               f"./environment/{namespace}/applications.yml"
               ])


def update_helm_chart_version(namespace, application_name, chart_version):
    return yq(["-i",
               "-e",
               f'. | (.helm.{application_name} | .chartRevision) = "{chart_version}"',
               f"./environment/{namespace}/applications.yml"
               ])
