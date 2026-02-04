package com.DocAi.docai.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "documents")
public class Documentt {

    @Id
    private String id;
    private String fileName;
    private String content;
}