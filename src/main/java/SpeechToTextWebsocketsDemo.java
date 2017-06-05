import com.github.catalystcode.fortis.speechtotext.Transcriber;
import com.github.catalystcode.fortis.speechtotext.WavTranscriber;
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
        final String audioPath = args[0];

        SpeechServiceConfig config = new SpeechServiceConfig(subscriptionKey, speechType, outputFormat, locale);

        Transcriber transcriber;
        if (audioPath.endsWith(".wav")) {
            transcriber = new WavTranscriber(config);
        } else {
            throw new IllegalArgumentException("Unsupported audio file type: " + audioPath);
        }

        try (InputStream audioStream = new BufferedInputStream(new FileInputStream(audioPath))) {
            transcriber.transcribe(audioStream, SpeechToTextWebsocketsDemo::onPhrase, SpeechToTextWebsocketsDemo::onHypothesis);
        }
    }

    private static void onPhrase(String phrase) {
        System.out.println("Phrase: " + phrase);
    }

    private static void onHypothesis(String hypothesis) {
        System.out.println("Hypothesis: " + hypothesis);
    }
}
