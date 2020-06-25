# Setup - AWS

1. Create an S3 bucket. Upload both the jar `aws-kinesis-analytics-java-apps-1.0.jar`, and a newline seperated document containing a list of records to process in `source.json`.
2. Create a new Flink KDA Application.
3. Add IAM permissions for the KDA Pipeline to be able to read/write to the S3 Bucket.
4. Click on the `Configure` tab in the KDA Application view. Then make the following changes:

under s3 bucket:
```
 bucket: s3://<your-bucket>
 Path to S3 Amazon Object: aws-kinesis-analytics-java-apps-1.0.jar
```

under properties:
```
Group ID: s3
 - Key: s3SinkPath
 - Value: s3://<your-bucket>/output

 - Key: s3SourceFile
 - Value: s3://<your-bucket>/source.json
```

# Setup - Local Development

To compile the pipeline, and re-bundle changes to the python codebase, you'll need to make a few installs.


## Maven

Maven's official install directions can be found here: https://maven.apache.org/install.html

If you are on os-x with homebrew install you can simply run: `$ brew install maven`.



## flink-connector-kinesis

First install the flink connector for kinesis. This should only be required if you use a kinesis stream as either the input or output source. The maven `pom.xml` file does reference `flink-connector-kinesis` and will fail without this setup.

The following will download the flink 1.8.2 source and compile/install the flink-connector-kinesis to your system.
```
$ wget -qO- https://github.com/apache/flink/archive/release-1.8.2.zip | bsdtar -xf-
cd flink-release-1.8.2/
mvn clean install -DskipTests -Pinclude-kinesis -pl flink-connectors/flink-connector-kinesis/
``` 


## Creating the jar

Simply run `$ mvn package` in the project's root directory. Once completed you'll find the Flink Jar binary stored in `target/aws-kinesis-analytics-java-apps-1.0.jar`

You'll be copying this file to `s3://<your-bucket>/aws-kinesis-analytics-java-apps-1.0.jar`



# Jython/Python info

Jython provides a python 2.7 environment in the JVM. Python3 is not currently supported by Jython, but there's active work on exposing a python3 Jython environment. Jython is still very much in active development.

The one limitation of Jython is that any python module which bundles in non-python code/CPython will fail to run. This means libraries such-as Numpy will not run in the Jython environment.

## Adding a python library

Add your library source code under the `Lib/` directory. You can have pip do this for you by running:
`$ pip install requests -t ./Lib`


## Adding python source code

Your actual python source code is stored in the `src/python/` directory.

As currently configured, the pipeline expects a file with a function of `def handler(event)` to be located in `src/python/handler.py`. This `handler` function is the main entry point, and is called for each line of data in `s3://<your-bucket>/sources.json`.

