package com.learnwords.deckservice.service.GrpcClient.impl;

import com.learnwords.deckservice.service.GrpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.BatchGetOnlyWordResponse;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesRequest;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesResponse;
import com.learnwords.vocabulary.v1.VocabularyReadServiceGrpc;
import io.grpc.Deadline;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class VocabularyGrpcClientImpl implements VocabularyGrpcClient {

    @GrpcClient("vocabulary-read")
    private VocabularyReadServiceGrpc.VocabularyReadServiceBlockingStub blockingStub;

    @Override
    public BatchGetVocabulariesResponse batchGetVocabularies(List<String> ids) {
        var req = BatchGetVocabulariesRequest.newBuilder()
                .addAllIds(ids)
                .build();
        var withDeadline = blockingStub.withDeadline(Deadline.after(800, TimeUnit.MILLISECONDS));
        return withDeadline.batchGetVocabularies(req);
    }

    @Override
    public BatchGetOnlyWordResponse batchGetOnlyWord(List<String> ids){
        var req = BatchGetVocabulariesRequest.newBuilder()
                .addAllIds(ids)
                .build();
        var withDeadline = blockingStub.withDeadline(Deadline.after(800, TimeUnit.MILLISECONDS));
        return withDeadline.batchGetOnlyWord(req);
    }
}

