package org.theopitsi.multimedia.server.connection.user;

import java.util.Objects;
import java.util.UUID;

public class UserData {
    private final String username;
    private final UUID uuid;
    //what the user is currently watching
    //what their preferences are
    //last ping's download speed, prolly.

    public UserData(String identifier) {
        this.username = identifier;
        uuid = UUID.randomUUID();
    }

//    public void Update(UserDataUpdatePacket packet){
//
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(uuid, userData.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public String getUsername() {
        return username;
    }

    public UUID getUUID() {
        return uuid;
    }
}

