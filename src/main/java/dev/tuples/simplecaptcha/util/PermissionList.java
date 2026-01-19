package dev.tuples.simplecaptcha.util;

import com.hypixel.hytale.server.core.Message;

public enum PermissionList {
    USE("use", "You do not have permission to use this command."),
    TARGET("target", "You do not have permission to target other players with this command.");
    private final String permission;
    private final String denyMessage;

    PermissionList(String permission, String denyMessage){
        this.permission = String.format("simplecaptcha.%s", permission);
        this.denyMessage = denyMessage;
    }

    public Message getMessage(){
        return Message.raw(denyMessage);
    }

    public String getPermission() {
        return permission;
    }
}