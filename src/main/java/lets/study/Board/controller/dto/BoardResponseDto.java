package lets.study.Board.controller.dto;

import lets.study.Board.entity.Board;

public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writer;

    // ������
    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getWriter();
    }
    
    // Getter�� �־ OK (Setter�� ����)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getWriter() { return writer; }
}