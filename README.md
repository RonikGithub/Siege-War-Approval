# Siege War Approval Plugin

## For Players:
The goal with Ronik's Siege War Approval is to reduce the impact of siege war on a server, to orient sieges to suit a primarily building/roleplay/community based server. 
- **Controlled Siege Initiation:** Only admin approved players can start a war by placing a banner.
- **Anti low playtime:** Prevents low playtime players from attending a seige.

## For Developers and Server owners:
Ronik's Siege War Approval is a plugin add-on for the Siege War plugin. It ensures that only players approved by an administrator can initiate a war by placing a banner. Once approved via the admin command, a player has temporary permission (default 60 minutes) to place a banner and trigger a siege.

## Features
### Features

- **Controlled Siege Initiation:** Only approved players can start a war by placing a banner.
- **Anti low playtime:** Prevents new players from attending a seige.
- **Admin Command:** Use `/dma <approve|disapprove> <player>` to grant/revoke temporary approval.
- **Configurable Approval Duration:** Adjust the approval time in `config.yml`.
- **Seamless Integration:** Works alongside the Siege War plugin to prevent unauthorized war starts.

## Requirements
### Requirements

- Minecraft server running Paper (tested on Paper 1.20.4).
- Java 8 or higher.
- Siege War plugin installed and configured.

## Installation
### Installation

1. **Download the Plugin Jar:**  
   Place the `RoniksSeigeWarApprovalV1.jar` file into your server's `plugins` folder.
@@ -25,3 +32,6 @@ Ronik's Siege War Approval is a plugin add-on for the Siege War plugin. It ensur
   ```yaml
   # The number of minutes a player is approved to place a banner.
   approvalMinutes: 60

   # Number of hours a player needs to wait before they can receive war ranks.
   requiredPlaytimeHours: 2
