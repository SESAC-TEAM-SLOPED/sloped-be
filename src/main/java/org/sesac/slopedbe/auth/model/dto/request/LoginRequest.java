package org.sesac.slopedbe.auth.model.dto.request;

public record LoginRequest(
	String memberId,
	String password) {
}
