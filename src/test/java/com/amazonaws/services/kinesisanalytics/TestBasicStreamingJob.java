package com.amazonaws.services.kinesisanalytics;
import static java.nio.file.FileSystems.newFileSystem;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.net.URISyntaxException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;


public class TestBasicStreamingJob {
    /**
     * Tests that it can find the python scripts in the src/python folder or jar archive depending on environment run
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testPythonWalking() throws URISyntaxException, IOException {
//        final AtomicInteger i = new AtomicInteger();
//        PythonInterpreter interp = new PythonInterpreter();
//        interp.exec("from src.python.handler import handler");
//        PyObject handler = interp.get("handler");
//        assertNotEquals(null, handler);
    }
}
