import com.github.catalystcode.fortis.speechtotext.websocket.*;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.websocket.jetty.JettySpeechServiceClient;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

public class Main {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) throws Exception {
        final String key = System.getenv("OXFORD_SPEECH_TOKEN");
        final Endpoint endpoint = Endpoint.CONVERSATION;
        final Format format = Format.SIMPLE;
        final Locale locale = new Locale("en-US");
        final InputStream wavStream = new BufferedInputStream(new FileInputStream(args[0]));

        SpeechServiceConfig url = new SpeechServiceConfig(key, endpoint, format, locale);
        MessageReceiver receiver = new MessageReceiver();

        SpeechServiceClient client = new JettySpeechServiceClient();
        try {
            MessageSender sender = client.start(url, receiver);
            sender.sendConfiguration();
            sender.sendAudio(wavStream);
            client.awaitEnd();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            client.stop();
            wavStream.close();
        }
    }
}
