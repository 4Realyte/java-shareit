package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addComment_shouldReturnBadRequest_whenTextIsBlank() {
        CommentRequestDto requestDto = getCommentRequestDto();
        requestDto.setText("");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addComment(anyLong(), any(), anyLong());
    }

    private static CommentRequestDto getCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("very good")
                .build();
    }
}