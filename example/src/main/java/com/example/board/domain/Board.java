package com.example.board.domain;

import com.example.attachfile.domain.AttachFile;
import com.example.util.TimeEntity;
import com.example.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String boardTitle;

    private String boardContent;

    private int boardHit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachFile> files = new ArrayList<>();

    @Builder
    public Board(String boardTitle, String boardContent, User user) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardHit = 0;
        this.user = user;
    }

    public void addAttachFile(AttachFile attachFile) {
        attachFile.attachToBoard(this);
        this.files.add(attachFile);
    }

    public void deleteFiles(Long[] delFileNo) {
        for(int i = 0; i < this.files.size(); i++) {
            for(Long id : delFileNo) {
                if(id.equals(this.files.get(i).getId())) {
                    this.files.remove(i);
                }
            }
        }
    }

    public void editBoard(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }
}
