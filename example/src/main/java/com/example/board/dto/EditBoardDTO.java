package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class EditBoardDTO {
    @NotNull
    private Long id;

    @NotBlank
    private String boardTitle;

    @NotBlank
    private String boardContent;

    private Long[] delFileNo;

    private MultipartFile[] boardFile;
}
