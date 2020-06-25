package com.amazonaws.services.kinesisanalytics;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        String s3SinkPath = (String) applicationProperties.get("s3").get("s3SinkPath");
        stream.writeAsText(s3SinkPath + "-" + fmt.print(dt));

        /**
         * If wanting to write to local filesystem use statement such-as:
         * stream.writeAsText("/Users/boocolin/output-" + fmt.print(dt)+".json");
         */
    }

    /**
     * Sets pipeline Source to a single s3 file which contains newline seperated json records for each source.
     * @param env
     * @return
     */
    private static DataStream<String> createSourceFromS3(StreamExecutionEnvironment env) throws IOException {
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        String s3SourceFile = (String) applicationProperties.get("s3").get("s3SourceFile");

        return env.readTextFile(s3SourceFile, "UTF-8");
        /**
         * If wanting to read from local filesystem, use statement such-as:
         * return env.readTextFile("/Users/boocolin/source.json", "UTF-8");
         */
    }

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Read in data from S3
        DataStream<String> input = createSourceFromS3(env);

        // Transfor each newline seperated json data
        DataStream<String> transformed = input.flatMap(new PyFlatMap());

        // Write result to s3
        writeStreamToS3(transformed);

        env.execute("Demo Pipeline");
    }
}
