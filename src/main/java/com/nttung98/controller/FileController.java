package com.nttung98.controller;

import com.nttung98.dto.MessageDTO;
import com.nttung98.dto.OutputTransportDTO;
import com.nttung98.entity.Message;
import com.nttung98.service.GroupService;
import com.nttung98.service.MessageService;
import com.nttung98.service.StorageService;
import com.nttung98.service.UserSeenMessageService;
import com.nttung98.utils.enums.TypeMessage;
import com.nttung98.utils.enums.ActionTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FileController {

    private static Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserSeenMessageService seenMessageService;

    /**
     * Receive file to put in DB and send it back to the group conversation
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam(name = "file") MultipartFile file, @RequestParam(name = "userId") int userId, @RequestParam(name = "groupUrl") String groupUrl) {
        int groupId = groupService.findGroupByUrl(groupUrl);
        try {
            Message message = messageService.createAndSaveMessage(userId, groupId, TypeMessage.FILE.toString(), "have send a file");
            storageService.store(file, message.getId());
            OutputTransportDTO res = new OutputTransportDTO();
            MessageDTO messageDTO = messageService.createNotificationMessageDTO(message, userId);
            res.setAction(ActionTransport.NOTIFICATION_MESSAGE);
            res.setObject(messageDTO);
            seenMessageService.saveMessageNotSeen(message, groupId);
            List<Integer> toSend = messageService.createNotificationList(userId, groupUrl);
            toSend.forEach(toUserId -> messagingTemplate.convertAndSend("/topic/user/" + toUserId, res));
        } catch (Exception e) {
            log.error("Cannot save file, caused by {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }
}
