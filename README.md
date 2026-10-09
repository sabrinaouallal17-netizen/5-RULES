# 5-RULES
Hey!!! 5 RULES IS AN MINECRAFT MOD! :D

A Fabric troll mod for **Minecraft 1.21.1**. Break a rule and pay the price.

## The rules

1. **Use the RIGHT tool for the RIGHT thing.** Breaking a block with a tool that isn't meant for it
   (a log with a pickaxe, stone with an axe...) makes you explode and die. Bare hands are fine.
2. **Ask animals before killing them.** Sneak + right-click an animal with an empty hand to ask.
   It answers Yes or No (50/50) and remembers its answer. Kill it after a No, or without asking,
   and lightning strikes you dead.
3. **Do not waste food.** Eating with an almost full food bar (16/20, 8 drumsticks or more) makes an anvil
   fall from the sky on you (up to 5 hearts of damage).
4. **Do not break nature's beauty.** Felling 5 trees in a row (less than 2 minutes between each)
   empties your whole inventory. Then the rule rests for 10 minutes.
   A tree counts as soon as you break any of its logs, and only once. Logs you placed yourself don't count.
5. **Do not cheat.** The F3 screen only shows "Cheater" (it fades out after 3 seconds), and opening it
   eats 2 to 8 points of your food bar (it never drops to zero).
6. **Say good night.** Write "good night" in the chat (at most 5 minutes) before going to bed,
   or you wake up surrounded by 3 zombies.
7. **Don't dig straight down.** Breaking the block under your feet has 1 chance in 3 to open
   a 10-block hole under you.
8. **Don't stare at the sun.** Looking at the sun for 3 seconds blinds you for 10 seconds.
9. **Don't stand still.** Standing still for 60 seconds summons an Angry Chicken that pecks you
   (half a heart per second) for 15 seconds.
10. **Don't lie to animals.** If an animal said Yes and you don't kill it within a minute, it feels lied to:
    it follows you and hits you until you kill it or get more than 48 blocks away.
11. **Close the door behind you.** Leave a door you opened open for 30 seconds and a creeper spawns outside.
12. **Stay hydrated.** 20 minutes of play without drinking a water bottle makes you slow until you drink one.
13. **Don't sleep in armor.** Sleeping in armor breaks one random armor piece when you wake up.
14. **Don't look at the moon.** Staring at the moon for 3 seconds summons a phantom next to you.
15. **Read every book.** Picking up a book opens that book, and you can't close it before the last page.
16. **Feed your pets.** Tamed wolves, cats and parrots not fed for 20 minutes (while you play) run away.

Press **B** (configurable in Controls) to open a book with all the rules.

Creative and spectator players are not affected.

## Build

Requires Java 21.

```sh
./gradlew build
```

The mod jar is in `build/libs/`. Put it in your `mods` folder together with
[Fabric API](https://modrinth.com/mod/fabric-api). Every push is also built by GitHub Actions,
which uploads the jar as a build artifact.
