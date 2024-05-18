package com.example.attachfile.service;

import com.example.attachfile.domain.AttachFile;
import com.example.attachfile.repository.AttachFileRepository;
import com.example.attachfile.dto.BoardFileDownloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachFileServiceImpl implements AttachFileService {
    private final AttachFileRepository repository;

    @Override
    public BoardFileDownloadDTO findAttachFile(Long id) {
        AttachFile attachFile = repository.findById(id).orElseThrow(NullPointerException::new);
        return new BoardFileDownloadDTO(attachFile.getUploadFileName(), attachFile.getOriginalFileName());
    }
}
