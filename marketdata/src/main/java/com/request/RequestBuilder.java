package com.request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestBuilder {
    //todo buuld these requests dunamically
    public static String BINANCE_TOB_BTC = "wss://stream.binance.com:9443/ws/btcusdt@bookTicker";

    public static String BUILD_BINANCE_TOB_MULTI_PAIR(List<String> topPairs) {
        return "wss://stream.binance.com:9443/stream?streams=" +
                topPairs.stream()
                        .map(pair -> pair.toLowerCase() + "@bookTicker")
                        .collect(Collectors.joining("/"));
    }

}
