package net.acesinc.data.json.util;

import io.crossbar.autobahn.wamp.Session;

public interface OnConnectHandler {
    void connected(Session session);
}
