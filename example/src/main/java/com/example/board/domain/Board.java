package com.example.board.domain;

import com.example.common.domain.AttachFile;
import com.example.common.domain.TimeEntity;
import com.example.member.domain.Member;
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
    @Column(name = "board_no")
    private Long id;

    private String boardTitle;

    private String boardContent;

    private int boardHit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachFile> files = new ArrayList<>();

    @Builder
    public Board(String boardTitle, String boardContent, Member member) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardHit = 0;
        this.member = member;
    }

    public void increaseHit() {
        ++this.boardHit;
    }

    public void addAttachFile(AttachFile attachFile) {
        attachFile.attachBoard(this);
        this.files.add(attachFile);
    }

    public List<String> deleteFiles(Long[] delFileNo) {
        List<String> uploadPaths = new ArrayList<>();

        for(int i = 0; i < this.files.size(); i++) {
            for(Long id : delFileNo) {
                if(this.files.get(i).getId().equals(id)) {
                    uploadPaths.add(this.files.get(i).getUploadFilePath());
                    this.files.remove(i);
                }
            }
        }

        return uploadPaths;
    }

    public void editBoard(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }
}
