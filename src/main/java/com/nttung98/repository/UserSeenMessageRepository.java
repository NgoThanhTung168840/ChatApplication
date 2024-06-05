package com.nttung98.repository;

import com.nttung98.entity.MessageUser;
import com.nttung98.dto.key.MessageUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSeenMessageRepository extends JpaRepository<MessageUser, MessageUserKey> {

    MessageUser findAllByMessageIdAndUserId(int messageId, int userId);

}
