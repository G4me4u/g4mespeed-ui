package com.g4mesoft.ui.panel.dialog;

import com.g4mesoft.ui.util.GSTextUtil;

import net.minecraft.text.Text;

public final class GSConfirmOption implements Comparable<GSConfirmOption> {

	public static final GSConfirmOption YES = new GSConfirmOption(
		GSTextUtil.translatable("panel.confirmDialog.yes"),
		GSEConfirmOptionPlacement.RIGHT
	);
	public static final GSConfirmOption NO = new GSConfirmOption(
		GSTextUtil.translatable("panel.confirmDialog.no"),
		GSEConfirmOptionPlacement.RIGHT
	);
	public static final GSConfirmOption CANCEL = new GSConfirmOption(
		GSTextUtil.translatable("panel.confirmDialog.cancel"),
		GSEConfirmOptionPlacement.RIGHT
	);
	public static final GSConfirmOption OK = new GSConfirmOption(
		GSTextUtil.translatable("panel.confirmDialog.ok"),
		GSEConfirmOptionPlacement.CENTER
	);
	
	private final Text text;
	private final GSEConfirmOptionPlacement placement;

	public GSConfirmOption(Text text) {
		this(text, GSEConfirmOptionPlacement.RIGHT);
	}
	
	public GSConfirmOption(Text text, GSEConfirmOptionPlacement placement) {
		this.text = text;
		this.placement = placement;
	}
	
	public Text getText() {
		return text;
	}
	
	public GSEConfirmOptionPlacement getPlacement() {
		return placement;
	}

	@Override
	public int compareTo(GSConfirmOption other) {
		return Integer.compare(placement.getOrder(), other.placement.getOrder());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof GSConfirmOption) {
			GSConfirmOption other = (GSConfirmOption)obj;
			if (placement != other.placement)
				return false;
			return text.equals(other.text);
		}
		return false;
	}
}
