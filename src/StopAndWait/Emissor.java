package StopAndWait;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Emissor {
	public static final int QTD_PACOTES = 1000;

	public void run() throws UnknownHostException, IOException {

		Socket socketEmi = new Socket("localhost", 12345);

		System.out.println("O receptor se conectou ao emissor!");
		
		InputStream retornoACK = socketEmi.getInputStream();
		
		OutputStream saida = socketEmi.getOutputStream();
		
		int qtdReenviados = 0;
		int qtdErro = 0;
		boolean pctSemErro;
				
		for (int i = 1; i <= QTD_PACOTES; i++){
			saida.write(1);
			pctSemErro = true;
			
			while(retornoACK.read() == 0){
				pctSemErro = false;
				System.out.println("Falha no envio do pacote "+i+"... Reenviando pacote...");
				saida.write(1);
				qtdReenviados++;
			}
			
			if(!pctSemErro){
				qtdErro++;
			}
		}
		saida.write(0);
		retornoACK.close();
		saida.close();
		System.out.println("Fechando conexão do Emissor");
		socketEmi.close();
		System.out.println("=============================================");
		System.out.println("Número de pacotes reenviados: "+qtdReenviados);
		System.out.println("Taxa de pacotes com erro: "+(((double)(qtdErro)/QTD_PACOTES)*100)+"%");
		System.out.println("=============================================");
	}
}
