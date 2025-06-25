package lets.study.Board.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lets.study.Board.controller.dto.BoardResponseDto;
import lets.study.Board.entity.Board;
import lets.study.Board.service.BoardService;

@Controller
@RequestMapping("/boards/view")
public class BoardViewController {

    private final BoardService boardService;

    public BoardViewController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 목록
    @GetMapping
    public String boardList(Model model) {
        List<BoardResponseDto> dtos = boardService.findAll().stream()
            .map(BoardResponseDto::new)
            .collect(Collectors.toList());
        model.addAttribute("boards", dtos);
        return "board/list";
    }

    // 단건 조회
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findById(id).orElseThrow();
        model.addAttribute("board", new BoardResponseDto(board));
        return "board/detail";
    }

    // 작성 폼 (Entity 사용 – 입력용)
    @GetMapping("/form")
    public String boardForm(Model model) {
        model.addAttribute("board", new Board()); // 입력은 Entity 사용
        return "board/form";
    }

    // 등록 처리
    @PostMapping("/form")
    public String createBoard(@ModelAttribute Board board) {
        boardService.save(board);
        return "redirect:/boards/view";
    }

    // 수정 폼 (DTO로 변환)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findById(id).orElseThrow();
        model.addAttribute("board", board); // 폼에는 Entity 사용 (데이터 바인딩)
        return "board/edit";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    public String updateBoard(@PathVariable("id") Long id, @ModelAttribute Board board, @RequestParam("password") String password, Model model) {
        Board existing = boardService.findById(id).orElseThrow();
        if (!password.equals(existing.getPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("board", existing);
            return "board/edit";
        }
        board.setId(id);
        boardService.save(board);
        return "redirect:/boards/view";
    }

    // 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteBoard(@PathVariable("id") Long id, @RequestParam("password") String password, Model model) {
        Board board = boardService.findById(id).orElseThrow();
        if (!password.equals(board.getPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("boards", boardService.findAll().stream()
                    .map(BoardResponseDto::new)
                    .collect(Collectors.toList()));
            return "board/list";
        }
        boardService.delete(id);
        return "redirect:/boards/view";
    }
}