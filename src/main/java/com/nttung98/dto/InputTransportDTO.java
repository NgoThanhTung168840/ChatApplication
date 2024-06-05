package com.nttung98.dto;

import com.nttung98.utils.enums.ActionTransport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InputTransportDTO {

    private int userId;

    private ActionTransport action;

    private String wsToken;

    private String groupUrl;

    private String message;

    private int messageId;
}
