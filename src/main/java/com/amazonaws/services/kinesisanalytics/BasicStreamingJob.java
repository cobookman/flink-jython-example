package com.amazonaws.services.kinesisanalytics;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import scala.util.parsing.json.JSON;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * A basic Kinesis Data Analytics for Java application with Kinesis data
 * streams as source and sink.
 */
public class BasicStreamingJob {

    /**
     * Writes stream to s3
     * @param stream
     */
    private static void writeStreamToS3(DataStream<String> stream) throws IOException {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy.MM.dd.HH.mm.ss");

        stream.writeAsText("/Users/boocolin/output-" + fmt.print(dt)+".json");
//        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
//        String s3SinkPath = (String) applicationProperties.get("s3").get("s3SinkPath");
//
//        stream.writeAsText(s3SinkPath);
    }

    /**
     * Sets pipeline Source to a single s3 file which contains newline seperated json records for each source.
     * @param env
     * @return
     */
    private static DataStream<String> createSourceFromS3(StreamExecutionEnvironment env) throws IOException {
//        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
//        String s3SourceFile = (String) applicationProperties.get("s3").get("s3SourceFile");
//
//        return env.readTextFile(s3SourceFile, "UTF-8");
        return env.readTextFile("/Users/boocolin/source.json", "UTF-8");
    }

    private static class PythonWrapper extends RichFlatMapFunction<String, String> {
        private static PythonInterpreter interp;
        private static PyObject handler;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            this.interp = new PythonInterpreter();

            String pythonScript = ""
                    + "import requests\n"
                    + "import json\n"
                    + "def handler(event):\n"
                    + "  event = json.loads(event)\n"
                    + "  resp = requests.get(event['url'], headers={\"Accept-Encoding\":\"deflate\"})\n"
                    + "  out = {'resp': resp.text, 'event': event}\n"
                    + "  return json.dumps(out)\n";
            this.interp.exec(pythonScript);
            this.handler = this.interp.get("handler");
        }

        @Override
        public void close() throws Exception {
            super.close();
            if (this.interp != null) {
                this.interp.close();
            }
        }

        @Override
        public void flatMap(String s, Collector<String> collector) throws Exception {
            PyObject out = this.handler.__call__(new PyString(s));
            collector.collect(out.toString().replace("\n", " "));
        }
    }

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Read in data from S3
        DataStream<String> input = createSourceFromS3(env);

        // Transfor each newline seperated json data
        DataStream<String> transformed = input.flatMap(new PythonWrapper());

        // Write result to s3
        writeStreamToS3(transformed);

        env.execute("Demo Pipeline");
    }
}
