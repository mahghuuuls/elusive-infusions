# Changelog

All notable changes to Elusive Infusions. The format follows Keep a Changelog; versions follow Semantic Versioning.

## [1.0.0] - 2026-10-03

First release.

### Added

- The Blank Infusor, crafted from iron bars and four iron nuggets, used up when crafting a standard infusion.
- Effect infusions, Unique, giving a potion effect after a completed drink: Deepwater (Water Breathing), Ashen (Fire Resistance), Sunspire (Speed), Gravebell (Resistance), Winterthorn (Absorption), Ledgeleap (Jump Boost), Blazewart (Strength). Each has a Concentrated version with level II or a longer effect.
- Stackable modifier infusions, copies adding up: Tideheart (healing), Duskwisp (drink speed), Frostbell (hit resistance), Thawbud (recharge speed), Starpetal (effect power).
- Trade-off infusions, Unique, a large bonus with a penalty, each with a stronger Concentrated version: Quickwater, Heartroot, Stillwater, Prismheart, Soulwick, Moonveil, Sporecap, Cinder, Deepglow, Rootbound.
- Infusions for optional mods, present only when the mod is installed: Canopy, Cloudfern, Ironbark, Diamondbark (Potion Core), Riverstride (Oceanic Expanse), Manabloom (JAWMS Additions), Wellspring (Electroblob's Wizardry).
- Rules: a Unique family allows one piece per flask; a standard and a Concentrated piece of one family never share a flask; effect infusions last longer with effect power, capped at twice the base duration by default, and never gain a level; repeated drinks follow the vanilla refresh rule.
- Tooltips written by the parent (cost and effect or bonuses) plus the Unique, Stackable, and effect power lines; a journal entry per infusion and for the Blank Infusor.
- One shared infuser look tinted by family color; Concentrated infusions carry the enchantment glint.
- Config `config/elusiveinfusions.cfg`: `effectPowerMaximum`; per item `cost` and either `level` and `seconds` or the bonus fractions; an on/off switch per recipe.

### Compatibility

- Requires Everfilling Flasks 1.2.0 or newer and Elusive Flora 1.0.0 or newer on client and server (minimums; built and tested against those versions).
- Optional Potion Core 1.9, Oceanic Expanse 1.2.2, JAWMS 1.1.0 with JAWMS Additions 1.1.0, Electroblob's Wizardry 4.3.19 (tested). With a mod present but one of its effects switched off, the item stays without a recipe and says so on its tooltip.
- Six families need plants that grow only with other biome mods or the Aether installed (see the mod page).
- Dedicated servers: the mod is needed on both sides; tested on a Forge 14.23.5.2847 server.

### Known Issues

- Configuration is not sent to clients. Gameplay follows the server's file; tooltips show the client's. Ship the same file to both.
