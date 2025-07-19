package com.example.polling_api_demo.controllers.user;

import com.example.polling_api_demo.dtos.PollDTO;
import com.example.polling_api_demo.services.user.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/polls")
@CrossOrigin("*")
public class PollController {

    private final PollService pollService;

    @PostMapping("/poll")
    public ResponseEntity<?> postPoll(@RequestBody PollDTO pollDTO) {
        if (pollService.hasMaxPolls()) {
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(Collections.singletonMap("message", "You have reached the poll creation limit"));
        }

        PollDTO createdPollDTO = pollService.postPoll(pollDTO);
        if (createdPollDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdPollDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/poll/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/public/all")
    public ResponseEntity<?> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @GetMapping("/my-polls")
    public ResponseEntity<?> getMyPolls() {
        return ResponseEntity.ok(pollService.getMyPolls());
    }
}
