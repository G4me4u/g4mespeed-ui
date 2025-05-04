package com.g4mesoft.ui.renderer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.g4mesoft.ui.access.client.GSITextRendererAccess;
import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.renderer.text.GSText;
import com.g4mesoft.ui.util.GSMathUtil;
import com.g4mesoft.ui.util.GSTextUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;

public class GSBasicRenderer2D implements GSIRenderer2D {

	private static final int LINE_SPACING = 2;
	private static final float DEFAULT_Z_OFFSET = 0.0f;
	
	private final Minecraft client;
	
	private BufferBuilder builder;
	private int mouseX;
	private int mouseY;
	private int viewportWidth;
	private int viewportHeight;
	
	private boolean building;
	private int buildingShape;
	
	private double nextVertexX;
	private double nextVertexY;
	private double nextVertexZ;
	
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
	
	public void begin(BufferBuilder builder, int mouseX, int mouseY, int viewportWidth, int viewportHeight) {
		this.builder = builder;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
	}
	
	public void end() {
		if (building)
			throw new IllegalStateException("Renderer is still building");

		transformStack.clear();
		transform.reset();
		builder = null;

		invalidateClipBounds();
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
	}

	@Override
	public void popMatrix() {
		if (transformStack.isEmpty())
			throw new IllegalStateException("Transform stack is empty!");
		
		transform = transformStack.pop();
		
		invalidateClipBounds();
	}

	@Override
	public void translate(int x, int y) {
		transform.offsetX += x;
		transform.offsetY += y;

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
		setScissor(clipStack.peek());
	}

	@Override
	public GSClipRect popClip() {
		if (clipStack.isEmpty())
			throw new IllegalStateException("Clip stack is empty!");
		
		GSClipRect oldClip = clipStack.pop();
		
		invalidateClipBounds();
		setScissor(clipStack.peek());
		
		return oldClip;
	}
	
