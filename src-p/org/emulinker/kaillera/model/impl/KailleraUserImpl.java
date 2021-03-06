package org.emulinker.kaillera.model.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.logging.*;

import org.emulinker.kaillera.access.*;
import org.emulinker.kaillera.model.*;
import org.emulinker.kaillera.model.event.*;
import org.emulinker.kaillera.model.exception.*;
import org.emulinker.util.*;

public final class KailleraUserImpl implements KailleraUser, Executable {
    private static Log log = LogFactory.getLog(KailleraUserImpl.class);
    private static final String EMULINKER_CLIENT_NAME = "Emulinker X Admin Client v1.1";

    private KailleraServerImpl server;
    private KailleraGameImpl game;

    double latency;
    private int id;
    private String name;
    private String protocol;
    private boolean stealth = false;
    private String clientType;

    private byte connectionType;
    private int ping;
    private InetSocketAddress connectSocketAddress;
    private InetSocketAddress clientSocketAddress;
    private int status;
    private boolean loggedIn;
    private String toString;
    private int access;
    private boolean emulinkerClient;

    private long connectTime;
    private int timeouts = 0;
    private long lastActivity;
    private long lastKeepAlive;
    private long lastChatTime;
    private long lastGameChatTime;
    private long lastCreateGameTime;
    private long lastTimeout;

    private int frameCount;
    private int delay;
    private int tempDelay;
    private int totalDelay;
    private int bytesPerAction;
    private int arraySize;

    private boolean p2P = false;

    private int playerNumber = -1;
    private boolean ignoreAll = false;
    private boolean msg = true;
    private int lastMsgID = -1;
    private boolean mute = false;

    private List<byte[]> lostInput = new ArrayList<byte[]>();
    private boolean hasData = false;
    private List<String> ignoredUsers = new ArrayList<String>();

    private long gameDataErrorTime = -1;

    private boolean isRunning = false;
    private boolean stopFlag = false;

    private KailleraEventListener listener;
    private BlockingQueue<KailleraEvent> eventQueue = new LinkedBlockingQueue<KailleraEvent>();

