package routerSimulation;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/** 座標を見えるように */
public class PolygonViewMap extends OriginalMapView {
	public PolygonViewMap() {
	}

	public static void main(final String[] args) {
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		// ウィンドウ生成
		JFrame app = new PolygonViewMap();
		PolygonViewCanvas cvs = new PolygonViewCanvas(getData());
		app.add(cvs);
		app.setVisible(true);
		cvs.repaint();
	}
}

class PolygonViewCanvas extends OriginalCanvas {
	protected ArrayList<PolygonDataSet> polygonData = new ArrayList<PolygonDataSet>();
	protected int point_x, point_y;// マウスの位置

	public PolygonViewCanvas() {
	}

	public PolygonViewCanvas(ArrayList<PointDataSet>[] _pointData) {
		this.pointData = _pointData;
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK
				| AWTEvent.MOUSE_EVENT_MASK);
		setPolygonMinMax();
	}

	// 以下に描画したい内容を書く
	public void paint(Graphics g) {
		showText(g);
		setPoint(g);
	}

	protected void showText(Graphics g) {
		// マウスの位置に座標を表示
		g.drawString("マウスの座標", 1150, 80);
		g.drawString("x:" + point_x + " y:" + point_y, 1150, 100);
		g.drawString("ポリゴン番号: " + getPolygon(point_x, point_y), 1150, 120);
		
		
		g.drawString("ori_x:" + (WIDTH-point_x)/SCALE + "\n ori_y:" + (point_y+Y_SCALE)/SCALE, 1000, 500);

	}


	// マウスが移動されと、自動で実行されるメソッド
	protected void processMouseMotionEvent(MouseEvent e) {
		point_x = e.getX();// マウスの横位置をセット
		point_y = e.getY();// マウスの位置をセット
		repaint();// 再描画
	}

	/**
	 * 各ポリゴンの最小最大座標の取得
	 * */
	protected void setPolygonMinMax() {
		for (int i = 0; i < pointData.length; i++) {
			double min_x = 10000, max_x = 0, min_y = 10000, max_y = 0;
			for (int j = 0; j < pointData[i].size(); j++) {
				double x = pointData[i].get(j).getX() * SCALE;// + (scale)*10;
				double y = pointData[i].get(j).getY() * SCALE - Y_SCALE;
				x = -x + WIDTH;
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
			polygonData.add(new PolygonDataSet(i, min_x, max_x, min_y, max_y));
		}
	}

	/**
	 * マウスが示しているポリゴン番号を取得
	 * 
	 * */
	protected int getPolygon(double point_x, double point_y) {
		int nearPolygon = 0;
		double m_dis_x = 100000, m_dis_x2 = 100000, m_dis_y = 100000, m_dis_y2 = 100000;
		for (int i = 0; i < polygonData.size(); i++) {
			double dis_x = 0, dis_x2 = 0, dis_y = 0, dis_y2 = 0;
			dis_x = point_x - polygonData.get(i).getMinX();
			dis_y = point_y - polygonData.get(i).getMinY();
			dis_x2 = polygonData.get(i).getMinX() - point_x;
			dis_y2 = polygonData.get(i).getMinY() - point_y;

			/* 　マウスの座標が図形の中にあるか */
			if (polygonData.get(i).getMinX() <= point_x
					&& point_x <= polygonData.get(i).getMaxX()
					&& polygonData.get(i).getMinY() <= point_y
					&& point_y <= polygonData.get(i).getMaxY()) {

				/* その中で一番近い図形 */
				if (dis_x < m_dis_x && dis_x2 < m_dis_x2 && dis_y < m_dis_y
						&& dis_y2 < m_dis_y2) {
					m_dis_x = dis_x;
					m_dis_x2 = dis_x2;
					m_dis_y = dis_y;
					m_dis_y2 = dis_y2;
					nearPolygon = i;
				}
			}
		}
		return nearPolygon;
	}
}
