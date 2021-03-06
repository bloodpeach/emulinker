package org.emulinker.kaillera.controller.v086.protocol;

import org.emulinker.kaillera.controller.messaging.MessageFormatException;

public class JoinGame_Request extends JoinGame {
    public static final String DESC = "Join Game Request";

    public JoinGame_Request(int messageNumber, int gameID, byte connectionType)
            throws MessageFormatException {
        super(messageNumber, gameID, 0, "", 0, 0xFFFF, connectionType);
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public String getDescription() {
        return DESC;
    }

    @Override
    public String toString() {
        return getInfoString() + "[gameID=" + getGameID() + " connectionType="
                + getConnectionType() + "]";
    }
}
