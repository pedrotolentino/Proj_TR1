package Verificacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

public class CRC {

	private final int      geradorBits  = 4;
	private final int      pacoteBits   = 8;
	private final int      totalBits    = pacoteBits + geradorBits - 1;
	private final Integer  polGerador[] = new Integer[geradorBits];

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
	
	private int[] divide(int dividendo[],Integer divisor[], int resto[])
	{
		int atual=0;
		while(true)
		{
			for(int i=0;i<divisor.length;i++){
				resto[atual+i]=(resto[atual+i]^divisor[i]);
			}
			while(resto[atual]==0 && atual!=resto.length-1){
				atual++;
			}
			if((resto.length-atual)<divisor.length){
				break;
			}
		}
		return resto;
	}
	
	public static void main(String args[]){
		
		CRC crc = new CRC();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Começando.. Entre com o número binário de 8 dígitos (um bit a cada enter):");
		
		int pacote[]     = new int[8];
		int pacoteEncr[] = new int[11];
				
		try{
			for(int i=0; i<8; i++){
				pacote[i]=Integer.parseInt(br.readLine());
			}
			pacoteEncr = crc.encriptar(pacote);
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Quebrou!");
		}
			
		System.out.println("Pacote encriptado: ");		
		
		for(int i=0; i< pacoteEncr.length; i++){
			System.out.print(pacoteEncr[i]);        
			System.out.println();
		}
		
		System.out.println("Entre com o número binário de 11 dígitos a ser desencriptado (um bit a cada enter):");
		
		try{
			for(int i=0; i<11; i++){
				pacoteEncr[i]=Integer.parseInt(br.readLine());
			}
			
			if(crc.desencriptar(pacoteEncr)){
				System.out.println("Correto!");
			}else{
				System.out.println("Errado!");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Quebrou2!");
		}
	}
	
	
//	public static void main(String args[]) throws IOException
//	{
//		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//		int[] data;
//		int[] div;
//		int[] divisor;
//		int[] rem;
//		int[] crc;
//		int data_bits, divisor_bits, tot_length;
//
//		System.out.println("Enter number of data bits : ");
//		data_bits=Integer.parseInt(br.readLine());
//		data=new int[data_bits];
//
//		System.out.println("Enter data bits : ");
//		for(int i=0; i<data_bits; i++)
//			data[i]=Integer.parseInt(br.readLine());
//
//		System.out.println("Enter number of bits in divisor : ");
//		divisor_bits=Integer.parseInt(br.readLine());
//		divisor=new int[divisor_bits];
//
//		System.out.println("Enter Divisor bits : ");
//		for(int i=0; i<divisor_bits; i++)
//			divisor[i]=Integer.parseInt(br.readLine());
//
//
//		/*System.out.print("Data bits are : ");
//        for(int i=0; i< data_bits; i++)
//            System.out.print(data[i]);        
//        System.out.println();
//
//        System.out.print("divisor bits are : ");
//        for(int i=0; i< divisor_bits; i++)
//            System.out.print(divisor[i]);        
//        System.out.println();
//
//		 */        
//
//		tot_length=data_bits+divisor_bits-1;
//
//		div=new int[tot_length];
//		rem=new int[tot_length];
//		crc=new int[tot_length];
//		/*------------------ CRC GENERATION-----------------------*/    
//		for(int i=0;i<data.length;i++)
//			div[i]=data[i];
//
//		System.out.print("Dividend (after appending 0's) are : ");
//		for(int i=0; i< div.length; i++)
//			System.out.print(div[i]);        
//		System.out.println();
//
//		for(int j=0; j<div.length; j++){
//			rem[j] = div[j];
//		}
//
//		rem=divide2(div, divisor, rem);
//
//		for(int i=0;i<div.length;i++)           //append dividend and ramainder
//		{
//			crc[i]=(div[i]^rem[i]);
//		}
//
//		System.out.println();
//		System.out.println("CRC code : ");    
//		for(int i=0;i<crc.length;i++)
//			System.out.print(crc[i]);
//
//		/*-------------------ERROR DETECTION---------------------*/    
//		System.out.println();
//		System.out.println("Enter CRC code of "+tot_length+" bits : ");
//		for(int i=0; i<crc.length; i++)
//			crc[i]=Integer.parseInt(br.readLine());
//
//
//		/*        System.out.print("crc bits are : ");
//        for(int i=0; i< crc.length; i++)
//            System.out.print(crc[i]);        
//        System.out.println();
//		 */        
//		for(int j=0; j<crc.length; j++){
//			rem[j] = crc[j];
//		}
//
//		rem=divide2(crc, divisor, rem);
//
//		for(int i=0; i< rem.length; i++)
//		{
//			if(rem[i]!=0)
//			{
//				System.out.println("Error");
//				break;
//			}
//			if(i==rem.length-1)
//				System.out.println("No Error");
//		}
//
//		System.out.println("THANK YOU.... :)");
//	}
//
//	static int[] divide2(int div[],int divisor[], int rem[])
//	{
//		int cur=0;
//		while(true)
//		{
//			for(int i=0;i<divisor.length;i++)
//				rem[cur+i]=(rem[cur+i]^divisor[i]);
//
//			while(rem[cur]==0 && cur!=rem.length-1)
//				cur++;
//
//			if((rem.length-cur)<divisor.length)
//				break;
//		}
//		return rem;
//	}
}