	private void setScissor(GSClipRect clip) {
		if (clip != null) {
			Window window = new Window(client.options, client.width, client.height);
			double s = window.getScale();
			int h = client.height;
			
			int x = (int)Math.round(clip.x0 * s);
			int y = h - (int)Math.round(clip.y1 * s);
			int width = (int)Math.round((clip.x1 - clip.x0) * s);
			int height = (int)Math.round((clip.y1 - clip.y0) * s);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(x, y, width, height);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
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

	@Override
	public void fillGradient(int x, int y, int width, int height,
	                         float rtl, float gtl, float btl, float atl,
	                         float rtr, float gtr, float btr, float atr,
	                         float rbl, float gbl, float bbl, float abl,
	                         float rbr, float gbr, float bbr, float abr,
	                         boolean mirror) {
		
		if (building && buildingShape != QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(QUADS);
		
		float x0 = (float)x;
		float y0 = (float)y;
		float x1 = x0 + width;
		float y1 = y0 + height;

		if (mirror) {
			vert(x0, y0, DEFAULT_Z_OFFSET).color(rtl, gtl, btl, atl).next();
			vert(x0, y1, DEFAULT_Z_OFFSET).color(rbl, gbl, bbl, abl).next();
			vert(x1, y1, DEFAULT_Z_OFFSET).color(rbr, gbr, bbr, abr).next();
			vert(x1, y0, DEFAULT_Z_OFFSET).color(rtr, gtr, btr, atr).next();
		} else {
			vert(x0, y1, DEFAULT_Z_OFFSET).color(rbl, gbl, bbl, abl).next();
			vert(x1, y1, DEFAULT_Z_OFFSET).color(rbr, gbr, bbr, abr).next();
			vert(x1, y0, DEFAULT_Z_OFFSET).color(rtr, gtr, btr, atr).next();
			vert(x0, y0, DEFAULT_Z_OFFSET).color(rtl, gtl, btl, atl).next();
		}

		if (!wasBuilding)
			finish();
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		if (building && buildingShape != QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(QUADS);
		
		drawHLine(x, x + width, y, color);
		drawHLine(x, x + width, y + height - 1, color);
		
		drawVLine(x, y + 1, y + height - 1, color);
		drawVLine(x + width - 1, y + 1, y + height - 1, color);

		if (!wasBuilding)
			finish();
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy) {
		drawTexture(texture, x, y, width, height, sx, sy, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, int width, int height, int sx, int sy, float r, float g, float b) {
		drawTexture(texture.getRegion(sx, sy, width, height), x, y, r, g, b);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y) {
		drawTexture(texture, x, y, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawTexture(GSITextureRegion texture, int x, int y, float r, float g, float b) {
		if (building)
			throw new IllegalStateException("Batches are not supported when drawing textures");
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r, g, b, opacity);
		client.textureManager.bind(client.textureManager.load(texture.getTexture().getIdentifier()));

		float x0 = (float)x;
		float y0 = (float)y;
		float x1 = x0 + texture.getRegionWidth();
		float y1 = y0 + texture.getRegionHeight();

		build(QUADS);
		vert(x0, y1, DEFAULT_Z_OFFSET).tex(texture.getU0(), texture.getV1()).next();
		vert(x1, y1, DEFAULT_Z_OFFSET).tex(texture.getU1(), texture.getV1()).next();
		vert(x1, y0, DEFAULT_Z_OFFSET).tex(texture.getU1(), texture.getV0()).next();
		vert(x0, y0, DEFAULT_Z_OFFSET).tex(texture.getU0(), texture.getV0()).next();
		finish();

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
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
		if (building && buildingShape != QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(QUADS);
		
		int n = (y1 - y0) / (length + spacing);
		
		for (int yl = 0; yl <= n; yl++) {
			int yl0 = y0 + yl * (length + spacing);
			int yl1 = Math.min(yl0 + length, y1);
			drawVLine(x, yl0, yl1, color);
		}
		
		if (!wasBuilding)
			finish();
	}
	
	@Override
	public void drawDottedHLine(int x0, int x1, int y, int length, int spacing, int color) {
		if (building && buildingShape != QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(QUADS);
		
		int n = (x1 - x0) / (length + spacing);
		
		for (int xl = 0; xl <= n; xl++) {
			int xl0 = x0 + xl * (length + spacing);
			int xl1 = Math.min(xl0 + length, x1);
			drawHLine(xl0, xl1, y, color);
		}
		
		if (!wasBuilding)
			finish();
	}

	@Override
	public int getTextAscent() {
		return client.textRenderer.fontHeight - 2;
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
	public float getTextWidth(CharSequence text) {
		return client.textRenderer.getWidth(text.toString());
	}

	@Override
	public float getTextWidthNoStyle(CharSequence text) {
		try {
			((GSITextRendererAccess)client.textRenderer).setEscapeTextFlag(true);
			return getTextWidth(text);
		} finally {
			((GSITextRendererAccess)client.textRenderer).setEscapeTextFlag(false);
		}
	}

	@Override
	public void drawText(CharSequence text, int x, int y, int color, boolean shadowed) {
		if (building)
			throw new IllegalStateException("Batches are not supported for drawing text");
		
		int alpha = (int)((color >>> 24) * opacity);
		color = (alpha << 24) | (color & 0x00FFFFFF);
		
		x += transform.offsetX;
		y += transform.offsetY;
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		if (shadowed) {
			client.textRenderer.drawWithShadow(text.toString(), x, y, color);
		} else {
			client.textRenderer.draw(text.toString(), x, y, color);
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	@Override
	public void drawTextNoStyle(CharSequence text, int x, int y, int color, boolean shadowed) {
		try {
			((GSITextRendererAccess)client.textRenderer).setEscapeTextFlag(true);
			drawText(text, x, y, color, shadowed);
		} finally {
			((GSITextRendererAccess)client.textRenderer).setEscapeTextFlag(false);
		}
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
	public GSText trimString(GSText text, int availableWidth, GSText ellipsis) {
		return GSTextUtil.literal(trimString(text.build(true), availableWidth, ellipsis.build(true)));
	}
	
	@Override
	public void build(int shape) {
		if (building)
			throw new IllegalStateException("Already building!");
		
		builder.start(shape);
		
		buildingShape = shape;
		building = true;
	}

	@Override
	public GSBasicRenderer2D vert(float x, float y, float z) {
		nextVertexX = x + transform.offsetX;
		nextVertexY = y + transform.offsetY;
		nextVertexZ = z;
		return this;
	}

	@Override
	public GSBasicRenderer2D color(float r, float g, float b, float a) {
		builder.color(r, g, b, a * opacity);
		return this;
	}

	@Override
	public GSBasicRenderer2D tex(float u, float v) {
		builder.texture(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer2D next() {
		builder.vertex(nextVertexX, nextVertexY, nextVertexZ);
		return this;
	}
	
	@Override
	public void finish() {
		if (!building)
			throw new IllegalStateException("Not building!");
		
		builder.end();
		building = false;
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
