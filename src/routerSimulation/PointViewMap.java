package routerSimulation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class PointViewMap extends PolygonViewMap {
	public PointViewMap() {

	}

	public static void main(final String[] args) {
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		// ウィンドウ生成
		JFrame app = new PolygonViewMap();
		final PointViewCanvas cvs = new PointViewCanvas(getData());
		app.add(cvs);
		cvs.repaint();

		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("点の場所");
		JMenu menu2 = new JMenu("間隔");
		menubar.add(menu1);
		menubar.add(menu2);
		JMenuItem menuitem1 = new JMenuItem("全体");
		JMenuItem menuitem2 = new JMenuItem("部屋のみ");
		JMenuItem menuitem2_2 = new JMenuItem("現実的");

		JMenuItem menuitem3 = new JMenuItem("0.5m");
		JMenuItem menuitem4 = new JMenuItem("1m");
		JMenuItem menuitem5 = new JMenuItem("2m");

		menu1.add(menuitem1);
		menu1.add(menuitem2);
		menu1.add(menuitem2_2);
		menu2.add(menuitem3);
		menu2.add(menuitem4);
		menu2.add(menuitem5);
		app.setJMenuBar(menubar);
		app.setVisible(true);

		menuitem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.mesh_type = 0;
				cvs.init();
				cvs.repaint();
			}
		});

		menuitem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.mesh_type = 1;
				cvs.init();
				cvs.repaint();
			}
		});

		menuitem2_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.mesh_type = 2;
				cvs.init();
				cvs.repaint();
			}
		});

		menuitem3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.C_MULTI = 1;
				cvs.init();
				cvs.repaint();
			}
		});

		menuitem4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.C_MULTI = 2;
				cvs.init();
				cvs.repaint();
			}
		});
		menuitem5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cvs.C_MULTI = 4;
				cvs.init();
				cvs.repaint();
			}
		});
	}
}

class PointViewCanvas extends PolygonViewCanvas {
	
	protected int mesh_type = 2;
	
	protected int[][] roomPoint = { { 5, 7, 8, 9, 10 }, { 11 }, { 12 },
			{ 80, 84, 86, 90, 92, 96, 98, 102, 104, 118 } };
	final protected double HALF_METER = 11.5;
	final protected double MAX_DIR = HALF_METER * 40;

	protected double C_MULTI = 2;
	protected double U_MULTI = 2;
	protected double C_INTERVAL = HALF_METER * C_MULTI;
	protected double U_INTERVAL = HALF_METER * U_MULTI;

	final protected double[] ROOM_WIDTH = { 675, 1354, 1509, 309 };
	final protected double[] ROOM_HEIGHT = { 1200, 326, 326, 675 };
	final protected double[] DIV_X = { 6, 13, 15, 3 };
	final protected double[] DIV_Y = { 12, 3, 3, 6 };

	protected ArrayList<CandidatePointData>[] roomOnlyCPoint = new ArrayList[17];// <CandidatePointData>();

	protected ArrayList<CandidatePointData>[] userPoint = new ArrayList[17];// <CandidatePointData>();

	// protected CandidatePointData[][] userPoint = new
	// CandidatePointData[1000][1000];
	protected CandidatePointData[][] allCPoint = new CandidatePointData[1000][1000];
	protected CandidatePointData rooterPoint = new CandidatePointData();

	protected int cPointCnt_i = 0, cPointCnt_j = 0;
	protected double rPoint_i = 0, rPoint_j;
	protected int userPointCnt_i = 0, userPointCnt_j;

	protected double max_x = 0, max_y = 0, min_x = 100000, min_y = 100000;

	protected ArrayList<RoomPointData> roomPointData = new ArrayList<RoomPointData>();

	protected boolean isRooter = false;

	protected String folderPath = "/Users/Ken/Dropbox/javadata/output_data/"
			+ "user" + (int) (U_MULTI / 2) + "m_candi" + (int) (C_MULTI / 2)
			+ "m/";
	protected String pointDataPath = folderPath + "point_data/";
	protected String cplexDataPath = folderPath + "cplex_data/";

	public PointViewCanvas(ArrayList<PointDataSet>[] _pointData) {
		super(_pointData);
		this.pointData = _pointData;
		makeFolder();
		getMinAndMax();
		setLineData();
		getRoomPoint();
		setUserPoint();
		init();
	}

