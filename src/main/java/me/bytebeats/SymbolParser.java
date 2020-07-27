package me.bytebeats;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SymbolParser {
    List<String> parse();

    @Nullable
    String raw();

    String prefix();//对应于不同市场, 例如us, hk, sh, sz
}
