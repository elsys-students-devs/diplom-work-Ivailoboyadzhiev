package com.myfitnessjourney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myfitnessjourney.dto.ChatMessageDto;
import com.myfitnessjourney.dto.ChatUserDto;
import com.myfitnessjourney.dto.SendMessageRequest;
import com.myfitnessjourney.service.ChatNotificationService;
import com.myfitnessjourney.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private ChatNotificationService chatNotificationService;

    @Test
    @WithMockUser(username = "user@test.com")
    void getChatPartners_whenAuthenticated_returnsOkAndList() throws Exception {
        when(chatService.getChatPartners("user@test.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/chat/partners").with(user("user@test.com")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chatService).getChatPartners("user@test.com");
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getChatPartners_whenHasPartners_returnsList() throws Exception {
        ChatUserDto partner = new ChatUserDto(2L, "partner", "Partner User", null, null, 0L);
        when(chatService.getChatPartners("user@test.com")).thenReturn(List.of(partner));

        mockMvc.perform(get("/api/chat/partners").with(user("user@test.com")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("partner"));
    }

    @Test
    void getChatPartners_whenNotAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/chat/partners").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getConversation_whenAuthenticated_returnsOkAndMessages() throws Exception {
        when(chatService.getConversation("user@test.com", 2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/chat/conversations/2").with(user("user@test.com")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chatService).getConversation("user@test.com", 2L);
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getUnreadCount_whenAuthenticated_returnsOkAndCount() throws Exception {
        when(chatService.getUnreadMessageCount("user@test.com")).thenReturn(3L);

        mockMvc.perform(get("/api/chat/messages/unread/count").with(user("user@test.com")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        verify(chatService).getUnreadMessageCount("user@test.com");
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void sendMessage_whenValidRequest_returnsOkAndMessageDto() throws Exception {
        SendMessageRequest request = new SendMessageRequest(2L, "Здравей!");
        ChatMessageDto messageDto = new ChatMessageDto(
                1L, 1L, "user@test.com", 2L, "partner",
                "Здравей!", LocalDateTime.now(), false
        );
        when(chatService.sendMessage(eq("user@test.com"), eq(request))).thenReturn(messageDto);

        mockMvc.perform(post("/api/chat/messages")
                        .with(user("user@test.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Здравей!"))
                .andExpect(jsonPath("$.recipientId").value(2));

        verify(chatService).sendMessage("user@test.com", request);
        verify(chatNotificationService).notifyUsers(messageDto);
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void markAsRead_whenAuthenticated_returnsNoContent() throws Exception {
        mockMvc.perform(put("/api/chat/messages/read/2")
                        .with(user("user@test.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(chatService).markMessagesAsRead("user@test.com", 2L);
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void searchUsers_whenAuthenticated_returnsOkAndList() throws Exception {
        when(chatService.searchUsers("test", "user@test.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/chat/users").with(user("user@test.com")).param("query", "test").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chatService).searchUsers("test", "user@test.com");
    }
}
