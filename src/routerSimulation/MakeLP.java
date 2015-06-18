package routerSimulation;

import java.io.PrintWriter;
import java.util.ArrayList;

public class MakeLP  extends Calc{
	
	private static int I = 17;
	private static int J = 30;
	private static int CH = 13;
	

	static String output_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"LP"+FS+"lp_Data.lp";

	static String input_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"LP"+FS+"data"+FS+"bij_allCPoint.csv";
	static String input_path3 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"LP"+FS+"data"+FS+"aij_allCPoint.csv";

//	static String input_path = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"LP"+FS+"test_data"+FS+"bij_test.csv";
//	static String input_path3 = System.getProperty("user.home")+FS+"Dropbox"+FS+"javaData"+FS+"LP"+FS+"test_data"+FS+"test1.csv";
	
	static double[][][][][] coefficient = new double[J][J][CH][CH][I];
	
	
	static ArrayList<Integer>[] aij = new ArrayList[J];
	static ArrayList<Double>[] bij = new ArrayList[J];
	static ArrayList<Integer>[] pck = new ArrayList[CH];

	public static void main(String arg[]) {
		//出力用ファイルの設定
		PrintWriter pw = null;
		//関数makeFileを呼び出す
		pw = makeFile(pw, output_path);
		
		//関数fileOpenを呼び出して、それぞれのcsvの内容を配列に入れる
		bij = fileOpen(bij, input_path, 1);
		pck = fileOpen(pck, input_path2, 0);
		aij = fileOpen(aij, input_path3, 0);
		
		
		pw = writeObj(pw);
		pw = writeSubject(pw);
		pw = writeBinary(pw);
		pw.close();
		System.out.println("end");
	}
	
	protected static PrintWriter writeObj(PrintWriter _pw) {
		_pw.println("minimize");
		_pw.println("obj:");
		String str = "";
		double tmp = 0.0;
		for(int j1=1; j1<=J; j1++) {
			for(int j2=1; j2<=J; j2++) {
				if(j1 != j2) {
					for(int c=1; c<=CH; c++) {
						for(int k=1; k<=CH; k++) {
							for(int i=1; i<=I; i++) {
								tmp = pck[c-1].get(k-1) * bij[j1-1].get(i-1) * bij[j2-1].get(i-1);
							//	str = Double.toString(tmp);
								str = "+ " + tmp + " z(" + j1 + "," + c + "," + j2 + "," + k + ")";
								_pw.println(str);
							}
						}
					}
				}
			}
		}
		_pw.println("");
		return _pw;
	}
	
			
	protected static PrintWriter writeSubject(PrintWriter _pw) {
		_pw.println("subject to");
		String str = "";
		for(int i=1; i<=I; i++) {
			for(int j=1; j<=J; j++) {
				str = "";
				for(int c=1; c<=CH; c++) {
					if(aij[j-1].get(i-1) == 1)
						str += "+ " + aij[j-1].get(i-1) + " x(" + j + "," + c + ") ";
				}
				if(str.length() > 0) {
					str += ">= 1";
					_pw.println(str);
				}
			}
		}
		
		for(int j=1; j<=J; j++) {
			str = "";
			for(int c=1; c<=CH; c++) {
				str += "+ x(" + j + "," + c + ") ";
			}
			str += "<= 1";
			_pw.println(str);
		}
		
		for(int c=1; c<=CH; c++) {
			str = "";
			for(int j=1; j<=J; j++) {
				str += "+ x(" + j + "," + c + ") ";
			}
			str += "<= 1";
			_pw.println(str);
		}
		
		String x1, x2;
		for(int j1=1; j1<=J; j1++) {
			for(int j2=1; j2<=J; j2++) {
				if(j1 != j2) {
					for(int c=1; c<=CH; c++) {
						for(int k=1; k<=CH; k++) {
							str = "z(" + j1 + "," + c + "," + j2 + "," + k + ")";
							x1 = " x(" + j1 + "," + c + ")";
							x2 = " x(" + j2 + "," + k + ")";
							_pw.println(str + " -" +  x1 + " <= 0" );
							_pw.println(str + " -" +  x2 + " <= 0" );
							_pw.println(str  + " -"+ x1 + " -" +  x2 + " >= - 1");
						}
					}
				}
			}
		}
		_pw.println("");
		return _pw;
	}
	
	protected static PrintWriter writeBinary(PrintWriter _pw) {
		_pw.println("binary");
		for(int j=1; j<=J; j++) {
			for(int c=1; c<=CH; c++) {
				_pw.println("x(" + j + "," + c + ") ");
			}
		}
		for(int j1=1; j1<=J; j1++) {
			for(int j2=1; j2<=J; j2++) {
				if(j1 != j2) {
					for(int c=1; c<=CH; c++) {
						for(int k=1; k<=CH; k++) {
							_pw.println("z(" + j1 + "," + c + "," + j2 + "," + k + ")");	
						}
					}
				}
			}
		}
		
		_pw.println("\nend");
		return _pw;
	}
}
