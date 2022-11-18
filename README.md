# Server Chat Heads
Use player heads in chat and more via placeholders! Credits to [CatDevz](https://github.com/CatDevz) for making the [original Minestom code](https://canary.discord.com/channels/706185253441634317/1042351571930984448/1042352421176885318).

![Showcase](https://user-images.githubusercontent.com/32773961/202585468-50f7663d-9df2-4ff1-b8ee-7beab1140783.png)

## How it works
When a player joins its skin gets downloaded and the colors of the pixels of the face get extracted and put in a `User UUID, TextColor[][]` Map, when the placeholder gets invoked, it searches for the user in that map and tries to print the head in chat using negative spaces and a custom pixel font.

## Placeholders
|          Placeholder          |         Function         |
|-------------------------------|--------------------------|
|     `%chatheads:player%`      | Print the invoker's head |
| `%chatheads:player [target]%` | Print the target's head  |

## Credits
- **[CatDevz](https://github.com/CatDevz)** for making the [original Minestom code](https://canary.discord.com/channels/706185253441634317/1042351571930984448/1042352421176885318)
- **[Camper_Samu (me)](https://github.com/CamperSamu)** for making the [Fabric](fabricmc.net) port

## Setup

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.

## License

This template is available under the GPLv3 license. Feel free to learn from it and incorporate it in your own projects following the license's guidelines.
