import com.github.catalystcode.fortis.speechtotext.websocket.*;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceUrl;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

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

        CountDownLatch socketCloseLatch = new CountDownLatch(1);
        MessageHandler handler = new MessageHandler(socketCloseLatch);
        SpeechServiceUrl url = new SpeechServiceUrl(key, endpoint, format, locale);
        SpeechServiceClient client = new SpeechServiceClient(url, handler);
        try {
            Session session = client.start().get();
            MessageSender sender = new MessageSender(session.getRemote());
            sender.sendConfiguration();
            sender.sendAudio(wavStream);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            socketCloseLatch.await();
            client.stop();
            wavStream.close();
        }
    }
}
