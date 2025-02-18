package com.common.entity;


import com.common.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "login_status = 'ACTIVE'")
@SQLDelete(sql =  "UPDATE member  SET login_status = 'INACTIVE' WHERE member_id = ?")
public class Member extends BaseEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private String memberId;

    private String nickname;

    private String name;

    private String identity;

    private Integer credit;

    private Integer childAge;

    private String color;

    private LocalDate lastVisit;

//    @Enumerated(EnumType.STRING)
//    private PersonalColor color;

    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookstore> bookstores;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Fairytale> fairytales;


    public void setLoginStatus(){
        this.loginStatus=LoginStatus.ACTIVE;
    }

}
