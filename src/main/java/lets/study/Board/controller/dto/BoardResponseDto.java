package lets.study.Board.controller.dto;

import lets.study.Board.entity.Board;

public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String createDate;

    // 생성자
    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getWriter();
        this.createDate = board.getCreateDate() != null 
                ? board.getCreateDate().toString()
                : "";
    }
    
    // Getter만 있어도 OK (Setter는 선택)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getWriter() { return writer; }
    public String getCreateDate() { return createDate; }
}