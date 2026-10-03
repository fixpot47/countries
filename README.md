# Countries

Client-side Fabric mod for Minecraft 26.3 that adds a small country flag to the right of player names in TAB.

## Sources of country data

Countries checks several sources, in this order:

1. `config/countries.json` on your computer.
2. The repository's public `players.json`.
3. Countries learned earlier from the optional same-mod handshake and saved in `config/countries-cache.json`.

This means a player does **not** need to install Countries for their flag to appear on Hypixel or another server if their username or UUID is already in one of your directories.

Minecraft clients are not given other players' IP addresses, so completely unknown strangers cannot be geolocated automatically from the normal multiplayer connection.

## players.json / config/countries.json

Both files use the same simple format. Keys can be Minecraft usernames or UUIDs and values are two-letter ISO country codes.

```json
{
  "ExamplePlayer": "TR",
  "AnotherPlayer": "DE",
  "00000000-0000-0000-0000-000000000000": "US"
}
```

- Local `config/countries.json` is checked about every 5 seconds.
- GitHub `players.json` is refreshed about every 60 seconds.
- UUID entries continue to work after a player changes their Minecraft name.

## Learned cache

If another Countries user reports their own country through the same-mod handshake, the mapping is saved to `config/countries-cache.json`.

After that, the flag can still appear in later sessions even when that player no longer has Countries installed.

## Same-mod handshake

When another participant uses Countries, their client can determine only **its own** country and sends `fixpot47` a short country marker through the server's normal private-message command. The marker contains the player's UUID and two-letter country code, not their IP address.

The handshake depends on the server allowing a compatible `/msg` command. The directory system above does not depend on it.

## Flags

The mod uses bitmap country flags embedded into the built JAR. No menu or settings screen is required.
