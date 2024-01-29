package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    private String uploadFileName;//업로드한 파일명(겹칠확률있음)
    private String storeFileName;//시스템에 저장한 파일명(고유)

}
