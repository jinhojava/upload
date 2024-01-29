package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}")//application.properties의 속성을 그대로 가져올 수 있다.
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}",request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);

        Collection<Part> parts = request.getParts();//multi part 폼데이터에서 각각의 part를 받아볼수있다.
        log.info("parts={}", parts);

        for (Part part : parts) {
            log.info("===PART===");
            log.info("name={}",part.getName());
            Collection<String> headerNames = part.getHeaderNames();//part도 헤더와 바디로 나뉘어진다 각각의값 확인해보자
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName,part.getHeader(headerName));
            }
            //편의 메서드
            //content-disposition; filename
            log.info("submittedFilename={}",part.getSubmittedFileName());
            log.info("size={}",part.getSize());//part body size

            //바디 데이터 읽기
            InputStream inputStream = part.getInputStream();//바이트파일읽어오기
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info(body);

            //파일 저장하기
            if(StringUtils.hasText(part.getSubmittedFileName())){//클라이언트가 전달한 파일명
                //지정된 제출파일명이 있는가(파일인지확인)
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일저장 fullPath={}", fullPath);
                part.write(fullPath);//저장
            }

        }

        return "upload-form";
    }
}
