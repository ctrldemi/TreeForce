package com.ctrldemi.treeforce.listeners;

import com.ctrldemi.treeforce.TreeForce;
import com.ctrldemi.treeforce.files.PlacedBlocks;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockPlaceEvent implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(org.bukkit.event.block.BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (TreeForce.LOGS.contains(block.getType()) || TreeForce.ORES.contains(block.getType())) {
            String path = block.getX() + "," + block.getY() + "," + block.getZ();
            PlacedBlocks.get().set(path, 1);
            PlacedBlocks.save();
        }
    }
}