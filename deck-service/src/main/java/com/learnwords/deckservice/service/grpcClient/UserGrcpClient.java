package com.learnwords.deckservice.service.grpcClient;

import com.learnwords.auth.v1.GetUserNameByIdResponse;

public interface UserGrcpClient {
    GetUserNameByIdResponse getUserNameById(String userId);
}
