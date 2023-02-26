package com.hps.bookshop.service.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    void init();

    String save(MultipartFile file);

    Resource load(String filename);

    String getUrl(String filename);

    void delete(String filename);

    void deleteAll();

    Stream<Path> loadAll();
}
