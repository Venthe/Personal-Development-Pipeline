docker build . --tag venthe/repository-templater:latest
docker run \
    --env GIT_USERNAME=test \
    --env GIT_EMAIL=test@test.test \
    --rm \
    --interactive \
    --entrypoint=bash \
    venthe/repository-templater:latest