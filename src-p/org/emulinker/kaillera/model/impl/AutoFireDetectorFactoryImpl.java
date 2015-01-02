package org.emulinker.kaillera.model.impl;

import org.emulinker.kaillera.model.*;

public class AutoFireDetectorFactoryImpl implements AutoFireDetectorFactory {
    @Override
    public AutoFireDetector getInstance(KailleraGame game,
            int defaultSensitivity) {
        return new AutoFireScanner2(game, defaultSensitivity);
    }
}
