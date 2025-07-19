package com.example.polling_api_demo.services.user;

import com.example.polling_api_demo.dtos.OptionsDTO;
import com.example.polling_api_demo.dtos.PollDTO;
import com.example.polling_api_demo.entities.Options;
import com.example.polling_api_demo.entities.Poll;
import com.example.polling_api_demo.entities.User;
import com.example.polling_api_demo.repositories.OptionsRepository;
import com.example.polling_api_demo.repositories.PollRepository;
import com.example.polling_api_demo.repositories.VoteRepository;
import com.example.polling_api_demo.utils.AuthHelper;
import com.example.polling_api_demo.utils.MailUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollServiceImpl implements PollService {

    private final AuthHelper authHelper;
    private final PollRepository pollRepository;
    private final OptionsRepository optionsRepository;
    private final VoteRepository voteRepository;
    private final MailUtil mailUtil;

    @Value("${app.max.polls}")
    private int maxPolls;

    @Override
    public PollDTO postPoll(PollDTO pollDTO) {
        User user = authHelper.getLoggedInUser();
        if (user != null) {
            Poll poll = new Poll();
            poll.setQuestion(pollDTO.getQuestion());
            poll.setPostedDate(new Date());
            poll.setExpiredAt(pollDTO.getExpiredAt());
            poll.setUser(user);
            poll.setTotalVoteCount(0);
            Poll createdPoll = pollRepository.save(poll);

            List<Options> options = new ArrayList<>();
            for (String optionTitle : pollDTO.getOptions()) {
                Options option = new Options();
                option.setTitle(optionTitle);
                option.setPoll(createdPoll);
                option.setVoteCount(0);
                options.add(option);
            }

            List<Options> savedOptions = optionsRepository.saveAll(options);
            poll.setOptions(savedOptions);
            pollRepository.save(poll);

            if (createdPoll.getId() != null) {
                mailUtil.createPollMessage(user, createdPoll);
            }
            return getPollDTOInService(createdPoll);
        }
        return null;
    }

    @Override
    public void deletePoll(Long id) {
        pollRepository.deleteById(id);
    }

    @Override
    public List<PollDTO> getAllPolls() {
        return pollRepository.findAll().stream()
                .sorted(Comparator.comparing(Poll::getPostedDate).reversed())
                .map(this::getPollDTOInService)
                .collect(Collectors.toList());
    }

    @Override
    public List<PollDTO> getMyPolls() {
        User user = authHelper.getLoggedInUser();
        if (user != null) {
            return pollRepository.findAllByUserId(user.getId()).stream()
                    .sorted(Comparator.comparing(Poll::getPostedDate).reversed())
                    .map(this::getPollDTOInService)
                    .collect(Collectors.toList());
        }
        throw new EntityNotFoundException("User not found");
    }

    @Override
    public Boolean hasMaxPolls() {
        return getMyPolls().size() >= maxPolls;
    }

    public PollDTO getPollDTOInService(Poll poll) {
        User loggedInUser = authHelper.getLoggedInUser();

        PollDTO pollDTO = new PollDTO();
        pollDTO.setId(poll.getId());
        pollDTO.setQuestion(poll.getQuestion());
        pollDTO.setExpiredAt(poll.getExpiredAt());
        pollDTO.setExpired(poll.getExpiredAt() != null && poll.getExpiredAt().before(new Date()));
        pollDTO.setPostedDate(poll.getPostedDate());

        Long userId = (loggedInUser != null) ? loggedInUser.getId() : null;
        pollDTO.setOptionsDTOS(poll.getOptions().stream()
                .map(opt -> this.getOptionDTO(opt, userId, poll.getId()))
                .collect(Collectors.toList()));

        pollDTO.setTotalVoteCount(poll.getTotalVoteCount());

        User pollOwner = poll.getUser();
        boolean ownedByCurrent = loggedInUser != null && pollOwner.getId().equals(loggedInUser.getId());
        pollDTO.setOwnedByCurrentUser(ownedByCurrent);
        pollDTO.setUsername(ownedByCurrent
                ? "You"
                : pollOwner.getFirstName() + " " + pollOwner.getLastName());
        pollDTO.setUserId(pollOwner.getId());

        if (loggedInUser != null) {
            pollDTO.setVoted(voteRepository.existsByPollIdAndUserId(poll.getId(), userId));
        } else {
            pollDTO.setVoted(false);
        }
        return pollDTO;
    }

    public OptionsDTO getOptionDTO(Options options, Long userId, Long pollId) {
        OptionsDTO optionsDTO = new OptionsDTO();
        optionsDTO.setId(options.getId());
        optionsDTO.setTitle(options.getTitle());
        optionsDTO.setPollId(options.getPoll().getId());
        optionsDTO.setVoteCount(options.getVoteCount());

        if (userId != null) {
            optionsDTO.setUserVotedThisOption(
                    voteRepository.existsByPollIdAndUserIdAndOptionsId(pollId, userId, options.getId())
            );
        } else {
            optionsDTO.setUserVotedThisOption(false);
        }
        return optionsDTO;
    }
}
