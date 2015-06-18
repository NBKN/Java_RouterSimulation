package routerSimulation;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * 部屋だけ
 * 
 * */
public class RoomOnly extends OriginalMapView {
	public RoomOnly() {
		
	}
	public static  void main(final String[] args) {
		// ウィンドウ生成
		RoomOnly app = new RoomOnly();
		RoomOnlyCanvas cvs = new RoomOnlyCanvas(getData());
		app.add(cvs);
		app.setVisible(true);
		cvs.repaint();
	}
}

class RoomOnlyCanvas extends OriginalCanvas {
	static int[] roomPoint = { 11 ,12, 5, 7, 8, 9, 10, 102, 104, 118, 102, 118,
		98, 96, 92, 90, 86, 84, 80 };
	
	public RoomOnlyCanvas(ArrayList<PointDataSet>[] _pointData) {
		this.pointData = _pointData;
		checkRoomPoint(pointData);
	}

	protected void doDraw(Graphics _g, int index, double _x, double _x2, double _y, double _y2) {
		 for(int k=0; k<roomPoint.length; k++) {
			 if(index == roomPoint[k]) {
				 Graphics2D g2 = (Graphics2D)_g;
				 g2.draw(new Line2D.Double(-_x + WIDTH, _y, -_x2 + WIDTH, _y2));
			 }
		 }
	}
	

	/** 部屋の座標を取得 */
	private static void checkRoomPoint(ArrayList<PointDataSet>[] _pointData) {
		System.out.println("polygon," + "min_x," + "max_x," + "min_y,"
				+ "max_y");
		for (int i = 0; i < _pointData.length; i++) {
			for (int j = 0; j < roomPoint.length; j++) {
				if (i == roomPoint[j]) {
					double min_x = 10000, max_x = 0, min_y = 10000, max_y = 0;
					for (int k = 0; k < _pointData[i].size(); k++) {
						double x = _pointData[i].get(k).getX() * 8;
						double y = _pointData[i].get(k).getY() * 8 - 12 * 80;
						x = -x + 1280;
						if (max_x < x) {
							max_x = x;
						}
						if (min_x > x) {
							min_x = x;
						}
						if (max_y < y) {
							max_y = y;
						}
						if (min_y > y) {
							min_y = y;
						}
					}
					System.out.println(i + "," + min_x + "," + max_x + ","
							+ min_y + "," + max_y);
				}
			}
		}
	}
}