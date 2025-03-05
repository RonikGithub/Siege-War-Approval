# Siege War Approval Plugin

Ronik's Siege War Approval is a plugin add-on for the Siege War plugin. It ensures that only players approved by an administrator can initiate a war by placing a banner. Once approved via the admin command, a player has temporary permission (default 60 minutes) to place a banner and trigger a siege.

## Features

- **Controlled Siege Initiation:** Only approved players can start a war by placing a banner.
- **Admin Command:** Use `/dma <approve|disapprove> <player>` to grant/revoke temporary approval.
- **Configurable Approval Duration:** Adjust the approval time in `config.yml`.
- **Seamless Integration:** Works alongside the Siege War plugin to prevent unauthorized war starts.

## Requirements

- Minecraft server running Paper (tested on Paper 1.20.4).
- Java 8 or higher.
- Siege War plugin installed and configured.

## Installation

1. **Download the Plugin Jar:**  
   Place the `RoniksSeigeWarApprovalV1.jar` file into your server's `plugins` folder.

2. **Ensure Config File is Included:**  
   The plugin creates a `config.yml` file. 
   ```yaml
   # The number of minutes a player is approved to place a banner.
   approvalMinutes: 60
