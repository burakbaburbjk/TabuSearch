package brk ;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class TabuSearch {

	public static double[][] sehirlerArasiMesafeler;
    public static TSPEnvironment tspEnvironment;
	

    public static void main(String[] args) {

		BufferedReader br = null;
		int numberOfIterations = 100;
        int tabuLength = 48;
       // int move = 5;
		int x = 2;
		int y = 3;
		ArrayList<City> Cities = new ArrayList<>();
		
		try 
		{
			String Coordinates;
			String map;
			Scanner input = new Scanner(System.in);
			
			System.out.print("TSP uygulamak istediðiniz haritayý seçiniz..:\n"+
			"1. bayg29\n2. att48\n3. berlin52\n4. Türkiye\n");
			
			map = input.next();
			
			switch (map) {
			case "1":
				System.out.print("bayg29 haritasýný sectiniz.");
		        tabuLength = 29; x = 2; y = 3; 
		        br = new BufferedReader(new FileReader("../TabuSearch/src/haritalar/bayg29.hrt"));
				break;
			case "2":
				System.out.print("att48 haritasýný sectiniz.");
		        tabuLength = 48; x = 1; y = 2; 
		        br = new BufferedReader(new FileReader("../TabuSearch/src/haritalar/att48.hrt"));
				break;
			case "3":
				System.out.print("berlin52 haritasýný sectiniz.");
		        tabuLength = 52; x = 1; y = 2;
		        br = new BufferedReader(new FileReader("../TabuSearch/src/haritalar/berlin52.hrt"));
				break;
			case "4":
				System.out.print("Türkiye haritasýný sectiniz.");
		        tabuLength = 81; x = 1; y = 2;
		        br = new BufferedReader(new FileReader("../TabuSearch/src/haritalar/Türkiye.hrt"));
		        
				break;
			default:
				break;
			}
			System.out.print("Ýterasyon sayýsýný giriniz..:");
			numberOfIterations = input.nextInt();
			
			//Koordinatlar dosyadan okunur ve diziye atilir
			while ((Coordinates = br.readLine()) != null) {
				String []koordinatlar = Coordinates.split("\\s+");
				
				City newCity = new City();				
				newCity.setX(Double.parseDouble(koordinatlar[x]));
				newCity.setY(Double.parseDouble(koordinatlar[y]));								
				
				Cities.add(newCity);				
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
    	
		//Cities, sehirleri obje olarak tutar. Her sehrin X ve Y koordinatý vardir
		//Bu koordinatlar kullanilarak sehirler arasi mesafeler bulunur ve bir matriste tutulur.
		MesafeleriGetir(Cities);
		
        tspEnvironment = new TSPEnvironment();

        //Sehirler arasi mesafeler tutuluyor..
        tspEnvironment.distances = sehirlerArasiMesafeler;
        //Between cities. 0,1 represents distance between cities 0 and 1, and so on.
        
    	int[] currSolution = new int[Cities.size()];
        for(int i = 0; i<Cities.size(); i++)
        {   
        	currSolution[i] = i;
        }
        
  //      currSolution = greedyBaslangicOlustur(currSolution);
        printSolution(currSolution);
        
        //initial solution
        //city numbers start from 0
        //the first and last cities' positions do not change

        //TabuList olusturuluyor.
        TabuList tabuList = new TabuList(tabuLength);

        //Baslangic cozumu en iyi cozum olarak kabul edilir
        int[] bestSol = new int[currSolution.length]; 
       
        System.arraycopy(currSolution, 0, bestSol, 0, bestSol.length);
        
        //En iyi cozumun maliyetini hesaplar
        int bestCost = tspEnvironment.getObjectiveFunctionValue(bestSol);
        System.out.println("Baslangic cozumu..:"+ bestCost);
      
        ///////////////////////////////////////////////////////////////////////////
        //TABU SEARCH ALGORITMASI BASLANGIC COZUMU ICIN ITERASYON ADEDINCE UYGULANIR
        //////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < numberOfIterations; i++) { 
        	
            currSolution = TabuSearch.getBestNeighbour(tabuList, tspEnvironment, currSolution);
            printSolution(currSolution);
            int currCost = tspEnvironment.getObjectiveFunctionValue(currSolution);
            System.out.println("Anlýk Cozum..:"+currCost);
            //System.out.println("Current best cost = " + tspEnvironment.getObjectiveFunctionValue(currSolution));

            if (currCost < bestCost) {
                System.arraycopy(currSolution, 0, bestSol, 0, bestSol.length);
                bestCost = currCost;
           //     bestCost += sehirlerArasiMesafeler[][]
            }
            System.out.println("En iyi cozum..:"+bestCost);
        }
        ////////////////////////////////////////////////////////////////////////////

        System.out.println("Search done! \nBest Solution cost found = " + bestCost + "\nBest Solution :");

        printSolution(bestSol);
    }
    
    public static void MesafeleriGetir(ArrayList<City> Cities)
    {            
    	int CityNumber = Cities.size();
        sehirlerArasiMesafeler = new double[CityNumber][CityNumber];
        for (int i = 0; i < CityNumber; i++)
            for (int j = 0; j < CityNumber; j++)
                sehirlerArasiMesafeler[i][j] = Math.sqrt(Math.pow(Cities.get(i).getX()- Cities.get(j).getX(), 2.0) + Math.pow(Cities.get(i).getY() - Cities.get(j).getY(), 2.0));

    }
    public static void printSolution(int[] solution) {
        for (int i = 0; i < solution.length; i++) {
            System.out.print(solution[i] + " ");
        }
        System.out.print(solution[0]);
        System.out.println();
    }
    
    public static int[] greedyBaslangicOlustur(int [] baslangiCozumu)
    {
         int []enKisaRota = new int[baslangiCozumu.length];            
         double enKisaMesafe;
         double mevcutMesafe;

       
         int sehirSayisi = baslangiCozumu.length;

         enKisaMesafe = 9999999;
         for (int i = 0; i < sehirSayisi; i++)
         {
        	 baslangiCozumu = new int[baslangiCozumu.length];
             int [] sehirler = new int[baslangiCozumu.length];
             for (int j = 0; j < baslangiCozumu.length; j++)
             {
                 sehirler[j] = j; 
             }
             baslangiCozumu[i] = sehirler[i];
             sehirler[i] = 0;
             for (int j = 0; j < sehirSayisi - 1; j++)
             {
                 int enYakinSehir = sehirler[0];
                 for (int k = 1; k < sehirSayisi; k++)
                 {
                     if (sehirlerArasiMesafeler[baslangiCozumu[j]][sehirler[k]]
                           < sehirlerArasiMesafeler[baslangiCozumu[j]][enYakinSehir])
                     {
                         enYakinSehir = sehirler[k]; 
                     }
                 }
                 baslangiCozumu[j] = enYakinSehir;
                 //Gidilen bir þehire tekrar gidilemez.
                 sehirler[enYakinSehir] = 0;
             }
             mevcutMesafe = tspEnvironment.getObjectiveFunctionValue(baslangiCozumu);
             if (mevcutMesafe < enKisaMesafe)
             {
                 enKisaRota = new int[baslangiCozumu.length];
                 enKisaMesafe = mevcutMesafe;
             }
         }
         baslangiCozumu = new int[enKisaRota.length];
         mevcutMesafe = enKisaMesafe;
         return baslangiCozumu;
    }
    
    public static int[] getBestNeighbour(TabuList tabuList, TSPEnvironment tspEnviromnet, int[] initSolution) 
    {

        int[] bestSol = new int[initSolution.length]; //this is the best Solution So Far
        System.arraycopy(initSolution, 0, bestSol, 0, bestSol.length);
        int bestCost = tspEnviromnet.getObjectiveFunctionValue(initSolution);
        int city1 = 0;
        int city2 = 0;
        boolean firstNeighbor = true;

        //1,2 /1,3/ 1,4 || 2,2/ 2,3/ 2,4 || 
        for (int i = 1; i < bestSol.length - 1; i++) 
        {
            for (int j = 2; j < bestSol.length - 1; j++) 
            {
                if (i == j) 
                {
                    continue;
                }
                int[] newBestSol = new int[bestSol.length]; //this is the best Solution So Far
                System.arraycopy(bestSol, 0, newBestSol, 0, newBestSol.length);

                //Dongu boyunca iki sehrin yeri degistirilir ve yeni yol maliyeti hesaplanir.
                newBestSol = swapOperator(i, j, initSolution); 
                
                int newBestCost = tspEnviromnet.getObjectiveFunctionValue(newBestSol);
                //Local minimum noktalarindan kurtulmak icin ilk cozum kotu olsa bile kabul edilerek
                //Diger cozumlerle karsilastirilir
                if ((newBestCost < bestCost || firstNeighbor) && tabuList.tabuList[i][j]== 0) { //if better move found, store it
                    firstNeighbor = false;
                    city1 = i;
                    city2 = j;
                    System.arraycopy(newBestSol, 0, bestSol, 0, newBestSol.length);
                    bestCost = newBestCost;
                }              
                
            }
        }
        if (city1 != 0) {
            tabuList.decrementTabu();
            tabuList.tabuMove(city1, city2);
        }
        return bestSol;
    }

    //swaps two cities
    public static int[] swapOperator(int city1, int city2, int[] solution) {
        int temp = solution[city1];
        solution[city1] = solution[city2];
        solution[city2] = temp;
        return solution;
    }
}
