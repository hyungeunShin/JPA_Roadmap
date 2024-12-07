package com.example.board.service;

import com.example.attachfile.domain.AttachFile;
import com.example.board.domain.Board;
import com.example.board.dto.BoardDetailDTO;
import com.example.board.dto.BoardListDTO;
import com.example.board.dto.EditBoardDTO;
import com.example.board.dto.RegisterBoardDTO;
import com.example.board.repository.BoardJpaRepository;
import com.example.user.domain.User;
import com.example.user.repository.UserJpaRepository;
import com.example.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
//    private final BoardRepository repository;
//    private final UserRepository userRepository;

    private final BoardJpaRepository boardRepository;
    private final UserJpaRepository userRepository;
    private final FileStore fileStore;

    @Override
    public Page<BoardListDTO> getBoardList(Pageable pageable, String searchType, String searchWord) {
        return boardRepository.findAllWithPaging(pageable, searchType, searchWord);
    }

    @Override
    @Transactional
    public Long saveBoard(RegisterBoardDTO dto, Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);

        Board board = Board.builder()
                           .boardTitle(dto.getBoardTitle())
                           .boardContent(dto.getBoardContent())
                           .user(user)
                           .build();

        saveAttachFile(dto.getBoardFile(), board);

        boardRepository.save(board);

        return board.getId();
    }

    @Override
    @Transactional
    public BoardDetailDTO findBoardDetail(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(NullPointerException::new);
        boardRepository.increaseHit(board.getId());
        return new BoardDetailDTO(board);
    }

    @Override
    @Transactional
    public void editBoard(EditBoardDTO dto) throws IOException {
        Board board = boardRepository.findById(dto.getId()).orElseThrow(NullPointerException::new);

        Long[] delFileNo = dto.getDelFileNo();
        if(delFileNo != null && delFileNo.length > 0) {
            board.deleteFiles(delFileNo);
        }
        board.editBoard(dto.getBoardTitle(), dto.getBoardContent());
        saveAttachFile(dto.getBoardFile(), board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(NullPointerException::new);
        boardRepository.delete(board);
    }

    private void saveAttachFile(MultipartFile[] files, Board board) throws IOException {
        for(MultipartFile multipartFile : files) {
            if(multipartFile != null && !multipartFile.isEmpty()) {
                String originalFilename = multipartFile.getOriginalFilename();

                String uploadFileName = fileStore.uploadFile(multipartFile);
                String ext = fileStore.extractExt(originalFilename);
                String uploadFilePath = fileStore.getFullPath(uploadFileName);


                AttachFile attachFile = AttachFile.builder()
                                                  .originalFileName(originalFilename)
                                                  .uploadFileName(uploadFileName)
                                                  .uploadFilePath(uploadFilePath)
                                                  .fileSize(multipartFile.getSize())
                                                  .fileFancySize(FileUtils.byteCountToDisplaySize(multipartFile.getSize()))
                                                  .fileExt(ext)
                                                  .build();

                board.addAttachFile(attachFile);
            }
        }
    }
}
