package com.DocAi.docai.Dtos;

import lombok.Data;

@Data
public class AnalysisRequest {
    private String documentId;
    private String analysisType;
}