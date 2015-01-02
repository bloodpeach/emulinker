package org.emulinker.kaillera.controller.v086.action;

import org.apache.commons.logging.*;
import org.emulinker.kaillera.controller.messaging.MessageFormatException;
import org.emulinker.kaillera.controller.v086.V086Controller;
import org.emulinker.kaillera.controller.v086.protocol.*;
import org.emulinker.kaillera.model.KailleraUser;
import org.emulinker.kaillera.model.event.*;
import org.emulinker.kaillera.model.exception.ActionException;

public class QuitAction implements V086Action, V086ServerEventHandler {
    private static Log log = LogFactory.getLog(QuitAction.class);
    private static final String desc = "QuitAction";

    private static QuitAction singleton = new QuitAction();

    public static QuitAction getInstance() {
        return singleton;
    }

    private int actionCount = 0;
    private int handledCount = 0;

    private QuitAction() {

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
        if (!(message instanceof Quit_Request)) {
            throw new FatalActionException(
                    "Received incorrect instance of Quit: " + message);
        }

        actionCount++;

        Quit_Request quitRequest = (Quit_Request) message;

        try {
            clientHandler.getUser().quit(quitRequest.getMessage());
        }
        catch (ActionException e) {
            throw new FatalActionException("Failed to quit: " + e.getMessage());
        }
    }

    @Override
    public void handleEvent(ServerEvent event,
            V086Controller.V086ClientHandler clientHandler) {
        handledCount++;

        UserQuitEvent userQuitEvent = (UserQuitEvent) event;

        try {
            KailleraUser user = userQuitEvent.getUser();

            String m = userQuitEvent.getMessage();
            String temp = m;

            temp = temp.replace(" ", "");

            if (temp.toLowerCase().contains("ggpo.net")) {
                m = "http://www.God-Weapon.com";
            }
            else if (temp.toLowerCase().contains("2dfighter.com")) {
                m = "http://www.God-Weapon.com";
            }

            // 69.90.34.245
            else if (temp.toLowerCase().contains("69")
                    && temp.toLowerCase().contains("90")
                    && temp.toLowerCase().contains("34")
                    && temp.toLowerCase().contains("245")) {
                m = "k.god-weapon.com:27888";
            }
            else if (temp.toLowerCase().contains("209")
                    && temp.toLowerCase().contains("144")
                    && temp.toLowerCase().contains("21")
                    && temp.toLowerCase().contains("174")) {
                m = "k.god-weapon.com:27888";
            }

            clientHandler.send(new Quit_Notification(clientHandler
                    .getNextMessageNumber(), user.getName(), user.getID(), m));
        }
        catch (MessageFormatException e) {
            log.error(
                    "Failed to contruct Quit_Notification message: "
                            + e.getMessage(), e);
        }
    }
}
