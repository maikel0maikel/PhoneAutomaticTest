package com.sinohb.hardware.test.module.audoex;

import com.sinohb.hardware.test.entities.AmplifierEntity;

public interface EffectManagerable {
     int EQ_MODE_NORMAL = 0;
     int EQ_MODE_CLASSIC = 2;
     int EQ_MODE_JAZZ = 3;
     int EQ_MODE_POP = 4;
     int EQ_MODE_FOLK = 7;
     int EQ_MODE_ROCK = 8;

    int playEffect(int effect);

    int closeEffect();

    int playAmplifier(AmplifierEntity entity);

    void destroy();

    int stop();

}
