# 🏎️ Bridge Racing Dojo

[![Version](https://img.shields.io/badge/Version-1.0.0-orange?style=for-the-badge)](https://github.com/yourusername/bridge-racing-dojo)
[![Tested](https://img.shields.io/badge/Tested-1.18--1.20-brightgreen?style=for-the-badge)](https://spigotmc.org)

**Bridge Racing Dojo** is a high-performance, competitive bridging mini-game designed for players who want to master the art of speed-bridging. Whether you're practicing for BedWars or just want to beat the clock, this "Dojo" is your ultimate training ground.

---

## ✨ Key Features

- **🏆 Competitive Racing**: Clean, bug-free racing logic with millisecond-precision timers.
- **👻 Ghost Mode**: Race against your own personal bests! See your previous run in real-time and push your limits.
- **📊 Detailed Persistence**: Full support for both **MySQL** and **YAML** storage. Track your total races and best times for every map.
- **⏱️ Live Scoreboards**: Dynamic sidebar display showing your current pace and personal bests.
- **🛠️ Professional Arena Tools**: Intuitive admin commands to create, configure, and manage maps on the fly.
- **🧹 Automatic Cleanup**: No more messy maps! Blocks are automatically cleared the moment a race ends.

---

## 🎮 How to Play

1. **Join a Map**: `/dojo join <mapName>`
2. **Bridge to the End**: Place blocks as fast as you can.
3. **Beat your PB**: Hit the finish line and check your stats with `/dojo stats`.
4. **Leave Anytime**: Need a break? Just type `/dojo leave`.

---

## 📜 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/dojo join <map>` | Join a racing arena | `None` |
| `/dojo leave` | Exit your current race | `None` |
| `/dojo list` | See all available maps | `None` |
| `/dojo stats` | View your personal bests | `None` |
| `/dojo admin` | Full setup & reload tools | `bridgedojo.admin` |

### Detailed Admin Commands
- `/dojo admin create <name>` - Create a new map profile.
- `/dojo admin startset <name>` - Set the spawn point.
- `/dojo admin finishset <name>` - Set the finish line.
- `/dojo admin setmaxplayers <name> <val>` - Limit the arena capacity.
- `/dojo admin reload` - Refresh configurations instantly.

---

## 🚀 Quick Setup

1. Drop the `BridgeRacingDojo.jar` into your `plugins` folder.
2. Restart your server to generate the default configuration.
3. (Optional) Configure your **MySQL** settings in `config.yml`.
4. Create your first map using `/dojo admin create FirstMap`.
5. Stand where players should start and type `/dojo admin startset FirstMap`.
6. Stand on the finish block and type `/dojo admin finishset FirstMap`.
7. **Done!** Users can now join using `/dojo join FirstMap`.

---

## 🎨 Professional Design
Designed with performance and aesthetics in mind, Bridge Racing Dojo provides a seamless experience for both players and server owners. Perfect for Lobby servers, Practice networks, or Minigame hubs.

---

*Developed with ❤️ by Ciree*
