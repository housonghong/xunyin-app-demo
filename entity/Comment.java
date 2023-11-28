package com.example.xunyindemo_1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment {
    @TableId(type= IdType.AUTO)
    private Integer commentid;
    private Integer belongtextid;
    private Integer belongid;
    private String picture;
    private String audio;
    private String text;
    private Timestamp time;
    private Integer likes;
    @Id
    @Column(name = "commentid")

    public Integer getCommentid() {
        return commentid;
    }

    public void setCommentid(Integer commentid) {
        this.commentid = commentid;
    }
}
