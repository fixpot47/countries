# Countries

Client-side Fabric mod for Minecraft 26.3.

Countries adds a small rectangular bitmap country flag directly to the **right of a player's world nametag above their head**.

It does **not** add the flag to TAB.

The flags are Minecraft bitmap-font textures (12×9 px), not Apple/Android emoji.

## In-game command

You do not need to edit JSON manually.

Use:

```
/country <player> <country>
```

Example:

```
/country GrimGruff Serbia
```

The command is handled entirely by the client and is not sent to the server.

If the player is currently online, Countries stores both the username and UUID. If the player is not online, it still stores the username.

The result is saved automatically to:

```
config/countries.json
```

## Country names

Human-readable names are supported. Examples:

- `Turkey`, `Turkiye`, `Türkiye`
- `Germany`, `Deutschland`, `Duetchland`, `Deutchland`
- `USA`, `United States`, `United States of America`
- `Russia`
- `Serbia`
- `United Kingdom`, `UK`
- standard country names in English, Turkish, German, and Russian

## World nametag rendering

The flag is inserted after the actual Minecraft username while preserving server prefixes, ranks, colors and suffixes when possible.

Example:

```
[MVP+] GrimGruff [Serbia flag]
```

The flag component forces white text tint so team/rank colors do not recolor the bitmap.

## Sources of country data

Countries checks:

1. `config/countries.json`
2. GitHub `players.json`
3. learned UUID mappings in `config/countries-cache.json`
4. the optional same-mod handshake

Players do not need Countries installed if their username or UUID is already known by your directory.

Unknown players on a third-party server cannot be geolocated from the ordinary Minecraft connection because the client is not sent their IP address.
