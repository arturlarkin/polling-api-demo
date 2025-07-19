package com.example.polling_api_demo.services.user;

import com.example.polling_api_demo.dtos.VoteDTO;

public interface VoteService {
    VoteDTO vote(Long pollId, Long optionId);
    void cancelVote(Long pollId);
}
