package lets.study.Board.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lets.study.Board.controller.dto.BoardResponseDto;
import lets.study.Board.entity.Board;
import lets.study.Board.service.BoardService;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 전체 게시글 조회
    @GetMapping
    public List<BoardResponseDto> getAllBoards() {
        List<Board> boards = boardService.findAll();

        for (Board board : boards) {
            System.out.println(board);
        }

        return boards.stream()
                     .map(BoardResponseDto::new)
                     .collect(Collectors.toList());
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("id") Long id) {
        return boardService.findById(id)
                .map(board -> {
                    System.out.println("조회된 게시글: " + board);
                    return ResponseEntity.ok(new BoardResponseDto(board));
                })
                .orElseGet(() -> {
                    System.out.println("ID " + id + " 에 해당하는 게시글이 없습니다.");
                    return ResponseEntity.notFound().build();
                });
    }

    //게시판 등록
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardService.save(board);
    }

    //기사판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable("id") Long id, @RequestBody Board requestBoard) {
        return boardService.findById(id)
                .map(existingBoard -> {
                    if (!existingBoard.getPassword().equals(requestBoard.getPassword())) {
                    	System.out.println("비밀번호 불일치");
                    	// 비밀번호 불일치
                        return ResponseEntity.status(403).body("비밀번호가 일치하지 않습니다.");
                    }
                    boardService.delete(id);
                    return ResponseEntity.ok("삭제 성공");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //게시판 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, @RequestBody Board updatedBoard) {
        return boardService.findById(id)
                .map(existingBoard -> {
                    if (!existingBoard.getPassword().equals(updatedBoard.getPassword())) {
                    	System.out.println("비밀번호 불일치");
                        // 비밀번호 불일치
                        return ResponseEntity.status(403).body("비밀번호가 일치하지 않습니다.");
                    }
                    // 비밀번호 일치 시 업데이트 진행
                    existingBoard.setTitle(updatedBoard.getTitle());
                    existingBoard.setContent(updatedBoard.getContent());
                    existingBoard.setWriter(updatedBoard.getWriter());
                    existingBoard.setCreateDate(updatedBoard.getCreateDate());

                    Board savedBoard = boardService.save(existingBoard);
                    System.out.println("수정된 게시글: " + savedBoard);
                    return ResponseEntity.ok(savedBoard);
                })
                .orElseGet(() -> {
                    System.out.println("ID " + id + " 에 해당하는 게시글이 없습니다.");
                    return ResponseEntity.notFound().build();
                });
    }
}