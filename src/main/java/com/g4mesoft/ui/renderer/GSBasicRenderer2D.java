package com.g4mesoft.ui.renderer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.g4mesoft.ui.mixin.client.GSIDrawContextAccess;
import com.g4mesoft.ui.mixin.client.GSIGameRendererAccess;
import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.util.GSMathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

public class GSBasicRenderer2D implements GSIRenderer2D {

	private static final int LINE_SPACING = 2;
	private static final float DEFAULT_Z_OFFSET = 0.0f;
	
	private static final GSTexture MENU_BACKGROUND_TEXTURE = new GSTexture(Screen.MENU_BACKGROUND_TEXTURE, 16, 16);
	private static final GSTexture INWORLD_MENU_BACKGROUND_TEXTURE = new GSTexture(Identifier.ofVanilla("textures/gui/inworld_menu_background.png"), 16, 16);
	private static final CubeMapRenderer PANORAMA_RENDERER = new CubeMapRenderer(Identifier.ofVanilla("textures/gui/title/background/panorama"));
	private static final RotatingCubeMapRenderer ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(PANORAMA_RENDERER);

	private static final Identifier BLUR_EFFECT_IDENTIFIER = Identifier.ofVanilla("blur");
	
	private final MinecraftClient client;
	
	private DrawContext context;
	private VertexConsumerProvider.Immediate vertexConsumers;
	private MatrixStack matrixStack;
	private int mouseX;
	private int mouseY;
	private int viewportWidth;
	private int viewportHeight;
	
	private RenderLayer currentRenderLayer;
	private VertexConsumer currentVertexConsumer;
	
	private GSTransform2D transform;
	private final Deque<GSTransform2D> transformStack;
	private final Deque<GSClipRect> clipStack;
	private float opacity;
	private final Deque<Float> opacityStack;
	
	private GSRectangle cachedClippedBounds;

	private long lastPanoramaTickTime;
	
	public GSBasicRenderer2D(MinecraftClient client) {
		this.client = client;
		
		transform = new GSTransform2D();
		transformStack = new ArrayDeque<>();
		clipStack = new ArrayDeque<>();
		opacity = 1.0f;
		opacityStack = new ArrayDeque<>();
		
		cachedClippedBounds = null;
	
		lastPanoramaTickTime = System.currentTimeMillis();
	}
	
	public void begin(DrawContext context, int mouseX, int mouseY, int viewportWidth, int viewportHeight) {
		this.context = context;
		this.vertexConsumers = ((GSIDrawContextAccess)context).gs_getVertexConsumers();
		this.matrixStack = context.getMatrices();
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
	}
	
	public void end() {
		if (isBuilding())
			throw new IllegalStateException("Renderer is still building");
		if (!clipStack.isEmpty())
			throw new IllegalStateException("Clip stack is not empty");
		
		transformStack.clear();
		transform.reset();
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
		
		matrixStack.push();
	}

	@Override
	public void popMatrix() {
		if (transformStack.isEmpty())
			throw new IllegalStateException("Transform stack is empty!");
		
		transform = transformStack.pop();
		matrixStack.pop();
		
		invalidateClipBounds();
	}
	
	@Override
	public void identity() {
		matrixStack.loadIdentity();
		transform.offsetX = transform.offsetY = 0;
	}

	@Override
	public void translate(int x, int y) {
		transform.offsetX += x;
		transform.offsetY += y;

		matrixStack.translate(x, y, 0.0f);
		
		invalidateClipBounds();
	}
	
