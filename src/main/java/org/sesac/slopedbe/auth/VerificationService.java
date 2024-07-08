package org.sesac.slopedbe.auth;

import org.springframework.stereotype.Service;

@Service

public interface VerificationService {

	public boolean verifyCode(String email, String code);
	public void sendVerificationCode(String email);
}
