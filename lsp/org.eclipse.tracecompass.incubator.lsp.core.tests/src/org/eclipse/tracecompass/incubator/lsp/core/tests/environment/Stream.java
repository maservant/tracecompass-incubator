package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Class that returns à PipedInputStream and a PipedOutputStream
 * @author maxtibs
 *
 */
public class Stream {

    public PipedInputStream read;
    public PipedOutputStream write;

    public Stream() {
        write = new PipedOutputStream();
        read = new PipedInputStream();

        try {
            write.connect(read);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}