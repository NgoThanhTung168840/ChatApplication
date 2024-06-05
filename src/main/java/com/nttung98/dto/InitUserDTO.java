package com.nttung98.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InitUserDTO {

    private UserDTO user;

    private List<GroupWrapperDTO> groupsWrapper;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<GroupWrapperDTO> getGroupsWrapper() {
        return groupsWrapper;
    }

    public void setGroupsWrapper(List<GroupWrapperDTO> groupsWrapper) {
        this.groupsWrapper = groupsWrapper;
    }
}
