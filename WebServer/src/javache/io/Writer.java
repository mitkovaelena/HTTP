package javache.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class Writer {
    private Writer() {
    }

    public static void writeBytes(byte[] responseContent, OutputStream csOutputStream) throws IOException {
        DataOutputStream writer = new DataOutputStream(csOutputStream);

        writer.write(responseContent);
    }
}
