package acs.aws_final_project.controller;



import acs.aws_final_project.service.CommentService;
import acs.aws_final_project.dto.comment.CommentRequestDto;
import acs.aws_final_project.dto.comment.CommentResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{bookstoreId}")
    public ApiResponse<CommentResponseDto.CommentCreateDto> createComment(@RequestHeader("memberId") String memberId, @PathVariable("bookstoreId") Long bookstoreId, @RequestBody CommentRequestDto.CommentCreateDto createDto){

        log.info("createComment API Request time: {}", LocalDateTime.now());

        CommentResponseDto.CommentCreateDto result = commentService.createComment(createDto, memberId, bookstoreId);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{bookstoreId}/{commentId}")
    public ApiResponse<CommentResponseDto.CommentCreateDto> updateComment(@RequestHeader("memberId") String memberId, @PathVariable("bookstoreId") Long bookstoreId, @PathVariable("commentId") Long commentId , @RequestBody CommentRequestDto.CommentUpdateDto updateDto){

        log.info("updateComment API Request time: {}", LocalDateTime.now());

        CommentResponseDto.CommentCreateDto result = commentService.updateComment(memberId, bookstoreId, commentId , updateDto);

        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/{bookstoreId}/{commentId}")
    public ApiResponse<CommentResponseDto.CommentCreateDto> deleteComment(@RequestHeader("memberId") String memberId, @PathVariable("bookstoreId") Long bookstoreId, @PathVariable("commentId") Long commentId){

        log.info("deleteComment API Request time: {}", LocalDateTime.now());

        Long id = commentService.deleteComment(memberId, bookstoreId, commentId);

        CommentResponseDto.CommentCreateDto result = CommentResponseDto.CommentCreateDto.builder()
                .commentId(id)
                .build();

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/{bookstoreId}/latest")
    public ApiResponse<List<CommentResponseDto.CommentListDto>> getCommentByDate(@PathVariable("bookstoreId") Long bookstoreId) {

        log.info("getCommentByDate API Request time: {}", LocalDateTime.now());

        List<CommentResponseDto.CommentListDto> result = commentService.getCommentByDate(bookstoreId);

        return ApiResponse.onSuccess(result);

    }

    @GetMapping("/{bookstoreId}/score")
    public ApiResponse<List<CommentResponseDto.CommentListDto>> getCommentByScore(@PathVariable("bookstoreId") Long bookstoreId) {

        log.info("getCommentByScore API Request time: {}", LocalDateTime.now());

        List<CommentResponseDto.CommentListDto> result = commentService.getCommentByScore(bookstoreId);

        return ApiResponse.onSuccess(result);

    }
}
