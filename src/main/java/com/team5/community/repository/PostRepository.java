package com.team5.community.repository;

import com.team5.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 1. 서비스에서 호출하는 기본 메서드 (상태별 검색)
    List<Post> findByStatus(String status);

    // 2. 서비스에서 호출하는 기본 메서드 (작성자별 검색)
    List<Post> findByWriter(String writer);

    // 3. 최신순 정렬이 필요한 경우를 위한 메서드 (이름을 다르게 하거나 서비스에서 정렬)
    List<Post> findByStatusOrderByCreatedAtDesc(String status);
    List<Post> findByWriterOrderByCreatedAtDesc(String writer);

    // 4. 제목 검색용
    List<Post> findByTitleContaining(String keyword);
    
    // 5. 전체 조회 (최신순)
    List<Post> findAllByOrderByCreatedAtDesc();
}