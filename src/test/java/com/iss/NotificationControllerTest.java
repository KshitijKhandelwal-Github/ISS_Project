package com.iss;

import com.iss.controller.NotificationController;
import com.iss.dto.NotificationDto;
import com.iss.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // TEST 1: GET /api/notifications
    @Test
    void shouldReturnListOfNotifications() throws Exception {

        NotificationDto dto1 = new NotificationDto(
                1L,
                "INFO",
                "Title 1",
                "Message 1",
                LocalDateTime.now()
        );

        NotificationDto dto2 = new NotificationDto(
                2L,
                "INFO",
                "Title 2",
                "Message 2",
                LocalDateTime.now()
        );

        when(notificationService.getNotifications())
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].message").value("Message 1"))
                .andExpect(jsonPath("$[1].message").value("Message 2"));

        verify(notificationService, times(1)).getNotifications();
    }

    // TEST 2: GET returns empty list
    @Test
    void shouldReturnEmptyListWhenNoNotifications() throws Exception {

        when(notificationService.getNotifications())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(notificationService, times(1)).getNotifications();
    }

    // TEST 3: DELETE /api/notifications
    @Test
    void shouldClearNotifications() throws Exception {

        doNothing().when(notificationService).clearNotifications();

        mockMvc.perform(delete("/api/notifications"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).clearNotifications();
    }
}