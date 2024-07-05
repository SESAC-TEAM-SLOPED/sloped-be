package org.sesac.slopedbe.auth;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VerificationController.class)
@ActiveProfiles("test")
public class VerificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VerificationService verificationService;

	@Test
	@WithMockUser
	public void sendVerificationCode() throws Exception {
		//Service, Controller 상호 작용 테스트, 실제 메일을 보내는 테스트가 아닙니다.
		String email = "sas1397@naver.com";
		String code = "123456";

		when(verificationService.generateVerificationCode()).thenReturn(code);
		doNothing().when(verificationService).saveVerificationCode(email, code);
		doNothing().when(verificationService).sendVerificationEmail(email, code);

		mockMvc.perform(post("/api/auth/sendCode")
				.param("email", email)
				.with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF 토큰 포함
			.andExpect(status().isOk())
			.andExpect(content().string("Verification code sent to " + email));

		verify(verificationService, times(1)).generateVerificationCode();
		verify(verificationService, times(1)).saveVerificationCode(email, code);
		verify(verificationService, times(1)).sendVerificationEmail(email, code);
	}

	@Test
	@WithMockUser
	public void verifyCode() throws Exception {
		String email = "sas1397@naver.com";
		String code = "123456";

		when(verificationService.verifyCode(email, code)).thenReturn(true);

		mockMvc.perform(post("/api/auth/verifyCode")
				.param("email", email)
				.param("code", code)
			.with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF 토큰 포함
			.andExpect(status().isOk())
			.andExpect(content().string("Email verified successfully"));

		verify(verificationService, times(1)).verifyCode(email, code);
	}

	@Test
	@WithMockUser
	public void verifyCode_ShouldReturnErrorMessage() throws Exception {
		String email = "sas1397@naver.com";
		String code = "123456";

		when(verificationService.verifyCode(email, code)).thenReturn(false);

		mockMvc.perform(post("/api/auth/verifyCode")
				.param("email", email)
				.param("code", code)
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Invalid verification code"));

		verify(verificationService, times(1)).verifyCode(email, code);
	}
}
