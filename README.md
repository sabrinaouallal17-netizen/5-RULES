# 5-RULES
Hey!!! 5 RULES IS AN MINECRAFT MOD! :D

A Fabric troll mod for **Minecraft 1.21.1**. Break a rule and pay the price.

## The rules

1. **Use the RIGHT tool for the RIGHT thing.** Breaking a block with a tool that isn't meant for it
   (a log with a pickaxe, stone with an axe...) makes you explode and die. Bare hands are fine.
2. **Ask animals before killing them.** Sneak + right-click an animal with an empty hand to ask.
   It answers Yes or No (50/50) and remembers its answer. Kill it after a No, or without asking,
   and lightning strikes you dead.
3. **Do not waste food.** Eating while your food bar is at half or more makes an anvil fall from the sky on you.
4. **Do not break nature's beauty.** Felling 5 trees in a row (less than 2 minutes between each)
   empties your whole inventory. Then the rule rests for 10 minutes.
   A tree counts when you break its bottom log (the one on dirt/grass).
5. **Do not cheat.** The F3 screen only shows "Cheater", and opening it eats 2 to 8 points
   of your food bar (it never drops to zero).

Creative and spectator players are not affected.

## Build

Requires Java 21.

```sh
./gradlew build
```

The mod jar is in `build/libs/`. Put it in your `mods` folder together with
[Fabric API](https://modrinth.com/mod/fabric-api). Every push is also built by GitHub Actions,
which uploads the jar as a build artifact.
