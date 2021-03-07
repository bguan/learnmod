package xmod;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

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
				.executes((context) -> doCommand(context));

		dispatcher.register(xCommand);
	}

	static int doCommand(CommandContext<CommandSource> ctx) {
		putBlock(ctx, -2, 2, 2);
		putBlock(ctx, -1, 2, 2);
		putBlock(ctx, 0, 2, 2);
		putBlock(ctx, 1, 2, 2);
		putBlock(ctx, 2, 2, 2);
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

}
