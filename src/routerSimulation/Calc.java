package routerSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class Calc {
	
	//フォルダの区切り windowsなら¥マーク
	static String FS = File.separator;
	//貰ったODデータのパス
	static String input_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"calc"+FS+"bij_roomOnlyPoint.csv";
	//自分で作ったデータのパス
	static String input_path2 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"calc"+FS+"interference_cost2.csv";
	
	static String input_path3 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"calc"+FS+"aij_roomOnlyPoint.csv";
	//出力用ファイルのパス
	static String output_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"keita"+FS+"aaaaaanew_Data.csv";
	
	static String output_path2 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"tree.txt";

	//それぞれデータの行の長さ
	static int  bij_DataSize = 180;
	static int  interference_DataSize = 13;

	
	//データを入れておく配列
	static ArrayList<Double>[] bij = new ArrayList[bij_DataSize];
	static ArrayList<Integer>[] pck = new ArrayList[interference_DataSize];
	
	public static void main(String arg[]) {	
		//出力用ファイルの設定
		PrintWriter pw = null;
		//関数makeFileを呼び出す
		pw = makeFile(pw, output_path);
		
		//関数fileOpenを呼び出して、それぞれのcsvの内容を配列に入れる
		bij = fileOpen(bij, input_path, 1);
		pck = fileOpen(pck, input_path2, 0);
	
		int[] rooter = {21,55,102,118,132,159};//{17,87,100,119,124,145,168};
		int[] ch = {12,11,1,5,3,2};//{11,13,1,12,10,3,2};
	//	System.out.println("num=1   " + calcMin(54, 4));
	//	System.out.println("num=5   " + calcMin(117, 4));
		int sum = 0;
		//298512
		for(int i=0; i<rooter.length; i++) {
		//	for(int j=0; j<ch.length; j++) {
				sum += calcMin(rooter[i]-1, ch[i]-1);
				System.out.println("num="+(rooter[i])+"   " + calcMin(rooter[i]-1, ch[i]-1));	
		//	}
		}
		System.out.println(sum);//1260220 1203456
		
	//	int a=84*12*
//		tree();
		
	}
	
	
	protected static int calcMin(int set_rooter_num, int set_ch_num) {
		int room_num = 17;
		int rooter_num = bij_DataSize;
		int ch_num = interference_DataSize;
		int[] rooter = {21,55,102,118,132,159};
		///int[] rooter = {17,87,100,119,124,145,168};
		int[] use_ch = {12,5,1,13,3,2};//{11,13,1,12,10,3,2};
	//	int[] use_ch = {11, 2, 13, 8, 3, 12};
		int sum = 0;
		
		for(int i=0; i<room_num; i++) {
			for(int j=0; j<rooter_num; j++) {
				for(int k=0; k<ch_num; k++) {
					if(j != set_rooter_num ) {
						for(int m=0; m<rooter.length; m++) {
							if(j == rooter[m]-1) {
								for(int l=0; l<use_ch.length; l++) {
									if(use_ch[l]-1 == k) {
						//	sum += bij[set_rooter_num].get(i)/100*pck[set_ch_num].get(k)*bij[j].get(i)/100;
										sum += bij[set_rooter_num].get(i)*pck[set_ch_num].get(k) + bij[j].get(i)*pck[k].get(set_ch_num);
//									if(bij[set_rooter_num].get(i)*pck[set_ch_num].get(k) > 0 && bij[j].get(i)*pck[k].get(set_ch_num) > 0)
//									System.out.println(bij[set_rooter_num].get(i) + " * " + pck[set_ch_num].get(k) 
//											+ " + " + bij[j].get(i) + " * " + pck[k].get(set_ch_num));
									}
								}
							}
						}
					}
				}
			}
		}
		return sum;
	}
	
	
	/**
	 * ファイルを開いて内容を配列に代入する
	 * 
	 * */
	protected static ArrayList[] fileOpen(ArrayList[] array, String _path, int type) {
		File file = new File(_path);
		try {
			/* 読み込める形に変更 */
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line;
			int cnt = 0;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				//カンマで区切る
				if(type == 0)
					array[cnt] = new ArrayList<Integer>();
				else 
					array[cnt] = new ArrayList<Double>();

				String[] tmp = line.split(",");
				for(int i=0; i<tmp.length; i++) {
					if(type == 0)
						array[cnt].add(Integer.parseInt(tmp[i].trim()));
					else
						array[cnt].add(Double.parseDouble(tmp[i].trim()));
				}
				cnt++;
			}
			fis.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
		return array;
	}
	
	
	/*
	 * 書き込むファイルを作成し、開く
	 * 
	 * */
	protected static PrintWriter makeFile(PrintWriter _pw, String fileName) {
		// File file = new File();
		File file = new File(fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos,  "Shift_JIS");
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
	
	private static void tree() {
		PrintWriter pw = null;
		pw = makeFile(pw, output_path2);
		int cnt = 0;
		int cnt2=0;
		int cnt3=0;
		int max = 1000;
		for(int i=0; i<500; i++) {
			String str = "";
			for(int j=0; j<500; j++) {
				if(i < 30*5) {
				if(j >=49*5-cnt && 49*5+cnt >=j) {
					str += "0 ";
				}
				else 
					str += "1 ";
				}
//				else if(i>=40*5 && i<=45*5){
//					 if(j>=60*5 && j<=65*5) {
//						 if(i<=41*5)
//						 if((j>=60*5 && j<=61*5) || (j>=64*5 && j<=65*5)) {
//							 str += "0 ";
//						 }
//						 else 
//							 str += "1 ";
//					}
//					 else if(j >=30*5-cnt2 && 70*5+cnt2 >=j) {
//						str += "0 ";
//					}
//
//					else {
//						str += "1 ";
//					}
//				}
				else if(i>=30*5 && i<=60*5) {
					if(j >=30*5-cnt2 && 70*5+cnt2 >=j) {
						str += "0 ";
					}
//					else if(i>=40*5 && i<=45*5 && j>=60*5 && j<=65*5) {
//						str += "1 ";
//					}
					else {
						str += "1 ";
					}
				}

				else if(i>60*5 && i<70*5){
					if(j >=40*5 && 60*5 >=j) {
						str += "0 ";
					}
					else 
						str += "1 ";
				}
				else {
					if(j >=30*5+(cnt3/5) && 70*5-(cnt3/5) >=j) {
						str += "0 ";
					}
					else 
						str += "1 ";
				}
			}
			if(i>70*5) {
				cnt3++;
			}
			else if(i>=30*5 && i<=60*5) {
				cnt2++;
			}
			cnt++;
			//System.out.println(str);
			pw.println(str);
		}
		pw.close();

	} 
}
