package com.example.polling_api_demo.dtos;

import lombok.Data;

@Data
public class VoteDTO {
    private Long id;
    private Long optionId;
    private Long pollId;
    private Long postedBy;
}
