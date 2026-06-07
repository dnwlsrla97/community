package com.team5.community;

import com.team5.community.entity.*;
import com.team5.community.service.*;
import com.team5.community.repository.*; 
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeService likeService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    @GetMapping("/")
    public String home(@RequestParam(value = "status", required = false) String status, 
                       @RequestParam(value = "filter", required = false) String filter, 
                       @RequestParam(value = "category", required = false) String category,
                       Model model, HttpSession session) { 
        
        String loginId = (String) session.getAttribute("loginId");
        String loginName = (String) session.getAttribute("loginName"); 
        
        if (loginId == null) return "login";

        List<Post> posts;
        if ("my".equals(filter)) {
            posts = postService.getMyPosts(loginId);
        } else if ("joined".equals(filter)) {
            posts = postService.getAllPosts().stream()
                    .filter(p -> p.getParticipantIds() != null && p.getParticipantIds().contains(loginId))
                    .collect(Collectors.toList());
        } else if ("active".equals(status)) {
            posts = postService.getActivePosts();
        } else {
            posts = postService.getAllPosts();
        }
        
        if (category != null && !category.isEmpty() && !"전체".equals(category)) {
            posts = posts.stream()
                    .filter(post -> category.equals(post.getCategory()))
                    .collect(Collectors.toList());
        }
        
        List<String> categoryList = postService.getAllPosts().stream()
                .map(Post::getCategory)
                .filter(c -> c != null)
                .distinct()
                .collect(Collectors.toList());
        
        model.addAttribute("posts", posts);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("loginId", loginId); 
        model.addAttribute("loginName", loginName); 
        model.addAttribute("currentCategory", category != null ? category : "전체");
        model.addAttribute("currentFilter", filter != null ? filter : "all");
        
        return "home"; 
    }

    @PostMapping("/login")
    public String loginProcessing(@RequestParam("studentId") String studentId, 
                                  @RequestParam("password") String password, 
                                  HttpSession session, Model model) {
        if ("admin".equals(studentId) && "1234".equals(password)) {
            session.setAttribute("loginId", "admin");
            session.setAttribute("loginName", "마스터관리자");
            return "redirect:/";
        }
        
        Optional<User> userOpt = userRepository.findById(studentId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("loginId", studentId);
                session.setAttribute("loginName", user.getName()); 
                return "redirect:/";
            }
        }
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }

    @GetMapping("/write")
    public String writeForm(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        model.addAttribute("loginId", loginId);
        List<String> categoryList = postService.getAllPosts().stream()
                .map(Post::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("categoryList", categoryList);
        return "form";
    }

    @PostMapping("/save")
    public String savePost(@RequestParam("title") String title, @RequestParam("content") String content,
                           @RequestParam("category") String category,
                           @RequestParam(value = "maxParticipants", required = false) Integer maxParticipants,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           HttpSession session) { 
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        
        User user = userRepository.findById(loginId).orElse(null);
        String writerName = (user != null) ? (loginId + "(" + user.getName() + ")") : loginId;

        Post post = new Post();
        post.setTitle(title); post.setContent(content); post.setCategory(category); post.setWriter(writerName);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try { post.setImagePath(s3Service.uploadFile(imageFile)); } 
            catch (IOException e) { post.setImagePath(null); }
        }
        
        if (maxParticipants != null && maxParticipants > 0) {
            post.setMaxParticipants(maxParticipants);
            post.setCurrentParticipants(1);
            post.setParticipantIds(loginId);
            post.setStatus("모집중");
        } else {
            post.setStatus("일반글");
        }
        postService.savePost(post);
        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) return "redirect:/";

        model.addAttribute("post", post);
        model.addAttribute("loginId", loginId);
        model.addAttribute("likesCount", likeService.countLikes(post));
        model.addAttribute("isLiked", userRepository.findById(loginId).map(u -> likeService.isLiked(u, post)).orElse(false));
        model.addAttribute("comments", commentService.getCommentsByPost(post));
        return "detail";
    }

    @Transactional 
    @GetMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        Post post = postRepository.findById(id).orElse(null);
        
        if (post != null) {
            String writerId = post.getWriter().contains("(") ? post.getWriter().split("\\(")[0] : post.getWriter();
            if (loginId != null && (loginId.equals(writerId) || loginId.equals("admin"))) {
                if (post.getImagePath() != null && !post.getImagePath().isEmpty() && post.getImagePath().startsWith("https://")) {
                    try { s3Service.deleteFile(post.getImagePath()); } catch (Exception e) {}
                }
                commentRepository.deleteByPost(post);
                likesRepository.deleteByPost(post);
                postRepository.delete(post);
            }
        }
        return "redirect:/"; 
    }

    @GetMapping("/post/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        Post post = postRepository.findById(id).orElse(null);
        model.addAttribute("post", post);
        return "edit";
    }

    @PostMapping("/post/{id}/edit")
    public String editPost(@PathVariable("id") Long id, 
                           @ModelAttribute Post post, 
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (existingPost.getImagePath() != null) s3Service.deleteFile(existingPost.getImagePath());
                existingPost.setImagePath(s3Service.uploadFile(imageFile));
            } catch (IOException e) { e.printStackTrace(); }
        }
        postRepository.save(existingPost);
        return "redirect:/post/" + id;
    }

    @PostMapping("/post/{id}/like")
    public String likePost(@PathVariable("id") Long id, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        userRepository.findById(loginId).ifPresent(user -> likeService.toggleLike(id, user));
        return "redirect:/post/" + id;
    }

    @PostMapping("/post/{id}/comment")
    public String writeComment(@PathVariable("id") Long id, @RequestParam String content, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        userRepository.findById(loginId).ifPresent(user -> commentService.writeComment(id, content, user));
        return "redirect:/post/" + id;
    }

    @PostMapping("/post/{id}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        userRepository.findById(loginId).ifPresent(user -> commentService.deleteComment(commentId, user));
        return "redirect:/post/" + id;
    }

    @PostMapping("/post/{id}/join")
    public String joinPost(@PathVariable("id") Long id, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "login";
        Post post = postRepository.findById(id).orElse(null);
        if (post != null && post.getMaxParticipants() != null) {
            String currentList = (post.getParticipantIds() == null) ? "" : post.getParticipantIds();
            if (!currentList.contains(loginId) && post.getCurrentParticipants() < post.getMaxParticipants()) {
                post.setCurrentParticipants(post.getCurrentParticipants() + 1);
                post.setParticipantIds(currentList.isEmpty() ? loginId : currentList + "," + loginId);
                if (post.getCurrentParticipants().equals(post.getMaxParticipants())) post.setStatus("모집완료");
                postService.savePost(post);
            }
        }
        return "redirect:/post/" + id;
    }
}