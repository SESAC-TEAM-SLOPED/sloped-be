package org.sesac.slopedbe.auth.DTO;


public class AuthResponse {

	private String jwt;

	public AuthResponse(String jwt) {
		this.jwt = jwt;
	}
}
