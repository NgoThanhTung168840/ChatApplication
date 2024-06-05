package com.nttung98.service;

import com.nttung98.dto.MessageDTO;
import com.nttung98.dto.NotificationDTO;
import com.nttung98.entity.File;
import com.nttung98.entity.Group;
import com.nttung98.entity.Message;
import com.nttung98.repository.MessageRepository;
import com.nttung98.utils.enums.TypeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private FileService fileService;

    private static final String[] colorsArray =
            {
                    "#FFC194", "#9CE03F", "#62C555", "#3AD079",
                    "#44CEC3", "#F772EE", "#FFAFD2", "#FFB4AF",
                    "#FF9207", "#E3D530", "#D2FFAF", "FF5733"
            };

    private static final Map<Integer, String> colors = new HashMap<>();

    public String getRandomColor() {
        return colorsArray[new Random().nextInt(colorsArray.length)];
    }

    public Message createAndSaveMessage(int userId, int groupId, String type, String data) {
        Message msg = new Message(userId, groupId, type, data);
        return messageRepository.save(msg);
    }

    public void flush() {
        messageRepository.flush();
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findByGroupId(int id, int offset) {
        List<Message> list = messageRepository.findByGroupIdAndOffset(id, offset);
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        return list;
    }

    public void deleteAllMessagesByGroupId(int groupId) {
        messageRepository.deleteMessagesDataByGroupId(groupId);
    }

    public Message findLastMessage(int groupId) {
        return messageRepository.findLastMessageByGroupId(groupId);
    }

    public int findLastMessageIdByGroupId(int groupId) {
        return messageRepository.findLastMessageIdByGroupId(groupId);
    }


    public MessageDTO createMessageDTO(int id, String type, int userId, String date, int group_id, String message) {
        colors.putIfAbsent(userId, getRandomColor());
        String username = userService.findUsernameById(userId);
        String fileUrl = "";
        String[] arr = username.split(",");
        String initials = arr[0].substring(0, 1).toUpperCase() + arr[1].substring(0, 1).toUpperCase();
        String sender = StringUtils.capitalize(arr[0]) +
                " " +
                StringUtils.capitalize(arr[1]);
        if (type.equals(TypeMessage.FILE.toString())) {
            File file = fileService.findByFkMessageId(id);
            fileUrl = file.getUrl();
        }
        return new MessageDTO(id, type, message, userId, group_id, null, sender, date, initials, colors.get(userId), fileUrl, userId == id);
    }

    public static String createUserInitials(String firstAndLastName) {
        String[] names = firstAndLastName.split(",");
        return names[0].substring(0, 1).toUpperCase() + names[1].substring(0, 1).toUpperCase();
    }

    @Transactional
    public List<Integer> createNotificationList(int userId, String groupUrl) {
        int groupId = groupService.findGroupByUrl(groupUrl);
        List<Integer> toSend = new ArrayList<>();
        Optional<Group> optionalGroupEntity = groupService.findById(groupId);
        if (optionalGroupEntity.isPresent()) {
            Group group = optionalGroupEntity.get();
            group.getUserEntities().forEach(userEntity -> toSend.add(userEntity.getId()));
        }
        return toSend;
    }

    public NotificationDTO createNotificationDTO(Message msg) {
        String groupUrl = groupService.getGroupUrlById(msg.getGroup_id());
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setGroupId(msg.getGroup_id());
        notificationDTO.setGroupUrl(groupUrl);
        if (msg.getType().equals(TypeMessage.TEXT.toString())) {
            notificationDTO.setType(TypeMessage.TEXT);
            notificationDTO.setMessage(msg.getMessage());
        }
        if (msg.getType().equals(TypeMessage.FILE.toString())) {
            File file = fileService.findByFkMessageId(msg.getId());
            notificationDTO.setType(TypeMessage.FILE);
            notificationDTO.setMessage(msg.getMessage());
            notificationDTO.setFileUrl(file.getUrl());
            notificationDTO.setFileName(file.getFilename());
        }
        notificationDTO.setFromUserId(msg.getUser_id());
        notificationDTO.setLastMessageDate(msg.getCreatedAt().toString());
        notificationDTO.setSenderName(userService.findFirstNameById(msg.getUser_id()));
        notificationDTO.setMessageSeen(false);
        return notificationDTO;
    }

    public MessageDTO createNotificationMessageDTO(Message msg, int userId) {
        String groupUrl = groupService.getGroupUrlById(msg.getGroup_id());
        String firstName = userService.findFirstNameById(msg.getUser_id());
        String initials = userService.findUsernameById(msg.getUser_id());
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(msg.getId());
        if (msg.getType().equals(TypeMessage.FILE.toString())) {
            String url = fileService.findFileUrlByMessageId(msg.getId());
            messageDTO.setFileUrl(url);
        }
        messageDTO.setType(msg.getType());
        messageDTO.setMessage(msg.getMessage());
        messageDTO.setUserId(msg.getUser_id());
        messageDTO.setGroupUrl(groupUrl);
        messageDTO.setGroupId(msg.getGroup_id());
        messageDTO.setSender(firstName);
        messageDTO.setTime(msg.getCreatedAt().toString());
        messageDTO.setInitials(createUserInitials(initials));
        messageDTO.setColor(colors.get(msg.getUser_id()));
        messageDTO.setMessageSeen(msg.getUser_id() == userId);
        return messageDTO;
    }
}
