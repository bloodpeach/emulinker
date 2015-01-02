package org.emulinker.kaillera.controller.v086.action;

import org.apache.commons.logging.*;
import org.emulinker.kaillera.controller.messaging.MessageFormatException;
import org.emulinker.kaillera.controller.v086.V086Controller;
import org.emulinker.kaillera.controller.v086.protocol.*;
import org.emulinker.kaillera.model.*;
import org.emulinker.kaillera.model.event.*;
import org.emulinker.kaillera.model.exception.DropGameException;

public class DropGameAction implements V086Action, V086GameEventHandler {
    private static Log log = LogFactory.getLog(DropGameAction.class);
    private static final String desc = "DropGameAction";
    private static DropGameAction singleton = new DropGameAction();

    private int actionCount = 0;
    private int handledCount = 0;

    public static DropGameAction getInstance() {
        return singleton;
    }

    @Override
    public int getActionPerformedCount() {
        return actionCount;
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
    public void performAction(V086Message message,
            V086Controller.V086ClientHandler clientHandler)
            throws FatalActionException {
        if (!(message instanceof PlayerDrop_Request)) {
            throw new FatalActionException(
                    "Received incorrect instance of PlayerDrop: " + message);
        }

        actionCount++;
        try {
            log.error("Mysterious Drop in DropGameAction");
            clientHandler.getUser().dropGame();
        }
        catch (DropGameException e) {
            log.debug("Failed to drop game: " + e.getMessage());
        }
    }

    @Override
    public void handleEvent(GameEvent event,
            V086Controller.V086ClientHandler clientHandler) {
        handledCount++;

        UserDroppedGameEvent userDroppedEvent = (UserDroppedGameEvent) event;
        try {
            KailleraUser user = userDroppedEvent.getUser();
            int playerNumber = userDroppedEvent.getPlayerNumber();

            if (!user.getStealth()) {
                clientHandler.send(new PlayerDrop_Notification(clientHandler
                        .getNextMessageNumber(), user.getName(),
                        (byte) playerNumber));
            }
        }
        catch (MessageFormatException e) {
            log.error("Failed to contruct PlayerDrop_Notification message: "
                    + e.getMessage(), e);
        }
    }
}
