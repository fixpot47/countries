# Countries

Experimental client-side Fabric mod for Minecraft 26.3.

## How it works

This version is designed for a planned event where every participant installs the same mod.

1. Each client determines **its own** two-letter country code from its public IP.
2. The mod sends that country code and the player's UUID to `fixpot47` using a normal private `/msg`.
3. Clients with the mod hide the `[CF:uuid:CC]` handshake from chat.
4. The host client stores `UUID -> country` and adds only the country's bitmap flag to the right of that player's TAB name.

The mod never receives another player's IP address. It only exchanges a UUID and two-letter country code.

## Important limitations

- The server must allow the normal vanilla-style `/msg` command.
- Players without this mod cannot report their country automatically.
- A VPN/proxy will usually produce the VPN/proxy country.
- Participants send the handshake twice: about 5 seconds and 35 seconds after joining.
- The flag resource pack is downloaded at build time from the CountryFlags pack URL included in the plugin supplied for this project.

## Privacy

For automatic country detection, the client contacts `ipapi.co` first and Cloudflare's trace endpoint as a fallback. Only the resulting two-letter country code is sent to `fixpot47` through the Minecraft server.
