package com.space.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.space.app.config.properties.ApiKeyProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link ApiKeyAuthFilter} 测试：直接构造 mock 请求 / 响应，避免拉起 Spring 容器。
 */
class ApiKeyAuthFilterTest {

    private static final String VALID_KEY = "dev-app-api-key-please-rotate";

    private ApiKeyAuthFilter filter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ApiKeyProperties properties = new ApiKeyProperties();
        properties.setApiKeys(List.of(VALID_KEY));
        objectMapper = new ObjectMapper();
        filter = new ApiKeyAuthFilter(properties, objectMapper);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void missing_header_returns_401_problem_detail_and_blocks_chain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/app/merchants");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        ObjectNode body = (ObjectNode) objectMapper.readTree(response.getContentAsString());
        assertThat(body.get("status").asInt()).isEqualTo(401);
        // 不允许透露具体原因；统一文案
        assertThat(body.get("detail").asText()).isEqualTo("Invalid or missing API key");
        verify(chain, never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void wrong_header_returns_same_401_response() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/app/merchants");
        request.addHeader("X-API-Key", "totally-wrong");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(objectMapper.readTree(response.getContentAsString()).get("detail").asText())
                .isEqualTo("Invalid or missing API key");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void valid_header_passes_through_chain_and_sets_anonymous_authentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/app/merchants");
        request.addHeader("X-API-Key", VALID_KEY);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_APP_CLIENT");
    }

    @Test
    void non_app_path_is_skipped() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/error");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
