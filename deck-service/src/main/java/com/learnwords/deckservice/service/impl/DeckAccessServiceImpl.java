package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.entity.Deck;
import org.springframework.stereotype.Service;

@Service
public class DeckAccessServiceImpl {

//    private final UserClient userClient; // gRPC/REST do user-service
//
//    public boolean canUserEnrollToDeck(Deck deck, String userId) {
//        if (deck.getOwnerId().equals(userId)) {
//            return true;
//        }
//
//        return switch (deck.getVisibility()) {
//            case PRIVATE -> false;
//
//            case FRIENDS_ONLY ->
//                    userClient.isFriends(userId, deck.getOwnerUserId());
//
//            case STUDENTS_ONLY ->
//                    userClient.hasTeacherStudentRelation(
//                            studentId = userId,
//                            teacherId = deck.getOwnerUserId()
//                    );
//
//            case  PUBLIC->
//                    deck.getCommunityId() != null
//                            && userClient.isMemberOfCommunity(userId, deck.getCommunityId());
//        };
//    }
}

