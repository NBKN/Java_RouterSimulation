package routerSimulation;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFrame;

public class CadTest2 extends Frame {
	public static final void main(final String[] args) {
		int[] exclude = {29, 100, };//{19,18,17,16,15,14,13,12,7,5,4,3,2,1};
		int start = 0;
		int end = 10;
		boolean check = true;
		//ウィンドウ生成
		JFrame[] app = new JFrame[300];
		for(int i=start; i<end; i++) {
			check = true;
			for(int j=0; j<exclude.length; j++) {
				if(i == exclude[j]) {
					check = false;
				}
			}
			if(check) {
				app[i] = new JFrame();
				//タイトル設定
				app[i].setTitle("polygon "+i);
				//ウィンドウサイズ設定(タイトルや枠も含んだサイズ)
				app[i].setSize(fstCanvas.WIDTH, fstCanvas.HEIGHT);
				app[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//キャンバスを配置
				fstCanvas cvs = new fstCanvas(getData(i));
				app[i].add(cvs);
				//ウィンドウ表示
				app[i].setVisible(true);
				//無限ループ
			//	for(int i=0; i<pointData.size(); i++) {//画面再描画
				cvs.repaint();
			}
		}
	//	}
	}
	
	static class fstCanvas extends Canvas {
		ArrayList<PointDataSet>[] pointData;// = getData();
		ArrayList<PointDataSet> LineData;
		final public static int WIDTH = 1280;
		public final static int HEIGHT = 736;

		fstCanvas(ArrayList<PointDataSet>[] _pointData) {
			this.pointData = _pointData;
		}
		public void paint(Graphics g) {
			//座標用変数
			int x, y, x2, y2, scale = 5;
			for(int i=0; i<pointData.length; i++) {//画面再描画
				for(int j=0; j<pointData[i].size()-1; j++) {
					x = (int)pointData[i].get(j).getX() *scale;// + (scale)*10;
					y = (int)pointData[i].get(j).getY() *scale - (scale)*80;
					x2 = (int)pointData[i].get(j+1).getX() *scale;// +  (scale)*10;
					y2 = (int)pointData[i].get(j+1).getY() *scale -  (scale)*80;
					//点を描画する
					g.drawLine(-x+WIDTH, y, -x2+WIDTH, y2);
					//g.drawLine(-x+WIDTH, y, -x+WIDTH, y);
				}
			}
		}
		//updateメソッドを乗っ取って，画面クリアを防ぐ
	/*	public void update(Graphics g){
			paint(g);
		}*/
	}	
	
	
	private static ArrayList<PointDataSet>[] getData(int polygon_num) {
		ArrayList<PointDataSet>[] tmpPointData = new ArrayList[256];
		File file = new File("/Users/Ken/AppData/cad_data.txt");
		BufferedReader br = null;
		
		for(int i=0; i<tmpPointData.length; i++) {
			tmpPointData[i] = new ArrayList<PointDataSet>();
		}
		double min = 1000;
		 double max = 0;
		try {
			FileReader fr=new FileReader(file);     //FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			int p_cnt = 0;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null && p_cnt <= polygon_num) {
				if(!line.matches(".*" +  "POLYGON" + ".*")) {
					if(p_cnt == polygon_num) {
					String[] array = line.split(",");
					double x, y;
					x = Double.parseDouble(array[0]);
					y = Double.parseDouble(array[1]);

					if(max < y) {
						max = y;
					}
					if(min >y) {
						min = y;
					}
					tmpPointData[p_cnt].add(new PointDataSet(x, y));
					System.out.println("polygon "+p_cnt);
					}
				}
				else {
					p_cnt++;
					System.out.println("else "+p_cnt);

				}
			}
			fr.close();
		}
		catch(Exception e) {
			System.out.println(e);  //エラーが起きたらエラー内容を表示
		}
		return tmpPointData;
	}
}