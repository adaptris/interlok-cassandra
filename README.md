# interlok-cassandra

[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-cassandra.svg)](https://github.com/adaptris/interlok-cassandra/tags) [![codecov](https://codecov.io/gh/adaptris/interlok-cassandra/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-cassandra) [![Total alerts](https://img.shields.io/lgtm/alerts/g/adaptris/interlok-cassandra.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/adaptris/interlok-cassandra/alerts/) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/adaptris/interlok-cassandra.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/adaptris/interlok-cassandra/context:java)

Using Interlok to interact with Cassandra


## Unit tests
If you want to run all the tests against an actual cassandra environment there is a docker-compose available in src/test/docker

```
$ cd src/test/docker
$ docker-compose up
```
After that, create a `src/test/resources/unit-tests.properties.template.[hostname]` based on unit-tests.properties.template and change the `CassandraServiceCase.enabled=true`.