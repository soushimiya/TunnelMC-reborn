# TunnelMC
### Development temporarily on hold... (written on 14 March 2023)

TunnelMC allows Minecraft Java Edition Players to join and play Minecraft Bedrock Edition servers

# How does it work
Firstly TunnelMC is a [Fabric Mod](https://fabricmc.net/). What we do is we open a connection to a Minecraft Bedrock server and translate any incoming and outgoing packets, so they can be read by both Editions.

# Why a Fabric Mod and not a Proxy
Well we love fabric ❤️, also making it a mod instead of a proxy allows us to do some stuff we normally could not do. Such as skins, we read the skins from the bedrock server instead of [Minecraft.net](https://minecraft.net/) this would not be possible without some sort of mod. Also *technically* we could add emotes and other stuff Minecraft Java Edition does not have. Granted we probably wont add emotes but we *could*.

# What is left to add
A lot! But luckily basic gameplay is already possible.
- Block/item translation (thanks to [PrismarineJS' mappings](https://github.com/PrismarineJS/minecraft-data), this still needs work on, for example: stairs
- Good bedrock bridging
- Containers, like chests
- Crafting, and a lot more survival features...
- Resource packs (with/without encryption)
- Custom blocks/items/entities or at least have substitutes for them
- Xbox live features, like joining worlds from invites

# How can I try it
As of this writing, you cannot\*. We're still in development and a lot has not been added yet.<br>
*\* = I'm not comfortable with non-technical players using this mod yet. There's obviously nothing stopping you from building this mod, but know that I'm not at fault for any punishments you may gain.*

# Contributing
If you'd like to help, then please follow the instructions from [Fabric Wiki](https://fabricmc.net/wiki/tutorial:setup#intellij_idea) 

# Credits
This project would not be possible without the amazing work of these open source projects, whether it's just looking how thing works inorder to reverse translate them, looking at their code to see how thing work, and/or copying a little bit of their code. We apperiate all these projects.
- [Protocol](https://github.com/CloudburstMC/Protocol)
- [Nukkit](https://github.com/CloudburstMC/Nukkit)
- [Geyser](https://github.com/GeyserMC/Geyser)
- [gophertunnel](https://github.com/Sandertv/gophertunnel)
- [PrismarineJS](https://github.com/PrismarineJS/minecraft-data)

# Pictures
This is a picture of the Java Edition on a Bedrock Edition server
![](/pictures/JavaEdition.png)
This is a picture of what it looks like on the Bedrock Edition
![](/pictures/Windows10Edition.png)
