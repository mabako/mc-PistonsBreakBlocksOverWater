package net.mabako.minecraft.PistonsBreakBlocksOverWater;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
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
	 * The main part - where the blocks are destroyed and items dropped
	 */
	@Override
	public void onBlockPhysics( BlockPhysicsEvent event )
	{
		if( event.isCancelled( ) )
			return;
		
		Block block = event.getBlock( );
		if( block.getType( ) == Material.PISTON_STICKY_BASE || block.getType( ) == Material.PISTON_BASE )
		{
			// This is for the piston withdrawing
			BlockFace face;
			switch( block.getData( ) )
			{
				case 0:
					face = BlockFace.DOWN;
					break;
				case 1:
					face = BlockFace.UP;
					break;
				case 2:
					face = BlockFace.EAST;
					break;
				case 3:
					face = BlockFace.WEST;
					break;
				case 4:
					face = BlockFace.NORTH;
					break;
				case 5:
					face = BlockFace.SOUTH;
					break;
				default:
					return;
			}

			block = block.getFace( face );
		}
		else if( event.getChangedType( ) == Material.PISTON_EXTENSION )
		{
			// Piston extending, we can use the block given by the event
		}
		else
			return;

		Block below = block.getFace( BlockFace.DOWN );
		if( below.getType( ) == Material.WATER || below.getType( ) == Material.STATIONARY_WATER )
		{
			// Save all data
			final Location location = block.getLocation( );
			final Material material = block.getType( );
			final byte data = block.getData( );
			
			// Add a new Thread
			instance.getServer( ).getScheduler( ).scheduleSyncDelayedTask( instance, new Runnable( )
			{
				public void run( )
				{
					// Check if the block is still the same
					// Check via air seems necessary, or it'll corrupt the chunk(?) -> ItemStack with Air won't work -> invalid packet
					Block blocky = location.getBlock( );
					if( blocky.getType( ) != material || blocky.getData( ) != data )
						return;
					switch( blocky.getType( ) )
					{
						case AIR:
						case LAVA:
						case WATER:
						case STATIONARY_LAVA:
						case STATIONARY_WATER:
							return;
					}
					
					// Turn the block into air
					blocky.setType( Material.AIR );

					// Drop the item
					World world = location.getWorld( );
					world.dropItemNaturally( location, new ItemStack( material, 1, (short) 0, data ) );
				}
			}, 1 );
		}
	}
}
