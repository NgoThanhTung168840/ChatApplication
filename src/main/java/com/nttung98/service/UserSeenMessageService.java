package com.nttung98.service;

import com.nttung98.entity.Group;
import com.nttung98.entity.Message;
import com.nttung98.entity.MessageUser;
import com.nttung98.repository.UserSeenMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserSeenMessageService {

    @Autowired
    private UserSeenMessageRepository seenMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Transactional
    public void saveMessageNotSeen(Message msg, int groupId) {
        Optional<Group> group = groupService.findById(groupId);

        group.ifPresent(groupEntity ->
                groupEntity.getUserEntities().forEach((user) -> {
                    MessageUser message = new MessageUser();
                    message.setMessageId(msg.getId());
                    message.setUserId(user.getId());
                    message.setSeen(msg.getUser_id() == user.getId());
                    seenMessageRepository.save(message);
                }));
    }

    public MessageUser findByMessageId(int messageId, int userId) {
        return seenMessageRepository.findAllByMessageIdAndUserId(messageId, userId);
    }

    public void saveMessageUserEntity(MessageUser toSave) {
        seenMessageRepository.save(toSave);
    }
}
