package com.nttung98.repository;

import com.nttung98.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File,Integer> {

    File findByMessageId(int id);

    @Query(value = "SELECT storage.url FROM file_storage storage WHERE storage.fk_message_id = :id", nativeQuery = true)
    String findFileUrlByMessageId(@Param(value = "id") int id);
}
