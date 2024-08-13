package org.sesac.slopedbe.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FakeIdService {

	@Value("${JWT_SECRET_KEY}")
	private String secretKey;
	private final Map<String, MemberCompositeKey> mapping;

	public FakeIdService() {
		this.mapping = new HashMap<>();
	}

	public String generateFakeId(String email, MemberOauthType oauthType) throws NoSuchAlgorithmException, InvalidKeyException {
		String combinedString = email + ":" + oauthType.getValue();

		Mac sha256Hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		sha256Hmac.init(secretKeySpec);

		byte[] hashBytes = sha256Hmac.doFinal(combinedString.getBytes(StandardCharsets.UTF_8));
		String base64Hash = Base64.getEncoder().encodeToString(hashBytes);

		String fakeId = base64Hash.replace('+', '-')
			.replace('/', '_')
			.replace("=", "");

		mapping.put(fakeId, new MemberCompositeKey(email, oauthType));

		return fakeId;
	}

	public MemberCompositeKey decodeFakeId(String fakeId) throws IllegalArgumentException {
		MemberCompositeKey memberKey = mapping.get(fakeId);
		if (memberKey == null) {
			throw new IllegalArgumentException("Invalid or unknown fake ID");
		}
		return memberKey;
	}

	public String extractEmailFromFakeId(String fakeId) {
		try {
			MemberCompositeKey memberKey = decodeFakeId(fakeId);
			return memberKey.getEmail();
		} catch (IllegalArgumentException e) {
			log.error("Error decoding fake ID", e);
			return null;
		} catch (Exception e) {
			log.error("Unexpected error while decoding fake ID", e);
			return null;
		}
	}

	public MemberOauthType extractOAuthTypeFromFakeId(String fakeId) {
		try {
			MemberCompositeKey memberKey = decodeFakeId(fakeId);
			return memberKey.getOauthType();
		} catch (IllegalArgumentException e) {
			log.error("Error decoding fake ID", e);
			return null;
		} catch (Exception e) {
			log.error("Unexpected error while decoding fake ID", e);
			return null;
		}
	}

}
