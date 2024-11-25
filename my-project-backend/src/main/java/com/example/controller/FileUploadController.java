package com.example.controller;

import com.example.service.FileService;
import com.example.entity.RestBean;
import com.example.service.SSHService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private SSHService sshService;

    @PostMapping("/upload")
    public ResponseEntity<RestBean<String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                messageHandle(() -> {
                    fileService.store(file);
                    return "File uploaded successfully!";  // 返回成功的消息
                })
        );
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<RestBean<String>> downloadFile(@PathVariable String filename) {
        return ResponseEntity.ok(
                messageHandle(() -> {
                    sshService.sshcll("training/shell/spark/predict.sh");
                    List<String> fileNames = List.of("Predictions.csv");
                    String remoteDirectory = "/home/willson/training/shell/spark/";
                    String localDirectory = "D:/VSCode/SpringBoot-Vue-Template-Jwt-main/my-project-frontend/src/views/img";

                    try {
                        sshService.downloadFiles(fileNames, remoteDirectory, localDirectory);
                    } catch (JSchException e) {
                        throw new RuntimeException(e);
                    } catch (SftpException e) {
                        throw new RuntimeException(e);
                    }

                    return fileService.loadFileContent("Predictions.csv");
                })  // 返回文件内容
        );
    }

    /**
     * 使用泛型方法处理所有的请求和响应，统一消息处理逻辑
     */
    private <T> RestBean<T> messageHandle(Supplier<T> action) {
        try {
            T result = action.get();
            return RestBean.success(result);  // 成功的响应
        } catch (Exception e) {
            return RestBean.failure(400, "Failed due to: " + e.getMessage());  // 错误的响应
        }
    }
}
