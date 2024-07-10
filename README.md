# WireMockAuthExtension
extension of wiremock AdminRequestFilterV2 to enable basic auth

# reason for this repo
- to create a wiremock extension jar for securing `__admin` endpoints with basic authentication
- to use as an extension in wiremock docker (see [example](https://github.com/NavGitGood/WireMockDockerWithExtension))

# how to generate the jar
1. clone the project `git clone https://github.com/NavGitGood/WireMockAuthExtension.git`
2. refresh gradle dependencies
3. run `./gradlew shadowJar` to generate the jar
4. get the generated jar from `build/libs`

# how to use
1. clone a example project `git clone https://github.com/NavGitGood/WireMockDockerWithExtension.git`
2. copy the generated jar in `./extensions` directory
3. follow steps from https://github.com/NavGitGood/WireMockDockerWithExtension/blob/main/README.md

# reference
https://wiremock.org/docs/extending-wiremock/