package com.nttung98.mapper;

import com.nttung98.dto.GroupDTO;
import com.nttung98.dto.GroupMemberDTO;
import com.nttung98.entity.Group;
import com.nttung98.entity.GroupUser;
import com.nttung98.entity.Message;
import com.nttung98.entity.MessageUser;
import com.nttung98.service.MessageService;
import com.nttung98.service.UserSeenMessageService;
import com.nttung98.service.UserService;
import com.nttung98.utils.enums.TypeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMapper {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserSeenMessageService seenMessageService;

    @Autowired
    private UserService userService;

    public GroupDTO toGroupDTO(Group grp, int userId) {
        GroupDTO grpDTO = new GroupDTO();
        grpDTO.setId(grp.getId());
        grpDTO.setName(grp.getName());
        grpDTO.setUrl(grp.getUrl());
        grpDTO.setGroupType(grp.getTypeGroup().toString());
        Message msg = messageService.findLastMessage(grp.getId());
        if (msg != null) {
            String sender = userService.findFirstNameById(msg.getUser_id());
            MessageUser messageUser = seenMessageService.findByMessageId(msg.getId(), userId);
            grpDTO.setLastMessageSender(sender);
            if (messageUser != null) {
                if (msg.getType().equals(TypeMessage.FILE.toString())) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String senderName = userId == msg.getUser_id() ? "You" : sender;
                    stringBuilder.append(senderName);
                    stringBuilder.append(" ");
                    stringBuilder.append("have send a file");
                    grpDTO.setLastMessage(stringBuilder.toString());
                } else {
                    grpDTO.setLastMessage(msg.getMessage());
                }
                grpDTO.setLastMessage(msg.getMessage());
                grpDTO.setLastMessageSeen(messageUser.isSeen());
                grpDTO.setLastMessageDate(msg.getCreatedAt().toString());
            }
        } else {
            grpDTO.setLastMessageDate(grp.getCreatedAt().toString());
            grpDTO.setLastMessageSeen(true);
        }
        return grpDTO;
    }

    public GroupMemberDTO toGroupMemberDTO(GroupUser groupUser) {
        return new GroupMemberDTO(groupUser.getUserMapping().getId(), groupUser.getUserMapping().getFirstName(), groupUser.getUserMapping().getLastName(), groupUser.getRole() == 1);
    }
}
