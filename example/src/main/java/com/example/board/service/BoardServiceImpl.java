package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.*;
import com.example.board.repository.BoardRepository;
import com.example.attachfile.domain.AttachFile;
import com.example.user.domain.CustomUser;
import com.example.user.domain.User;
import com.example.user.repository.UserRepository;
import com.example.util.FileStore;
import com.example.util.PaginationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserRepository userRepository;

    private final FileStore fileStore;

    @Override
    public List<BoardListDTO> getBoardList(PaginationInfo<BoardListDTO> paginationInfo) {
        return repository.findAll(paginationInfo).stream().map(BoardListDTO::new).toList();
    }

    @Override
    public int getBoardTotalCount(PaginationInfo<BoardListDTO> paginationInfo) {
        return repository.findAllTotalCount(paginationInfo).intValue();
    }

    @Transactional
    @Override
    public Long saveBoard(RegisterBoardDTO dto) throws IOException, NullPointerException {
        CustomUser principal = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(NullPointerException::new);

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
    public BoardDetailDTO findBoardDetail(Long id) throws NullPointerException {
        Board board = repository.findById(id).orElseThrow(NullPointerException::new);

        CustomUser principal = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!board.getUser().getId().equals(principal.getId())) {
            board.increaseHit();
        }

        return new BoardDetailDTO(board);
    }

    @Transactional
    @Override
    public List<String> editBoard(EditBoardDTO dto) throws IOException, NullPointerException {
        Board board = repository.findById(dto.getId()).orElseThrow(NullPointerException::new);

        List<String> deleteFileUploadPath = null;
        Long[] delFileNo = dto.getDelFileNo();
        if(delFileNo != null) {
            deleteFileUploadPath = board.deleteFiles(delFileNo);
        }
        board.editBoard(dto.getBoardTitle(), dto.getBoardContent());
        saveAttachFile(dto.getBoardFile(), board);

        return deleteFileUploadPath;
    }

    @Transactional
    @Override
    public void deleteBoard(Long id) throws NullPointerException {
        Board board = repository.findById(id).orElseThrow(NullPointerException::new);
        repository.remove(board);
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
