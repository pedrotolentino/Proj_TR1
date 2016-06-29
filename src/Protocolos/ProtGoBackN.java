package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

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
			System.out.print("Emi -> ");
			try{
				ret	 = (int[]) in.readObject();
				
				if(ret[0] == Constantes.TIME_OUT){
					throw new SocketTimeoutException();
				}

				System.out.println();
				
				for(int i = ret.length - 1; i >= 0 ; i--){
					if(ret[i] == Constantes.NACK){
						System.out.println("NACK do pacote "+(i+1)+"... Realizando reenvio");
						flagRetorno = Constantes.NACK;
						pacoteErro++;
						break;
					}else if(ret[i] == Constantes.ACK){
						tProp = System.currentTimeMillis() - tProp;
						System.out.println("ACK do pacote "+(i+1));
						pacote.remove(i);
					}
				}
			}catch(SocketTimeoutException e){
				System.out.println("Pacote não enviado por timeout... Realizando reenvio ");
			}
		}
	}
}
