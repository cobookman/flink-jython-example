package com.amazonaws.services.kinesisanalytics;

import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

class PyFlatMap extends RichFlatMapFunction<String, String> {
    private PythonInterpreter interp;
    private PyObject handler;

    /**
     * Instantiates a new Jython environment per class, and loads python source code from jar/filesystem.
     *
     * Expects events to be passed to a function called 'handler'. Future modification could be done to
     * use a flink/kda runtime parameter to specify the python function to call.
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.interp = new PythonInterpreter();

        // import handler.py file
        this.interp.exec("from src.python.handler import handler");

        // grabs pointer to the handler function.
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
        if (this.handler != null) {
            PyObject out = this.handler.__call__(new PyString(s));
            collector.collect(out.toString().replace("\n", " "));
        }
    }
}

