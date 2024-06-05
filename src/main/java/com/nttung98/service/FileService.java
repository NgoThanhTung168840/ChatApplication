package com.nttung98.service;

import com.nttung98.entity.File;
import com.nttung98.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public File save(File f) {
        return fileRepository.save(f);
    }

    public File findByFkMessageId(int id) {
        return fileRepository.findByMessageId(id);
    }

    public String findFileUrlByMessageId(int id) {
        return  fileRepository.findFileUrlByMessageId(id);
    }
}
