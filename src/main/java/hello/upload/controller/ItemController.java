package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form){
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        //단일 파일인지 다중 파일인지 스프링이 알아서 판단해준다.


        //단일일 경우 로직실행
        MultipartFile a = form.getAttachFile();
        UploadFile attachFile = fileStore.storeFile(a);

        //다중일 경우 로직실행 (알아서 여러개 다 받고 반복실행)
        List<MultipartFile> imageFiles = form.getImageFiles();
        List<UploadFile> storeImageFiles = fileStore.storeFiles(imageFiles);

        //데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemsId", item.getId());

        return "redirect:/items/{itemsId}";
    }

    //사용자에게 이미지 파일 보여주기
    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model){
        Item item = itemRepository.findById(id);
        model.addAttribute("item",item);
        return "item-view";
    }

    //실제 이미지 파일 url
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
       return new UrlResource("file:"+fileStore.getFullPath(filename));
        //UrlResource가 파일경로와 이름을 주면 알아서 파일 찾아온다.
    }

    @GetMapping("/attach/{itemId}")//첨부파일다운
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);

        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        //UrlResource가 파일경로 이름 주면 알아서 파일 찾아온다
        log.info("uploadFileName={}",uploadFileName);

        //한글 파일명 파일 깨짐 방지
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        String contentDisposition="attachment; filename=\""+encodedUploadFileName+"\"";
        //헤더추가해야함 안그러면 첨부파일 다운안됨

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)//필수
                .body(resource);
            }

//    Content-Disposition 헤더생성코드. 여기서 attachment는 파일을 다운로드하도록지시하고, filename은 다운로드될 파일의 이름을 지정.
//uploadFileName은 다운로드될 파일의 실제 이름을 나타냄. 이 이름은 클라이언트(브라우저)에서 다운로드할 때 사용. \와 "는 이중 따옴표를 문자열 안에 포함시키기 위한 이스케이프 문자.
//    따라서, 예를 들어 uploadFileName이 "example.txt"라면, 최종적으로 Content-Disposition 헤더는 다음과 같이 될 것.
//    Content-Disposition: attachment; filename="example.txt"
//    이렇게 설정된 헤더는 브라우저에게 해당 파일을 다운로드하라는 지시를 전달하고, 다운로드되는 파일의 이름을 "example.txt"로 표시.
}
