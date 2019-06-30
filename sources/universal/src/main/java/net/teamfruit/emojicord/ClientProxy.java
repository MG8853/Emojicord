package net.teamfruit.emojicord;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;

public class ClientProxy extends CommonProxy {
	public static final Minecraft MC = CompatMinecraft.getMinecraft();

	//public static final Map<String, String> EMOJI_NAME_MAP = Maps.newHashMap();
	boolean error = false;

	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		Log.log = event.getModLog();
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		if (EmojiEndpoint.loadGateway())
			EmojiEndpoint.loadStandardEmojis();

		if (!this.error)
			MC.fontRenderer = new EmojiFontRenderer(MC);
	}

	@Override
	public void postInit(final @Nonnull CompatFMLPostInitializationEvent event) {
		super.postInit(event);

		//MinecraftForge.EVENT_BUS.register(new UTFSendTest());
	}
}