    public KailleraUserImpl(int userID, String protocol,
            InetSocketAddress connectSocketAddress,
            KailleraEventListener listener, KailleraServerImpl server) {
        this.id = userID;
        this.protocol = protocol;
        this.connectSocketAddress = connectSocketAddress;
        this.server = server;
        this.listener = listener;

        toString = "User" + userID + "(" + connectSocketAddress.getAddress().getHostAddress() + ")";

        lastChatTime = lastCreateGameTime = lastTimeout = 0;
        lastActivity = lastKeepAlive = connectTime = System.currentTimeMillis();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public InetSocketAddress getConnectSocketAddress() {
        return connectSocketAddress;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public int getArraySize() {
        return arraySize;
    }

    @Override
    public int getBytesPerAction() {
        return bytesPerAction;
    }

    @Override
    public long getConnectTime() {
        return connectTime;
    }

    @Override
    public boolean getMute() {
        return mute;
    }

    @Override
    public void setTempDelay(int tempDelay) {
        this.tempDelay = tempDelay;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void setLastMsgID(int lastMsgID) {
        this.lastMsgID = lastMsgID;
    }

    @Override
    public int getLastMsgID() {
        return lastMsgID;
    }

    @Override
    public void setMute(boolean mute) {
        this.mute = mute;
    }

    @Override
    public void setIgnoreAll(boolean ignoreAll) {
        this.ignoreAll = ignoreAll;
    }

    @Override
    public int getFrameCount() {
        return frameCount;
    }

    @Override
    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    @Override
    public boolean getIgnoreAll() {
        return ignoreAll;
    }

    @Override
    public int getTimeouts() {
        return timeouts;
    }

    @Override
    public void setTimeouts(int timeouts) {
        this.timeouts = timeouts;
    }

    @Override
    public boolean getMsg() {
        return msg;
    }

    @Override
    public boolean getP2P() {
        return p2P;
    }

    @Override
    public byte[] getLostInput() {
        return lostInput.get(0);
    }

    @Override
    public void setP2P(boolean p2P) {
        this.p2P = p2P;
    }

    @Override
    public void setMsg(boolean msg) {
        this.msg = msg;
    }

    @Override
    public Collection<KailleraUserImpl> getUsers() {
        return getServer().getUsers();
    }

    @Override
    public void addIgnoredUser(String address) {
        ignoredUsers.add(address);
    }

    @Override
    public boolean findIgnoredUser(String address) {
        for (int i = 0; i < ignoredUsers.size(); i++) {
            if (ignoredUsers.get(i).equals(address)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeIgnoredUser(String address, boolean removeAll) {
        int i = 1;
        boolean here = false;

        if (removeAll == true) {
            ignoredUsers.clear();
            return true;
        }

        for (i = 0; i < ignoredUsers.size(); i++) {
            if (ignoredUsers.get(i).equals(address)) {
                ignoredUsers.remove(i);
                here = true;
            }
        }

        return here;
    }

    @Override
    public boolean searchIgnoredUsers(String address) {
        int i = 1;

        for (i = 0; i < ignoredUsers.size(); i++) {
            if (ignoredUsers.get(i).equals(address)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn() {
        loggedIn = true;
    }

    public void setLoggedIn(boolean loggedIn) {
        loggedIn = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        toString = "User" + id + "(" + (name.length() > 15 ? (name.substring(0, 15) + "...") : name) + "/" + connectSocketAddress.getAddress().getHostAddress() + ")";
    }

    @Override
    public String getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(String clientType) {
        this.clientType = clientType;

        if (clientType != null && clientType.startsWith(EMULINKER_CLIENT_NAME)) {
            emulinkerClient = true;
        }
    }

    @Override
    public boolean isEmuLinkerClient() {
        return emulinkerClient;
    }

    @Override
    public byte getConnectionType() {
        return connectionType;
    }

    @Override
    public void setConnectionType(byte connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public boolean getStealth() {
        return stealth;
    }

    @Override
    public void setStealth(boolean stealth) {
        this.stealth = stealth;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return clientSocketAddress;
    }

    @Override
    public void setSocketAddress(InetSocketAddress clientSocketAddress) {
        this.clientSocketAddress = clientSocketAddress;
    }

    @Override
    public int getPing() {
        return ping;
    }

    @Override
    public void setPing(int ping) {
        this.ping = ping;
    }

    @Override
    public long getLastActivity() {
        return lastActivity;
    }

    @Override
    public long getLastKeepAlive() {
        return lastKeepAlive;
    }

    @Override
    public void updateLastKeepAlive() {
        this.lastKeepAlive = System.currentTimeMillis();
    }

    @Override
    public KailleraEventListener getListener() {
        return listener;
    }

    @Override
    public KailleraServerImpl getServer() {
        return server;
    }

    @Override
    public KailleraGameImpl getGame() {
        return game;
    }

    protected void setGame(KailleraGameImpl game) {
        this.game = game;

        if (game == null) {
            playerNumber = -1;
        }
    }

    protected void setStatus(int status) {
        this.status = status;
    }

    protected long getLastChatTime() {
        return lastChatTime;
    }

    protected long getLastCreateGameTime() {
        return lastCreateGameTime;
    }

    protected long getLastTimeout() {
        return lastTimeout;
    }

    protected void setLastTimeout() {
        lastTimeout = System.currentTimeMillis();
    }

    @Override
    public int getAccess() {
        return access;
    }

    public String getAccessStr() {
        return AccessManager.ACCESS_NAMES[access];
    }

    protected void setAccess(int access) {
        this.access = access;
    }

    @Override
    public int getPlayerNumber() {
        return playerNumber;
    }

    // protected
    @Override
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    @Override
    public void updateLastActivity() {
        lastActivity = lastKeepAlive = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KailleraUserImpl
                && ((KailleraUserImpl) obj).getID() == getID()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return toString;
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KailleraUserImpl[id=");
        sb.append(getID());
        sb.append(" protocol=");
        sb.append(getProtocol());
        sb.append(" status=");
        sb.append(KailleraUser.STATUS_NAMES[getStatus()]);
        sb.append(" name=");
        sb.append(getName());
        sb.append(" clientType=");
        sb.append(getClientType());
        sb.append(" ping=");
        sb.append(getPing());
        sb.append(" connectionType=");
        sb.append(KailleraUser.CONNECTION_TYPE_NAMES[getConnectionType()]);
        sb.append(" remoteAddress=");
        sb.append((getSocketAddress() == null
            ? EmuUtil.formatSocketAddress(getConnectSocketAddress())
            : EmuUtil.formatSocketAddress(getSocketAddress())));
        sb.append("]");
        return sb.toString();
    }

    @Override
    public void stop() {
        synchronized (this) {
            if (!isRunning) {
                log.debug(this + "  thread stop request ignored: not running!");
                return;
            }

            if (stopFlag) {
                log.debug(this
                        + "  thread stop request ignored: already stopping!");
                return;
            }

            stopFlag = true;

            try {
                Thread.sleep(500);
            }
            catch (Exception e) {
            }

            addEvent(new StopFlagEvent());
        }

        listener.stop();
    }

    @Override
    public synchronized void droppedPacket() {
        if (game != null) {
            // if(game.getStatus() == KailleraGame.STATUS_PLAYING){
            game.droppedPacket(this);

            // }
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    // server actions

    @Override
    public synchronized void login() throws PingTimeException,
            ClientAddressException, ConnectionTypeException, UserNameException,
            LoginException {
        updateLastActivity();
        server.login(this);
    }

    @Override
    public synchronized void chat(String message) throws ChatException,
            FloodException {
        updateLastActivity();
        server.chat(this, message);
        lastChatTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void gameKick(int userID) throws GameKickException {
        updateLastActivity();

        if (game == null) {
            log.warn(this + " kick User " + userID + " failed: Not in a game");
            throw new GameKickException(
                    EmuLang.getString("KailleraUserImpl.KickErrorNotInGame"));
        }

        game.kick(this, userID);
    }

    @Override
    public synchronized KailleraGame createGame(String romName)
            throws CreateGameException, FloodException {
        updateLastActivity();

        if (getStatus() == KailleraUser.STATUS_PLAYING) {
            log.warn(this + " create game failed: User status is Playing!");
            throw new CreateGameException(
                    EmuLang.getString("KailleraUserImpl.CreateGameErrorAlreadyInGame"));
        }
        else if (getStatus() == KailleraUser.STATUS_CONNECTING) {
            log.warn(this + " create game failed: User status is Connecting!");
            throw new CreateGameException(
                    EmuLang.getString("KailleraUserImpl.CreateGameErrorNotFullyConnected"));
        }

        KailleraGame game = server.createGame(this, romName);
        lastCreateGameTime = System.currentTimeMillis();
        return game;
    }

    @Override
    public synchronized void quit(String message) throws QuitException,
            DropGameException, QuitGameException, CloseGameException {
        updateLastActivity();
        server.quit(this, message);
        loggedIn = false;
    }

    @Override
    public synchronized KailleraGame joinGame(int gameID)
            throws JoinGameException {
        updateLastActivity();

        if (game != null) {
            log.warn(this + " join game failed: Already in: " + game);
            throw new JoinGameException(
                    EmuLang.getString("KailleraUserImpl.JoinGameErrorAlreadyInGame"));
        }

        if (getStatus() == KailleraUser.STATUS_PLAYING) {
            log.warn(this + " join game failed: User status is Playing!");
            throw new JoinGameException(
                    EmuLang.getString("KailleraUserImpl.JoinGameErrorAnotherGameRunning"));
        }
        else if (getStatus() == KailleraUser.STATUS_CONNECTING) {
            log.warn(this + " join game failed: User status is Connecting!");
            throw new JoinGameException(
                    EmuLang.getString("KailleraUserImpl.JoinGameErrorNotFullConnected"));
        }

        KailleraGameImpl game = (KailleraGameImpl) server.getGame(gameID);

        if (game == null) {
            log.warn(this
                    + " join game failed: Game " + gameID + " does not exist!");
            throw new JoinGameException(
                    EmuLang.getString("KailleraUserImpl.JoinGameErrorDoesNotExist"));
        }

        // if (connectionType != game.getOwner().getConnectionType())
        // {
        //	log.warn(this + " join game denied: " + this + ": You must use the same connection type as the owner: " + game.getOwner().getConnectionType());
        //	throw new JoinGameException(EmuLang.getString("KailleraGameImpl.StartGameConnectionTypeMismatchInfo"));
        // }

        playerNumber = game.join(this);
        setGame(game);

        gameDataErrorTime = -1;

        return game;
    }

    // game actions
    @Override
    public synchronized void gameChat(String message, int messageID)
            throws GameChatException {
        updateLastActivity();

        if (game == null) {
            log.warn(this + " game chat failed: Not in a game");
            throw new GameChatException(
                    EmuLang.getString("KailleraUserImpl.GameChatErrorNotInGame"));
        }

        if (getMute() == true) {
            log.warn(this + " gamechat denied: Muted: " + message);
            game.announce("You are currently muted!", this);
            return;
        }

        if (server.accessManager.isSilenced(getSocketAddress().getAddress())) {
            log.warn(this + " gamechat denied: Silenced: " + message);
            game.announce("You are currently silenced!", this);
            return;
        }

        game.chat(this, message);
    }

    @Override
    public synchronized void dropGame() throws DropGameException {
        updateLastActivity();

        if (game != null) {
            if (getStatus() == KailleraUser.STATUS_IDLE) {
                return;
            }

            setStatus(KailleraUser.STATUS_IDLE);
            game.drop(this, playerNumber);

            if (p2P == true) {
                game.announce(
                        "Please Relogin, to update your client of missed server activity during P2P!",
                        this);
            }

            p2P = false;
        }
        else {
            log.debug(this + " drop game failed: Not in a game");
        }
    }

    @Override
    public synchronized void quitGame() throws DropGameException,
            QuitGameException, CloseGameException {
        updateLastActivity();

        if (game == null) {
            log.debug(this + " quit game failed: Not in a game");
            // throw new QuitGameException("You are not in a game!");
            return;
        }

        if (status == KailleraUser.STATUS_PLAYING) {
            game.drop(this, playerNumber);
            setStatus(KailleraUser.STATUS_IDLE);
        }

        game.quit(this, playerNumber);

        if (status != KailleraUser.STATUS_IDLE) {
            setStatus(KailleraUser.STATUS_IDLE);
        }

        setMute(false);
        setGame(null);
        addEvent(new UserQuitGameEvent(game, this));
    }

    @Override
    public synchronized void startGame() throws StartGameException {
        updateLastActivity();

        if (game == null) {
            log.warn(this + " start game failed: Not in a game");
            throw new StartGameException(
                    EmuLang.getString("KailleraUserImpl.StartGameErrorNotInGame"));
        }

        game.start(this);
    }

    @Override
    public synchronized void playerReady() throws UserReadyException {
        updateLastActivity();

        if (game == null) {
            log.warn(this + " player ready failed: Not in a game");
            throw new UserReadyException(
                    EmuLang.getString("KailleraUserImpl.PlayerReadyErrorNotInGame"));
        }

        if (playerNumber > game.getPlayerActionQueue().length
                || game.getPlayerActionQueue()[playerNumber - 1].isSynched()) {
            return;
        }

        totalDelay = (game.getDelay() + tempDelay + 5);
        game.ready(this, playerNumber);
    }

    @Override
    public void addGameData(byte[] data) throws GameDataException {
        updateLastActivity();

        try {
            if (game == null) {
                throw new GameDataException(
                        EmuLang.getString("KailleraUserImpl.GameDataErrorNotInGame"), data, getConnectionType(), 1, 1);
            }

            // Initial Delay
            // totalDelay = (game.getDelay() + tempDelay + 5)
            if (frameCount < totalDelay) {
                bytesPerAction = (data.length / connectionType);
                arraySize = (game.getPlayerActionQueue().length
                        * connectionType * bytesPerAction);
                byte[] response = new byte[arraySize];

                for (int i = 0; i < response.length; i++) {
                    response[i] = 0;
                }

                lostInput.add(data);
                addEvent(new GameDataEvent(game, response));

                frameCount++;
            }
            else {
                // lostInput.add(data);
                if (lostInput.size() > 0) {
                    game.addData(this, playerNumber, lostInput.get(0));
                    lostInput.remove(0);
                }
                else {
                    game.addData(this, playerNumber, data);
                }
            }

            gameDataErrorTime = 0;
        }
        catch (GameDataException e) {
            // this should be warn level, but it creates tons of lines in the
            // log
            log.debug(this + " add game data failed: " + e.getMessage());

            // i'm going to reflect the game data packet back at the user to
            // prevent game lockups,
            // but this uses extra bandwidth, so we'll set a counter to prevent
            // people from leaving
            // games running for a long time in this state

            if (gameDataErrorTime > 0) {
                // give the user time to close thegame
                if ((System.currentTimeMillis() - gameDataErrorTime) > 30000) 
                {
                    // this should be warn level, but it creates tons of lines
                    // in the log
                    log.debug(this + ": error game data exceeds drop timeout!");
                    // e.setReflectData(false);
                    throw new GameDataException(e.getMessage());
                }
                else {
                    // e.setReflectData(true);
                    throw e;
                }
            }
            else {
                gameDataErrorTime = System.currentTimeMillis();

                // e.setReflectData(true);
                throw e;
            }
        }
    }

    @Override
    public void addEvent(KailleraEvent event) {
        if (event == null) {
            log.error(this + ": ignoring null event!");
            return;
        }

        if (status != STATUS_IDLE) {
            if (p2P) {
                if (event.toString() == "InfoMessageEvent") {
                    return;
                }
            }
        }

        eventQueue.offer(event);
    }

    @Override
    public void run() {
        isRunning = true;
        log.debug(this + " thread running...");

        try {
            while (!stopFlag) {
                KailleraEvent event = eventQueue.poll(200, TimeUnit.SECONDS);

                if (event == null) {
                    continue;
                }
                else if (event instanceof StopFlagEvent) {
                    break;
                }

                listener.actionPerformed(event);

                if (event instanceof GameStartedEvent) {
                    setStatus(KailleraUser.STATUS_PLAYING);
                }
                else if (event instanceof UserQuitEvent
                        && ((UserQuitEvent) event).getUser().equals(this)) {
                    stop();
                }
            }
        }
        catch (InterruptedException e) {
            log.error(this + " thread interrupted!");
        }
        catch (Throwable e) {
            log.fatal(this + " thread caught unexpected exception!", e);
        }
        finally {
            isRunning = false;
            log.debug(this + " thread exiting...");
        }
    }

    private static class StopFlagEvent implements KailleraEvent {
        @Override
        public String toString() {
            return "StopFlagEvent";
        }
    }
}
