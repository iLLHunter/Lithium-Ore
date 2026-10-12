# Lithium Ore for NeoForge 1.21.1

Lithium Ore adds configurable lithium ore generation for the Overworld, Nether, End, and custom dimensions. Optional Mekanism integration provides Crusher recipes for lithium dust.

Current development version: `0.1.2`

## Features

- Four ore blocks: stone, deepslate, Nether, and End lithium ore.
- Iron pickaxe or better required for drops.
- Block and item compatibility tags under `c:ores/lithium` and `c:ores`.
- JSON-configurable dimensions, replacement materials, height ranges, vein sizes, attempt counts, distributions, and biome restrictions.
- Vanilla `OreFeature` vein placement with exact block replacement.
- Optional generated Mekanism Crusher recipes.
- No retrogen or chunk-load generation. Configuration changes affect newly generated chunks.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.194` or newer in the `21.1.x` line
- Java 21
- Mekanism `10.7+` when `use_mekanism` is enabled

## Building

The Gradle wrapper is included:

```powershell
.\gradlew.bat clean build
```

On Linux or macOS:

```bash
./gradlew clean build
```

The resulting JAR is written to `build/libs/lithiumore-0.1.2.jar`.

## Configuration

At first startup, Lithium Ore creates:

```text
config/lithiumore/
  main.json
  overworld.json
  nether.json
  end.json
  generated_recipes/
    pack.mcmeta
    data/lithiumore/recipe/
      crushing_lithium_ore.json
      crushing_deepslate_lithium_ore.json
      crushing_nether_lithium_ore.json
      crushing_end_lithium_ore.json
```

Reference copies are available in `config-examples/lithiumore/`.

### Mekanism integration

`main.json` controls the integration and Crusher outputs:

```json
{
  "use_mekanism": true,
  "mekanism": {
    "crusher_outputs": {
      "lithium_ore": 2,
      "deepslate_lithium_ore": 3,
      "nether_lithium_ore": 4,
      "end_lithium_ore": 8
    }
  }
}
```

Set `use_mekanism` to `false` and restart to remove the Mekanism runtime requirement and generated Crusher recipes. Output counts must be integers from 1 through 64.

### Ore generation

Each dimension file has shared settings and one or more entries in `generators`:

- `enabled`: enables or disables the entire file.
- `distribution`: `uniform` or `triangle`.
- `generators[].block`: one of Lithium Ore's four registered ore block IDs.
- `generators[].material`: exact block ID to replace.
- `generators[].cluster-size`: vein size from 1 through 64.
- `generators[].cluster-count`: placement attempts per chunk from 0 through 128.
- `generators[].min-height` and `max-height`: inclusive Y range, clipped to the dimension's build limits.
- `generators[].enabled`: optional switch for one generator entry.
- `biome`: `all`, a biome ID, a list of biome IDs, or a biome tag such as `#minecraft:is_forest`.
- `dimension.restriction`: `whitelist` or `blacklist`.
- `dimension.value`: modern dimension IDs such as `minecraft:the_end`.

The default Overworld file includes separate stone and deepslate generators. `config-examples/examples/moon.json` demonstrates a custom dimension configuration.

All configuration is read during process startup. Restart the client or server after editing it.

## Validation

The repository includes static resource validation and a GitHub Actions build. Local verification:

```powershell
python tools/validate_assets.py
.\gradlew.bat clean build
```

Always test world generation changes in disposable worlds and newly generated chunks.

## License

GNU Lesser General Public License v3.0 only (`LGPL-3.0-only`). See `LICENSE`.
