package com.example.xunyindemo_1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("text")
public class Text {
    @TableId(type= IdType.AUTO)
    private Integer textid;
    private Integer belongid;
    private String picture;
    private String audio;
    private Integer likes;
    private String text;
    private Timestamp time;
    @Id
@Column(name="textid")
    public Integer getTextid() {
        return textid;
    }

    public void setTextid(Integer textid) {
        this.textid = textid;
    }
   /* @Column(name="belongid")
    public int getBelong_id() {
        return belongid;
    }

    public void setBelong_id(int belongid) {
        this.belongid = belongid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }*/
}
