package com.example.xunyindemo_1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.xunyindemo_1.entity.Comment;
import com.example.xunyindemo_1.entity.Text;
import com.example.xunyindemo_1.entity.User;
import com.example.xunyindemo_1.mapper.CommentMapper;
import com.example.xunyindemo_1.mapper.TextMapper;
import com.example.xunyindemo_1.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
@CrossOrigin
@RestController
public class TextController {
    @Autowired
    TextMapper textMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CommentMapper commentMapper;
    @RequestMapping("/text/list")
    public List<Object> textList(HttpServletRequest request, HttpServletResponse response){
        //不需要输入
        if(request.getCookies()!=null){
            List<Text> texts=textMapper.selectList(null);
            /*Collections.reverse(texts);*/
            List<Object> totallist = new ArrayList<>();
            for (int i = texts.size()-1; i > texts.size()-6; i--){
                List<Object> templist = new ArrayList<>();

                templist.add(texts.get(i));
                templist.add(userMapper.selectById(texts.get(i).getBelongid()));
                totallist.add(templist);

                if(i==0) break;
            }
            response.setStatus(200);
            return totallist;
        }else response.setStatus(401);
        return null;
    }
    @RequestMapping("/text/selectbytextid")
    public List<Object> selectBytextid(HttpServletRequest request, HttpServletResponse response){
        //输入要textid
        if(request.getCookies()!=null){
            try {
                String textidParam = request.getParameter("textid");
                if (textidParam != null) {
                    int textid = Integer.valueOf(textidParam);
                    // 成功转换为整数，可以在这里使用 textid
                    List<Object> totallist = new ArrayList<>();
                    Text temptext = textMapper.selectById(textid);
                    if (!temptext.equals(null)) {
                        User tempuser = userMapper.selectById(temptext.getBelongid());
                        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

                        queryWrapper.eq("belongtextid", textid);
                        List<Comment> tempcomment = commentMapper.selectList(queryWrapper);

                        totallist.add(temptext);
                        totallist.add(tempuser);
                        totallist.add(tempcomment);
                        response.setStatus(200);
                        return totallist;
                    } else {
                        // "textid" 参数不存在，可以处理缺失参数的情况
                        response.setStatus(400);
                    }
                }
            }catch(NumberFormatException e){
                // 无法成功转换为整数，处理异常情况
                response.setStatus(400);
            }
        } else response.setStatus(401);
        return null;
    }
    @RequestMapping("/text/selectbyuserid")
    public List<Text> selectByuserid(HttpServletRequest request,HttpServletResponse response){
        //输入要cookie,userid
        if(request.getCookies()!=null){
            Integer tempid= Integer.valueOf(request.getParameter("userid"));

            QueryWrapper<Text> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("belongid",tempid);
            List<Text> usertexts = textMapper.selectList(queryWrapper);

            response.setStatus(200);
            return usertexts;
        }else response.setStatus(401);
        return null;
    }

    @RequestMapping("/text/selectbykeyword")
    public List<Text> selectByKeyword(HttpServletRequest request,HttpServletResponse response){
        //输入需要cookie keyword
        if(request.getCookies()!=null){
            String keyword=request.getParameter("keyword");
            QueryWrapper<Text> querywrapper=new QueryWrapper<>();
            querywrapper.like("text",keyword);
            List<Text> temptexts=textMapper.selectList(querywrapper);
            if(!temptexts.isEmpty()){
                response.setStatus(200);
                return temptexts;
            }
        }else response.setStatus(401);
        return null;
    }
    @RequestMapping("/text/insert")
    public void setText(@RequestParam MultipartFile photo,@RequestParam MultipartFile audio, HttpServletRequest request, HttpServletResponse response) {
        //输入cookie，photo，audio，text
        if(request.getCookies()!=null){
            Integer belongid = Integer.valueOf(request.getCookies()[0].getValue());
            String text=request.getParameter("text");
            Text temptext=new Text();
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
                String fileName= photo.getOriginalFilename();
                try {
                    photo.transferTo(new File(Myfilepath+"images/"+fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                temptext.setPicture("http://10.241.10.67:8080" + "/images" + "/" + fileName);
            }
            if (!audio.isEmpty()) {
                String fileName=audio.getOriginalFilename();
                try {
                    audio.transferTo(new File(Myfilepath+"audios/"+fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                temptext.setAudio("http://localhost:8080" + "/audios" + "/" + fileName);
            }
            Timestamp temptime=new Timestamp(System.currentTimeMillis());

            temptext.setTime(temptime);
            temptext.setBelongid(belongid);
            temptext.setText(text);
            response.setStatus(200);
            textMapper.insert(temptext);
        }else response.setStatus(401);//cookie已经失效
    }
    @RequestMapping("/text/delete")
    public void deleteText(HttpServletRequest request,HttpServletResponse response){
        //输入要cookie textid
        Integer textid = Integer.valueOf(request.getParameter("textid"));

        if(request.getCookies()!=null){
            Text temptext=textMapper.selectById(textid);

            //当当前登录的用户id与目标删除评论的从属用户id相同时，才进行删除操作
            if(Integer.valueOf(request.getCookies()[0].getValue()).equals(temptext.getBelongid())||request.getCookies()[2].getValue()=="2"){
                String projectPath = System.getProperty("user.dir");
                String Myfilepath=projectPath+"\\src\\main\\resources\\static\\";

                //String temppic="images/"+tempcomment.getPicture().substring(10);

                String temppic=temptext.getPicture();
                if (temppic!=null){
                    temppic=Myfilepath + "images/"+temptext.getPicture().substring(29);
                    FileSystemUtils.deleteRecursively(new File(temppic));
                }

                String tempaud=temptext.getAudio();
                if (tempaud!=null){
                    tempaud=Myfilepath + "audios/"+temptext.getAudio().substring(29);
                    FileSystemUtils.deleteRecursively(new File(tempaud));
                }
                response.setStatus(200);
                textMapper.deleteById(textid);

            }else response.setStatus(403);//服务端理解操作，但不执行
        }else response.setStatus(401);

    }

}