	public void init() {
		C_INTERVAL = HALF_METER * C_MULTI;
		if (mesh_type == 0) {
			setCandidatePoint_All();
		} else if (mesh_type == 1) {
			setCandidatePoint_Room();
		} else if (mesh_type == 2) {
			setCandidatePoint_Reality();
		}
	}
	
	
	/* 子クラスで使うから消さない */
	protected void makeFolder() {
		
	}

	// 以下に描画したい内容を書く
	public void paint(Graphics g) {
		setPoint(g);
		
		if (isRooter) {
			g.setColor(Color.black);
			checkIntersect(rooterPoint, g);
			drawRooterPoint(g, rooterPoint);
		}
		else {
			drawCandidatePoint(g);
		}
	}

	/**
	 * 描写する範囲の最大最小を取得
	 * 
	 * */
	private void getMinAndMax() {
		max_x = polygonData.get(9).getMaxX();
		max_y = polygonData.get(9).getMaxY();
		min_x = polygonData.get(80).getMinX();
		min_y = polygonData.get(80).getMinY();
	}

	/**
	 * ルータを描写する
	 * 
	 * */
	protected void drawRooterPoint(Graphics _g, CandidatePointData rooterPoint) {
		// _g.setColor(Color.GREEN);
		Graphics2D g2 = (Graphics2D) _g;
	//	_g.setColor(Color.RED);
		BasicStroke wideStroke = new BasicStroke(10.0f);
		g2.setStroke(wideStroke);
		AlphaComposite composite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f);
		// アルファ値をセット（以後の描画は半透明になる）
		g2.setComposite(composite);
		g2.fillOval((int) rooterPoint.getX() - 7, (int) rooterPoint.getY() - 7,
				15, 15);
	}

	/**
	 * 候補点の描写
	 */
	private void drawCandidatePoint(Graphics _g) {
		_g.setColor(Color.black);
		Graphics2D g2 = (Graphics2D) _g;
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);

		if (mesh_type == 0) {
			for (int i = 0; i < cPointCnt_i; i++) {
				for (int j = 0; j < cPointCnt_j; j++) {
					g2.drawRect((int) allCPoint[i][j].getX(),
							(int) allCPoint[i][j].getY(), 3, 3);
				}
			}
		} else if (mesh_type >= 1) {
			for (int i = 0; i < roomOnlyCPoint.length; i++) {
				for (int j = 0; j < roomOnlyCPoint[i].size(); j++) {
					g2.drawRect((int) roomOnlyCPoint[i].get(j).getX(),
							(int) roomOnlyCPoint[i].get(j).getY(), 3, 3);
				}
			}
		}
	}

	/**
	 * 利用点の設置
	 * 
	 */
	/*
	 * protected void setUserPoint() { for (double i = min_x; i <= max_x; i +=
	 * U_INTERVAL) { userPointCnt_j = 0; for (double j = min_y; j <= max_y; j +=
	 * U_INTERVAL) { userPoint[userPointCnt_i][userPointCnt_j] = new
	 * CandidatePointData( i, j); userPointCnt_j++; } userPointCnt_i++; } }
	 */
	/**
	 * 利用点の設置
	 * 
	 */
	
