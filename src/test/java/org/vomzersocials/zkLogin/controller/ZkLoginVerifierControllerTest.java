package org.vomzersocials.zkLogin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.vomzersocials.zkLogin.dtos.ZkProofRequest;
import org.vomzersocials.zkLogin.security.ZkLoginVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZkLoginVerifierController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters for testing
@Import(ZkLoginVerifierControllerTest.TestConfig.class)
public class ZkLoginVerifierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ZkLoginVerifier zkLoginVerifier; // Manually injected mock

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testVerifyProof_Success() throws Exception {
        // Prepare the request payload.
        ZkProofRequest request = new ZkProofRequest("valid_base64_proof", "valid_public_key");

        // When the verifier is called with these parameters, return true.
        when(zkLoginVerifier.verifyProof(eq("valid_base64_proof"), eq("valid_public_key")))
                .thenReturn(true);

        // Perform POST request to /zklogin/verify with CSRF token.
        mockMvc.perform(post("/zklogin/verify")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Proof Verified"));
    }

    @Test
    void testVerifyProof_Failure() throws Exception {
        // Prepare the request payload.
        ZkProofRequest request = new ZkProofRequest("invalid_proof", "some_public_key");

        // When the verifier is called with these parameters, return false.
        when(zkLoginVerifier.verifyProof(eq("invalid_proof"), eq("some_public_key")))
                .thenReturn(false);

        // Perform POST request to /zklogin/verify with CSRF token.
        mockMvc.perform(post("/zklogin/verify")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Proof"));
    }

    // Test configuration class to provide mocks as beans
    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public ZkLoginVerifier zkLoginVerifier() {
            return Mockito.mock(ZkLoginVerifier.class);
        }
    }
}
