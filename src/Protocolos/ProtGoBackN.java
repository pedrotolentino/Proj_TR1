package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.Vector;

import Simulacao.Constantes;
import Simulacao.Protocolo;

public class ProtGoBackN implements Protocolo{

	public int  pacoteErro;
	public int  pacotesEnviados;
	public long tProp;
	
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		int[] ret = null;
		while(flagRetorno != Constantes.ACK){
			flagRetorno = Constantes.ACK;
			pacote.trimToSize();
			pacotesEnviados += pacote.size();
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.reset();
			tProp = System.currentTimeMillis();
			out.writeObject(pacote);
			try{
				ret	 = (int[]) in.readObject();
				
				if(ret[0] == Constantes.TIME_OUT){
					flagRetorno = Constantes.TIME_OUT;
					throw new SocketTimeoutException();
				}
				
				for(int i = ret.length - 1; i >= 0 ; i--){
					if(ret[i] == Constantes.NACK){
						flagRetorno = Constantes.NACK;
						pacoteErro++;
						break;
					}else if(ret[i] == Constantes.ACK){
						tProp = System.currentTimeMillis() - tProp;
						pacote.remove(i);
					}
				}
			}catch(SocketTimeoutException e){
				pacoteErro += pacote.size();
			}
		}
	}
}
