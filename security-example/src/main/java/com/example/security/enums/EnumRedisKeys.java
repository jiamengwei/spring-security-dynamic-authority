package com.example.security.enums;

public enum EnumRedisKeys {

	RESOURCE("resource_key"),
	SESSION("session_key");

	private String key;

	EnumRedisKeys(String key){
		this.key = key;
	}

	public String getValue(){
		return this.key;
	}
}
