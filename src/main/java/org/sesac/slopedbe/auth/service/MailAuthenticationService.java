package org.sesac.slopedbe.auth.service;

public interface MailAuthenticationService {

	void verifyCode(String email, String code);
	void sendRegisterVerificationCode(String email);
	void sendFindIdVerificationCode(String email);
}
