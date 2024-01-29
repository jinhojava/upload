package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    //이미지다중업로드
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
            List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                UploadFile uploadFile = storeFile(multipartFile);
                storeFileResult.add(uploadFile);
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        //업로드파일명  예 : image.png
        String originalFilename = multipartFile.getOriginalFilename();

        //서버에 저장하는 파일명
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));//저장소에 저장하기
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {

        String ext = extractExt(originalFilename);//확장자명 png 뽑기
        String uuid = UUID.randomUUID().toString(); //예 : "qqa54we-wrwrqw-678f6-86s5d65"
        String storeFileName = uuid + "." + ext; //확장자명까지 붙여주기
        //예 : "qqa54we-wrwrqw-678f6-86s5d65.png"
        return storeFileName;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos + 1);//확장자명
        return ext;
    }

}
