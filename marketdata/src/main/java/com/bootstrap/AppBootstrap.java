package com.bootstrap;

import com.lion.clock.Clock;
import com.lion.clock.SystemClock;
import com.lion.config.ConfigLoader;
import com.market.data.BinanceTobToSbeTranslator;
import com.netty.NettyWebSocketClient;
import com.netty.WebSocketManager;
import com.netty.client.NettyWebSocketConfiguration;
import com.netty.handler.WebSocketHandler;
import com.request.RequestBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.util.List;

public class AppBootstrap {
    public AppBootstrap(ConfigLoader configLoader) {
        final List<String> topPairs = List.of("BTCUSDT", "ETHUSDT");
        final String websocketUrl = RequestBuilder.BUILD_BINANCE_TOB_MULTI_PAIR(topPairs);
        final NioEventLoopGroup group = new NioEventLoopGroup(1);;// Single-threaded event loop

        final Clock clock = new SystemClock();

        final NettyWebSocketClient binanceClient = NettyWebSocketConfiguration.builder()
                .name("Binance")
                .url(websocketUrl)
                .group(group)
                .bootstrap(new Bootstrap())
                .addHeader(HttpHeaderNames.HOST, "stream.binance.com")
                .addHeader(HttpHeaderNames.ORIGIN, "https://stream.binance.com")
                .addHeader(HttpHeaderNames.USER_AGENT, "netty-websocket-client")
                .handler(new WebSocketHandler(clock, new BinanceTobToSbeTranslator(4096), 4096))
                .build();

        final WebSocketManager webSocketManager = new WebSocketManager();
        webSocketManager.addClient(binanceClient);
        webSocketManager.start();;
    }
}
