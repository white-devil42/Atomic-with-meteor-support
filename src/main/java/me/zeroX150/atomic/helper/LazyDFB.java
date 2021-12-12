/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;

import java.util.concurrent.Executor;

public class LazyDFB extends DataFixerBuilder {

    private static final Executor NO_OP_EXECUTOR = command -> {
    };

    public LazyDFB(int dataVersion) {
        super(dataVersion);
    }

    @Override public DataFixer build(Executor executor) {
        return super.build(NO_OP_EXECUTOR);
    }
}