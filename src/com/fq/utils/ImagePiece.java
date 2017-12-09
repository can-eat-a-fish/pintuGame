package com.fq.utils;

import android.graphics.Bitmap;

public class ImagePiece {

	private int index;
	private Bitmap bitmap;
	public ImagePiece() {
		// TODO 自动生成的构造函数存根
	}
	public ImagePiece(int index, Bitmap bitmap) {
		//super();
		this.index = index;
		this.bitmap = bitmap;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	@Override
	public String toString() {
		return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
	}
	

}
