package com.netty;

import com.lion.clock.SystemClock;
import com.market.data.Translator;
import com.netty.handler.WebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class NettyWebSocketConfiguration2Test {

    @Mock private EventLoopGroup eventLoopGroup;
    @Mock private Bootstrap bootstrap;
    @Mock private ChannelFuture channelFuture;
    @Mock private Channel channel;
    @Mock private HttpHeaders headers;

    private NettyWebSocketClient client;
    private final String name = "TestClient";
    private final String url = "wss://stream.binance.com:9443/ws/test";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simulate Netty Bootstrap behavior
        when(bootstrap.group(any())).thenReturn(bootstrap);
        when(bootstrap.channel(NioSocketChannel.class)).thenReturn(bootstrap);
        when(bootstrap.handler(any())).thenReturn(bootstrap);
        when(bootstrap.connect(anyString(), anyInt())).thenReturn(channelFuture);
        when(channelFuture.addListener(any(ChannelFutureListener.class))).thenReturn(channelFuture);
        when(channelFuture.channel()).thenReturn(channel);
        when(channel.closeFuture()).thenReturn(channelFuture);
        when(channel.isActive()).thenReturn(true);

        // Inject mocked Bootstrap into the client
        client = new NettyWebSocketClient(name, url, 1, eventLoopGroup, bootstrap, headers, new WebSocketHandler(new SystemClock(), new Translator() {
            @Override
            public MutableDirectBuffer translate(DirectBuffer sourceBuffer, int offset, int length, long receivedNanoTime) {
                return null;
            }

            @Override
            public int getEncodedLength() {
                return 0;
            }

        }, 4096, (msgType, directBuffer, offset, length) -> {

        }));
    }

    @Test
    void testConnect_Success() throws Exception {
        // Simulate Bootstrap connection returning a ChannelFuture
        when(bootstrap.connect(anyString(), anyInt())).thenReturn(channelFuture);
        when(channelFuture.channel()).thenReturn(channel);
        when(channel.closeFuture()).thenReturn(channelFuture);

        // Simulate successful connection listener execution
        doAnswer(invocation -> {
            ChannelFutureListener listener = invocation.getArgument(0);
            listener.operationComplete(channelFuture); // Simulates successful connection
            return channelFuture;
        }).when(channelFuture).addListener(any(ChannelFutureListener.class));

        // Execute connection
        client.connect();

        // Verifications
        verify(bootstrap, times(1)).connect(anyString(), anyInt()); // ✅ Ensures Bootstrap is used
        verify(channelFuture, atLeastOnce()).addListener(any(ChannelFutureListener.class)); // ✅ Ensures listener is attached
        verify(channel, atLeastOnce()).closeFuture(); // ✅ Ensures WebSocket close future is handled
    }

    @Test
    void testConnect_Failure_Retries() throws Exception {
        // Simulate connection failure by invoking failure listener
        doAnswer(invocation -> {
            ChannelFutureListener listener = invocation.getArgument(0);
            listener.operationComplete(mock(ChannelFuture.class)); // Simulate failure
            return channelFuture;
        }).when(channelFuture).addListener(any(ChannelFutureListener.class));

        client.connect();

        // Allow multiple invocations (since Netty retries after failure)
        verify(eventLoopGroup, atLeastOnce()).schedule(any(Runnable.class), eq(5L), eq(TimeUnit.SECONDS));
    }
    @Test
    void testSendMessage_FailsWhenChannelInactive() {
        // Simulate an inactive channel
        when(channel.isActive()).thenReturn(false);

        client.sendMessage("Test Message");

        // Verify that no message was sent since the channel was inactive
        verify(channel, never()).writeAndFlush(any(TextWebSocketFrame.class));
    }

    @Test
    void testShutdown() {
        client.shutdown();
        verify(eventLoopGroup, times(1)).shutdownGracefully();
    }

    @Test
    void testGetHeaders() {
        assert client.getHeaders() == headers;
    }
}