package org.emulinker.kaillera.controller.v086.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.emulinker.kaillera.controller.messaging.MessageFormatException;
import org.emulinker.kaillera.controller.v086.V086Controller.V086ClientHandler;
import org.emulinker.kaillera.controller.v086.protocol.GameStatus;
import org.emulinker.kaillera.model.KailleraGame;
import org.emulinker.kaillera.model.KailleraUser;
import org.emulinker.kaillera.model.event.GameStatusChangedEvent;
import org.emulinker.kaillera.model.event.ServerEvent;

public class GameStatusAction implements V086ServerEventHandler {
    private static Log log = LogFactory.getLog(GameStatusAction.class);
    private static final String desc = "GameStatusAction";
    private static GameStatusAction singleton = new GameStatusAction();

    private int handledCount = 0;

    public static GameStatusAction getInstance() {
        return singleton;
    }

    @Override
    public int getHandledEventCount() {
        return handledCount;
    }

    @Override
    public String toString() {
        return desc;
    }

    @Override
    public void handleEvent(ServerEvent event, V086ClientHandler clientHandler) {
        handledCount++;

        GameStatusChangedEvent statusChangeEvent = (GameStatusChangedEvent) event;
        try {
            KailleraGame game = statusChangeEvent.getGame();
            int num = 0;
            for (KailleraUser user : game.getPlayers()) {
                if (!user.getStealth()) {
                    num++;
                }
            }
            clientHandler.send(new GameStatus(clientHandler
                    .getNextMessageNumber(), game.getID(), 0, (byte) game
                    .getStatus(), (byte) num, (byte) game.getMaxUsers()));
        }
        catch (MessageFormatException e) {
            log.error("Failed to contruct CreateGame_Notification message: "
                    + e.getMessage(), e);
        }
    }
}
