package routerSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class EditFile {
	private int[][] b = new int[17][326];

	public void main(String arg[]) {
		fileRead();
	}
	
	private void fileRead() {
		String FS = File.separator;
		String path = System.getProperty("user.home")+FS+"Dropbox"+FS+"opl"+FS;//+"SetCovering"+FS;
		
		File file = new File(path);
		BufferedReader br = null;
		
		try {
			FileReader fr = new FileReader(file); // FileReaderオブジェクトの作成
			br = new BufferedReader(fr);
			String line;
			int cnt = 0;
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {	
				String[] array = line.split(","); // カンマで分割
				for(int i=0; i<array.length; i++) {
					b[cnt][i] = Integer.parseInt(array[i]);
				}
			}
			fr.close();
		} catch (Exception e) {
			System.out.println(e); // エラーが起きたらエラー内容を表示
		}
	}
	
	private void makeFile() {
		for(int i=0; i<b.length; i++) {
			for(int j=0; j<b[i].length-1; j++) {
				for(int k=j+1; k<b[i].length; k++) {
					if(b[i][j] == b[i][k]) {
						
					}
				}
			}
		}
	}
}
