package com.example.polling_api_demo.controllers.user;

import com.example.polling_api_demo.dtos.VoteDTO;
import com.example.polling_api_demo.services.user.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vote")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteDTO> vote(@RequestParam Long pollId, @RequestParam Long optionId) {
        VoteDTO voteDTO = voteService.vote(pollId, optionId);
        return new ResponseEntity<>(voteDTO, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelVote(@RequestParam Long pollId) {
        voteService.cancelVote(pollId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
