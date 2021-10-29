package com.g4mesoft.panel.scroll;

import com.g4mesoft.util.GSMathUtil;

public class GSDefaultScrollBarModel extends GSAbstractScrollBarModel {

	private static final float DEFAULT_BLOCK_SCROLL = 20.0f;
	private static final float DEFAULT_MIN_SCROLL   = 0.0f;
	private static final float DEFAULT_MAX_SCROLL   = 100.0f;
	
	private float scroll;
	
	private float minScroll;
	private float maxScroll;
	private float blockScroll;
	
	public GSDefaultScrollBarModel() {
		this(DEFAULT_MIN_SCROLL, DEFAULT_MAX_SCROLL);
	}
	
	public GSDefaultScrollBarModel(float minScroll, float maxScroll) {
		this(minScroll, minScroll, maxScroll);
	}

	public GSDefaultScrollBarModel(float scroll, float minScroll, float maxScroll) {
		this.minScroll = Float.isNaN(minScroll) ? DEFAULT_MIN_SCROLL : minScroll;
		this.maxScroll = Float.isNaN(maxScroll) ? DEFAULT_MAX_SCROLL : maxScroll;
		this.blockScroll = DEFAULT_BLOCK_SCROLL;

		setScroll(scroll);
	}

	@Override
	public float getScroll() {
		return scroll;
	}

	@Override
	public void setScroll(float scroll) {
		if (Float.isNaN(scroll))
			scroll = minScroll;
		
		this.scroll = GSMathUtil.clamp(scroll, minScroll, maxScroll);
		dispatchScrollChanged(this.scroll);
		dispatchValueChanged();
	}

	@Override
	public float getMinScroll() {
		return minScroll;
	}

	@Override
	public void setMinScroll(float minScroll) {
		setScrollInterval(minScroll, maxScroll);
	}

	@Override
	public float getMaxScroll() {
		return maxScroll;
	}

	@Override
	public void setMaxScroll(float maxScroll) {
		setScrollInterval(minScroll, maxScroll);
	}
	
	@Override
	public void setScrollInterval(float minScroll, float maxScroll) {
		this.minScroll = Float.isNaN(minScroll) ?      0.0f : minScroll;
		this.maxScroll = Float.isNaN(maxScroll) ? minScroll : maxScroll;
		
		if (this.minScroll > scroll) {
			setScroll(this.minScroll);
		} else if (this.maxScroll < scroll) {
			setScroll(this.maxScroll);
		} else {
			// setScroll dispatches a value changed event
			dispatchValueChanged();
		}
	}

	@Override
	public float getBlockScroll() {
		return blockScroll;
	}
	
	@Override
	public void setBlockScroll(float blockScroll) {
		this.blockScroll = Math.max(0.0f, blockScroll);
	}
}
