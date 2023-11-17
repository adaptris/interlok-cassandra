# interlok-cassandra

[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-cassandra.svg)](https://github.com/adaptris/interlok-cassandra/tags)
[![license](https://img.shields.io/github/license/adaptris/interlok-cassandra.svg)](https://github.com/adaptris/interlok-cassandra/blob/develop/LICENSE)
[![Actions Status](https://github.com/adaptris/interlok-cassandra/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/adaptris/interlok-cassandra/actions)
[![codecov](https://codecov.io/gh/adaptris/interlok-cassandra/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-cassandra)
[![CodeQL](https://github.com/adaptris/interlok-cassandra/workflows/CodeQL/badge.svg)](https://github.com/adaptris/interlok-cassandra/security/code-scanning)
[![Known Vulnerabilities](https://snyk.io/test/github/adaptris/interlok-cassandra/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/adaptris/interlok-cassandra?targetFile=build.gradle)
[![Closed PRs](https://img.shields.io/github/issues-pr-closed/adaptris/interlok-cassandra)](https://github.com/adaptris/interlok-cassandra/pulls?q=is%3Apr+is%3Aclosed)

Using Interlok to interact with Cassandra


## Unit tests
If you want to run all the tests against an actual cassandra environment there is a docker-compose available in src/test/docker

```
$ cd src/test/docker
$ docker-compose up
```
After that, create a `src/test/resources/unit-tests.properties.template.[hostname]` based on unit-tests.properties.template and change the `CassandraServiceCase.enabled=true`.