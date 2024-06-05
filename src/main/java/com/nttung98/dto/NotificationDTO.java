package com.nttung98.dto;

import com.nttung98.utils.enums.TypeMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private int fromUserId;

    private String senderName;

    private TypeMessage type;

    private String message;

    private String lastMessageDate;

    private String groupUrl;

    private int groupId;

    private String fileUrl;

    private String fileName;

    private boolean isMessageSeen;
}
