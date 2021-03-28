package xmod;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class XCommand {

	static final Logger LOGGER = LogManager.getLogger();

	static final BlockStateInput DIAMOND_BLOCK = new BlockStateInput(Blocks.DIAMOND_BLOCK.getDefaultState(),
			Collections.emptySet(), null);

	public static void register(CommandDispatcher<CommandSource> dispatcher) {

		LiteralArgumentBuilder<CommandSource> xCommand = Commands.literal("X")
				.requires((commandSource) -> commandSource.hasPermissionLevel(1))
				.executes((context) -> doX(context));

		dispatcher.register(xCommand);

		LiteralArgumentBuilder<CommandSource> gCommand = Commands.literal("G")
				.requires((commandSource) -> commandSource.hasPermissionLevel(1))
				.executes((context) -> doG(context));

		dispatcher.register(gCommand);

	}

	static int doX(CommandContext<CommandSource> ctx) {
		putBlock(ctx, -2, 2, 2);
		putBlock(ctx, -1, 2, 2);
		putBlock(ctx, 0, 2, 2);
		putBlock(ctx, 1, 2, 2);
		putBlock(ctx, 2, 2, 2);
		return 0;
	}

	static int doG(CommandContext<CommandSource> ctx) {
		int goodiesFound = 0;
		for (int x = -100; x <= 100; x++) {
			for (int y = -100; y <=100; y++) {
				for (int z = -100; z <= 100; z++) {
					if (isGoodie(ctx, x, y, z)) {
						goodiesFound++;
					}
				}
			}
		}
		LOGGER.info("Total Goodies found: "+goodiesFound);
		return 0;
	}


	static void putBlock(CommandContext<CommandSource> ctx, int x, int y, int z) {
		try {
			CommandSource source = ctx.getSource();
			ServerWorld serverworld = source.getWorld();
			BlockPos origin = source.asPlayer().getPosition();
			BlockPos bpos = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
			TileEntity tileentity = serverworld.getTileEntity(bpos);
			IClearable.clearObj(tileentity);
			DIAMOND_BLOCK.place(serverworld, bpos, 2);
		} catch (Exception e) {
			LOGGER.warn(e);
		}
	}

	static boolean isGoodie(CommandContext<CommandSource> ctx, int x, int y, int z) {
		
		Block[] goodies = { 
			Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.NETHER_GOLD_ORE,
			Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE,
			Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK,
			Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
			Blocks.REDSTONE_ORE, Blocks.REDSTONE_BLOCK,
			Blocks.OBSIDIAN, 
			Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.TRAPPED_CHEST,
		};

		try {
			CommandSource source = ctx.getSource();
			ServerWorld serverworld = source.getWorld();
			BlockPos origin = source.asPlayer().getPosition();
			BlockPos bpos = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
			TileEntity entity = serverworld.getTileEntity(bpos);
			if (entity == null) return false;
			if (entity.getBlockState() == null) return false;
			if (entity.getBlockState().getBlock() == null) return false;
			Block bxyz = entity.getBlockState().getBlock();
			for (Block gb : goodies) {
				if (bxyz.equals(gb)) {
					LOGGER.info("Found "+bxyz+" at "+bpos);
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.warn(e);
		}
		return false;
	}
}
