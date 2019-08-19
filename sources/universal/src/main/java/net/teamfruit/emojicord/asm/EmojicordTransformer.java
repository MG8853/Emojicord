package net.teamfruit.emojicord.asm;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.asm.lib.VisitorHelper.TransformProvider;

public class EmojicordTransformer implements IClassTransformer {
	private boolean intelliinputloaded;
	private boolean intelliinputinitialized;

	private void initIntelliInput(final String name, final String transformedName) {
		if (!this.intelliinputloaded) {
			if (StringUtils.equals(transformedName, "$wrapper.com.tsoft_web.IntelliInput.asm.IntelliInputTransformer"))
				this.intelliinputloaded = true;
		} else if (!this.intelliinputinitialized) {
			this.intelliinputinitialized = true;
			try {
				final Field $transformers = LaunchClassLoader.class.getDeclaredField("transformers");
				$transformers.setAccessible(true);
				@SuppressWarnings("unchecked")
				final List<IClassTransformer> transformers = (List<IClassTransformer>) $transformers.get(Launch.classLoader);
				int signpic = -1, intelliinput = -1;
				for (final ListIterator<IClassTransformer> itr = transformers.listIterator(); itr.hasNext();) {
					final int index = itr.nextIndex();
					final IClassTransformer transformer = itr.next();
					final String tname = transformer.getClass().getName();
					if (StringUtils.equals(tname, "$wrapper."+EmojicordTransformer.class.getName()))
						signpic = index;
					else if (StringUtils.equals(tname, "$wrapper.com.tsoft_web.IntelliInput.asm.IntelliInputTransformer"))
						intelliinput = index;
				}
				if (signpic>=0&&intelliinput>=0&&intelliinput>signpic) {
					Collections.swap(transformers, signpic, intelliinput);
					Log.log.info("The order of EmojicordTransformer and IntelliInputTransformer has been swapped while loading "+transformedName);
				}
			} catch (final Exception e) {
				Log.log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public @Nullable byte[] transform(final @Nullable String name, final @Nullable String transformedName, final @Nullable byte[] bytes) {
		if (bytes==null||name==null||transformedName==null)
			return bytes;

		initIntelliInput(name, transformedName);

		try {
			if (transformedName.equals("net.minecraft.client.gui.GuiTextField"))
				return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
					@Override
					public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
						Log.log.info(String.format("Patching GuiTextField (class: %s)", name));
						return new GuiTextFieldVisitor(name, cv);
					}
				});
		} catch (final Exception e) {
			Log.log.fatal("Could not transform: ", e);
		}

		return bytes;
	}
}