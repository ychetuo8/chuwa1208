package com.chuwa.redbook.service.impl;

import com.chuwa.redbook.dao.CommentRepository;
import com.chuwa.redbook.dao.PostRepository;
import com.chuwa.redbook.entity.Comment;
import com.chuwa.redbook.entity.Post;
import com.chuwa.redbook.exception.BlogAPIException;
import com.chuwa.redbook.exception.ResourceNotFoundException;
import com.chuwa.redbook.payload.CommentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();

        try {
            var f = CommentServiceImpl.class.getDeclaredField("modelMapper");
            f.setAccessible(true);
            f.set(commentService, modelMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- createComment ----------

    @Test
    void createComment_success_shouldSetPostAndSave() {
        long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        CommentDto req = new CommentDto();
        req.setName("Alice");
        req.setEmail("a@b.com");
        req.setBody("hello");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentDto res = commentService.createComment(postId, req);

        assertNotNull(res);
        assertEquals("Alice", res.getName());
        assertEquals("a@b.com", res.getEmail());
        assertEquals("hello", res.getBody());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();
        assertNotNull(saved.getPost());
        assertEquals(postId, saved.getPost().getId());
    }

    @Test
    void createComment_postNotFound_shouldThrowResourceNotFoundException() {
        long postId = 99L;
        CommentDto req = new CommentDto();
        req.setName("Alice");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(postId, req));
        verify(commentRepository, never()).save(any());
    }

    // ---------- getCommentsByPostId ----------

    @Test
    void getCommentsByPostId_emptyList_shouldReturnEmpty() {
        long postId = 1L;
        when(commentRepository.findByPostId(postId)).thenReturn(Collections.emptyList());

        List<CommentDto> res = commentService.getCommentsByPostId(postId);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    void getCommentsByPostId_multiple_shouldMapAll() {
        long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        Comment c1 = new Comment();
        c1.setName("N1");
        c1.setEmail("e1");
        c1.setBody("b1");
        c1.setPost(post);

        Comment c2 = new Comment();
        c2.setName("N2");
        c2.setEmail("e2");
        c2.setBody("b2");
        c2.setPost(post);

        when(commentRepository.findByPostId(postId)).thenReturn(Arrays.asList(c1, c2));

        List<CommentDto> res = commentService.getCommentsByPostId(postId);

        assertEquals(2, res.size());
        assertEquals("N1", res.get(0).getName());
        assertEquals("N2", res.get(1).getName());
    }

    // ---------- getCommentById ----------

    @Test
    void getCommentById_success_shouldReturnDto() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setName("Bob");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentDto res = commentService.getCommentById(postId, commentId);

        assertNotNull(res);
        assertEquals("Bob", res.getName());
    }

    @Test
    void getCommentById_postNotFound_shouldThrow() {
        long postId = 1L;
        long commentId = 10L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, commentId));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void getCommentById_commentNotFound_shouldThrow() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, commentId));
    }

    @Test
    void getCommentById_commentNotBelongToPost_shouldThrowBlogAPIException() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Post otherPost = new Post();
        otherPost.setId(999L);

        Comment comment = new Comment();
        comment.setPost(otherPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(BlogAPIException.class, () -> commentService.getCommentById(postId, commentId));
    }

    // ---------- updateComment ----------

    @Test
    void updateComment_success_shouldUpdateFieldsAndSave() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Comment existing = new Comment();
        existing.setPost(post);
        existing.setName("Old");
        existing.setEmail("old@x.com");
        existing.setBody("old body");

        CommentDto req = new CommentDto();
        req.setName("NewName");
        req.setEmail("new@x.com");
        req.setBody("new body");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentDto res = commentService.updateComment(postId, commentId, req);

        assertEquals("NewName", res.getName());
        assertEquals("new@x.com", res.getEmail());
        assertEquals("new body", res.getBody());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();
        assertEquals("NewName", saved.getName());
        assertEquals("new@x.com", saved.getEmail());
        assertEquals("new body", saved.getBody());
    }

    @Test
    void updateComment_notBelongToPost_shouldThrowBlogAPIExceptionAndNotSave() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Post otherPost = new Post();
        otherPost.setId(999L);

        Comment existing = new Comment();
        existing.setPost(otherPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        CommentDto req = new CommentDto();
        req.setName("x");

        assertThrows(BlogAPIException.class, () -> commentService.updateComment(postId, commentId, req));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_postNotFound_shouldThrow() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, 2L, new CommentDto()));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void updateComment_commentNotFound_shouldThrow() {
        long postId = 1L;
        long commentId = 2L;

        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(postId, commentId, new CommentDto()));
        verify(commentRepository, never()).save(any());
    }

    // ---------- deleteComment ----------

    @Test
    void deleteComment_success_shouldDelete() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_notBelongToPost_shouldThrowAndNotDelete() {
        long postId = 1L;
        long commentId = 10L;

        Post post = new Post();
        post.setId(postId);

        Post otherPost = new Post();
        otherPost.setId(999L);

        Comment comment = new Comment();
        comment.setPost(otherPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(BlogAPIException.class, () -> commentService.deleteComment(postId, commentId));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_postNotFound_shouldThrow() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L, 2L));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void deleteComment_commentNotFound_shouldThrow() {
        long postId = 1L;
        long commentId = 2L;

        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(postId, commentId));
        verify(commentRepository, never()).delete(any());
    }

    // ---------- static mapper util ----------

    @Test
    void commentServiceMapperUtil_shouldMapEntityToDto() {
        Comment c = new Comment();
        c.setName("Zoe");
        c.setEmail("z@z.com");
        c.setBody("hi");

        CommentDto dto = CommentServiceImpl.commentServiceMapperUtil(c);

        assertNotNull(dto);
        assertEquals("Zoe", dto.getName());
        assertEquals("z@z.com", dto.getEmail());
        assertEquals("hi", dto.getBody());
    }
}
