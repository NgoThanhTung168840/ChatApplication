package com.nttung98.service;


import com.nttung98.dto.GroupMemberDTO;
import com.nttung98.entity.Group;
import com.nttung98.dto.key.RoleKey;
import com.nttung98.entity.GroupUser;
import com.nttung98.entity.User;
import com.nttung98.repository.GroupRepository;
import com.nttung98.utils.enums.TypeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupUserJoinService groupUserJoinService;

    public int findGroupByUrl(String url) {
        return groupRepository.findGroupByUrl(url);
    }

    public List<Integer> getAllUsersIdByGroupUrl(String groupUrl) {
        int groupId = groupRepository.findGroupByUrl(groupUrl);
        List<GroupUser> users = groupUserJoinService.findAllByGroupId(groupId);
        return users.stream().map(GroupUser::getUserId).collect(Collectors.toList());
    }

    public String getGroupName(String url) {
        return groupRepository.getGroupEntitiesBy(url);
    }

    public String getGroupUrlById(int id) {
        return groupRepository.getGroupUrlById(id);
    }

    public GroupMemberDTO addUserToConversation(int userId, int groupId) {
        Optional<Group> groupEntity = groupRepository.findById(groupId);
        if (groupEntity.isPresent() && groupEntity.orElse(null).getTypeGroup().equals(TypeGroup.SINGLE)) {
            log.info("Cannot add user in a single conversation");
            return new GroupMemberDTO();
        }
        User user = userService.findById(userId);
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupMapping(groupEntity.orElse(null));
        groupUser.setUserMapping(user);
        groupUser.setGroupId(groupId);
        groupUser.setUserId(userId);
        groupUser.setRole(0);
        GroupUser saved = groupUserJoinService.save(groupUser);
        assert groupEntity.orElse(null) != null;
        groupEntity.orElse(null).getGroupUsers().add(saved);
        groupRepository.save(groupEntity.orElse(null));
        return new GroupMemberDTO(user.getId(), user.getFirstName(), user.getLastName(), false);
    }

    public Group createGroup(int userId, String name) {
        GroupUser groupUser = new GroupUser();
        Group group = new Group(name);
        group.setName(name);
        group.setUrl(UUID.randomUUID().toString());
        group.setTypeGroup(TypeGroup.GROUP);
        Group savedGroup = groupRepository.save(group);
        User user = userService.findById(userId);
        RoleKey roleKey = new RoleKey();
        roleKey.setUserId(userId);
        roleKey.setGroupId(savedGroup.getId());
        groupUser.setGroupId(savedGroup.getId());
        groupUser.setUserId(userId);
        groupUser.setRole(1);
        groupUser.setUserMapping(user);
        groupUser.setGroupMapping(group);
        groupUserJoinService.save(groupUser);
        return savedGroup;
    }

    public Optional<Group> findById(int groupId) {
        return groupRepository.findById(groupId);
    }

    public void createConversation(int id1, int id2) {
        Group group = new Group();
        group.setName(null);
        group.setUrl(UUID.randomUUID().toString());
        group.setTypeGroup(TypeGroup.SINGLE);
        Group savedGroup = groupRepository.save(group);

        User user1 = userService.findById(id1);
        User user2 = userService.findById(id2);

        GroupUser groupUser1 = new GroupUser();
        groupUser1.setGroupId(savedGroup.getId());
        groupUser1.setUserId(id1);

        groupUser1.setRole(0);
        groupUser1.setUserMapping(user1);
        groupUser1.setGroupMapping(group);

        GroupUser groupUser2 = new GroupUser();
        groupUser2.setUserId(savedGroup.getId());
        groupUser2.setGroupId(id2);
        groupUser2.setRole(0);
        groupUser2.setUserMapping(user2);
        groupUser2.setGroupMapping(group);
        groupUserJoinService.saveAll(Arrays.asList(groupUser1, groupUser2));
    }
}
