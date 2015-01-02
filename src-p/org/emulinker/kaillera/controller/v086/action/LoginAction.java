package org.emulinker.kaillera.controller.v086.action;

import org.apache.commons.logging.*;
import org.emulinker.kaillera.access.AccessManager;
import org.emulinker.kaillera.controller.messaging.MessageFormatException;
import org.emulinker.kaillera.controller.v086.V086Controller;
import org.emulinker.kaillera.controller.v086.protocol.*;
import org.emulinker.kaillera.model.*;
import org.emulinker.kaillera.model.event.*;
import org.emulinker.kaillera.model.impl.*;

public class LoginAction implements V086Action, V086ServerEventHandler {
    private static Log log = LogFactory.getLog(LoginAction.class);
    private static final String desc = "LoginAction";
    private static LoginAction singleton = new LoginAction();

    public static LoginAction getInstance() {
        return singleton;
    }

    private int actionCount = 0;
    private int handledCount = 0;

    private LoginAction() {
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
        actionCount++;

        UserInformation userInfo = (UserInformation) message;
        KailleraUser user = clientHandler.getUser();

        // Username Hacking
        if (user.isLoggedIn()) {
            for (KailleraUser i : user.getServer().getUsers()) {
                if (i.getConnectSocketAddress()
                        .getAddress()
                        .getHostAddress()
                        .equals(user.getConnectSocketAddress().getAddress()
                                .getHostAddress())) {
                    try {
                        i.quit("Forceful Exit: Dupe");
                    }
                    catch (Exception e) {
                    }
                }
            }

            throw new IllegalArgumentException("Forceful Exit: Duplicate = "
                    + user.getName() + ", "
                    + user.getSocketAddress().getAddress().getHostAddress());
        }

        user.setName(userInfo.getUserName());
        user.setClientType(userInfo.getClientType());
        user.setSocketAddress(clientHandler.getRemoteSocketAddress());
        user.setConnectionType(userInfo.getConnectionType());

        clientHandler.startSpeedTest();

        try {
            clientHandler.send(new ServerACK(clientHandler
                    .getNextMessageNumber()));
        }
        catch (MessageFormatException e) {
            log.error(
                    "Failed to contruct ServerACK message: " + e.getMessage(),
                    e);
        }
    }

    @Override
    public void handleEvent(ServerEvent event,
            V086Controller.V086ClientHandler clientHandler) {
        handledCount++;

        UserJoinedEvent userJoinedEvent = (UserJoinedEvent) event;

        try {
            KailleraUserImpl user = (KailleraUserImpl) userJoinedEvent
                    .getUser();
            clientHandler.send(new UserJoined(clientHandler
                    .getNextMessageNumber(), user.getName(), user.getID(), user
                    .getPing(), user.getConnectionType()));

            KailleraUserImpl thisUser = (KailleraUserImpl) clientHandler
                    .getUser();

            if (thisUser.isEmuLinkerClient()
                    && thisUser.getAccess() >= AccessManager.ACCESS_ADMIN) {
                if (!user.equals(thisUser)) {
                    StringBuilder sb = new StringBuilder();

                    sb.append(":USERINFO=");
                    sb.append(user.getID());
                    sb.append('\002');
                    sb.append(user.getConnectSocketAddress().getAddress()
                            .getHostAddress());
                    sb.append('\002');
                    sb.append(user.getAccessStr());
                    sb.append('\002');
                    sb.append(user.getName());
                    sb.append('\002');
                    sb.append(user.getPing());
                    sb.append('\002');
                    sb.append(user.getStatus());
                    sb.append('\002');
                    sb.append(user.getConnectionType());

                    clientHandler.send(new InformationMessage(clientHandler
                            .getNextMessageNumber(), "server", sb.toString()));
                }
            }
        }
        catch (MessageFormatException e) {
            log.error("Failed to contruct UserJoined_Notification message: "
                    + e.getMessage(), e);
        }
    }
}
