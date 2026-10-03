# Countries

Client-side Fabric mod for Minecraft 26.3 that adds a small country flag to the right of player names in TAB.

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

Human-readable names are supported. Two-letter codes are not required.

Examples include:

- `Turkey`, `Turkiye`, `Türkiye`
- `Germany`, `Deutschland`, `Duetchland`, `Deutchland`
- `USA`, `United States`, `United States of America`
- `Russia`
- `Serbia`
- `United Kingdom`, `UK`
- and standard country names in English, Turkish, German, and Russian

The JSON files also store readable names such as `Serbia` and `Germany`.

## Sources of country data

Countries checks several sources, in this order:

1. `config/countries.json` on your computer.
2. The repository's public `players.json`.
3. Countries learned earlier from the optional same-mod handshake and saved in `config/countries-cache.json`.

This means a player does **not** need to install Countries for their flag to appear on Hypixel or another server if their username or UUID is already in one of your directories.

Minecraft clients are not given other players' IP addresses, so completely unknown strangers cannot be geolocated automatically from the normal multiplayer connection.

## Live updates

- Local `config/countries.json` is checked about every 5 seconds.
- GitHub `players.json` is refreshed about every 60 seconds.
- `/country` updates the local database immediately.
- UUID entries continue to work after a player changes their Minecraft name.

## Learned cache

If another Countries user reports their own country through the same-mod handshake, the mapping is saved to `config/countries-cache.json`.

After that, the flag can still appear in later sessions even when that player no longer has Countries installed.

## Flags

The mod uses bitmap country flags embedded into the built JAR. No menu or settings screen is required.
