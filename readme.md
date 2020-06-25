# jython

As currently configured, a python 2.7 environment is exposed in a Flink pipeline. Python3 is not currently supported by Jython, but it has been in the works for a while. Jython is still in active development.


# Adding a python library

add all python library source code you'd like under the /Lib directory

To install say requests simply run:
`pip install requests -t ./Lib`

# Setup

Installing flink-connector-kinesis:
```
$ wget -qO- https://github.com/apache/flink/archive/release-1.8.2.zip | bsdtar -xf-
cd flink-release-1.8.2/
mvn clean install -DskipTests -Pinclude-kinesis -pl flink-connectors/flink-connector-kinesis/
```
