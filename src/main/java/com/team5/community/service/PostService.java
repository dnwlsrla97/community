package com.team5.community.service;

import com.team5.community.entity.Post;
import com.team5.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 1. 전체 게시글 목록을 DB에서 가져오는 기능
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 2. "모집중"인 게시글만 필터링해서 가져오는 기능
    public List<Post> getActivePosts() {
        return postRepository.findByStatus("모집중");
    }

    // 3. 진짜 웹 화면에서 사용자가 쓴 글을 AWS DB에 저장하는 배달 기능
    public void savePost(Post post) {
        postRepository.save(post);
    }

    // 🌟 [오류 해결 마스터키!] 로그인한 학번(writer)을 넘겨받아 그 사람이 쓴 글만 반환하는 로직입니다.
    public List<Post> getMyPosts(String writer) {
        return postRepository.findByWriter(writer);
    }
    public void deletePost(Long id) {
    postRepository.deleteById(id); // 레포지토리를 통해 삭제
}
// ... 기존 코드 아래에 추가 ...

    // 🌟 참여하기 기능
    // PostService.java
public boolean joinPost(Long postId, String userId) {
    Post post = postRepository.findById(postId).orElseThrow();
    
    // 1. 참여자 목록(String) 가져오기
    String currentList = (post.getParticipantIds() == null) ? "" : post.getParticipantIds();
    
    // 2. 이미 참여했는지 확인 (문자열에 아이디가 포함되어 있으면 중복)
    if (currentList.contains(userId)) {
        return false; // 중복 참여 발생! 여기서 멈춤
    }
    
    // 3. 참여자 추가 및 인원수 증가
    post.setParticipantIds(currentList + "," + userId);
    int count = (post.getCurrentParticipants() == null) ? 1 : post.getCurrentParticipants();
    post.setCurrentParticipants(count + 1);
    
    postRepository.save(post);
    return true; // 성공!
}
}