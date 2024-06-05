package com.nttung98.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nttung98.utils.enums.TypeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {

    public Group(String name) {
        this.name = name;
    }

    public Group(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    private String url;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private TypeGroup typeGroup;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private Set<User> userEntities = new HashSet<>();

    @OneToMany(mappedBy = "groupMapping", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<GroupUser> groupUsers = new HashSet<>();

}
