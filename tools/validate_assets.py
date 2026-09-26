#!/usr/bin/env python3
"""Static consistency checks only. Not a replacement for Gradle or a Minecraft test run."""
import json
import re
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources'
ASSETS = RES / 'assets/lithiumore'
DATA = RES / 'data/lithiumore'
CONFIG = ROOT / 'config-examples/lithiumore'
ORE = ('lithium_ore', 'deepslate_lithium_ore', 'nether_lithium_ore', 'end_lithium_ore')


def load(path):
    with path.open(encoding='utf-8') as fh:
        return json.load(fh)


def verify():
    files = list(ROOT.rglob('*.json'))
    for path in files:
        load(path)
    config_source = (ROOT / 'src/main/java/com/lithiumore/config/ConfigManager.java').read_text(encoding='utf-8')
    seeds = dict(re.findall(r'seed\("([^"]+)",\s*"""(.*?)"""\);', config_source, re.S))
    assert set(seeds) == {'main.json', 'overworld.json', 'nether.json', 'end.json'}
    for name, body in seeds.items():
        assert json.loads(body) == load(CONFIG / name), name
    main = load(CONFIG / 'main.json')
    assert main['use_mekanism'] is True
    assert main['mekanism']['crusher_outputs'] == dict(zip(ORE, (2, 3, 4, 8)))
    expected = [
        ('overworld.json', 'minecraft:overworld', [
            ('minecraft:stone', ORE[0], 3, 3, 0, 59),
            ('minecraft:deepslate', ORE[1], 4, 4, -63, 0),
        ]),
        ('nether.json', 'minecraft:the_nether', [('minecraft:netherrack', ORE[2], 4, 2, 5, 20)]),
        ('end.json', 'minecraft:the_end', [('minecraft:end_stone', ORE[3], 8, 4, 3, 250)]),
    ]
    for file, dimension, generators in expected:
        rule = load(CONFIG / file)
        assert rule['dimension']['value'] == [dimension]
        assert [
            (generator['material'], generator['block'].removeprefix('lithiumore:'),
             generator['cluster-size'], generator['cluster-count'],
             generator['min-height'], generator['max-height'])
            for generator in rule['generators']
        ] == generators
        assert 'retrogen' not in rule
    common_blocks = load(RES / 'data/c/tags/block/ores/lithium.json')['values']
    common_items = load(RES / 'data/c/tags/item/ores/lithium.json')['values']
    for ore in ORE:
        assert load(ASSETS / 'blockstates' / f'{ore}.json')['variants']['']['model'] == f'lithiumore:block/{ore}'
        assert load(ASSETS / 'models/block' / f'{ore}.json')['textures']['all'] == f'lithiumore:block/{ore}'
        assert load(ASSETS / 'models/item' / f'{ore}.json')['parent'] == f'lithiumore:block/{ore}'
        png = (ASSETS / 'textures/block' / f'{ore}.png').read_bytes()
        assert png[:8] == b'\x89PNG\r\n\x1a\n' and struct.unpack('>II', png[16:24]) == (16, 16)
        assert load(DATA / 'loot_table/blocks' / f'{ore}.json')['pools'][0]['entries'][0]['name'] == f'lithiumore:{ore}'
        assert f'block.lithiumore.{ore}' in load(ASSETS / 'lang/en_us.json')
        for tag in ('mineable/pickaxe', 'needs_iron_tool'):
            assert f'lithiumore:{ore}' in load(RES / 'data/minecraft/tags/block' / f'{tag}.json')['values']
        assert f'lithiumore:{ore}' in common_blocks
        assert f'lithiumore:{ore}' in common_items
    assert not (RES / 'data/minecraft/tags/block/needs_stone_tool.json').exists()
    assert load(RES / 'data/c/tags/block/ores.json')['values'] == ['#c:ores/lithium']
    assert load(RES / 'data/c/tags/item/ores.json')['values'] == ['#c:ores/lithium']
    assert load(DATA / 'neoforge/biome_modifier/all_biomes.json')['features'] == 'lithiumore:configurable_ore'
    assert load(DATA / 'worldgen/placed_feature/configurable_ore.json')['placement'] == []
    java = '\n'.join(path.read_text(encoding='utf-8') for path in (ROOT / 'src/main/java').rglob('*.java'))
    assert 'mekanism:dust_lithium' in java and 'mekanism:crushing' in java
    assert 'ChunkEvent' not in java and 'ChunkDataEvent' not in java
    assert 'nextLong(span)' not in java
    metadata = (ROOT / 'src/main/templates/META-INF/neoforge.mods.toml').read_text(encoding='utf-8')
    assert 'modId="mekanism"' in metadata and 'type="optional"' in metadata
    properties = dict(
        line.split('=', 1) for line in (ROOT / 'gradle.properties').read_text(encoding='utf-8').splitlines()
        if line and not line.startswith('#')
    )
    assert f"Current development version: `{properties['mod_version']}`" in (ROOT / 'README.md').read_text(encoding='utf-8')
    print(f'PASS: {len(files)} JSON documents, 4 ore variants, models, textures, loot, tags and config references.')
    print('NOTE: This does not verify Java compilation or Minecraft runtime behavior.')


if __name__ == '__main__':
    verify()
