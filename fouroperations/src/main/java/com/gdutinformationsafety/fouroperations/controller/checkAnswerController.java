package com.gdutinformationsafety.fouroperations.controller;


import com.gdutinformationsafety.fouroperations.service.checkAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
@RequestMapping("/checkAnswer")
public class checkAnswerController {
    private checkAnswerService checkAnswerService;

    public checkAnswerController(@Autowired checkAnswerService checkAnswerService){
        this.checkAnswerService=checkAnswerService;
    }

    @PostMapping("checkResult")
    public ResponseEntity<Resource> checkAnswer(@RequestParam("testFile") MultipartFile uploadTestFile, @RequestParam("answerFile") MultipartFile uploadAnswerFile, HttpServletRequest request) {
        File testFile=checkAnswerService.saveFile(uploadTestFile);
        File answerFile=checkAnswerService.saveFile(uploadAnswerFile);
        if(testFile!=null&&answerFile!=null){
            Resource resource = checkAnswerService.check(testFile, answerFile);
            String contentType= null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

    }


}
