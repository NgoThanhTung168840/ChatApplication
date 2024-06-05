package com.nttung98.service;


import com.nttung98.controller.FileController;
import com.nttung98.dto.key.RoleKey;
import com.nttung98.entity.GroupUser;
import com.nttung98.repository.GroupUserJoinRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupUserJoinService {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private GroupUserJoinRepository groupUserJoinRepository;

    @Autowired
    private MessageService messageService;

    public GroupUser save(GroupUser groupUser) {
        return groupUserJoinRepository.save(groupUser);
    }

    public void saveAll(List<GroupUser> groups) {
        try {
            groupUserJoinRepository.saveAll(groups);
        } catch (Exception e) {
            log.error("Cannot save user for conversation : {}", e.getMessage());
        }
    }

    public Optional<GroupUser> findById(RoleKey id) {
        return groupUserJoinRepository.findById(id);
    }

    public List<GroupUser> findAll() {
        return groupUserJoinRepository.findAll();
    }

    public List<GroupUser> findAllByGroupId(int groupId) {
        return groupUserJoinRepository.getAllByGroupId(groupId);
    }

    public boolean checkIfUserIsAuthorizedInGroup(int userId, int groupId) {
        List<Integer> ids = groupUserJoinRepository.getUsersIdInGroup(groupId);
        return ids.stream().noneMatch(id -> id == userId);
    }


    public GroupUser grantUserAdminInConversation(int userId, int groupId) {
        return executeActionOnGroupUser(userId, groupId, 1);
    }

    public void removeUserAdminFromConversation(int userIdToDelete, int groupId) {
        executeActionOnGroupUser(userIdToDelete, groupId, 0);
    }

    private GroupUser executeActionOnGroupUser(int userId, int groupId, int role) {
        RoleKey roleKey = new RoleKey(groupId, userId);
        Optional<GroupUser> optionalGroupUserToDelete = groupUserJoinRepository.findById(roleKey);
        if (optionalGroupUserToDelete.isPresent()) {
            GroupUser groupUser = optionalGroupUserToDelete.get();
            groupUser.setRole(role);
            return groupUserJoinRepository.save(groupUser);
        }
        return null;
    }

    public void removeUserFromConversation(int userIdToDelete, int groupId) {
        RoleKey roleKey = new RoleKey(groupId, userIdToDelete);
        try {
            Optional<GroupUser> optionalGroupUserToDelete = groupUserJoinRepository.findById(roleKey);
            optionalGroupUserToDelete.ifPresent(groupUser -> groupUserJoinRepository.delete(groupUser));
            List<Integer> usersId = groupUserJoinRepository.getUsersIdInGroup(groupId);
            if (usersId.isEmpty()) {
                log.info("All users have left the group [groupId => {}]. Deleting messages...", groupId);
                messageService.deleteAllMessagesByGroupId(groupId);
                log.info("All messages have been successfully deleted");
            }
        } catch (Exception exception) {
            log.error("Error occurred during user removal : {}", exception.getMessage());
        }
    }
}
