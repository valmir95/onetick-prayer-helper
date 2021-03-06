package com.vame.zulrah;/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;

import java.awt.*;

public class TextComponent implements RenderableEntity
{
	@Setter(AccessLevel.PACKAGE)
	private String text;

	@Setter(AccessLevel.PACKAGE)
	private Point position = new Point();

	@Setter(AccessLevel.PACKAGE)
	private Color color = Color.WHITE;

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Draw shadow
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, position.x + 1, position.y + 1);

		// Draw actual text
		graphics.setColor(color);
		graphics.drawString(text, position.x, position.y);

		final FontMetrics fontMetrics = graphics.getFontMetrics();
		return new Dimension(fontMetrics.stringWidth(text), fontMetrics.getHeight());
	}
}
