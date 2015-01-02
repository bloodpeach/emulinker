package org.emulinker.kaillera.controller.v086.action;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.emulinker.kaillera.access.AccessManager;
import org.emulinker.kaillera.controller.messaging.MessageFormatException;
import org.emulinker.kaillera.controller.v086.V086Controller;
import org.emulinker.kaillera.controller.v086.protocol.*;
import org.emulinker.kaillera.model.exception.ActionException;
import org.emulinker.kaillera.model.impl.*;
import org.emulinker.kaillera.model.*;
import org.emulinker.util.EmuLang;

public class GameOwnerCommandAction implements V086Action {
    public static final String COMMAND_HELP = "/help";
    public static final String COMMAND_DETECTAUTOFIRE = "/detectautofire";

    // SF MOD
    public static final String COMMAND_LAGSTAT = "/lag";
    public static final String COMMAND_MAXUSERS = "/maxusers";
    public static final String COMMAND_MAXPING = "/maxping";
    public static final String COMMAND_START = "/start";
    public static final String COMMAND_STARTN = "/startn";
    public static final String COMMAND_MUTE = "/mute";
    public static final String COMMAND_UNMUTE = "/unmute";
    public static final String COMMAND_SWAP = "/swap";
    public static final String COMMAND_KICK = "/kick";
    public static final String COMMAND_EMU = "/setemu";
    public static final String COMMAND_SAMEDELAY = "/samedelay";
    public static final String COMMAND_NUM = "/num";
    private static long lastMaxUserChange = 0;
    private static Log log = LogFactory.getLog(GameOwnerCommandAction.class);
    private static final String desc = "GameOwnerCommandAction";
    private static GameOwnerCommandAction singleton = new GameOwnerCommandAction();
    private int actionCount = 0;

    public static GameOwnerCommandAction getInstance() {
        return singleton;
    }

    private GameOwnerCommandAction() {

    }

    @Override
    public int getActionPerformedCount() {
        return actionCount;
    }

    @Override
    public String toString() {
        return desc;
    }

