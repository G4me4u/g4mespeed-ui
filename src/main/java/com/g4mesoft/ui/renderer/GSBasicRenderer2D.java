package com.g4mesoft.ui.renderer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.g4mesoft.ui.mixin.client.GSIGuiGraphicsAccess;
import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.util.GSColorUtil;
import com.g4mesoft.ui.util.GSMathUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

public class GSBasicRenderer2D implements GSIRenderer2D {

	private static final int LINE_SPACING = 2;
	
	private static final GSTexture MENU_BACKGROUND_TEXTURE = new GSTexture(Screen.MENU_BACKGROUND, 32, 32);
	private static final GSTexture INWORLD_MENU_BACKGROUND_TEXTURE = new GSTexture(Identifier.withDefaultNamespace("textures/gui/inworld_menu_background.png"), 32, 32);

	private final Minecraft client;
	
	private GuiGraphicsExtractor context;
	private int mouseX;
	private int mouseY;
	private int viewportWidth;
	private int viewportHeight;
	private ScreenRectangle viewBounds;
	
	private GSTransform2D transform;
	private final Deque<GSTransform2D> transformStack;
	private final Deque<GSClipRect> clipStack;
	private float opacity;
	private final Deque<Float> opacityStack;
	
	private GSRectangle cachedClippedBounds;
	
	public GSBasicRenderer2D(Minecraft client) {
		this.client = client;
		
		transform = new GSTransform2D();
		transformStack = new ArrayDeque<>();
		clipStack = new ArrayDeque<>();
		opacity = 1.0f;
		opacityStack = new ArrayDeque<>();
		
		cachedClippedBounds = null;
	}
	
	public void begin(GuiGraphicsExtractor context, int mouseX, int mouseY, int viewportWidth, int viewportHeight) {
		this.context = context;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		
		viewBounds = new ScreenRectangle(0, 0, viewportWidth, viewportHeight);
	}
	
	public void end() {
		if (!clipStack.isEmpty())
			throw new IllegalStateException("Clip stack is not empty");
		if (!transformStack.isEmpty())
			throw new IllegalStateException("Transform stack is not empty");
		
		transformStack.clear();
		transform.reset();
		
		viewBounds = null;
	}

	public void nextLayer() {
		context.nextStratum();
	}
	
	@Override
	public int getMouseX() {
		return mouseX - transform.offsetX;
	}

	@Override
	public int getMouseY() {
		return mouseY - transform.offsetY;
	}
	
	@Override
	public void pushMatrix() {
		transformStack.push(transform);
		transform = new GSTransform2D(transform);
		
		context.pose().pushMatrix();
	}

	@Override
	public void popMatrix() {
		if (transformStack.isEmpty())
			throw new IllegalStateException("Transform stack is empty!");
		
		transform = transformStack.pop();
		context.pose().popMatrix();
		
		invalidateClipBounds();
	}
	
	@Override
	public void identity() {
		context.pose().identity();
		transform.offsetX = transform.offsetY = 0;
	}

	@Override
	public void translate(int x, int y) {
		transform.offsetX += x;
		transform.offsetY += y;

		context.pose().translate(x, y);
		
		invalidateClipBounds();
	}
	
	@Override
	public void pushClip(int x, int y, int width, int height) {
		pushClip(new GSClipRect(x, y, x + width, y + height));
	}

	@Override
	public void pushClip(GSClipRect clip) {
		// Translate clip according to current transform.
		clip = clip.offset(transform.offsetX, transform.offsetY);
		// Ensure previous clip bounds are still in effect.
		GSClipRect prevClip = clipStack.peek();
		if (prevClip != null) {
			clip = prevClip.intersection(clip);
		}
		
		clipStack.push(clip);
		
		invalidateClipBounds();
		// Compute the clip bounds and update scissor
		context.scissorStack.push(new ScreenRectangle(clip.x0, clip.y0, clip.x1 - clip.x0, clip.y1 - clip.y0));
	}

	@Override
	public GSClipRect popClip() {
		if (clipStack.isEmpty())
			throw new IllegalStateException("Clip stack is empty!");
		
		GSClipRect oldClip = clipStack.pop();
		
		invalidateClipBounds();
		context.disableScissor();
		
		return oldClip;
	}

	@Override
	public GSRectangle getClipBounds() {
		if (cachedClippedBounds == null)
			cachedClippedBounds = computeClippedBounds();
		return new GSRectangle(cachedClippedBounds);
	}
	
	private void invalidateClipBounds() {
		// Should be invoked whenever the transform, clip, or viewport size changes.
		cachedClippedBounds = null;
	}
	
