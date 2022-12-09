package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

public record XboxToken(String token, String userHash) {
    public String header() {
        return "XBL3.0 x=" + userHash() + ";" + token();
    }
}
