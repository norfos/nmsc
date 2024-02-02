package go.kr.nmsc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public abstract class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	
		registry.addMapping("/**").allowedOrigins("*"); //모든 경로 모든 곳에서 접속 가능하게 설정
	}

}