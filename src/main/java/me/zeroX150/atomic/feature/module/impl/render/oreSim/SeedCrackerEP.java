/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render.oreSim;

import kaptainwutax.seedcrackerX.api.SeedCrackerAPI;
import me.zeroX150.atomic.feature.module.ModuleRegistry;

public class SeedCrackerEP implements SeedCrackerAPI {

    @Override public void pushWorldSeed(long seed) {
        OreSim oreSim = ModuleRegistry.getByClass(OreSim.class);
        oreSim.config.get("Seed").setValue(String.valueOf(seed));
    }
}
