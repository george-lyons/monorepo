package com.netty;

import com.market.data.BinanceTobToSbeTranslator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.request.RequestBuilder.BINANCE_TOB_BTC;

public class WebSocketManager {
    private static final Logger logger = LogManager.getLogger(WebSocketManager.class);
    private static final String AERON_CHANNEL = "aeron:ipc";
    private static final int STREAM_ID = 1001;
    private final NioEventLoopGroup group;

    public WebSocketManager() {
        this.group = new NioEventLoopGroup(1); // Single-threaded event loop
    }

    public void start() {
        logger.info("🚀 Starting WebSocket clients...");

        // Binance WebSocket (Order Book)
        NettyWebSocketClient binanceClient = new NettyWebSocketClient(
                "Binance",
                BINANCE_TOB_BTC,
                group,
                new Bootstrap(),
                new DefaultHttpHeaders()
                    .add(HttpHeaderNames.HOST, "stream.binance.com")
                    .add(HttpHeaderNames.ORIGIN, "https://stream.binance.com")
                    .add(HttpHeaderNames.USER_AGENT, "netty-websocket-client"),
                new BinanceTobToSbeTranslator(4096)
        );
        binanceClient.connect();

//        // Kraken WebSocket (Public Trades)
//        NettyWebSocketClient krakenClient = new NettyWebSocketClient(
//                "Kraken",
//                "wss://ws.kraken.com",
//                group,
//                new DefaultHttpHeaders()
//                    .add(HttpHeaderNames.HOST, "ws.kraken.com")
//                    .add(HttpHeaderNames.UPGRADE, "websocket")
//                    .add(HttpHeaderNames.CONNECTION, "Upgrade")
//                    .add(HttpHeaderNames.ORIGIN, "https://demo-futures.kraken.com")
//                    .add(HttpHeaderNames.USER_AGENT, "Mozilla/5.0")
//                    .add("Sec-WebSocket-Version", "13")
//                    .add("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==")
//        );
//        krakenClient.connect();
//
//        // Standard Kraken API subscription format
//        String subscribeMessage = """
//                {
//                    "event": "subscribe",
//                    "pair": ["XBT/USD"],
//                    "subscription": {
//                        "name": "ticker"
//                    }
//                }""";
//
//        group.schedule(() -> krakenClient.sendMessage(subscribeMessage), 2, TimeUnit.SECONDS);

        // Wait indefinitely
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down WebSocket clients...");
            binanceClient.shutdown();
//            krakenClient.shutdown();
        }));
    }

    public static void main(String[] args) {
        new WebSocketManager().start();
    }
}