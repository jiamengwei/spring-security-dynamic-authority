package com.example.security.enums;

public enum EnumRedisKeys {

	RESOURCE("resource_key"),
	USER("user_key"),
	TOKEN("token_key");

	private String key;

	EnumRedisKeys(String key){
		this.key = key;
	}

	public String getValue(){
		return this.key;
	}
}
