package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class RegisterBoardDTO {
    @NotBlank
    private String boardTitle;

    @NotBlank
    private String boardContent;

    private MultipartFile[] boardFile;
}
