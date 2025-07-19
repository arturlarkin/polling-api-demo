package com.example.polling_api_demo.dtos;

import lombok.Data;

import java.util.List;

@Data
public class OptionsDTO {
    private Long id;
    private String title;
    private Long pollId;
    private Integer voteCount;
    private boolean userVotedThisOption;
    private List<VoteDTO> voteDTOS;
}
