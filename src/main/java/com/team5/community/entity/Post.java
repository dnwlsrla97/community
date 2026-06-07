package com.team5.community.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    

    @Column(columnDefinition = "TEXT")
    private String content;

    private String writer;
    private String status;
    private String category;
    private Integer maxParticipants;
    private Integer currentParticipants = 1; // 기존에 있던 걸 이렇게 바꾸세요
    private String participantIds = "";
    private String imagePath;
    
    // 작성 시간 및 조회수 필드 추가
    private LocalDateTime createdAt = LocalDateTime.now();
    private Integer views = 0;
    private Integer likes = 0;
    
}