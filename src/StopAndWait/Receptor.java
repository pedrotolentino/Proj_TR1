package StopAndWait;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Receptor {
	
	private static final int PROB_ERRO_CANAL = 10;
	private static ServerSocket servidor = null;
	private static Receptor r = null;
	
	boolean isTraferidoComSucesso(int prob) throws SocketTimeoutException{
		if(prob > PROB_ERRO_CANAL){
			return true;
		}else{
			throw new SocketTimeoutException();
		}
	}
	
	public void iniciarServidor(int port) throws IOException {
		
		r = new Receptor();
		servidor = new ServerSocket(port);
		
	}

	public void aceitarServidor() throws Exception {
		
		Random gerador = new Random();
		System.out.println("Porta "+ servidor.getLocalPort() +" aberta!");

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
