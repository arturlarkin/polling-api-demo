package com.example.polling_api_demo.services.user;

import com.example.polling_api_demo.dtos.PollDTO;

import java.util.List;

public interface PollService {
    PollDTO postPoll(PollDTO pollDTO);
    void deletePoll(Long id);
    List<PollDTO> getAllPolls();
    List<PollDTO> getMyPolls();
    Boolean hasMaxPolls();
}
