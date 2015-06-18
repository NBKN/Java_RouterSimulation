package routerSimulation;

import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


/**  
 * 使わなくなったけど今後使うかもしてない関数をメモっとく
 * 
 * */
public class SauceStore {
	/** 歩いた道を繋ぐ */
	private static ArrayList<PointDataSet> getLine() {

		ArrayList<PointDataSet> tmpPointData = new ArrayList();
		File file = new File("/Users/Ken/AppData/map_and_dbm_data.txt");
		BufferedReader br = null;

		/*
		 * for(int i=0; i<tmpPointData.length; i++) { tmpPointData[i] = new
		 * ArrayList<PointDataSet>(); }
		 */
		double min = 1000;
		double max = 0;
		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			int p_cnt = 0;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {

				String[] array = line.split(",");
				double x, y;
				x = Double.parseDouble(array[0]);
				y = Double.parseDouble(array[1]);
				tmpPointData.add(new PointDataSet(x, y));
				p_cnt++;
			}
			fr.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
		return tmpPointData;
	}
	
	
	protected static ArrayList<PointDataSet>[] getData() {
		ArrayList<PointDataSet>[] tmpPointData = new ArrayList[256];
		File file = new File("/Users/Ken/AppData/cad_data.txt");
		BufferedReader br = null;

		for (int i = 0; i < tmpPointData.length; i++) {
			tmpPointData[i] = new ArrayList<PointDataSet>();
		}
		double min = 1000;
		double max = 0;
		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			int p_cnt = -1;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				if (!line.matches(".*" + "POLYGON" + ".*")) {
					String[] array = line.split(",");
					double x, y;
					x = Double.parseDouble(array[0]);
					y = Double.parseDouble(array[1]);

					if (max < y) {
						max = y;
					}
					if (min > y) {
						min = y;
					}
					tmpPointData[p_cnt].add(new PointDataSet(x, y));
				} else {
					p_cnt++;
				}
			}
			fr.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
		return tmpPointData;
	}
	
	
	/**
	 * 各点との交差判定
	 * 
	 * */
	
	protected String checkIntersectAndMakeFile(CandidatePointData c_point) {
		boolean isIntersect = false;
		String str = "";
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
				if (inter_cnt <= 4 && checkMaxDir(c_point, userPoint[i].get(j))) {
					str += "1,";
				} else {
					str += "0,";
				}
			}
		}
		return str;
	}

}
