package ru.apertum.qsystem.server.http;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.common.WebSocketRemoteEndpoint;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.jar.JarOutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Тест на то, что вообще этот вызов по веэбсокету нормально обрабатывает и что-от отдает нормальное.
 */
public class QWebSocketHandlerTest {

    QWebSocketHandler handler;

    @BeforeTest
    public void init() {
        handler = new QWebSocketHandler();
    }

    /**
     * Тест на то, что вообще этот вызов по веэбсокету нормально обрабатывает и что-от отдает нормальное.
     *
     * @throws Exception что-то упало.
     */
    @Test
    public void testOnMessage() throws Exception {
        Session session = mock(WebSocketSession.class);
        // session.getRemoteAddress().getAddress()
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1000));
        // session.getRemote().sendString("Got your connect message");
        RemoteEndpoint endpoint = mock(RemoteEndpoint.class);
        when(session.getRemote()).thenReturn(endpoint);

        handler.onConnect(session);
        handler.onMessage("bad msg");
        handler.onMessage("{\"jsonrpc\":\"2.0\",\"id\":\"1570700675933\",\"method\":\"Получить состояние сервера\"}");

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(endpoint, times(3)).sendString(argument.capture());
        assertTrue(argument.getAllValues().get(0).startsWith("Got your"));
        assertTrue(argument.getAllValues().get(1).startsWith("Error of handle task in websocket"));
        assertTrue(argument.getAllValues().get(2).startsWith("{\"result\":[{"));

        //session.getRemote().sendString(answer);
        doThrow(IOException.class).when(endpoint).sendString(anyString());
        handler.onMessage("{\"jsonrpc\":\"2.0\",\"id\":\"1570700675933\",\"method\":\"Получить состояние сервера\"}");

        handler.onClose(0, "end test.");
    }
}