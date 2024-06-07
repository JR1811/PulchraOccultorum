package net.shirojr.pulchra_occultorum.compat.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;

public class SimpleVoiceChatCompat implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return PulchraOccultorum.MOD_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        VoicechatPlugin.super.registerEvents(registration);
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
    }
}
