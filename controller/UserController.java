package com.example.xunyindemo_1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.xunyindemo_1.entity.Comment;
import com.example.xunyindemo_1.entity.User;
import com.example.xunyindemo_1.mapper.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.server.Cookie;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.Cookie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin
@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;
    @RequestMapping("/user/list")
    public List<User> selectAll(){
        return  userMapper.selectList(null);
    }
    @GetMapping("/user/selectbyid")
    public User getUserByid(HttpServletRequest request, HttpServletResponse response){
        //输入要有id
        Integer id= Integer.valueOf(request.getParameter("id"));
        //条件查询，根据id查询用户信息
        response.setStatus(200);
        return userMapper.selectById(id);
    }
    @GetMapping("/user/selectbykeyword")
    public List<User> getUserBykeyword(HttpServletRequest request, HttpServletResponse response){
        //输入要有keyword
        String keyword = request.getParameter("keyword");
        //根据users集合中的条件查询用户信息,例如姓名
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("name",keyword);
        response.setStatus(200);
        return userMapper.selectList(queryWrapper);
    }
    @RequestMapping("/user/login")
    public void login(HttpServletRequest request, HttpServletResponse response)
    {
        Integer id = Integer.valueOf(request.getParameter("id"));
        String password = request.getParameter("password");
        User user=userMapper.selectById(id);
        if ((user != null) && Objects.equals(user.getPassword(), password))
        {
            Cookie c1= new Cookie("id",id.toString());
            Cookie c2= new Cookie("password",password);
            Cookie c3= new Cookie("permission",user.getPermission().toString());

            c1.setMaxAge(600);
            c2.setMaxAge(600);
            c3.setMaxAge(600);
            c1.setPath("/");
            c2.setPath("/");
            c3.setPath("/");
            response.addCookie(c1);
            response.addCookie(c2);
            response.addCookie(c3);

            response.setStatus(200);
        }else if(user==null) response.setStatus(404);//用户不存在
        else response.setStatus(403);//账户或密码错误

    }
    @RequestMapping( "/user/register")
    public User addUser(@RequestParam MultipartFile photo,@RequestParam MultipartFile audio,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //注册用户信息
        User tempuser=new User();
        String projectPath = System.getProperty("user.dir");
        String Myfilepath=projectPath+"\\src\\main\\resources\\static\\";


//      Part filePart = request.getPart("photo"); // 获取名为"file"的部分
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
            tempuser.setPhoto("http://localhost:8080" + "/images" + "/" + fileName);
        }
        if (!audio.isEmpty()) {
            String fileName=audio.getOriginalFilename();
            try {
                audio.transferTo(new File(Myfilepath+"audios/"+fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tempuser.setMyaudio("http://localhost:8080" + "/audios" + "/" + fileName);
        }

        tempuser.setPermission(1);
        tempuser.setName(request.getParameter("name"));
        tempuser.setPassword(request.getParameter("password"));
        tempuser.setEmail(request.getParameter("email"));
        userMapper.insert(tempuser);
        response.setStatus(200);
        return tempuser;
    }
    @RequestMapping("/user/delete")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //输入要有cookie，和userid
        Integer userid = Integer.valueOf(request.getParameter("userid"));
        //用户要注销账户时
        if(request.getCookies()!=null){
            Cookie[] cookies = request.getCookies();
            Cookie id=cookies[0];
            User tempuser=userMapper.selectById(userid);
            String projectPath = System.getProperty("user.dir");
            String Myfilepath=projectPath+"\\src\\main\\resources\\static\\";

            if(Objects.equals(id.getValue(), userid.toString())){
                String temppic=tempuser.getPhoto();
                if (temppic!=null){
                    temppic=Myfilepath + "images/"+tempuser.getPhoto().substring(29);
                    FileSystemUtils.deleteRecursively(new File(temppic));
                }

                String tempaud=tempuser.getMyaudio();
                if (tempaud!=null){
                    tempaud=Myfilepath + "audios/"+tempuser.getMyaudio().substring(29);
                    FileSystemUtils.deleteRecursively(new File(tempaud));
                }

                response.setStatus(200);
                userMapper.deleteById(userid);
            }else response.setStatus(403);
        }else response.setStatus(401);


    }
}
