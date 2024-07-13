# What is this mod?

This Fabric mod was part of the [Modfest Carnival event](https://modfest.net/carnival) for Minecraft 1.21.

## What does it contain?

Many interesting features are in this mod. But even more were planned. 
Due to a short development phase, this mod contained not as many features on launch, as I would have liked it to have.

Right now this mod includes...

- Blocks for safe landing
  - Elastic Sand (bounces entities a little and reduces fall damage)
  - Safety Net (bounces entities back a lot and completely removes fall damage)
- Unicycle (Item and Entity)
  - When riding this entity, two new icons will show up near your crosshair. The left paddle symbolizes your key to walk left (A) and the right paddle symbolizes your key to walk right (D). Similar to the alternating icons you will have to switch between those two keys periodically to go forward. Pressing the opposite key will drive the Unicycle backwards.
- Decorative but functional blocks
  - Spotlight Lamp Block
    - Emits light cone based on incoming redstone power from the block below
    - Spotlight lamp blocks can read redstone power from even further below using the new `pulchra-occultorum:sends_update_power_vertically` block tag
    - Has a custom UI where you can control rotation and the speed of its rotation
  - Flag Pole block / Flag Pole Base block
    - Flag structure can hoist any banner of your choice to display that in the air
    - The new Block tag `pulchra-occultorum:supports_flag_pole` defines blocks, which can support flag pole blocks on top for decoration. Keep in mind that only the original Flag Pole **Base** block can equip banners.
    - Depending on the received redstone power from the Flag Pole Base block or the block below, the height of the banner can be adjusted
  - Whip item, which sends passive mobs into a frenzy. When riding a horse, this item will give it a good boost. Upon usage, the rearing horse state, where they stand up and don't want to move, will cancel too.
- Tarot System
  - This is a new system, which can predict "occult events" in the world. It includes a new Registry type, which makes it easy, to add new functionality using this mod's API features.
    - Tarot Card items can be observed to gain advantages, punishments, warnings and other things
    - Occult Events usually have a predefined time (World time), can repeat or will only activate once, if their corresponding tarot card has been observed
    - The Monolith block and its Tarot card are an example of that. If the event is currently active in the world, the Tarot card can be observed. If that happens, this Monolith structure will spawn nearby and more hostile mobs spawn, to damage this block. Keep it safe to get a reward.
    - The Warlord Tarot card will buff the observing player for a whole day, but more hostile mobs spawn around them.

# What are the issues and what does the future hold?

Currently, this mod is by no means complete. Here is a list of issues which may happen and what was planned originally.

## Issues and unexpected behaviours

- Unicycle movement is unforgiving for beginners and may have unexpected velocity changes underwater or when rolling down small slopes
- Whip Item applies passive mob frenzy not as planned and turns the item around in the hand wrongly when used
- Spotlight lamp block screen doesn't save handle position. So when reopening the screen, the new rotation might not be as expected
- Spotlight lamp block doesn't keep rotating if the screen is closed
- ...

## Originally Planned content

- Safety Net Block had coloured variants (like wool), slab variants and stair variants planned
- Tarot and Occult Event system is only really implemented in the back end without much content yet. At least four more cards with events were planned
- Spotlight lamp block's light rays were supposed to be colourable using the screen
- Spotlight lamp block's light ray length and power
- A microphone item with `Simple Voice Chat API` integration. Using this item would've muted/lowered the volume of everyone in a radius around the user
- A new set of blocks for a Greenhouse feature was planned, which, when used for an enclosed space and sunlight above, increased the temperature and/or humidity of that space, to allow for faster plant growth and even allowed new more exotic plants to survive
- A feature where two players can hold a third player above them, like an acrobatic pyramid

And many more...
  
## Future plans

The development of that mod was very rushed and didn't go very smoothly, so I burnt out on that mod a little bit.
I can add and fix all the features, which are mentioned above, but right now I feel more like working on other Fabric mod projects, like [Boatism](https://github.com/JR1811/Boatism), [Archaic Weaponry](https://github.com/JR1811/archaic-weaponry), some other new, not yet public, mods and the [official Fabric docs](https://github.com/FabricMC/fabric-docs) site.

If I see people being interested in the ideas and the content of this mod, I might change my mind, so tell me what you think and if you are interested in seeing more development being made on `Pulchra Occultorum` in the [Issues](https://github.com/JR1811/PulchraOccultorum/issues) site here on GitHub.

That's all for now,
Thank you!
