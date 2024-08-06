package org.sesac.slopedbe.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTokenResponse {

	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("expires_in")
	public Integer expiresIn;
	@JsonProperty("token_type")
	public String tokenType;
	@JsonProperty("scope")
	public String scope;
	@JsonProperty("refresh_token")
	public String refreshToken;

}
