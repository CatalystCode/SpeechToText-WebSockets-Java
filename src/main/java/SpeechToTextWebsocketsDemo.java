import com.github.catalystcode.fortis.speechtotext.SpeechTranscriber;
import com.github.catalystcode.fortis.speechtotext.config.Endpoint;
import com.github.catalystcode.fortis.speechtotext.config.Format;
import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

public class SpeechToTextWebsocketsDemo {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
    }

    public static void main(String[] args) throws Exception {
        final String subscriptionKey = System.getenv("OXFORD_SPEECH_TOKEN");
        final Endpoint endpoint = Endpoint.DICTATION;
        final Format format = Format.SIMPLE;
        final Locale locale = new Locale("en-US");
        final String wavPath = args[0];

        InputStream wavStream = new BufferedInputStream(new FileInputStream(wavPath));
        SpeechServiceConfig config = new SpeechServiceConfig(subscriptionKey, endpoint, format, locale);

        SpeechTranscriber transcriber = new SpeechTranscriber(config);
        try {
            transcriber.transcribe(wavStream, System.out::println);
        } finally {
            wavStream.close();
        }
    }
}
