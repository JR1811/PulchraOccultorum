package net.shirojr.pulchra_occultorum.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.shirojr.pulchra_occultorum.util.SoundOrigin;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractDynamicSoundInstance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, List<SoundInstance>> activeSounds = new HashMap<>();

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void play(@NotNull SoundOrigin origin, SoundInstance... instances) {
        if (!this.activeSounds.containsKey(origin.getUniqueId())) {
            this.activeSounds.put(origin.getUniqueId(), new ArrayList<>());
        }
        List<SoundInstance> activeInstancesForEntity = this.activeSounds.get(origin.getUniqueId());
        for (SoundInstance soundInstance : instances) {
            if (activeInstancesForEntity.contains(soundInstance)) continue;
            MinecraftClient.getInstance().getSoundManager().play(soundInstance);
            activeInstancesForEntity.add(soundInstance);
        }
    }

    public void stop(@NotNull SoundOrigin origin, SoundInstance... instances) {
        List<SoundInstance> entriesForEntity = this.activeSounds.get(origin.getUniqueId());
        if (entriesForEntity == null) return;
        for (SoundInstance soundInstance : instances) {
            if (!entriesForEntity.contains(soundInstance)) continue;
            if (soundInstance instanceof AbstractDynamicSoundInstance<?> dynamicSoundInstance)
                dynamicSoundInstance.finishSoundInstance();
            else MinecraftClient.getInstance().getSoundManager().stop(soundInstance);

            this.activeSounds.get(origin.getUniqueId()).remove(soundInstance);
        }
    }

    public void stopAll(@NotNull SoundOrigin origin) {
        List<SoundInstance> entriesForEntity = this.activeSounds.get(origin.getUniqueId());
        for (SoundInstance soundInstance : entriesForEntity) {
            if (soundInstance instanceof AbstractDynamicSoundInstance<?> dynamicSoundInstance) {
                dynamicSoundInstance.finishSoundInstance();
            } else {
                MinecraftClient.getInstance().getSoundManager().stop(soundInstance);
            }
        }
        this.activeSounds.remove(origin.getUniqueId());
    }
}
