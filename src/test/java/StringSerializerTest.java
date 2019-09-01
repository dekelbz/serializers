import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StringSerializerTest {

    private static final Charset CHARSET = Charset.defaultCharset();

    @Mock
    private Kryo kryo;
    @Mock
    private Input input;
    @Mock
    private Output output;

    private StringSerializer stringSerializer;

    @Before
    public void setUp() {
        stringSerializer = new StringSerializer();
    }

    @Test
    public void shouldWrite() {
        //given
        String string = "example string";

        //when
        stringSerializer.write(kryo, output, string);

        //then
        verify(output).writeInt(string.length());
        verify(output).write(string.getBytes(CHARSET));
    }

    @Test
    public void shouldWriteNull() {
        //when
        stringSerializer.write(kryo, output, null);

        //then
        verify(output).writeInt(-1);
        verifyNoMoreInteractions(output);
    }

    @Test
    public void shouldRead() {
        //given
        String string = "example string";
        given(input.readInt()).willReturn(string.length());
        byte[] buffer = "ab".concat(string).concat("cd").getBytes(CHARSET);
        given(input.getBuffer()).willReturn(buffer);
        int inputOriginalPosition = 2;
        given(input.position()).willReturn(inputOriginalPosition);

        //when
        String returnedString = stringSerializer.read(kryo, input, String.class);

        //then
        assertThat(returnedString, is(string));
        verify(input).setPosition(inputOriginalPosition + string.length());
    }
}