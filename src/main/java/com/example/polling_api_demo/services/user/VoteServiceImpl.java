package com.example.polling_api_demo.services.user;

import com.example.polling_api_demo.dtos.VoteDTO;
import com.example.polling_api_demo.entities.Options;
import com.example.polling_api_demo.entities.Poll;
import com.example.polling_api_demo.entities.User;
import com.example.polling_api_demo.entities.Vote;
import com.example.polling_api_demo.repositories.OptionsRepository;
import com.example.polling_api_demo.repositories.PollRepository;
import com.example.polling_api_demo.repositories.VoteRepository;
import com.example.polling_api_demo.utils.AuthHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final OptionsRepository optionsRepository;
    private final AuthHelper authHelper;

    @Override
    public VoteDTO vote(Long pollId, Long optionId) {
        User user = authHelper.getLoggedInUser();
        if (user == null) {
            throw new SecurityException("User not authenticated");
        }

        if (voteRepository.existsByPollIdAndUserId(pollId, user.getId())) {
            throw new IllegalStateException("You have already voted in this poll");
        }

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("Poll not found"));

        if (poll.getExpiredAt() != null && poll.getExpiredAt().before(new Date())) {
            throw new IllegalStateException("Poll has expired");
        }

        Options option = optionsRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));

        if (!option.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("Option does not belong to the poll");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setPoll(poll);
        vote.setOptions(option);
        vote.setPostedDate(new Date());
        voteRepository.save(vote);

        option.setVoteCount(option.getVoteCount() + 1);
        optionsRepository.save(option);

        poll.setTotalVoteCount(poll.getTotalVoteCount() + 1);
        pollRepository.save(poll);

        return vote.getVoteDTO();
    }

    @Override
    public void cancelVote(Long pollId) {
        User user = authHelper.getLoggedInUser();
        if (user == null) {
            throw new SecurityException("User not authenticated");
        }

        Vote vote = voteRepository.findByPollIdAndUserId(pollId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No vote found for this poll"));

        Options option = vote.getOptions();
        Poll poll = vote.getPoll();

        option.setVoteCount(option.getVoteCount() - 1);
        optionsRepository.save(option);

        poll.setTotalVoteCount(poll.getTotalVoteCount() - 1);
        pollRepository.save(poll);

        voteRepository.delete(vote);
    }
}
