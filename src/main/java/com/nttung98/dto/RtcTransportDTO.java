package com.nttung98.dto;

import com.nttung98.utils.enums.RtcAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RtcTransportDTO {

    private int userId;

    private String groupUrl;

    private RtcAction action;

    private Object offer;

    private Object answer;
}
