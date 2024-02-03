package net.acesinc.data.json.util;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator;
import io.crossbar.autobahn.wamp.interfaces.ISession.OnConnectListener;
import io.crossbar.autobahn.wamp.interfaces.ISession.OnJoinListener;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.TransportOptions;

public class WampClient {

	final Logger logger = Logger.getLogger(WampClient.class.getName());
    String url;
    String realm;
    Session session;
    private OnConnectHandler onConnectHandler;

    public WampClient(String url, String realm, OnConnectHandler onConnectHandler2) {
        this.url = url;
        this.realm = realm;
        this.onConnectHandler = onConnectHandler2;
        try {
            _start();
        } catch (Exception e) {
            logger.severe("Error in WAMP init: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void _start() throws Exception {
        try {
            this.session = new Session();
            /*  OnConnect... */
            session.addOnConnectListener(new OnConnectListener() {
                @Override
                public void onConnect(Session arg0) {
                    System.out.println("onConnect success = " + url + " | " + realm);
                }
            });

            /*  OnJoin...   */
            session.addOnJoinListener(new OnJoinListener() {
                @Override
                public void onJoin(Session _session, SessionDetails details) {
                    System.out.println("onJoin = " + url + " | " + realm);
                    if (onConnectHandler != null) {
                        onConnectHandler.connected(session);
                    }
                }
            });

            // finally, provide everything to a Client and connect
            Client client = new Client(session, this.url, this.realm, new ArrayList<IAuthenticator>());
            TransportOptions transportOptions = new TransportOptions();
            transportOptions.setMaxFramePayloadSize(128 * 60000);
            transportOptions.setAutoPingTimeout(5);
            transportOptions.setAutoPingInterval(10);
            CompletableFuture<ExitInfo> exitInfoCompletableFuture = client.connect(transportOptions);
        } catch (Exception e) {
            System.err.println("Got an error in WAMP service " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    public void publish(String topic, String message) {
        this.session.publish(topic, message);
    }

    public void close() {
        try {
            this.session.leave("Disconnecting...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
