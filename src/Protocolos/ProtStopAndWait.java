package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.Vector;

import Simulacao.Constantes;
import Simulacao.Protocolo;

public class ProtStopAndWait implements Protocolo{
	
	public int  pacoteErro;
	public int  pacotesEnviados;
	public long tProp;
	
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		while(flagRetorno != Constantes.ACK){
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.reset();
			tProp = System.currentTimeMillis();
			out.writeObject(pacote);
			pacotesEnviados++;

			try{
				int[] ret = (int[]) in.readObject();
				
				flagRetorno = ret[0];
				
				if(flagRetorno == Constantes.TIME_OUT){
					throw new SocketTimeoutException();
				}else if(flagRetorno == Constantes.NACK){
					pacoteErro++;
				}else{
					tProp = System.currentTimeMillis() - tProp;
				}
			}catch(SocketTimeoutException e){
				pacoteErro++;
			}
		}
	}
}
