package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
//상품저장용 폼
@Data
public class ItemForm {

    private Long itemId;
    private String itemName;

    //단일파일
    private MultipartFile attachFile;//참고 : @ModelAttribute에서 MultipartFile를 그대로 사용할 수 있다.

    //다중파일
    private List<MultipartFile> imageFiles;//이미지를 다중 업로드 하기위해 MultipartFile사용


}
