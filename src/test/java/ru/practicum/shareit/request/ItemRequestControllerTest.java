package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestItemService requestItemService;
    @Autowired
    private MockMvc mvc;

    private static RequestItemDto getRequestDto() {
        return RequestItemDto.builder()
                .id(1L)
                .description("some description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    void addNewRequest_shouldAddRequest_whenRequestIsValid() {
        RequestItemDto dto = getRequestDto();
        // when
        when(requestItemService.addNewRequest(any(), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(dto.getId()), Long.class),
                        jsonPath("$.description", containsString("some description")),
                        jsonPath("$.created").exists()
                );
    }

    @Test
    @SneakyThrows
    void addNewRequest_withEmptyDescription() {
        RequestItemDto dto = getRequestDto();
        dto.setDescription("");
        // when
        when(requestItemService.addNewRequest(any(), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .locale(Locale.ENGLISH)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest())
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void getRequestById_shouldReturnRequest_whenRequestIsValid() {
        // given
        RequestItemResponseDto dto = getResponseDto();
        // when
        when(requestItemService.getRequestById(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(dto.getId()), Long.class),
                        jsonPath("$.description", containsString("some desc")),
                        jsonPath("$.created", notNullValue()),
                        jsonPath("$.items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestById_shouldReturnBadRequest_whenPathVariableIsIncorrect() {
        // given
        RequestItemResponseDto dto = getResponseDto();
        // when
        when(requestItemService.getRequestById(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestById_shouldReturnBadRequest_whenUserIdHeaderIsAbsent() {
        // given
        RequestItemResponseDto dto = getResponseDto();
        // when
        when(requestItemService.getRequestById(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestById(anyLong(), anyLong());
    }

    private static RequestItemResponseDto getResponseDto() {
        return RequestItemResponseDto.builder()
                .id(1L)
                .description("some desc")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        // given
        List<RequestItemResponseDto> dtos = List.of(getResponseDto());

        // when
        when(requestItemService.getAllRequests(1L, 0, 1))
                .thenReturn(dtos);
        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(dtos.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("some desc")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestsByUserId() {
        // given
        List<RequestItemResponseDto> dtos = List.of(getResponseDto());

        // when
        when(requestItemService.getRequests(1L))
                .thenReturn(dtos);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(dtos.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("some desc")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }
}