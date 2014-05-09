<center>![FactionsDB](http://www.easymfne.net/images/factionsdb.png)</center>

<center>[Source](https://github.com/EasyMFnE/FactionsDB) |
[Change Log](https://github.com/EasyMFnE/FactionsDB/blob/master/CHANGES.log) |
[Feature Request](https://github.com/EasyMFnE/FactionsDB/issues) |
[Bug Report](https://github.com/EasyMFnE/FactionsDB/issues) |
[Donate](https://www.paypal.com/cgi-bin/webscr?hosted_button_id=457RX2KYUDY5G&item_name=FactionsDB&cmd=_s-xclick)</center>

<center>**Latest Version:** v1.0 for Bukkit 1.7+</center>

## About ##

FactionsDB, also known as *FactionsDeathBans*, is designed to add extra importance to player deaths and power loss on servers using the Factions plugin.  Players whose deaths cause them to fall below a desired power level will be temporarily banned (*DeathBanned*) from joining the server.  This is also effective at preventing continued abuse such as players being repeatedly killed at their spawn location or players repeatedly killing each other for rewards (e.g. skill levels in [McMMO](http://dev.bukkit.org/bukkit-plugins/mcmmo/)).

**Note:** This plugin requires [Factions](http://dev.bukkit.org/bukkit-plugins/factions/) v2.0 or later.

## Features ##

* Configurable power-level threshold
* Configurable power boost upon return from a DeathBan
* DeathBan duration is fully configurable
* Players can be sent to the spawn of a world upon DeathBan (for safety)
* DeathBans have configurable messages for kicked players, failed logins, server broadcasts, and players returning from bans.  Accepts color codes and several replacement tags.
* Server-wide broadcasting can be enabled/disabled
* Fully **UUID-compatible**
* Can track DeathBans across server restarts (if persistence is enabled)

## Installation ##

1. Download FactionsDB jar file.
2. Move/copy to your server's `plugins` folder.
3. Restart your server.
4. [**Optional**] Grant specific user permissions (see below).

## Permissions ##

FactionsDB has the following permission nodes:

* `factionsdb.admin` - Adminstrator permissions.  Default: `op`. Implies:
    * `factionsdb.command.factionsdb: true` (allows use of `/factionsdb`)
    * `factionsdb.ban.exempt: true` (prevents user from being DeathBanned)
* `factionsdb.defaults` - Default user permissions. Default: `true`. Implies:
    * `factionsdb.ban.exempt: false` (allows user to be DeathBanned)

## Commands ##

FactionsDB has only one command, `/factionsdb` (Alias: `/fdb`)

* `/factionsdb` - Show plugin usage information  
* `/factionsdb list` - Show a list of existing DeathBans 
* `/factionsdb pardon <name|*>...` - Pardon DeathBans for one or more players    
    * Note: "\*" implies all players
* `/factionsdb reload` - Reload configuration from disk    

## Configuration ##

Configuration files are located in the plugin's folder, `plugins/FactionsDB`    
The following configuration options are included and documented in `config.yml`:

        uuid-mode: (boolean, use UUIDs to track bans instead of names)
        persistence: (boolean, periodically save bans & reload at startup)
                     (should only be modified while the server is off!)
        
        power:
          threshold: (number, power level where DeathBans can be given)
          boost: (number, power boost given to players returning from bans)
        ban:
          broadcast: (boolean, should bans be broadcast server-wide)
          duration: (String, length of bans in format "<#[.#]><s|m|h|d>")
          send-to-spawn: (boolean, send to spawn before DeathBanning)
          spawn-world: (String, name of the world to respawn players into)
                       (If blank, players respawned to their current world)
      
        strings:
          kick: (String, message to send to player as they get DeathBanned)
          login: (String, message when DeathBan prevents joining server)
          broadcast: (String, message to broadcast server-wide upon DeathBan)
          return: (String, message sent to players returning from DeathBans)

**The above strings accept '`&`' style color codes, and also replace the following tags:**    

        {0} --> Player name
        {1} --> Ban duration
        {2} --> Power threshold
        {3} --> Time left
        {4} --> Power boost

With persistence enabled, DeathBans are saved as `bans.yml`.  It is ill-advised to manually edit this file, especially while a server is running.

## Bugs/Requests ##

This template is continually tested to ensure that it is correct, but sometimes bugs can sneak in.  If you have found a bug within the project, or if you have a feature request, please [create an issue on Github](https://github.com/EasyMFnE/FactionsDB/issues).

## Donations ##

Donating is a great way to thank the developer if you find the project useful for your server, and encourages work on more 100% free and open-source plugins.  If you would like to donate (any amount), there is an easily accessible link in the top right corner of this page.  Thank you!

## Privacy ##

This project template utilizes Hidendra's **Plugin-Metrics** system.  Users may opt out of this service by editing their configuration located in `plugins/Plugin Metrics`.  The following anonymous data is collected and sent to [mcstats.org](http://mcstats.org):

* A unique identifier
* The server's version of Java
* Whether the server is in online or offline mode
* The plugin's version
* The server's version
* The OS version, name, and architecture
* The number of CPU cores
* The number of online players
* The Metrics version

## License ##

This template is released as a free and open-source project under the [GNU General Public License version 3 (GPLv3)](http://www.gnu.org/copyleft/gpl.html).  To learn more about what this means, click that link or [read about it on Wikipedia](http://en.wikipedia.org/wiki/GNU_General_Public_License).
