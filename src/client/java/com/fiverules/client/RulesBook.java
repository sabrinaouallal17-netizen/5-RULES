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
						"No F3!\n\nCheaters see nothing but the truth, and lose some of their food."));
	}

	private static Text title() {
		return Text.empty()
				.append(Text.literal("\n\n   THE 5 RULES\n\n").formatted(Formatting.DARK_RED, Formatting.BOLD))
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
