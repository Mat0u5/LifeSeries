package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Mixin(Language.class)
public class LanguageMixin {

	@Inject(method = "loadDefault", at = @At("RETURN"), cancellable = true)
	private static void onServerLanguageLoad(CallbackInfoReturnable<Language> cir) {
		if (LifeSeries.hasClient()) return;
		Language vanilla = cir.getReturnValue();
		Map<String, String> customTranslations = new HashMap<>();

		try (InputStream is = LanguageMixin.class.getResourceAsStream("/resourcepacks/lifeseries/assets/lifeseries/lang/en_us.json")) {
			if (is != null) {
				Language.loadFromJson(is, customTranslations::put);
			}
		} catch (Exception e) {
			System.err.println("[LifeSeries] Failed to load server-side translations!");
			e.printStackTrace();
		}

		cir.setReturnValue(new Language() {
			@Override
			public String getOrDefault(String key, String fallback) {
				if (customTranslations.containsKey(key)) {
					return customTranslations.get(key);
				}
				return vanilla.getOrDefault(key, fallback);
			}

			@Override
			public boolean has(String key) {
				return customTranslations.containsKey(key) || vanilla.has(key);
			}

			@Override
			public boolean isDefaultRightToLeft() {
				return vanilla.isDefaultRightToLeft();
			}

			@Override
			public FormattedCharSequence getVisualOrder(FormattedText text) {
				return vanilla.getVisualOrder(text);
			}
		});
	}
}