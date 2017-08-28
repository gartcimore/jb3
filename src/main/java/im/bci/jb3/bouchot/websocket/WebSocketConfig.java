package im.bci.jb3.bouchot.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig  implements WebSocketConfigurer {
    
    @Autowired
    private WebDirectCoinHandler webSocketHandler;
    
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(64 * 1024);
        container.setMaxBinaryMessageBufferSize(64 * 1024);
        container.setMaxSessionIdleTimeout(60 * 1000);
        container.setAsyncSendTimeout(60 * 1000);
        return container;
    }

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/webdirectcoin").addInterceptors(new HandshakeInterceptor() {
            
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                attributes.put(WebDirectCoinSessionAttributes.URI_BUILDER, UriComponentsBuilder.fromHttpRequest(request).replacePath(""));
                return true;
            }
            
            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {               
            }
        });
	}
	
	
}
