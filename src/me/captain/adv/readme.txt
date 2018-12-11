MYOA 0.7

Requirements: CaptainChat

Configuration: /plugins/MYOA/config.yml
    - Insert paths to info files here with the following format:

Adventure:
  path: adventure.yml

Features: A condensed rundown of things available
    Adventures:
        - Create multiple adventures for players
        - Create quests with multiple stages and rewards
        - Create NPCs to facilitate quests and for atmosphere

Commands:
    /adv: Commands with {} denote optional arguments, <> denote necessary parameters

        *blank*: 
            - Shows loaded adventures and their IDs
        live:
            - Show live quests you are currently playing
        char:
            - Show some information about your character
        locations/zones/perks/quests/players:
            - List all of the given item regardless of adventure
        save:
            - Save the current adventures and NPCs
        start <adventure id> {quest id}:
            - Start the given adventure/quest
        class <player class>:
            - Set your character's class
        states <quest id>:
            - Lists the states for the given quest
        info:
            loc <adventure id> <location id>:
                - Get info about the given location
            tp <adventure id> <teleport id>:
                - Get info about the given teleport
            zone <adventure id> <zone id>:
                - Get info about the given zone
            quest <adventure id> <quest id>:
                - Get info about the given quest
            state <adventure id> <quest id> <state id>:
                - Get info about the given quest state
        new:
            loc <name> <id> <adventure id>:
                - Creates a new location with the given name, id, and adventure id
            sign <name> <id> <adventure id>:
                - Creates a new sign/bed location with the given name, id, and adventure id
            quest <adventure id> <quest id> <reward xp>:
                - Creates a new quest with the given id/reward/adventure id
            zone <name> <node> <id> <offset> <adventure id>:
                - Creates a new zone with the given values
            state <adventure id> <quest id> <state id> <next state id> <state type>:
                - Creates a new quest state with the given values
            tp <name> <id> <adventure id> <location id> <zone id>:
                - Creates a new teleport with the given values
        reward <adventure id> <quest id> <material> <amount>:
            - Set the reward of the given quest to the given item stack
        required <adventure id> <quest id> <material> <amount>:
            - Set the items required to start the quest to the given item stack
        zone <adventure id> <quest id> <state id> <zone id>:
            - Set the zone of the given quest state
        loc <adventure id> <quest id> <state id> <location id>:
            - Set the location of the given quest state
        name:
            loc <adventure id> <location id> <name>{as many words as you want}:
                - Sets the name of the location to the new one specified
            quest <adventure id> <quest id> <name>{as many words as you want}:
                - Sets the name of the quest to the new one specified
            state <adventure id> <quest id> <state id> <name>{as many words as you want}:
                - Sets the name of the quest state to the new one specified
        meta:
            loc <adventure id> <location id> <flavor text>{as many words as you want}:
                - Adds flavor text to the given location
            state <adventure id> <quest id> <state id> <flavor text>{as many words as you want}:
                - Adds flavor text to the given quest state
        npc:
            add <adventure id> <npc id> <npc name>{as many words etc}:
                - Adds the npc to the adventure
            career <adventure id> <npc id> <villager career>:
                - Sets the NPCs career
            anchor <adventure id> <npc id>:
                - Sets the NPCs location anchor to the current player location
            gaze <adventure id> <npc id>:
                - Sets the NPCs location gaze to the current player location
            uuid <adventure id> <npc id>:
                - Sets the NPCs location anchor to something random