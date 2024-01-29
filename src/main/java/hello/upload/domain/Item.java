package hello.upload.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String itemName;

    //단일파일
    private UploadFile attachFile;

    //다중파일
    private List<UploadFile> imageFiles;

}
