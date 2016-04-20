package Dominio;

import java.util.*;

/**
 * La classe <tt>ConjuntClusters</tt> representa un conjunt de cl�sters i un conjunt d'elements que no pertanyen a cap cl�ster.
 * @author Arnau Blanch Cort�s
 *
 */
public class ConjuntClusters {
	private ArrayList<Cluster> cjt;
	private int k;
	private ArrayList<Integer> noAssignats;
	
	/**
	 * Crea un nou ConjuntClusters amb capacitat inicial per a <tt>k</tt> cl�sters
	 * @param k nombre de cl�sters
	 * @throws Exception si <tt>k</tt> �s igual o inferior a 0
	 */
	public ConjuntClusters(int k) throws Exception {
		if (k <= 0) throw new Exception("'k' no v�l�lida");
		this.k = k;
		this.cjt = new ArrayList<Cluster>(k);
		for (int i = 0; i < k; ++i) this.cjt.add(new Cluster());
		this.noAssignats = new ArrayList<Integer>();
	}
	
	/**
	 * Retorna el n�mero de cl�sters del conjunt (<tt>k</tt>)
	 * @return n�mero de cl�sters
	 */
	public int getK() {
		return this.k;
	}
	
	/**
	 * Actualitza els medoides de tots els cl�sters del conjunt
	 * @param M matriu de rellev�ncia
	 */
	public void updateMedoids(ArrayList<ArrayList<Double>> M) {
		for (Cluster c : cjt) c.updateMedoid(M);
	}
	
	/**
	 * Retorna el cl�ster amb �ndex <tt>i</tt>
	 * @param i �ndex del cl�ster a consultar
	 * @return cl�ster amb l'�ndex indicat
	 * @throws IndexOutOfBoundsException si <tt>i</tt> est� fora del rang (0 &lt; i &gt; k)
	 */
	public Cluster get(int i) throws IndexOutOfBoundsException {
		if (i < 0 || i >= k) throw new IndexOutOfBoundsException("'i' fora de rang");
		return cjt.get(i);
	}
	
	/**
	 * Canvia l'element <tt>e</tt> del cl�ster <tt>cl1</tt> al cl�ster <tt>cl2</tt>
	 * @param e element a canviar
	 * @param cl1 cl�ster origen
	 * @param cl2 cl�ster destinaci�
	 * @throws Exception si el cl�ster <tt>cl1</tt> no cont� <tt>e</tt> 
	 */
	public void changeCluster(int e, int cl1, int cl2) throws Exception {
		if (!cjt.get(cl1).getList().contains(e)) throw new Exception("El cl�ster 'cl1' no cont� 'e'");
		//else if (cjt.get(cl2).getList().contains(e)) throw new Exception("El cl�ster 'cl2' ja cont� 'e'");
		else {
			cjt.get(cl1).deleteElem(e);
			cjt.get(cl2).add(e);
		}
	}
	
	/**
	 * Retorna el conjunt d'elements no assignats a cap cl�ster
	 * @return conjunt d'elements no assignats
	 */
	public ArrayList<Integer> getNoAssig() {
		return noAssignats;
	}
	
	/**
	 * Retorna el cl�ster amb centroide m�s similar a <tt>e</tt>
	 * @param e element pel qual cal buscar cl�ster
	 * @param M matriu de rellev�ncia
	 * @return �ndex del cl�ster amb centroide m�s similar a <tt>e</tt>
	 * @throws Exception si hi ha algun cl�ster sense medoide assignat
	 */
	public int getClusterMesProper(int e, ArrayList<ArrayList<Double>> M) throws Exception {
		HashMap<Integer, Double> relevClusters = new HashMap<Integer, Double>();
		int clustProper;
		for (int j = 0; j < k; ++j) {
			try {
				int med = cjt.get(j).getMedoid();
				double relev = M.get(med).get(e);
				if (relev != 0) relevClusters.put(j,relev);
			}
			catch (Exception ex) {
				throw ex;
				//throw new Exception("Hi ha cl�sters sense medoide assignat");
			}
		}
		
		if (relevClusters.size() == 0) clustProper = -1;
		else {
			Map<Integer,Double> ordenat = Cluster.ordenaPerValor(relevClusters);
			// Ara tenim un map ordenat per la suma de similaritats (descendent)
			
			// Agafem els elements que tenen el valor m�xim de la suma de rellev�ncies
			Iterator<Map.Entry<Integer,Double>> it = ordenat.entrySet().iterator();
			Boolean changed = false;
			LinkedList<Map.Entry<Integer,Double>> candidats =
					new LinkedList<Map.Entry<Integer,Double>>(); // candidats a cluster
			Map.Entry<Integer, Double> first = it.next(); // 1r element amb rellev�ncia m�xima
			candidats.add(first);
			
			while (it.hasNext() && !changed) { // mentre quedin elements i no hagi canviat la rellev�ncia
				Map.Entry<Integer, Double> elem = it.next();
				changed = (elem.getValue() != first.getValue());
				if (!changed) candidats.add(elem);
			}
			
			// En cas d'haver-hi m�s d'un candidat, el medoide es selecciona aleat�riament
			if (candidats.size() > 1) {
				Random r = new Random();
				int c = r.nextInt(candidats.size());
				clustProper = candidats.get(c).getKey();
			}
			else clustProper = candidats.getFirst().getKey();
		}
		
		return clustProper;
	}
}
