package org.sesac.slopedbe.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoResponse {

	@JsonProperty("id")
	private String id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("verified_email")
	private boolean verifiedEmail;

	@JsonProperty("name")
	private String name;

	@JsonProperty("given_name")
	private String givenName;

	@JsonProperty("family_name")
	private String familyName;

	@JsonProperty("picture")
	private String picture;

	@JsonProperty("locale")
	private String locale;
}
