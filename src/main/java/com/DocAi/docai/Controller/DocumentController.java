package com.DocAi.docai.Controller;

import com.DocAi.docai.model.Documentt;
import com.DocAi.docai.service.DocumentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public Documentt upload(@RequestParam("file") MultipartFile file) {
        return documentService.save(file);
    }
}