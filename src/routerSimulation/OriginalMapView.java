package routerSimulation;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

/**
 * 基本形
 * 
 * */
public class OriginalMapView extends JFrame {
	private boolean isFullScreenMode = false;

	public OriginalMapView() {
		
		for(int i=1; i<=13; i++) {
			String str = "";
			for(int j=1; j<=13; j++) {
				if(Math.abs(i-j) >= 5) {
					str += 0 + ",";
				}
				else if(Math.abs(i-j) == 0) {
					str +=  "100,";
				}
				else if(Math.abs(i-j) == 1) {
					str +=  "50,";
				}
				else if(Math.abs(i-j) == 2) {
					str +=  "20,";
				}
				else if(Math.abs(i-j) == 3) {
					str +=  "10,";
				}
				else if(Math.abs(i-j) == 4) {
					str +=  "1,";
				}
			}
		//	System.out.println(i +","+ str);
		}
		
		// タイトル設定
		setTitle("8号館3期");
		// ウィンドウサイズ設定(タイトルや枠も含んだサイズ)

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (isFullScreenMode && gd.isFullScreenSupported()) {
			setUndecorated(true);
			gd.setFullScreenWindow(this);
		} else {
			System.err.println("Full screen not supported");
			setSize(OriginalCanvas.WIDTH, OriginalCanvas.HEIGHT);
			setVisible(true);
		}
	}

	public static void main(final String[] args) {
		// ウィンドウ生成
		JFrame app = new OriginalMapView();
		// キャンバスを配置
		OriginalCanvas cvs = new OriginalCanvas(getData());
		app.add(cvs);
		app.setVisible(true);
		cvs.repaint();
	}

	/** ポリゴンの座標データ取得 */
	protected static ArrayList<PointDataSet>[] getData() {
		ArrayList<PointDataSet>[] tmpPointData = new ArrayList[256];
		String FS = File.separator;
		String path = System.getProperty("user.home")+FS+"Dropbox"+FS+ "prog" + FS + "javadata"+FS+"input_data"+FS+"cad_data.txt";
		File file = new File(path);
		BufferedReader br = null;

		int[] exclude = {6, 172, 13, 30, 156, 21, 141,159 }; //ダミーのポリゴン
		int[] wall={19, 22, 23, 24, 25, 26, 28, 29 //
						,161,160, 33, 34, 35, 31, 37, 32, 158, 16, 17, 18//ドア
					,20,27,//,80, 84, 86, 90, 92, 96, 98, 102, 104, 118, };// 190,213, 192, 209, 194, 207, 196, 206, 198};//, 4, }
					190,213, 192, 209, 194, 207, 196, 206, 198,  //先生の部屋の壁
					202,203,204,205,208,201,210,200,212,115,
					36,39,38,41,40,45,44,43,42,
					186, 187, 188, 189,
				//	199
					220, 221, 223, 224, 190, 213 ,192, 209, 194, 207, 196, 206, 198
		};
		
	
	//	int[] wall= {220, 221, 223, 224, };//190, 213};//, 192, 209, 194, 207, 196, 206, 198}; //厚い壁
		

		for (int i = 0; i < tmpPointData.length; i++) {
			tmpPointData[i] = new ArrayList<PointDataSet>();
		}
		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			int p_cnt = -1;
			boolean isExclude = false;
			boolean isWall = false;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				isExclude = false;
				isWall = false;
				/* POLYGON と書かれているところは読み取らない */
				for (int i = 0; i < exclude.length; i++) {
					if (p_cnt == exclude[i]) {
						isExclude = true;
					}
				}
				for(int i=0; i<wall.length; i++) {
					if(p_cnt == wall[i]) {
						isWall = true;
					}
				}
				if (!line.matches(".*" + "POLYGON" + ".*")) {
								
					if (!isExclude && !isWall) {
//					if(isWall){
						String[] array = line.split(","); // カンマで分割
						double x, y;
						x = Double.parseDouble(array[0]);
						y = Double.parseDouble(array[1]);
						tmpPointData[p_cnt].add(new PointDataSet(x, y)); // データ保存
					}
				}
				/* 次のPOLYGON を読み込む */
				else {
					p_cnt++;
				}
			}
			fr.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
		return tmpPointData;
	}
}

