package hello.upload.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {


    @Value("${file.dir}")//application.properties의 속성을 그대로 가져올 수 있다.
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }


    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file, HttpServletRequest request) throws IOException//request없어도되는데 log찍으려고
    {
        log.info("request={}", request);
        log.info("iterName={}",itemName);
        log.info("multipartFile={}",file);

        if(!file.isEmpty()){
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath));//저장할 파일 위치. file을 괄호안 경로로 보낸다
        }
        return "upload-form";
    }

}
