package org.sesac.slopedbe.auth.service;

import org.springframework.stereotype.Service;

@Service

public interface VerificationService {

	public boolean verifyCode(String email, String code);
	void sendRegisterVerificationCode(String email);
	void sendFindIdVerificationCode(String email);
	void sendFindPasswordVerificationCode(String id, String email);
}
