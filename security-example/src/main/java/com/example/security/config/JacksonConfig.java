package com.example.security.config;

import com.example.security.config.security.serialize.UsernamePasswordAuthenticationTokenDeserialize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(UsernamePasswordAuthenticationToken.class, new UsernamePasswordAuthenticationTokenDeserialize());
		mapper.registerModule(module);
		return mapper;
	}
}
