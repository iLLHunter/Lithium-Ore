# Lithium Ore 1.0.0 for Neoforge

Lithium Ore adds configurable lithium ore generation for the Overworld, Nether, End, and custom dimensions, with optional Mekanism Crusher recipes.

Current development version: `1.0.0`

## Features

- Four ore variants: stone, deepslate, Nether, and End.
- Requires an iron pickaxe or better for drops.
- Configurable dimensions, replacement blocks, height ranges, vein sizes, distributions, and biome restrictions.
- Compatibility tags under `c:ores/lithium` and `c:ores`.
- Translations for 22 locales, including separate Portuguese and Chinese variants.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.243` or newer in the `21.1.x` line
- Java 21
- Mekanism `10.7+` when the integration is enabled

## Configuration

JSON configuration files are created in `config/lithiumore/` on first startup. Edit them to customize ore generation and Crusher outputs, then restart the client or server. Set `use_mekanism` to `false` in `main.json` to run without Mekanism.

Configuration changes affect newly generated chunks; existing chunks are not regenerated. Example configurations are included in [`config-examples/`](config-examples/).

## License

GNU Lesser General Public License v3.0 only (`LGPL-3.0-only`). See [LICENSE](LICENSE).
