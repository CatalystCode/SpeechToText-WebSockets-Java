import com.github.catalystcode.fortis.speechtotext.Transcriber;
import com.github.catalystcode.fortis.speechtotext.config.OutputFormat;
import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.config.SpeechType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

public class SpeechToTextWebsocketsDemo {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) throws Exception {
        final String subscriptionKey = System.getenv("OXFORD_SPEECH_TOKEN");
        final SpeechType speechType = SpeechType.CONVERSATION;
        final OutputFormat outputFormat = OutputFormat.SIMPLE;
        final String audioPath = args[0];
        final Locale locale = Locale.forLanguageTag(args.length > 1 ? args[1] : "en-US");
        final String audioType = args.length > 2 ? args[2] : audioPath;

        SpeechServiceConfig config = new SpeechServiceConfig(subscriptionKey, speechType, outputFormat, locale);

        try (InputStream audioStream = openStream(audioPath)) {
            Transcriber.create(audioType, config).transcribe(audioStream, SpeechToTextWebsocketsDemo::onPhrase, SpeechToTextWebsocketsDemo::onHypothesis,
                    SpeechToTextWebsocketsDemo::onTurnStart, SpeechToTextWebsocketsDemo::onTurnEnd);
        }
    }

    private static InputStream openStream(String audioPath) throws IOException {
        InputStream inputStream = audioPath.startsWith("http://") || audioPath.startsWith("https://")
            ? new URL(audioPath).openConnection().getInputStream()
            : new FileInputStream(audioPath);

        return new BufferedInputStream(inputStream);
    }

    private static void onTurnEnd() {
        System.out.println("TurnEnd:");
    }

    private static void onPhrase(String phrase) {
        System.out.println("Phrase: " + phrase);
    }

    private static void onHypothesis(String hypothesis) {
        System.out.println("Hypothesis: " + hypothesis);
    }

    private static void onTurnStart(String serviceTag) {
        System.out.println("TurnStart: " + serviceTag);
    }
}
