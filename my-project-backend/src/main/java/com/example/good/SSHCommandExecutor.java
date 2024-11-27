package com.example.good;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

public class SSHCommandExecutor {

    public static String sshcll(String command) // 要执行的CMD命令
    {
        String host = "niit01"; // 远程主机IP地址
        String user = "niit";   // SSH登录用户名
        String password = "123456"; // SSH登录密码

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();

            // 输出命令执行结果
            java.io.InputStream input = channel.getInputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                while (input.available() > 0) {
                    int i = input.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(buffer, 0, i));
                }
                if (channel.isClosed()) {
                    if (input.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {}
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "sucessful";
    }

//    public static void main(String[] args) {
//        String host = "niit01"; // 远程主机IP地址
//        String user = "niit";   // SSH登录用户名
//        String password = "123456"; // SSH登录密码
//        String command = "echo hello";  // 要执行的CMD命令
//
//        try {
//            JSch jsch = new JSch();
//            Session session = jsch.getSession(user, host, 22);
//            session.setPassword(password);
//            session.setConfig("StrictHostKeyChecking", "no");
//            session.connect();
//
//            Channel channel = session.openChannel("exec");
//            ((ChannelExec) channel).setCommand(command);
//
//            channel.setInputStream(null);
//            ((ChannelExec) channel).setErrStream(System.err);
//
//            channel.connect();
//
//            // 输出命令执行结果
//            java.io.InputStream input = channel.getInputStream();
//            byte[] buffer = new byte[1024];
//            while (true) {
//                while (input.available() > 0) {
//                    int i = input.read(buffer, 0, 1024);
//                    if (i < 0) {
//                        break;
//                    }
//                    System.out.print(new String(buffer, 0, i));
//                }
//                if (channel.isClosed()) {
//                    if (input.available() > 0) continue;
//                    System.out.println("exit-status: " + channel.getExitStatus());
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception ee) {}
//            }
//            channel.disconnect();
//            session.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
