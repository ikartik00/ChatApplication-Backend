package in.kr.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// /chat endpoint connection ko establish karega matlab frontend se request connection ke liye /chat par aaegi
		registry.addEndpoint("/chat")
				.setAllowedOrigins("http://localhost:5173", "https://chat-application-frontend-red.vercel.app")
				.withSockJS();
		//sockJs batata ha ki agar koi browser websocket ko support nhi karta ha to ye alternative ways provide karta ha jaise polling bagera etc.
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//ye batata ha ki server /topic message broadcast kar do yani ki sab subscibers ko bhej do
		config.enableSimpleBroker("/topic", "/queue");
		//Client agar /app/... par message bhejega to wo Spring controller ke @MessageMapping wale method me jayega.
		//ye batata ha ki client /app se data bhejega to wo kis controller par jaega
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}
	
	@Bean

    public ServletServerContainerFactoryBean createWebSocketContainer() {

        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();

        container.setMaxTextMessageBufferSize(512 * 1024);   // 512 KB

        container.setMaxBinaryMessageBufferSize(512 * 1024); // 512 KB

        container.setMaxSessionIdleTimeout(60000L);

        return container;

    }
	
	@Override

	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {

        // Hum size limit ko 64KB se badhakar 50 MB kar rahe hain taaki badi images crash na karein

        registration.setMessageSizeLimit(50 * 1024 * 1024); 

        registration.setSendBufferSizeLimit(50 * 1024 * 1024);

        registration.setSendTimeLimit(60 * 1000);

    }
	
	
}
