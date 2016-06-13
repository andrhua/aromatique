package com.kekonyan.aromatique.game.tiled.core;

import com.kekonyan.aromatique.game.tiled.util.Dimension;

public class Rectangle {

	public int x;
	public int y;
	public int width;
	public int height;

	public Rectangle(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Rectangle() {
		// TODO Auto-generated constructor stub
	}

	public Rectangle(Rectangle r) {
		setBounds(r);
	}

	public Rectangle(int x2, int y2, int width2, int height2) {
		x = x2;
		y = y2;
		width = width2;
		height = height2;
	}

	public Rectangle(Dimension tileDimensions) {
		this.width = tileDimensions.width;
		this.height = tileDimensions.height;
	}

	public void setBounds(Rectangle r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}

	public boolean contains(int x2, int y2) {
        if ((width | height) < 0) {
            return false;
        }
        if (x2 < x || y2 < y) {
            return false;
        }
        int w = x + width;
        int h = y + height;
        return ((w < x || w > x2) && (h < y || h > y2));
	}

	public void translate(int dx, int dy) {
		x += dx;
		y += dy;
		
	}

}
