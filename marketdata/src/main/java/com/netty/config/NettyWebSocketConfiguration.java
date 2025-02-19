package com.netty.config;

import com.netty.NettyWebSocketClient;
import com.netty.handler.WebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;

public class NettyWebSocketConfiguration {
    private final String name;
    private final String url;
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final HttpHeaders headers;
    private final WebSocketHandler handler;

    private NettyWebSocketConfiguration(Builder builder) {
        this.name = builder.name;
        this.url = builder.url;
        this.group = builder.group;
        this.bootstrap = builder.bootstrap;
        this.headers = builder.headers;
        this.handler = builder.handler;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public EventLoopGroup getGroup() {
        return group;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public WebSocketHandler getHandler() {
        return handler;
    }

    public static class Builder {
        private int id;
        private String name;
        private String url;
        private EventLoopGroup group;
        private Bootstrap bootstrap = new Bootstrap();
        private HttpHeaders headers = new DefaultHttpHeaders();
        private WebSocketHandler handler;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder group(EventLoopGroup group) {
            this.group = group;
            return this;
        }

        public Builder bootstrap(Bootstrap bootstrap) {
            this.bootstrap = bootstrap;
            return this;
        }

        public Builder addHeader(CharSequence name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public Builder handler(WebSocketHandler handler) {
            this.handler = handler;
            return this;
        }

        public NettyWebSocketClient build() {
            return new NettyWebSocketClient(name, url, group, bootstrap, headers, handler);
        }

    }

}