//	protected void drawUserPoint(Graphics _g) {
//		 _g.setColor(Color.blue);
//		Graphics2D g2 = (Graphics2D) _g;
//		BasicStroke wideStroke = new BasicStroke(4.0f);
//		g2.setStroke(wideStroke);
//		AlphaComposite composite = AlphaComposite.getInstance(
//				AlphaComposite.SRC_OVER, 0.5f);
//		for(int i=0; i<roomPointData.size(); i++) {
//			for(int j=0; j<userPoint[i].size(); j++) 
//		g2.drawRect((int) userPoint[i].get(j).getX(),
//				(int) userPoint[i].get(j).getY(), 3, 3);
//		}
//	}
	protected void setUserPoint() {
		for (int k = 0; k < roomPointData.size(); k++) {
			int type = 0;
			if (userPoint[k] != null) {
				userPoint[k].clear();
			}
			double r_max_x = roomPointData.get(k).getMaxX();
			double r_max_y = roomPointData.get(k).getMaxY();
			double r_min_x = roomPointData.get(k).getMinX();
			double r_min_y = roomPointData.get(k).getMinY();
			for (int l = 0; l < roomPoint.length; l++) {
				for (int m = 0; m < roomPoint[l].length; m++) {
					if (roomPointData.get(k).getPolygonNum() == roomPoint[l][m]) {
						type = l;
					}
				}
			}
			double interval_x = (r_max_x - r_min_x)
					/ (DIV_X[type] * (2.0 / U_MULTI));
			double interval_y = (r_max_y - r_min_y)
					/ (DIV_Y[type] * (2.0 / U_MULTI));

			userPoint[k] = new ArrayList<CandidatePointData>();

			for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER; i += interval_x) {
				for (double j = r_min_y + HALF_METER; j <= r_max_y - HALF_METER; j += interval_y) {
					if(type == 0 && j+interval_y >= r_max_y - HALF_METER &&
							(i+interval_x >= r_max_x - HALF_METER
							|| (i == r_min_x + HALF_METER))){
						
					}else
						userPoint[k].add(new CandidatePointData(i, j));
				}
			}
		}
	}

	/**
	 * 候補点の設定 全体
	 * 
	 * */
	protected void setCandidatePoint_All() {
		cPointCnt_j = 0;
		cPointCnt_i = 0;
		for (double i = min_x; i <= max_x; i += C_INTERVAL) {
			cPointCnt_j = 0;
			for (double j = min_y; j <= max_y; j += C_INTERVAL) {
				allCPoint[cPointCnt_i][cPointCnt_j] = new CandidatePointData(i,
						j);

				cPointCnt_j++;
			}
			cPointCnt_i++;
		}
	}

	protected void getRoomPoint() {
		String FS = File.separator;
		String path = System.getProperty("user.home") + FS + "Dropbox" + FS + "prog" + FS 
				+ "javadata" + FS + "input_data" + FS + "room_point.csv";
		File file = new File(path);
		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			br.readLine();
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				String[] array = line.split(",");
				int p_num = Integer.parseInt(array[0]);
				double[] point = new double[4];
				for (int i = 0; i < 4; i++) {
					point[i] = Double.parseDouble(array[i + 1]);
				}
				roomPointData.add(new RoomPointData(p_num, point[0], point[1],
						point[2], point[3]));
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// fileInputStream = openFileInput("all_dbm_map.csv");
		catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 候補点の設定 部屋のみ
	 * 
	 * */
	protected void setCandidatePoint_Room() {
		for (int k = 0; k < roomPointData.size(); k++) {
			int type = 0;
			if (roomOnlyCPoint[k] != null) {
				roomOnlyCPoint[k].clear();
			}
			double r_max_x = roomPointData.get(k).getMaxX();
			double r_max_y = roomPointData.get(k).getMaxY();
			double r_min_x = roomPointData.get(k).getMinX();
			double r_min_y = roomPointData.get(k).getMinY();
			for (int l = 0; l < roomPoint.length; l++) {
				for (int m = 0; m < roomPoint[l].length; m++) {
					if (roomPointData.get(k).getPolygonNum() == roomPoint[l][m]) {
						type = l;
					}
				}
			}
			double interval_x = (r_max_x - r_min_x)
					/ (DIV_X[type] * (2.0 / C_MULTI));
			double interval_y = (r_max_y - r_min_y)
					/ (DIV_Y[type] * (2.0 / C_MULTI));

			roomOnlyCPoint[k] = new ArrayList<CandidatePointData>();

			if (k < 5) {
				for (double i = r_min_x + HALF_METER * (C_MULTI / 2); i <= r_max_x
						- HALF_METER * (C_MULTI / 2); i += interval_x) {
					for (double j = r_min_y + HALF_METER * (C_MULTI / 2); j <= r_max_y
							- HALF_METER * (C_MULTI / 2); j += interval_y) {

						roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						// cPintData2[cPointCnt_i][cPointCnt_j] = new
						// CandidatePointData(i, j);

					}
				}
			} else {
				for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER; i += interval_x) {
					for (double j = r_min_y + HALF_METER; j <= r_max_y
							- HALF_METER; j += interval_y) {
						if(C_MULTI >= 4 && k>6) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j+10));
						}
						else
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						// cPintData2[cPointCnt_i][cPointCnt_j] = new
						// CandidatePointData(i, j);

					}
				}
			}
		}
	}

	/**
	 * 候補点の設定 現実的
	 * 
	 * */
	protected void setCandidatePoint_Reality() {
		for (int k = 0; k < roomPointData.size(); k++) {
			int type = 0;
			if (roomOnlyCPoint[k] != null) {
				roomOnlyCPoint[k].clear();
			}
			double r_max_x = roomPointData.get(k).getMaxX();
			double r_max_y = roomPointData.get(k).getMaxY();
			double r_min_x = roomPointData.get(k).getMinX();
			double r_min_y = roomPointData.get(k).getMinY();
			for (int l = 0; l < roomPoint.length; l++) {
				for (int m = 0; m < roomPoint[l].length; m++) {
					if (roomPointData.get(k).getPolygonNum() == roomPoint[l][m]) {
						type = l;
					}
				}
			}
			double interval_x = (r_max_x - r_min_x)
					/ (DIV_X[type] * (2.0 / C_MULTI));
			double interval_y = (r_max_y - r_min_y)
					/ (DIV_Y[type] * (2.0 / C_MULTI));

			roomOnlyCPoint[k] = new ArrayList<CandidatePointData>();

			if (k < 5) {
				for (double i = r_min_x + HALF_METER * (C_MULTI / 2); i <= r_max_x
						- HALF_METER * (C_MULTI / 2); i += interval_x) {
					for (double j = r_min_y + HALF_METER * (C_MULTI / 2); j <= r_max_y
							- HALF_METER * (C_MULTI / 2); j += interval_y) {
						if (j + interval_y >= r_max_y - HALF_METER
								* (C_MULTI / 2)) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						} else if (i == r_min_x + HALF_METER * (C_MULTI / 2)
								|| i + interval_x >= r_max_x - HALF_METER
										* (C_MULTI / 2))
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
					}
				}
			} else {
				int x_cnt = 0;
				for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER; i += interval_x) {
					for (double j = r_min_y + HALF_METER; j <= r_max_y
							- HALF_METER; j += interval_y) {
						if (j == r_min_y + HALF_METER) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						} else if (i == r_min_x + HALF_METER
								|| i + interval_x >= r_max_x - HALF_METER) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						} else if (j + interval_y >= r_max_y - HALF_METER) {
							// 実験室ドアの前に置かないようにする
							if (k == 5 && x_cnt >= 2 && x_cnt <= 10 || k == 6
									&& x_cnt >= 2 && x_cnt <= 12)
								roomOnlyCPoint[k].add(new CandidatePointData(i,
										j));
						}
					}
					x_cnt++;
				}
			}
		}
	}

	/**
	 * マウス操作時の動作
	 * 
	 * */
	public void processMouseEvent(MouseEvent e) {
		/* クリックイベント */
		if (e.getID() == MouseEvent.MOUSE_CLICKED) {
			/* クリックした点を取得し、ルータとする */
			getClickPoint(e.getX(), e.getY());
			if (mesh_type == 0) {
				rooterPoint = allCPoint[(int) rPoint_i][(int) rPoint_j];
			} else if (mesh_type >= 1) {
				rooterPoint = new CandidatePointData(rPoint_i, rPoint_j);
			}
			repaint();
		}
	}

	/**
	 * 線分の交差判定 描写用
	 * 
	 */
	protected void checkIntersect(CandidatePointData c_point, Graphics _g) {
		// _g.setColor(Color.blue);
		Graphics2D g2 = (Graphics2D) _g;
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);
		AlphaComposite composite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5f);
		// アルファ値をセット（以後の描画は半透明になる）
	//	g2.setComposite(composite);

		boolean isIntersect = false;
		boolean isCover = true;

		for (int i = 0; i < userPoint.length; i++) {
			for (int j = 0; j < userPoint[i].size(); j++) {
				int inter_cnt = 0;
				isIntersect = false;
				for (int k = 0; k < lineData.size(); k++) {
					isIntersect = Line2D.linesIntersect(c_point.getX(),
							c_point.getY(), userPoint[i].get(j).getX(),
							userPoint[i].get(j).getY(), lineData.get(k).getX(),
							lineData.get(k).getY(), lineData.get(k).getX2(),
							lineData.get(k).getY2());
					if (isIntersect) {
						inter_cnt++;
					}
				} 
				
				if (checkDir_dBm(c_point, userPoint[i].get(j)) - inter_cnt * 4  >= -57) {
					g2.drawRect((int) userPoint[i].get(j).getX(),
							(int) userPoint[i].get(j).getY(), 3, 3);
					// System.out.println(c_point.getX()+" "+c_point.getY()+" "+userPoint[i].get(j).getX()+" "+userPoint[i].get(j).getY());
				}
			}
		}
	}

	/**
	 * 線分の長さの判定
	 * 
	 * */
