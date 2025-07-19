package com.g4mesoft.ui.renderer;

import java.util.List;

import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.util.GSColorUtil;
import com.g4mesoft.ui.util.GSTextUtil;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public interface GSIRenderer2D extends GSIRenderer {

	public static final String DEFAULT_ELLIPSIS = "...";
	public static final Text DEFAULT_ELLIPSIS_TEXT = GSTextUtil.literal(DEFAULT_ELLIPSIS);
	
	public int getMouseX();

	public int getMouseY();

	default public boolean isMouseInside(GSRectangle bounds) {
		return isMouseInside(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	default public boolean isMouseInside(int x, int y, int width, int height) {
		if (getMouseX() < x || getMouseX() >= x + width)
			return false;
		if (getMouseY() < y || getMouseY() >= y + height)
			return false;
		return true;
	}
	
	public void pushMatrix();
	
	public void popMatrix();
	
	public void identity();

	public void translate(int x, int y);
	
	public void pushClip(int x, int y, int width, int height);
	
	public void pushClip(GSClipRect clipRect);

	public GSClipRect popClip();
	
	public GSRectangle getClipBounds();
	
	public void pushOpacity(float opacity);

	public float popOpacity();

	default public void fillRect(int x, int y, int width, int height, float r, float g, float b, float a) {
		fillRect(x, y, width, height, GSColorUtil.denormalizeRGBA(r, g, b, a));
	}

	default public void fillRect(int x, int y, int width, int height, int color) {
		fillGradient(x, y, width, height, color, color, color, color, false);
	}

	default public void fillHGradient(int x, int y, int width, int height,
	                                  float rl, float gl, float bl, float al,
	                                  float rr, float gr, float br, float ar) {

		fillHGradient(x, y, width, height,
		              GSColorUtil.denormalizeRGBA(rl, gl, bl, al),
		              GSColorUtil.denormalizeRGBA(rr, gr, br, ar));
	}
	
	default public void fillHGradient(int x, int y, int width, int height, int leftColor, int rightColor) {
		fillGradient(x, y, width, height, leftColor, rightColor, leftColor, rightColor, false);
	}
	
	default public void fillVGradient(int x, int y, int width, int height,
	                                  float rt, float gt, float bt, float at,
	                                  float rb, float gb, float bb, float ab) {
		
		fillVGradient(x, y, width, height,
		              GSColorUtil.denormalizeRGBA(rt, gt, bt, at),
		              GSColorUtil.denormalizeRGBA(rb, gb, bb, ab));
	}
	
	default public void fillVGradient(int x, int y, int width, int height, int topColor, int bottomColor) {
		fillGradient(x, y, width, height, topColor, topColor, bottomColor, bottomColor, false);
	}

	default public void fillGradient(int x, int y, int width, int height,
	                                 float rtl, float gtl, float btl, float atl,
	                                 float rtr, float gtr, float btr, float atr,
	                                 float rbl, float gbl, float bbl, float abl,
	                                 float rbr, float gbr, float bbr, float abr,
	                                 boolean mirror) {
		
		fillGradient(x, y, width, height,
		             GSColorUtil.denormalizeRGBA(rtl, gtl, btl, atl),
		             GSColorUtil.denormalizeRGBA(rtr, gtr, btr, atr),
		             GSColorUtil.denormalizeRGBA(rbl, gbl, bbl, abl),
		             GSColorUtil.denormalizeRGBA(rbr, gbr, bbr, abr),
		             mirror);
	}
	
	public void fillGradient(int x, int y, int width, int height,
	                         int tlColor, int trColor, int blColor, int brColor,
	                         boolean mirror);

	public void drawRect(int x, int y, int width, int height, int color);

	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy);

	default public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy, float r, float g, float b) {
		drawTexture(texture, x, y, width, height, sx, sy, GSColorUtil.denormalizeRGB(r, g, b));
	}

	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy, int color);
	
	public void drawTexture(GSITextureRegion texture, int x, int y);

	default public void drawTexture(GSITextureRegion texture, int x, int y, float r, float g, float b) {
		drawTexture(texture, x, y, GSColorUtil.denormalizeRGB(r, g, b));
	}

	public void drawTexture(GSITextureRegion texture, int x, int y, int color);

	public void drawVLine(int x, int y0, int y1, int color);
	
	public void drawHLine(int x0, int x1, int y, int color);

	public void drawDottedVLine(int x, int y0, int y1, int length, int spacing, int color);

	public void drawDottedHLine(int x0, int x1, int y, int length, int spacing, int color);
	
	public void drawMenuBackground(boolean inWorld);

	public void drawPanoramaBackground();

	public void applyBlur();
	
	public int getTextAscent();
	
	public int getTextDescent();

	public int getTextHeight();
	
	public int getLineHeight();
	
	public float getTextWidth(String text);

	public float getTextWidthNoStyle(CharSequence text);

	default public float getTextWidth(Text text) {
		return getTextWidth(text.asOrderedText());
	}
	
	public float getTextWidth(OrderedText text);
	
	default public void drawCenteredText(String text, int xc, int y, int color) {
		drawCenteredText(text, xc, y, color, true);
	}
	
	default public void drawCenteredText(String text, int xc, int y, int color, boolean shadowed) {
		drawText(text, xc - (int)Math.ceil(getTextWidth(text)) / 2, y, color, shadowed);
	}
	
	default public void drawText(String text, int x, int y, int color) {
		drawText(text, x, y, color, true);
	}

	public void drawText(String text, int x, int y, int color, boolean shadowed);
	
	default public void drawCenteredTextNoStyle(CharSequence text, int xc, int y, int color) {
		drawCenteredTextNoStyle(text, xc, y, color, true);
	}
	
	default public void drawCenteredTextNoStyle(CharSequence text, int xc, int y, int color, boolean shadowed) {
		drawTextNoStyle(text, xc - (int)Math.ceil(getTextWidthNoStyle(text)) / 2, y, color, shadowed);
	}
	
	default public void drawTextNoStyle(CharSequence text, int x, int y, int color) {
		drawTextNoStyle(text, x, y, color, true);
	}
	
	public void drawTextNoStyle(CharSequence text, int x, int y, int color, boolean shadowed);
	
	default public void drawCenteredText(Text text, int xc, int y, int color) {
		drawCenteredText(text.asOrderedText(), xc, y, color);
	}
	
	default public void drawCenteredText(Text text, int xc, int y, int color, boolean shadowed) {
		drawCenteredText(text.asOrderedText(), xc, y, color, shadowed);
	}
	
	default public void drawText(Text text, int x, int y, int color) {
		drawText(text.asOrderedText(), x, y, color);
	}

	default public void drawText(Text text, int x, int y, int color, boolean shadowed) {
		drawText(text.asOrderedText(), x, y, color, shadowed);
	}
	
	default public void drawCenteredText(OrderedText text, int xc, int y, int color) {
		drawCenteredText(text, xc, y, color, true);
	}
	
	default public void drawCenteredText(OrderedText text, int xc, int y, int color, boolean shadowed) {
		drawText(text, xc - (int)Math.ceil(getTextWidth(text)) / 2, y, color, shadowed);
	}
	
	default public void drawText(OrderedText text, int x, int y, int color) {
		drawText(text, x, y, color, true);
	}

	public void drawText(OrderedText text, int x, int y, int color, boolean shadowed);
	
	default public String trimString(String text, int availableWidth) {
		return trimString(text, availableWidth, DEFAULT_ELLIPSIS);
	}

	public String trimString(String text, int availableWidth, String ellipsis);

	public List<String> splitToLines(String text, int availableWidth);

	default public OrderedText trimString(Text text, int availableWidth) {
		return trimString(text, availableWidth, DEFAULT_ELLIPSIS_TEXT);
	}
	
	public OrderedText trimString(Text text, int availableWidth, Text ellipsis);
	
	public List<OrderedText> splitToLines(Text text, int availableWidth);

}
