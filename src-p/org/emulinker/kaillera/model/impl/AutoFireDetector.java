package org.emulinker.kaillera.model.impl;

import org.emulinker.kaillera.model.*;

public interface AutoFireDetector {
    public void start(int numPlayers);

    public void addPlayer(KailleraUser user, int playerNumber);

    public void addData(int playerNumber, byte[] data, int bytesPerAction);

    public void stop(int playerNumber);

    public void stop();

    public void setSensitivity(int sensivitiy);

    public int getSensitivity();
}
