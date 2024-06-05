package com.nttung98.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupWrapperDTO {

    private GroupDTO group;

    private GroupCallDTO groupCall;
}
