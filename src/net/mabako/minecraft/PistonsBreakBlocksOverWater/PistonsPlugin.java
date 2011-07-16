package net.mabako.minecraft.PistonsBreakBlocksOverWater;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin base
 * @author mabako (mabako@gmail.com)
 */
public class PistonsPlugin extends JavaPlugin
{
	/** Our blocklistener for events */
	private BlockListener blockListener;

	/**
	 * Custom enabling event, registers event and prints a message
	 */
	@Override
	public void onEnable( )
	{
		// Create the block listener
		blockListener = new PistonsBlockListener( this );

		// Register all relevant events
		PluginManager pm = getServer( ).getPluginManager( );

		pm.registerEvent( Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this );

		// Output a message
		PluginDescriptionFile pdfFile = this.getDescription( );
		System.out.println( pdfFile.getName( ) + " version " + pdfFile.getVersion( ) );
	}

	@Override
	public void onDisable( )
	{
	}
}
