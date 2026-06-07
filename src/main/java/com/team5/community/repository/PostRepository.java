package com.team5.community.repository;

import com.team5.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 1. 우진님이 말씀하셨던 "모집중인 게시글만 필터링"하는 치트키 메서드입니다!
    List<Post> findByStatus(String status);

    // 🌟 [우진님 기획 반영!] 로그인한 사람의 학번(writer)과 일치하는 글만 DB에서 찾아오는 마법의 메서드입니다.
    List<Post> findByWriter(String writer);
}