	private GSRectangle computeClippedBounds() {
		GSClipRect clip = clipStack.peek();
		if (clip == null) {
			// Clipped by viewport edges.
			return new GSRectangle(-transform.offsetX, -transform.offsetY, viewportWidth, viewportHeight);
		}

		// Find minimum bounds that contains the clip (x0, y0, x1, and y1 should be
		// mathematical integers, since they have only been set by GSIRenderer2D).
		int x = Math.max((int)(clip.x0 - 0.5f), 0);
		int y = Math.max((int)(clip.y0 - 0.5f), 0);
		int w = Math.min((int)(clip.x1 + 0.5f) - x, viewportWidth);
		int h = Math.min((int)(clip.y1 + 0.5f) - y, viewportHeight);
		
		return new GSRectangle(x - transform.offsetX, y - transform.offsetY, w, h);
	}
	
	@Override
	public void pushOpacity(float opacityMultiplier) {
		opacityStack.push(this.opacity);
		this.opacity *= GSMathUtil.clamp(opacityMultiplier, 0.0f, 1.0f);
	}

	@Override
	public float popOpacity() {
		float oldOpacity = opacity;
		opacity = opacityStack.pop();
		return oldOpacity;
	}
	
	private int applyOpacity(int color) {
		return GSColorUtil.withAlpha(color, (int)((color >>> 24) * opacity));
	}

	@Override
	public void fillGradient(int x, int y, int width, int height,
	                         int tlColor, int trColor, int blColor, int brColor,
	                         boolean mirror) {

		x += transform.offsetX;
		y += transform.offsetY;
		
		ScreenRectangle scissorArea = context.scissorStack.peek();
		ScreenRectangle layerBounds = (scissorArea != null) ? scissorArea : viewBounds;

		context.guiRenderState.addGuiElement(new GSFilledQuad(x, y, width, height,
		                                                      applyOpacity(tlColor),
		                                                      applyOpacity(trColor),
		                                                      applyOpacity(blColor),
		                                                      applyOpacity(brColor),
		                                                      mirror,
		                                                      scissorArea,
		                                                      layerBounds));
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		drawHLine(x, x + width, y, color);
		drawHLine(x, x + width, y + height - 1, color);
		
		drawVLine(x, y + 1, y + height - 1, color);
		drawVLine(x + width - 1, y + 1, y + height - 1, color);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy) {
		drawTexture(texture, x, y, width, height, sx, sy, 0xFFFFFFFF);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy, int color) {
		drawTexture(texture.getRegion(sx, sy, width, height), x, y, color);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y) {
		drawTexture(texture, x, y, 0xFFFFFFFF);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, int color) {
		Identifier sprite = texture.getTexture().getIdentifier();
		int x1 = x + texture.getRegionWidth();
		int y1 = y + texture.getRegionHeight();
		
		((GSIGuiGraphicsAccess)context).gs_innerBlit(RenderPipelines.GUI_TEXTURED, sprite,
		                                             x, x1, y, y1,
		                                             texture.getU0(), texture.getU1(), texture.getV0(), texture.getV1(),
		                                             applyOpacity(color));
	}

