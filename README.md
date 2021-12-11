![image](https://media.discordapp.net/attachments/396663973006540802/886686297140363315/logo.png)

A 1.18 ([1.17 version here](https://github.com/0x151/Atomic/tree/c58cad3209ea17ebff915c32a31bca8aa21c081f/builds)) fabric mod with useful features for enforcing the [Minecraft commercial use guidelines](https://account.mojang.com/documents/commercial_guidelines#:~:text=sell%20entitlements%20that%20affect%20gameplay)
on certian Minecraft servers.

## Why?

This is the sequel to [cornos](https://cornos.cf), but ported to 1.18, with a new look and functionality. Cornos became
a bit stale, so I decided to start this.

## Support

0x150 the 2nd#0194<br>
Discord server: https://discord.gg/f2mAAz5pHF

## Downloading

You can download this from the `builds` folder. There is only one file in there, so i dont think you can download the wrong thing. Download and drag
into your 1.18+ mods folder to use.

## Installation

### GNU/Linux <!--on top-->

1. Download java 17
    - `sudo apt install openjdk-17-jre` on ubuntu/debian
    - `sudo pacman -s jre-openjdk` on manjaro/arch
    - `echo "you're on your own, good luck"` on gentoo
   <!--tbh i never used fedora so I can't help them-->
2. Extract java 17 to a folder of your choice
3. Tell your 1.18 minecraft instance to use that java 17, if it doesn't automatically do it
4. Install [fabric](https://fabricmc.net/use/) for 1.18
5. Drag the .jar into the `~/.minecraft/mods` folder, fabric api is required.
6. Launch fabric loader for 1.18 via the minecraft launcher

### Windows

The default launcher should already choose java 17 for the runtime, so you're free from steps 1-3

1. Install [fabric](https://fabricmc.net/use/) for 1.18
2. Drag the .jar into the `%appdata%/.minecraft/mods` folder, Fabric API is required
3. Launch fabric loader for 1.18 via the minecraft launcher

### Mac

1. Install [fabric](https://fabricmc.net/use/) for 1.18
2. Drag the .jar into the `~/Library/Application Support/minecraft/mods` folder
3. Launch fabric loader for 1.18 via the minecraft launcher
