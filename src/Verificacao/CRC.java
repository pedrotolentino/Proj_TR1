package Verificacao;


public class CRC {

	private final int      geradorBits  = 4;
	private final int      pacoteBits   = 8;
	private final int      totalBits    = pacoteBits + geradorBits - 1;
	private final int  polGerador[] = new int[geradorBits];

	public CRC(){
		polGerador[0]=1; 
		polGerador[1]=1; 
		polGerador[2]=0; 
		polGerador[3]=1;
	}
	
	public int[] encriptar(int[] pacote) throws Exception{

		int dividendo[] = new int[totalBits];
		int resto    [] = new int[totalBits];
		int crc      [] = new int[totalBits];
		
		if(pacote == null || pacote.length < pacoteBits){
			throw new Exception("Pacote vazio ou menor do que 8 bits");
		}

		for(int i=0; i < pacote.length; i++){
			dividendo[i] = pacote[i];
		}

		for(int j=0; j < dividendo.length; j++){
			resto[j] = dividendo[j];
		}

		resto=divide(dividendo, polGerador, resto);

		for(int k=0; k < dividendo.length; k++){
			crc[k] = dividendo[k] ^ resto[k];
		}

		return crc;
	}

	public boolean desencriptar(int[] crc) throws Exception{
	
		int resto[] = new int[totalBits];
		
		for(int i=0; i < crc.length; i++){
			resto[i] = crc[i];
		}
		
		resto=divide(crc, polGerador, resto);
		
		for(int j=0; j < resto.length; j++)
		{
			if(resto[j] != 0)
			{
				return false;
			}
			if(j == resto.length-1)
				return true;
		}
		
		return false;
	}
	
	private int[] divide(int dividendo[], int[] polGerador2, int resto[]){
		int atual = 0;
		while(true)
		{
			for(int i=0; i < polGerador2.length; i++){
				resto[atual+i] = (resto[atual+i] ^ polGerador2[i]);
			}
			while(resto[atual] == 0 && atual != resto.length-1){
				atual++;
			}
			if((resto.length - atual) < polGerador2.length){
				break;
			}
		}
		return resto;
	}
}
