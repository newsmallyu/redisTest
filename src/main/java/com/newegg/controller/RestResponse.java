package com.newegg.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class RestResponse {

	private int code;
	private String message;
	private Object data;

	public RestResponse() {
		this.code = 200;
	}

	public RestResponse(Object data) {
		this.code = 200;
		this.data = data;
	}
	public RestResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	

	public RestResponse(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
