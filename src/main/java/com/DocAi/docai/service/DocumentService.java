package com.DocAi.docai.service;


import com.DocAi.docai.Repository.DocumentRepository;
import com.DocAi.docai.model.Documentt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final PdfTextExtractor extractor;

    public Documentt save(MultipartFile file) {
        String text = extractor.extractText(file);

        Documentt doc = new Documentt();
        doc.setFileName(file.getOriginalFilename());
        doc.setContent(text);

        return repository.save(doc);
    }

    public Documentt getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}