package com.iss;

import com.iss.controller.NotificationController;
import com.iss.dto.NotificationDto;
import com.iss.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getNotifications_ShouldReturnList() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.set("Test notification");

        when(notificationService.getNotifications())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test notification"));

        verify(notificationService, times(1)).getNotifications();
    }

    @Test
    void clearNotifications_ShouldReturnNoContent() throws Exception {
        doNothing().when(notificationService).clearNotifications();

        mockMvc.perform(delete("/api/notifications"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).clearNotifications();
    }
}