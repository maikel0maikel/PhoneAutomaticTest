package com.sinohb.hardware.test.module.audoex;

import com.sinohb.hardware.test.entities.AmplifierEntity;

public interface EffectManagerable {


    int playNormal();

    int playEffect();

    int closeEffect();

    int playAmplifier(AmplifierEntity entity);

    void destroy();

}