//	protected boolean checkMaxDir(CandidatePointData c_point,
//			CandidatePointData u_point) {
//		double x = Math.pow(c_point.getX() - u_point.getX(), 2);
//		double y = Math.pow(c_point.getY() - u_point.getY(), 2);
//		double dir = Math.sqrt(x + y);
//
//		if (dir <= MAX_DIR) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * 線分の長さの判定
	 * 
	 * */
	protected double checkDir_dBm(CandidatePointData c_point,
			CandidatePointData u_point) {
		double x = Math.pow(c_point.getX() - u_point.getX(), 2);
		double y = Math.pow(c_point.getY() - u_point.getY(), 2);
		double dir = Math.sqrt(x + y);
		dir = dir / 23;
//		return dir * 1.0699 + 2.5224;
	//	return dir * 1.0425 + 1.6149;
		return 10 - 20*Math.log10(dir*32*Math.PI);
		// return Math.log(dir)*5.2568+1.3337;
	}

	/**
	 * クリックした点を取得
	 * 
	 * */
	protected void getClickPoint(double click_x, double click_y) {
		isRooter = true;
		if (mesh_type == 0) {
			int x_cnt = 0, y_cnt = 0;
			click_x -= 100;
			while (click_x > 0) {
				click_x -= C_INTERVAL;
				x_cnt++;
			}
			click_y -= 50;
			while (click_y > 0) {
				click_y -= C_INTERVAL;
				y_cnt++;
			}
			rPoint_i = x_cnt;
			rPoint_j = y_cnt;
		} else if (mesh_type >= 1) {
			int room_num = 0;
			for (int i = 0; i < roomPointData.size(); i++) {
				if (roomPointData.get(i).getMinX() <= click_x
						&& click_x <= roomPointData.get(i).getMaxX()
						&& roomPointData.get(i).getMinY() <= click_y
						&& click_y <= roomPointData.get(i).getMaxY()) {
					room_num = i;
				}
			}
			for (int i = 0; i < roomOnlyCPoint[room_num].size(); i++) {
				if (roomOnlyCPoint[room_num].get(i).getX() - 3 <= click_x
						&& roomOnlyCPoint[room_num].get(i).getX() + 5 >= click_x
						&& roomOnlyCPoint[room_num].get(i).getY() - 3 <= click_y
						&& roomOnlyCPoint[room_num].get(i).getY() + 5 >= click_y) {
					rPoint_i = roomOnlyCPoint[room_num].get(i).getX();
					rPoint_j = roomOnlyCPoint[room_num].get(i).getY();
				}
			}
		}
	}
}

class CandidatePointData {
	private double x, y;

	public CandidatePointData() {
	}

	public CandidatePointData(double _x, double _y) {
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

class RoomPointData {
	private int polygon_num;
	private double min_x;
	private double max_x;
	private double min_y;
	private double max_y;

	public RoomPointData(int _polygon_num, double _min_x, double _max_x,
			double _min_y, double _max_y) {
		this.polygon_num = _polygon_num;
		this.min_x = _min_x;
		this.max_x = _max_x;
		this.min_y = _min_y;
		this.max_y = _max_y;
	}

	public int getPolygonNum() {
		return polygon_num;
	}

	public double getMinX() {
		return min_x;
	}

	public double getMaxX() {
		return max_x;
	}

	public double getMinY() {
		return min_y;
	}

	public double getMaxY() {
		return max_y;
	}

}
