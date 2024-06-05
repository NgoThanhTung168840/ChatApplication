package com.nttung98.mapper;

import com.nttung98.dto.*;
import com.nttung98.entity.Group;
import com.nttung98.entity.User;
import com.nttung98.utils.ListGroupDTO;
import com.nttung98.utils.ListWrapperGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserMapper {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupCallMapper groupCallMapper;

    public InitUserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        InitUserDTO initUserDTO = new InitUserDTO();
        List<GroupWrapperDTO> groupWrapperDTOS = new ArrayList<>();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setWsToken(user.getWsToken());
        userDTO.setJwt(user.getJwt());

        user.getGroupSet().forEach(groupEntity -> {
                    GroupWrapperDTO groupWrapperDTO = new GroupWrapperDTO();
                    groupWrapperDTO.setGroup(groupMapper.toGroupDTO(groupEntity, user.getId()));
                    groupWrapperDTO.setGroupCall(groupCallMapper.toGroupCall(groupEntity));
                    groupWrapperDTOS.add(groupWrapperDTO);
                }
        );
        groupWrapperDTOS.sort(new ListWrapperGroupDTO());

        Optional<GroupWrapperDTO> groupUrl = groupWrapperDTOS.stream().findFirst();
        String firstGroupUrl = groupUrl.isPresent() ? groupUrl.get().getGroup().getUrl() : "";

        userDTO.setFirstGroupUrl(firstGroupUrl);
        initUserDTO.setUser(userDTO);
        initUserDTO.setGroupsWrapper(groupWrapperDTOS);
        return initUserDTO;
    }


    public AuthUserDTO toLightUserDTO(User user) {
        Set<Group> groups = user.getGroupSet();
        List<GroupDTO> allUserGroups = new ArrayList<>(user.getGroupSet().stream()
                .map((groupEntity) -> groupMapper.toGroupDTO(groupEntity, user.getId())).toList());
        Optional<Group> groupUrl = groups.stream().findFirst();
        String lastGroupUrl = groupUrl.isPresent() ? groupUrl.get().getUrl() : "";
        allUserGroups.sort(new ListGroupDTO());
        return new AuthUserDTO(user.getId(), user.getFirstName(), lastGroupUrl, user.getWsToken(), allUserGroups);
    }
}
