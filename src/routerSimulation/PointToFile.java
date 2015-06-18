package routerSimulation;

import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PointToFile extends PolygonViewMap {
	public PointToFile() {
	}

	public static void main(final String[] args) {
		GetCandidateCanvas cvs = new GetCandidateCanvas(getData());
	}
}

class GetCandidateCanvas extends PointViewCanvas {

	public GetCandidateCanvas(ArrayList<PointDataSet>[] _pointData) {
		super(_pointData);
		this.pointData = _pointData;
		allCheck();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	protected void makeFolder() {
		File folder = null;
		folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs(); // make folders
		}
		folder = new File(pointDataPath);
		if (!folder.exists()) {
			folder.mkdirs(); // make folders
		}
		folder = new File(cplexDataPath);
		if (!folder.exists()) {
			folder.mkdirs(); // make folders
		}
		folder.mkdirs();
	}

	/**
	 * 利用点の設置
	 * 
	 */
	protected void setUserPoint() {
		PrintWriter pw = null;
		pw = makeFile(pw, pointDataPath + "座標データ_userPoint.csv");
		int cnt = 1;

		pw.println("num, x, y");
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
					if (type == 0
							&& j + interval_y >= r_max_y - HALF_METER
							&& (i + interval_x >= r_max_x - HALF_METER || (i == r_min_x
									+ HALF_METER))) {

					} else {
						userPoint[k].add(new CandidatePointData(i, j));
						pw.println(cnt + "," + i + "," + j);
						cnt++;
					}
				}
			}
		}
		pw.close();
	}

	/**
	 * 候補点の設定 全体
	 * 
	 * */
	protected void setCandidatePoint_All() {
		PrintWriter pw = null;
		pw = makeFile(pw, pointDataPath + "座標データ_allCandidate.csv");
		pw.println("num, x, y");
		cPointCnt_j = 0;
		cPointCnt_i = 0;
		int cnt = 1;
		for (double i = min_x; i <= max_x; i += C_INTERVAL) {
			cPointCnt_j = 0;
			for (double j = min_y; j <= max_y; j += C_INTERVAL) {
				allCPoint[cPointCnt_i][cPointCnt_j] = new CandidatePointData(i,
						j);
				cPointCnt_j++;
				pw.println(cnt + "," + i + "," + j + ",");
				cnt++;
			}
			cPointCnt_i++;
		}
		pw.close();
	}

	/**
	 * 候補点の設定 部屋のみ
	 * 
	 * */
	protected void setCandidatePoint_Room() {
		PrintWriter pw = null;
		pw = makeFile(pw, pointDataPath + "座標データ_RoomOnlyCandidate.csv");
		int cnt = 1;
		pw.println("num, x, y");
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
			/*
			 * for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER;
			 * i += interval_x) { for (double j = r_min_y + HALF_METER; j <=
			 * r_max_y - HALF_METER; j += interval_y) {
			 * roomOnlyCPoint[k].add(new CandidatePointData(i, j));
			 * pw.println(cnt + "," + i + "," + j); cnt++; } }
			 */

			if (k < 5) {
				for (double i = r_min_x + HALF_METER * (C_MULTI / 2); i <= r_max_x
						- HALF_METER * (C_MULTI / 2); i += interval_x) {
					for (double j = r_min_y + HALF_METER * (C_MULTI / 2); j <= r_max_y
							- HALF_METER * (C_MULTI / 2); j += interval_y) {
						roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						// cPintData2[cPointCnt_i][cPointCnt_j] = new
						// CandidatePointData(i, j);
						pw.println(cnt + "," + i + "," + j);
						cnt++;
					}
				}
			} else {
				for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER; i += interval_x) {
					for (double j = r_min_y + HALF_METER; j <= r_max_y
							- HALF_METER; j += interval_y) {
						roomOnlyCPoint[k].add(new CandidatePointData(i, j));
						// cPintData2[cPointCnt_i][cPointCnt_j] = new
						// CandidatePointData(i, j);
						pw.println(cnt + "," + i + "," + j);
						cnt++;
					}
				}
			}
		}
		pw.close();
	}

	/**
	 * 候補点の設定 現実的
	 * 
	 * */
	protected void setCandidatePoint_Reality() {
		PrintWriter pw = null;
		pw = makeFile(pw, pointDataPath + "座標データ_RealityCandidate.csv");
		int cnt = 1;
		pw.println("num, x, y");

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
							pw.println(cnt + "," + i + "," + j);
							cnt++;
						} else if (i == r_min_x + HALF_METER * (C_MULTI / 2)
								|| i + interval_x >= r_max_x - HALF_METER
										* (C_MULTI / 2)) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
							pw.println(cnt + "," + i + "," + j);
							cnt++;
						}
					}
				}
			} else {
				int x_cnt = 0;
				for (double i = r_min_x + HALF_METER; i <= r_max_x - HALF_METER; i += interval_x) {
					for (double j = r_min_y + HALF_METER; j <= r_max_y
							- HALF_METER; j += interval_y) {
						if (j == r_min_y + HALF_METER) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
							pw.println(cnt + "," + i + "," + j);
							cnt++;
						} else if (i == r_min_x + HALF_METER
								|| i + interval_x >= r_max_x - HALF_METER) {
							roomOnlyCPoint[k].add(new CandidatePointData(i, j));
							pw.println(cnt + "," + i + "," + j);
							cnt++;
						} else if (j + interval_y >= r_max_y - HALF_METER) {
							// 実験室ドアの前に置かないようにする
							if (k == 5 && x_cnt >= 2 && x_cnt <= 10 || k == 6
									&& x_cnt >= 2 && x_cnt <= 12) {
								roomOnlyCPoint[k].add(new CandidatePointData(i,
										j));
								pw.println(cnt + "," + i + "," + j);
								cnt++;
							}
						}
					}
					x_cnt++;
				}
			}
		}
		pw.close();
	}

	protected PrintWriter makeFile(PrintWriter _pw, String fileName) {
		// File file = new File();
		File file = new File(fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			_pw = new PrintWriter(osw);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return _pw;
	}

	/**
	 * 全ての線分交差判定
	 * 
	 * */
	protected void allCheck() {
		PrintWriter pw = null;
		PrintWriter pw2 = null;
		String[] str = {};

		if (mesh_type == 0) {
			pw = makeFile(pw, cplexDataPath + "aij_allCPoint.csv");
			pw2 = makeFile(pw, cplexDataPath + "bij_allCPoint.csv");

			for (int i = 0; i < cPointCnt_i; i++) {
				for (int j = 0; j < cPointCnt_j; j++) {
					str = checkIntersectAndMakeFile2(allCPoint[i][j]);
					pw.println(str[0]);
					pw2.println(str[1]);
				}
			}
		}
		else {
			if (mesh_type == 1) {
				pw = makeFile(pw, cplexDataPath + "aij_roomOnlyPoint.csv");
				pw2 = makeFile(pw2, cplexDataPath + "bij_roomOnlyPoint.csv");

			} else if (mesh_type == 2) {
				pw = makeFile(pw, cplexDataPath + "aij_realityPoint.csv");
				pw2 = makeFile(pw2, cplexDataPath + "bij_realityPoint.csv");
			}
			for (int i = 0; i < roomOnlyCPoint.length; i++) {
				for (int j = 0; j < roomOnlyCPoint[i].size(); j++) {
					str = checkIntersectAndMakeFile2(roomOnlyCPoint[i].get(j));
					pw.println(str[0]);
					pw2.println(str[1]);
				}
			}
		}
		pw.close();
		pw2.close();
	}

	/**
	 * 線分の交差判定 ファイル出力用
	 * 
	 */
	protected String checkIntersectAndMakeFile(CandidatePointData c_point) {
		boolean isIntersect = false;
		boolean isCover = true;
		String str = "";

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
					// str += "1,";
				} else {
					isCover = false;
					break;
					// str += "0,";
				}
			}
			if (isCover) {
				str += "1,";
			} else {
				str += "0,";
			}
		}
		return str;
	}

	/**
	 * 線分の交差判定 ファイル出力用
	 * 
	 */
	protected String[] checkIntersectAndMakeFile2(CandidatePointData c_point) {
		boolean isIntersect = false;
		String[] str = {"", ""};
		

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
					interference_point++;
				}
				if (dBm >= -57) {
					cover_point++;
					// str += "1,";
				}
				sum_point++;
			}
			if ((cover_point / sum_point) == 1) {
				str[0] += "1,";
				str[1] += "1,";
			} else {
				str[0] += "0,";
		        //元データ
		        double val = (double)(interference_point/sum_point);
		        //元データをBigDecimal型にする
		        BigDecimal bd = new BigDecimal(val);
		        BigDecimal bd2 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);  //小数第２位
		        str[1] += Double.toString(bd2.doubleValue()) + ",";
			}
		}
		return str;
	}
}
