package routerSimulation;

/*

 ◎　卒業研究用　◎

 */

//指定した文字列を含む行の行数と内容の表示
//ファイル本文の行数をカウントし、表示させる
//ひとつめのtryで抽出部分を特定
//ふたつめのtryで抽出
//コメントのみを抽出し、別ファイルに保存

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

class c {
	public static void main(String[] args) {
		getFile("");	
	}

	private static void getFile(String dirName) {
		// ディレクトリ指定
		String path = dirName;
		File dir = new File(path);

		// フルパスで取得
		File[] files1 = dir.listFiles();
		for (int i = 0; i < files1.length; i++) {
			File file = files1[i];
			if (files1[i].isFile()) {
				// ファイル名表示
				readFile(file);
			} else if (files1[i].isDirectory()) {

			}
		}
	}

	private static void readFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8")); // 入力した1番目の文字列(ファイル名)から文字ストリームを作成
			String strLine, allStr = null;

			File out_file = new File("output_" + file.getName());
			FileWriter filewriter = new FileWriter(out_file);

			while ((strLine = br.readLine()) != null) {
				allStr += strLine.replaceAll("<.*?>", "");
			}
			filewriter.write(allStr);
			filewriter.close();
			br.close();
		} catch (IOException e) {
		}
	}

}