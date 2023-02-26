package com.hps.bookshop.service.file;

import com.hps.bookshop.controller.FileController;
import com.hps.bookshop.exception.AlreadyExistsException;
import com.hps.bookshop.exception.NoDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root = Paths.get("./uploads");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException exc) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            if (file.getOriginalFilename() == null) {
                throw new NoDataException("Name file cannot be empty");
            }
            String fileName = new Date().getTime() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
            Files.copy(file.getInputStream(), root.resolve(fileName));
            return fileName;
        } catch (Exception exc) {
            if (exc instanceof FileAlreadyExistsException) {
                throw new AlreadyExistsException("A file of that name already exists.");
            }
            throw new RuntimeException(exc.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path path = root.resolve(filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException exc) {
            throw new RuntimeException(exc.getMessage());
        }
    }

    @Override
    public String getUrl(String filename) {
        try {
            Path path = root.resolve(filename);
            String url = MvcUriComponentsBuilder.fromMethodName(FileController.class, "loadFile",
                    path.getFileName().toString()).build().toString();
            return url;
        } catch (Exception exc) {
            throw new RuntimeException(exc.getMessage());
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path path = root.resolve(filename);
            Files.deleteIfExists(path);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(root, 1).filter(path -> !path.equals(root)).map(root::relativize);
        } catch (IOException exc) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
