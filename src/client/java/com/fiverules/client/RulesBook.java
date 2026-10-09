package com.fiverules.client;

import java.util.List;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** The pages of the rules book opened with the rules book key. */
public final class RulesBook {
	private RulesBook() {
	}

	public static List<Text> pages() {
		return List.of(
				title(),
				rule("Rule 1", "The right tool",
						"Use the RIGHT tool for the RIGHT thing.\n\nA log with a pickaxe? Stone with an axe? You explode.\n\nBare hands are fine."),
				rule("Rule 2", "Ask first",
						"Ask animals before killing them: sneak + right-click with an empty hand.\n\nThey say Yes or No. Kill one that said No, or that you never asked, and lightning strikes you."),
				rule("Rule 3", "Don't waste food",
						"Only eat when you are hungry.\n\nEat with an almost full food bar (8 drumsticks or more) and an anvil falls on your head."),
				rule("Rule 4", "Nature's beauty",
						"Do not fell 5 trees in a row.\n\nNature takes back everything in your inventory.\n\nAfter that, nature rests for 10 minutes."),
				rule("Rule 5", "No cheating",
						"No F3!\n\nCheaters see nothing but the truth, and lose some of their food."),
				rule("Rule 6", "Say good night",
						"Write \"good night\" in the chat before going to bed.\n\nSleep without saying it and you wake up surrounded by zombies."),
				rule("Rule 7", "Don't dig down",
						"Never dig straight down.\n\nBreak the block under your feet and the ground may open into a 10-block hole."),
				rule("Rule 8", "Don't stare at the sun",
						"Looking at the sun for 3 seconds makes you blind for 10 seconds.\n\nYour mom warned you."),
				rule("Rule 9", "Don't stand still",
						"Stay still for 60 seconds and an Angry Chicken comes to peck you.\n\nKeep moving!"),
				rule("Rule 10", "Don't lie to animals",
						"An animal said Yes? Then kill it within a minute.\n\nOtherwise it feels lied to, follows you and hits you."),
				rule("Rule 11", "Close the door",
						"Close the door behind you.\n\nLeave a door open for 30 seconds and something comes in. Something green."),
				rule("Rule 12", "Stay hydrated",
						"Drink a water bottle at least every 20 minutes.\n\nOtherwise you get slow until you drink."),
				rule("Rule 13", "No armor in bed",
						"Don't sleep in armor.\n\nWho does that? One piece of your armor breaks while you sleep."),
				rule("Rule 14", "Don't look at the moon",
						"Staring at the moon for 3 seconds makes something come down from the sky.\n\nIt has wings."),
				rule("Rule 15", "Read every book",
						"Pick up a book and you have to read it.\n\nAll of it. Until the last page. Like you're doing right now."),
				rule("Rule 16", "Feed your pets",
						"Feed your dogs, cats and parrots.\n\nForget them for 20 minutes and they run away."));
	}

	private static Text title() {
		return Text.empty()
				.append(Text.literal("\n\n     THE RULES\n\n").formatted(Formatting.DARK_RED, Formatting.BOLD))
				.append(Text.literal("Break one and pay the price.\n\nGood luck.").formatted(Formatting.BLACK));
	}

	private static Text rule(String number, String name, String body) {
		MutableText page = Text.empty();
		page.append(Text.literal(number + "\n").formatted(Formatting.DARK_RED, Formatting.BOLD));
		page.append(Text.literal(name + "\n\n").formatted(Formatting.DARK_GRAY, Formatting.UNDERLINE));
		page.append(Text.literal(body).formatted(Formatting.BLACK));
		return page;
	}
}
