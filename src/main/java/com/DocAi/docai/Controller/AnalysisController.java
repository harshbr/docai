package com.DocAi.docai.Controller;

import com.DocAi.docai.Dtos.AnalysisRequest;
import com.DocAi.docai.Dtos.AnalysisResponse;
import com.DocAi.docai.model.Documentt;
import com.DocAi.docai.service.AiAnalysisService;
import com.DocAi.docai.service.DocumentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
@RequiredArgsConstructor
public class AnalysisController {

    private final DocumentService documentService;
    private final AiAnalysisService aiService;

    @PostMapping
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        Documentt doc = documentService.getById(request.getDocumentId());
        String result = aiService.analyze(doc.getContent(), request.getAnalysisType());
        return new AnalysisResponse(result);
    }
}
