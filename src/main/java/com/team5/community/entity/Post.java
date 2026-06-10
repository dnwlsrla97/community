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
@Table(name = "posts") // 데이터베이스 테이블 이름과 일치 확인됨
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // 제목은 필수값으로 지정
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String writer;
    private String status;
    private String category;
    private Integer maxParticipants;
    
    // 기본값 설정이 엔티티 수준에서 잘 처리되어 있습니다.
    private Integer currentParticipants = 1; 
    
    @Column(columnDefinition = "TEXT") // 참여자 ID 리스트가 길어질 수 있으므로 TEXT 설정
    private String participantIds = "";
    
    private String imagePath;
    
    @Column(nullable = false, updatable = false) // 생성 시간은 수정되지 않도록 설정
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private Integer views = 0;
    private Integer likes = 0;

    // 엔티티가 저장되기 전 시간을 자동으로 설정 (선택 사항)
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    public void update(String title, String content, String category, String imagePath) {
    this.title = title;
    this.content = content;
    this.category = category; // 카테고리 업데이트 추가
    if (imagePath != null && !imagePath.isEmpty()) {
        this.imagePath = imagePath;
    }
}
}