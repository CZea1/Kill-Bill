package brigade.killbill.misc;

import java.io.File;
import java.time.Instant;
import java.util.Locale;

import brigade.killbill.KillBillGame;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

public class Presence {
    public KillBillGame game;

	public Core core;

    public Presence(KillBillGame game) {
        this.game = game;

		File discordLibrary;
		try {
			discordLibrary = getDiscordLibrary();
		} catch (Exception e) {
			System.err.println("Failed to download discord SDK. Skipping.");
			return;
		}
		// Initialize the Core
		Core.init(discordLibrary);

        try(CreateParams params = new CreateParams())
		{
			params.setClientID(1100934663654092981L);
			params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			// Create the Core
			try {
				core = new Core(params);
			} catch (Exception e) {
				System.err.println("Core creation failed. Skipping.");
				return;
			}
				// Create the Activity
			try(Activity activity = new Activity())
			{
				activity.setDetails("Having a fun time with Bill");
				activity.setState("Do not interrupt me.");

				// Setting a start time causes an "elapsed" field to appear
				activity.timestamps().setStart(Instant.now());

				// Make a "cool" image show up
				activity.assets().setLargeImage("title1");

				// Finally, update the current activity to our activity
				core.activityManager().updateActivity(activity);
			}
		}
    }

    public static File getDiscordLibrary()
	{
		// Find out which name Discord's library has (.dll for Windows, .so for Linux)
		String name = "discord_game_sdk";
		String suffix;

		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

		if(osName.contains("windows"))
		{
			suffix = ".dll";
		}
		else if(osName.contains("linux"))
		{
			suffix = ".so";
		}
		else if(osName.contains("mac os"))
		{
			suffix = ".dylib";
		}
		else
		{
			throw new RuntimeException("cannot determine OS type: " + osName);
		}

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
		if(arch.equals("amd64"))
			arch = "x86_64";

		// Path of Discord's library inside the ZIP
		return new File("lib/discord_game_sdk/lib/" + arch + "/" + name + suffix);
	}
}