class OriginalCanvas extends Canvas {
	protected ArrayList<PointDataSet>[] pointData;// = getData();
	final public static int WIDTH = 1280;
	public final static int HEIGHT = 736;
	final double SCALE = 8;
	final double Y_SCALE = (SCALE + 4) * 80;

	protected ArrayList<LineData> lineData = new ArrayList<LineData>();
	protected ArrayList<WallData> wallData = new ArrayList<WallData>();

	
	public OriginalCanvas() {

	}

	public OriginalCanvas(ArrayList<PointDataSet>[] _pointData) {
		this.pointData = _pointData;
		setLineData();
	}

	public void paint(Graphics g) {
		setPoint(g);
//		drawLine(g);
//		drawWall(g);
	}

	private void drawLine(Graphics g) {
		for(int i=0; i<lineData.size(); i++) {
			if( Math.abs(lineData.get(i).getY() -lineData.get(i).getY2()) > 100)
			doDraw(g, i, lineData.get(i).getX(), lineData.get(i).getX2(), lineData.get(i).getY(), lineData.get(i).getY2());
		}
	}
	private void drawWall(Graphics g) {
		System.out.println(wallData.size());
		for(int i=0; i<wallData.size(); i++) {
			doDraw(g, i, wallData.get(i).getXLine().getX(), wallData.get(i).getXLine().getX2(),
					 wallData.get(i).getXLine().getY(), wallData.get(i).getXLine().getY());
//			doDraw(g, i, wallData.get(i).getX2Line().getX(), wallData.get(i).getX2Line().getX2(),
//					 wallData.get(i).getX2Line().getY(), wallData.get(i).getX2Line().getY());
//			doDraw(g, i, wallData.get(i).getYLine().getX(), wallData.get(i).getYLine().getX2(),
//					 wallData.get(i).getYLine().getY(), wallData.get(i).getYLine().getY());
//			doDraw(g, i, wallData.get(i).getY2Line().getX(), wallData.get(i).getY2Line().getX2(),
//					 wallData.get(i).getY2Line().getY(), wallData.get(i).getY2Line().getY());
		}
	}
	
	protected void setPoint(Graphics g) {
		// 座標用変数
		double x, y, x2, y2;
		for (int i = 0; i < pointData.length; i++) {// 画面再描画
			for (int j = 0; j < pointData[i].size(); j++) {
				x = pointData[i].get(j).getX() * SCALE;// + (scale)*10;
				y = pointData[i].get(j).getY() * SCALE - Y_SCALE;
				if (j < pointData[i].size() - 1) {
					x2 = pointData[i].get(j + 1).getX() * SCALE;// + (scale)*10;
					y2 = pointData[i].get(j + 1).getY() * SCALE - Y_SCALE;
				} else {
					x2 = pointData[i].get(0).getX() * SCALE;// + (scale)*10;
					y2 = pointData[i].get(0).getY() * SCALE - Y_SCALE;
				}
				doDraw(g, i, x, x2, y, y2);
			}
		}
	}

