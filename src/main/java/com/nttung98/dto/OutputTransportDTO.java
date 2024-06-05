package com.nttung98.dto;

import com.nttung98.utils.enums.ActionTransport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutputTransportDTO {

    private ActionTransport action;

    private Object object;
}