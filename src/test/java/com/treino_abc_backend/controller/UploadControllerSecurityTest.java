package com.treino_abc_backend.controller;

import com.treino_abc_backend.security.JwtRequestFilter;
import com.treino_abc_backend.security.SecurityConfig;
import com.treino_abc_backend.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UploadController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class UploadControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadService uploadService;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void headRequestToUploadsEndpointShouldNotBeForbidden() throws Exception {
        mockMvc.perform(head("/api/uploads/does-not-matter.jpg")
                        .header("Origin", "http://localhost:54746"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:54746"));
    }
}
