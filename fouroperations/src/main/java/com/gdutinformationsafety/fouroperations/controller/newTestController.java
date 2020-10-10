package com.gdutinformationsafety.fouroperations.controller;


import com.gdutinformationsafety.fouroperations.service.newTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/fourOperation")
public class newTestController {
    private newTestService newTestService;

    public newTestController(@Autowired newTestService newTestService){
        this.newTestService=newTestService;
    }


    @GetMapping("/test")
    public ResponseEntity<Resource> getTest(@RequestParam("number") String number, @RequestParam("resultLimit") String resultLimit, HttpServletRequest request){
        String fileName=newTestService.newTest(Integer.valueOf(number),Integer.valueOf(resultLimit));
        Resource resource=newTestService.loadTestToFile(fileName);
        String contentType=null;
        try{
            if(fileName==null||resource==null){
                throw new Exception();
            }
            contentType=request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("题目生成失败，请稍候再试");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

}
