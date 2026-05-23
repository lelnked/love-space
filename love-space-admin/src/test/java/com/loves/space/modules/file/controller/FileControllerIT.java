package com.loves.space.modules.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loves.space.infrastructure.storage.StsCredentialIssuer;
import com.loves.space.infrastructure.storage.StsCredentialIssuer.StsCredential;
import com.loves.space.modules.file.dto.UploadCredentialRequest;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link FileController} 集成测试：200 / 400 / 401。
 */
@AutoConfigureMockMvc
class FileControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private StsCredentialIssuer stsCredentialIssuer;

    private String token;

    @BeforeEach
    void setUp() {
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());
        when(stsCredentialIssuer.issueFor(anyString()))
                .thenReturn(new StsCredential("STS-AK", "STS-SK", "STS-TOKEN", "2026-05-23T08:00:00Z"));
    }

    @Test
    void issueCredentialReturns200ForValidContentType() throws Exception {
        String body = objectMapper.writeValueAsString(new UploadCredentialRequest("image/png"));

        mockMvc.perform(post("/api/admin/files/upload-credentials")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessKeyId").value("STS-AK"))
                .andExpect(jsonPath("$.securityToken").value("STS-TOKEN"))
                .andExpect(jsonPath("$.objectKey").value(org.hamcrest.Matchers.matchesRegex("^images/[0-9a-f-]+\\.png$")));
    }

    @Test
    void issueCredentialReturns400ForInvalidContentType() throws Exception {
        String body = objectMapper.writeValueAsString(new UploadCredentialRequest("application/pdf"));

        mockMvc.perform(post("/api/admin/files/upload-credentials")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void issueCredentialReturns401WithoutToken() throws Exception {
        String body = objectMapper.writeValueAsString(new UploadCredentialRequest("image/png"));

        mockMvc.perform(post("/api/admin/files/upload-credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
