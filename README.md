# Modes adapter
Creates virtual devices to set states such as guest mode, vacation mode, etc.

Modes creates devices and entities base on the configuration files. The adapter does not communicate with any external hardware or applications except MQTT through the house adapter interface.

## Configuration
The adapter supports sharing configuration files with other adapters by using the namespace `modes`.
Within `devices` lists the virtual devices to be created. Named entities are set in the map `entities` where the key will be the entity name.

Entities must specify `type` even though the only implemented type is switch at this point. An icon name can optionally be set as well.

### Example
```yaml
modes:
   devices:
    - device:
         name: Guest mode
      entities:
         Enable:
            type: switch
            icon: account-group

    - device:
         room: Living room
         name: Nightmare preventer
      entities:
         Enable:
            type: switch
            icon: ghost 
         Blocker:
            type: switch
            icon: cancel

```

In this example we have created two devices. The first device is room-less with the name `Guest mode`.
It uses a switch as it's `Enable` entity with the icon `account-group`. Guest mode can be used in Home Assistant or Node red to disable certain automations when quests are in the house.

The other device `Nightmare preventer` has an additional named entity `Blocker`. The idea behind this device is to pause and turn off the screen if kids are entering the room while grown ups are watching Marvels Legion... The `Enable` entity is used to turn the detection on or off, the `Blocker` entity is used to control the actual pause & av-mute state. It makes sense to gather these entities under a single device.