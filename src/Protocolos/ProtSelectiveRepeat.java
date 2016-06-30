package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.Vector;

import Simulacao.Constantes;
import Simulacao.Protocolo;

public class ProtSelectiveRepeat implements Protocolo{
	
	public int  pacoteErro;
	public int  pacotesEnviados;
	public long tProp;

	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int erro = 0;
		int[] ret = null;
		Vector novoPacote = new Vector<int[]>();
		while(!pacote.isEmpty()){
			for(int i = erro; i < Constantes.TAMANHO_JANELA && i < pacote.size(); i++){
				novoPacote.add(pacote.get(i));
			}
			novoPacote.trimToSize();
			pacotesEnviados += novoPacote.size();
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.reset();
			tProp = System.currentTimeMillis();
			out.writeObject(novoPacote);
			
			try{
				ret	 = (int[]) in.readObject();
				
				erro = 0;
				for(int i = ret.length - 1; i >= 0 ; i--){
					if(ret[0] == Constantes.TIME_OUT){
						throw new SocketTimeoutException();
					}else if(ret[i] == Constantes.NACK){
						erro++;
						pacoteErro++;
					}else if(ret[i] == Constantes.ACK && (novoPacote.size() >= i+1) && (pacote.size() >= i+1)){
						tProp = System.currentTimeMillis() - tProp;
						novoPacote.remove(i);
						pacote.remove(i);
					}
				}
			}catch(SocketTimeoutException e){
				pacoteErro += novoPacote.size();
				novoPacote.clear();
			}		
		}
	}
}
