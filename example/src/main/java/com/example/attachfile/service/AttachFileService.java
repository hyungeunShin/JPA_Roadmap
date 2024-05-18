package com.example.attachfile.service;

import com.example.attachfile.dto.BoardFileDownloadDTO;

public interface AttachFileService {
    BoardFileDownloadDTO findAttachFile(Long id);
}
