package com.DocAi.docai.Repository;

import com.DocAi.docai.model.Documentt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<Documentt, String> {
}