package net.mabako.minecraft.PistonsBreakBlocksOverWater;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Physics Listener!
 * @author mabako (mabako@gmail.com)
 */
public class PistonsBlockListener extends BlockListener
{
	/** Plugin instance */
	private PistonsPlugin instance;

	/**
	 * Saves the plugin instance
	 * @param instance
	 */
	public PistonsBlockListener( PistonsPlugin instance )
	{
		this.instance = instance;
	}

	/**
	 * Pistons extend, whoo. Needs a check for each block
	 */
	@Override
	public void onBlockPistonExtend( BlockPistonExtendEvent event )
	{
		if( !event.isCancelled( ) )
		{
			for( Block block : event.getBlocks( ) )
			{
				processBlock( block, event.getDirection( ) );
			}
		}
	}
	
	/**
	 * Pistons retract, only sticky pistons matter
	 */
	@Override
	public void onBlockPistonRetract( BlockPistonRetractEvent event )
	{
		if( !event.isCancelled( ) && event.isSticky( ) )
		{
			processBlock( event.getBlock( ).getRelative( event.getDirection( ), 2 ), event.getDirection( ).getOppositeFace( ) );
		}
	}

	public void processBlock( Block block, BlockFace blockFace )
	{
		final Block targetBlock = block.getRelative( blockFace );
		Block below = targetBlock.getRelative( BlockFace.DOWN );
		if( canDropBlock( block ) )
		{
			if( below.getType( ) == Material.WATER || below.getType( ) == Material.STATIONARY_WATER )
			{
				// Save all data
				final Material material = block.getType( );
				final byte data = block.getData( );
				
				// Add a new Thread
				instance.getServer( ).getScheduler( ).scheduleSyncDelayedTask( instance, new Runnable( )
				{
					public void run( )
					{
						// Check if the block is still the same
						// Check via air seems necessary, or it'll corrupt the chunk(?) -> ItemStack with Air won't work -> invalid packet
						
						if( targetBlock.getType( ) != material || targetBlock.getData( ) != data )
						{
							instance.getServer( ).getLogger( ).warning( "PistonsBreakBlocksOverWater: Block expected to be " + material.getId( ) + ":" + data + " but is " + targetBlock.getTypeId( ) + ":" + targetBlock.getData( ) + "." );
							return;
						}
						if( !canDropBlock( targetBlock ) )
							return;
						
						// Turn the block into air
						targetBlock.setType( Material.AIR );

						// Drop the item
						World world = targetBlock.getWorld( );
						world.dropItemNaturally( targetBlock.getLocation( ), new ItemStack( material, 1, (short) 0, data ) );
					}
				}, 10 );
			}
		}
	}
	
	private boolean canDropBlock( Block block )
	{
		switch( block.getType( ) )
		{
			case AIR:
			case LAVA:
			case WATER:
			case PISTON_EXTENSION:
			case PISTON_MOVING_PIECE:
			case STATIONARY_LAVA:
			case STATIONARY_WATER:
				return false;
		}
		return true;
	}
}
