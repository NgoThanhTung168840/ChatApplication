package com.nttung98.entity;

import com.nttung98.dto.key.RoleKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "group_user")
@IdClass(RoleKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUser implements Serializable {

    @Id
    private int groupId;

    @Id
    private int userId;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    Group groupMapping;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User userMapping;

    private int role;

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GroupUser groupRoleKey = (GroupUser) obj;
        return groupId == groupRoleKey.groupId &&
                userId == groupRoleKey.userId;
    }
}
