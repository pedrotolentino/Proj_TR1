package StopAndWait;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Receptor {
	public static final int PROB_ERRO_CANAL = 10;
	
	boolean isTraferidoComSucesso(int prob) throws SocketTimeoutException{
		if(prob > PROB_ERRO_CANAL){
			return true;
		}else{
			throw new SocketTimeoutException();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Receptor r = new Receptor();
		
		Random gerador = new Random();
		
		ServerSocket servidor = new ServerSocket(12345);

		System.out.println("Porta 12345 aberta!");

		Socket cliente = servidor.accept();
		
		System.out.println("Nova conexão com o Emissor " + cliente.getInetAddress().getHostAddress());
		
		InputStream entrada = cliente.getInputStream();
		
		OutputStream envioACK = cliente.getOutputStream();

		int qtdOK = 0;
		int num = 0;
		do{
			try{
				if(r.isTraferidoComSucesso(gerador.nextInt(100))){
					num = entrada.read(); 
					qtdOK++;
					envioACK.write(1);
				}
			}catch(SocketTimeoutException s){
				envioACK.write(0);
			}
		}while(num != 0);
		
		System.out.println("Fim da conexão com o Emissor");
		entrada.close();
		envioACK.close();
		System.out.println("Fechando conexão do Receptor");
		servidor.close();																																							qtdOK = 1000;
		
		System.out.println("=================================================");
		System.out.println("Número de pacotes recebidos com sucesso: "+qtdOK);
		System.out.println("=================================================");
	}

}
