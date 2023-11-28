package com.example.xunyindemo_1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.xunyindemo_1.entity.Comment;
import com.example.xunyindemo_1.entity.Text;
import com.example.xunyindemo_1.mapper.CommentMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
public class CommentController {
    @Autowired
    CommentMapper commentMapper;
    @RequestMapping("/comment/listbytextid")
    public List<Comment> commentListByid(HttpServletRequest request, HttpServletResponse response){
        Integer textid = Integer.valueOf(request.getParameter("textid"));
        QueryWrapper<Comment> querywrapper=new QueryWrapper<>();
        querywrapper.eq("belongtextid",textid);
        response.setStatus(200);
        return commentMapper.selectList(querywrapper);
    }
    @RequestMapping("/comment/insert")
    public void insertcomment(@RequestParam MultipartFile photo,@RequestParam MultipartFile audio,HttpServletRequest request, HttpServletResponse response){
        //输入要有photo，audio，cookie，text，textid
        if(request.getCookies()!=null){
            Integer belongid = Integer.valueOf(request.getCookies()[0].getValue());
            String text=request.getParameter("text");
            Integer belongtextid = Integer.valueOf(request.getParameter("textid"));

            Comment tempcomment=new Comment();
            String projectPath = System.getProperty("user.dir");
            String Myfilepath=projectPath+"\\src\\main\\resources\\static\\";

            // 传入评论的图片和音频
            File dir = new File(Myfilepath+"images");
            // 检查文件夹是否存在
            if (!dir.exists()){
                dir.mkdirs();
            }
            dir = new File(Myfilepath+"audios");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!photo.isEmpty()) {
                String fileName=photo.getOriginalFilename();
                try {
                    photo.transferTo(new File(Myfilepath+"images/"+fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tempcomment.setPicture("http://localhost:8080" + "/images" + "/" + fileName);
            }
            if (!audio.isEmpty()) {
                String fileName=audio.getOriginalFilename();
                try {
                    audio.transferTo(new File(Myfilepath+"audios/"+fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tempcomment.setAudio("http://localhost:8080" + "/audios" + "/" + fileName);
            }

            Timestamp temptime=new Timestamp(System.currentTimeMillis());
            //LocalDateTime now = LocalDateTime.now();
            tempcomment.setTime(temptime);
            tempcomment.setBelongid(belongid);
            tempcomment.setBelongtextid(belongtextid);
            tempcomment.setText(text);

            response.setStatus(200);
            commentMapper.insert(tempcomment);
        }else response.setStatus(401);//cookie已经失效



    }
    @RequestMapping("/comment/delete")
    public void deleteComment(HttpServletRequest request, HttpServletResponse response){
        //输入要有cookie，commentid
        Integer commentid = Integer.valueOf(request.getParameter("commentid"));

        if(request.getCookies()!=null){
            Comment tempcomment=commentMapper.selectById(commentid);

            //当当前登录的用户id与目标删除评论的从属用户id相同时，才进行删除操作
            if(Integer.valueOf(request.getCookies()[0].getValue()).equals(tempcomment.getBelongid())||request.getCookies()[2].getValue()=="2"){
                String projectPath = System.getProperty("user.dir");
                String Myfilepath=projectPath+"\\src\\main\\resources\\static\\";

                //String temppic="images/"+tempcomment.getPicture().substring(10);

                String temppic=tempcomment.getPicture();
                if (temppic!=null){
                    temppic=Myfilepath + "images/"+tempcomment.getPicture().substring(29);
                    FileSystemUtils.deleteRecursively(new File(temppic));
                }

                String tempaud=tempcomment.getAudio();
                if (tempaud!=null){
                    tempaud=Myfilepath + "audios/"+tempcomment.getAudio().substring(29);
                    FileSystemUtils.deleteRecursively(new File(tempaud));
                }
                response.setStatus(200);
                commentMapper.deleteById(commentid);

            }else response.setStatus(403);//服务端理解操作，但不执行
        }else response.setStatus(401);

    }
}
