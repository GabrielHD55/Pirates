package com.gabrielhd.pirates.Arena;

import org.bukkit.block.*;
import java.util.*;
import org.bukkit.*;
import com.google.common.collect.*;

public class BlockTracker {

    private final LinkedList<BlockState> changeTrackers;
    private final Set<Location> playerPlacedBlocks;
    
    public BlockTracker() {
        this.changeTrackers = Lists.newLinkedList();
        this.playerPlacedBlocks = Sets.newHashSet();
    }
    
    public synchronized void rollback() {
        BlockState blockState;
        while ((blockState = this.changeTrackers.pollLast()) != null) {
            blockState.update(true, false);
        }
        this.playerPlacedBlocks.clear();
    }
    
    public void add(BlockState blockState) {
        if (!this.changeTrackers.contains(blockState)) {
            this.changeTrackers.add(blockState);
        }
    }
}
