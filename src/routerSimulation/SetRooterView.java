package routerSimulation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JFrame;

public class SetRooterView extends PolygonViewMap {
	public SetRooterView() {
		
	}

	public static void main(final String[] args) {
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		// ウィンドウ生成
		JFrame app = new PolygonViewMap();
		SetRooterCanvas src = new SetRooterCanvas(getData());
		app.add(src);
		src.repaint();
		app.setVisible(true);
	}
}

class SetRooterCanvas extends PointViewCanvas {
	//private int[] setRooterPoint = {};
	private ArrayList<Integer> setRooterPoint =  new ArrayList<Integer>();
	private ArrayList<Integer> selectChNum =  new ArrayList<Integer>();

	private Color[] rooterColor ={new Color(0, 0, 255), new Color(255, 0, 0),  new Color(0, 255, 0), new Color(0, 128, 0),
	   new Color(255, 0, 255), new Color(0, 0, 128), new Color(150, 100, 0), new Color(0, 139, 139), new Color(255, 69, 0)};
	
	
	private ArrayList<Rooter> rooter = new ArrayList<Rooter>();
	
	public SetRooterCanvas(ArrayList<PointDataSet>[] _pointData) {
		super(_pointData);
		getRooterPoint();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void paint(Graphics g) {
		setPoint(g);
		setChannel(g);
		setRooter(g);
		drawInterference(g);
	}

	private void setChannel(Graphics _g) {
		int color = 0;
		for(int i=0; i<selectChNum.size(); i++) {
			if(color >= rooterColor.length) {
				color = 0;
			}
			color++;
			_g.setColor(rooterColor[i]);
			rooterPoint = new CandidatePointData(1150, 50+(i*20));
			drawRooterPoint(_g, rooterPoint);
			_g.drawString(Integer.toString(selectChNum.get(i)), 1170, 50+(i*20)+5);
		}
	}
	
	protected void getRooterPoint() {
		String FS = File.separator;
		String path = System.getProperty("user.home")+FS+"Dropbox"+FS + "prog" + FS+"javaData"+FS+"result"+FS;
//		String path = System.getProperty("user.home")+FS+"Dropbox"+FS+"opl"+FS+"SetCovering"+FS;
		
		if(mesh_type == 0) {
			path += "all_result10.csv";
		}
		else if(mesh_type == 1){
			path += "room_result10.csv";
//			path += "all_result.csv";

		}
		else if(mesh_type == 2){
			path += "reality_result10.csv";
		}
		
		File file = new File(path);
		BufferedReader br = null;
		
		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {					
				String[] array = line.split(","); // カンマで分割		
				setRooterPoint.add(Integer.parseInt(array[0]));
				if(array.length > 1) {
					selectChNum.add(Integer.parseInt(array[1]));
					rooter.add(new Rooter(Integer.parseInt(array[0]), Integer.parseInt(array[1])));
				}
//						rooter.add(new Rooter(Integer.parseInt(array[0]),0));

			}
			fr.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
	}
	
	protected void setRooter(Graphics _g) {
		int cnt = 1;
		int color = 0;
		if (mesh_type == 0) {
			for (int i = 0; i < cPointCnt_i; i++) {
				for (int j = 0; j < cPointCnt_j; j++) {
					for (int k = 0; k < setRooterPoint.size(); k++) {
						if(cnt == setRooterPoint.get(k)) {
							_g.setColor(rooterColor[color]);
							rooterPoint = allCPoint[i][j];
							drawRooterPoint(_g, rooterPoint);
//							checkIntersectRoom(rooterPoint, _g);
//							rooter.get(k).setInterferencePoint(checkIntersectRoom2(rooterPoint, _g));
							checkIntersectRoom2(rooterPoint, _g, k);
							color++;
							if(color >= rooterColor.length) {
								color = 0;
							}
						}
					}
					cnt++;
				}
			}
		} else if (mesh_type >= 1) {
			for (int i = 0; i < roomOnlyCPoint.length; i++) {
				for (int j = 0; j < roomOnlyCPoint[i].size(); j++) {
					for (int k = 0; k < setRooterPoint.size(); k++) {
						if(cnt == setRooterPoint.get(k)) {
//							System.out.println(cnt+" "+setRooterPoint[k]);
							_g.setColor(Color.BLACK);//rooterColor[color]);
							rooterPoint = roomOnlyCPoint[i].get(j);
							drawRooterPoint(_g, rooterPoint);
//							checkIntersectRoom(rooterPoint, _g);
//							rooter.get(k).setInterferencePoint(checkIntersectRoom2(rooterPoint, _g));
							checkIntersectRoom2(rooterPoint, _g, k);
			//				System.out.println(cnt + "  " +rooterPoint.getX()+"  "+rooterPoint.getY());

							color++;
							if(color >= rooterColor.length) {
								color = 0;
							}
						}
					}
					cnt++;
				}
			}
		}
	}
	
	
	private void drawInterference(Graphics _g) {
			_g.setColor(Color.black);
		Graphics2D g2 = (Graphics2D) _g;
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);
		int cnt = 0;
		for(int i=0; i<rooter.size(); i++) {
			ArrayList<CandidatePointData> tmp = rooter.get(i).getCoverPoint();
			for(int j=0; j<rooter.size(); j++) {
				if(i!=j && Math.abs(rooter.get(i).getCh() - rooter.get(j).getCh()) <= 3) {
					ArrayList<CandidatePointData> tmp2 = rooter.get(j).getInterferencePoint();
					for(int k=0; k<tmp.size(); k++) {
						for(int l=0; l<tmp2.size(); l++) {
							if(tmp.get(k).getX() == tmp2.get(l).getX()) {
								if(tmp.get(k).getY() == tmp2.get(l).getY()) {
							//		if(cnt==0) {
									System.out.println("num "+ rooter.get(i).getNum() + " ch "+rooter.get(i).getCh()  + "   num2 "+  rooter.get(j).getNum()+ " ch " +rooter.get(j).getCh());
									g2.drawRect((int) tmp.get(k).getX(),
											(int) tmp.get(k).getY(), 5, 5);
									cnt++;
								//	}
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * 線分の交差判定 部屋全体
	 * 
	 */
	protected void checkIntersectRoom(CandidatePointData c_point, Graphics _g) {
	//	_g.setColor(Color.blue);
		Graphics2D g2 = (Graphics2D) _g;
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);
	
		boolean isIntersect = false;
		boolean isCover = true;
		for (int i = 0; i < userPoint.length; i++) {
			isCover = false;
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
				if (checkDir_dBm(c_point, userPoint[i].get(j)) - inter_cnt * 4 >= -57) {
					isCover = true;
				} else {
					isCover = false;
					break;
				}
			}
			if (isCover) {
				for (int j = 0; j < userPoint[i].size(); j++) {
				//	g2.drawRect((int) userPoint[i].get(j).getX(),
				//			(int) userPoint[i].get(j).getY(), 3, 3);
				}
			}
		}
	}
	
	protected void checkIntersectRoom2(CandidatePointData c_point, Graphics _g, int rooter_num) {
	//	_g.setColor(Color.blue);
		Graphics2D g2 = (Graphics2D) _g;
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);
	
		ArrayList<CandidatePointData> tmp_interferencePoint = new ArrayList<CandidatePointData>();
		rooter.get(rooter_num).interferencePoint = new ArrayList<CandidatePointData>();
		rooter.get(rooter_num).coverPoint = new ArrayList<CandidatePointData>();

		boolean isIntersect = false;
		for (int i = 0; i < userPoint.length; i++) {
			double sum_point = 0;
			double cover_point = 0;
			double interference_point = 0;
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
				double dBm = checkDir_dBm(c_point, userPoint[i].get(j)) - inter_cnt * 4;
				if (dBm >= -70) {
					rooter.get(rooter_num).interferencePoint.add(userPoint[i].get(j));
				}
				if (dBm >= -57) {
					rooter.get(rooter_num).coverPoint.add(userPoint[i].get(j));
					cover_point++;
				}
				sum_point++;
			}
			if ((cover_point / sum_point) == 1) {
				for (int j = 0; j < userPoint[i].size(); j++) {
			//		g2.drawRect((int) userPoint[i].get(j).getX(),
			//				(int) userPoint[i].get(j).getY(), 3, 3);
				}
			}
		}
	//	return tmp_interferencePoint;
	}
}


class Rooter {
	private int num;
	private int ch;
	public ArrayList<CandidatePointData> coverPoint;
	public ArrayList<CandidatePointData> interferencePoint;
	
	public Rooter(int _num, int _ch) {
		this.num = _num;
		this.ch = _ch;
	}
	
	public int getNum() {
		return num;
	}
	public int getCh() {
		return ch;
	}
	public ArrayList<CandidatePointData> getInterferencePoint() {
		return interferencePoint;
	}
	public ArrayList<CandidatePointData> getCoverPoint() {
		return coverPoint;
	}
	public void setInterferencePoint(ArrayList<CandidatePointData> _interferencePoint) {
		this.interferencePoint = _interferencePoint;
	}
	public void setCoverPoint(ArrayList<CandidatePointData> _coverPoint) {
		this.coverPoint = _coverPoint;
	}
}
