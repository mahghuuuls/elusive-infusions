This mod adds infusions for [Everfilling Flasks](https://www.curseforge.com/minecraft/mc-mods/everfilling-flasks), crafted from the plants of [Elusive Flora](https://www.curseforge.com/minecraft/mc-mods/elusive-flora). An infusion sits in a flask's infusion grid and works while it is there. Effect infusions give a short potion effect after each completed drink. Modifier infusions change the flask itself: more healing, a faster drink, a drink that is harder to interrupt, a faster recharge, or stronger effects. Most modifier infusions pay for their bonus with a penalty. Most infusions also have a Concentrated version, crafted from the standard one and more plants. It is stronger and costs more potency.

<span style="color:#d6a100">**AI usage disclaimer:** This mod was developed with AI-agent assistance using [this agent workflow](https://github.com/mahghuuuls/minecraft-1.12.2-mod-agent-workflow). The project owner reviewed the work during development.</span>

- Craft a Blank Infusor from iron bars and four iron nuggets. Craft it with picked plants to make an infusion. The plants are found and picked in the world as Elusive Flora describes; this mod adds no plants.
- Put the infusion in the infusion grid of the flask screen. It stays there and is not used up. Each infusion costs potency, and a flask has a limited potency budget. The cost is on the tooltip.
- Unique: one infusion of that family per flask. Stackable: it can be placed several times, and the copies add up.
- A Concentrated infusion is crafted from the standard infusion plus the same plants again. It never shares a flask with its standard version.
- Effect infusions last longer with the flask's effect power bonus, up to twice as long by default. Effect power never raises the level.
- Every infusion has an entry with its recipe in the flask journal.

**Effect infusions**

Unique. The effect comes after a completed drink. Concentrated versions give level II, or a longer effect where level II would be too much.

- Deepwater: Water Breathing I for 30 s, 60 s Concentrated. Pearlfrond and Tideheart.
- Ashen: Fire Resistance I for 20 s, 45 s Concentrated. Ashen Lotus and Emberroot.
- Sunspire: Speed I for 10 s, Speed II Concentrated. Sunspire and Skyclover.
- Gravebell: Resistance I for 8 s, Resistance II Concentrated. Gravebell and Mournlily.
- Winterthorn: Absorption I for 15 s, Absorption II Concentrated. Winterthorn and Frostbell.
- Ledgeleap: Jump Boost I for 30 s, Jump Boost II Concentrated. Ledgebloom and Skyclover.
- Blazewart: Strength I for 8 s, Strength II Concentrated. Blazecap and Wartvine.

**Stackable modifier infusions**

Small bonuses with no penalty. Two plants of each kind. No Concentrated version.

- Tideheart: +10% healing. Tideheart and Frostbell.
- Duskwisp: +20% drink speed. Duskwisp and Streamreed.
- Frostbell: +40% hit resistance. Frostbell and Duskwisp.
- Thawbud: +8% recharge speed. Thawbud and Rainthistle.
- Starpetal: +15% effect power. Starpetal and Voidbloom.

**Trade-off infusions**

Unique. A large bonus with a penalty. Concentrated versions are stronger on both sides.

- Quickwater: +50% drink speed, -20% healing. Streamreed and Rainthistle.
- Heartroot: +35% healing, -30% drink speed. Dunerose and Amberleaf.
- Stillwater: +80% hit resistance, -25% drink speed. Mournlily and Pearlfrond.
- Prismheart: +50% effect power, -25% healing. Prismthorn and Shadelace.
- Soulwick: +30% recharge speed, -30% drink speed. Soulwick and Ashen Lotus.
- Moonveil: +50% healing, -25% recharge speed. Moonveil, Umbrabud, and Duskwisp.
- Sporecap: +60% drink speed, -20% recharge speed. Sporecap and Duskwisp.
- Cinder: +30% recharge speed, -25% healing. Emberroot and Dunerose.
- Deepglow: +50% effect power, -25% recharge speed. Deepglow and Sporelantern.
- Rootbound: +80% hit resistance, -20% recharge speed. Canopy Tear and Mournlily.

**With [Potion Core](https://www.curseforge.com/minecraft/mc-mods/potion-core)**

- Canopy: Climb I for 30 s, 60 s Concentrated. Canopy Tear and Ledgebloom.
- Cloudfern: Slow Fall I for 30 s, 60 s Concentrated. Cloudfern and Skyclover.
- Ironbark: Iron Skin I for 10 s, Iron Skin II Concentrated. Amberleaf and Winterthorn.
- Diamondbark: Diamond Skin I for 10 s, Diamond Skin II Concentrated. Prismthorn and Marrowbloom.

**With [Oceanic Expanse](https://www.curseforge.com/minecraft/mc-mods/oceanic-expanse)**

- Riverstride: Dolphin's Grace I for 30 s, 60 s Concentrated. Streamreed and Tideheart.

**With [JAWMS Additions](https://www.curseforge.com/minecraft/mc-mods/jawms-additions)**

- Manabloom: Mana Expansion I for 30 s, Mana Expansion II Concentrated. Voidbloom and Umbrabud.

**With [Electroblob's Wizardry](https://www.curseforge.com/minecraft/mc-mods/electroblobs-wizardry)**

- Wellspring: Font of Mana I for 15 s, Font of Mana II Concentrated. Duskwisp and Rainthistle.

Without one of these four mods, its infusions do not exist in the game.

**Plants from other mods**

Some Elusive Flora plants grow only in biomes other mods add. Without those mods the infusion exists, but its plants cannot be found: Blazewart and Diamondbark need Nether biomes from Biomes O' Plenty, BetterNether, or Nether Backport; Diamondbark, Prismheart, Deepglow, and Starpetal need End biomes from Better End Forge, Stygian End, or Environs; Cloudfern needs [the Aether](https://www.curseforge.com/minecraft/mc-mods/aether-legacy).

**For modpack creators**

`config/elusiveinfusions.cfg` has every infusion's potency cost and values, an on/off switch for every recipe, and the cap on how far effect power lengthens an effect. Changes take effect on the next game start. Use the same file on the server and the clients, because the server's values decide and the tooltips show the client's.

**Install**

Needed on the server and on every client. Requires Everfilling Flasks 1.2.0 or newer and Elusive Flora 1.0.0 or newer.

Source: [github.com/mahghuuuls/elusive-infusions](https://github.com/mahghuuuls/elusive-infusions)
