package com.learnwords.deckservice.service.grpcClient;

import com.learnwords.vocabulary.v1.BatchGetOnlyWordResponse;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesResponse;
import com.learnwords.vocabulary.v1.BatchGetWordsResponse;
import com.learnwords.vocabulary.v1.GetWordResponse;

import java.util.List;

public interface VocabularyGrpcClient {
    @Deprecated
    BatchGetVocabulariesResponse batchGetVocabularies(List<String> ids);

    BatchGetOnlyWordResponse batchGetOnlyWord(List<String> ids);

    //Nowe bazujace na WordDto
    public GetWordResponse getWordById(String id);
    public BatchGetWordsResponse batchGetWordsByIds(List<String> ids);

}
