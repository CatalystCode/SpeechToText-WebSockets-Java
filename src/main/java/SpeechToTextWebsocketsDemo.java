import com.github.catalystcode.fortis.speechtotext.SpeechTranscriber;
import com.github.catalystcode.fortis.speechtotext.config.SpeechType;
import com.github.catalystcode.fortis.speechtotext.config.OutputFormat;
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
        final SpeechType speechType = SpeechType.DICTATION;
        final OutputFormat outputFormat = OutputFormat.SIMPLE;
        final Locale locale = new Locale("en-US");
        final String wavPath = args[0];

        SpeechServiceConfig config = new SpeechServiceConfig(subscriptionKey, speechType, outputFormat, locale);

        SpeechTranscriber transcriber = new SpeechTranscriber(config);
        try (InputStream wavStream = new BufferedInputStream(new FileInputStream(wavPath))) {
            transcriber.transcribe(
                wavStream,
                message -> System.out.println("Phrase: " + message),
                hypothesis -> System.out.println("Hypothesis: " + hypothesis));
        }
    }
}
