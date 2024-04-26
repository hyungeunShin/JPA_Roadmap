package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.*;
import com.example.board.repository.BoardRepository;
import com.example.common.domain.AttachFile;
import com.example.user.domain.User;
import com.example.util.FileStore;
import com.example.util.PaginationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository repository;

    private final FileStore fileStore;

    @Override
    public List<BoardListDTO> getBoardList(PaginationInfo<BoardListDTO> paginationInfo) {
        //return repository.boardList(paginationInfo).stream().map(BoardListDTO::new).toList();
        return null;
    }

    @Override
    public int getBoardTotalCount(PaginationInfo<BoardListDTO> paginationInfo) {
        //return repository.boardTotalCount(paginationInfo).intValue();
        return 0;
    }

    @Transactional
    @Override
    public Long saveBoard(RegisterBoardDTO dto, User user) throws IOException {
        Board board = Board.builder()
                           .boardTitle(dto.getBoardTitle())
                           .boardContent(dto.getBoardContent())
                           .user(user)
                           .build();

        saveAttachFile(dto.getBoardFile(), board);

        repository.save(board);

        return board.getId();
    }

    @Override
    public BoardDetailDTO findBoardDetail(Long boardNo) throws NullPointerException {
        Board board = repository.findBoardById(boardNo).orElseThrow(NullPointerException::new);
        return new BoardDetailDTO(board);
    }

    @Transactional
    @Override
    public void increaseHit(Long boardNo) {
        Board board = repository.findBoardById(boardNo).orElseThrow(NullPointerException::new);
        board.increaseHit();
    }

    @Override
    public BoardFileDownloadDTO findAttachFile(Long fileNo) throws NullPointerException {
        AttachFile attachFile = repository.findAttachFileById(fileNo).orElseThrow(NullPointerException::new);
        return new BoardFileDownloadDTO(attachFile.getUploadFileName(), attachFile.getOriginalFileName());
    }

    @Transactional
    @Override
    public List<String> editBoard(EditBoardDTO dto) throws IOException {
        Board board = repository.findBoardById(dto.getId()).orElseThrow(NullPointerException::new);

        List<String> deleteFileUploadPath = null;
        Long[] delFileNo = dto.getDelFileNo();
        if(delFileNo != null) {
            deleteFileUploadPath = board.deleteFiles(delFileNo);
        }
        board.editBoard(dto.getBoardTitle(), dto.getBoardContent());
        saveAttachFile(dto.getBoardFile(), board);

        return deleteFileUploadPath;
    }

    private void saveAttachFile(MultipartFile[] files, Board board) throws IOException {
        for(MultipartFile multipartFile : files) {
            if(!multipartFile.isEmpty()) {
                String ext = fileStore.extractExt(Objects.requireNonNull(multipartFile.getOriginalFilename(), "이미지 이름이 존재하지 않습니다."));
                String uploadFileName = fileStore.uploadFile(multipartFile);
                String uploadPath = fileStore.getFullPath(uploadFileName);

                AttachFile attachFile = new AttachFile(multipartFile, uploadFileName, uploadPath, ext);

                board.addAttachFile(attachFile);
            }
        }
    }
}
