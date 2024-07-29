package org.sesac.slopedbe.auth.model;

import java.util.Collection;
import java.util.Map;

import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralUserDetails implements OAuth2User, UserDetails {
	// Local User, Social User 모두 포함
	// JWT 생성, loadUserByUsername에 사용



	private Member member;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	// authorities : 사용자에게 부여된 권한(예: ROLE_USER, ROLE_ADMIN 등)
	// attributes : OAuth2 인증을 통해 제공된 사용자 속성(예: email, nickname) 이번 프로젝트는 email만 제공

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		// OAuth2User 메서드, 주로 OAuth2 인증을 통해 제공된 사용자 정보 가져오는데 사용
		// 소셜 로그인 할 때, email만 받아오기 때문에 DB의 nickname 사용
		return this.member.getNickname();
	}

	@Override
	public String getPassword() {
		return this.member.getPassword(); // 소셜 사용자의 경우 null
	}

	@Override
	public String getUsername() {
		return this.member.getId().getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getUserOauthType() {
		return this.member.getId().getOauthType().toString();
	}
}