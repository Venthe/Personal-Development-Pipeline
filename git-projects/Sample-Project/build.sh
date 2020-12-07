#  docker run --rm -it \
#     --volume $(pwd):/workspace \
#     gcr.io/kaniko-project/executor:latest \
#     --dockerfile /workspace/Dockerfile \
#     --no-push \
#     --context dir:///workspace/ \
#     --verbosity debug


# docker run --rm -it --entrypoint ash \
#     --privileged \
#     --volume $(pwd):/workspace \
#     docker:stable-dind

DOCKER_BUILDKIT=1 docker build . --progress=plain
# docker run --rm -it \
#     --volume $(pwd):/workspace \
#     docker:latest -- build /workspace/Dockerfile