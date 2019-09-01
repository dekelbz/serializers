import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import sun.misc.SharedSecrets;
import sun.nio.cs.ArrayDecoder;

import java.nio.charset.Charset;

/**
 * Ultra fast string serializer for Kryo serializer. The read() method strips many of the overhead of
 * byte[] to string conversion, leaving only the bare essentials.
 *
 * User this serializer by adding to your code:
 * kryo.register(String.class, new StringSerializer());
 */
public class StringSerializer extends Serializer<String> {

    private static final Charset CHARSET = Charset.defaultCharset();
    private final ArrayDecoder arrayDecoder = (ArrayDecoder) CHARSET.newDecoder();

    public StringSerializer() {
        super(true, true);
    }

    public void write(Kryo kryo, Output output, String string) {
        if (string == null) {
            output.writeInt(-1);
        } else {
            output.writeInt(string.length());
            output.write(string.getBytes(CHARSET));
        }
    }

    public String read(Kryo kryo, Input input, Class<String> aClass) {
        int stringLength = input.readInt();
        if (stringLength == -1) {
            return null;
        }
        char[] stringAsChars = new char[stringLength];
        arrayDecoder.decode(input.getBuffer(), input.position(), stringLength, stringAsChars);
        input.setPosition(input.position() + stringLength);

        return SharedSecrets.getJavaLangAccess().newStringUnsafe(stringAsChars);
    }
}
