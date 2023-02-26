package com.hps.bookshop.controller;

import com.hps.bookshop.service.file.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assets")
@AllArgsConstructor
@Slf4j
public class FileController {
    private final FileStorageService fileStorageService;
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> loadFile(@PathVariable("fileName") String fileName) {
        try {
            Resource file = fileStorageService.load(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
