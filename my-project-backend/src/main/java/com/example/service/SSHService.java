package com.example.service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
public class SSHService {
    String host = "niit01";
    String user = "niit";
    String password = "123456";
    public String sshcll(String command) {

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("exec");
            // 在执行命令之前添加环境变量设置
            String fullCommand = "source /etc/profile; " + command;
            ((ChannelExec) channel).setCommand(fullCommand);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();

            // 使用BufferedReader读取命令执行结果
            InputStream input = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 直接打印到控制台
            }

            // 等待命令执行完成
            channel.disconnect();
            session.disconnect();

            return "Command executed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String statuscll(String fileName) {
        long waitTimeMillis = 20000;
        String filePath = "/home/niit/upload/" + fileName;  // 修改为你的具体路径

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("exec");
            String command = "if [ -f " + filePath + " ]; then echo 'File exists'; else echo 'File does not exist'; fi";
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream input = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            channel.disconnect();
            session.disconnect();

            String result = output.toString().trim();
            if (result.equals("File exists")) {
                return "File exists at path: " + filePath;
            } else {
                return "Error: File does not exist at path: " + filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public void uploadToRemote(MultipartFile file, String userId){
        String filePath = "/home/niit/upload/";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            try (InputStream inputStream = file.getInputStream()) {
                sftpChannel.put(inputStream, filePath + "/" + userId + ".csv");
            }

            sftpChannel.exit();
            session.disconnect();

            System.out.println("File uploaded to remote successfully");
            return;
        } catch (JSchException | SftpException | IOException e) {
            throw new RuntimeException("Failed to upload file to remote server.", e);
        }
    }

    public void downloadFiles(List<String> fileNames, String remoteDirectory, String localDirectory) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        try {
            for (String fileName : fileNames) {
                String remoteFilePath = remoteDirectory + "/" + fileName;
                String localFilePath = localDirectory + "/" + fileName;

                File localFile = new File(localFilePath);
                if (localFile.exists()) {
                    System.out.println("File " + fileName + " already exists in local directory. Skipping download.");
                    continue;
                }

                sftpChannel.get(remoteFilePath, localFilePath);
                System.out.println("Downloaded file: " + fileName);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally {
            sftpChannel.disconnect();
            session.disconnect();
        }
    }

//    public static void main(String[] args) {
//        //直接输入命令。开始目录就是training的父目录
//        new SSHService().sshcll("test.sh");
//    }
}