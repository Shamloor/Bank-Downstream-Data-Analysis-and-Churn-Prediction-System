package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.FileService;
import com.example.service.SSHService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/ssh")
public class SSHController {

    @Autowired
    private SSHService sshService;
    @Autowired
    private FileService fileService;

    @PostMapping("/command")
    public ResponseEntity<RestBean<String>> executeSSHCommand(@RequestParam("command") String command) {
        return ResponseEntity.ok(
                messageHandle(() -> {
                    sshService.sshcll(command);
                    List<String> fileNames = List.of(
                            "countplots.png",
                            "df7.png",
                            "train_age.png",
                            "train_balance.png",
                            "train_creditscore.png",
                            "train_estimatedsalary.png",
                            "train_exited(1).png",
                            "train_exited.png",
                            "train_gender(1).png",
                            "train_gender.png",
                            "train_geography(1).png",
                            "train_geography.png",
                            "train_hascrcard(1).png",
                            "train_hascrcard.png",
                            "train_isactivemember(1).png",
                            "train_isactivemember.png",
                            "train_numofproducts(1).png",
                            "train_numofproducts.png",
                            "train_tenure(1).png",
                            "train_tenure.png"
                    );
                    String remoteDirectory = "/home/willson/training/shell/visual/";
                    String localDirectory = "D:/VSCode/SpringBoot-Vue-Template-Jwt-main/my-project-frontend/src/views/img/xx";

                    try {
                        sshService.downloadFiles(fileNames, remoteDirectory, localDirectory);
                    } catch (JSchException e) {
                        throw new RuntimeException(e);
                    } catch (SftpException e) {
                        throw new RuntimeException(e);
                    }
                    return "Training successfully!";  // 返回成功的消息
                })
        );
    }

    @GetMapping("/status/{filename:.+}")
    public ResponseEntity<RestBean<String>> executeSSHStatus(@PathVariable String filename) {
        return ResponseEntity.ok(
                messageHandle(() -> sshService.statuscll(filename))  // 返回文件内容
        );
    }

    @GetMapping("/train/{filename:.+}")
    public ResponseEntity<RestBean<String>> executeSSHPredict(@PathVariable String filename) {
        return ResponseEntity.ok(
                messageHandle(() -> {
                    sshService.sshcll("cd training/shell/spark/ && ./model_training.sh");
                    System.out.println("good....................................................................................................");
                    List<String> fileNames = List.of("merged_evaluation.csv", "roc_curve.png", "feature_importances.png");
                    String remoteDirectory = "/home/willson/training/shell/spark/";
                    String localDirectory = "D:/VSCode/SpringBoot-Vue-Template-Jwt-main/my-project-frontend/src/views/img";

                    try {
                        sshService.downloadFiles(fileNames, remoteDirectory, localDirectory);
                    } catch (JSchException e) {
                        throw new RuntimeException(e);
                    } catch (SftpException e) {
                        throw new RuntimeException(e);
                    }
                    return fileService.loadFileContent("merged_evaluation.csv");
                })  // 返回文件内容
        );
    }
    private <T> RestBean<T> messageHandle(Supplier<T> action) {
        try {
            T result = action.get();
            return RestBean.success(result);  // 成功的响应
        } catch (Exception e) {
            return RestBean.failure(400, "Failed due to: " + e.getMessage());  // 错误的响应
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String userId = getCurrentUserId();
            sshService.uploadToRemote(file, userId); // 上传到远程虚拟机
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 获取用户名作为用户ID，您可以根据实际情况修改
    }
}