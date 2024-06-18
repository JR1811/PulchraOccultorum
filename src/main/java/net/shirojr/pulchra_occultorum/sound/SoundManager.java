package net.shirojr.pulchra_occultorum.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SoundManager {
    private static SoundManager instance;
    private final Map<UUID, List<SoundInstance>> activeSounds = new HashMap<>();

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void play(@NotNull LivingEntity livingEntity, SoundInstance... instances) {
        if (!this.activeSounds.containsKey(livingEntity.getUuid())) {
            this.activeSounds.put(livingEntity.getUuid(), new ArrayList<>());
        }
        List<SoundInstance> activeInstancesForEntity = this.activeSounds.get(livingEntity.getUuid());
        for (SoundInstance soundInstance : instances) {
            if (activeInstancesForEntity.contains(soundInstance)) continue;
            MinecraftClient.getInstance().getSoundManager().play(soundInstance);
            activeInstancesForEntity.add(soundInstance);
        }
    }

    public void stop(@NotNull LivingEntity livingEntity, SoundInstance... instances) {
        List<SoundInstance> entriesForEntity = this.activeSounds.get(livingEntity.getUuid());
        if (entriesForEntity == null) return;
        for (SoundInstance soundInstance : instances) {
            if (!entriesForEntity.contains(soundInstance)) continue;
            MinecraftClient.getInstance().getSoundManager().stop(soundInstance);
            entriesForEntity.remove(soundInstance);
        }
    }

    public void stopAll(@NotNull LivingEntity livingEntity) {
        List<SoundInstance> entriesForEntity = this.activeSounds.get(livingEntity.getUuid());
        for (SoundInstance soundInstance : entriesForEntity) {
            MinecraftClient.getInstance().getSoundManager().stop(soundInstance);
        }
        this.activeSounds.remove(livingEntity.getUuid());
    }
}
