package com.ctrldemi.treeforce.listeners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ctrldemi.treeforce.TreeForce;
import com.ctrldemi.treeforce.files.PlacedBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class BlockBreakEvent implements Listener {

    private final ImmutableList<Material> AXES = ImmutableList.of(
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.NETHERITE_AXE,
            Material.WOODEN_AXE,
            Material.COPPER_AXE
    );

    private final ImmutableList<Material> PICKAXES = ImmutableList.of(
            Material.DIAMOND_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.IRON_PICKAXE,
            Material.STONE_PICKAXE,
            Material.NETHERITE_PICKAXE,
            Material.WOODEN_PICKAXE,
            Material.COPPER_PICKAXE
    );

    private final ImmutableMap<Material, Material> SAPLING_MAP = ImmutableMap.<Material, Material>builder()
            .put(Material.OAK_LOG, Material.OAK_SAPLING)
            .put(Material.SPRUCE_LOG, Material.SPRUCE_SAPLING)
            .put(Material.BIRCH_LOG, Material.BIRCH_SAPLING)
            .put(Material.JUNGLE_LOG, Material.JUNGLE_SAPLING)
            .put(Material.ACACIA_LOG, Material.ACACIA_SAPLING)
            .put(Material.DARK_OAK_LOG, Material.DARK_OAK_SAPLING)
            .put(Material.MANGROVE_LOG, Material.MANGROVE_PROPAGULE)
            .put(Material.CHERRY_LOG, Material.CHERRY_SAPLING)
            .put(Material.PALE_OAK_LOG, Material.PALE_OAK_SAPLING)
            .put(Material.WARPED_STEM, Material.WARPED_FUNGUS)
            .put(Material.CRIMSON_STEM, Material.CRIMSON_FUNGUS)
            .build();

    private final ImmutableList<Material> SOIL_TYPES = ImmutableList.of(
            Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT,
            Material.PODZOL, Material.MOSS_BLOCK, Material.MUD, Material.MUDDY_MANGROVE_ROOTS,
            Material.WARPED_NYLIUM, Material.CRIMSON_NYLIUM
    );

    @EventHandler
    public void onBlockBreakEvent(org.bukkit.event.block.BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        Block b = e.getBlock();

        int blockX = b.getLocation().getBlockX();
        int blockY = b.getLocation().getBlockY();
        int blockZ = b.getLocation().getBlockZ();

        String path = blockX + "," + blockY + "," + blockZ;

        boolean isPlayerPlacedBlock = PlacedBlocks.get().isInt(path);

        if (
            (
                (
                    AXES.contains(itemInHand.getType()) && TreeForce.LOGS.contains(b.getType())
                ) || (
                    PICKAXES.contains(itemInHand.getType()) && TreeForce.ORES.contains(b.getType())
                )
            ) && !isPlayerPlacedBlock && p.isSneaking()
        ) {
            Material logType = b.getType();
            Location loc = b.getLocation().clone();

            this.checkNearbyBlocks(b, p, e);

            if (SAPLING_MAP.containsKey(logType)) {
                replantSapling(logType, loc);
            }

        } else if (isPlayerPlacedBlock && TreeForce.LOGS.contains(b.getType())) {
            PlacedBlocks.get().set(path, null);
        }
    }

    private void replantSapling(Material logType, Location loc) {
        Material saplingType = SAPLING_MAP.get(logType);
        
        Bukkit.getScheduler().runTaskLater(TreeForce.getPlugin(), () -> {
            Block targetBlock = loc.getBlock();
            Block soil = targetBlock.getRelative(BlockFace.DOWN);

            if (targetBlock.getType() == Material.AIR && SOIL_TYPES.contains(soil.getType())) {
                targetBlock.setType(saplingType);
            }
        }, 2L);
    }

    public void checkNearbyBlocks(Block block, Player player, org.bukkit.event.block.BlockBreakEvent event) {
        int x = -1;
        int y = -1;
        int z = -1;
        Random rand = new Random();
        outerLoop:
        for (int k = 0; k < 3; k++) {

            for (int i = 0; i < 3; i++) {

                for (int j = 0; j < 3; j++) {
                    Location center = block.getLocation().clone();

                    center.add(x, y, z);

                    Block nearbyBlock = center.getBlock();

                    if (TreeForce.LOGS.contains(nearbyBlock.getType()) || TreeForce.ORES.contains(nearbyBlock.getType())) {
                        ItemStack tool = player.getInventory().getItemInMainHand();

                        nearbyBlock.breakNaturally(tool, true);

                        switch (nearbyBlock.getType()) {
                            case COAL_ORE:
                                event.setExpToDrop(rand.nextInt(0,2));
                                break;
                            case NETHER_GOLD_ORE:
                                event.setExpToDrop(rand.nextInt(0,1));
                                break;
                            case DIAMOND_ORE:
                            case DEEPSLATE_DIAMOND_ORE:
                            case EMERALD_ORE:
                            case DEEPSLATE_EMERALD_ORE:
                                event.setExpToDrop(rand.nextInt(3,7));
                                break;
                            case LAPIS_ORE:
                            case DEEPSLATE_LAPIS_ORE:
                            case NETHER_QUARTZ_ORE:
                                event.setExpToDrop(rand.nextInt(2,5));
                                break;
                            case REDSTONE_ORE:
                            case DEEPSLATE_REDSTONE_ORE:
                                event.setExpToDrop(rand.nextInt(1,5));
                                break;
                            default:
                                break;
                        }

                        int xpAmount = event.getExpToDrop();
                        if(xpAmount > 0) nearbyBlock.getWorld().spawn(nearbyBlock.getLocation(), ExperienceOrb.class).setExperience(xpAmount);

                        int toolDurability = tool.getType().getMaxDurability();

                        ItemMeta meta = tool.getItemMeta();

                        Damageable dmg = (Damageable) meta;

                        if (dmg != null && dmg.getDamage() <= toolDurability) {
                            if (meta.hasEnchant(Enchantment.UNBREAKING)) {
                                int unbLevel = meta.getEnchantLevel(Enchantment.UNBREAKING);
                                double chanceOfDmg = (double) 1 /(unbLevel+1);

                                if (rand.nextDouble() < chanceOfDmg) {
                                    dmg.setDamage(dmg.getDamage() + 1);
                                }
                            } else {
                                dmg.setDamage(dmg.getDamage() + 1);
                            }

                            tool.setItemMeta(dmg);

                            player.getInventory().setItemInMainHand(tool);
                        } else {
                            player.getInventory().setItemInMainHand(null);
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                            break outerLoop;
                        }

                        this.checkNearbyBlocks(nearbyBlock, player, event);
                    }
                    z++;
                }
                x++;
                z = -1;
            }
            y++;
            x = -1;
            z = -1;
        }
    }
}