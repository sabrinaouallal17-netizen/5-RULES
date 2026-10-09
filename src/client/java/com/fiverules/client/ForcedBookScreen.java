package com.fiverules.client;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Rule 15: a book that can only be closed once you reach its last page. */
public class ForcedBookScreen extends Screen {
	private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/gui/book.png");
	private static final int BOOK_SIZE = 192;
	private static final int TEXT_WIDTH = 114;
	private static final int TEXT_COLOR = 0x000000;

	private final List<Text> pages;
	private int page;
	private ButtonWidget previousButton;
	private ButtonWidget nextButton;
	private ButtonWidget doneButton;

	public ForcedBookScreen(List<Text> pages) {
		super(Text.literal("Read the book"));
		this.pages = pages.isEmpty() ? List.of(Text.empty()) : pages;
	}

	@Override
	protected void init() {
		int center = width / 2;
		int buttonsY = BOOK_SIZE + 4;
		previousButton = addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> turn(-1))
				.dimensions(center - 100, buttonsY, 40, 20).build());
		nextButton = addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> turn(1))
				.dimensions(center + 60, buttonsY, 40, 20).build());
		doneButton = addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
				.dimensions(center - 50, buttonsY, 100, 20).build());
		refreshButtons();
	}

	private void turn(int delta) {
		page = Math.max(0, Math.min(pages.size() - 1, page + delta));
		refreshButtons();
	}

	private boolean onLastPage() {
		return page >= pages.size() - 1;
	}

	private void refreshButtons() {
		previousButton.active = page > 0;
		nextButton.active = !onLastPage();
		doneButton.visible = onLastPage();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return onLastPage();
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		context.drawTexture(BOOK_TEXTURE, (width - BOOK_SIZE) / 2, 2, 0, 0, BOOK_SIZE, BOOK_SIZE);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		int left = (width - BOOK_SIZE) / 2;
		String indicator = "Page " + (page + 1) + " of " + pages.size();
		context.drawText(textRenderer, indicator, left + BOOK_SIZE - 44 - textRenderer.getWidth(indicator), 18, TEXT_COLOR, false);
		List<OrderedText> lines = textRenderer.wrapLines(pages.get(page), TEXT_WIDTH);
		int y = 32;
		for (OrderedText line : lines.subList(0, Math.min(lines.size(), 14))) {
			context.drawText(textRenderer, line, left + 36, y, TEXT_COLOR, false);
			y += textRenderer.fontHeight;
		}
		if (!onLastPage()) {
			Text hint = Text.literal("Read until the last page!");
			context.drawCenteredTextWithShadow(textRenderer, hint, width / 2, BOOK_SIZE + 28, 0xFF5555);
		}
	}
}