	protected void setLineData() {
		int[][] roomPoint = { { 5, 7, 8, 9, 10 }, { 11 }, { 12 },
				{ 80, 84, 86, 90, 92, 96, 98, 102, 104, 118 } };
		int [] intersectWall = {199	,190,213, 192, 209, 194, 207, 196, 206, 198,220, 221, 223, 224};

		// 座標用変数
		double x, y, x2, y2;
		for (int i = 0; i < pointData.length; i++) {// 画面再描画
			ArrayList<LineData> tmp_WallLineData = new ArrayList<LineData>();

			for (int j = 0; j < pointData[i].size(); j++) {
				x = pointData[i].get(j).getX() * SCALE;// + (scale)*10;
				y = pointData[i].get(j).getY() * SCALE - Y_SCALE;
				if (j < pointData[i].size() - 1) {
					x2 = pointData[i].get(j + 1).getX() * SCALE;// + (scale)*10;
					y2 = pointData[i].get(j + 1).getY() * SCALE - Y_SCALE;
				} else {
					x2 = pointData[i].get(0).getX() * SCALE;// + (scale)*10;
					y2 = pointData[i].get(0).getY() * SCALE - Y_SCALE;
				}
				
//				for(int k=0; k<roomPoint.length; k++) {
//					for(int l=0; l<roomPoint[k].length; l++) {
////						if(Arrays.asList(roomPoint[i]).contains(new Integer(j))) 
//						if(roomPoint[k][l] == i) {
//					//		lineData.add(new LineData(-x + WIDTH, y, -x2 + WIDTH, y2));	
//						}
//					}
//				}
//				for(int k=0; k<intersectWall.length; k++) {
//					if(intersectWall[k] == i) {
//						tmp_WallLineData.add(new LineData(-x + WIDTH, y, -x2 + WIDTH, y2));
//					}
//				}
//				if(tmp_WallLineData.size() >0) {
//					lineData.add(tmp_WallLineData.get(0));	
//					lineData.add(tmp_WallLineData.get(1));	
//
//					lineData.add(tmp_WallLineData.get(2));	
//
//					lineData.add(tmp_WallLineData.get(3));	
//
//					wallData.add(new WallData(tmp_WallLineData.get(0),
//							tmp_WallLineData.get(1),
//							tmp_WallLineData.get(2),
//							tmp_WallLineData.get(3)
//							));
//				}
//				
				lineData.add(new LineData(-x + WIDTH, y, -x2 + WIDTH, y2));	

			}
		}
	}

	protected void doDraw(Graphics _g, int index, double _x, double _x2,
			double _y, double _y2) {
		Graphics2D g2 = (Graphics2D) _g;
		g2.draw(new Line2D.Double(-_x + WIDTH, _y, -_x2 + WIDTH, _y2));
//		g2.drawRect((int)-_x + WIDTH, (int)_y, 3, 3);
	}
}

/**
 * ポリゴンの座標データセット
 * */
class PointDataSet {
	private double x, y;

	PointDataSet(double _x, double _y) {
		this.x = _x;
		this.y = _y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}

class PolygonDataSet {
	private int num;
	private double min_x, max_x, min_y, max_y;

	PolygonDataSet(int _num, double _min_x, double _max_x, double _min_y,
			double _max_y) {
		this.num = _num;
		this.min_x = _min_x;
		this.max_x = _max_x;
		this.min_y = _min_y;
		this.max_y = _max_y;
	}

	public double getNum() {
		return num;
	}

	public double getMaxX() {
		return max_x;
	}

	public double getMaxY() {
		return max_y;
	}

	public double getMinX() {
		return min_x;
	}

	public double getMinY() {
		return min_y;
	}
}

class LineData {
	private double x, y, x2, y2;

	public LineData(double _x, double _y, double _x2, double _y2) {
		this.x = _x;
		this.y = _y;
		this.x2 = _x2;
		this.y2 = _y2;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}
}

class WallData {
	private LineData x_line, y_line, x2_line, y2_line;

	public WallData(LineData _x_line, LineData _y_line, LineData _x2_line, LineData _y2_line) {
		this.x_line = _x_line;
		this.y_line = _y_line;
		this.x2_line = _x2_line;
		this.y2_line = _y2_line;
	}

	public LineData getXLine() {
		return x_line;
	}

	public LineData getYLine() {
		return y_line;
	}

	public LineData getX2Line() {
		return x2_line;
	}

	public LineData getY2Line() {
		return y2_line;
	}
}