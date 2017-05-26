import com.github.catalystcode.fortis.speechtotext.config.Endpoint;
import com.github.catalystcode.fortis.speechtotext.config.Format;
import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;
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
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) throws Exception {
        final String key = System.getenv("OXFORD_SPEECH_TOKEN");
        final Endpoint endpoint = Endpoint.CONVERSATION;
        final Format format = Format.SIMPLE;
        final Locale locale = new Locale("en-US");
        final InputStream wavStream = new BufferedInputStream(new FileInputStream(args[0]));

        SpeechServiceConfig config = new SpeechServiceConfig(key, endpoint, format, locale);
        MessageReceiver receiver = new MessageReceiver();

        SpeechServiceClient client = new NvSpeechServiceClient();
        try {
            MessageSender sender = client.start(config, receiver);
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
