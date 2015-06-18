package routerSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class Keita {
	//フォルダの区切り windowsなら¥マーク
	static String FS = File.separator;
	//貰ったODデータのパス
	static String input_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"keita"+FS+"od_Data.csv";
	//自分で作ったデータのパス
	static String input_path2 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"keita"+FS+"my_Data.csv";
	//出力用ファイルのパス
	static String output_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"keita"+FS+"aaaaaanew_Data.csv";
	
	//それぞれデータの行の長さ
	static int  od_Data_Size = 1232;
	static int  my_Data_Size = 118;

	/** 
	 * メイン文
	 * */
	public static void main(String arg[]) {
		//データを入れておく配列
		ArrayList<String>[] od_Data = new ArrayList[od_Data_Size];
		ArrayList<String>[] my_Data = new ArrayList[my_Data_Size];
		
		//出力用ファイルの設定
		PrintWriter pw = null;
		//関数makeFileを呼び出す
		pw = makeFile(pw, output_path);
		
		//関数fileOpenを呼び出して、それぞれのcsvの内容を配列に入れる
		od_Data = fileOpen(od_Data, input_path);
		my_Data = fileOpen(my_Data, input_path2);
		
		for(int j=0; j<my_Data.length; j++) {
			for(int i=4; i<od_Data.length; i++) {
				if(od_Data[i].size() > 1) {
					//括弧の後ろを取り除く　例：京橋（外）➡ 京橋
					String tmp[] = od_Data[i].get(1).split("\\（");
					//od_dataとmy_dataの比較。文字列が一致してれば od_data の内容を書き込む
					if(my_Data[j].get(0).equals(tmp[0]) && !tmp[0].equals("") && tmp[0]!=null) {
						String str = "";
						for(int k=1; k<od_Data[i].size(); k++) {
							str += od_Data[i].get(k) + ",";
						}
						//書き込み
						pw.println(str);
					}
				}
			}
		}
		pw.close();
	}
	
	
	/**
	 * ファイルを開いて内容を配列に代入する
	 * 
	 * */
	private static ArrayList[] fileOpen(ArrayList[] array, String _path) {
		File file = new File(_path);		
		try {
			/* 読み込める形に変更 */
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "Shift_JIS"));
			String line;
			int cnt = 0;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				//カンマで区切る
				array[cnt] = new ArrayList<String>(Arrays.asList(line.split(","))); // カンマで分割
				System.out.println(array[cnt]);
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
	private static PrintWriter makeFile(PrintWriter _pw, String fileName) {
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
}