	public void legacyDrawGuiTexture(Identifier texture, int x, int y, int w, int h) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, w, h);
	}
	
	@Override
	public void drawVLine(int x, int y0, int y1, int color) {
		fillRect(x, y0, 1, y1 - y0, color);
	}

	@Override
	public void drawHLine(int x0, int x1, int y, int color) {
		fillRect(x0, y, x1 - x0, 1, color);
	}
	
	@Override
	public void drawDottedVLine(int x, int y0, int y1, int length, int spacing, int color) {
		int n = (y1 - y0) / (length + spacing);
		
		for (int yl = 0; yl <= n; yl++) {
			int yl0 = y0 + yl * (length + spacing);
			int yl1 = Math.min(yl0 + length, y1);
			drawVLine(x, yl0, yl1, color);
		}
	}
	
	@Override
	public void drawDottedHLine(int x0, int x1, int y, int length, int spacing, int color) {
		int n = (x1 - x0) / (length + spacing);
		
		for (int xl = 0; xl <= n; xl++) {
			int xl0 = x0 + xl * (length + spacing);
			int xl1 = Math.min(xl0 + length, x1);
			drawHLine(xl0, xl1, y, color);
		}
	}
	
	@Override
	public void drawMenuBackground(boolean inWorld) {
		if (!inWorld)
			drawPanoramaBackground();
		// Apply blur
		applyBlur();
		// Draw darkening texture
		pushMatrix();
		identity();
		GSTexture darkening = inWorld ? INWORLD_MENU_BACKGROUND_TEXTURE : MENU_BACKGROUND_TEXTURE;
		drawTexture(darkening.getRegion(0, 0, viewportWidth, viewportHeight), 0, 0);
		popMatrix();
	}
	
	@Override
	public void drawPanoramaBackground() {
		pushMatrix();
		identity();
		client.gameRenderer.panorama().extractRenderState(context, viewportWidth, viewportHeight);
		popMatrix();
	}
	
	@Override
	public void applyBlur() {
		context.blurBeforeThisStratum();
	}

	@Override
	public int getTextAscent() {
		return client.font.lineHeight - 2;
	}

	@Override
	public int getTextDescent() {
		return 1;
	}
	
	@Override
	public int getTextHeight() {
		// Include shadows in the text height
		return getTextAscent() + getTextDescent() + 1;
	}

	@Override
	public int getLineHeight() {
		return getTextHeight() + LINE_SPACING;
	}
	
	@Override
	public float getTextWidth(String text) {
		return client.font.width(text);
	}

	@Override
	public float getTextWidthNoStyle(CharSequence text) {
		return client.font.width(new GSFormattedCharSequence(text));
	}

	@Override
	public void drawText(String text, int x, int y, int color, boolean shadowed) {
		context.text(client.font, text, x, y, applyOpacity(color), shadowed);
	}
	
	@Override
	public void drawTextNoStyle(CharSequence text, int x, int y, int color, boolean shadowed) {
		drawText(new GSFormattedCharSequence(text), x, y, color, shadowed);
	}
	
	@Override
	public float getTextWidth(FormattedCharSequence text) {
		return client.font.width(text);
	}

	@Override
	public void drawText(FormattedCharSequence text, int x, int y, int color, boolean shadowed) {
		context.text(client.font, text, x, y, applyOpacity(color), shadowed);
	}
	
	@Override
	public String trimString(String text, int availableWidth, String ellipsis) {
		int len = text.length();
		if (len <= 0)
			return text;

		// Text fits inside bounds.
		if (getTextWidth(text) <= availableWidth)
			return text;

		availableWidth -= getTextWidth(ellipsis);

		// No space for any other characters.
		if (availableWidth < 0)
			return ellipsis;

		String result = "";
		for (int i = 0; i < len; i++) {
			String substr = text.substring(0, i + 1);
			if (getTextWidth(substr) >= availableWidth)
				return result + ellipsis;
		
			result = substr;
		}

		// This should never happen.
		return result;
	}
	
	@Override
	public List<String> splitToLines(String text, int availableWidth) {
		List<String> result = new ArrayList<>();
		
		int len = text.length();
		if (len <= 0)
			return result;
		
		int lineBegin = 0;
		int lastSpaceIndex = -1;
		
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			int lineWidth = (int)Math.ceil(getTextWidth(text.substring(lineBegin, i)));
			
			if (c == ' ')
				lastSpaceIndex = i;
			
			if (lineWidth > availableWidth) {
				if (lastSpaceIndex != -1) {
					result.add(text.substring(lineBegin, lastSpaceIndex));
					
					i = lastSpaceIndex;
					lineBegin = lastSpaceIndex + 1;
					
					lastSpaceIndex = -1;
				} else {
					result.add(text.substring(lineBegin, i));
					lineBegin = i;
				}
			}
		}

		if (lineBegin != len)
			result.add(text.substring(lineBegin));
		
		return result;
	}
	
	@Override
	public FormattedCharSequence trimString(Component text, int availableWidth, Component ellipsis) {
		if (getTextWidth(text) <= availableWidth)
			return text.getVisualOrderText();
		
		availableWidth -= (int)Math.ceil(getTextWidth(ellipsis));
		
		FormattedText trimmed = client.font.substrByWidth(text, availableWidth);
		FormattedText result = FormattedText.composite(trimmed, ellipsis);
		
		return Language.getInstance().getVisualOrder(result);
	}
	
	@Override
	public List<FormattedCharSequence> splitToLines(Component text, int availableWidth) {
		return client.font.split(text, availableWidth);
	}

	private class GSTransform2D {
		
		private int offsetX;
		private int offsetY;
		
		public GSTransform2D() {
			reset();
		}

		public GSTransform2D(GSTransform2D other) {
			offsetX = other.offsetX;
			offsetY = other.offsetY;
		}
		
		private void reset() {
			offsetX = offsetY = 0;
		}
	}
}