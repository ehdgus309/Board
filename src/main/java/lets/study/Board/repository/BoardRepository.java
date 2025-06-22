package lets.study.Board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lets.study.Board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}