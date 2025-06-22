package lets.study.Board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lets.study.Board.entity.Board;
import lets.study.Board.service.BoardService;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //�Խ��� ��ȸ
    @GetMapping
    public List<Board> getAllBoards() {
    	List<Board> list = boardService.findAll();
    	
    	for(Board board: list ) {
    		System.out.println(board);
    	}
        return boardService.findAll();
    }

    //�Խ��� �ܰ� ��ȸ
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable("id") Long id) {
    	return boardService.findById(id)
    	        .map(board -> {
    	            System.out.println("��ȸ�� �Խñ�: " + board);
    	            return ResponseEntity.ok(board);
    	        })
    	        .orElseGet(() -> {
    	            System.out.println("ID " + id + " �� �ش��ϴ� �Խñ��� �����ϴ�.");
    	            return ResponseEntity.notFound().build();
    	        });
    }

    //�Խ��� ���
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardService.save(board);
    }

    //����� ����
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable("id") Long id, @RequestBody Board requestBoard) {
        return boardService.findById(id)
                .map(existingBoard -> {
                    if (!existingBoard.getPassword().equals(requestBoard.getPassword())) {
                    	System.out.println("��й�ȣ ����ġ");
                    	// ��й�ȣ ����ġ
                        return ResponseEntity.status(403).body("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
                    }
                    boardService.delete(id);
                    return ResponseEntity.ok("���� ����");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //�Խ��� ����
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, @RequestBody Board updatedBoard) {
        return boardService.findById(id)
                .map(existingBoard -> {
                    if (!existingBoard.getPassword().equals(updatedBoard.getPassword())) {
                    	System.out.println("��й�ȣ ����ġ");
                        // ��й�ȣ ����ġ
                        return ResponseEntity.status(403).body("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
                    }
                    // ��й�ȣ ��ġ �� ������Ʈ ����
                    existingBoard.setTitle(updatedBoard.getTitle());
                    existingBoard.setContent(updatedBoard.getContent());
                    existingBoard.setWriter(updatedBoard.getWriter());
                    existingBoard.setCreateDate(updatedBoard.getCreateDate());

                    Board savedBoard = boardService.save(existingBoard);
                    System.out.println("������ �Խñ�: " + savedBoard);
                    return ResponseEntity.ok(savedBoard);
                })
                .orElseGet(() -> {
                    System.out.println("ID " + id + " �� �ش��ϴ� �Խñ��� �����ϴ�.");
                    return ResponseEntity.notFound().build();
                });
    }
}