    public boolean isValidCommand(String chat) {
        if (chat.startsWith(COMMAND_HELP)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_DETECTAUTOFIRE)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_MAXUSERS)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_MAXPING)) {
            return true;
        }
        else if (chat.equals(COMMAND_START)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_STARTN)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_MUTE)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_EMU)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_UNMUTE)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_SWAP)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_KICK)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_SAMEDELAY)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_LAGSTAT)) {
            return true;
        }
        else if (chat.startsWith(COMMAND_NUM)) {
            return true;
        }
        return false;
    }

    @Override
    public void performAction(V086Message message,
            V086Controller.V086ClientHandler clientHandler)
            throws FatalActionException {
        GameChat chatMessage = (GameChat) message;
        String chat = chatMessage.getMessage();

        KailleraUserImpl user = (KailleraUserImpl) clientHandler.getUser();
        KailleraGameImpl game = user.getGame();

        if (game == null) {
            throw new FatalActionException(
                    "GameOwner Command Failed: Not in a game: " + chat);
        }

        if (!user.equals(game.getOwner())
                && user.getAccess() < AccessManager.ACCESS_ADMIN) {
            log.warn("GameOwner Command Denied: Not game owner: " + game + ": "
                    + user + ": " + chat);
            return;
        }

        try {
            if (chat.startsWith(COMMAND_HELP)) {
                processHelp(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_DETECTAUTOFIRE)) {
                processDetectAutoFire(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_MAXUSERS)) {
                processMaxUsers(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_MAXPING)) {
                processMaxPing(chat, game, user, clientHandler);
            }
            else if (chat.equals(COMMAND_START)) {
                processStart(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_STARTN)) {
                processStartN(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_MUTE)) {
                processMute(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_EMU)) {
                processEmu(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_UNMUTE)) {
                processUnmute(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_SWAP)) {
                processSwap(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_KICK)) {
                processKick(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_LAGSTAT)) {
                processLagstat(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_SAMEDELAY)) {
                processSameDelay(chat, game, user, clientHandler);
            }
            else if (chat.startsWith(COMMAND_NUM)) {
                processNum(chat, game, user, clientHandler);
            }
            else {
                game.announce("Unknown Command: " + chat, user);
                log.info("Unknown GameOwner Command: " + game + ": " + user
                        + ": " + chat);
            }
        }
        catch (ActionException e) {
            log.info("GameOwner Command Failed: " + game + ": " + user + ": "
                    + chat);
            game.announce(
                    EmuLang.getString("GameOwnerCommandAction.CommandFailed",
                            e.getMessage()), user);
        }
        catch (MessageFormatException e) {
            log.error("Failed to contruct message: " + e.getMessage(), e);
        }
    }

    private void processHelp(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        // game.setIndividualGameAnnounce(admin.getPlayerNumber());
        // game.announce(EmuLang.getString("GameOwnerCommandAction.AvailableCommands"));
        // try { Thread.sleep(20); } catch(Exception e) {}
        game.announce(EmuLang
                .getString("GameOwnerCommandAction.SetAutofireDetection"),
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce("/maxusers <#> to set capacity of room", admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce("/maxping <#> to set maximum ping for room", admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/start or /startn <#> start game when n players are joined.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/mute /unmute  <UserID> or /muteall or /unmuteall to mute player(s).",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/swap <order> eg. 123..n {n = total # of players; Each slot = new player#}",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce("/kick <Player#> or /kickall to kick a player(s).", admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/setemu <Emulator> To restrict the gameroom to this emulator!",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/lagstat To check who has the most lag spikes or /lagreset to reset lagstat!",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/samedelay {true | false} If you want both want to play at the same delay setting. Default is false.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/me <message> to make personal message eg. /me is bored ...SupraFast is bored.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/msg <UserID> <msg> to PM somebody. /msgon or /msgoff to turn pm on | off.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/ignore <UserID> or /unignore <UserID> or /ignoreall or /unignoreall to ignore users.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                "/p2pon or /p2poff this option ignores ALL server activity during gameplay.",
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
    }

    private void autoFireHelp(KailleraGameImpl game, KailleraUserImpl admin) {
        int cur = game.getAutoFireDetector().getSensitivity();
        game.announce(
                EmuLang.getString("GameOwnerCommandAction.HelpSensitivity"),
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(EmuLang.getString("GameOwnerCommandAction.HelpDisable"),
                admin);
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {
        }
        game.announce(
                EmuLang.getString(
                        "GameOwnerCommandAction.HelpCurrentSensitivity", cur)
                        + (cur == 0 ? (EmuLang
                                .getString("GameOwnerCommandAction.HelpDisabled"))
                                : ""), admin);
    }

    private void processDetectAutoFire(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        if (game.getStatus() != KailleraGame.STATUS_WAITING) {
            game.announce(
                    EmuLang.getString("GameOwnerCommandAction.AutoFireChangeDeniedInGame"),
                    admin);
            return;
        }
        StringTokenizer st = new StringTokenizer(message, " ");
        if (st.countTokens() != 2) {
            autoFireHelp(game, admin);
            return;
        }
        String command = st.nextToken();
        String sensitivityStr = st.nextToken();
        int sensitivity = -1;
        try {
            sensitivity = Integer.parseInt(sensitivityStr);
        }
        catch (NumberFormatException e) {
        }

        if (sensitivity > 5 || sensitivity < 0) {
            autoFireHelp(game, admin);
            return;
        }
        game.getAutoFireDetector().setSensitivity(sensitivity);
        game.announce(
                EmuLang.getString(
                        "GameOwnerCommandAction.HelpCurrentSensitivity",
                        sensitivity)
                        + (sensitivity == 0 ? (EmuLang
                                .getString("GameOwnerCommandAction.HelpDisabled"))
                                : ""), null);
    }

    private void processEmu(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");

        scanner.next();
        String str = scanner.next();

        if (str.length() < 1) {
            admin.getGame().announce("Invalid Argument!", admin);
            return;
        }

        if (admin.getServer().getAccessManager()
                .isSilenced(admin.getSocketAddress().getAddress())) {
            admin.getGame().announce("Chat Denied: You're silenced!", admin);
            return;
        }

        try {

            admin.getGame().setAEmulator(str);
            admin.getGame().announce(
                    "Owner has restricted the emulator to: " + str, null);
            return;
        }
        catch (NoSuchElementException e) {
            admin.getGame().announce("Emulator Set Error: /setemu <Emulator>",
                    admin);
        }
    }

    private void processNum(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        admin.getGame().announce(game.getNumPlayers() + " in the room!", admin);
    }

    private void processLagstat(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {

        if (game.getStatus() != KailleraGame.STATUS_PLAYING) {
            game.announce("Lagstat is only available during gameplay!", admin);
        }
        if (message.equals("/lagstat")) {
            String str = "";
            for (KailleraUser player : game.getPlayers()) {
                if (!player.getStealth()) {
                    str = str + "P" + player.getPlayerNumber() + ": "
                            + player.getTimeouts() + ", ";
                }
            }

            str = str.substring(0, str.length() - ", ".length());
            game.announce(str + " lag spikes", null);
        }
        else if (message.equals("/lagreset")) {
            for (KailleraUser player : game.getPlayers()) {
                player.setTimeouts(0);
            }

            game.announce("LagStat has been reset!", null);
        }
    }

    private void processSameDelay(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        if (message.equals("/samedelay true")) {
            game.setSameDelay(true);
            admin.getGame()
                    .announce(
                            "Players will have the same delay when game starts (restarts)!",
                            null);
        }
        else {
            game.setSameDelay(false);
            admin.getGame()
                    .announce(
                            "Players will have independent delays when game starts (restarts)!",
                            null);
        }
    }

    private void processMute(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");

        String str = scanner.next();
        if (str.equals("/muteall")) {
            for (int w = 1; w <= game.getPlayers().size(); w++) {
                if (game.getPlayer(w).getAccess() < AccessManager.ACCESS_ADMIN) {
                    game.getPlayer(w).setMute(true);
                }
            }
            admin.getGame().announce("All players have been muted!", null);
            return;
        }

        int userID = scanner.nextInt();
        if (userID < 1) {
            admin.getGame().announce("Player doesn't exist!", admin);
            return;
        }
        KailleraUserImpl user = (KailleraUserImpl) clientHandler.getUser()
                .getServer().getUser(userID);

        if (user == null) {
            admin.getGame().announce("Player doesn't exist!", admin);
            return;
        }
        if (user.getAccess() >= AccessManager.ACCESS_ADMIN
                && admin.getAccess() != AccessManager.ACCESS_SUPERADMIN) {
            user.getGame().announce("You can't mute an Admin", admin);
            return;
        }
        if (user == clientHandler.getUser()) {
            user.getGame().announce("You can't mute yourself!", admin);
            return;
        }
        try {
            user.setMute(true);
            KailleraUserImpl user1 = (KailleraUserImpl) clientHandler.getUser();
            user1.getGame().announce(user.getName() + " has been muted!", null);
        }
        catch (NoSuchElementException e) {
            user.getGame().announce("Mute Player Error: /mute <PlayerNumber>",
                    admin);
        }
    }

    private void processUnmute(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");

        String str = scanner.next();
        if (str.equals("/unmuteall")) {
            for (int w = 1; w <= game.getPlayers().size(); w++) {
                game.getPlayer(w).setMute(false);
            }
            admin.getGame().announce("All players have been unmuted!", null);
            return;
        }
        int userID = scanner.nextInt();
        if (userID < 1) {
            admin.getGame().announce("Player doesn't exist!", admin);
            return;
        }
        KailleraUserImpl user = (KailleraUserImpl) clientHandler.getUser()
                .getServer().getUser(userID);

        if (user == null) {
            admin.getGame().announce("Player doesn't exist!", admin);
            return;
        }

        if (user.getAccess() >= AccessManager.ACCESS_ADMIN
                && admin.getAccess() != AccessManager.ACCESS_SUPERADMIN) {
            user.getGame().announce("You can't unmute an Admin", admin);
            return;
        }

        if (user == clientHandler.getUser()) {
            user.getGame().announce("You can't unmute yourself!", admin);
            return;
        }
        try {
            user.setMute(false);
            KailleraUserImpl user1 = (KailleraUserImpl) clientHandler.getUser();
            user1.getGame().announce(user.getName() + " has been unmuted!",
                    null);
        }
        catch (NoSuchElementException e) {
            user.getGame().announce(
                    "Unmute Player Error: /unmute <PlayerNumber>", admin);
        }
    }

    private void processStartN(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");
        try {
            scanner.next();
            int num = scanner.nextInt();

            if (num > 0 && num < 101) {
                game.setStartN((byte) num);
                game.announce("This game will start when " + num
                        + " players have joined.", null);
            }
            else {
                game.announce("StartN Error: Enter value between 1 and 100.",
                        admin);
            }
        }
        catch (NoSuchElementException e) {
            game.announce("Failed: /startn <#>", admin);
        }
    }

    private void processSwap(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        if (game.getStatus() != KailleraGame.STATUS_PLAYING) {
            game.announce("Failed: /swap can only be used during gameplay!",
                    admin);
            return;
        }

        Scanner scanner = new Scanner(message).useDelimiter(" ");

        scanner.next();
        int test = scanner.nextInt();
        String str = Integer.toString(test);

        if (game.getPlayers().size() < str.length()) {
            game.announce(
                    "Failed: You can't swap more than the # of players in the room.",
                    admin);
            return;
        }
        try {
            PlayerActionQueue temp = game.getPlayerActionQueue()[0];
            for (int i = 0; i < str.length(); i++) {
                KailleraUserImpl player = (KailleraUserImpl) game.getPlayers()
                        .get(i);
                String w = String.valueOf(str.charAt(i));
                player.setPlayerNumber(Integer.parseInt(w));

                if (Integer.parseInt(w) == 1) {
                    game.getPlayerActionQueue()[i] = temp;
                }
                else {
                    game.getPlayerActionQueue()[i] = game
                            .getPlayerActionQueue()[Integer.parseInt(w) - 1];
                }

                game.announce(
                        player.getName() + " is now Player#: "
                                + player.getPlayerNumber(), null);
            }

        }
        catch (NoSuchElementException e) {
            game.announce(
                    "Swap Player Error: /swap <order> eg. 123..n {n = total # of players; Each slot = new player#}",
                    admin);
        }
    }

    private void processStart(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        game.start(admin);
    }

    private void processKick(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");

        try {
            String str = scanner.next();
            if (str.equals("/kickall")) {
                for (int w = 1; w <= game.getPlayers().size(); w++) {
                    game.kick(game.getPlayer(w), game.getPlayer(w).getID());
                }
                admin.getGame().announce("All players have been kicked!", null);
                return;
            }
            int playerNumber = scanner.nextInt();

            if (game.getPlayer(playerNumber) != null) {
                game.kick(admin, game.getPlayer(playerNumber).getID());
            }
            else {
                game.announce("Player doesn't exisit!", admin);
            }
        }
        catch (NoSuchElementException e) {
            game.announce(
                    "Failed: /kick <Player#> or /kickall to kick all players.",
                    admin);
        }
    }

    private void processMaxUsers(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        if ((System.currentTimeMillis() - lastMaxUserChange) <= 3000) {
            game.announce("Max User Command Spam Detection...Please Wait!",
                    admin);
            lastMaxUserChange = System.currentTimeMillis();
            return;
        }

        lastMaxUserChange = System.currentTimeMillis();

        Scanner scanner = new Scanner(message).useDelimiter(" ");
        try {
            scanner.next();
            int num = scanner.nextInt();

            if (num > 0 && num < 101) {
                game.setMaxUsers(num);
                game.announce("Max Users has been set to " + num, null);
            }
            else {
                game.announce("Max Users Error: Enter value between 1 and 100",
                        admin);
            }
        }
        catch (NoSuchElementException e) {
            game.announce("Failed: /maxusers <#>", admin);
        }
    }

    private void processMaxPing(String message, KailleraGameImpl game,
            KailleraUserImpl admin,
            V086Controller.V086ClientHandler clientHandler)
            throws ActionException, MessageFormatException {
        Scanner scanner = new Scanner(message).useDelimiter(" ");
        try {
            scanner.next();
            int num = scanner.nextInt();
            if (num > 0 && num < 1001) {
                game.setMaxPing(num);
                game.announce("Max Ping has been set to " + num, null);
            }
            else {
                game.announce("Max Ping Error: Enter value between 1 and 1000",
                        admin);
            }
        }
        catch (NoSuchElementException e) {
            game.announce("Failed: /maxping <#>", admin);
        }
    }
}