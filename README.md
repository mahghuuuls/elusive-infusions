# Elusive Infusions

A Minecraft 1.12.2 Forge mod that adds infusions for Everfilling Flasks, crafted from Elusive Flora plants: effect infusions that give a short potion effect after a completed drink, modifier infusions that change the flask's healing, drink speed, hit resistance, recharge speed, or effect power, most of them with a penalty, and stronger Concentrated versions. Seven families need an optional effect mod and exist only when it is installed.

Player-facing description: [MOD-PAGE.md](MOD-PAGE.md). Release history: [CHANGELOG.md](CHANGELOG.md). License: [MIT](LICENSE). Build template notice: [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).

## Install side

- Mod id `elusiveinfusions`, version 1.0.0. Needed on the server and on every client.
- Requires [Everfilling Flasks](https://www.curseforge.com/minecraft/mc-mods/everfilling-flasks) 1.2.0 or newer and [Elusive Flora](https://www.curseforge.com/minecraft/mc-mods/elusive-flora) 1.0.0 or newer (minimums, no upper bounds; built and tested against 1.2.0 and 1.0.0). Their own dependencies, Inventory Button Bar and Patchouli ROFL Edition, come with them.
- Optional: [Potion Core](https://www.curseforge.com/minecraft/mc-mods/potion-core) (Canopy, Cloudfern, Ironbark, Diamondbark), [Oceanic Expanse](https://www.curseforge.com/minecraft/mc-mods/oceanic-expanse) (Riverstride), [JAWMS Additions](https://www.curseforge.com/minecraft/mc-mods/jawms-additions) (Manabloom), [Electroblob's Wizardry](https://www.curseforge.com/minecraft/mc-mods/electroblobs-wizardry) (Wellspring). Each is checked by mod id at item registration; without the mod, its items, recipes, and journal entries do not exist. With the mod but with the effect removed from its registry (Potion Core lets a pack switch effects off), the item stays, loses its recipe, and says so on its tooltip; one warning is logged.
- Gameplay values come from the server's configuration. Tooltips read the client's file, so a pack ships the same file to both.

## Infusion roster

`src/main/resources/assets/elusiveinfusions/infusion-roster.csv` is the single source of truth for every infusion family: id, display name, kind (effect or modifier), unique or stackable, the optional mod it needs, the effect id, and for each tier the plants, the potency cost, the bonuses or the effect level and seconds, and the family color. Items, recipes, config defaults, tooltips, journal entries, and the item tint all derive from it. The same file is kept in the design documentation of the workflow that produced this mod.

Rules the roster does not spell out:

- Unique families allow one piece per flask; a standard and a Concentrated piece of one family never share a flask. Both refusals come from the parent's conflict hook, so the flask screen explains them.
- Effect infusions apply `PotionEffect(effect, seconds x 20 x effect power, level - 1)` after a completed drink, with effect power capped by `effectPowerMaximum`; the vanilla refresh rule applies, so a repeated drink never adds durations together.
- Modifier bonuses are plain fractions added to the parent's bonus sums; the parent applies them.

## Configuration

`config/elusiveinfusions.cfg`, written with comments on first start. All options need a game restart.

| Key | Scope | Default | Meaning |
| --- | --- | --- | --- |
| `effectPowerMaximum` | general | 2.0 | Largest effect power multiplier applied to an effect infusion's duration (1.0 to 10.0) |
| `cost` | per infusion item | roster | Potency used while the piece is in a flask (0 to 100) |
| `level`, `seconds` | per effect infusion item | roster | Effect level (1 is I) and base duration in seconds before effect power (1 to 10; 1 to 3600) |
| `healing`, `drink_speed`, `hit_resistance`, `recharge_speed`, `effect_power` | per modifier infusion item | roster | Fractions added to the flask's bonus: 0.35 is +35%, -0.30 is -30% (-0.95 to 10.0); only the channels the roster gives that item appear |
| `<item>` | recipes | true | `false` removes the recipe that makes the item |

A value outside its range is clamped and logged once; the file is left as the pack maker wrote it. Text that is not a number falls back to the roster default with the same warning.

## Scope boundaries

- No plants, no world generation, no farming: Elusive Flora owns the plants and where they grow. Six families need plants that grow only with other biome mods or the Aether installed; the mod page lists them.
- No new flask screen, slot, journal, or flask liquid color; the parent owns those.
- No configuration sync to clients; no recipe changes through configuration other than on and off.
- Effect power lengthens effects only; it never raises a level.
- Cleanroom is not tested by this project.

## Development

Standard Forge 1.12.2 project on the CleanroomMC ForgeDevEnv template: `./gradlew build` produces `build/libs/elusiveinfusions-1.0.0.jar`; `./gradlew runClient` and `./gradlew runServer` start the development runtimes. Everfilling Flasks comes from CurseMaven at build time; the Elusive Flora development jar must be dropped into `run/mods` to run the game. Unit tests run without Minecraft and cover the roster grammar, the duration and family rules, config parsing and clamping, the optional-effect availability decision, and the bonus contributions against the parent's accumulator.

## License

MIT, see [LICENSE](LICENSE). The build scripts and project skeleton come from CleanroomMC ForgeDevEnv under MIT, see [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
