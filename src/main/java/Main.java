import com.github.catalystcode.fortis.speechtotext.websocket.MessageHandler;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class Main {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);
    }

    public static void main(String[] args) throws Exception {
        final String serverUrl = "wss://echo.websocket.org";

        CountDownLatch countDownLatch = new CountDownLatch(1);
        MessageHandler handler = new MessageHandler(countDownLatch);
        WebSocketClient client = new WebSocketClient(new SslContextFactory());
        try {
            client.start();
            Session session = client.connect(handler, new URI(serverUrl), new ClientUpgradeRequest()).get();
            RemoteEndpoint remote = session.getRemote();
            remote.sendString("hello");
            remote.sendString("world");
            session.close();
        } finally {
            countDownLatch.await();
            client.stop();
        }
    }
}