	@Override
	public void translateDepth(float z) {
		matrixStack.translate(0.0f, 0.0f, z);
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
			Window window = MinecraftClient.getInstance().getWindow();
			double s = window.getScaleFactor();
			int h = window.getFramebufferHeight();
			
			int x = (int)Math.round(clip.x0 * s);
			int y = h - (int)Math.round(clip.y1 * s);
			int width = (int)Math.round((clip.x1 - clip.x0) * s);
			int height = (int)Math.round((clip.y1 - clip.y0) * s);
			RenderSystem.enableScissor(x, y, width, height);
		} else {
			RenderSystem.disableScissor();
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
		
		if (isBuilding() && !isBuilding(DrawMode.QUADS, VertexFormats.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");

		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
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
		if (isBuilding() && !isBuilding(DrawMode.QUADS, VertexFormats.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");

		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
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
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported when drawing textures");
		
		build(RenderLayer.getGuiTextured(texture.getTexture().getIdentifier()));

		float x0 = (float)x;
		float y0 = (float)y;
		float x1 = x0 + texture.getRegionWidth();
		float y1 = y0 + texture.getRegionHeight();
		
		vert(x0, y1, DEFAULT_Z_OFFSET).tex(texture.getU0(), texture.getV1()).color(r, g, b).next();
		vert(x1, y1, DEFAULT_Z_OFFSET).tex(texture.getU1(), texture.getV1()).color(r, g, b).next();
		vert(x1, y0, DEFAULT_Z_OFFSET).tex(texture.getU1(), texture.getV0()).color(r, g, b).next();
		vert(x0, y0, DEFAULT_Z_OFFSET).tex(texture.getU0(), texture.getV0()).color(r, g, b).next();

		finish();
	}

	public void legacyDrawGuiTexture(Identifier texture, int x, int y, int w, int h) {
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported when drawing gui textures");

		context.drawGuiTexture(RenderLayer::getGuiTextured, texture, x, y, w, h);
		context.draw();

		// Note: seems to disable scissor test.
		setScissor(clipStack.peek());
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
		if (isBuilding() && !isBuilding(DrawMode.QUADS, VertexFormats.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");

		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
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
		if (isBuilding() && !isBuilding(DrawMode.QUADS, VertexFormats.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");

		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		int n = (x1 - x0) / (length + spacing);
		
		for (int xl = 0; xl <= n; xl++) {
			int xl0 = x0 + xl * (length + spacing);
			int xl1 = Math.min(xl0 + length, x1);
			drawHLine(xl0, xl1, y, color);
		}
		
		if (!wasBuilding)
			finish();
	}
	
	private float getPanoramaTickDelta() {
		long l = System.currentTimeMillis();
		float f = (float)(l - lastPanoramaTickTime) / 50.0f;
		lastPanoramaTickTime = l;
		return (f > 7.0f) ? 0.5f : f;
	}
	
	@Override
	public void drawMenuBackground(boolean inWorld) {
		if (!inWorld)
			drawPanoramaBackground();
		// Apply blur
		applyBlur(client.options.getMenuBackgroundBlurrinessValue());
		// Draw darkening texture
		pushMatrix();
		identity();
		GSTexture darkening = inWorld ? INWORLD_MENU_BACKGROUND_TEXTURE : MENU_BACKGROUND_TEXTURE;
		drawTexture(darkening.getRegion(0, 0, viewportWidth, viewportHeight), 0, 0);
		popMatrix();
	}
	
	@Override
	public void drawPanoramaBackground() {
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported while drawing panorama!");
		pushMatrix();
		identity();
		// Note: context uses the same matrix stack as we do.
		ROTATING_PANORAMA_RENDERER.render(context, viewportWidth, viewportHeight, 1.0f, getPanoramaTickDelta());
		popMatrix();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void applyBlur(float radius) {
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported while blurring!");
		
		// See client.gameRenderer.renderBlur(...)
		PostEffectProcessor blurPostProcessor = client.getShaderLoader().loadPostEffect(BLUR_EFFECT_IDENTIFIER, DefaultFramebufferSet.MAIN_ONLY);
		if (blurPostProcessor != null && radius >= 1.0f) {
			// Finish writing frame buffer.
			blurPostProcessor.render(this.client.getFramebuffer(),
					((GSIGameRendererAccess)client.gameRenderer).getPool(),
					pass -> pass.setUniform("Radius", radius));
		}
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
	public float getTextWidth(String text) {
		return client.textRenderer.getWidth(text);
	}

	@Override
	public float getTextWidthNoStyle(CharSequence text) {
		return client.textRenderer.getWidth(new GSCharSequenceOrderedText(text));
	}

	@Override
	public void drawText(String text, int x, int y, int color, boolean shadowed) {
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported for drawing text");

		int alpha = (int)((color >>> 24) * opacity);
		color = (alpha << 24) | (color & 0x00FFFFFF);
		
		context.drawText(client.textRenderer, text, x, y, color, shadowed);
		// Note: immediately draw to handle scissor.
		context.draw();
	}
	
	@Override
	public void drawTextNoStyle(CharSequence text, int x, int y, int color, boolean shadowed) {
		drawText(new GSCharSequenceOrderedText(text), x, y, color, shadowed);
	}
	
	@Override
	public float getTextWidth(OrderedText text) {
		return client.textRenderer.getWidth(text);
	}

	@Override
	public void drawText(OrderedText text, int x, int y, int color, boolean shadowed) {
		if (isBuilding())
			throw new IllegalStateException("Batches are not supported for drawing text");
		
		int alpha = (int)((color >>> 24) * opacity);
		color = (alpha << 24) | (color & 0x00FFFFFF);

		context.drawText(client.textRenderer, text, x, y, color, shadowed);
		// Note: immediately draw to handle scissor.
		context.draw();
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
	public OrderedText trimString(Text text, int availableWidth, Text ellipsis) {
		if (getTextWidth(text) <= availableWidth)
			return text.asOrderedText();
		
		availableWidth -= (int)Math.ceil(getTextWidth(ellipsis));
		
		StringVisitable trimmed = client.textRenderer.trimToWidth(text, availableWidth);
		StringVisitable result = StringVisitable.concat(trimmed, ellipsis);
		
		return Language.getInstance().reorder(result);
	}
	
	@Override
	public List<OrderedText> splitToLines(Text text, int availableWidth) {
		return client.textRenderer.wrapLines(text, availableWidth);
	}
	
	@Override
	public void build(DrawMode drawMode, VertexFormat format) {
		if (drawMode == QUADS && format == VertexFormats.POSITION_COLOR) {
			build(GSRenderLayers.GUI);
		} else {
			throw new IllegalArgumentException("Unsupported draw mode and vertex format!");
		}
	}
	
	@Override
	public void build(RenderLayer renderLayer) {
		if (isBuilding())
			throw new IllegalStateException("Already building!");

		currentRenderLayer = renderLayer;
		// Retrieve appropriate buffer for render layer.
		currentVertexConsumer = vertexConsumers.getBuffer(renderLayer);
	}

	@Override
	public GSBasicRenderer2D vert(float x, float y, float z) {
		currentVertexConsumer.vertex(x + transform.offsetX, y + transform.offsetY, z);
		return this;
	}

	@Override
	public GSBasicRenderer2D color(float r, float g, float b, float a) {
		currentVertexConsumer.color(r, g, b, a * opacity);
		return this;
	}

	@Override
	public GSBasicRenderer2D tex(float u, float v) {
		currentVertexConsumer.texture(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer2D next() {
		// Note: BufferBuilder#next has been removed.
		return this;
	}
	
	@Override
	public void finish() {
		if (!isBuilding())
			throw new IllegalStateException("Not building!");
	
		// Draw immediately to respect rendering order.
		vertexConsumers.draw(currentRenderLayer);
		
		currentRenderLayer = null;
		currentVertexConsumer = null;
	}

	@Override
	public boolean isBuilding() {
		return currentRenderLayer != null;
	}
	
	@Override
	public boolean isBuilding(DrawMode drawMode, VertexFormat format) {
		if (!isBuilding())
			return false;
		if (currentRenderLayer.getDrawMode() != drawMode)
			return false;
		if (!currentRenderLayer.getVertexFormat().equals(format))
			return false;
		return true;
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