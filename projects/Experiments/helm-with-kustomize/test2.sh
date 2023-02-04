#!/usr/bin/env bash

function _yq {
    yq ea '[.] | ... comments="" | sort_by(.apiVersion) | sort_keys(..) | .[] | splitDoc'  $1
}

diff <(_yq all.yaml) <(_yq result.yaml)
