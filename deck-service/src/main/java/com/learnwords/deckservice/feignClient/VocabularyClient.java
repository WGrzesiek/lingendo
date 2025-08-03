package com.learnwords.deckservice.feignClient;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "${vocabulary-read-service.name}",
        url = "${vocabulary-read-service.url}",
        configuration = FeignConfig.class)
public interface VocabularyClient {

//    @PostMapping("${vocabulary-read-service.url.get-words}")
    @PostMapping("/words")
    List<OnlyWordDto> getWords(@RequestBody List<String> ids);


    @PostMapping("${vocabulary-read-service.url.get-vocabularies}")
    List<ResponseVocabularyDto> getVocabularies(@RequestBody List<String> ids);

    @GetMapping("/{id}")
    ResponseVocabularyDto getVocabulary(@PathVariable String id);
}
